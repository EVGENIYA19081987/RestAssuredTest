package api;

public class СreateUser {
    private String name="JANE19";
    private Integer age=36;

    public СreateUser(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
    public СreateUser(){

    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }
}

