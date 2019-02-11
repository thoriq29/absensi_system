
package com.thoriq.absensismea.DaftarHadir;

/**
 * Created by Thoriq on 4/18/2018.
 */

public class ModelBelumAbsen {
    String id,nama,nis,foto;

    public ModelBelumAbsen(String id, String nama, String nis, String foto) {
        this.id = id;
        this.nama = nama;
        this.nis = nis;
        this.foto = foto;
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
}
