package com.example.renato.cidadedorme.dados;

public class LoginFacebook {

    //Variaveis
    static boolean conta;
    static String Uid = "";
    static String email = "";
    static String nome;
    static String urlPhoto;

    public static String getUid() {
        return Uid;
    }

    public static void setUid(String uid) {
        Uid = uid;
    }


    public static boolean getConta() {
        return conta;
    }

    public static void setConta(boolean conta) {
        LoginFacebook.conta = conta;
    }


    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        LoginFacebook.email = email;
    }





}
