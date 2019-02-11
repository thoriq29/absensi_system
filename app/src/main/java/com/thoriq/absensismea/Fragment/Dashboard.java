package com.thoriq.absensismea.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.vision.barcode.Barcode;
import com.thoriq.absensismea.Activity.Login;
import com.thoriq.absensismea.Config.Server;
import com.thoriq.absensismea.R;
import com.thoriq.absensismea.Volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.barcode.BarcodeReader;

/**
 * Created by Thoriq on 4/16/2018.
 */

public class Dashboard extends Fragment {


    String email,total,laki,cewe,hadir_hariini;
    TextView jmlh,hadir,blmHadir,Persen;
    ProgressBar bar,bar1,bar2,bar3;
    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main,container,false);
        email = getActivity().getIntent().getStringExtra("email");
        jmlh = view.findViewById(R.id.tvTotalSiswa);
        hadir = view.findViewById(R.id.tvTotalHadir);
        bar = view.findViewById(R.id.pg);
        bar1 = view.findViewById(R.id.pg1);
        bar2 = view.findViewById(R.id.pg2);
        blmHadir = view.findViewById(R.id.tvTotalBelumHadir);
        Persen = view.findViewById(R.id.tvPersen);
        bar3 = view.findViewById(R.id.pg3);
        loadJmlhSiswa();
        return view;
    }
    public void loadJmlhSiswa()
    {
        Date currentTime = Calendar.getInstance().getTime();
        java.text.SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd");
        java.text.SimpleDateFormat jam= new SimpleDateFormat("HH:mm");
        final String tgl = df.format(currentTime);
        //363A45
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.SERVER_URL + "Data/dataHome.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        jmlh.setVisibility(View.VISIBLE);
                        hadir.setVisibility(View.VISIBLE);
                        blmHadir.setVisibility(View.VISIBLE);
                        Persen.setVisibility(View.VISIBLE);
                        bar.setVisibility(View.GONE);
                        bar1.setVisibility(View.GONE);
                        bar2.setVisibility(View.GONE);
                        bar3.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("HomeQ");
                            JSONObject data = jsonArray.getJSONObject(0);
                            total = data.getString("total");
                            laki = data.getString("laki");
                            cewe = data.getString("cewe");
                            hadir_hariini = data.getString("hadir");
                            int blmHadirr = Integer.parseInt(total)-Integer.parseInt(hadir_hariini);
                            int persen = (Integer.parseInt(hadir_hariini)*100)/Integer.parseInt(total);
                            jmlh.setText(total+" "+"("+laki+"/"+cewe+")");
                            hadir.setText(hadir_hariini);
                            blmHadir.setText(String.valueOf(blmHadirr));
                            Persen.setText(String.valueOf(persen)+"%");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                jmlh.setVisibility(View.INVISIBLE);
                hadir.setVisibility(View.INVISIBLE);
                blmHadir.setVisibility(View.INVISIBLE);
                Persen.setVisibility(View.INVISIBLE);
                bar.setVisibility(View.VISIBLE);
                bar1.setVisibility(View.VISIBLE);
                bar2.setVisibility(View.VISIBLE);
                bar3.setVisibility(View.VISIBLE);
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
        AppController.getInstance(getActivity()).addToRequestQue(stringRequest);
    }


}

