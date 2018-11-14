package mkawel.fci.com.mkawel.Employee;

import java.util.List;

public class Employee {
    int Id;
    String Name;
    String JobTitle;
    String Image;
    String Phone;
    List<Project> projects;

    public Employee(int id, String name, String jobTitle, String image, String phone) {
        Id = id;
        Name = name;
        JobTitle = jobTitle;
        Image = image;
        Phone = phone;
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

    public void setJobTitle(String jobTitle) {
        JobTitle = jobTitle;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
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

    public List<Project> getProjects() {
        return projects;
    }

    public String getJobTitle() {
        return JobTitle;
    }

    public String getPhone() {
        return Phone;
    }
}
