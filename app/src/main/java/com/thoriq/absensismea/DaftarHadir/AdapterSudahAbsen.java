package com.thoriq.absensismea.DaftarHadir;

import android.content.Context;
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
 * Created by Thoriq on 4/19/2018.
 */

public class AdapterSudahAbsen extends ArrayAdapter<ModelSudahAbsen> {
    private List<ModelSudahAbsen> sudahAbsenList;

    private Context context;
    public AdapterSudahAbsen(List<ModelSudahAbsen> sudahAbsenList, Context mCtx)
    {
        super(mCtx, R.layout.list_sudah_hadir,sudahAbsenList);
        this.sudahAbsenList = sudahAbsenList;
        this.context = mCtx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View list = inflater.inflate(R.layout.list_sudah_hadir,null,true);

        TextView nama = list.findViewById(R.id.namaSiswa);
        TextView NIS = list.findViewById(R.id.NISSIswa);
        ImageView Foto = list.findViewById(R.id.fotoSiswa);
        TextView id = list.findViewById(R.id.idSiswa);
        ImageView ket = list.findViewById(R.id.keter);
        ModelSudahAbsen absen = sudahAbsenList.get(position);
        String keterangan = absen.getKeterangan();
        nama.setText(absen.getNama());
        NIS.setText(absen.getNis());
        Glide.with(context).load(Server.IMG_URL+absen.getFoto()).into(Foto);
        id.setText(absen.getId());
        String izin = "Izin";
        if (keterangan.equals(izin))
        {
            ket.setBackgroundResource(R.drawable.logoizin);
        }else if (keterangan.equals("Sakit"))
        {
            ket.setBackgroundResource(R.drawable.logosakit);
        }else if (keterangan.equals("Alfa"))
        {

            ket.setBackgroundResource(R.drawable.logoalfa);
        }
        return list;

    }

}
