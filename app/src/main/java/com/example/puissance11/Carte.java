package com.example.puissance11;

import android.widget.ImageView;

import java.util.concurrent.atomic.AtomicInteger;


public class Carte {
    ImageView image;
    private static final AtomicInteger count = new AtomicInteger(0);
    int id ;
    int gagne;
    int gagne2 = -1;
    int gagne3 = -1;
    int perd;
    int perd2 = -1;
    int perd3 = -1;


    Carte(ImageView im, boolean raz){
        image=im;
        if(raz) {
            count.set(0);
        }
        id = count.incrementAndGet();

    }
    Carte(ImageView im){
        image=im;
        id = count.incrementAndGet();

    }

    public void setMatch(int gagne,int perd){  //Set les valeurs de gagne et perd
        this.gagne=gagne;
        this.perd=perd;
    }

    public void setMatch(int gagne,int gagne2,int perd, int perd2){  //Set les valeurs de gagne et perd
        this.gagne=gagne;
        this.perd=perd;
        this.gagne2=gagne2;
        this.perd2=perd2;
    }

    public void setMatch(int gagne,int gagne2,int gagne3,int perd, int perd2, int perd3){  //Set les valeurs de gagne et perd
        this.gagne=gagne;
        this.perd=perd;
        this.gagne2=gagne2;
        this.perd2=perd2;
        this.gagne3=gagne3;
        this.perd3=perd3;
    }

    public ImageView getImage(){
        return image;
    }

    public int getID(){
        return id;
    }

    public int getGagne(){
        return gagne;
    }

    public int getGagne2(){return gagne2;}

    public int getGagne3(){
        return gagne3;
    }

    public int getPerd(){
        return perd;
    }

    public int getPerd2(){
        return perd2;
    }

    public int getPerd3(){
        return perd3;
    }

    public void setId(int ID){
        id=ID;
    }



}

