package com.example.a143music;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class CreateNotification {

    public static final String CHANNEL_ID="channel1";
    public static final String ACTIONPREVIOUS="actionprevious";
    public static final String CHANNEL_PLAY="actionplay";
    public static final String CHANNEL_NEXT="actionnxt";

    public static Notification notification;
    public static void createNotification(Context ctx,Track track,int plabtn,int pos,int size){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationManagerCompat nmC= NotificationManagerCompat.from(ctx);
            MediaSessionCompat msc=new MediaSessionCompat(ctx,"tag");
           // Bitmap icon= BitmapFactory.decodeResource(ctx.getResources(),track.get());
           /*  notification=new NotificationCompat.Builder(ctx,CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_music_note)
                    .setContentTitle(track.getTitle())
                    .setContentText(track.getArtist())
                    .setLargeIcon(icon)
                    .setOnlyAlertOnce(true)
                    .setShowWhen(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();*/
            nmC.notify(1,notification);
        }
    }

}
