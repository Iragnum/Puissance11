package com.example.puissance11;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Regles extends AppCompatActivity {

    Intent intent;
    Button retour;
    Bundle extras;
    int tempsMusique = 0;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cacherBarre();
        setContentView(R.layout.activity_regles);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.menu);
        intent = new Intent(getApplicationContext(), MainActivity.class);
        extras=getIntent().getExtras();
        if (extras != null) {
            tempsMusique = extras.getInt("musicKey");
        }
        mediaPlayer.seekTo(tempsMusique);
        mediaPlayer.start();


        retour=findViewById(R.id.buttonRetour);
        retour.setOnClickListener(view ->  {
            intent.putExtra("musicKey",tempsMusique);
            setResult(Activity.RESULT_OK,intent);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
            finish();
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



/*
\n




  Query personsQuery = personsRef.orderByChild("score");
recAdapterRank = new FirebaseRecyclerAdapter<Word, RankingViewHolder>(personsOptions) {
    @Override
    protected void onBindViewHolder(RankingActivity.RankingViewHolder holder, final int position, final Word model) {
        holder.setWord(model.getWord());
        long score = model.getCount();
        holder.setScore(String.valueOf(score));

        //Here is the Code
        int realRank = 100 - holder.getAdapterPosition();

        holder.setRank(String.valueOf(realRank));
    }


 */