package com.example.sic;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationClass {
    private NotificationManagerCompat nfc;
    private NotificationCompat.Builder builder;

    public NotificationClass() {

    }

    public void build(Context context, int maxProgress, String title, String body){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_love)
                .setContentTitle(title)
                .setContentText(body)
                .setOngoing(true)
                .setProgress(maxProgress, 0, false);

        notificationManager.notify(notificationId, builder.build());
        nfc = NotificationManagerCompat.from(context);
    }

    public void updateNotificationBar(int maxProgress, int progress){
        builder.setContentTitle(progress+"/"+maxProgress).setProgress(maxProgress, progress, false);
        nfc.notify(1, builder.build());
        if(progress==maxProgress)
            removeProgressBar();
    }

    public void removeProgressBar(){
        builder.setOngoing(false);
        builder.setContentText("Posted")
                .setProgress(0,0,false);
        nfc.notify(1, builder.build());
    }

}
