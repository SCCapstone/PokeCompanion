package com.example.template;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class team_builder extends AppCompatActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    String pokemonName = "";
    String[] natures = {"Hardy", "Lonely", "Brave", "Adamant", "Naughty", "Bold", "Docile", "Relaxed", "Impish", "Lax", "Timid", "Hasty",
                        "Serious", "Jolly", "Naive", "Modest", "Mild", "Quiet", "Bashful", "Rash", "Calm", "Gentle", "Sassy", "Careful", "Quirky"};
    String[] abilities = {""};

    Spinner natureSpinner;
    ArrayAdapter<String> natureAdapter;

    Spinner abilitySpinner;
    ArrayAdapter<String> abilityAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        natureSpinner = (Spinner)findViewById(R.id.nature);
        abilitySpinner = (Spinner)findViewById(R.id.ability);

        abilities = getIntent().getStringArrayExtra("abilities");
        pokemonName = getIntent().getStringExtra("pokemon");

        // setting up the spinner for the natures
        //natureAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, natures);
        natureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        natureSpinner.setAdapter(natureAdapter);

        // setting up the spinners for the abilities
        //abilityAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, abilities);
        abilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        abilitySpinner.setAdapter(abilityAdapter);

    }

    // take the name of the pokemon (pokemonID) display it at the top and let them enter the values for their stats
    // also should be able to enter nature and ability from a dropdown menu

    /*
    private void showDataSpinner() {
        databaseReference.child("Pokemon").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    arrayList.add(item.child("Name").getValue(String.class));
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(team_builder.this, R.layout.style_spinner, arrayList);
                pokemon1.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    private void insertPokemonData(){
        //String stat = pokemon_stat.getText().toString();

        //Pokemon pokemon = new Pokemon(stat);

        //databaseReference.push().setValue(pokemon);
    }

    public void gotoAddView(View view) {
        //Intent intent = new Intent(this, team_builder.class);
        //startActivity(intent);
    }

    public void submit(View view) {

    }





    // methods for going between the screens
    public void gotoDexView(View view) {
        Intent intent = new Intent(this, PokedexView.class);
        startActivity(intent);
    }

    public void gotoNewsView(View view) {
        Intent intent = new Intent(this, RSS_view.class);
        startActivity(intent);
    }

    public void gotoTeamView(View view) {
        Intent intent = new Intent(this, Individual_Pokemon_view.class);
        startActivity(intent);
    }

    public void gotoSettingsView(View view) {
        Intent intent = new Intent(this, Main_menu_view.class);
        startActivity(intent);
    }

}