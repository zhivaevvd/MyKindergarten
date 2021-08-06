package Modules;

public class User {

    public String email, password, name, ageGroup;

    public User(){}

    public User(String email, String password, String name, String ageGroup){
        this.email = email;
        this. password = password;
        this.name = name;
        this.ageGroup = ageGroup;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }
}
