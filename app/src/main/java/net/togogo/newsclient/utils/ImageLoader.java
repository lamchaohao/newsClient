package net.togogo.newsclient.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import net.togogo.newsclient.cacheUtil.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Lam on 2017/9/23.
 */

public class ImageLoader {

    private String TAG = "ImageLoader";

    //内存缓存.
    private LruCache<String,Bitmap> mMemoryCache;  //map<>
    //硬盘缓存
    private DiskLruCache mDiskLruCache;

    private Context mContext;

    //线程池
    private ExecutorService mExecutors;

    private Handler mHandler;

    public ImageLoader(Context context) {
        mContext = context;
        long maxMemory = Runtime.getRuntime().maxMemory();
        mMemoryCache = new LruCache<String,Bitmap>((int) (maxMemory/8)){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        //第一个参数 数据的缓存目录
        //
        File diskCacheDir = getFileCacheDir("bitmap");
        //更换版本,更换缓存

        try {
            mDiskLruCache = DiskLruCache.open(diskCacheDir,getAppVersion(),1,20*1024*1024);

        } catch (IOException e) {
            e.printStackTrace();
        }
        mExecutors= Executors.newFixedThreadPool(6);//限制线程池大小为6
        mHandler = new Handler();
    }

    /**
     *  mContext.getExternalCacheDir() /sdcard/Android/data/<app package>/cache/
     * @return
     */
    private File getFileCacheDir(String uniqueName){
        String path;
        //如果sd卡没问题,则使用sd卡目录下作为缓存
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                ||!Environment.isExternalStorageRemovable()){
            path = mContext.getExternalCacheDir().getPath();
        }else {
            path = mContext.getCacheDir().getPath();
        }
        return new File(path+File.separator+uniqueName);
    }

    private int getAppVersion(){
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }


    public void loadImage(ImageView imageView,String imageUrl){

        //1.先从内存读取缓存
        Bitmap bitmap = mMemoryCache.get(imageUrl);
        if (bitmap==null) {
            //2.从硬盘加载
            startExcutor(imageView,imageUrl);
        }else {
            Log.d(TAG, "loadImage: 从内存获取 ");
            imageView.setImageBitmap(bitmap);
        }

    }

    private void startExcutor(final ImageView imageView, final String imageUrl) {
        mExecutors.execute(new Runnable() {
            @Override
            public void run() {
                //1.从硬盘读取
                //imageUrl -- 有特殊符号 /\<>,DiskLruCache
                String cacheKey = hashKeyForDisk(imageUrl);
                try {
                    DiskLruCache.Snapshot snapshot = mDiskLruCache.get(cacheKey);
                    if (snapshot==null) {//null 则代表还没有在硬盘缓存
                        DiskLruCache.Editor edit = mDiskLruCache.edit(cacheKey);
                        if (edit!=null){
                            OutputStream outputStream = edit.newOutputStream(0);
                            //从网络上读取,已经写入了硬盘
                            boolean flag = downloadFromInternet(imageUrl, outputStream);
                            if (flag){
                                edit.commit();//提交
                            }else {
                                edit.abort();//取消操作
                            }
                        }

                    }

                    snapshot=mDiskLruCache.get(cacheKey);

                    if (snapshot!=null){
                        //从硬盘读取出来了
                        Log.d(TAG, "startExcutor: 从硬盘获取");
                        InputStream inputStream = snapshot.getInputStream(0);
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        if (bitmap!=null) {
                            //加入内存缓存
                            addBitmapToMemoryCache(imageUrl,bitmap);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(bitmap);
                                }
                            });

                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void addBitmapToMemoryCache(String imageUrl, Bitmap bitmap) {
        //假如从内存缓存中读取出来为null,则说明名内存缓存没有放此对象
        if (mMemoryCache.get(imageUrl)==null) {
            mMemoryCache.put(imageUrl,bitmap);
        }
    }

    private boolean downloadFromInternet(String imageUrl,OutputStream outputStream) {
        Log.d(TAG, "downloadFromInternet:从网络上获取 ");
        BufferedInputStream is = null;
        HttpURLConnection urlConnection = null;
        BufferedOutputStream os= null;
        try {
            URL url = new URL(imageUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            //从网络流读取流
            is=new BufferedInputStream(urlConnection.getInputStream(),8*1024);
            os = new BufferedOutputStream(outputStream,8*1024);
            int b;
            while((b = is.read())!=-1){
                os.write(b);
            }
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (urlConnection!=null) {
                urlConnection.disconnect();
            }
            if (is!=null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return false;
    }

    private String hashKeyForDisk(String imageUrl) {
        //MD5 消息摘要
        //byte数组转成十六进制的字符串,0-9 a-f
        String cacheKey = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(imageUrl.getBytes());
            byte[] digest = md5.digest();
            cacheKey = byteToHexString(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            cacheKey = String.valueOf(imageUrl.hashCode());
        }
        return cacheKey;

    }

    private String byteToHexString(byte[] digest) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            String s = Integer.toHexString(0xFF & digest[i]);
            sb.append(s);
        }
        return sb.toString();
    }


}
