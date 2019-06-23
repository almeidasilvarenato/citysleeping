package com.example.renato.cidadedorme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renato.cidadedorme.dados.DadosUsuario;
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

    //Ferramentas para animação
    Animation animFadeOut, animMoveDown;

    ImageView img_cidade;
    TextView txt_cidade;
    Button login_entrar;
    EditText userName;
    EditText userKey;
    TextView textView;
    TextView textView2;
    ConstraintLayout linha;


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
    private Intent criarNickName;

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

        //Faz a animação
        //==========================================================================================
        login_entrar = (Button) findViewById(R.id.login_entrar);
        login_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fadeOut();
                //moveDown();
            }
        });


        //------------------------------------------------------------------------------------------
    }

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

        }
    }

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

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        //Toast.makeText(InicialLoginActivity.this, "Cancelado!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        //Toast.makeText(InicialLoginActivity.this, "Erro"+ exception, Toast.LENGTH_SHORT).show();
                    }
                });


    };

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

        DadosUsuario dados = new DadosUsuario();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(isLoggedIn == true){

            dados.setTipoConexao(1);
            dados();

            //Verifica se já ah conta
            dados.verificarExistencia();
            if(dados.getConta()==false){

                criarNickName = new Intent(InicialLoginActivity.this, CriarNomeUsuario.class);
                startActivity(criarNickName);
                finish();

            } else {

                //Aqui importa e inicial a tela inicial do jogo logo após o usuario estar conectado
                telaInicial = new Intent(InicialLoginActivity.this, TelaInicial.class);
                startActivity(telaInicial);
                finish();
            }

        }
    }

    //----------------------------------------------------------------------------------------------


    //Conexao do Facebook e Google ao firebase
    //==============================================================================================

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

                            //Aqui envia os dados para o banco ou pega os dados de lá
                            DadosUsuario dados = new DadosUsuario();
                            dados.setTipoConexao(1);

                            for (UserInfo profile : user.getProviderData()) {
                                // Id of the provider (ex: google.com)

                                dados.setUid(profile.getUid());

                                // Name, email address, and profile photo Url
                                dados.setNome(profile.getDisplayName());
                                dados.setEmail(profile.getEmail());
                                dados.setUrlFoto(profile.getPhotoUrl().toString());

                            }

                            //Verifica se já ah conta
                            dados.verificarExistencia();
                            if(dados.getConta()==false){

                                criarNickName = new Intent(InicialLoginActivity.this, CriarNomeUsuario.class);
                                startActivity(criarNickName);
                                finish();

                            } else {

                                //Aqui importa e inicial a tela inicial do jogo logo após o usuario estar conectado
                                telaInicial = new Intent(InicialLoginActivity.this, TelaInicial.class);
                                startActivity(telaInicial);
                                finish();
                            }

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

    public void dados(){

        //Esse metodo pega os dados direto do firebase
        //==========================================================================================

        DadosUsuario dados = new DadosUsuario();

        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)

                dados.setUid(profile.getUid());

                // Name, email address, and profile photo Url
                dados.setNome(profile.getDisplayName());
                dados.setEmail(profile.getEmail());
                dados.setUrlFoto(profile.getPhotoUrl().toString());

            }
        }
    }

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
            //Toast.makeText(this, "Google Sign In Successful.", Toast.LENGTH_SHORT).show();




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

    //Aqui é responsavel por se pegar os dados do usuario no Google
    private void getProfileInformation(GoogleSignInAccount acct) {
        //if account is not null fetch the information
        if (acct != null) {

            //Adiciona os dados a classe dados
            DadosUsuario dados = new DadosUsuario();
            dados.setTipoConexao(2);
            dados.setUid(acct.getId());
            dados.setEmail(acct.getEmail());
            dados.setNome(acct.getDisplayName());
            dados.setUrlFoto(acct.getPhotoUrl().toString());
            //Toast.makeText(this, ""+dados.getUrlFoto(), Toast.LENGTH_SHORT).show();

            //Verifica se já ah conta
            dados.verificarExistencia();
            if(dados.getConta()==false){

                criarNickName = new Intent(InicialLoginActivity.this, CriarNomeUsuario.class);
                startActivity(criarNickName);
                finish();

            } else {

                //Aqui importa e inicial a tela inicial do jogo logo após o usuario estar conectado
                telaInicial = new Intent(InicialLoginActivity.this, TelaInicial.class);
                startActivity(telaInicial);
                finish();
            }

            //user display name
            //String personName = acct.getDisplayName();
            //user first name
            //String personGivenName = acct.getGivenName();
            //user last name
            //String personFamilyName = acct.getFamilyName();
            //user email id
            //String personEmail = acct.getEmail();
            //user unique id
            //String personId = acct.getId();
            //user profile pic
            //Uri personPhoto = acct.getPhotoUrl();

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

    //Animação
    //==============================================================================================

    public void fadeOut(){

        //Implementações
        txt_cidade = (TextView) findViewById(R.id.txt_cidade);
        facebookButton = (Button) findViewById(R.id.facebookButton);
        gmailButton = (Button) findViewById(R.id.gmailButton);
        userName = (EditText) findViewById(R.id.userName);
        userKey = (EditText) findViewById(R.id.userKey);
        textView = (TextView) findViewById(R.id.textView);
        linha = (ConstraintLayout) findViewById(R.id.linha);
        textView2 = (TextView) findViewById(R.id.textView2);

        //Implementação da animação
        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);

        //Usar a animação
        txt_cidade.startAnimation(animFadeOut);
        facebookButton.startAnimation(animFadeOut);
        gmailButton.startAnimation(animFadeOut);
        login_entrar.startAnimation(animFadeOut);
        userName.startAnimation(animFadeOut);
        userKey.startAnimation(animFadeOut);
        textView.startAnimation(animFadeOut);
        linha.startAnimation(animFadeOut);
        textView2.startAnimation(animFadeOut);

        //Continuar invisivel
        txt_cidade.setVisibility(View.INVISIBLE);
        facebookButton.setVisibility(View.INVISIBLE);
        gmailButton.setVisibility(View.INVISIBLE);
        login_entrar.setVisibility(View.INVISIBLE);
        userName.setVisibility(View.INVISIBLE);
        userKey.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
        linha.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);

    }

    public void moveDown (){
        animMoveDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.move_down);

        img_cidade = (ImageView) findViewById(R.id.img_cidade);
        img_cidade.startAnimation(animMoveDown);


    }

    }


