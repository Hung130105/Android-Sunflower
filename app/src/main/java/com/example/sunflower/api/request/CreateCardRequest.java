package com.example.sunflower.api.request;

public class CreateCardRequest {
    private String front;
    private String back;
    private String phonetic;
    private String example;

    public CreateCardRequest(String front, String back, String phonetic, String example) {
        this.front = front;
        this.back = back;
        this.phonetic = phonetic;
        this.example = example;
    }

    public String getFront() { return front; }
    public void setFront(String front) { this.front = front; }
    public String getBack() { return back; }
    public void setBack(String back) { this.back = back; }
    public String getPhonetic() { return phonetic; }
    public void setPhonetic(String phonetic) { this.phonetic = phonetic; }
    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }
}