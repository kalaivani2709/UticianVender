package com.aryvart.uticianvender.utician;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.aryvart.uticianvender.R;

/**
 * Created by Android2 on 7/20/2016.
 */
public class HomePage extends Activity {
    TextView txt_login, txt_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.home_page);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            txt_login = (TextView) findViewById(R.id.txt_login);
            txt_register = (TextView) findViewById(R.id.txt_register);
            txt_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomePage.this, SignInActivity.class));
                    finish();
                }
            });
            txt_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomePage.this, SignUpActivity.class));
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
