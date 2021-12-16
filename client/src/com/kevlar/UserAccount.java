package com.kevlar;

public class UserAccount {
    private String userName,masterPassword,validationKey;

    public UserAccount(String name, String mp,String vk){
        this.userName = name;
        this.masterPassword = mp;
        this.validationKey = vk;
    }

    public String getUserName(){
        return this.userName;
    }

    public String getMasterPassword(){
        return this.masterPassword;
    }

    public String getValidationKey(){
        return this.validationKey;
    }
}
