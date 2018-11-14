package mkawel.fci.com.mkawel.Home;

import java.io.Serializable;

public class Category implements Serializable {
    int Id;
    int numProjects;
    String Name;
    String Image;
    String Description;

    public Category(int id, int numProjects, String name) {
        Id = id;
        this.numProjects = numProjects;
        Name = name;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setNumProjects(int numProjects) {
        this.numProjects = numProjects;
    }

    public int getId() {
        return Id;
    }

    public String getImage() {
        return Image;
    }

    public String getName() {
        return Name;
    }

    public int getNumProjects() {
        return numProjects;
    }

    public String getDescription() {
        return Description;
    }
}
