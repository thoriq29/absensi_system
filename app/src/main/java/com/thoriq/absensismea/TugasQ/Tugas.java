package com.thoriq.absensismea.TugasQ;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.thoriq.absensismea.Config.Server;
import com.thoriq.absensismea.DaftarHadir.AdapterSudahAbsen;
import com.thoriq.absensismea.DaftarHadir.ModelSudahAbsen;
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

/**
 * Created by Thoriq on 4/30/2018.
 */

public class Tugas extends Fragment {


    List<TugasModel> tugasModel;
    TugasAdapter adapter;
    String email;
    TextView error;
    ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tugas, container, false);
        listView = (ListView) view.findViewById(R.id.listTugas);
        tugasModel = new ArrayList<>();
        email=getActivity().getIntent().getStringExtra("email");
        error = (TextView) view.findViewById(R.id.tvError);
        loadTugas();
        return view;
    }
    public void loadTugas()
    {

            Date currentTime = Calendar.getInstance().getTime();
            java.text.SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd");
            final String date = df.format(currentTime);
            tugasModel.clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.SERVER_URL+"Data/loadTugas.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response.trim().equals("null")) {

                                listView.setVisibility(View.GONE);
                                error.setVisibility(View.VISIBLE);
                            }else
                            {
                                listView.setVisibility(View.VISIBLE);
                                error.setVisibility(View.GONE);

                                try {
                                    JSONObject obj = new JSONObject(response);
                                    JSONArray array = obj.getJSONArray("TugasQ");

                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject titipanObj = array.getJSONObject(i);
                                        TugasModel semua = new TugasModel(titipanObj.getString("id_tugas"), titipanObj.getString("nama_guru"),titipanObj.getString("tugas"),titipanObj.getString("keterangan_lainnya"));
                                        tugasModel.add(semua);
                                    }
                                    adapter = new TugasAdapter(tugasModel,getActivity());
                                    listView.setAdapter(adapter);
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
}
