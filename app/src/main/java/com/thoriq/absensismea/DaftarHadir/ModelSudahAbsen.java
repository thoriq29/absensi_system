package com.thoriq.absensismea.DaftarHadir;

/**
 * Created by Thoriq on 4/19/2018.
 */

public class ModelSudahAbsen {

    String id,nama,nis,foto,keterangan;

    public ModelSudahAbsen(String id, String nama, String nis, String foto,String keterangan) {
        this.id = id;
        this.nama = nama;
        this.nis = nis;
        this.foto = foto;
        this.keterangan = keterangan;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getNis() {
        return nis;
    }

    public String getFoto() {
        return foto;
    }
    public String getKeterangan()
    {
        return keterangan;
    }
}
