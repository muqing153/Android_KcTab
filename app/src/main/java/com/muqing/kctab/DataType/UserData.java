package com.muqing.kctab.DataType;

public class UserData {
    public String account;
    public String password;

    public UserData(String account, String password) {
        this.account = account;
        this.password = password;
    }
    public UserData() {
        this.account = "";
        this.password = "";
    }


//    public static void Save(UserData userData) {
//        SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
//        editor.putString("account", userData.account);
//        editor.putString("password", userData.password);
//        editor.apply();
//    }
}
