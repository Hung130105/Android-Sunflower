package com.example.sunflower.models;

import com.google.gson.annotations.SerializedName;

public class DapAn {
    @SerializedName("MaDapAn")
    private int MaDapAn;

    @SerializedName("KyHieu")
    private String KyHieu;

    @SerializedName("NoiDung")
    private String NoiDung;

    @SerializedName("IsCorrect")
    private boolean IsCorrect;

    public int getMaDapAn() { return MaDapAn; }
    public void setMaDapAn(int MaDapAn) { this.MaDapAn = MaDapAn; }
    public String getKyHieu() { return KyHieu; }
    public void setKyHieu(String KyHieu) { this.KyHieu = KyHieu; }
    public String getNoiDung() { return NoiDung; }
    public void setNoiDung(String NoiDung) { this.NoiDung = NoiDung; }
    public boolean isCorrect() { return IsCorrect; }
    public void setCorrect(boolean correct) { IsCorrect = correct; }
}