package com.aryvart.uticianvender.customgallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aryvart.uticianvender.R;

/**
 * Created by ${Rajaji} on 30-06-2016.
 */
public class MainActivity extends Activity {
    Button btnClick;
    TextView tvPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GalleryAlbumActivity.class);
                startActivity(i);

            }
        });


    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    private void updateUI(Intent intent) {
        final String strVal = intent.getStringExtra("data");
        Log.i("VAL", "Path : " + strVal);
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] sPath = strVal.substring(1, strVal.length() - 1).split(",");

                for (int i = 0; i < sPath.length; i++) {
                    Log.i("PATH", "Value : " + sPath[i]);
                    tvPath.append(sPath[i] + "\n");
                }

                //Using the Above path convert the path into file and send it to where ever you want

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter("com.recyclergrid.multiselect"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }


}
