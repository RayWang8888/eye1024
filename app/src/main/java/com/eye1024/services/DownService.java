package com.eye1024.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RemoteViews;

import com.eye1024.R;
import com.raywang.rayutils.ThreadDown;

import java.io.File;

/**
 * 下载的服务
 * Created by Ray on 2015/9/2.
 */
public class DownService extends Service {
    public static final int DOWN = 1;
    public static final int PAUSE = 2;
    public static final int CANCEL = 0;
    private ThreadDown down;
    private NotificationManager nm = null;
    private RemoteViews contentView;
    private Notification notification = null;
    private int allLength;
    private ThreadDown.OnDownPre onDownPre = new ThreadDown.OnDownPre() {
        @Override
        public void onChange(long newLength) {
            contentView.setProgressBar(R.id.progressBar,allLength,(int)newLength,false);

            contentView.setTextViewText(R.id.info,newLength+"/"+allLength);
            nm.notify(R.string.app_name,notification);
        }

        @Override
        public void onAllLength(long allLength) {
            DownService.this.allLength = (int)allLength;
            contentView.setProgressBar(R.id.progressBar,DownService.this.allLength,0,false);
            contentView.setTextViewText(R.id.info,"0/"+allLength);
            nm.notify(R.string.app_name,notification);
        }

        @Override
        public void onFinish(String filePath) {
            nm.cancel(R.string.app_name);
            Intent intent2 = new Intent(Intent.ACTION_VIEW);
            intent2.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//以新压入栈
            startActivity(intent2);
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        @Override
        public void onSpeed(long speed) {

        }

        @Override
        public void onError(String e) {

        }

        @Override
        public void onStart() {
            contentView.setTextViewText(R.id.pause, getResources().getString(R.string.pause));
            nm.notify(R.string.app_name, notification);
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            switch (intent.getIntExtra("type", 0)) {
                case DOWN:
                    if (down != null && down.getUrl().equals(intent.getStringExtra("url"))) {
                        down.down();
                    } else {
                        down = new ThreadDown(this, intent.getStringExtra("url"),
                                Environment.getExternalStoragePublicDirectory("1024eye").getAbsolutePath(),
                                onDownPre);
                        down.down();
                        notification = new Notification.Builder(this).build();

                        notification.icon = R.drawable.ic_launcher;
                        notification.tickerText = getResources().getString(R.string.arc_bottom_text);

                        contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
                        Intent cancel = new Intent(this,DownService.class);
                        cancel.putExtra("type",CANCEL);

                        contentView.setOnClickPendingIntent(R.id.cancel,PendingIntent.getService(
                                this,0,cancel,PendingIntent.FLAG_UPDATE_CURRENT ));

                        Intent pause = new Intent(this,DownService.class);
                        pause.putExtra("type",PAUSE);
                        contentView.setOnClickPendingIntent(R.id.pause,PendingIntent.getService(
                                this,1,pause,PendingIntent.FLAG_UPDATE_CURRENT ));



                        notification.bigContentView = contentView;
                        nm.notify(R.string.app_name, notification);
                    }

                    break;
                case PAUSE:
                    if(down != null) {
                        if(down.isDown()) {
                            contentView.setTextViewText(R.id.pause, getResources().getString(R.string.reuse));
                            nm.notify(R.string.app_name, notification);
                            down.stop();
                        }else{
                            down.down();
                        }
                    }
                    break;
                case CANCEL:
                    if (down != null) {
                        down.stop();
                        nm.cancel(R.string.app_name);
                    }
                    break;
            }
        }
        return Service.START_NOT_STICKY;
    }
}
