package com.thoriq.absensismea.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.thoriq.absensismea.Config.Server;
import com.thoriq.absensismea.Home;
import com.thoriq.absensismea.R;
import com.thoriq.absensismea.Volley.AppController;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    SignInButton signInButto;
    Button logOut;
    CoordinatorLayout coordinatorLayout;
    Boolean session =false;
    GoogleApiClient client;
    public static final String my_shared_pref = "sessionLogin";
    public static final String session_status =     "session_status";
    SharedPreferences sharedPreferences;
    String email,nama,foto;
    private static final int REQ_CODE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initComponent();

    }
    public void signIn()
    {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(client);
        startActivityForResult(intent,REQ_CODE);

    }

    public void initComponent()
    {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        signInButto=(SignInButton) findViewById(R.id.btnsignin);
        signInButto.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(my_shared_pref, Context.MODE_PRIVATE);
        session =sharedPreferences.getBoolean("isLogin",false);
        email = sharedPreferences.getString(   "email",null);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        client =new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

        if (Build.VERSION.SDK_INT >= 21)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorBlak));

        }
        if(session)
        {
            Intent intent = new Intent(Login.this, Home.class);
            intent.putExtra("email",email);
            intent.putExtra("key","1");
            intent.putExtra("nis","2");
            finish();
            startActivity(intent);
        }
    }
    public void HandleResult(GoogleSignInResult result)
    {
        if (result.isSuccess())
        {
            GoogleSignInAccount account = result.getSignInAccount();
            email = account.getEmail();
            nama = account.getDisplayName();

            Login(email);

        }
    }
    public void Login(final String email)
    {final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Mohon Tunggu");
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.AUTH_URL+"login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("Success"))
                        {
                            dialog.dismiss();
                            Profile();
                            signOut();
                        }else
                        {
                            dialog.dismiss();
                            Snackbar.make(coordinatorLayout,response,Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            }).show();
                            signOut();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Snackbar.make(coordinatorLayout,error.getMessage(),Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
                signOut();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params  = new HashMap<String, String>();
                params.put("email",email);
                return params;
            }
        };
        AppController.getInstance(getApplicationContext()).addToRequestQue(stringRequest);
    }
    public void signOut()
    {
        Auth.GoogleSignInApi.signOut(client).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                cekStatus(false);
            }
        });
    }
    private void Profile()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogin",true);
        editor.putString("email",email);
        editor.putString("key","1");
        editor.putString("nis","2");
        editor.commit();
        Intent intent = new Intent(Login.this,Home.class);
        intent.putExtra("email",email);
        intent.putExtra("key","1");
        intent.putExtra("nis","2");
        finish();
        startActivity(intent);
    }
    public void cekStatus(boolean isLogin)
    {
        if (isLogin)
        {

        }else
        {

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(coordinatorLayout,"Terjadi Kesalahan,Coba Lagi..",Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).show();
    }

    public void progresDialog(String isi)
    {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Mohon Tunggu");
        dialog.setMessage(isi);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }
    @Override
    public void onClick(View view) {
        if (view == signInButto) {
            signIn();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==  REQ_CODE)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            HandleResult(result);
        }
    }
}
//SH1 : 93:09:8A:96:B3:B1:80:06:6C:FA:07:CF:C0:A8:EB:20:F1:AF:50:
