package com.thoriq.absensismea.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.thoriq.absensismea.Config.Server;
import com.thoriq.absensismea.Home;
import com.thoriq.absensismea.R;
import com.thoriq.absensismea.Volley.AppController;

import java.util.HashMap;
import java.util.Map;

public class NewGoogleSignIn extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{
    Boolean session =false;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    CoordinatorLayout coordinatorLayout;
    SharedPreferences sharedPreferences;
    String email=null, personName=null,foto;
    Button logout;
    public static final String my_shared_pref = "sessionLogin";
    private static final int RC_SIGN_IN = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initial();
    }
    private void initial(){
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        signInButton=(SignInButton) findViewById(R.id.btnsignin);
        logout = findViewById(R.id.btnlogout);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
        logout.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        sharedPreferences = getSharedPreferences(my_shared_pref, Context.MODE_PRIVATE);
        session =sharedPreferences.getBoolean("isLogin",false);
        email = sharedPreferences.getString(   "email",null);
        if (Build.VERSION.SDK_INT >= 21)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorBlak));

        }
        if(session)
        {
            Intent intent = new Intent(NewGoogleSignIn.this, Home.class);
            intent.putExtra("email",email);
            intent.putExtra("key","1");
            intent.putExtra("nis","2");
            finish();
            startActivity(intent);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void updateUI(GoogleSignInAccount account) {
//        if (account != null) {
//            personName = account.getDisplayName();
//            email = account.getEmail();
//            String personId = account.getId();
//            Uri personPhoto = account.getPhotoUrl();
//            Login(email);
//        }
    }

    private void getUser(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {
            personName = account.getDisplayName();
            email = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();
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

    private void Profile()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogin",true);
        editor.putString("email",email);
        editor.putString("key","1");
        editor.putString("nis","2");
        editor.commit();
        Intent intent = new Intent(NewGoogleSignIn.this, Home.class);
        intent.putExtra("email",email);
        intent.putExtra("key","1");
        intent.putExtra("nis","2");
        finish();
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("status", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void signOut(){
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    startActivity(new Intent(getApplicationContext(), Splashscreen.class));
                }
            });
    }

    @Override
    public void onClick(View view) {
        if (view == signInButton){
            getUser();
        }else if (view==logout){
            signOut();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
