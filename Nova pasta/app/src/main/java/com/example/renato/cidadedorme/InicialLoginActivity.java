package com.example.renato.cidadedorme;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.renato.cidadedorme.dados.LoginFacebook;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InicialLoginActivity extends AppCompatActivity {
    private static final String TAG = "";

    //Importações
    //==============================================================================================

    //Facebook
    private CallbackManager callbackManager;
    private LoginButton loginButtonFacebook;
    private Button facebookButton;

    //Google
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 111 ;
    private Button gmailButton;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase;

    //Intents
    private Intent telaInicial;

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial_login);

        //Inicio do codigo
        //==========================================================================================

        //Adiciona os dados ao firebase
        mAuth = FirebaseAuth.getInstance();

        //Inicia a conexão com o facebook
        //==========================================================================================
        facebookStatus();
        facebookConect();
        loginBd();
        //------------------------------------------------------------------------------------------

        //Inicia a conexão com o Google
        //==========================================================================================

        gmailButton = (Button) findViewById ( R.id.gmailButton );
        configureGoogleSignIn();
        gmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doGoogleSignIn();
            }
        });

        //------------------------------------------------------------------------------------------

    }


    //Conexão pelo o Facebook
    //==============================================================================================

    public void facebookConect(){

        //Aqui troca o botão de login do facebook pelo q eu criei
        //Ele aciona o click do botão invisivel
        facebookButton = (Button) findViewById(R.id.facebookButton);
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButtonFacebook.callOnClick();

            }
        });

        //Inicio da conexão com o facebook
        callbackManager = CallbackManager.Factory.create();

        loginButtonFacebook = (LoginButton) findViewById(R.id.login_buttonFacebook);
        loginButtonFacebook.setReadPermissions("email");

        // Callback registration
        loginButtonFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        //Retorno da conexão do facebook

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        //Toast.makeText(InicialLoginActivity.this, "Conectado ao facebook!", Toast.LENGTH_SHORT).show();

                        //Aqui se chama o metodo que conecta o facebook ao firebase
                        handleFacebookAccessToken(loginResult.getAccessToken());
                        loginBd();

                        //Aqui importa e inicial a tela inicial do jogo logo após o usuario estar conectado
                        telaInicial = new Intent(InicialLoginActivity.this, TelaInicial.class);
                        startActivity(telaInicial);
                        finish();

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Toast.makeText(InicialLoginActivity.this, "Cancelado!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(InicialLoginActivity.this, "Erro"+ exception, Toast.LENGTH_SHORT).show();
                    }
                });


    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            //method to handle google sign in result
            handleSignInResult(task);

            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                //Toast.makeText(this, ""+task, Toast.LENGTH_SHORT).show();



            } catch (ApiException e){
                Toast.makeText(this, "Iii falha"+ e, Toast.LENGTH_SHORT).show();

            }


            //google
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account);
//                Toast.makeText(this, ""+account, Toast.LENGTH_SHORT).show();
//            } catch (ApiException e) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
//                // ...
//            }

        }
    }

    //Fim da conexão pelo facebook
    //----------------------------------------------------------------------------------------------


    //Pega a chave hash para a conexão do app ao Facebook
    //==============================================================================================

    /*try {
        PackageInfo info = getPackageManager().getPackageInfo(
                getPackageName(),
                PackageManager.GET_SIGNATURES);
        for (Signature signature : info.signatures) {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(signature.toByteArray());
            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
        }
    }
        catch (PackageManager.NameNotFoundException e) {

    }
        catch (NoSuchAlgorithmException e) {

    }*/

    //Fim
    //----------------------------------------------------------------------------------------------


    //Verifica o Status de conexão com o Facebook e mantem o app conectado
    //==============================================================================================

    public void facebookStatus(){

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(isLoggedIn == true){

            //Aqui importa e inicial a tela inicial do jogo logo após o usuario estar conectado
            telaInicial = new Intent(InicialLoginActivity.this, TelaInicial.class);
            startActivity(telaInicial);
            finish();

        }
        //Toast.makeText(this, ""+isLoggedIn, Toast.LENGTH_SHORT).show();
        //Maneira diferente para se adicionar um botão.
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

    }

    //----------------------------------------------------------------------------------------------



    //Conexao do Facebook ao firebase
    //==============================================================================================

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        // google
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //update the UI if user has already sign in with the google for this app
        getProfileInformation(account);

        //-------
    }

    private void updateUI(FirebaseUser currentUser) {
    }

    private void handleFacebookAccessToken(AccessToken token) {
        //Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(InicialLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    //Fim da conexão do Facebook ao firebase
    //----------------------------------------------------------------------------------------------

    //Pegar Dados do facebook ou outra rede exemplo
    //==============================================================================================
    /*
    public void pegaDados(){

        //Esse metodo pega os dados direto do firebase
        //==========================================================================================

        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                uid = profile.getUid();

                // Name, email address, and profile photo Url
                String name = profile.getDisplayName();
                String email = profile.getEmail();
                Uri photoUrl = profile.getPhotoUrl();

            }
        }
        */
        //------------------------------------------------------------------------------------------

        //Forma de pegar a foto do usuario do facebook
        //==========================================================================================

        /*
        Large https://graph.facebook.com/{facebookId}/picture?type=large

        Medium https://graph.facebook.com/{facebookId}/picture?type=normal

        Small https://graph.facebook.com/{facebookId}/picture?type=small

        Square https://graph.facebook.com/{facebookId}/picture?type=square

        Exemplo: http://graph.facebook.com/1675697252718027/picture?type=large

        */

        //Meu teste
        //String url2 = "https://graph.facebook.com/"+uid+"/picture?type=large";


        //Metodo utilizando o picasso para imprimir a foto para o usuario
        //==========================================================================================

        /*
        image = (ImageView) findViewById(R.id.image);
        Picasso.get().load(url2).into(image);
        */

        //------------------------------------------------------------------------------------------


    //Aqui será responsavel por armazenar os dados do usuario no banco de dados e no banco de documentos
    //==============================================================================================
    private void loginBd(){

        final LoginFacebook loginFacebook = new LoginFacebook();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {

                loginFacebook.setEmail(profile.getEmail());
                loginFacebook.setUid(profile.getUid());

            }

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                loginFacebook.setConta(dataSnapshot.child("USUARIO").child("ID").child(loginFacebook.getUid()).exists());
                //Toast.makeText(InicialLoginActivity.this, ""+loginFacebook.getConta(), Toast.LENGTH_SHORT).show();

                if(loginFacebook.getConta()==false)
                {
                    mDatabase.child("USUARIO").child("ID").child(LoginFacebook.getUid()).child("EMAIL").setValue(loginFacebook.getEmail());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
        }

    //----------------------------------------------------------------------------------------------

    //Inicio da conexão com o Google
    //==============================================================================================

    private void configureGoogleSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()//request email id
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void doGoogleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);//pass the declared request code here
    }
    

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            getProfileInformation(account);

            //show toast
            Toast.makeText(this, "Google Sign In Successful.", Toast.LENGTH_SHORT).show();


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());

            //show toast
            Toast.makeText(this, "Failed to do Sign In : " + e.getStatusCode(), Toast.LENGTH_SHORT).show();

            //update Ui for this
            getProfileInformation(null);
        }
    }
    private void getProfileInformation(GoogleSignInAccount acct) {
        //if account is not null fetch the information
        if (acct != null) {

            telaInicial = new Intent(InicialLoginActivity.this, TelaInicial.class);
            startActivity(telaInicial);
            finish();

            //user display name
            String personName = acct.getDisplayName();

            //user first name
            String personGivenName = acct.getGivenName();

            //user last name
            String personFamilyName = acct.getFamilyName();

            //user email id
            String personEmail = acct.getEmail();

            //user unique id
            String personId = acct.getId();

            //user profile pic
            Uri personPhoto = acct.getPhotoUrl();

            //show the user details
//            userDetailLabel.setText("ID : " + personId + "\nDisplay Name : " + personName + "\nFull Name : " + personGivenName + " " + personFamilyName + "\nEmail : " + personEmail);
//
//            //show the user profile pic
//            Picasso.with(this).load(personPhoto).fit().placeholder(R.mipmap.ic_launcher_round).into(userProfileImageView);
//

        }
    }
        //google
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //Toast.makeText(this, ""+acct.getIdToken(), Toast.LENGTH_SHORT).show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    //----------------------------------------------------------------------------------------------

    }


