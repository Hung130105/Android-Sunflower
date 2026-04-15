package com.example.sunflower.models;

import com.google.gson.annotations.SerializedName;

public class DeThi {

    @SerializedName("MaDeThi")
    private int MaDeThi;

    @SerializedName("TenDeThi")
    private String TenDeThi;

    @SerializedName("MoTa")
    private String MoTa;

    @SerializedName("ThoiGianLam")
    private int ThoiGianLam;

    @SerializedName("SoCau")
    private int SoCau;

    @SerializedName("TrangThai")
    private String TrangThai;

    @SerializedName("TrangThaiText")
    private String TrangThaiText;

    // ===== GETTER AN TOÀN =====
    public int getMaDeThi() { return MaDeThi; }

    public String getTenDeThi() {
        return TenDeThi != null ? TenDeThi : "Không có tên";
    }

    public String getMoTa() {
        return MoTa != null ? MoTa : "Không có mô tả";
    }

    public int getThoiGianLam() { return ThoiGianLam; }

    public int getSoCau() { return SoCau; }

    public String getTrangThai() {
        return TrangThai != null ? TrangThai : "";
    }

    public String getTrangThaiText() {
        return TrangThaiText != null ? TrangThaiText : "";
    }
}