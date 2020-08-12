package app.com.temperature;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class myService extends Service {
    int notificationId = 1;
    String channelId = "channel-01";
    String channelName = "Channel Name";
    int importance = NotificationManager.IMPORTANCE_HIGH;
    int i=0;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("myDb/temperatur/valeur");
    Context context = this;
    SharedPreferences prefs ;
    String editTextValue ="1000";
    //   private static final int NOTIF_ID = 123;

    //   DatabaseReference myRef1 = database.getReference("myDb/lightdptmnt/lampstat/valeur");
    ValueEventListener listener;
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        // Toast.makeText(this, prefs.getString("Key", "not found"), Toast.LENGTH_LONG).show();


        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onCreate() {
        final Intent intent=new Intent(getApplicationContext(), myService.class);

        showNotification_app(getApplicationContext(),  intent);
        database.goOnline();
        prefs = this.getSharedPreferences(
                "app.com.temperature", Context.MODE_PRIVATE);

        editTextValue =prefs.getString("Key", "1000");

        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //    if(prefs.getString("Key1", "1000").equals("go")){

                String value = dataSnapshot.getValue(String.class);


                sendmessage(value);
                if (value.contains(",")){
                    Toast.makeText(getApplicationContext(),value, Toast.LENGTH_LONG).show();
                    value = value.split(",",0)[0];
                }
                if(Integer.parseInt(editTextValue)<Integer.parseInt(value)){

                    //  Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_LONG).show();
                    showNotification(getApplicationContext(), "هناك تجاوز لدرجة الحرارة", "درجة الحرارة:"+" C°"+dataSnapshot.getValue(String.class), intent);


                }
            }
            // }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value


            }
        });
        if(prefs.getString("Key1", "1000").equals("stop")){
            FirebaseDatabase.getInstance().goOffline();
            this.stopSelf();}

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendmessage(String s) {
        Intent intent = new Intent("service_message");
        intent.putExtra("message_key",s);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
    public void showNotification(Context context, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                /* .setSmallIcon(R.mipmap.ic_launcher)
                 .setContentTitle(title)
                 .setContentText(body);*/
                .setSmallIcon(R.drawable.ic_add_alert_black_24dp)     // drawable for API 26
                .setAutoCancel(true)
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setContentText(body)
                .setVibrate(new long[] { 0, 500, 110, 500, 110, 450, 110, 200, 110,
                        170, 40, 450, 110, 200, 110, 170, 40, 500 } )
                .setLights(Color.RED, 3000, 3000);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        startForeground(notificationId, mBuilder.build());
        //   stopForeground(false);
        //  notificationManager.notify(notificationId, mBuilder.build());
    }
    public void showNotification_app(Context context,  Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                // .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("alert is running...")
                // .setContentText(body);
                .setSmallIcon(R.drawable.ic_add_alert_black_24dp)     // drawable for API 26
                .setAutoCancel(true);




        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        startForeground(notificationId, mBuilder.build());
//        stopForeground(false);
        //  notificationManager.notify(notificationId, mBuilder.build());
    }
}
