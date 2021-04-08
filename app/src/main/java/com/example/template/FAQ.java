package com.example.template;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

// this is the page people will arrive at after pressing the FAQ button in settings
public class FAQ extends AppCompatActivity {
    // this will be displayed as the FAQ
    ListView listView;
    ArrayAdapter<String> arrAdapter;
    String[] dispText = {"How can I use the app best?",
            "Enter the Pokemon you have on your team into your Pokedex and we will tell you how good each individual Pokemon on your team is.",
            "What are natures?",
            "There are a bunch of different natures, so they may seem confusing but they aren't too hard to understand. " +
                    "They either \n\n1) Are neutral and don't impact your Pokemon's stats at all OR:" +
                    "\n\n2) Make 1 stat better in exchange for making another worse.",
            "So what's the best nature?",
            "That varies from Pokemon to Pokemon. Some Pokemon want high speed and don't really care about attack, " +
                    "so certain natures would help them whereas they wouldn't help others.",
            "So what else goes into stat calculation?",
            "Despite being a children's game, Pokemon has some really complex formulas for stat calculation. For the most part, each Pokemon has base stats, EVs and IVs." +
                    "\n\nIVs or Individual Values are randomly assigned to Pokemon when they appear into the world. They often determine the Pokemon's potential. The higher" +
                    " the IVs, the higher that Pokemon's stats can go, and the better it can theoretically be." +
                    "\n\nEVs or Effort Values are values that trainers can influence, but they are also extremely difficult to track. Everytime your Pokemon defeats another, it gets a specific" +
                    " EV from that Pokemon and then, their stats grow based on those accumulated EVs. You can pretty much just ignore EVs for the most part during your playthrough.\n"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        listView = (ListView)findViewById(R.id.faqListView);
        arrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dispText);
        listView.setAdapter(arrAdapter);
    }

    // these are the functions that go between each page
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
        Intent intent = new Intent(this, Main_menu_view.class);
        startActivity(intent);
    }
}
