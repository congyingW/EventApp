package com.example.eventapp.adapter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.eventapp.MainActivity;
import com.example.eventapp.R;

public class RemindActionService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("notify")){
            Intent intent1 = new Intent(context, MainActivity.class);
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//            stackBuilder.addNextIntent(intent1);
//            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntent;
            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_IMMUTABLE);
            } else{
                pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);
            }
            NotificationManager notificationManager = (NotificationManager) context.
                    getSystemService(Context.NOTIFICATION_SERVICE);
            String nameEvent = intent.getStringExtra("name");
            Notification.Builder builder = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setTicker("event")
                    .setContentTitle(nameEvent)
                    .setContentText("you have events to do today")
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setNumber(1);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                CharSequence name = "channel name";
                String description = "channel description";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("10", name, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
                builder.setChannelId("10");
            }
            notificationManager.notify(10, builder.build());
        }
    }
}
