package net.togogo.newsclient.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;

import net.togogo.newsclient.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class PushService extends Service {

    private Handler mHandler;

    public PushService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        initPushThread();
        mHandler = new Handler();
//        showMsg("直接显示.");
        return super.onStartCommand(intent, flags, startId);
    }

    private void initPushThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = new Socket();
                try {
                    //保持长连接
                    socket.setKeepAlive(true);
                    //传入主机名,端口号,超时时间
                    socket.connect(new InetSocketAddress("192.168.2.161",8996),60000);

                    while(true){
                        byte[] bytes = new byte[2048];
                        socket.getInputStream().read(bytes);
                        final String message = new String(bytes,"utf-8");
                        if (message!= null){
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showMsg(message);
                                }
                            });
                        }
                    }



                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();

    }

    //将消息显示在通知栏
    private void showMsg(String msg){


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(msg)
                .setDefaults(Notification.DEFAULT_ALL)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_logo))
                .setSmallIcon(R.drawable.icon_bxq_current)
                .build();

        manager.notify(25,notification);


    }



}
