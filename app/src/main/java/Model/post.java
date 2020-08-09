package Model;

import java.util.Date;

public class post {
    private String Dish_name;
    private String hrs,mins;
    private String image_url;
    private String post_id;
    private String servings;
    private String user_id;
    private Date date;
    private long datedesc;
    private String ingredients;
    private String steps;
    private String chef_note;
    public post() {
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDatedesc() {
        return datedesc;
    }

    public void setDatedesc(long datedesc) {
        this.datedesc = datedesc;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getChef_note() {
        return chef_note;
    }

    public void setChef_note(String chef_note) {
        this.chef_note = chef_note;
    }

    public post(String dish_name, String hrs, String mins, String image_url, String post_id, String servings, String user_id, Date date, long datedesc, String ingredients, String steps, String chef_note) {
        Dish_name = dish_name;
        this.hrs = hrs;
        this.mins = mins;
        this.image_url = image_url;
        this.post_id = post_id;
        this.servings = servings;
        this.user_id = user_id;
        this.date=date;
        this.datedesc=datedesc;
        this.ingredients=ingredients;
        this.steps=steps;
        this.chef_note=chef_note;
    }

    public void setDish_name(String dish_name) {
        Dish_name = dish_name;
    }

    public void setHrs(String hrs) {
        this.hrs = hrs;
    }

    public void setMins(String mins) {
        this.mins = mins;
    }

    public void setimage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public void setServings(String servings) {
        this.servings = servings;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDish_name() {
        return Dish_name;
    }

    public String getHrs() {
        return hrs;
    }

    public String getMins() {
        return mins;
    }

    public String getimage_url() {
        return image_url;
    }

    public String getPost_id() {
        return post_id;
    }

    public String getServings() {
        return servings;
    }

    public String getUser_id() {
        return user_id;
    }




}
