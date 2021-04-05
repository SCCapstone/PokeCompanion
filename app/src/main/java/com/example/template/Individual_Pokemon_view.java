/*
 * Sample Android app for Capstone Research Milestone
 * Weighted average Pokemon Stat calculator
 *      Written by Nicholas Mueller
 *
 *      This simple app takes a series of 6 numbers from the user
 *      these values represent a Pokemon's Individual Values, a
 *      set of numbers ranging from 0-31 which set a baseline for
 *      that Pokemon's respective manifested stat.
 *
 *      As an average, the number is meant to show a generalized value
 *      to represent the overall potential of the Pokemon; however, in nearly
 *      all cases, a Pokemon will only ever use either its Attack or Sp. Attack
 *      stat, whichever is higher. Therefore, by including an irrelevant number
 *      to a running average, the effective average stat is fluffed out to appear
 *      either worse or better than the Pokemon will become in practice.
 *
 *      This app makes a simple comparison to determine if the Attack and Sp. Attack
 *      differ by a factor greater than 15% of the greater stat. If so, the weaker stat
 *      is removed from the calculation, and the Pokemon's new weighted average stat is
 *      displayed.
 *
 */

package com.example.template;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import static java.lang.Integer.parseInt;
import static java.lang.Math.floor;

public class Individual_Pokemon_view extends AppCompatActivity {

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = db.getReference();
    FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    String userName = currUser.getEmail().replace('.', ',');
    DatabaseReference dbRefDex = db.getReference().child("users").child(userName).child("pokedex");
    DatabaseReference dbRefBase = db.getReference().child("Pokemon");
    String dexNum = "001";
    String pokemonID = "Bulbasaur";
    DatabaseReference basePoke = dbRefBase.child(dexNum);
    DatabaseReference dexPoke = dbRefDex.child(pokemonID);



    int HP, ATK, DEF, SPD, SATK, SDEF;
    double IRR_FACTOR = 0.85;

    pokemonUser displayMon;


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

    Stats currStats = new Stats(0,0,0,0,0,0);
    Stats baseStats = new Stats(0,0,0,0,0,0);
    Stats evStats = new Stats(0, 0, 0, 0,0,0);
    Stats ivStats = new Stats(0, 0, 0, 0,0,0);

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual__pokemon_view);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // whenever this page is accessed, we should get a nickname value from PersonalDex
        if((getIntent().getStringExtra("nickname"))!=null)
            pokemonID = getIntent().getStringExtra("nickname");

        // whenever this page is accessed, we should also get all the stats for the pokemon that we are currently looking at
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

        // the following chunk of code is for the sprite that will appear in the top left
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

        // assigning all of our views so that we can manipulate them later
        hpInput = (EditText)findViewById(R.id.hpInput);
        atkInput = (EditText)findViewById(R.id.atkInput);
        defInput = (EditText)findViewById(R.id.defenseInput);
        spdInput = (EditText)findViewById(R.id.spdInput);
        satkInput = (EditText)findViewById(R.id.satkInput);
        sdefInput = (EditText)findViewById(R.id.sdefInput);


        displayCurrStats();
         /* Commented out by JD to reduce data load from activity_individual__pokemon_view.xml
            2021 April 4
         baseHpDisplay = (TextView)findViewById(R.id.base_hp_num);
         baseAtkDisplay = (TextView)findViewById(R.id.base_atk_num);
         baseDefDisplay = (TextView)findViewById(R.id.base_def_num);
         baseSatkDisplay = (TextView)findViewById(R.id.base_satk_num);
         baseSdefDisplay = (TextView)findViewById(R.id.base_sdef_num);
         baseSpdDisplay = (TextView)findViewById(R.id.base_speed_num);
         */

         ivHpDisplay = (TextView)findViewById(R.id.iv_hp_num);
         ivAtkDisplay = (TextView)findViewById(R.id.iv_atk_num);
         ivDefDisplay = (TextView)findViewById(R.id.iv_def_num);
         ivSatkDisplay = (TextView)findViewById(R.id.iv_satk_num);
         ivSdefDisplay = (TextView)findViewById(R.id.iv_sdef_num);
         ivSpdDisplay = (TextView)findViewById(R.id.iv_speed_num);

        statCalculation = (Button)findViewById(R.id.calcStats);

        dispName = (TextView) findViewById(R.id.pokemon_name);


        // accessing the user's stored pokemon information and getting the base stats
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snap = snapshot;
                dexNum = String.valueOf(snap.child("users").child(userName).child("pokedex").child(pokemonID).child("number").getValue());
                basePoke = dbRefBase.child(dexNum);

                // reading all the base stats from the database and converting them from longs to ints
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Individual_Pokemon_view.this,"Error: "+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        basePoke.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                baseStats = snapshot.child("baseStats").getValue(Stats.class);
                //displayBaseStats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Individual_Pokemon_view.this,"Error: "+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });



        displayCurrStats();
        //showToast("Current HP: "+ Integer.toString(currStats.getHp()));
        dispName.setText(pokemonID);




        hpInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    dexPoke.child("stats").child("hp").setValue(parseInt(hpInput.getText().toString()));
                } catch (NumberFormatException e) {
                    dexPoke.child("stats").child("hp").setValue(0);
                }
            }
        });
        atkInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    dexPoke.child("stats").child("atk").setValue(parseInt(atkInput.getText().toString()));
                } catch (NumberFormatException e) {
                    dexPoke.child("stats").child("atk").setValue(0);
                }
            }
        });
        defInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    dexPoke.child("stats").child("def").setValue(parseInt(defInput.getText().toString()));
                } catch (NumberFormatException e) {
                    dexPoke.child("stats").child("def").setValue(0);
                }
            }
        });
        satkInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    dexPoke.child("stats").child("satk").setValue(parseInt(satkInput.getText().toString()));
                } catch (NumberFormatException e) {
                    dexPoke.child("stats").child("satk").setValue(0);
                }
            }
        });
        sdefInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    dexPoke.child("stats").child("sdef").setValue(parseInt(sdefInput.getText().toString()));
                } catch (NumberFormatException e) {
                    dexPoke.child("stats").child("sdef").setValue(0);
                }
            }
        });
        spdInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    dexPoke.child("stats").child("spd").setValue(parseInt(spdInput.getText().toString()));
                } catch (NumberFormatException e) {
                    dexPoke.child("stats").child("spd").setValue(0);
                }
            }
        });

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

                ivHpDisplay.setText(Integer.toString(calculateHP(baseStats.getHp(), evStats.getHp(), HP, 50)));
                ivAtkDisplay.setText(Integer.toString(calculateIV(baseStats.getAtk(), evStats.getAtk(), ATK, 50)));
                ivDefDisplay.setText(Integer.toString(calculateIV(baseStats.getDef(), evStats.getDef(), DEF, 50)));
                ivSatkDisplay.setText(Integer.toString(calculateIV(baseStats.getSatk(), evStats.getSatk(), SATK, 50)));
                ivSdefDisplay.setText(Integer.toString(calculateIV(baseStats.getSdef(), evStats.getSdef(), SDEF, 50)));
                ivSpdDisplay.setText(Integer.toString(calculateIV(baseStats.getSpd(), evStats.getSpd(), SPD, 50)));
            }
        });
        /*chooseGrowlithe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                basePoke = dbRefBase.child("058");
                dexPoke = dbRefDex.child("growlithe");
                currStats = snap.child("users").child("user_test").child("pokedex").child("growlithe").child("stats").getValue(Stats.class);
                baseStats = snap.child("Pokemon").child("058").child("baseStats").getValue(Stats.class);
                try {
                    hpInput.setText(Integer.toString(currStats.getHp()));
                    atkInput.setText(Integer.toString(currStats.getAtk()));
                    defInput.setText(Integer.toString(currStats.getDef()));
                    satkInput.setText(Integer.toString(currStats.getSatk()));
                    sdefInput.setText(Integer.toString(currStats.getSdef()));
                    spdInput.setText(Integer.toString(currStats.getSpd()));
                } catch (NullPointerException e) {
                    showToast("Null exception");
                }
                //showToast("Current HP: "+ Integer.toString(currStats.getHp()));
                dispName.setText(R.string.growlithe);
                displayBaseStats();
                //showToast("HP: " + Integer.toString(baseStats.getHp()));
            }
        });
        chooseStaryu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                basePoke = dbRefBase.child("120");
                dexPoke = dbRefDex.child("staryu");
                currStats = snap.child("users").child("user_test").child("pokedex").child("staryu").child("stats").getValue(Stats.class);
                baseStats = snap.child("Pokemon").child("120").child("baseStats").getValue(Stats.class);
                try {
                    hpInput.setText(Integer.toString(currStats.getHp()));
                    atkInput.setText(Integer.toString(currStats.getAtk()));
                    defInput.setText(Integer.toString(currStats.getDef()));
                    satkInput.setText(Integer.toString(currStats.getSatk()));
                    sdefInput.setText(Integer.toString(currStats.getSdef()));
                    spdInput.setText(Integer.toString(currStats.getSpd()));
                } catch (NullPointerException e) {
                    showToast("Null exception");
                }


                dispName.setText(R.string.staryu);
                displayBaseStats();
                //showToast("HP: " + Integer.toString(baseStats.getHp()));

            }
        });
        chooseCharizard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                basePoke = dbRefBase.child("006");
                dexPoke = dbRefDex.child("charizard");
                currStats = snap.child("users").child("user_test").child("pokedex").child("charizard").child("stats").getValue(Stats.class);
                baseStats = snap.child("Pokemon").child("006").child("baseStats").getValue(Stats.class);
                try {
                    hpInput.setText(Integer.toString(currStats.getHp()));
                    atkInput.setText(Integer.toString(currStats.getAtk()));
                    defInput.setText(Integer.toString(currStats.getDef()));
                    satkInput.setText(Integer.toString(currStats.getSatk()));
                    sdefInput.setText(Integer.toString(currStats.getSdef()));
                    spdInput.setText(Integer.toString(currStats.getSpd()));
                } catch (NullPointerException e) {
                    showToast("Null exception");
                }
                dispName.setText(R.string.charizard);
                displayBaseStats();
                //showToast("HP: " + Integer.toString(baseStats.getHp()));

            }
        });
        chooseMew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                basePoke = dbRefBase.child("151");
                dexPoke = dbRefDex.child("mew");
                currStats = snap.child("users").child("user_test").child("pokedex").child("mew").child("stats").getValue(Stats.class);
                baseStats = snap.child("Pokemon").child("151").child("baseStats").getValue(Stats.class);
                try {
                    hpInput.setText(Integer.toString(currStats.getHp()));
                    atkInput.setText(Integer.toString(currStats.getAtk()));
                    defInput.setText(Integer.toString(currStats.getDef()));
                    satkInput.setText(Integer.toString(currStats.getSatk()));
                    sdefInput.setText(Integer.toString(currStats.getSdef()));
                    spdInput.setText(Integer.toString(currStats.getSpd()));
                } catch (NullPointerException e) {
                    showToast("Null exception");
                }
                dispName.setText(R.string.mew);
                displayBaseStats();
                //showToast("HP: " + Integer.toString(baseStats.getHp()));
            }
        });*/
    }

    private void displayCurrStats() {
        hpInput.setText(currStats.getHp() + "");
        atkInput.setText(currStats.getAtk() + "");
        defInput.setText(currStats.getDef() + "");
        satkInput.setText(currStats.getSatk() + "");
        sdefInput.setText(currStats.getSdef() + "");
        spdInput.setText(currStats.getSpd() + "");
    }

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

    private int calculateHP(int base, int ev, int stat, int level) {
        int iv = (int) floor(((stat - 10 - level) *100) / level - 2 * base - ev/4);
        if (iv > 31)
            iv = 31;
        if (iv < 0)
            iv = 0;
        return iv;
    }
    private int calculateIV(int base, int ev, int stat, int level) {
        int iv = (int) floor(((stat - 5) *100) / level - 2 * base - ev/4);
        if (iv > 31)
            iv = 31;
        if (iv < 0)
            iv = 0;
        return iv;
    }

    private void displayBaseStats() {
        baseHpDisplay.setText(Integer.toString(baseStats.getHp()));
        baseAtkDisplay.setText(Integer.toString(baseStats.getAtk()));
        baseDefDisplay.setText(Integer.toString(baseStats.getDef()));
        baseSatkDisplay.setText(Integer.toString(baseStats.getSatk()));
        baseSdefDisplay.setText(Integer.toString(baseStats.getSdef()));
        baseSpdDisplay.setText(Integer.toString(baseStats.getSpd()));
    }




    public int weightedAverage(int hp, int atk, int def, int spd, int satk, int sdef) {
        int avg = 0;
        int sum = 0;
        int count = 6;
        if (satk < Math.round(atk * IRR_FACTOR)) {
            count = 5;
            sum = hp + atk + def + spd + sdef;
        }
        else if (atk < Math.round(satk * IRR_FACTOR)) {
            count = 5;
            sum = hp + def + spd + satk + sdef;
        }
        else {
            sum = hp + atk + def + spd + satk + sdef;
        }
        avg = sum / count;

        return avg;
    }
    private void showToast(String t) {
        Toast.makeText(Individual_Pokemon_view.this, t, Toast.LENGTH_LONG).show();
    }

}


