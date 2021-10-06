package com.example.puissance11;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    AppCompatButton jouer;
    AppCompatButton connexion;
    AppCompatButton inscription;
    AppCompatButton deconnexion;
    MediaPlayer mediaPlayer;
    TextView user_connecte;
    boolean connected;
    boolean gagne = false;
    int jeu = 0;
    boolean musicNotReleased = false;
    String username;
    String mdp;
    int tempsMusique =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cacherBarre();

        user_connecte= findViewById(R.id.textViewConnexion);
        jouer = findViewById(R.id.jouer);
        connexion = findViewById(R.id.connexion);
        inscription = findViewById(R.id.cancel);
        deconnexion = findViewById(R.id.deconnexion);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user_connecte.setText(user.getEmail());
            connected =true;
        } else {
            user_connecte.setText("Non connecté");
            connected =false;
        }
        deconnexion.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(MainActivity.this, "Déconnecté", Toast.LENGTH_SHORT).show();
            onResume();
        });

        connexion.setOnClickListener(view -> {

            Intent retour3 = new Intent(this, Connexion.class);
            retour3.putExtra("musicKey",tempsMusique);
            startActivityForResult(retour3,3);
            overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft);

        });


        jouer.setOnClickListener(view -> {
            if(jeu==0) {
                Intent retour2 = new Intent(this, Jeu.class);
                retour2.putExtra("musicKey", tempsMusique);
                startActivityForResult(retour2, 2);
                overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft);
            }else{
                Intent retour2 = new Intent(this, JeuPuit.class);
                retour2.putExtra("musicKey", tempsMusique);
                startActivityForResult(retour2, 2);
                overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft);
            }
        });

        inscription.setOnClickListener(view ->  {
            Intent retour = new Intent(this, Inscription.class);
            retour.putExtra("musicKey",tempsMusique);
            startActivityForResult(retour,1);
            overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1) {
            connected = data.getBooleanExtra("connected", true);
            tempsMusique = data.getIntExtra("musicKey", 0);
        }else if(requestCode==2){
            gagne = data.getBooleanExtra("gagne", false);
            tempsMusique = data.getIntExtra("musicKey", 0);
        }
        //jouer.setEnabled(connected);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        connected = savedInstanceState.getBoolean("connectedKey", false);
        username = savedInstanceState.getString("usernameKey", null);
        mdp = savedInstanceState.getString("mdpKey", null);
        tempsMusique = savedInstanceState.getInt("musicKey",0);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("connectedKey", connected);
        savedInstanceState.putString("usernameKey", username);
        savedInstanceState.putString("mdpKey", mdp);
        savedInstanceState.putInt("musicKey", tempsMusique);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && musicNotReleased) {
            if(mediaPlayer.isPlaying()) {
                tempsMusique = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
                mediaPlayer.release();
                musicNotReleased=false;
                mediaPlayer=null;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null && musicNotReleased) {
            if(mediaPlayer.isPlaying()) {
                tempsMusique = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
                mediaPlayer.release();
                musicNotReleased=false;
                mediaPlayer=null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        jouer.setEnabled(connected);
        inscription.setEnabled(!connected);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user_connecte.setText(user.getEmail());
            connected =true;
        } else {
            user_connecte.setText("Non connecté");
            connected =false;
        }
        if(!musicNotReleased) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.menu);
            musicNotReleased = true;
        }
        if (mediaPlayer != null && musicNotReleased) {
            if(!mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo(tempsMusique);
                mediaPlayer.start();
            }
        }

        if(connected) {
            connexion.setVisibility(View.INVISIBLE);
            deconnexion.setVisibility(View.VISIBLE);
        }else{
            connexion.setVisibility(View.VISIBLE);
            deconnexion.setVisibility(View.INVISIBLE);
        }
        inscription.setEnabled(!connected);
        if(gagne){
            jouer.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_button_clicked_green));
            jeu=1;
        }else{
            jouer.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_button_clicked));
            jeu=0;
        }
        gagne=false;
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




