package com.example.kshitijverma.beat_my_recipie;

public class explore_class {
    public String country;
    public String dish;
    public String image;
    public explore_class()
    {

    }
    public explore_class(String country, String desc)
    {
        this.country=country;
        this.dish=dish;
    }
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public  String getDish() {
        return dish;
    }

    public void setDish(String dish) {
        this.dish = dish;
    }

    public String getImage()
    {
        return image;
    }
    public void setImage(String image)
    {
        this.image=image;
    }

    /*
    public String getCountryName() { return country; }
    public void setCountryName(String country){this.country=country;}
    public void setdishName(String dish){this.dish=dish;}
    public String getDishName() { return dish; }

     */
}
