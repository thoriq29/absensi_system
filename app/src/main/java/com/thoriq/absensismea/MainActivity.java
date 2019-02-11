//package com.thoriq.absensismea;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//
//import com.thoriq.absensismea.Activity.Login;
//
//public class MainActivity extends AppCompatActivity {
//    //19B293
//    SharedPreferences shared;
//    String email;
//    TextView Email;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//        shared = getSharedPreferences(Login.my_shared_pref, Context.MODE_PRIVATE);
//        email = getIntent().getStringExtra("email");
//        Email = (TextView) findViewById(R.id.email);
//        Email.setText(email);
//        Email.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedPreferences.Editor editor = shared.edit();
//                editor.putBoolean("isLogin",false);
//                editor.putString("email",null);
//                editor.commit();
//
//                Intent intent = new Intent(MainActivity.this,Login.class);
//                finish();
//                startActivity(intent);
//            }
//        });
//    }
//}
