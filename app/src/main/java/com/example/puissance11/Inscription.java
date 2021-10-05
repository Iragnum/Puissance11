package com.example.puissance11;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Inscription extends AppCompatActivity {

    AppCompatButton retour;
    Intent intent;
    int tempsMusique = 0;
    Bundle extras;
    MediaPlayer mediaPlayer;

    EditText editNom, editPrenom, editMdp, editEmail, editDate;
    Button bouton_inscrire;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    private View.OnClickListener bouton_inscrire_listener = new View.OnClickListener() {

        public void onClick(View v) {


            String email = editEmail.getText().toString();
            String password = editMdp.getText().toString();


            if (TextUtils.isEmpty(email))
            {
                editEmail.setError("Il faut renseigner l'adresse e-mail");
                return;
            }

            if (TextUtils.isEmpty(password))
            {
                editMdp.setError("Il faut renseigner le mot de passe");
                return;
            }

            if (password.length() < 6)
            {
                editMdp.setError("Il faut mettre au moins 6 caractères");
                return;
            }
            createAccount(email,password);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.menu);
        setContentView(R.layout.activity_inscription);
        intent = new Intent(getApplicationContext(), MainActivity.class);
        extras=getIntent().getExtras();
        //cacher la barre du bas
        cacherBarre();
        //cacher la barre du bas fin
        if (extras != null) {
            tempsMusique = extras.getInt("musicKey");
        }
        mediaPlayer.seekTo(tempsMusique);
        mediaPlayer.start();
        retour=findViewById(R.id.cancel);
        retour.setOnClickListener(view ->  {
            intent.putExtra("connected",false);
            intent.putExtra("musicKey",tempsMusique);
            setResult(Activity.RESULT_OK,intent);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
            finish();
        });



        //init des variables
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        editNom = findViewById(R.id.editNom);
        editPrenom = findViewById(R.id.editPrenom);
        editMdp = findViewById(R.id.editMdp);
        editEmail = findViewById(R.id.editEmail);
        editDate = findViewById(R.id.editDate);
        bouton_inscrire = findViewById(R.id.buttonInscrire);
        bouton_inscrire.setOnClickListener(bouton_inscrire_listener);
        //fin init

    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.rightfromleft, R.anim.leftfromright);
    }

    public void cacherBarre(){
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;


        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {

            getWindow().getDecorView().setSystemUiVisibility(flags);


            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }
    }

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(Inscription.this, "Inscription reussie", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(Inscription.this, "Erreur - Le compte n'a pas été créé", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // [END create_user_with_email]
    }


    private void updateUI(FirebaseUser user) {
       add_database();
        intent.putExtra("connected",true);
        intent.putExtra("musicKey",tempsMusique);
        intent.putExtra("utilisateur",user);
        setResult(Activity.RESULT_OK,intent);
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer=null;
        finish();

    }


    private void add_database()
    {
        Map<String, Object> utilisateur = new HashMap<>();
        utilisateur.put("nom", editNom.getText().toString());
        utilisateur.put("prenom", editPrenom.getText().toString());
        utilisateur.put("email", editEmail.getText().toString());
        //utilisateur.put("sexe", editEmail.getText().toString());
        utilisateur.put("date de naissance", editDate.getText().toString());
        utilisateur.put("score", 0);
        utilisateur.put("rang", 1);

        db.collection("utilisateurs")
                .add(utilisateur)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e("Success","C'est good");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Fail","C'est pas good");
                    }
                });

    }

}
