package com.example.myapp;

//동물병원 즐겨찾기 db정보

public class animal {
    String name;
    String number;
    String address;

    public animal(){}

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public animal(String name, String number, String address){
        this.name = name;
        this.number = number;
        this.address = address;
    }
}
