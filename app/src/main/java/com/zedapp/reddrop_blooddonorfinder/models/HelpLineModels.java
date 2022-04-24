package com.zedapp.reddrop_blooddonorfinder.models;

public class HelpLineModels {
    int icon;
    String name,number;

    public  HelpLineModels(){

    }

    public HelpLineModels(int icon, String name, String number) {
        this.icon = icon;
        this.name = name;
        this.number = number;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}
