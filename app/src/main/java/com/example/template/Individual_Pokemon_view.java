/*
 * Sample Android app for Capstone Research Milestone
 * PokeCompanion
 * This app is a companion app for any main series Pokemon video game.
 * Features
 * Allows users to track their Pokemon's growth throughout their playthough so they can see the strengths and weaknesses it develops
 * Allows users to predict how they will continue to grow
 * Assists the user throughout their playthough by offering bits of advice concerning how to raise their Pokemon
 * Provides an easy way to theorycraft and plan their perfect team for their game
 *
 * Invdividual Pokemon Detailed View
 * This page allows users to edit the stats of Pokemon they have already created, to reflect change which will occur in-game.
 * At any point, the user can calculate their Pokemon's hidden stat determinants known as IVs to gain insight about their Pokemon's Stats
 *
 */

package com.example.template;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Integer.parseInt;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public class Individual_Pokemon_view extends AppCompatActivity {

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    /* ===============================
     * Declaring Database references for data retrieval and manipulation
     * =============================== */
    DatabaseReference dbRef = db.getReference();
    FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    String userName = currUser.getEmail().replace('.', ',');
    DatabaseReference dbRefDex = db.getReference().child("users").child(userName).child("pokedex");
    DatabaseReference dbRefBase = db.getReference().child("Pokemon");

    /* ===============================
     * Setting Default Values of most values
     * =============================== */
    String dexNum = "001";
    String pokemonID = "Bulbasaur";
    String nature = "Hardy";
    int level = 5;
    DatabaseReference basePoke = dbRefBase.child(dexNum);
    DatabaseReference dexPoke = dbRefDex.child(pokemonID);

    Stats currStats = new Stats(0,0,0,0,0,0);
    Stats baseStats = new Stats(0,0,0,0,0,0);
    Stats evStats = new Stats(0, 0, 0, 0,0,0);
    Stats ivStats = new Stats(0, 0, 0, 0,0,0);

    int HP, ATK, DEF, SPD, SATK, SDEF;
    double IRR_FACTOR = 0.85;

    pokemonUser displayMon;

    /* ===============================
     * Declaring Variables which will link to the elements on-screen. They must be declared beforehand to avoid nullPointerExceptions
     * =============================== */

    String pokemonName="";

    ImageView imageView;

    EditText hpInput;
    EditText atkInput;
    EditText defInput;
    EditText spdInput;
    EditText satkInput;
    EditText sdefInput;

    TextView baseHpDisplay;
    TextView baseAtkDisplay;
    TextView baseDefDisplay;
    TextView baseSatkDisplay;
    TextView baseSdefDisplay;
    TextView baseSpdDisplay;

    TextView ivHpDisplay;
    TextView ivAtkDisplay;
    TextView ivDefDisplay;
    TextView ivSatkDisplay;
    TextView ivSdefDisplay;
    TextView ivSpdDisplay;

    Button statCalculation;

    TextView dispName;

    DataSnapshot snap;



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* ===============================
         * Main Method of the App.
         * All functional code occurs within this block
         * Beginning with the initialization of the values which rely on data from the database
         * =============================== */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual__pokemon_view);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /* ===============================
         * This screen is only accessible by selecting a Pokemon from  your personal Pokedex
         * Data from that screen must be accessed by pulling extra data sent via intent switching
         * =============================== */
        if((getIntent().getStringExtra("nickname"))!=null)
            pokemonID = getIntent().getStringExtra("nickname");
        if(getIntent().getStringExtra("pkmnHP") != null)
            currStats.setHp(parseInt(getIntent().getStringExtra("pkmnHP")));
        if(getIntent().getStringExtra("pkmnSPD") != null)
            currStats.setSpd(parseInt(getIntent().getStringExtra("pkmnSPD")));
        if(getIntent().getStringExtra("pkmnATK") != null)
            currStats.setAtk(parseInt(getIntent().getStringExtra("pkmnATK")));
        if(getIntent().getStringExtra("pkmnDEF") != null)
            currStats.setDef(parseInt(getIntent().getStringExtra("pkmnDEF")));
        if(getIntent().getStringExtra("pkmnSATK") != null)
            currStats.setSatk(parseInt(getIntent().getStringExtra("pkmnSATK")));
        if(getIntent().getStringExtra("pkmnSDEF") != null)
            currStats.setSdef(parseInt(getIntent().getStringExtra("pkmnSDEF")));
        Log.e("stats", currStats.toString());

        /* ===============================
         * Setting the value of the Icon which will display the Minisprite of the Pokemon
         * =============================== */
        String picID = "icon001";
        if(getIntent().getStringExtra("pictureID") != null) {
            picID = getIntent().getStringExtra("pictureID");
            Log.e("picID", picID);
        }
        imageView = findViewById(R.id.spriteDisplay);
        // the picture name is passed to us, but we still have to access the android studio designated ID
        Context c = getApplicationContext();
        int id = c.getResources().getIdentifier("drawable/" + picID, null, c.getPackageName());
        imageView.setImageResource(id);

        /* ===============================
         * Linking the Editable text boxes on-screen to the values corresponding
         * =============================== */
        hpInput = (EditText)findViewById(R.id.hpInput);
        atkInput = (EditText)findViewById(R.id.atkInput);
        defInput = (EditText)findViewById(R.id.defenseInput);
        spdInput = (EditText)findViewById(R.id.spdInput);
        satkInput = (EditText)findViewById(R.id.satkInput);
        sdefInput = (EditText)findViewById(R.id.sdefInput);

        /* ===============================
         * Link to method which sets text in text boxes to the current stats stored in the database
         * =============================== */
        displayCurrStats();

        /* ===============================
         * Linking the Static number displays which show calculated IVs to their corresponding variables
         * =============================== */
        ivHpDisplay = (TextView)findViewById(R.id.iv_hp_num);
        ivAtkDisplay = (TextView)findViewById(R.id.iv_atk_num);
        ivDefDisplay = (TextView)findViewById(R.id.iv_def_num);
        ivSatkDisplay = (TextView)findViewById(R.id.iv_satk_num);
        ivSdefDisplay = (TextView)findViewById(R.id.iv_sdef_num);
        ivSpdDisplay = (TextView)findViewById(R.id.iv_speed_num);

        /* ===============================
         * Final linking of elements to variables
         * =============================== */
        statCalculation = (Button)findViewById(R.id.calcStats);

        dispName = (TextView) findViewById(R.id.pokemon_name);


        /* ===============================
         * Method which listens to the entire database for changes within it.
         * All values which are not to be changed after a single access are set within this.
         * These values being the base stats of the pokemon, its nature and level
         * An additional database reference is specified to rest at the node corresponding to the Pokemon which is being edited.
         * =============================== */
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snap = snapshot;
                dexNum = String.valueOf(snap.child("users").child(userName).child("pokedex").child(pokemonID).child("number").getValue());
                basePoke = dbRefBase.child(dexNum);
                baseStats = snapshot.child("Pokemon").child(dexNum).child("baseStats").getValue(Stats.class);

                Long tempAtk = (long)snapshot.child("Pokemon").child(dexNum).child("baseStats").child("atk").getValue();
                baseStats.setAtk(tempAtk.intValue());
                Long tempDef = (long)snapshot.child("Pokemon").child(dexNum).child("baseStats").child("def").getValue();
                baseStats.setDef(tempDef.intValue());
                Long tempHp = (long)snapshot.child("Pokemon").child(dexNum).child("baseStats").child("hp").getValue();
                baseStats.setHp(tempHp.intValue());
                Long tempSpd = (long)snapshot.child("Pokemon").child(dexNum).child("baseStats").child("spd").getValue();
                baseStats.setSpd(tempSpd.intValue());
                Long tempSatk = (long)snapshot.child("Pokemon").child(dexNum).child("baseStats").child("satk").getValue();
                baseStats.setSatk(tempSatk.intValue());
                Long tempSdef = (long)snapshot.child("Pokemon").child(dexNum).child("baseStats").child("sdef").getValue();
                baseStats.setSdef(tempSdef.intValue());


                dexPoke = dbRefDex.child(pokemonID);
                nature = String.valueOf(snapshot.child("users").child(userName).child("pokedex").child(pokemonID).child("nature").getValue());
                level = Integer.parseInt(String.valueOf(snapshot.child("users").child(userName).child("pokedex").child(pokemonID).child("level").getValue()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Individual_Pokemon_view.this,"Error: "+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


        /* ===============================
         * Just for double checking, the stats are again set to display within the editable text boxes
         * The display name for the pokemon is also declared
         * =============================== */
        displayCurrStats();
        dispName.setText(pokemonID);


        final Timer[] timer = new Timer[1];
        /* ===============================
         * A timer is declared for use when editing text
         *
         * Each editable text box has a monitor which detects when the values within are modified.
         * Upon the editing of text, the program will wait for one second to allow the user time to enter the number fully
         * A calculation is performed to find the theoretical minimum and maximum values of the stat
         * If the number provided is outside the bounds given, the number will automatically update to the closest number within the acceptable range
         * =============================== */
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
                        int lowest = (int) floor(floor(((2*baseStats.getHp()+0)*level)/100)+level+10);
                        int highest = (int) floor(floor(((2*baseStats.getHp()+31)*level)/100)+level+10);

                        if (!(hpInput.getText().toString().trim().isEmpty())) {
                            int actual = parseInt(hpInput.getText().toString());
                            if (actual < lowest) {
                                dexPoke.child("stats").child("hp").setValue(lowest);
                                showToast("HP too low");
                                hpInput.setText(String.valueOf(lowest));
                            } else if (actual > highest) {
                                dexPoke.child("stats").child("hp").setValue(highest);
                                showToast("HP too high");
                                hpInput.setText(String.valueOf(highest));
                            } else {
                                dexPoke.child("stats").child("hp").setValue(actual);
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
                        if (nature.equalsIgnoreCase("adamant") | nature.equalsIgnoreCase("lonely") | nature.equalsIgnoreCase("naughty") | nature.equalsIgnoreCase("brave")) {
                            lowest = (int) floor((floor(((2*baseStats.getAtk()+0)*level)/100)+5)*1.1);
                            highest = (int) floor((floor(((2*baseStats.getAtk()+31)*level)/100)+5)*1.1);
                        }
                        else if (nature.equalsIgnoreCase("bold") | nature.equalsIgnoreCase("modest") | nature.equalsIgnoreCase("calm") | nature.equalsIgnoreCase("timid")) {
                            lowest = (int) floor((floor(((2*baseStats.getAtk()+0)*level)/100)+5)*0.9);
                            highest = (int) floor((floor(((2*baseStats.getAtk()+31)*level)/100)+5)*0.9);
                        }
                        else {
                            lowest = (int) floor((floor(((2*baseStats.getAtk()+0)*level)/100)+5)*1);
                            highest = (int) floor((floor(((2*baseStats.getAtk()+31)*level)/100)+5)*1);
                        }


                        if (!(atkInput.getText().toString().trim().isEmpty())) {
                            int actual = parseInt(atkInput.getText().toString());
                            if (actual < lowest) {
                                dexPoke.child("stats").child("atk").setValue(lowest);
                                //showToast("Attack too low");
                                atkInput.setText(String.valueOf(lowest));
                            } else if (actual > highest) {
                                dexPoke.child("stats").child("atk").setValue(highest);
                                //showToast("Attack too high");
                                atkInput.setText(String.valueOf(highest));
                            } else {
                                dexPoke.child("stats").child("atk").setValue(actual);
                            }
                        }
                    }
                }, 1000);
                showToast("Stat value invalid. Too low/high!");

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
                        if (nature.equalsIgnoreCase("bold") | nature.equalsIgnoreCase("impish") | nature.equalsIgnoreCase("lax") | nature.equalsIgnoreCase("relaxed")) {
                            lowest = (int) floor((floor(((2*baseStats.getDef()+0)*level)/100)+5)*1.1);
                            highest = (int) floor((floor(((2*baseStats.getDef()+31)*level)/100)+5)*1.1);
                        }
                        else if (nature.equalsIgnoreCase("hasty") | nature.equalsIgnoreCase("gentle") | nature.equalsIgnoreCase("mild") | nature.equalsIgnoreCase("lonely")) {
                            lowest = (int) floor((floor(((2*baseStats.getDef()+0)*level)/100)+5)*0.9);
                            highest = (int) floor((floor(((2*baseStats.getDef()+31)*level)/100)+5)*0.9);
                        }
                        else {
                            lowest = (int) floor((floor(((2*baseStats.getDef()+0)*level)/100)+5)*1);
                            highest = (int) floor((floor(((2*baseStats.getDef()+31)*level)/100)+5)*1);
                        }


                        if (!(defInput.getText().toString().trim().isEmpty())) {
                            int actual = parseInt(defInput.getText().toString());
                            if (actual < lowest) {
                                dexPoke.child("stats").child("def").setValue(lowest);
                                //showToast("Defense too low");
                                defInput.setText(String.valueOf(lowest));
                            } else if (actual > highest) {
                                dexPoke.child("stats").child("def").setValue(highest);
                                //showToast("Defense too high");
                                defInput.setText(String.valueOf(highest));
                            } else {
                                dexPoke.child("stats").child("def").setValue(actual);
                            }
                        }
                    }
                },1000);
                showToast("Stat value invalid. Too low/high!");

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
                        if (nature.equalsIgnoreCase("modest") | nature.equalsIgnoreCase("mild") | nature.equalsIgnoreCase("rash") | nature.equalsIgnoreCase("quiet")) {
                            lowest = (int) floor((floor(((2*baseStats.getSatk()+0)*level)/100)+5)*1.1);
                            highest = (int) floor((floor(((2*baseStats.getSatk()+31)*level)/100)+5)*1.1);
                        }
                        else if (nature.equalsIgnoreCase("jolly") | nature.equalsIgnoreCase("careful") | nature.equalsIgnoreCase("impish") | nature.equalsIgnoreCase("adamant")) {
                            lowest = (int) floor((floor(((2*baseStats.getSatk()+0)*level)/100)+5)*0.9);
                            highest = (int) floor((floor(((2*baseStats.getSatk()+31)*level)/100)+5)*0.9);
                        }
                        else {
                            lowest = (int) floor((floor(((2*baseStats.getSatk()+0)*level)/100)+5)*1);
                            highest = (int) floor((floor(((2*baseStats.getSatk()+31)*level)/100)+5)*1);
                        }


                        if (!(satkInput.getText().toString().trim().isEmpty())) {
                            int actual = parseInt(satkInput.getText().toString());
                            if (actual < lowest) {
                                dexPoke.child("stats").child("satk").setValue(lowest);
                                //showToast("Sp. Attack too low");
                                satkInput.setText(String.valueOf(lowest));
                            } else if (actual > highest) {
                                dexPoke.child("stats").child("satk").setValue(highest);
                                //showToast("Sp. Attack too high");
                                satkInput.setText(String.valueOf(highest));
                            } else {
                                dexPoke.child("stats").child("satk").setValue(actual);
                            }
                        }
                    }
                },1000);
                showToast("Stat value invalid. Too low/high!");

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
                        if (nature.equalsIgnoreCase("sassy") | nature.equalsIgnoreCase("careful") | nature.equalsIgnoreCase("gentle") | nature.equalsIgnoreCase("calm")) {
                            lowest = (int) floor((floor(((2*baseStats.getSdef()+0)*level)/100)+5)*1.1);
                            highest = (int) floor((floor(((2*baseStats.getSdef()+31)*level)/100)+5)*1.1);
                        }
                        else if (nature.equalsIgnoreCase("naughty") | nature.equalsIgnoreCase("lax") | nature.equalsIgnoreCase("rash") | nature.equalsIgnoreCase("naive")) {
                            lowest = (int) floor((floor(((2*baseStats.getSdef()+0)*level)/100)+5)*0.9);
                            highest = (int) floor((floor(((2*baseStats.getSdef()+31)*level)/100)+5)*0.9);
                        }
                        else {
                            lowest = (int) floor((floor(((2*baseStats.getSdef()+0)*level)/100)+5)*1);
                            highest = (int) floor((floor(((2*baseStats.getSdef()+31)*level)/100)+5)*1);
                        }


                        if (!(sdefInput.getText().toString().trim().isEmpty())) {
                            int actual = parseInt(sdefInput.getText().toString());
                            if (actual < lowest) {
                                dexPoke.child("stats").child("sdef").setValue(lowest);
                                //showToast("Sp. Defense too low");
                                sdefInput.setText(String.valueOf(lowest));
                            } else if (actual > highest) {
                                dexPoke.child("stats").child("sdef").setValue(highest);
                                //showToast("Sp. Defense too high");
                                sdefInput.setText(String.valueOf(highest));
                            } else {
                                dexPoke.child("stats").child("sdef").setValue(actual);
                            }
                        }
                    }
                },1000);
                showToast("Stat value invalid. Too low/high!");

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
                        if (nature.equalsIgnoreCase("timid") | nature.equalsIgnoreCase("hasty") | nature.equalsIgnoreCase("jolly") | nature.equalsIgnoreCase("naive")) {
                            lowest = (int) floor((floor(((2*baseStats.getSpd()+0)*level)/100)+5)*1.1);
                            highest = (int) floor((floor(((2*baseStats.getSpd()+31)*level)/100)+5)*1.1);
                        }
                        else if (nature.equalsIgnoreCase("sassy") | nature.equalsIgnoreCase("quiet") | nature.equalsIgnoreCase("relaxed") | nature.equalsIgnoreCase("brave")) {
                            lowest = (int) floor((floor(((2*baseStats.getSpd()+0)*level)/100)+5)*0.9);
                            highest = (int) floor((floor(((2*baseStats.getSpd()+31)*level)/100)+5)*0.9);
                        }
                        else {
                            lowest = (int) floor((floor(((2*baseStats.getSpd()+0)*level)/100)+5)*1);
                            highest = (int) floor((floor(((2*baseStats.getSpd()+31)*level)/100)+5)*1);
                        }


                        if (!(spdInput.getText().toString().trim().isEmpty())) {
                            int actual = parseInt(spdInput.getText().toString());
                            if (actual < lowest) {
                                dexPoke.child("stats").child("spd").setValue(lowest);
                                //showToast("Sped too low");
                                spdInput.setText(String.valueOf(lowest));
                            } else if (actual > highest) {
                                dexPoke.child("stats").child("spd").setValue(highest);
                                //showToast("Speed too high");
                                spdInput.setText(String.valueOf(highest));
                            } else {
                                dexPoke.child("stats").child("spd").setValue(actual);
                            }
                        }
                    }
                },1000);
                showToast("Stat value invalid. Too low/high!");

            }
        });

        /* ===============================
         * Method for Individual Value (IV) calculation
         * The IV of a Pokemon's Stat can be a number from 0-31
         * This will calculate the potential IV for each stat based on the data the user has input, then the values for IVs are displayed on-screen
         * =============================== */
        statCalculation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HP = parseInt(hpInput.getText().toString());
                    ATK = parseInt(atkInput.getText().toString());
                    DEF = parseInt(defInput.getText().toString());
                    SPD = parseInt(spdInput.getText().toString());
                    SATK = parseInt(satkInput.getText().toString());
                    SDEF = parseInt(sdefInput.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    showToast("Stat values cannot be empty!");
                }

                ivHpDisplay.setText(Integer.toString(calculateHP(baseStats.getHp(), evStats.getHp(), HP, level)));
                ivAtkDisplay.setText(Integer.toString(calculateAtk(baseStats.getAtk(), evStats.getAtk(), ATK, level)));
                ivDefDisplay.setText(Integer.toString(calculateDef(baseStats.getDef(), evStats.getDef(), DEF, level)));
                ivSatkDisplay.setText(Integer.toString(calculateSatk(baseStats.getSatk(), evStats.getSatk(), SATK, level)));
                ivSdefDisplay.setText(Integer.toString(calculateSdef(baseStats.getSdef(), evStats.getSdef(), SDEF, level)));
                ivSpdDisplay.setText(Integer.toString(calculateSpd(baseStats.getSpd(), evStats.getSpd(), SPD, level)));
            }
        });
    }

    /* ===============================
     * Method which sets the values within each editable text box to the values from the database
     * =============================== */
    private void displayCurrStats() {
        hpInput.setText(currStats.getHp() + "");
        atkInput.setText(currStats.getAtk() + "");
        defInput.setText(currStats.getDef() + "");
        satkInput.setText(currStats.getSatk() + "");
        sdefInput.setText(currStats.getSdef() + "");
        spdInput.setText(currStats.getSpd() + "");
    }

    /* ===============================
     * Method called when the Add Button is pressed; Switches to the Add Pokemon Activity
     * =============================== */
    public void gotoAddView(View view) {
        Intent intent = new Intent(this, team_builder.class);
        startActivity(intent);
    }

    /* ===============================
     * Method called when the Dex Button is pressed; Switches to the general Pokedex Activity
     * =============================== */
    public void gotoDexView(View view) {
        Intent intent = new Intent(this, PokedexView.class);
        startActivity(intent);
    }

    /* ===============================
     * Method called when the News Button is pressed; Switches to the Newsfeed Activity
     * =============================== */
    public void gotoNewsView(View view) {
        Intent intent = new Intent(this, RSS_view.class);
        startActivity(intent);
    }

    /* ===============================
     * Method called when Team Button is pressed; Switches to the Personal Pokedex Activity
     * =============================== */
    public void gotoTeamView(View view) {
        Intent intent = new Intent(this, PersonalDex.class);
        startActivity(intent);
    }

    /* ===============================
     * Method called when the Settings Button is pressed; Switches to the Settings Activity
     * =============================== */
    public void gotoSettingsView(View view) {
        Intent intent = new Intent(this, Main_menu_view.class);
        startActivity(intent);
    }

    /* ===============================
     * Unknown what this method does, but it stays to prevent a crash
     * =============================== */
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /* ===============================
     * Method which calculates the IV of the Pokemon, given the information provided by the user
     * =============================== */
    private int calculateHP(int base, int ev, int stat, int level) {

        int iv = (int) ceil(((stat - 10 - level) *100) / level - 2 * baseStats.getHp() - ev/4);
        if (iv > 31)
            iv = 31;
        if (iv < 0)
            iv = 0;
        return iv;
    }
    /* ===============================
     * Method which calculates the IV of the Pokemon, given the information provided by the user
     * Attack, Defense, Special Attack, Special Defense and Speed are calculated by factoring in the positive and negative affects of the Pokemon's nature
     * =============================== */
    private int calculateAtk(int base, int ev, int stat, int level) {
        int iv;
        if (nature.equalsIgnoreCase("adamant") | nature.equalsIgnoreCase("lonely") | nature.equalsIgnoreCase("naughty") | nature.equalsIgnoreCase("brave")) {
            iv = (int) ceil(((stat/1.1 - 5) *100) / level - 2 * baseStats.getAtk() - ev/4);
        }
        else if (nature.equalsIgnoreCase("bold") | nature.equalsIgnoreCase("modest") | nature.equalsIgnoreCase("calm") | nature.equalsIgnoreCase("timid")) {
            iv = (int) ceil(((stat/0.9 - 5) *100) / level - 2 * baseStats.getAtk() - ev/4);
        }
        else {
            iv = (int) ceil(((stat/1 - 5) *100) / level - 2 * baseStats.getAtk() - ev/4);
        }


        if (iv > 31)
            iv = 31;
        if (iv < 0)
            iv = 0;
        return iv;
    }
    /* ===============================
     * Method which calculates the IV of the Pokemon, given the information provided by the user
     * Attack, Defense, Special Attack, Special Defense and Speed are calculated by factoring in the positive and negative affects of the Pokemon's nature
     * =============================== */
    private int calculateDef(int base, int ev, int stat, int level) {
        int iv;
        if (nature.equalsIgnoreCase("bold") | nature.equalsIgnoreCase("impish") | nature.equalsIgnoreCase("lax") | nature.equalsIgnoreCase("relaxed")) {
            iv = (int) ceil(((stat/1.1 - 5) *100) / level - 2 * baseStats.getDef() - ev/4);
        }
        else if (nature.equalsIgnoreCase("hasty") | nature.equalsIgnoreCase("gentle") | nature.equalsIgnoreCase("mild") | nature.equalsIgnoreCase("lonely")) {
            iv = (int) ceil(((stat/0.9 - 5) *100) / level - 2 * baseStats.getDef() - ev/4);
        }
        else {
            iv = (int) ceil(((stat/1 - 5) *100) / level - 2 * baseStats.getDef() - ev/4);
        }

        if (iv > 31)
            iv = 31;
        if (iv < 0)
            iv = 0;
        return iv;

    }
    /* ===============================
     * Method which calculates the IV of the Pokemon, given the information provided by the user
     * Attack, Defense, Special Attack, Special Defense and Speed are calculated by factoring in the positive and negative affects of the Pokemon's nature
     * =============================== */
    private int calculateSatk(int base, int ev, int stat, int level) {
        int iv;
        if (nature.equalsIgnoreCase("modest") | nature.equalsIgnoreCase("mild") | nature.equalsIgnoreCase("rash") | nature.equalsIgnoreCase("quiet")) {
            iv = (int) ceil(((stat/1.1 - 5) *100) / level - 2 * baseStats.getSatk() - ev/4);
        }
        else if (nature.equalsIgnoreCase("adamant") | nature.equalsIgnoreCase("impish") | nature.equalsIgnoreCase("careful") | nature.equalsIgnoreCase("jolly")) {
            iv = (int) ceil(((stat/0.9 - 5) *100) / level - 2 * baseStats.getSatk() - ev/4);
        }
        else {
            iv = (int) ceil(((stat/1 - 5) *100) / level - 2 * baseStats.getSatk() - ev/4);
        }


        if (iv > 31)
            iv = 31;
        if (iv < 0)
            iv = 0;
        return iv;
    }
    /* ===============================
     * Method which calculates the IV of the Pokemon, given the information provided by the user
     * Attack, Defense, Special Attack, Special Defense and Speed are calculated by factoring in the positive and negative affects of the Pokemon's nature
     * =============================== */
    private int calculateSdef(int base, int ev, int stat, int level) {
        int iv;
        if (nature.equalsIgnoreCase("calm") | nature.equalsIgnoreCase("gentle") | nature.equalsIgnoreCase("careful") | nature.equalsIgnoreCase("sassy")) {
            iv = (int) ceil(((stat/1.1 - 5) *100) / level - 2 * baseStats.getSdef() - ev/4);
        }
        else if (nature.equalsIgnoreCase("naive") | nature.equalsIgnoreCase("rash") | nature.equalsIgnoreCase("lax") | nature.equalsIgnoreCase("naughty")) {
            iv = (int) ceil(((stat/0.9 - 5) *100) / level - 2 * baseStats.getSdef() - ev/4);
        }
        else {
            iv = (int) ceil(((stat/1 - 5) *100) / level - 2 * baseStats.getSdef() - ev/4);
        }


        if (iv > 31)
            iv = 31;
        if (iv < 0)
            iv = 0;
        return iv;
    }
    /* ===============================
     * Method which calculates the IV of the Pokemon, given the information provided by the user
     * Attack, Defense, Special Attack, Special Defense and Speed are calculated by factoring in the positive and negative affects of the Pokemon's nature
     * =============================== */
    private int calculateSpd(int base, int ev, int stat, int level) {
        int iv;
        if (nature.equalsIgnoreCase("timid") | nature.equalsIgnoreCase("hasty") | nature.equalsIgnoreCase("jolly") | nature.equalsIgnoreCase("naive")) {
            iv = (int) ceil(((stat/1.1 - 5) *100) / level - 2 * baseStats.getSpd() - ev/4);
        }
        else if (nature.equalsIgnoreCase("brave") | nature.equalsIgnoreCase("relaxed") | nature.equalsIgnoreCase("quiet") | nature.equalsIgnoreCase("sassy")) {
            iv = (int) ceil(((stat/0.9 - 5) *100) / level - 2 * baseStats.getSpd() - ev/4);
        }
        else {
            iv = (int) ceil(((stat/1 - 5) *100) / level - 2 * baseStats.getSpd() - ev/4);
        }


        if (iv > 31)
            iv = 31;
        if (iv < 0)
            iv = 0;
        return iv;
    }
    /* ===============================
     * Method which displays a toast
     * =============================== */
    private void showToast(String t) {
        Toast.makeText(Individual_Pokemon_view.this, t, Toast.LENGTH_LONG).show();
    }

}


