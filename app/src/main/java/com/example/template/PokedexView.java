package com.example.template;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

// this class was coded in its entirety by Jacob Letizia in March 2021
public class PokedexView extends AppCompatActivity {
    private final FirebaseDatabase fb = FirebaseDatabase.getInstance();
    DatabaseReference db = fb.getReference();
    // the actual thing being displayed
    ListView listView;
    // this is the array list we will be passing onto the ListView item
    ArrayList<String> arrList = new ArrayList<>();
    // there can only be a maximum of 3 ablities
    String[][] abilities = new String[160][3];

    // Array Adapter lets ListView find the array list it is supposed to display
    ArrayAdapter<String> arrAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // first establish a bunch of variables
        setContentView(R.layout.activity_pokedexview);
        listView = (ListView)findViewById(R.id.listviewtxt);
        arrAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, arrList);
        listView.setAdapter(arrAdapter);

        // this function will load the pokemon into a local array so that they can be displayed
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("data", "reading from pokedex");
                // 'i' will represent our spot in the pokedex
                String currMon;
                String currMonName;
                //long numInDex = snapshot.child("Pokemon").getChildrenCount();
                //Log.e("number", numInDex + " this is the number it is going until");
                for (int i = 1; i <= 151; i++) {
                    /* all pokemon have a number with three digits from 001 to currently 898
                    if that number is < 10, it has to be populated by a "00" to the left in order to
                    match up with our database, and so on for >10 but <100 */
                    if(i < 10)
                        currMon = "00" + i;
                    else if(i < 100)
                        currMon = "0" + i;
                    else
                        currMon = "" + i;
                    // finds the name of the pokemon associated with the current number
                    // every pokemon has at least 1 ability, so we don't have to check for the first one
                    abilities[i][0] = (String)snapshot.child("Pokemon").child(currMon).child("abilities").child("ab1").getValue();
                    // check if the 2nd ability exists before reading from it
                    if (snapshot.child("Pokemon").child(currMon).child("abilities/ab2").exists()) {
                        abilities[i][1] = (String)snapshot.child("Pokemon").child(currMon).child("abilities").child("ab2").getValue();
                    }
                    else
                        abilities[i][1] = "none";
                    // check if the hidden ability exists before reading from it
                    if (snapshot.child("Pokemon").child(currMon).child("abilities/abh").exists()) {
                        abilities[i][2] = (String)snapshot.child("Pokemon").child(currMon).child("abilities").child("abh").getValue();
                    }
                    else
                        abilities[i][2] = "none";

                    currMonName = (String)snapshot.child("Pokemon").child(currMon).child("Name").getValue();
                    currMonName = (currMonName.substring(0,1).toUpperCase()) + currMonName.substring(1);
                    // add that pokemon to the array list
                    arrList.add(currMon + "\t" + currMonName);
                }
                listView = (ListView)findViewById(R.id.listviewtxt);
                listView.setAdapter(arrAdapter);
                // NDSC lets the array know that it has new values and that it should be displayed
                arrAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PokedexView.this,"Error: "+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        listView = (ListView)findViewById(R.id.listviewtxt);
        arrAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, arrList);
        listView.setAdapter(arrAdapter);
        arrAdapter.notifyDataSetChanged();

        // this occurs when the list is clicked, should route to add pokemon view of that pokemon
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String temp = arrList.get(position).substring(4);

               String tempID = arrList.get(position).substring(0,3);
               Log.e("tempID",tempID);
               /* we will be passing some information to the add pokemon view
               so that the database doesn't have to be read again */
               int pokemonID = Integer.parseInt(arrList.get(position).substring(0,3));
               String[] possibleAbilities = abilities[pokemonID];
               Intent intent = new Intent(getBaseContext(), team_builder.class);
               intent.putExtra("pokemon", temp);
               intent.putExtra("abilities", possibleAbilities);
               intent.putExtra("pokemonID", tempID);

               startActivity(intent);
           }
        });
    }

    // this is the code for transitioning between views with the buttons on the bottom
    public void gotoAddView(View view) {
        Intent intent = new Intent(this, team_builder.class);
        startActivity(intent);
    }

    public void gotoDexView(View view) {
        //Intent intent = new Intent(this, PokedexView.class);
        //startActivity(intent);
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
