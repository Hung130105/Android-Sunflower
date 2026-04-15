package com.example.sunflower.models;

public class FlashCard {
    private int id;
    private String front;
    private String back;
    private String phonetic;
    private String example;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFront() { return front; }
    public void setFront(String front) { this.front = front; }
    public String getBack() { return back; }
    public void setBack(String back) { this.back = back; }
    public String getPhonetic() { return phonetic; }
    public void setPhonetic(String phonetic) { this.phonetic = phonetic; }
    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }
}