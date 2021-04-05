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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.jar.Attributes;

public class PersonalDex extends AppCompatActivity {
    private final FirebaseDatabase fb = FirebaseDatabase.getInstance();
    DatabaseReference db = fb.getReference();
    // our listview again for display purposes
    ListView listView;

    ArrayList<Stats> pokemonStats = new ArrayList<>();

    // we will use an ArrayList in our listview because users can add and subtract pokemon, so the size must be changable
    ArrayList<String> arrList = new ArrayList<>();
    // array adapters are used to link Arrays and listviews
    ArrayAdapter<String> arrayAdapter;


    // this is the code for transitioning between views with the buttons on the bottom
    public void gotoAddView(View view) {
        Intent intent = new Intent(this, team_builder.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_dex);

        Context context = getApplicationContext();
        CharSequence dispText = "You are not currently logged in. Log in to add Pokemon to your Pokedex.";
        int duration = Toast.LENGTH_LONG;
        Toast notLoggedInToast = Toast.makeText(context, dispText, duration);

        listView = (ListView)findViewById(R.id.listViewPersonalDex);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrList);
        listView.setAdapter(arrayAdapter);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // if user is null, no one is logged in, if user isn't null, then someone is logged in
        if (user != null) {
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.e("data", "reading from personal dex");
                    String currMon;
                    String currMonName = "";
                    String userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    userName = userName.replace('.', ',');
                    //Log.e("username", userName);

                    //Log.e("name", "user name: " + userName);
                    // this should return the number of pokemon the user currently have
                    // I am subtracting 2 because we populate each user with 2 dummy pokemon
                    long numInDex = snapshot.child("users").child(userName).child("pokedex").getChildrenCount();
                    //Log.e("dex number", "number of pokemon counted: " + numInDex);
                    int temp = 1;
                    for (DataSnapshot ds: snapshot.child("users").child(userName).child("pokedex").getChildren()) {
                        currMonName = ds.getKey();
                        if (!currMonName.equals("pkm1"))
                            if (!currMonName.equals("pkm2")) {
                                // we will be making 2 array lists, one of the names for displaying and ...
                                // one of the stats of each pokemon so that we can pass that information along to the individual screen
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

                                pokemonStats.add(tempStats);
                                arrList.add("" + temp + ".\t" + currMonName);
                                temp++;

                            }
                    }
                    /*
                    for (int i = 0; i < arrList.size(); i++) {
                        String temp = arrList.get(i);
                        Log.e("error", temp);
                    }*/

                    listView = (ListView)findViewById(R.id.listViewPersonalDex);
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("error","error reading");
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // 1. Mewtwo
                    // 0123
                    String temp = arrList.get(position).substring(3);
                    Intent intent = new Intent(getBaseContext(), Individual_Pokemon_view.class);
                    intent.putExtra("pkmnHP",pokemonStats.get(position).getHp() + "");
                    intent.putExtra("pkmnSPD",pokemonStats.get(position).getSpd() + "");
                    intent.putExtra("pkmnATK",pokemonStats.get(position).getAtk() + "");
                    intent.putExtra("pkmnDEF",pokemonStats.get(position).getDef() + "");
                    intent.putExtra("pkmnSATK",pokemonStats.get(position).getSatk() + "");
                    intent.putExtra("pkmnSDEF",pokemonStats.get(position).getSdef() + "");

                    intent.putExtra("nickname", temp);
                    startActivity(intent);
                    //Toast.makeText(PersonalDex.this, arrList.get(position) + "", Toast.LENGTH_LONG).show();
                }
            });
        }
        else // user isn't logged in
            notLoggedInToast.show();
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
