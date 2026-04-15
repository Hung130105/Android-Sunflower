package com.example.sunflower.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ExamDetail {
    @SerializedName("MaDeThi")
    private int MaDeThi;

    @SerializedName("TenDeThi")
    private String TenDeThi;

    @SerializedName("MoTa")
    private String MoTa;

    @SerializedName("ThoiGianLam")
    private int ThoiGianLam;

    @SerializedName("questions")
    private List<CauHoi> questions;

    public int getMaDeThi() { return MaDeThi; }
    public void setMaDeThi(int MaDeThi) { this.MaDeThi = MaDeThi; }
    public String getTenDeThi() { return TenDeThi; }
    public void setTenDeThi(String TenDeThi) { this.TenDeThi = TenDeThi; }
    public String getMoTa() { return MoTa; }
    public void setMoTa(String MoTa) { this.MoTa = MoTa; }
    public int getThoiGianLam() { return ThoiGianLam; }
    public void setThoiGianLam(int ThoiGianLam) { this.ThoiGianLam = ThoiGianLam; }
    public List<CauHoi> getQuestions() { return questions; }
    public void setQuestions(List<CauHoi> questions) { this.questions = questions; }
}