package Model;

import java.util.Date;

public class comment {
    private String comment;
    private String publisher;
    private String id;
    private Date date;
    private String postOwner;
    private String notification_id;
    public comment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPostOwner() {
        return postOwner;
    }

    public void setPostOwner(String postOwner) {
        this.postOwner = postOwner;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public comment(String comment, String publisher, String id, Date date, String postOwner, String notification_id) {
        this.comment = comment;
        this.publisher = publisher;
        this.id = id;
        this.date=date;
        this.postOwner=postOwner;
        this.notification_id=notification_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }


}
