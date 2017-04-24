package com.solodroid.yourradioapp.models;

public class ItemListRadio {

    private String RadioId;
    private String RadioName;
    private String RadioCategoryName;
    private String RadioImageUrl;
    private String RadioUrl;
    private int id;

    public ItemListRadio() {
    }

    public ItemListRadio(String Radioid) {
        this.RadioId = Radioid;
    }

    public ItemListRadio(String Radioid, String Radioname, String Radiocategoryname, String Radiourl, String image) {
        this.RadioId = Radioid;
        this.RadioName = Radioname;
        this.RadioCategoryName = Radiocategoryname;
        this.RadioUrl = Radiourl;
        this.RadioImageUrl = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRadioId() {
        return RadioId;
    }

    public void setRadioId(String Radioid) {
        this.RadioId = Radioid;
    }

    public String getRadioName() {
        return RadioName;
    }

    public void setRadioName(String Radioname) {
        this.RadioName = Radioname;
    }

    public String getRadioCategoryName() {
        return RadioCategoryName;
    }

    public void setRadioCategoryName(String Radiocategoryname) {
        this.RadioCategoryName = Radiocategoryname;
    }

    public String getRadioImageurl() {
        return RadioImageUrl;
    }

    public void setRadioImageurl(String radioimage) {
        this.RadioImageUrl = radioimage;
    }

    public String getRadiourl() {
        return RadioUrl;
    }

    public void setRadiourl(String radiourl) {
        this.RadioUrl = radiourl;
    }

}
