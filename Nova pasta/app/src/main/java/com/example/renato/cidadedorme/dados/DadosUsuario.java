package com.example.renato.cidadedorme.dados;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.renato.cidadedorme.R;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DadosUsuario {

    //Variaveis e get and seters
    //==============================================================================================

    //Mostra por onde o usuario se conectou 1=facebook, 2=google, 3=normal
    private static int tipoConexao;
    //Verifica se o usuario já tinha uma conta criada
    private static boolean conta;
    //dados do usuario
    private static String Uid;
    private static String email;
    private static String nickName;
    private static String nome;
    private static String urlFoto;

    public static int getTipoConexao() {
        return tipoConexao;
    }

    public static void setTipoConexao(int tipoConexao) {
        DadosUsuario.tipoConexao = tipoConexao;
    }

    public static boolean getConta() {
        return conta;
    }

    public static void setConta(boolean conta) {
        DadosUsuario.conta = conta;
    }

    public static String getUid() {
        return Uid;
    }

    public static void setUid(String uid) {
        Uid = uid;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        DadosUsuario.email = email;
    }

    public static String getNickName() {
        return nickName;
    }

    public static void setNickName(String nickName) {
        DadosUsuario.nickName = nickName;
    }

    public static String getNome() {
        return nome;
    }

    public static void setNome(String nome) {
        DadosUsuario.nome = nome;
    }

    public static String getUrlFoto() {
        return urlFoto;
    }

    public static void setUrlFoto(String urlFoto) {
        DadosUsuario.urlFoto = urlFoto;
    }
    //----------------------------------------------------------------------------------------------

    private DatabaseReference mDatabase;

    public void verificarExistencia(){

        //Implementação do firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setConta(dataSnapshot.child("JOGADORES").child("ID").child(getUid()).exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void criarDados(){

        //Implementação do firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Adiciona o ID
        mDatabase.child("JOGADORES").child("ID").child(getUid()).setValue(getNickName());

        //Adiciona os dados
        mDatabase.child("JOGADORES").child("NICKNAME").child(getNickName()).child("NICKNAME").setValue(getNickName());
        mDatabase.child("JOGADORES").child("NICKNAME").child(getNickName()).child("NOME").setValue(getNome());
        mDatabase.child("JOGADORES").child("NICKNAME").child(getNickName()).child("EMAIL").setValue(getEmail());
        mDatabase.child("JOGADORES").child("NICKNAME").child(getNickName()).child("FOTO").setValue(getUrlFoto());
        mDatabase.child("JOGADORES").child("NICKNAME").child(getNickName()).child("UID").setValue(getUid());


        setConta(true);
    }

}
