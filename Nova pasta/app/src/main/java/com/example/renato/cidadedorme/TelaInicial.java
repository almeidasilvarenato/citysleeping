package com.example.renato.cidadedorme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class TelaInicial extends AppCompatActivity {

    //Implementações e variaveis
    //==============================================================================================
    Button sair;
    Button sair1;

    //Google
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

        sair = (Button) findViewById(R.id.sair);

        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                facebookLogout();

            }
        });

        sair1 = (Button) findViewById(R.id.sair1);
        sair1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doGoogleSignOut();
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
