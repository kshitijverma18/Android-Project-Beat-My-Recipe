package Model;

public class likes {
    boolean hasLiked=false;
    String notification_id;

    public boolean isHasLiked() {
        return hasLiked;
    }

    public likes() {
    }

    public void setHasLiked(boolean hasLiked) {
        this.hasLiked = hasLiked;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public likes(boolean hasLiked, String notification_id) {
        this.hasLiked = hasLiked;
        this.notification_id = notification_id;
    }
}
