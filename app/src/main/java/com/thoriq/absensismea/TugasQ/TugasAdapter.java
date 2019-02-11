package com.thoriq.absensismea.TugasQ;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thoriq.absensismea.Config.Server;
import com.thoriq.absensismea.DaftarHadir.ModelBelumAbsen;
import com.thoriq.absensismea.R;

import java.util.List;

/**
 * Created by Thoriq on 5/2/2018.
 */

public class TugasAdapter extends ArrayAdapter<TugasModel> {
    private List<TugasModel> tugasModel;

    private Context context;

    public TugasAdapter(List<TugasModel> tugasModel, Context mCtx)
    {
        super(mCtx, R.layout.list_tugas,tugasModel);
        this.tugasModel = tugasModel;
        this.context = mCtx;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View list = inflater.inflate(R.layout.list_tugas,null,true);

        TextView nama_tugas = list.findViewById(R.id.NamaTugas);
        TextView ket = list.findViewById(R.id.Ket);
        TextView nama_guru = list.findViewById(R.id.namaGuru);
        TextView id = list.findViewById(R.id.idTugas);
        TugasModel tugas = tugasModel.get(position);
        nama_guru.setText(tugas.getNama_guru());
        nama_tugas.setText("Tugas : "+tugas.getTugas());
        ket.setText("Keterangan : "+tugas.getKet());
        id.setText(tugas.getId());

        return list;

    }
}
