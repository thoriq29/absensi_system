package com.thoriq.absensismea;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.thoriq.absensismea.Activity.Login;
import com.thoriq.absensismea.Activity.QRScanner;
import com.thoriq.absensismea.Config.Server;
import com.thoriq.absensismea.Fragment.CekDataSiswa;
import com.thoriq.absensismea.Fragment.DaftarHadirSiswa;
import com.thoriq.absensismea.Fragment.Dashboard;
import com.thoriq.absensismea.TugasQ.Tugas;
import com.thoriq.absensismea.Volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity  implements LocationListener{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    FragmentTransaction tx;
    Class fragmentClass;
    Fragment fragment = null;
    TextView Nama,Kelas;
    AdView adView;
    RelativeLayout Error;
    TextView Info;
    ImageView FotoSekre,Cek;
    LocationManager locationManager;
    String key,email,nama,kelas,foto,nis,tgl;
    SharedPreferences shared;
    String mprovider,lat,lng,time;
    GoogleApiClient googleApiClient;
    AlertDialog dialog;
    String state,status;

    final static int REQ_LOC = 199;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.setFinishOnTouchOutside(true);
        state = "Home";

        init();
        loadTugas();
    }
    public void loadTugas()
    {
        Date currentTime = Calendar.getInstance().getTime();
        java.text.SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd");
        java.text.SimpleDateFormat jam= new SimpleDateFormat("HH:mm");
        final String tgl = df.format(currentTime);
        //363A45
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.SERVER_URL + "Data/countTugas.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("TugasQ");
                            JSONObject data = jsonArray.getJSONObject(0);
                            String total = data.getString("total");
                            setNavItemCount(R.id.tugas,Integer.parseInt(total));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",email);
                params.put("tgl",tgl);
                return params;
            }
        };
        AppController.getInstance(getApplicationContext()).addToRequestQue(stringRequest);
    }
    public void enableGPS()
    {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        }
                    }).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30*1000);
            locationRequest.setFastestInterval(5 *1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            final PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient,builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                    final Status status = locationSettingsResult.getStatus();
                    switch (status.getStatusCode())
                    {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try
                            {
                                status.startResolutionForResult(Home.this,REQ_LOC);

                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            });

    }
    public void getLatLng()
    {
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        mprovider = locationManager.getBestProvider(criteria,false);
        if (mprovider != null && !mprovider.equals(" "))
        {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            Location location = locationManager.getLastKnownLocation(mprovider);
            locationManager.requestLocationUpdates(mprovider,1000,1, (LocationListener) this);
            if (location != null)
            {
                onLocationChanged(location);
            }
        }
    }
    public void loadAd()
    {
        adView.setAdListener(new AdListener()
        {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Toast.makeText(getApplicationContext(),"as",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }
        });
    }
    private void setNavItemCount(@IdRes int Id,int count)
    {
        TextView view = (TextView) navigationView.getMenu().findItem(Id).getActionView();
        view.setText(count > 0 ? String.valueOf(count)+" New":null);

    }
    public void init()
    {

        Error = (RelativeLayout) findViewById(R.id.lyerror);
        Info = (TextView) findViewById(R.id.tvInfo);
        Info.setSelected(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        shared = getSharedPreferences(Login.my_shared_pref, Context.MODE_PRIVATE);
        key = getIntent().getStringExtra("key");
        nis = getIntent().getStringExtra("nis");
        email = getIntent().getStringExtra("email");
        if (Build.VERSION.SDK_INT >= 21)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerL);
        navigationView = (NavigationView) findViewById(R.id.viewD);
        View hView =  navigationView.inflateHeaderView(R.layout.header);
        Nama = (TextView) hView.findViewById(R.id.namaSekolahHeader);
        Kelas = (TextView) hView.findViewById(R.id.alamatSekolahHeader);
        FotoSekre = (ImageView) hView.findViewById(R.id.fotoSekolahHeader);
        Cek = (ImageView) hView.findViewById(R.id.imgCek);
        toggle =new ActionBarDrawerToggle(this,drawerLayout,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawer(navigationView);
        loadData(email);
        Kelas.setText(email);
        String ll = "2";
        String lll = "3";
        getLatLng();
        if (key.equals(ll))
        {
            tx = getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.content, new CekDataSiswa());
            tx.commit();
        }else if (key.equals(lll))
        {
            tx = getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.content, new DaftarHadirSiswa());
            tx.commit();
        }
            else
        {
            tx = getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.content, new Dashboard());
            tx.commit();
        }
        loadTgl(email);


    }
    public void calc()
    {
        Location start = new Location("LocationA");
        start.setLatitude(-6.935222);
        start.setLongitude(106.925999);

        Location end = new Location("LocationB");
        end.setLatitude(-6.905647);
        end.setLongitude(106.930640);



    }
    public void loadData(final String email)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.SERVER_URL + "Data/loadDataSekretaris.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("sekreQ");
                            JSONObject data = jsonArray.getJSONObject(0);
                            nama = data.getString("nama_sekretaris");
                            foto = data.getString("foto");

                            Nama.setText(nama);
                            Glide.with(getApplicationContext()).load(Server.IMG_URL+foto).into(FotoSekre);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",email);
                return params;
            }
        };
        AppController.getInstance(getApplicationContext()).addToRequestQue(stringRequest);
    }
    public void loadTgl(final String email)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.SERVER_URL + "Data/selectTgl.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.trim().equals("Null"))
                        {
                            update();
                        }else
                        {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("sekreQ");
                                JSONObject data = jsonArray.getJSONObject(0);
                                tgl = data.getString("tanggal");
                                Date currentTime = Calendar.getInstance().getTime();
                                java.text.SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd");
                                final String date = df.format(currentTime);

                                if (tgl.trim().equals(date))
                                {

                                }else
                                {
                                    update();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",email);
                return params;
            }
        };
        AppController.getInstance(getApplicationContext()).addToRequestQue(stringRequest);
    }

    private void update() {
        StringRequest stringRequest  = new StringRequest(Request.Method.POST, Server.SERVER_URL + "Data/update.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",email);
                return params;
            }
        };
        AppController.getInstance(getApplicationContext()).addToRequestQue(stringRequest);
    }

    public void selectedItemDrawe(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.home:
                if (state.equals("Home"))
                {
                    drawerLayout.closeDrawers();
                    return;

                }else
                {
                    fragmentClass = Dashboard.class;
                    state = "Home";

                }
                break;
            case R.id.scan:
                Intent intent =new Intent(this, QRScanner.class);
                intent.putExtra("email",email);
                finish();
                startActivity(intent);
                break;
            case R.id.daftar:
                if (state.equals("Daftar"))
                {
                    drawerLayout.closeDrawers();
                    return;
                }else
                {
                    fragmentClass =DaftarHadirSiswa.class;
                    state = "Daftar";

                }
                break;
            case R.id.tugas:
                if (state.equals("Tugas"))
                {
                    drawerLayout.closeDrawers();
                    return;
                }else
                {
                    fragmentClass =Tugas.class;
                    state = "Tugas";
                }
                break;
            case R.id.log:
                SharedPreferences.Editor editor = shared.edit();
                editor.putBoolean("isLogin",false);
                editor.putString("email",null);
                editor.commit();

                Intent intent1 = new Intent(Home.this,Login.class);
                finish();
                startActivity(intent1);
                break;
            default:
                fragmentClass = Dashboard.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content,fragment).commit();
        item.setChecked(true);
        setTitle("Absensi Siswa");
        drawerLayout.closeDrawers();
    }
    private void setupDrawer(NavigationView navigationView)
    {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectedItemDrawe(item);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLatLng();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerL);
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else
        {
            if (checkNavigationMenu() !=0)
            {
                navigationView.setCheckedItem(R.id.home);
                tx = getSupportFragmentManager().beginTransaction();
                tx.replace(R.id.content, new Dashboard());

                tx.commit();
            }else
            {
                super.onBackPressed();
            }
        }

    }
    private int checkNavigationMenu()
    {
        Menu menu = navigationView.getMenu();
        for (int i =0; i<menu.size(); i++)
        {
            if (menu.getItem(i).isChecked())
                return i;
        }
        return -0;
    }

    @Override
    public void onLocationChanged(Location location) {
        Location start = new Location("LocationA");
        start.setLatitude(-6.935222);
        start.setLongitude(106.925999);

        Location end = new Location("LocationB");
        end.setLatitude(location.getLatitude());
        end.setLongitude(location.getLongitude());

        double distance = start.distanceTo(end);

        if (distance < 500)
        {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_luarsekolah);
            Error.setVisibility(View.VISIBLE);
              //  final AlertDialog.Builder builder = new AlertDialog.Builder(this);
       //         builder.setCancelable(false);
     //           builder.setTitle("Sorry..") .setMessage("Untuk melanjutkan,Datanglah ke Sekolah!") .setPositiveButton("OK", new DialogInterface.OnClickListener() {
   //                 @Override
 //                   public void onClick(DialogInterface dialogInterface, int i) {

                    //    finish();
                  //  }
                //});
               // dialog = builder.create();
                //dialog.show();

        }else
        {
            absenDiriSendiri();
            String status="OK";
            Error.setVisibility(View.INVISIBLE);
            if (status.equals("OK"))
            {
                return;
            }else
            {
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.drawer_profile);
            }

        }


    }
    private void updateSudahDiabsen(final String email) {

        StringRequest stringRequest  = new StringRequest(Request.Method.POST, Server.SERVER_URL + "Data/updateSudahAbsenSekre.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",email);
                return params;
            }
        };
        AppController.getInstance(getApplicationContext()).addToRequestQue(stringRequest);

    }
    public void absenDiriSendiri()
    {
        Date currentTime = Calendar.getInstance().getTime();
        java.text.SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd");
        java.text.SimpleDateFormat jam= new SimpleDateFormat("HH:mm");
        final String tgl = df.format(currentTime);
        final String time = jam.format(currentTime);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.SERVER_URL + "Data/absenDiriSendiri.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.trim().equals("Siswa Sudah diabsen"))
                        {

                            Cek.setVisibility(View.VISIBLE);
                        }else
                        {
                            updateSudahDiabsen(email);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",email);
                params.put("tgl",tgl);
                params.put("waktu",time);
                return params;
            }

        };
        AppController.getInstance(getApplicationContext()).addToRequestQue(stringRequest);
    }
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        enableGPS();
    }
}
