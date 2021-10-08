package com.example.puissance11;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Regles extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regles);

    }
}



/*
\n
        On fonction d'où on vient : faire en sorte que la page des règles renvoie où l'on était

        Niveau 1 : Pierre - Papier - Ciseaux\n

       Une victoire = 3 points, une défaite = -1 points \n

        - La pierre bat la paire de ciseaux\n
        - La paire de ciseaux bat la feuille\n
        - La feuille bat la pierre\n

        Niveau 2 : Pierre - Papier - Ciseaux - Puits  \n
        Une victoire = 5 points, une défaite = -2 points\n

         - La pierre bat la paire de ciseaux\n
         - La feuille bat la pierre\n
         - La feuille bat le puits\n
         - Le puits bat la paire de cieseaux\n
         - Le puits bat la pierre\n

         Niveau 3 : Puissance 7 : Pierre - Papier - Ciseaux - Eau - Feu - Air - Eponge\n
        <div></div>
        - La pierre éteint le feu, écrase les ciseaux et l'éponge\n
        - Le feu fait fondre les ciseaux, brûle l'éponge et le papier\n
        - Les ciseaux coupent l'éponge, le papier et son claquement réseonne dans l'air\n
        - L'éponge mouille le papier, contient des trous d'air et absorbe l'eau\n
        - Le papier évente l'air, flotte sur l'eau et recouvre la pierre\n
        - L'air évapore l'eau, érode la pierre et éteint le feu\n
        - l'eau érode la pierre, éteint le feu et rouille les ciseaux\n


        <string name="rules">Niveau 1 : Pierre - Papier - Ciseaux\nUne victoire = 3 points, une défaite = -1 points \n\n- La pierre bat la paire de ciseaux\n- La paire de ciseaux bat la feuille\n- La feuille bat la pierre\n\nNiveau 2 : Pierre - Papier - Ciseaux - Puits  \nUne victoire = 5 points, une défaite = -2 points\n\n- La pierre bat la paire de ciseaux\n- La feuille bat la pierre\n- La feuille bat le puits\n- Le puits bat la paire de cieseaux\n- Le puits bat la pierre\n\nNiveau 3 : Puissance 7 : Pierre - Papier - Ciseaux - Eau - Feu - Air - Eponge\n\n- La pierre éteint le feu, écrase les ciseaux et l'éponge\n- Le feu fait fondre les ciseaux, brûle l'éponge et le papier\n- Les ciseaux coupent l'éponge, le papier et son claquement réseonne dans l'air\n- L'éponge mouille le papier, contient des trous d'air et absorbe l'eau\n- Le papier évente l'air, flotte sur l'eau et recouvre la pierre\n- L'air évapore l'eau, érode la pierre et éteint le feu\n- l'eau érode la pierre, éteint le feu et rouille les ciseaux\n</string>



 */