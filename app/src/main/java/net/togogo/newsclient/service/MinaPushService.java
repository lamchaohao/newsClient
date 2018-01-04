package net.togogo.newsclient.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

import net.togogo.newsclient.R;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class MinaPushService extends Service implements IoHandler{
    String TAG = "MinaPushService";
    int notificationID;
    public MinaPushService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initMina();
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMina() {

        NioSocketConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(60000);
        connector.getFilterChain().addLast("logger", new LoggingFilter());
        connector.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ))));
        connector.setHandler(this);

        IoSession session;
        for (;;) {
            try {
                ConnectFuture future = connector.connect(new InetSocketAddress("106.14.21.150", 8999));
                future.awaitUninterruptibly();
                session = future.getSession();
                break;
            } catch (RuntimeIoException e) {
                System.err.println("");
                e.printStackTrace();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    @Override
    public void sessionCreated(IoSession ioSession) throws Exception {
        Log.i(TAG, "sessionCreated: ");
    }

    @Override
    public void sessionOpened(IoSession ioSession) throws Exception {
        Log.i(TAG, "sessionOpened: ");
    }

    @Override
    public void sessionClosed(IoSession ioSession) throws Exception {

    }

    @Override
    public void sessionIdle(IoSession ioSession, IdleStatus idleStatus) throws Exception {
        Log.i(TAG, "sessionIdle: idleStatus"+idleStatus);
    }

    @Override
    public void exceptionCaught(IoSession ioSession, Throwable throwable) throws Exception {

    }

    @Override
    public void messageReceived(IoSession ioSession, Object o) throws Exception {
        Log.i(TAG, "messageSent: ");
        showMsg((String) o);
        ioSession.write("我收到消息了"+o);
    }

    @Override
    public void messageSent(IoSession ioSession, Object o) throws Exception {

    }

    @Override
    public void inputClosed(IoSession ioSession) throws Exception {

    }

    //将消息显示在通知栏
    private void showMsg(String msg){

        Gson gson = new Gson();
        NotificationMsg notificationMsg = gson.fromJson(msg, NotificationMsg.class);


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(notificationMsg.getTitle())
                .setContentText(notificationMsg.getContent())
                .setDefaults(Notification.DEFAULT_ALL)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_logo))
                .setSmallIcon(R.drawable.icon_bxq_current)
                .build();
        notificationID++;
        manager.notify(notificationID,notification);


    }


    class NotificationMsg {
        private String title;
        private String content;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

}
