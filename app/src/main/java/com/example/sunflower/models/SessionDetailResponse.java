package com.example.sunflower.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SessionDetailResponse {
    @SerializedName("MaPhien")
    private int maPhien;

    @SerializedName("TenDeThi")
    private String tenDeThi;

    @SerializedName("DiemSo")
    private int diemSo;

    @SerializedName("SoCauDung")
    private int soCauDung;

    @SerializedName("SoCauSai")
    private int soCauSai;

    @SerializedName("SoCauKhongChon")
    private int soCauKhongChon;

    @SerializedName("ThoiGianBatDau")
    private String thoiGianBatDau;

    @SerializedName("ThoiGianKetThuc")
    private String thoiGianKetThuc;

    @SerializedName("chi_tiet")
    private List<CauHoi> chiTiet;

    // Getters and Setters
    public int getMaPhien() { return maPhien; }
    public void setMaPhien(int maPhien) { this.maPhien = maPhien; }
    public String getTenDeThi() { return tenDeThi; }
    public void setTenDeThi(String tenDeThi) { this.tenDeThi = tenDeThi; }
    public int getDiemSo() { return diemSo; }
    public void setDiemSo(int diemSo) { this.diemSo = diemSo; }
    public int getSoCauDung() { return soCauDung; }
    public void setSoCauDung(int soCauDung) { this.soCauDung = soCauDung; }
    public int getSoCauSai() { return soCauSai; }
    public void setSoCauSai(int soCauSai) { this.soCauSai = soCauSai; }
    public int getSoCauKhongChon() { return soCauKhongChon; }
    public void setSoCauKhongChon(int soCauKhongChon) { this.soCauKhongChon = soCauKhongChon; }
    public String getThoiGianBatDau() { return thoiGianBatDau; }
    public void setThoiGianBatDau(String thoiGianBatDau) { this.thoiGianBatDau = thoiGianBatDau; }
    public String getThoiGianKetThuc() { return thoiGianKetThuc; }
    public void setThoiGianKetThuc(String thoiGianKetThuc) { this.thoiGianKetThuc = thoiGianKetThuc; }
    public List<CauHoi> getChiTiet() { return chiTiet; }
    public void setChiTiet(List<CauHoi> chiTiet) { this.chiTiet = chiTiet; }
}