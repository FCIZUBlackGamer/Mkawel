package mkawel.fci.com.mkawel.Employee;

public class Project {
    int Id;
    float rate;
    String Name;
    String Image;
    String Description;

    public Project(int id, float rate, String name, String image, String description) {
        Id = id;
        this.rate = rate;
        Name = name;
        Image = image;
        Description = description;
    }

    public Project() {
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getName() {
        return Name;
    }

    public String getImage() {
        return Image;
    }

    public int getId() {
        return Id;
    }

    public float getRate() {
        return rate;
    }
}
