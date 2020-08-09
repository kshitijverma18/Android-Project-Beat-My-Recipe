package Model;

public class favourite {
    String Author_id;
    String dish_imageUrl;
    String dish_name;
    String post_id;


    public String getAuthor_id() {
        return Author_id;
    }

    public void setAuthor_id(String author_id) {
        Author_id = author_id;
    }

    public String getDish_imageUrl() {
        return dish_imageUrl;
    }

    public void setDish_imageUrl(String dish_imageUrl) {
        this.dish_imageUrl = dish_imageUrl;
    }

    public String getDish_name() {
        return dish_name;
    }

    public void setDish_name(String dish_name) {
        this.dish_name = dish_name;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }


    public favourite(String author_id, String dish_imageUrl, String dish_name, String post_id) {
        Author_id = author_id;
        this.dish_imageUrl = dish_imageUrl;
        this.dish_name = dish_name;
        this.post_id = post_id;

    }

    public favourite() {
    }
}
