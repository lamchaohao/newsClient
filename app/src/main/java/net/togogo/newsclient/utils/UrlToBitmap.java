package net.togogo.newsclient.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Lam on 2017/9/16.
 */

public class UrlToBitmap {
    private Bitmap mBitmap = null;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.i("handleMessgae", "handleMessage: msg.what"+msg.what);
        }
    };

    private Context mContext;
    public UrlToBitmap(Context context) {
        mContext = context;
        mHandler = new Handler();
    }

    public void getBitmapFromUrl(final String urlStr, final ImageView imageView){
        final String substring = urlStr.substring(urlStr.lastIndexOf("/")+1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                BufferedInputStream bis = null;
                BufferedOutputStream bos = null;
                Bitmap bitmap = null;
                try {
                    //1.建立HTTP连接
                    URL url = new URL(urlStr);
                    //2.获取到输入流
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
//                    bis = new BufferedInputStream(inputStream);//包装流
//                    bos = new BufferedOutputStream(mContext.openFileOutput("bitmap"+substring,Context.MODE_PRIVATE));
//                    byte[] bytes = new byte[1024];
                    bitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
//                    Log.i("handleMessgae", "run: getResponseMessage"+urlConnection.getResponseMessage());
//                    while( bis.read(bytes,0,bytes.length)!=-1){
//                        bos.write(bytes);
//                    }
                    //3.把流转换成bitmap
//                    Log.i("handleMessgae", "run: getResponseMessage"+urlConnection.getRequestMethod());
//                    bitmap = BitmapFactory.decodeStream(mContext.openFileInput("bitmap"+substring));
//                    Message msg = new Message();
//                    msg.obj = bitmap;
//                    msg.what= Constant.CODE_OK;
//                    mHandler.sendMessage(msg);
                    final Bitmap finalBitmap = bitmap;

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {//主线程
                            imageView.setImageBitmap(finalBitmap);
                            Log.i("post", "run: ");
                        }
                    });
//                    Log.i("handleMessgae", "run: sendMessage"+msg.what);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (bis!=null) {
                        try {
                            bis.close();
                            bis=null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (bos!=null) {
                        try {
                            bos.close();
                            bos=null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (urlConnection!=null) {
                        urlConnection.disconnect();
                    }
                }
            }
        }).start();



    }



}
