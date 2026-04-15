package com.example.sunflower.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CauHoi {
    @SerializedName("MaCauHoi")
    private int MaCauHoi;

    @SerializedName("STT")
    private int STT;

    @SerializedName("NoiDung")
    private String NoiDung;

    @SerializedName("GiaiThich")
    private String GiaiThich;

    @SerializedName("AudioURL")
    private String AudioURL;

    @SerializedName("ImgURL")
    private String ImgURL;

    @SerializedName("TenPart")
    private int TenPart;

    @SerializedName("MaNhom")
    private Integer MaNhom;

    @SerializedName("nhom")
    private NhomData nhom;

    @SerializedName("dap_an")
    private List<DapAn> dap_an;

    // ✅ THÊM CÁC FIELD CHO REVIEW
    @SerializedName("DapAnChonId")
    private Integer dapAnChonId;

    @SerializedName("DapAnChonKyHieu")
    private String dapAnChonKyHieu;

    @SerializedName("IsCorrect")
    private boolean isCorrect;

    @SerializedName("DapAnDungKyHieu")
    private String dapAnDungKyHieu;

    @SerializedName("DapAnDungId")
    private Integer dapAnDungId;

    // Getters
    public int getMaCauHoi() { return MaCauHoi; }
    public int getSTT() { return STT; }
    public String getNoiDung() { return NoiDung; }
    public String getGiaiThich() { return GiaiThich; }
    public String getAudioURL() { return AudioURL; }
    public String getImgURL() { return ImgURL; }
    public int getTenPart() { return TenPart; }
    public Integer getMaNhom() { return MaNhom; }
    public NhomData getNhom() { return nhom; }
    public List<DapAn> getDap_an() { return dap_an; }

    // ✅ Getter cho review
    public Integer getDapAnChonId() { return dapAnChonId; }
    public String getDapAnChonKyHieu() { return dapAnChonKyHieu; }
    public boolean isCorrect() { return isCorrect; }
    public String getDapAnDungKyHieu() { return dapAnDungKyHieu; }
    public Integer getDapAnDungId() { return dapAnDungId; }

    // Setters
    public void setMaCauHoi(int MaCauHoi) { this.MaCauHoi = MaCauHoi; }
    public void setSTT(int STT) { this.STT = STT; }
    public void setNoiDung(String NoiDung) { this.NoiDung = NoiDung; }
    public void setGiaiThich(String GiaiThich) { this.GiaiThich = GiaiThich; }
    public void setAudioURL(String AudioURL) { this.AudioURL = AudioURL; }
    public void setImgURL(String ImgURL) { this.ImgURL = ImgURL; }
    public void setTenPart(int TenPart) { this.TenPart = TenPart; }
    public void setMaNhom(Integer MaNhom) { this.MaNhom = MaNhom; }
    public void setNhom(NhomData nhom) { this.nhom = nhom; }
    public void setDap_an(List<DapAn> dap_an) { this.dap_an = dap_an; }

    // Setter cho review
    public void setDapAnChonId(Integer dapAnChonId) { this.dapAnChonId = dapAnChonId; }
    public void setDapAnChonKyHieu(String dapAnChonKyHieu) { this.dapAnChonKyHieu = dapAnChonKyHieu; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
    public void setDapAnDungKyHieu(String dapAnDungKyHieu) { this.dapAnDungKyHieu = dapAnDungKyHieu; }
    public void setDapAnDungId(Integer dapAnDungId) { this.dapAnDungId = dapAnDungId; }

    public static class NhomData {
        @SerializedName("MaNhom")
        private int MaNhom;

        @SerializedName("AudioURL")
        private String AudioURL;

        @SerializedName("images")
        private List<ImageData> images;

        public int getMaNhom() { return MaNhom; }
        public void setMaNhom(int MaNhom) { this.MaNhom = MaNhom; }
        public String getAudioURL() { return AudioURL; }
        public void setAudioURL(String AudioURL) { this.AudioURL = AudioURL; }
        public List<ImageData> getImages() { return images; }
        public void setImages(List<ImageData> images) { this.images = images; }
    }

    public static class ImageData {
        @SerializedName("ImgURL")
        private String ImgURL;

        @SerializedName("ThuTu")
        private int ThuTu;

        public String getImgURL() { return ImgURL; }
        public void setImgURL(String ImgURL) { this.ImgURL = ImgURL; }
        public int getThuTu() { return ThuTu; }
        public void setThuTu(int ThuTu) { this.ThuTu = ThuTu; }
    }
}