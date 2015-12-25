package com.example.student.gefriertruhapp.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.SystemClock;

import com.example.student.gefriertruhapp.Dashboard;
import com.example.student.gefriertruhapp.Model.DataBaseSingleton;
import com.example.student.gefriertruhapp.Model.FridgeItem;
import com.example.student.gefriertruhapp.Model.ShelfItem;
import com.example.student.gefriertruhapp.R;

/**
 * Created by student on 25.12.15.
 */
public abstract class Notifier {

    public final static String ITEM_ID = "ITEM_ID";
    public static void sendNotification(Context context, int id){
        DataBaseSingleton.init(context);
        DataBaseSingleton.getInstance().loadDataBase();


        FridgeItem item = DataBaseSingleton.getInstance().getItemByID(id);
        item.setNotified(true);
        DataBaseSingleton.getInstance().saveDataBase();

        Intent startIntent = new Intent(context, Dashboard.class);
        startIntent.putExtra(ITEM_ID, item.getId());

        PendingIntent pendingIntent = PendingIntent.getActivity(context, item.getId(), startIntent, PendingIntent.FLAG_ONE_SHOT);

        String shelf = "Gefriertruhe: ";

        if(item instanceof ShelfItem){
            shelf = "Lager: ";
        }

        long[] vibrate = new long[] {1000,500,1000,500,1000,500};
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_freezer_blue);
        Notification.Builder mBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.icon_freezer_blue)
                .setLargeIcon(bm)
                .setContentTitle(item.getName())
                .setContentText(shelf + "Erinnerung an: " + item.getName())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(vibrate)
                .setLights(Color.CYAN, 1000, 1000);
        Notification n = mBuilder.build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(item.getId(), n);
    }

    public static void sendNotificationsNotSendYet(Context context){
        DataBaseSingleton.init(context);
        DataBaseSingleton.getInstance().loadDataBase();
        for(ShelfItem item : DataBaseSingleton.getInstance().getShelfList()) {
            if(item.getNotificationDate() != null && !item.isNotified() && item.getNotificationDate().getMillis() < SystemClock.elapsedRealtime()){
                sendNotification(context, item.getId());
            }
        }
        for(FridgeItem item : DataBaseSingleton.getInstance().getFridgeList()){
            if(item.getNotificationDate() != null && !item.isNotified() && item.getNotificationDate().getMillis() < SystemClock.elapsedRealtime()){
                sendNotification(context, item.getId());
            }
        }
    }
}