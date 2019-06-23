package com.example.renato.cidadedorme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.renato.cidadedorme.dados.DadosUsuario;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class TelaInicial extends AppCompatActivity {

    //Implementações e variaveis
    //==============================================================================================
    Button sair_tudo;

    //Google
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

        //Adicionar os dados
        final DadosUsuario dados = new DadosUsuario();

        Toast.makeText(this, ""+dados.getUid(), Toast.LENGTH_SHORT).show();

        //Botão de sair dar 2 redes sociais Facebook e Google
        sair_tudo = (Button) findViewById(R.id.sair_tudo);
        sair_tudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(dados.getTipoConexao()==1){
                    facebookLogout();

                } else if(dados.getTipoConexao()==2){
                    doGoogleSignOut();
                }
            }
        });

    }

    //Desconecta o app do Facebook e do firebase que estava na conta atual
    //==============================================================================================
    private void facebookLogout(){

        //Desconção do Facebook
        //LoginManager.getInstance().logOut();
        //AccessToken.setCurrentAccessToken(null);

        //Desconeção do firebase
        FirebaseAuth.getInstance().signOut();
        //Toast.makeText(this, "Desconectado", Toast.LENGTH_SHORT).show();

        //Desconecta de vez do facebook
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                AccessToken.setCurrentAccessToken(null);
                LoginManager.getInstance().logOut();
                finish();

            }
        }).executeAsync();

    }
    //----------------------------------------------------------------------------------------------

    private void doGoogleSignOut() {

        //desconecta do firebase
        FirebaseAuth.getInstance().signOut();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()//request email id
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(TelaInicial.this, "Desconectado", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                });
    }
}
