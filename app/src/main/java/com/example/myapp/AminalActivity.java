package com.example.myapp;

public class AminalActivity {
    private String name;
    private String bloodtype;
    private String regnum;

    public void AnimalActivity(String name, String bloodtype, String regnum){
        this.name = name;
        this.bloodtype = bloodtype;
        this.regnum = regnum;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBloodtype() {
        return this.bloodtype;
    }

    public void setBloodtype(String bloodtype) {
        this.bloodtype = bloodtype;
    }

    public String getRegnum() {
        return this.regnum;
    }

    public void setRegnum(String regnum) {
        this.regnum = regnum;
    }
}
