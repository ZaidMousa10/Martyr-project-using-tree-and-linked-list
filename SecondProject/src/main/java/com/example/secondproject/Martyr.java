package com.example.secondproject;

public class Martyr implements Comparable<Martyr>{
    private String name;
    private int age;
    private String gender;

    public Martyr(String name, int age, String gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public int compareTo(Martyr other) {
        int ageComparison = this.age - other.age;

        if (ageComparison != 0) {
            return ageComparison;
        } else {
            return this.gender.compareToIgnoreCase(other.gender);
        }
    }


    @Override
    public String toString() {
        return "Martyr{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                '}';
    }
}
