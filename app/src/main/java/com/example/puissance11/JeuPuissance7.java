package com.example.puissance11;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class JeuPuissance7 extends AppCompatActivity {

    //Joueur
    Carte pierre;
    Carte feuille;
    Carte ciseaux;
    Carte eponge;
    Carte eau;
    Carte feu;
    Carte air;
    Carte [] cartes = {pierre,feuille,ciseaux,eponge,eau,feu,air};
    TextView scoreJ1;
    TextView mancheJ1;
    int score1 = 0;
    int manche1 = 0;
    int nbManches=0;
    //Joueur


    //Adversaire
    Carte pierre2;
    Carte feuille2;
    Carte ciseaux2;
    Carte eponge2;
    Carte eau2;
    Carte feu2;
    Carte air2;
    Carte [] cartes2 = {pierre2,feuille2,ciseaux2,eponge2,eau2,feu2,air2};
    TextView scoreJ2;
    TextView mancheJ2;
    int adversaire = 0;
    int score2 = 0;
    int manche2 = 0;
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
        setContentView(R.layout.activity_jeu_puissance7);
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
        eponge=new Carte(findViewById(R.id.eponge));
        eau=new Carte(findViewById(R.id.eau));
        feu=new Carte(findViewById(R.id.feu));
        air=new Carte(findViewById(R.id.air));


        pierre2=new Carte(findViewById(R.id.pierre2),true);
        feuille2=new Carte(findViewById(R.id.feuille2));
        ciseaux2=new Carte(findViewById(R.id.ciseaux2));
        eponge2=new Carte(findViewById(R.id.eponge2));
        eau2=new Carte(findViewById(R.id.eau2));
        feu2=new Carte(findViewById(R.id.feu2));
        air2=new Carte(findViewById(R.id.air2));



        scoreJ1=findViewById(R.id.score1);
        scoreJ2=findViewById(R.id.score2);
        scoreJ1.setText(String.valueOf(score1));
        scoreJ2.setText(String.valueOf(score2));

        mancheJ1=findViewById(R.id.scoreManche);
        mancheJ2=findViewById(R.id.scoreManche3);
        mancheJ1.setText(String.valueOf(manche1));
        mancheJ2.setText(String.valueOf(manche2));


        cancel=findViewById(R.id.cancelJEU);

        initCartes();
        clicCarte(pierre);
        clicCarte(feuille);
        clicCarte(ciseaux);
        clicCarte(eponge);
        clicCarte(eau);
        clicCarte(feu);
        clicCarte(air);

        cancel.setOnClickListener(view -> {
            intent.putExtra("musicKey",tempsMusique);
            intent.putExtra("gagne",aGagne());
            FirebaseUser user = mAuth.getCurrentUser();
            ScoreDatabase(user);
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
        pierre.setMatch(ciseaux.getID(),feu.getID(),eponge.getID(),feuille.getID(),eau.getID(),air.getID());
        feuille.setMatch(pierre.getID(),air.getID(),eau.getID(),ciseaux.getID(),feu.getID(),eponge.getID());
        ciseaux.setMatch(feuille.getID(),eponge.getID(),air.getID(),pierre.getID(),eau.getID(),feu.getID());
        feu.setMatch(feuille.getID(),eponge.getID(),ciseaux.getID(),eau.getID(),air.getID(),pierre.getID());
        eau.setMatch(feu.getID(),pierre.getID(),ciseaux.getID(),eponge.getID(),feuille.getID(),air.getID());
        air.setMatch(eau.getID(),feu.getID(),pierre.getID(),ciseaux.getID(),eponge.getID(),feuille.getID());
        eponge.setMatch(feuille.getID(),eau.getID(),air.getID(),feu.getID(),ciseaux.getID(),pierre.getID());


        cartes[0] = pierre;
        cartes[1] = feuille;
        cartes[2] = ciseaux;
        cartes[3] = eponge;
        cartes[4]= eau;
        cartes[5]= feu;
        cartes[6]= air;

        cartes2[0] = pierre2;
        cartes2[1] = feuille2;
        cartes2[2] = ciseaux2;
        cartes2[3] = eponge2;
        cartes2[4]= eau2;
        cartes2[5]= feu2;
        cartes2[6]= air2;

        for(i=0;i<cartes.length;i++){
            clicCarte(cartes[i]);
        }

    }

    public void clicCarte(Carte carte){
        carte.getImage().setOnClickListener(view -> {
            Jeu(carte);
        });
    }

    public void Jeu(Carte carte){

        cartes2[adversaire].getImage().setVisibility(View.INVISIBLE);

        adversaire = (int) Math.round(((Math.random() * ((60 - 0) + 0)) + 0)/10) ;
        cartes2[adversaire].getImage().setVisibility(View.VISIBLE);


        if(carte.getGagne() == adversaire+1 || carte.getGagne2() == adversaire+1 || carte.getGagne3() == adversaire+1){
            score1++;
            nbManches++;

        }else if(carte.getPerd() == adversaire+1 || carte.getPerd2() == adversaire+1 || carte.getPerd3() == adversaire+1){
            score2++;
            nbManches++;

        }else{
            nbManches++;
        }

        if(score1==3){
            manche1++;
            score1=0;
            score2=0;
        }else if (score2 == 3){
            manche2++;
            score1=0;
            score2=0;
        }
        else if(nbManches == 5){
            if(score1>score2){
                manche1++;
                score1=0;
                score2=0;
            }else if(score2>score1){
                manche2++;
                score1=0;
                score2=0;
            }else{
                score1=0;
                score2=0;
            }
        }

        scoreJ1.setText(String.valueOf(score1));
        scoreJ2.setText(String.valueOf(score2));

        mancheJ1.setText(String.valueOf(manche1));
        mancheJ2.setText(String.valueOf(manche2));
    }

    public void cacherBarre(){
        int currentApiVersion = Build.VERSION.SDK_INT;

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
                    if (aGagne() == 2)
                    {
                        scoreDBint=scoreDBint+8;
                    }
                    else if (aGagne() == 1)
                    {
                        scoreDBint=scoreDBint-3;
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

    public int aGagne(){
        if(manche1 > manche2){
            return 2;
        }else if(manche2 > manche1){
            return 1;
        }else{
            return 0;
        }
    }


}