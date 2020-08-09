package Model;

public class User {
    private String name;
    private String bio;
    private String username;
    private String website;
    private String image_url;
    private String user_id;
    public User() {
    }

    public User(String name, String bio, String username, String website, String image_url, String user_id) {
        this.name = name;
        this.bio = bio;
        this.username = username;
        this.website = website;
        this.image_url = image_url;
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


}
