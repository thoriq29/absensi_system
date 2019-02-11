package com.thoriq.absensismea.DaftarHadir;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thoriq.absensismea.Config.Server;
import com.thoriq.absensismea.R;

import java.util.List;

/**
 * Created by Thoriq on 4/18/2018.
 */

public class AdapterBelumAbsen extends ArrayAdapter<ModelBelumAbsen> {
    private List<ModelBelumAbsen> belumAbsenList;

    private Context context;

    public AdapterBelumAbsen(List<ModelBelumAbsen> belumAbsenList, Context mCtx)
    {
        super(mCtx, R.layout.list_daftarhadir,belumAbsenList);
        this.belumAbsenList = belumAbsenList;
        this.context = mCtx;
    }


    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View list = inflater.inflate(R.layout.list_daftarhadir,null,true);

        TextView nama = list.findViewById(R.id.namaSiswa);
        TextView NIS = list.findViewById(R.id.NISSIswa);
        ImageView Foto = list.findViewById(R.id.fotoSiswa);
        TextView id = list.findViewById(R.id.idSiswa);
        ModelBelumAbsen absen = belumAbsenList.get(position);
        nama.setText(absen.getNama());
        NIS.setText(absen.getNis());
        Glide.with(context).load(Server.IMG_URL+absen.getFoto()).into(Foto);
        id.setText(absen.getId());

        return list;

    }
}
