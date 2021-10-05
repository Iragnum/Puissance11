package com.example.puissance11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.AuthCredential;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Connexion extends AppCompatActivity {

    EditText editPassword, editMail;
    Button bouton_connexion, bouton_google;

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    private View.OnClickListener bouton_connexion_listener = new View.OnClickListener() {

        public void onClick(View v) {


            String email = editMail.getText().toString();
            String password = editPassword.getText().toString();


            if (TextUtils.isEmpty(email))
            {
                editMail.setError("Il faut renseigner l'adresse e-mail");
                return;
            }
            if (TextUtils.isEmpty(password))
            {
                editPassword.setError("Il faut renseigner le mot de passe");
                return;
            }
            signInEmail(email,password);
        }
    };

    private View.OnClickListener bouton_google_listener = new View.OnClickListener() {

        public void onClick(View v) {

            signInGoogle();
        }
    };

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // End Configure Google Sign In
        mAuth = FirebaseAuth.getInstance();

        editMail = findViewById(R.id.editMail);
        editPassword = findViewById(R.id.editPassword);
        bouton_connexion = findViewById(R.id.buttonConnexion);
        bouton_connexion.setOnClickListener(bouton_connexion_listener);
        bouton_google = findViewById(R.id.buttonGoogle);
        bouton_google.setOnClickListener(bouton_google_listener);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(Connexion.this, "Connecté", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(Connexion.this, "NAZE", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                Log.e("Connexion",e.toString());
                Toast.makeText(Connexion.this, "Erreur - La connexion avec google a échoué.", Toast.LENGTH_SHORT).show();

            }
        }
    }



    private void signInEmail (String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Connexion.this, "Connecté", Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(Connexion.this, "Erreur - Mot de passe ou identifiant incorrect.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    // [START signin]
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]


    private void reload() {

    }

    private void updateUI(FirebaseUser user) { //pour basculer dans la nouvelle activité
        System.out.println(mAuth.getCurrentUser().getEmail());
        System.out.println("test"+user.getUid());
        System.out.println("email "+user.getEmail());

    }

    // Deconnexion : FirebaseAuth.getInstance().signOut(this);
}