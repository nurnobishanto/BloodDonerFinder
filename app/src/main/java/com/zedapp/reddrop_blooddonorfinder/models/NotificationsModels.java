package com.zedapp.reddrop_blooddonorfinder.models;

public class NotificationsModels {
    String typeKey;
    String message;
    String type;
    String nKey;
    String reciever;
    String sender;
    public  NotificationsModels(){

    }

    public NotificationsModels(String typeKey, String message, String type, String nKey, String reciever, String sender) {
        this.typeKey = typeKey;
        this.message = message;
        this.type = type;
        this.nKey = nKey;
        this.reciever = reciever;
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getnKey() {
        return nKey;
    }

    public void setnKey(String nKey) {
        this.nKey = nKey;
    }
}

