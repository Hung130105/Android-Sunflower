package com.example.sunflower.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BoTu {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("icon")
    private String icon;

    @SerializedName("card_count")
    private int card_count;

    @SerializedName("cards")
    private List<FlashCard> cards;

    public BoTu() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public int getCard_count() { return card_count; }
    public void setCard_count(int card_count) { this.card_count = card_count; }
    public List<FlashCard> getCards() { return cards; }
    public void setCards(List<FlashCard> cards) { this.cards = cards; }
}