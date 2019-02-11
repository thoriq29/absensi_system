package com.thoriq.absensismea.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.thoriq.absensismea.Config.Server;
import com.thoriq.absensismea.Home;
import com.thoriq.absensismea.R;
import com.thoriq.absensismea.Volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thoriq on 4/16/2018.
 */

public class CekDataSiswa extends Fragment {

    String id, nis,nama,kelas,foto,jurusan,email,nohp_ortu;
    TextView hm;
    TextView Nama,Kelas;
    ImageView Foto;
    AdView adView;
    ProgressBar progressBar;
    LinearLayout layout;
    Button save;
    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cek_data_siswa,container,false);
        nis = getActivity().getIntent().getStringExtra("nis");
        Nama = (TextView) view.findViewById(R.id.nama_siswa);
        email = getActivity().getIntent().getStringExtra("email");
        Kelas = (TextView) view.findViewById(R.id.kelas_siswa);
        Foto = (ImageView) view.findViewById(R.id.foto_siswa);
        layout = (LinearLayout) view.findViewById(R.id.li);
        progressBar = (ProgressBar) view.findViewById(R.id.pg);
        loadData(nis);
        save = (Button) view.findViewById(R.id.btn_simpan_siswa);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id == "")
                {

                }else
                {
                    save();
                }

            }
        });
        //EEEE, dd MMM yyyy

        return view;
    }
    public void SendSMS()
    {
        final String pesan = "Selamat Pagi Orangtua/Wali Siswa dari "+nama+". Anak anda telah Hadir disekolah";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.SERVER_URL + "notif/Nexmo/example.php",
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
                params.put("no_hp",nohp_ortu);
                params.put("pesan",pesan);
                return params;
            }
        };
        AppController.getInstance(getActivity()).addToRequestQue(stringRequest);
    }
    public void save()
    {
        Date currentTime = Calendar.getInstance().getTime();
        java.text.SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd");
        java.text.SimpleDateFormat jam= new SimpleDateFormat("HH:mm");
        final String tgl = df.format(currentTime);
        final String time = jam.format(currentTime);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.SERVER_URL + "Data/saveAbsenQR.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    if(response.trim().equals("Success"))
                    {

                        updateSudahDiabsen(id);
                        Intent intent = new Intent(getActivity(), Home.class);
                        intent.putExtra("email",email);
                        intent.putExtra("key","3");
                        getActivity().finish();
                        startActivity(intent);
                    }else
                    {
                        Toast.makeText(getActivity(),response,Toast.LENGTH_SHORT).show();
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
                params.put("tgl",tgl);
                params.put("id_siswa",id);
                params.put("waktu",time);
                params.put("email",email);
                return params;
            }
        };
        AppController.getInstance(getActivity()).addToRequestQue(stringRequest);

    }

    private void updateSudahDiabsen(final String id) {
        StringRequest stringRequest  = new StringRequest(Request.Method.POST, Server.SERVER_URL + "Data/updateSudahAbsen.php",
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
                params.put("id",id);
                return params;
            }
        };
        AppController.getInstance(getActivity()).addToRequestQue(stringRequest);
    }

    public void loadData(final String nis)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.SERVER_URL + "Data/cekDataSiswa.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        save.setVisibility(View.VISIBLE);
                        layout.setVisibility(View.VISIBLE);
                        if (response.trim().equals("null"))
                        {
                            Toast.makeText(getActivity(),"Data tidak ditemukan!",Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("siswaQ");
                            JSONObject data = jsonArray.getJSONObject(0);
                            nama = data.getString("nama_lengkap");
                            foto = data.getString("foto");
                            kelas = data.getString("nama_kelas");
                            id = data.getString("id_siswa");
                            nohp_ortu = data.getString("email");
                            jurusan = data.getString("nama_jurusan");
                            Kelas.setText("Kelas : "+ kelas);
                            Nama.setText("Nama : "+ nama);

                            Glide.with(getActivity()).load(Server.IMG_URL+foto).into(Foto);
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
                params.put("nis",nis);
                params.put("email",email);
                return params;
            }
        };
        AppController.getInstance(getActivity()).addToRequestQue(stringRequest);
    }
}
