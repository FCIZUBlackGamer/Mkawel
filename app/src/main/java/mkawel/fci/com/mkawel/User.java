package mkawel.fci.com.mkawel;

import io.realm.RealmObject;

public class User extends RealmObject {

    int userId, catId;
    String name, image, cap, job_title, phone, type;
    float rate;

    public User() {
    }

    public User(int userId, int catId, String name, String image, String cap) {
        this.userId = userId;
        this.catId = catId;
        this.name = name;
        this.image = image;
        this.cap = cap;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public float getRate() {
        return rate;
    }

    public String getJob_title() {
        return job_title;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public int getCatId() {
        return catId;
    }

    public String getCap() {
        return cap;
    }
}
