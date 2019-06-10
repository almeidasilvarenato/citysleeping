package com.example.renato.cidadedorme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    //Importações e variaveis
    //==============================================================================================

    //importação da tela de login
    Intent inicialLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Importação e inicio da Intent login
        //==========================================================================================

        inicialLogin = new Intent (this, InicialLoginActivity.class);
        startActivity(inicialLogin);
        finish();

        //------------------------------------------------------------------------------------------


    }
}
