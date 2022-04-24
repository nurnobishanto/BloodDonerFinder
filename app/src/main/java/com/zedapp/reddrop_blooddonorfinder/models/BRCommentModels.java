package com.zedapp.reddrop_blooddonorfinder.models;

public class BRCommentModels {

    String ckey,userid,comment,name;

    public BRCommentModels(String ckey, String userid, String comment, String name) {
        this.ckey = ckey;

        this.userid = userid;
        this.comment = comment;
        this.name = name;
    }

    public  BRCommentModels(){

    }

    public String getCkey() {
        return ckey;
    }

    public void setCkey(String ckey) {
        this.ckey = ckey;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
