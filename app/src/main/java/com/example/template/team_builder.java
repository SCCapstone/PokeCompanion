package com.example.template;

import android.content.Context;
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

import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Integer.parseInt;
import static java.lang.Math.floor;

public class team_builder extends AppCompatActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    /*
    Declarations
     */

    pokemonUser pkmnUser;
    Stats stats;
    private final FirebaseDatabase fb = FirebaseDatabase.getInstance();
    DatabaseReference db = fb.getReference();
    String type;

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

    ImageView imageView;

    // all the edits texts so that we can set event listeners when they are changed
    EditText hpInput;
    EditText atkInput;
    EditText defInput;
    EditText spdInput;
    EditText satkInput;
    EditText sdefInput;
    Button send;


    String pokemonIDs;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_builder);

        /*
        Declarations for all of the stat inputs on the page.
         */

        stats = new Stats();
        final Stats[] baseStats = {new Stats(0, 0, 0, 0, 0, 0)};

        topDisplay = (TextView)findViewById(R.id.topDisplay);
        // all the edit texts have to be instantiated
        hpInput = (EditText)findViewById(R.id.HPInput);
        atkInput = (EditText)findViewById(R.id.attackInput);
        defInput = (EditText)findViewById(R.id.defenseInput);
        spdInput = (EditText)findViewById(R.id.speedInput);
        satkInput = (EditText)findViewById(R.id.sattackInput);
        sdefInput = (EditText)findViewById(R.id.sdefenseInput);

        imageView = (ImageView)findViewById((R.id.imageView));


        natureSpinner = (Spinner)findViewById(R.id.nature);
        abilitySpinner = (Spinner)findViewById(R.id.ability);

        if (!(getIntent().getStringExtra("pokemonID") == null))
            pokemonIDs = getIntent().getStringExtra("pokemonID");

        else
            pokemonIDs = "001";

        /*
        Showing the pokemon sprites for the application
         */

        String imageName = "icon" + pokemonIDs;
        Context c = getApplicationContext();
        int id = c.getResources().getIdentifier("drawable/" + imageName, null, c.getPackageName());
        Log.e("picture id", id + "");

        imageView.setImageResource(id);

        /*
        Making sure that the array is populating with the information from FireBase
         */

        if (!(getIntent().getStringArrayExtra("abilities") == null))
            abilities = getIntent().getStringArrayExtra("abilities");


        if (!(getIntent().getStringExtra("pokemon") == null)) {
            pokemonName = getIntent().getStringExtra("pokemon");
            topDisplay.setText(pokemonName);
        }

        if (!(getIntent().getStringExtra("type") == null)) {
            type = getIntent().getStringExtra("type");
        }

        // setting up the spinner for the natures
        natureAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, natures);
        natureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        natureSpinner.setAdapter(natureAdapter);

        // setting up the spinners for the abilities
        abilityAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, abilities);
        abilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        abilitySpinner.setAdapter(abilityAdapter);



        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dexNum = pokemonIDs;
                DatabaseReference basePoke = fb.getReference().child("Pokemon").child(dexNum);
                baseStats[0] = snapshot.child("Pokemon").child(dexNum).child("baseStats").getValue(Stats.class);

                Long tempAtk = (long)snapshot.child("Pokemon").child(dexNum).child("baseStats").child("atk").getValue();
                baseStats[0].setAtk(tempAtk.intValue());
                Long tempDef = (long)snapshot.child("Pokemon").child(dexNum).child("baseStats").child("def").getValue();
                baseStats[0].setDef(tempDef.intValue());
                Long tempHp = (long)snapshot.child("Pokemon").child(dexNum).child("baseStats").child("hp").getValue();
                baseStats[0].setHp(tempHp.intValue());
                Long tempSpd = (long)snapshot.child("Pokemon").child(dexNum).child("baseStats").child("spd").getValue();
                baseStats[0].setSpd(tempSpd.intValue());
                Long tempSatk = (long)snapshot.child("Pokemon").child(dexNum).child("baseStats").child("satk").getValue();
                baseStats[0].setSatk(tempSatk.intValue());
                Long tempSdef = (long)snapshot.child("Pokemon").child(dexNum).child("baseStats").child("sdef").getValue();
                baseStats[0].setSdef(tempSdef.intValue());




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(team_builder.this,"Error: "+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        //Text Watcher for each stat edit text box

        final Timer[] timer = new Timer[1];
        hpInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer[0] !=null)
                    timer[0].cancel();
            }
            @Override
            public void afterTextChanged(Editable s) {
                timer[0] = new Timer();
                timer[0].schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        int lowest = (int) floor(floor(((2* baseStats[0].getHp()+0)*1)/100)+1+10);
                        int highest = (int) floor(floor(((2* baseStats[0].getHp()+31)*100)/100)+100+10);

                        if (!(hpInput.getText().toString().trim().isEmpty())) {
                            int actual = parseInt(hpInput.getText().toString());
                            if (actual < lowest) {
                                //dexPoke.child("stats").child("hp").setValue(lowest);
                                showToast("HP too low");
                                hpInput.setText(String.valueOf(lowest));
                            } else if (actual > highest) {
                                //dexPoke.child("stats").child("hp").setValue(highest);
                                showToast("HP too high");
                                hpInput.setText(String.valueOf(highest));
                            } else {
                                //dexPoke.child("stats").child("hp").setValue(actual);
                            }
                        }
                    }
                }, 1000);
                //showToast("Stat value invalid. Too low/high!");

            }
        });
        /* ===============================
         * Monitor for Attack input
         *
         * Attack, Defense, Special Attack and Defense, and Speed are calculated differently than HP
         * Based on the nature of the pokemon, A specific Stat is raised by 10% and a Stat is lowered by 10%
         * The calculations will check for this, and will perform the proper calculations accordingly
         * =============================== */
        atkInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer[0] !=null)
                    timer[0].cancel();
            }

            @Override
            public void afterTextChanged(Editable s) {
                timer[0] = new Timer();
                timer[0].schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        int lowest;
                        int highest;
                        Spinner spinner1 = (Spinner)findViewById(R.id.nature);
                        String nature = spinner1.getSelectedItem().toString();
                        if (nature.equalsIgnoreCase("adamant") | nature.equalsIgnoreCase("lonely") | nature.equalsIgnoreCase("naughty") | nature.equalsIgnoreCase("brave")) {
                            lowest = (int) floor((floor(((2* baseStats[0].getAtk()+0)*1)/100)+5)*1.1);
                            highest = (int) floor((floor(((2* baseStats[0].getAtk()+31)*100)/100)+5)*1.1);
                        }
                        else if (nature.equalsIgnoreCase("bold") | nature.equalsIgnoreCase("modest") | nature.equalsIgnoreCase("calm") | nature.equalsIgnoreCase("timid")) {
                            lowest = (int) floor((floor(((2* baseStats[0].getAtk()+0)*1)/100)+5)*0.9);
                            highest = (int) floor((floor(((2* baseStats[0].getAtk()+31)*100)/100)+5)*0.9);
                        }
                        else {
                            lowest = (int) floor((floor(((2* baseStats[0].getAtk()+0)*1)/100)+5)*1);
                            highest = (int) floor((floor(((2* baseStats[0].getAtk()+31)*100)/100)+5)*1);
                        }


                        if (!(atkInput.getText().toString().trim().isEmpty())) {
                            int actual = parseInt(atkInput.getText().toString());
                            if (actual < lowest) {
                                //dexPoke.child("stats").child("atk").setValue(lowest);
                                showToast("Attack too low");
                                atkInput.setText(String.valueOf(lowest));
                            } else if (actual > highest) {
                                //dexPoke.child("stats").child("atk").setValue(highest);
                                showToast("Attack too high");
                                atkInput.setText(String.valueOf(highest));
                            } else {
                                //.child("stats").child("atk").setValue(actual);
                            }
                        }
                    }
                }, 1000);
                //showToast("Stat value invalid. Too low/high!");

            }
        });
        /* ===============================
         * Monitor for Defense Input
         * =============================== */
        defInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer[0] !=null)
                    timer[0].cancel();
            }

            @Override
            public void afterTextChanged(Editable s) {
                timer[0] = new Timer();
                timer[0].schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        int lowest;
                        int highest;
                        Spinner spinner1 = (Spinner)findViewById(R.id.nature);
                        String nature = spinner1.getSelectedItem().toString();
                        if (nature.equalsIgnoreCase("bold") | nature.equalsIgnoreCase("impish") | nature.equalsIgnoreCase("lax") | nature.equalsIgnoreCase("relaxed")) {
                            lowest = (int) floor((floor(((2* baseStats[0].getDef()+0)*1)/100)+5)*1.1);
                            highest = (int) floor((floor(((2* baseStats[0].getDef()+31)*100)/100)+5)*1.1);
                        }
                        else if (nature.equalsIgnoreCase("hasty") | nature.equalsIgnoreCase("gentle") | nature.equalsIgnoreCase("mild") | nature.equalsIgnoreCase("lonely")) {
                            lowest = (int) floor((floor(((2* baseStats[0].getDef()+0)*1)/100)+5)*0.9);
                            highest = (int) floor((floor(((2* baseStats[0].getDef()+31)*100)/100)+5)*0.9);
                        }
                        else {
                            lowest = (int) floor((floor(((2* baseStats[0].getDef()+0)*1)/100)+5)*1);
                            highest = (int) floor((floor(((2* baseStats[0].getDef()+31)*100)/100)+5)*1);
                        }


                        if (!(defInput.getText().toString().trim().isEmpty())) {
                            int actual = parseInt(defInput.getText().toString());
                            if (actual < lowest) {
                                //dexPoke.child("stats").child("def").setValue(lowest);
                                showToast("Defense too low");
                                defInput.setText(String.valueOf(lowest));
                            } else if (actual > highest) {
                                //dexPoke.child("stats").child("def").setValue(highest);
                                showToast("Defense too high");
                                defInput.setText(String.valueOf(highest));
                            } else {
                                //dexPoke.child("stats").child("def").setValue(actual);
                            }
                        }
                    }
                },1000);
                //showToast("Stat value invalid. Too low/high!");

            }
        });
        /* ===============================
         * Monitor for Special Attack Input
         * =============================== */
        satkInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer[0] !=null)
                    timer[0].cancel();
            }

            @Override
            public void afterTextChanged(Editable s) {
                timer[0] = new Timer();
                timer[0].schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        int lowest;
                        int highest;
                        Spinner spinner1 = (Spinner)findViewById(R.id.nature);
                        String nature = spinner1.getSelectedItem().toString();
                        if (nature.equalsIgnoreCase("modest") | nature.equalsIgnoreCase("mild") | nature.equalsIgnoreCase("rash") | nature.equalsIgnoreCase("quiet")) {
                            lowest = (int) floor((floor(((2* baseStats[0].getSatk()+0)*1)/100)+5)*1.1);
                            highest = (int) floor((floor(((2* baseStats[0].getSatk()+31)*100)/100)+5)*1.1);
                        }
                        else if (nature.equalsIgnoreCase("jolly") | nature.equalsIgnoreCase("careful") | nature.equalsIgnoreCase("impish") | nature.equalsIgnoreCase("adamant")) {
                            lowest = (int) floor((floor(((2* baseStats[0].getSatk()+0)*1)/100)+5)*0.9);
                            highest = (int) floor((floor(((2* baseStats[0].getSatk()+31)*100)/100)+5)*0.9);
                        }
                        else {
                            lowest = (int) floor((floor(((2* baseStats[0].getSatk()+0)*1)/100)+5)*1);
                            highest = (int) floor((floor(((2* baseStats[0].getSatk()+31)*100)/100)+5)*1);
                        }


                        if (!(satkInput.getText().toString().trim().isEmpty())) {
                            int actual = parseInt(satkInput.getText().toString());
                            if (actual < lowest) {
                                //dexPoke.child("stats").child("satk").setValue(lowest);
                                showToast("Sp. Attack too low");
                                satkInput.setText(String.valueOf(lowest));
                            } else if (actual > highest) {
                                //dexPoke.child("stats").child("satk").setValue(highest);
                                showToast("Sp. Attack too high");
                                satkInput.setText(String.valueOf(highest));
                            } else {
                                //dexPoke.child("stats").child("satk").setValue(actual);
                            }
                        }
                    }
                },1000);
                //showToast("Stat value invalid. Too low/high!");

            }
        });
        /* ===============================
         * Monitor for Special Defense Input
         * =============================== */
        sdefInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer[0] !=null)
                    timer[0].cancel();
            }

            @Override
            public void afterTextChanged(Editable s) {
                timer[0] = new Timer();
                timer[0].schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        int lowest;
                        int highest;
                        Spinner spinner1 = (Spinner)findViewById(R.id.nature);
                        String nature = spinner1.getSelectedItem().toString();
                        if (nature.equalsIgnoreCase("sassy") | nature.equalsIgnoreCase("careful") | nature.equalsIgnoreCase("gentle") | nature.equalsIgnoreCase("calm")) {
                            lowest = (int) floor((floor(((2* baseStats[0].getSdef()+0)*1)/100)+5)*1.1);
                            highest = (int) floor((floor(((2* baseStats[0].getSdef()+31)*100)/100)+5)*1.1);
                        }
                        else if (nature.equalsIgnoreCase("naughty") | nature.equalsIgnoreCase("lax") | nature.equalsIgnoreCase("rash") | nature.equalsIgnoreCase("naive")) {
                            lowest = (int) floor((floor(((2* baseStats[0].getSdef()+0)*1)/100)+5)*0.9);
                            highest = (int) floor((floor(((2* baseStats[0].getSdef()+31)*100)/100)+5)*0.9);
                        }
                        else {
                            lowest = (int) floor((floor(((2* baseStats[0].getSdef()+0)*1)/100)+5)*1);
                            highest = (int) floor((floor(((2* baseStats[0].getSdef()+31)*100)/100)+5)*1);
                        }


                        if (!(sdefInput.getText().toString().trim().isEmpty())) {
                            int actual = parseInt(sdefInput.getText().toString());
                            if (actual < lowest) {
                               //dexPoke.child("stats").child("sdef").setValue(lowest);
                                showToast("Sp. Defense too low");
                                sdefInput.setText(String.valueOf(lowest));
                            } else if (actual > highest) {
                               // dexPoke.child("stats").child("sdef").setValue(highest);
                                showToast("Sp. Defense too high");
                                sdefInput.setText(String.valueOf(highest));
                            } else {
                                //dexPoke.child("stats").child("sdef").setValue(actual);
                            }
                        }
                    }
                },1000);
                //showToast("Stat value invalid. Too low/high!");

            }
        });
        /* ===============================
         * Monitor for Speed Input
         * =============================== */
        spdInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer[0] !=null)
                    timer[0].cancel();
            }

            @Override
            public void afterTextChanged(Editable s) {
                timer[0] = new Timer();
                timer[0].schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        int lowest;
                        int highest;
                        Spinner spinner1 = (Spinner)findViewById(R.id.nature);
                        String nature = spinner1.getSelectedItem().toString();
                        if (nature.equalsIgnoreCase("timid") | nature.equalsIgnoreCase("hasty") | nature.equalsIgnoreCase("jolly") | nature.equalsIgnoreCase("naive")) {
                            lowest = (int) floor((floor(((2* baseStats[0].getSpd()+0)*1)/100)+5)*1.1);
                            highest = (int) floor((floor(((2* baseStats[0].getSpd()+31)*100)/100)+5)*1.1);
                        }
                        else if (nature.equalsIgnoreCase("sassy") | nature.equalsIgnoreCase("quiet") | nature.equalsIgnoreCase("relaxed") | nature.equalsIgnoreCase("brave")) {
                            lowest = (int) floor((floor(((2* baseStats[0].getSpd()+0)*1)/100)+5)*0.9);
                            highest = (int) floor((floor(((2* baseStats[0].getSpd()+31)*100)/100)+5)*0.9);
                        }
                        else {
                            lowest = (int) floor((floor(((2* baseStats[0].getSpd()+0)*1)/100)+5)*1);
                            highest = (int) floor((floor(((2* baseStats[0].getSpd()+31)*100)/100)+5)*1);
                        }


                        if (!(spdInput.getText().toString().trim().isEmpty())) {
                            int actual = parseInt(spdInput.getText().toString());
                            if (actual < lowest) {
                                //dexPoke.child("stats").child("spd").setValue(lowest);
                                showToast("Speed too low");
                                spdInput.setText(String.valueOf(lowest));
                            } else if (actual > highest) {
                                //dexPoke.child("stats").child("spd").setValue(highest);
                                showToast("Speed too high");
                                spdInput.setText(String.valueOf(highest));
                            } else {
                                //dexPoke.child("stats").child("spd").setValue(actual);
                            }
                        }
                    }
                },1000);
                //showToast("Stat value invalid. Too low/high!");

            }
        });

    }

    /*
    Tester method for adding pokemon
     */
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

    public void goPickOne(View view) {
        Intent intent = new Intent(this, PokedexView.class);
        startActivity(intent);
    }

    public void gotoDexView(View view) {

        // Declarations for pushing the information with firebase
        int hp = Integer.parseInt(String.valueOf(hpInput.getText()));
        int atk =  Integer.parseInt(String.valueOf(atkInput.getText()));
        int def =  Integer.parseInt(String.valueOf(defInput.getText()));
        int satk =  Integer.parseInt(String.valueOf(satkInput.getText()));
        int sdef =  Integer.parseInt(String.valueOf(sdefInput.getText()));
        int spd =  Integer.parseInt(String.valueOf(spdInput.getText()));
        System.out.println(hp + " " + atk);

        // Getting spinner/edit text information
        Spinner spinner1 = (Spinner)findViewById(R.id.nature);
        String nature = spinner1.getSelectedItem().toString();

        Spinner spinner2 = (Spinner)findViewById(R.id.ability);
        String ability = spinner2.getSelectedItem().toString();
        System.out.println(nature + " " + ability);

        EditText editLevel = (EditText)findViewById(R.id.level);
        String level = editLevel.getText().toString();
        if (level.equals("")) {
            level = "00";
        }
        int levelInt = Integer.parseInt(level);
        System.out.println(level);

        if (pokemonName == "") {
            Toast.makeText(team_builder.this, "Select one pokemon, please", Toast.LENGTH_LONG).show();
            return;
        } else if ((hp == 0) || (atk == 0) || (def == 0) || (satk == 0) || (sdef == 0) || (spd == 0) || (levelInt == 0)) {
            Toast.makeText(team_builder.this, "Fill all number fields, please", Toast.LENGTH_LONG).show();
            return;
        } else if (ability == "") {
            Toast.makeText(team_builder.this, "Select Ability, please", Toast.LENGTH_LONG).show();
            return;
        } else if (nature == "") {
            Toast.makeText(team_builder.this, "Select Nature, please", Toast.LENGTH_LONG).show();
            return;
        }else {

            // Pushing edit text information to the FireBase Data Base
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userName = user.getEmail().replace('.', ',');
            DatabaseReference mRef = db.getReference().child("users").child(userName);
            // String pkmId = "pkm" + pokemonID;
            String pkmId = pokemonName;

            // Refrencing the child that the information is pushed to
            mRef.child("pokedex").child(pkmId).child("stats").child("hp").setValue(hp);
            mRef.child("pokedex").child(pkmId).child("stats").child("atk").setValue(atk);
            mRef.child("pokedex").child(pkmId).child("stats").child("def").setValue(def);
            mRef.child("pokedex").child(pkmId).child("stats").child("satk").setValue(satk);
            mRef.child("pokedex").child(pkmId).child("stats").child("sdef").setValue(sdef);
            mRef.child("pokedex").child(pkmId).child("stats").child("spd").setValue(spd);

            mRef.child("pokedex").child(pkmId).child("type").setValue(type);

            mRef.child("pokedex").child(pkmId).child("nature").setValue(nature);
            mRef.child("pokedex").child(pkmId).child("ability").setValue(ability);
            mRef.child("pokedex").child(pkmId).child("level").setValue(level);

            mRef.child("pokedex").child(pkmId).child("number").setValue(pokemonIDs);

            Intent intent = new Intent(this, PersonalDex.class);
            startActivity(intent);
        }
    }

    /*
    Buttons at the bottom of the page
     */
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

    private void showToast(String t) {
        Toast.makeText(team_builder.this, t, Toast.LENGTH_LONG).show();
    }

}