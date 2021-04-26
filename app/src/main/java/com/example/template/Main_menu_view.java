package com.example.template;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

// this page functions as the settings page, and is accessed by pressing the cog button
public class Main_menu_view extends AppCompatActivity {
    // the 5 views we want
    /*home/newsfeed
    pokedex view
    add pokemon
    settings
    team view*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_view);
    }

    // this is the code for transitioning between views with the buttons on the bottom
    /*  ===============================================================================================
        Should these buttons be edited so the newsView is commented out instead of the settingsView?
        - JD Edwards 2021 April 5
     *  ============================================================================================ */
    public void gotoAddView(View view) {
        Intent intent = new Intent(this, team_builder.class);
        startActivity(intent);
    }

    public void gotoDexView(View view) {
        Intent intent = new Intent(this, PokedexView.class);
        startActivity(intent);
    }

    public void gotoNewsView(View view) {
        Intent intent = new Intent(this, RSS_view.class);
        startActivity(intent);
    }

    public void gotoTeamView(View view) {
        Intent intent = new Intent(this, PersonalDex.class);
        startActivity(intent);
    }

    public void gotoSettingsView(View view) {
        //Intent intent = new Intent(this, Main_menu_view.class);
        //startActivity(intent);
    }


    //signs the user out of Firebase(Google if they're using it) and returns them to the login ui
    public void signOut(View view) {
        final Intent intent = new Intent(this, MainActivity.class);
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(intent);
                    }
                });
        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
    }

    public void gotoFAQ(View view) {
        Intent intent = new Intent(this, FAQ.class);
        startActivity(intent);

    }
}