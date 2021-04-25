package com.example.template;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

// this class was coded in its entirety by Jacob Letizia in March 2021
public class PersonalDex extends AppCompatActivity {
    private final FirebaseDatabase fb = FirebaseDatabase.getInstance();
    DatabaseReference db = fb.getReference();
    // our listview again for display purposes and searchbar for filtering that listview
    ListView listView;
    SearchView searchbar;

    // these arrays are used to make sure we can pass along information about the pokemon that we pull
    ArrayList<Stats> pokemonStats = new ArrayList<>();
    ArrayList<String> pokemonIDs = new ArrayList<>();
    // this array is to help make sure we keep everything in order after the array has been filtered
    // and the indexes have been all messed up
    String[][] pokemonNames = new String[151][2];
    int numPokemon = 0;

    // we will use an ArrayList in our listview because users can add and subtract pokemon, so the size must be changable
    ArrayList<String> arrList = new ArrayList<>();

    // array adapters are used to link Arrays and listviews
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_dex);

        // if the user isn't logged in, we will display this toast
        Context context = getApplicationContext();
        CharSequence dispText = "You are not currently logged in. Log in to add Pokemon to your Pokedex.";
        int duration = Toast.LENGTH_LONG;
        Toast notLoggedInToast = Toast.makeText(context, dispText, duration);

        // we will assign our objects here and set up the adapter
        listView = (ListView)findViewById(R.id.listViewPersonalDex);
        searchbar = (SearchView)findViewById(R.id.filtersearch);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, arrList);
        listView.setAdapter(arrayAdapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // if user is null, no one is logged in, if user isn't null, then someone is logged in
        if (user != null) {
            // this method accesses the database
            searchbar.setOnQueryTextListener(new OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    arrayAdapter.getFilter().filter(newText);
                    return false;
                }
            });

            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Log.e("data", "reading from personal dex");
                    String currMon;
                    String currMonName = "";
                    String userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                     userName = userName.replace('.', ',');


                    // this should return the number of pokemon the user currently have
                    // I am subtracting 2 because we populate each user with 2 dummy pokemon
                    long numInDex = snapshot.child("users").child(userName).child("pokedex").getChildrenCount();

                    // for each loop so that we can get to access each pokemon
                    for (DataSnapshot ds: snapshot.child("users").child(userName).child("pokedex").getChildren()) {
                        currMonName = ds.getKey();
                        if (!currMonName.equals("pkm1"))
                            if (!currMonName.equals("pkm2")) {
                                /* we will be making 2 array lists, one of the names for displaying and
                                one of the stats of each pokemon so that we can pass that information along to the individual screen*/
                                Stats tempStats = new Stats();
                                Long tempAtk = (long)ds.child("stats").child("atk").getValue();
                                tempStats.setAtk(tempAtk.intValue());
                                Long tempDef = (long)ds.child("stats").child("def").getValue();
                                tempStats.setDef(tempDef.intValue());
                                Long tempSpd = (long)ds.child("stats").child("spd").getValue();
                                tempStats.setSpd(tempSpd.intValue());
                                Long tempHp = (long)ds.child("stats").child("hp").getValue();
                                tempStats.setHp(tempHp.intValue());
                                Long tempSatk = (long)ds.child("stats").child("satk").getValue();
                                tempStats.setSatk(tempSatk.intValue());
                                Long tempSdef = (long)ds.child("stats").child("sdef").getValue();
                                tempStats.setSdef(tempSdef.intValue());

                                String tempID = (String)ds.child("number").getValue();
                                pokemonIDs.add(tempID);

                                pokemonStats.add(tempStats);
                                arrList.add(currMonName);
                                pokemonNames[numPokemon][0] = currMonName;
                                pokemonNames[numPokemon][1] = numPokemon + "";
                                numPokemon++;
                            }
                    }
                    listView = (ListView)findViewById(R.id.listViewPersonalDex);
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("error","error reading");
                }

                    //Log.e("name", individualName);
            });
            // when a pokemon is clicked, we will be passing in a lot of information
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String individualName = arrList.get(position);
                    //System.out.println(arrList.get(position));
                    /* similarly to in pokedex view, we want to pass along a lot of information I stored in a bunch of arrays,
                    however the index of those arrays is based on the list being unfiltered, so we
                    just need to find the index real quick through a helper array I constructed
                     */
                    int index = 0;
                    for (int i = 0; i < numPokemon; i++) {
                        String temp = pokemonNames[i][0];
                        if (temp == individualName) {
                            index = i;
                        }
                    }
                    String tempID = pokemonIDs.get(index);
                    Log.e("help", "pokemonName: " + individualName + "\npkmnID:" + tempID + "\nindex: " + index + "\nstats" + pokemonStats.get(index));
                    // we will be going to the Individual Pokemon view
                    Intent intent = new Intent(getBaseContext(), Individual_Pokemon_view.class);
                    /* we will be passing all the current stats for the pokemon to the next screen so that
                    they don't need to be loaded again */
                    intent.putExtra("pkmnHP",pokemonStats.get(index).getHp() + "");
                    intent.putExtra("pkmnSPD",pokemonStats.get(index).getSpd() + "");
                    intent.putExtra("pkmnATK",pokemonStats.get(index).getAtk() + "");
                    intent.putExtra("pkmnDEF",pokemonStats.get(index).getDef() + "");
                    intent.putExtra("pkmnSATK",pokemonStats.get(index).getSatk() + "");
                    intent.putExtra("pkmnSDEF",pokemonStats.get(index).getSdef() + "");



                    // nickname is the name of the pokemon that will be going into the next screen
                    intent.putExtra("nickname", individualName);
                    intent.putExtra("pictureID", "icon" + tempID);
                    startActivity(intent);
                    // Toast.makeText(PersonalDex.this, arrList.get(position) + "", Toast.LENGTH_LONG).show();
                }
            });
            // this code works for the click and hold option to delete
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    final String str = arrList.get(position).replaceAll("\\s", "");

                    db.child("users").child(str).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            FirebaseDatabase db =  FirebaseDatabase.getInstance();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String userName = user.getEmail().replace('.', ',');
                            DatabaseReference mRef = db.getReference().child("users").child(userName);
                            mRef.child("pokedex").child(str).removeValue();
                            Toast.makeText(PersonalDex.this, "Item is deleted", Toast.LENGTH_LONG);
                            finish();
                            startActivity(getIntent());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(PersonalDex.this, "Item delete cancel", Toast.LENGTH_LONG);
                        }
                    });
                    return false;
                }
            });

        }
        else // user isn't logged in
            notLoggedInToast.show();
    }

    // this is the code for transitioning between views with the buttons on the bottom
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
        //Intent intent = new Intent(this, Individual_Pokemon_view.class);
        //startActivity(intent);
    }

    public void gotoSettingsView(View view) {
        Intent intent = new Intent(this, Main_menu_view.class);
        startActivity(intent);
    }
}
