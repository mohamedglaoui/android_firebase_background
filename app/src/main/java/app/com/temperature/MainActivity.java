package app.com.temperature;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity  implements
        AdapterView.OnItemSelectedListener {
    BroadcastReceiver mreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message_key");
            //    Toast.makeText(MainActivity.this,message , Toast.LENGTH_LONG).show();
            TextView text1= (TextView) findViewById(R.id.textView2);
            TextView text= (TextView) findViewById(R.id.textView);

            text.setText(message+" °C");
        }
    };
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("myDb/lightdptmnt/doorstat/valeur");
    String[] country = { "اختر درجة الحرارة", "28", "29", "30", "31","32", "33", "34", "35","36", "37", "38", "39"};
    String temperature="100";
    SharedPreferences prefs ;
    @Override
    protected void onStart() {//register the localbroadcast
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).
                registerReceiver(mreceiver,new IntentFilter("service_message"));
        TextView text1= (TextView) findViewById(R.id.textView5);


        text1.setText(prefs.getString("Key", "1000")+"°C");
        Intent intent=new Intent(MainActivity.this, myService.class);

        prefs.edit().putString("Key1", "go").apply();

        startService(intent);
    }

    @Override
    protected void onStop() {//unregister the localbroadcast
        super.onStop();

        LocalBroadcastManager.getInstance(getApplicationContext()).
                unregisterReceiver(mreceiver);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = this.getSharedPreferences(
                "app.com.temperature", Context.MODE_PRIVATE);
        Intent intent=new Intent(MainActivity.this, myService.class);

        prefs.edit().putString("Key1", "go").apply();

        startService(intent);
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,R.layout.simple_item,country);
        aa.setDropDownViewResource(R.layout.simple_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        Button button= (Button) findViewById(R.id.ok);
        Button button1= (Button) findViewById(R.id.ok1);



        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, myService.class);

                if(temperature.equals("")){
                    Toast.makeText(getApplicationContext(),"الرجاء اختيار درجة الحرارة" , Toast.LENGTH_LONG).show();
                }else {
                    prefs.edit().putString("Key", temperature).apply();
                    TextView text1= (TextView) findViewById(R.id.textView5);


                    text1.setText(temperature+"°C");
                    stopService(intent);
                    // intent.putExtra("temp",temperature );
                    startService(intent);
                }
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, myService.class);


                prefs.edit().putString("Key1", "stop").apply();
                stopService(intent);
                //  intent.putExtra("temp","stop");
                startService(intent);

                Toast.makeText(getApplicationContext(),"service stopped" , Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent=new Intent(MainActivity.this, myService.class);

        prefs.edit().putString("Key1", "go").apply();
        stopService(intent);

        //  startService(intent);
        ContextCompat.startForegroundService(MainActivity.this,intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        if(position!=0){
            //   Toast.makeText(getApplicationContext(),country[position] , Toast.LENGTH_LONG).show();
            temperature=country[position];}
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
