package com.example.puissance11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class JeuPuit extends AppCompatActivity {

    //Joueur
    Carte pierre;
    Carte feuille;
    Carte ciseaux;
    Carte puit;
    Carte [] cartes = {pierre,feuille,ciseaux,puit};
    TextView scoreJ1;
    int score1 = 0;
    boolean gagne = false;
    boolean perd = false;
    //Joueur


    //Adversaire
    Carte pierre2;
    Carte feuille2;
    Carte ciseaux2;
    Carte puit2;
    Carte [] cartes2 = {pierre2,feuille2,ciseaux2,puit2};
    TextView scoreJ2;
    int adversaire=0;
    int score2 = 0;
    //Adversaire

    AppCompatButton cancel;
    Bundle extras;
    MediaPlayer mediaPlayer;
    Intent intent;
    int tempsMusique = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //cacher la barre du bas
        cacherBarre();
        //cacher la barre du bas fin

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.menu);
        setContentView(R.layout.activity_jeu_puit);
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
        puit=new Carte(findViewById(R.id.puit));

        pierre2=new Carte(findViewById(R.id.pierre2),true);
        feuille2=new Carte(findViewById(R.id.feuille2));
        ciseaux2=new Carte(findViewById(R.id.ciseaux2));
        puit2=new Carte(findViewById(R.id.puit2));

        scoreJ1=findViewById(R.id.score1);
        scoreJ2=findViewById(R.id.score2);
        scoreJ1.setText(String.valueOf(score1));
        scoreJ2.setText(String.valueOf(score2));

        cancel=findViewById(R.id.cancelJEU);

        initCartes();
        clicCarte(pierre);
        clicCarte(feuille);
        clicCarte(ciseaux);
        clicCarte(puit);

        cancel.setOnClickListener(view -> {
            intent.putExtra("musicKey",tempsMusique);
            setResult(Activity.RESULT_OK,intent);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
            finish();
        });
    }

    public void initCartes(){
        int i;
        pierre.setMatch(ciseaux.getID(),-1,feuille.getID(),puit.getID());
        feuille.setMatch(pierre.getID(),puit.getID(),ciseaux.getID(),-1);
        ciseaux.setMatch(feuille.getID(),-1,pierre.getID(),puit.getID());
        puit.setMatch(pierre.getID(),ciseaux.getID(),feuille.getID(),-1);

        cartes[0] = pierre;
        cartes[1] = feuille;
        cartes[2] = ciseaux;
        cartes[3] = puit;
        cartes2[0] = pierre2;
        cartes2[1] = feuille2;
        cartes2[2] = ciseaux2;
        cartes2[3] = puit2;

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

        adversaire = (int) Math.round(((Math.random() * ((30 - 0) + 0)) + 0)/10) ;
        cartes2[adversaire].getImage().setVisibility(View.VISIBLE);


        if(carte.getGagne() == adversaire+1 || carte.getGagne2() == adversaire+1){
            score1++;

        }else if(carte.getPerd() == adversaire+1 || carte.getPerd2() == adversaire+1){
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

            setResult(Activity.RESULT_OK,intent);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
            finish();
        }
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