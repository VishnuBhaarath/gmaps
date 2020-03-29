package com.example.gmaps;
public class UserProfile {
    public String Age;
    public String name;

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserProfile(String username, String userage
    ){
        this.Age=userage;
        this.name=username;
    }
}
