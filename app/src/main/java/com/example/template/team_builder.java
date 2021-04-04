package com.example.template;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class team_builder extends AppCompatActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    pokemonUser pkmnUser;
    Stats stats;
    private final FirebaseDatabase fb = FirebaseDatabase.getInstance();
    DatabaseReference db = fb.getReference();

    String pokemonName = "";
    int currPkmnLevel;
    int currPkmnBase;
    String[] natures = {"Hardy", "Lonely", "Brave", "Adamant", "Naughty", "Bold", "Docile", "Relaxed", "Impish", "Lax", "Timid", "Hasty",
                        "Serious", "Jolly", "Naive", "Modest", "Mild", "Quiet", "Bashful", "Rash", "Calm", "Gentle", "Sassy", "Careful", "Quirky"};
    String[] abilities = {"Overgrow", "Blaze", "Torrent"};

    Spinner natureSpinner;
    ArrayAdapter<String> natureAdapter;

    Spinner abilitySpinner;
    ArrayAdapter<String> abilityAdapter;

    TextView topDisplay;

    // all the edits texts so that we can set event listeners when they are changed
    EditText hpInput;
    EditText atkInput;
    EditText defInput;
    EditText spdInput;
    EditText satkInput;
    EditText sdefInput;
    Button send;

    int pokemonID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_builder);

        stats = new Stats();

        topDisplay = (TextView)findViewById(R.id.topDisplay);
        // all the edit texts have to be instantiated
        hpInput = (EditText)findViewById(R.id.HPInput);
        atkInput = (EditText)findViewById(R.id.attackInput);
        defInput = (EditText)findViewById(R.id.defenseInput);
        spdInput = (EditText)findViewById(R.id.speedInput);
        satkInput = (EditText)findViewById(R.id.sattackInput);
        sdefInput = (EditText)findViewById(R.id.sdefenseInput);
        send = (Button)findViewById(R.id.send);


        natureSpinner = (Spinner)findViewById(R.id.nature);
        abilitySpinner = (Spinner)findViewById(R.id.ability);

        pokemonID = getIntent().getIntExtra("pokemonID", 0);

        if (!(getIntent().getStringArrayExtra("abilities") == null))
            abilities = getIntent().getStringArrayExtra("abilities");


        if (!(getIntent().getStringExtra("pokemon") == null)) {
            pokemonName = getIntent().getStringExtra("pokemon");
            topDisplay.setText(pokemonName);
        }

        // setting up the spinner for the natures
        natureAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, natures);
        natureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        natureSpinner.setAdapter(natureAdapter);

        // setting up the spinners for the abilities
        abilityAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, abilities);
        abilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        abilitySpinner.setAdapter(abilityAdapter);

        //Text Watcher for each stat edit text box
        hpInput.addTextChangedListener(new TextWatcher() {
            int temp;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            public void afterTextChanged(Editable s) {
                try {
                    stats.setHp(Integer.parseInt(hpInput.getText().toString()));
                } catch (NumberFormatException e) {
                    stats.setHp(0);
                }

            }
        });

        atkInput.addTextChangedListener(new TextWatcher() {
            int temp;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            public void afterTextChanged(Editable s) {
                try {
                    stats.setAtk(Integer.parseInt(s.toString()));
                } catch (NumberFormatException e) {
                    stats.setAtk(0);
                }

            }
        });

        defInput.addTextChangedListener(new TextWatcher() {
            int temp;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            public void afterTextChanged(Editable s) {
                try {
                    stats.setDef(Integer.parseInt(defInput.getText().toString()));
                } catch (NumberFormatException e) {
                    stats.setDef(0);
                }

            }
        });

        spdInput.addTextChangedListener(new TextWatcher() {
            int temp;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            public void afterTextChanged(Editable s) {
                try {
                    stats.setSpd(Integer.parseInt(spdInput.getText().toString()));
                } catch (NumberFormatException e) {
                    stats.setSpd(0);
                }

            }
        });

        satkInput.addTextChangedListener(new TextWatcher() {
            int temp;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            public void afterTextChanged(Editable s) {
                try {
                    stats.setSatk(Integer.parseInt(satkInput.getText().toString()));
                } catch (NumberFormatException e) {
                    stats.setSatk(0);
                }

            }
        });

        sdefInput.addTextChangedListener(new TextWatcher() {
            int temp;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            public void afterTextChanged(Editable s) {
                try {
                    stats.setSdef(Integer.parseInt(sdefInput.getText().toString()));
                } catch (NumberFormatException e) {
                    stats.setSdef(0);
                }

            }
        });

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



    private void addPokemon() {
        int temp = 0;
        //TODO level thing replace the 1, and replace the Base with the stat from the pokedex
        if (temp < ((((2 * currPkmnBase + 0) * currPkmnLevel)/100) + currPkmnLevel + 10)) {

        }
    }



    public void submit(View view) {


    }

    // methods for going between the screens
    public void gotoAddView(View view) {
        Intent intent = new Intent(this, team_builder.class);
        startActivity(intent);
    }

    public void gotoDexView(View view) {
        int hp = stats.getHp();
        int atk = stats.getAtk();
        int def = stats.getDef();
        int satk = stats.getSatk();
        int sdef = stats.getSdef();
        int spd = stats.getSpd();
        System.out.println(hp + " " + atk);

        Spinner spinner1 = (Spinner)findViewById(R.id.nature);
        String nature = spinner1.getSelectedItem().toString();

        Spinner spinner2 = (Spinner)findViewById(R.id.ability);
        String ability = spinner2.getSelectedItem().toString();
        System.out.println(nature + " " + ability);

        EditText editLevel = (EditText)findViewById(R.id.level);
        String level = editLevel.getText().toString();
        System.out.println(level);

        FirebaseDatabase db =  FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userName = user.getEmail().replace('.', ',');
        DatabaseReference mRef = db.getReference().child("users").child(userName);
        // String pkmId = "pkm" + pokemonID;
        String pkmId = pokemonName;

        mRef.child("pokedex").child(pkmId).child("stats").child("hp").setValue(hp);
        mRef.child("pokedex").child(pkmId).child("stats").child("atk").setValue(atk);
        mRef.child("pokedex").child(pkmId).child("stats").child("def").setValue(def);
        mRef.child("pokedex").child(pkmId).child("stats").child("satk").setValue(satk);
        mRef.child("pokedex").child(pkmId).child("stats").child("sdef").setValue(sdef);
        mRef.child("pokedex").child(pkmId).child("stats").child("spd").setValue(spd);

        mRef.child("pokedex").child(pkmId).child("nature").setValue(nature);
        mRef.child("pokedex").child(pkmId).child("ability").setValue(ability);
        mRef.child("pokedex").child(pkmId).child("level").setValue(level);

        mRef.child("pokedex").child(pkmId).child("number").setValue(pokemonID);

        Intent intent = new Intent(this, PersonalDex.class);
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