package com.example.puissance11;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.Query;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    AppCompatButton jouer;
    AppCompatButton connexion;
    AppCompatButton inscription;
    AppCompatButton deconnexion;
    MediaPlayer mediaPlayer;
    TextView user_connecte, score, rang;
    Button Regle;
    boolean connected;
    boolean gagne = false;
    boolean musicNotReleased = false;
    String username;
    String mdp;
    int tempsMusique =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cacherBarre();
        mDatabase = FirebaseDatabase.getInstance("https://puissance111-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");
        user_connecte = findViewById(R.id.textViewConnexion);
        score = findViewById(R.id.textViewScore);
        rang = findViewById(R.id.textViewRang);
        jouer = findViewById(R.id.jouer);
        connexion = findViewById(R.id.connexion);
        inscription = findViewById(R.id.cancel);
        deconnexion = findViewById(R.id.deconnexion);
        Regle = findViewById(R.id.buttonQuestion);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user_connecte.setText(user.getEmail());
            connected = true;

        } else {
            user_connecte.setText("Non connecté");
            score.setText("");
            rang.setText("");
            connected = false;
        }
        deconnexion.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(MainActivity.this, "Déconnecté", Toast.LENGTH_SHORT).show();
            onResume();
        });

        Regle.setOnClickListener(view -> {
            Intent retour3 = new Intent(this, Regles.class);

            retour3.putExtra("musicKey", tempsMusique);

            startActivity(retour3);

            overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft);
        });

        connexion.setOnClickListener(view -> {

            Intent retour3 = new Intent(this, Connexion.class);
            retour3.putExtra("musicKey",tempsMusique);
            startActivityForResult(retour3,3);
            overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft);

        });


        jouer.setOnClickListener(view -> {
            Intent retour2 = new Intent(this, MenuJeu.class);
            retour2.putExtra("musicKey", tempsMusique);
            startActivity(retour2);
            overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft);

        });

        inscription.setOnClickListener(view ->  {
            Intent retour = new Intent(this, Inscription.class);
            retour.putExtra("musicKey",tempsMusique);
            startActivity(retour);
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user_connecte.setText(user.getEmail());
            Query myTopPostsQuery = mDatabase.orderByChild("score");
            myTopPostsQuery.addChildEventListener(new ChildEventListener() {
            long classement =0;

                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    long Nombre_total = snapshot.getChildrenCount ();
                    if (classement==0) {
                        classement = Nombre_total+1;
                    }

                    System.out.println("The " + snapshot.getKey() + " score is " + snapshot.getValue()+ "classement = "+classement);

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(snapshot.getKey()+"/rang/",classement);
                    mDatabase.updateChildren(childUpdates);
                    classement--;

                        }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });


            readRang(new MyCallback2() {
                @Override
                public void onCallback(Long value) {
                    rang.setText("Rang : " + value);
                }
            });

            readScore(new MyCallback() {
                @Override
                public void onCallback(Long value) {
                    score.setText("Score : " + value);
                }
            });

            connected =true;

        } else {
            user_connecte.setText("Non connecté");
            score.setText("");
            rang.setText("");
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

        jouer.setEnabled(connected);
        inscription.setEnabled(!connected);
        if(connected) {
            connexion.setVisibility(View.INVISIBLE);
            deconnexion.setVisibility(View.VISIBLE);
        }else{
            connexion.setVisibility(View.VISIBLE);
            deconnexion.setVisibility(View.INVISIBLE);
        }
        inscription.setEnabled(!connected);
        gagne=false;
    }


    public interface MyCallback {
        void onCallback(Long value);
    }
    public interface MyCallback2 {
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

    public void readRang(MyCallback2 myCallback2) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child(user.getUid()).child("rang").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long value = dataSnapshot.getValue(Long.class);
                myCallback2.onCallback(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
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




