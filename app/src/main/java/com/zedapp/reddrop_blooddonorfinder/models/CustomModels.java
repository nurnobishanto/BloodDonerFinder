package com.zedapp.reddrop_blooddonorfinder.models;

public class CustomModels {
    String name,city,address,hotline,phonenumber,location,key,dataType;
    int icon;



    public CustomModels(int icon, String name, String city, String address, String hotline, String phonenumber, String location, String key, String dataType) {
        this.icon = icon;
        this.name = name;
        this.city = city;
        this.address = address;
        this.hotline = hotline;
        this.phonenumber = phonenumber;
        this.location = location;
        this.key = key;
        this.dataType = dataType;

    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public CustomModels(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHotline() {
        return hotline;
    }

    public void setHotline(String hotline) {
        this.hotline = hotline;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
