package com.example.puissance11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class Jeu extends AppCompatActivity {

    //Joueur
    Carte pierre;
    Carte feuille;
    Carte ciseaux;
    Carte [] cartes = {pierre,feuille,ciseaux};
    TextView scoreJ1;
    int score1 = 0;
    boolean gagne = false;
    boolean perd = false;
    //Joueur


    //Adversaire
    Carte pierre2;
    Carte feuille2;
    Carte ciseaux2;
    Carte [] cartes2 = {pierre2,feuille2,ciseaux2};
    TextView scoreJ2;
    int adversaire=0;
    int score2 = 0;
    //Adversaire

    AppCompatButton cancel;
    Bundle extras;
    MediaPlayer mediaPlayer;
    Intent intent;
    int tempsMusique = 0;

    //database
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //cacher la barre du bas
        cacherBarre();
        //cacher la barre du bas fin

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.menu);
        setContentView(R.layout.activity_jeu);
        intent = new Intent(getApplicationContext(), MainActivity.class);
        extras=getIntent().getExtras();
        if (extras != null) {
            tempsMusique = extras.getInt("musicKey");
        }
        mediaPlayer.seekTo(tempsMusique);
        mediaPlayer.start();
        score1=0;
        score2=0;


        pierre=new Carte(findViewById(R.id.pierre),true);
        feuille=new Carte(findViewById(R.id.feuille));
        ciseaux=new Carte(findViewById(R.id.ciseaux));

        pierre2=new Carte(findViewById(R.id.pierre2),true);
        feuille2=new Carte(findViewById(R.id.feuille2));
        ciseaux2=new Carte(findViewById(R.id.ciseaux2));

        scoreJ1=findViewById(R.id.score1);
        scoreJ2=findViewById(R.id.score2);
        scoreJ1.setText(String.valueOf(score1));
        scoreJ2.setText(String.valueOf(score2));

        cancel=findViewById(R.id.cancelJEU);

        initCartes();
        clicCarte(pierre);
        clicCarte(feuille);
        clicCarte(ciseaux);

        cancel.setOnClickListener(view -> {
            intent.putExtra("musicKey",tempsMusique);
            setResult(Activity.RESULT_OK,intent);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
            finish();
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://puissance111-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
    }

    public void initCartes(){
        int i;
        pierre.setMatch(ciseaux.getID(),feuille.getID());
        feuille.setMatch(pierre.getID(),ciseaux.getID());
        ciseaux.setMatch(feuille.getID(),pierre.getID());

        cartes[0] = pierre;
        cartes[1] = feuille;
        cartes[2] = ciseaux;

        cartes2[0] = pierre2;
        cartes2[1] = feuille2;
        cartes2[2] = ciseaux2;

        for(i=0;i<cartes.length;i++){
            clicCarte(cartes[i]);
        }

    }

    public void clicCarte(Carte carte){
        carte.getImage().setOnClickListener(view -> {
            if(!gagne && !perd) {
                Jeu(carte);
            }
        });
    }

    public void Jeu(Carte carte){

        cartes2[adversaire].getImage().setVisibility(View.INVISIBLE);

        adversaire = (int) Math.round(((Math.random() * ((20 - 0) + 0)) + 0)/10) ;

        cartes2[adversaire].getImage().setVisibility(View.VISIBLE);


        if(carte.getGagne() == adversaire+1){
            score1++;

        }else if(carte.getPerd() == adversaire+1){
            score2++;

        }else{

        }

        scoreJ1.setText(String.valueOf(score1));
        scoreJ2.setText(String.valueOf(score2));
        if(score1==3){
            gagne=true;



        }else if (score2 == 3){
            perd = true;
        }

        if(perd || gagne){
            intent.putExtra("gagne",gagne);
            intent.putExtra("musicKey",tempsMusique);
            FirebaseUser user = mAuth.getCurrentUser();
            ScoreDatabase(user);
            setResult(Activity.RESULT_OK,intent);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
            finish();
        }
    }

    void ScoreDatabase (FirebaseUser user)
    {

        mDatabase.child("users").child(user.getUid()).child("score").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    String scoreDB = String.valueOf(task.getResult().getValue());
                    int scoreDBint = Integer.parseInt(scoreDB);
                    if (gagne)
                    {
                        scoreDBint=scoreDBint+3;
                    }
                    else
                    {
                        scoreDBint=scoreDBint-1;
                        if (scoreDBint<0)
                        {
                            scoreDBint=0;
                        }
                    }
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("users/"+user.getUid()+"/score/",scoreDBint);
                    mDatabase.updateChildren(childUpdates);


                }
            }
        });

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

}