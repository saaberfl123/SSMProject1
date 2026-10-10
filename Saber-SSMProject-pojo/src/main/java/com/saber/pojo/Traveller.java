package com.saber.pojo;

public class Traveller
{
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Traveller{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
