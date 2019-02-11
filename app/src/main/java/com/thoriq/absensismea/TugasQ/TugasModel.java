package com.thoriq.absensismea.TugasQ;

/**
 * Created by Thoriq on 5/2/2018.
 */

public class TugasModel {
    String id,nama_guru,tugas,ket;

    public TugasModel(String id, String nama_guru, String tugas, String ket) {
        this.id = id;
        this.nama_guru = nama_guru;
        this.tugas = tugas;
        this.ket = ket;
    }

    public String getId() {
        return id;
    }

    public String getNama_guru() {
        return nama_guru;
    }

    public String getTugas() {
        return tugas;
    }

    public String getKet() {
        return ket;
    }
}
