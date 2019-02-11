package com.thoriq.absensismea.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.thoriq.absensismea.Config.Server;
import com.thoriq.absensismea.DaftarHadir.AdapterBelumAbsen;
import com.thoriq.absensismea.DaftarHadir.AdapterSudahAbsen;
import com.thoriq.absensismea.DaftarHadir.ModelBelumAbsen;
import com.thoriq.absensismea.DaftarHadir.ModelSudahAbsen;
import com.thoriq.absensismea.Home;
import com.thoriq.absensismea.R;
import com.thoriq.absensismea.Volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Thoriq on 4/17/2018.
 */

public class DaftarHadirSiswa extends Fragment {

    ListView Lvbelum,Lvsudah;
    TextView tv1,tv2;
    AdapterSudahAbsen adapterSudahAbsen;
    AdapterBelumAbsen adapterBelumAbsen;
    List<ModelBelumAbsen> belumAbsenList;
    List<ModelSudahAbsen> sudahAbsenList;
    String email,id_siswa,nohp_ortu,nama;
    String IdKeterangan="4",Ket="Hadir";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.daftar_hadir_siswa,container,false);
        belumAbsenList = new ArrayList<>();
        Lvbelum = view.findViewById(R.id.list_belum);
        Lvsudah = view.findViewById(R.id.list_udah);
        tv1 = view.findViewById(R.id.tv1);
        tv2 = view.findViewById(R.id.tv2);
        sudahAbsenList = new ArrayList<>();
        adapterBelumAbsen = new AdapterBelumAbsen(belumAbsenList,getActivity());
        adapterSudahAbsen = new AdapterSudahAbsen(sudahAbsenList,getActivity());
        email = getActivity().getIntent().getStringExtra("email");
        loadBelumAbsen();
        loadSudahAbsen();
        LVOnClik();
        return view;
    }
    public void showDialog()
    {
        String Hadir,Izin,Alfa,Sakit;
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        View parentView = getLayoutInflater().inflate(R.layout.dialog_keterangan,null);
        Button batal = parentView.findViewById(R.id.btnBatal);
        Button pilih = parentView.findViewById(R.id.btnPilih);
        RadioGroup radioGroup = parentView.findViewById(R.id.rg);
        RadioButton hadir = parentView.findViewById(R.id.rbHadir);
        RadioButton sakit = parentView.findViewById(R.id.rbSakit);
        RadioButton izin = parentView.findViewById(R.id.rbIzin);
        RadioButton alfa = parentView.findViewById(R.id.rbAlfa);
        Hadir = hadir.getText().toString();
        Izin = izin.getText().toString();
        Sakit = sakit.getText().toString();
        Alfa = alfa.getText().toString();
        if (Ket.equals(Hadir))
        {
            hadir.setChecked(true);
        }else if (Ket.equals(Izin))
        {
            izin.setChecked(true);
        }else if (Ket.equals(Sakit))
        {
            sakit.setChecked(true);
        }else if (Ket.equals(Alfa))
        {
            alfa.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rbHadir)
                {
                    IdKeterangan = "4";
                    Ket = "Hadir";

                }else if (i == R.id.rbSakit)
                {
                    IdKeterangan = "1";
                    Ket = "Sakit";
                }else if (i == R.id.rbIzin)
                {
                    IdKeterangan = "2";
                    Ket = "Izin";
                }else if (i==R.id.rbAlfa)
                {
                    IdKeterangan = "3";
                    Ket = "Alfa";
                }

            }
        });
        bottomSheetDialog.setContentView(parentView);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentView.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        bottomSheetBehavior.setPeekHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1000,getResources().getDisplayMetrics()));
        bottomSheetDialog.show();
        pilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IdKeterangan == "" )
                {
                    Toast.makeText(getActivity(),"Pilih Keterangan",Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }else
                {
                    loadData();
                    save();

                    bottomSheetDialog.dismiss();
                }
            }
        });
        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id_siswa ="";
                IdKeterangan = "";
                Ket = "";
                bottomSheetDialog.dismiss();
            }
        });
    }
    public void loadData()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.SERVER_URL + "Data/loadDataSiswa.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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

                            nohp_ortu = data.getString("hp_ortu");


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
                params.put("id_siswa",id_siswa);
                params.put("email",email);
                return params;
            }
        };
        AppController.getInstance(getActivity()).addToRequestQue(stringRequest);
    }
    public void SendSMS() {
        final String pesan;
        if (Ket.equals("Hadir"))
        {
            pesan = "Selamat Pagi Orangtua/Wali Siswa dari " + nama + ". Anak anda telah " +Ket+" disekolah";
        }else if (Ket.equals("Sakit"))
        {
            pesan = "Selamat Pagi Orangtua/Wali Siswa dari " + nama + ". Anak anda tidak dapat Hadir dikarenakan "+Ket;
        }else if(Ket.equals("Izin"))
        {
            pesan = "Selamat Pagi Orangtua/Wali Siswa dari " + nama + ". Anak anda tidak dapat Hadir dikarenakan "+Ket;
        }else
        {
            pesan = "Selamat Pagi Orangtua/Wali Siswa dari " + nama + ". Anak anda tidak Hadir tanpa Keterangan ";
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.SERVER_URL + "notif/Nexmo/example.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("no_hp", nohp_ortu);
                params.put("pesan", pesan);
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.SERVER_URL + "Data/saveAbsenManual.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("Success"))
                        {
                            updateSudahDiabsen(id_siswa);

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
                params.put("id_siswa",id_siswa);
                params.put("waktu",time);
                params.put("email",email);
                params.put("id_ket",IdKeterangan);
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
                        loadBelumAbsen();
                        loadSudahAbsen();
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
    public void LVOnClik()
    {
        Lvbelum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView id = view.findViewById(R.id.idSiswa);
                id_siswa = id.getText().toString();
                showDialog();

            }
        });
    }
    public void loadSudahAbsen()
    {
        Date currentTime = Calendar.getInstance().getTime();
        java.text.SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd");
        final String date = df.format(currentTime);
        sudahAbsenList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.SERVER_URL+"Data/daftarSiswaSudahAbsen.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("null")) {

                            Lvsudah.setVisibility(View.GONE);
                            tv2.setVisibility(View.GONE);
                        }else
                        {
                            Lvsudah.setVisibility(View.VISIBLE);
                            tv2.setVisibility(View.VISIBLE);

                            try {
                                JSONObject obj = new JSONObject(response);
                                JSONArray array = obj.getJSONArray("SudahAbsen");

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject titipanObj = array.getJSONObject(i);
                                    ModelSudahAbsen semua = new ModelSudahAbsen(titipanObj.getString("id_absensi"), titipanObj.getString("nama_lengkap"),titipanObj.getString("NIS"),titipanObj.getString("foto"),titipanObj.getString("nama_keterangan"));
                                    sudahAbsenList.add(semua);
                                }
                                AdapterSudahAbsen listSemua = new AdapterSudahAbsen(sudahAbsenList, getActivity().getApplicationContext());
                                Lvsudah.setAdapter(listSemua);
                            }
                            catch (JSONException e)
                            {
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
                Map<String,String> params = new HashMap<String,String>();
                params.put("email",email);
                params.put("tgl",date);
                return params;
            }
        };
        AppController.getInstance(getActivity()).addToRequestQue(stringRequest);
    }
    public void loadBelumAbsen()
    {
        belumAbsenList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.SERVER_URL+"Data/daftarSiswaBelumAbsen.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("null")) {
                            Lvbelum.setVisibility(View.GONE);
                            tv1.setVisibility(View.GONE);
                        }else
                        {Lvbelum.setVisibility(View.VISIBLE);
                            tv1.setVisibility(View.VISIBLE);
                            try {
                                JSONObject obj = new JSONObject(response);
                                JSONArray array = obj.getJSONArray("BelumAbsen");

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject titipanObj = array.getJSONObject(i);
                                    ModelBelumAbsen semua = new ModelBelumAbsen(titipanObj.getString("id_siswa"), titipanObj.getString("nama_lengkap"),titipanObj.getString("NIS"),titipanObj.getString("foto"));
                                    belumAbsenList.add(semua);
                                }
                                AdapterBelumAbsen listSemua = new AdapterBelumAbsen(belumAbsenList, getActivity().getApplicationContext());
                                Lvbelum.setAdapter(listSemua);
                            }
                            catch (JSONException e)
                            {
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
                Map<String,String> params = new HashMap<String,String>();
                params.put("email",email);
                return params;
            }
        };
        AppController.getInstance(getActivity()).addToRequestQue(stringRequest);
    }
}
