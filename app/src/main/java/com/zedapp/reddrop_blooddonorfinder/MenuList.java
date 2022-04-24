package com.zedapp.reddrop_blooddonorfinder;

public class MenuList {
    String menuName;
    int menuImg;

public  MenuList(){
}

    public MenuList(String menuName, int menuImg) {
        this.menuName = menuName;
        this.menuImg = menuImg;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getMenuImg() {
        return menuImg;
    }

    public void setMenuImg(int menuImg) {
        this.menuImg = menuImg;
    }
}