package com.example.sunflower.models;

public class HistorySession {
    private int MaPhien;
    private String TenDeThi;
    private int DiemSo;
    private int DiemLC;
    private int DiemRC;
    private int SoCauDung;
    private int SoCauSai;
    private int SoCauKhongChon;
    private String ThoiGianBatDau;
    private String ThoiGianKetThuc;

    // Getters
    public int getMaPhien() { return MaPhien; }
    public String getTenDeThi() { return TenDeThi; }
    public int getDiemSo() { return DiemSo; }
    public int getDiemLC() { return DiemLC; }
    public int getDiemRC() { return DiemRC; }
    public int getSoCauDung() { return SoCauDung; }
    public int getSoCauSai() { return SoCauSai; }
    public int getSoCauKhongChon() { return SoCauKhongChon; }
    public String getThoiGianBatDau() { return ThoiGianBatDau; }
    public String getThoiGianKetThuc() { return ThoiGianKetThuc; }

    // Setters
    public void setMaPhien(int MaPhien) { this.MaPhien = MaPhien; }
    public void setTenDeThi(String TenDeThi) { this.TenDeThi = TenDeThi; }
    public void setDiemSo(int DiemSo) { this.DiemSo = DiemSo; }
    public void setDiemLC(int DiemLC) { this.DiemLC = DiemLC; }
    public void setDiemRC(int DiemRC) { this.DiemRC = DiemRC; }
    public void setSoCauDung(int SoCauDung) { this.SoCauDung = SoCauDung; }
    public void setSoCauSai(int SoCauSai) { this.SoCauSai = SoCauSai; }
    public void setSoCauKhongChon(int SoCauKhongChon) { this.SoCauKhongChon = SoCauKhongChon; }
    public void setThoiGianBatDau(String ThoiGianBatDau) { this.ThoiGianBatDau = ThoiGianBatDau; }
    public void setThoiGianKetThuc(String ThoiGianKetThuc) { this.ThoiGianKetThuc = ThoiGianKetThuc; }
}