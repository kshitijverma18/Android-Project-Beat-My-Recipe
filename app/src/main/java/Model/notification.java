package Model;

import java.util.Date;

public class notification {
    private String userId;
    private String text;
    private String postId;
    private Boolean isPost;
    private Date date;
    private String notification_id;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public notification(String userId, String text, String postId, Boolean isPost, Date date, String notification_id) {
        this.userId = userId;
        this.text = text;
        this.postId = postId;
        this.isPost = isPost;
        this.date=date;
        this.notification_id=notification_id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Boolean getIsPost() {
        return isPost;
    }

    public void setIsPost(Boolean post) {
        isPost = post;
    }

    public notification() {
    }
}

