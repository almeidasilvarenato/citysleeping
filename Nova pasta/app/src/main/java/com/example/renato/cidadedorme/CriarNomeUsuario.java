package com.example.renato.cidadedorme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.renato.cidadedorme.dados.DadosUsuario;

public class CriarNomeUsuario extends AppCompatActivity {

    //Implementaões
    Button pronto;
    EditText nickName;

    //Intents
    Intent telaInicial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_nome_usuario);

        final DadosUsuario dados = new DadosUsuario();

        nickName = (EditText) findViewById(R.id.nickName);
        telaInicial = new Intent(CriarNomeUsuario.this, TelaInicial.class);

        pronto = (Button) findViewById(R.id.pronto);

        pronto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dados.setNickName(nickName.getText().toString());

                Toast.makeText(CriarNomeUsuario.this, ""+dados.getNickName(), Toast.LENGTH_SHORT).show();

                dados.criarDados();

                startActivity(telaInicial);
                finish();

            }
        });





    }
}
