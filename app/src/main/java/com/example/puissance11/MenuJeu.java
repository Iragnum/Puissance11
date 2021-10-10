package com.example.puissance11;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MenuJeu extends AppCompatActivity {

    private DatabaseReference mDatabase;
    AppCompatButton jouer;
    AppCompatButton jouerPuit;
    AppCompatButton jouerPuissance;
    AppCompatButton cancel;
    MediaPlayer mediaPlayer;
    TextView user_connecte, score, rang;
    boolean musicNotReleased = false;
    String username;
    String mdp;
    int tempsMusique =0;
    Intent intent;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choixjeu);
        cacherBarre();

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.menu);
        musicNotReleased=true;

        intent = new Intent(getApplicationContext(), MainActivity.class);
        extras=getIntent().getExtras();

        if (extras != null) {
            tempsMusique = extras.getInt("musicKey");
        }
        mediaPlayer.seekTo(tempsMusique);
        mediaPlayer.start();

        mDatabase = FirebaseDatabase.getInstance("https://puissance111-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");
        user_connecte = findViewById(R.id.textViewConnexion1);
        score= findViewById(R.id.textViewScore1);
        rang= findViewById(R.id.textViewRang1);
        jouer = findViewById(R.id.jouer1);
        jouerPuit = findViewById(R.id.jouer2);
        jouerPuissance= findViewById(R.id.jouer3);
        cancel=findViewById(R.id.cancel2);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user_connecte.setText(user.getEmail());
        } else {
            user_connecte.setText("Non connecté");
            score.setText("");
            rang.setText("");
        }
        jouerPuit.setOnClickListener(view -> {
            Intent retour2 = new Intent(this, JeuPuit.class);
            retour2.putExtra("musicKey", tempsMusique);
            startActivity(retour2);
            overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft);
        });

        jouerPuissance.setOnClickListener(view -> {
            Intent retour2 = new Intent(this, JeuPuissance7.class);
            retour2.putExtra("musicKey", tempsMusique);
            startActivity(retour2);
            overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft);
        });

        jouer.setOnClickListener(view -> {

                Intent retour2 = new Intent(this, Jeu.class);
                retour2.putExtra("musicKey", tempsMusique);
                startActivity(retour2);
                overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft);

        });

        cancel.setOnClickListener(view ->  {
            intent.putExtra("musicKey",tempsMusique);
            setResult(Activity.RESULT_OK,intent);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
            finish();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        tempsMusique = data.getIntExtra("musicKey", 0);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        username = savedInstanceState.getString("usernameKey", null);
        mdp = savedInstanceState.getString("mdpKey", null);
        tempsMusique = savedInstanceState.getInt("musicKey",0);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user_connecte.setText(user.getEmail());
            readScore(new MyCallback() {
                @Override
                public void onCallback(Long value) {
                    score.setText("Score : " + value);
                }
            });
            readRang(new MyCallback() {
                @Override
                public void onCallback(Long value) {
                    rang.setText("Rang : " + value);
                }
            });

        } else {
            user_connecte.setText("Non connecté");
            score.setText("");
            rang.setText("");
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

        jouer.setEnabled(true);

        readScore(new MyCallback() {
            @Override
            public void onCallback(Long value) {
                if(value>=10){
                    jouerPuit.setEnabled(true);
                    if(value>=20){
                        jouerPuissance.setEnabled(true);
                    }else{
                        jouerPuissance.setEnabled(false);
                    }
                }else{
                    jouerPuit.setEnabled(false);
                    jouerPuissance.setEnabled(false);
                }
            }
        });

    }


    public interface MyCallback {
        void onCallback(Long value);
    }

    public void readScore(MyCallback myCallback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child(user.getUid()).child("score").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long value = dataSnapshot.getValue(Long.class);
                myCallback.onCallback(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void readRang(MyCallback myCallback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child(user.getUid()).child("rang").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long value = dataSnapshot.getValue(Long.class);
                myCallback.onCallback(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
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


}




