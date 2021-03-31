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
                    Log.e("username", userName);

                    //Log.e("name", "user name: " + userName);
                    // this should return the number of pokemon the user currently have
                    // I am subtracting 2 because we populate each user with 2 dummy pokemon
                    long numInDex = snapshot.child("users").child(userName).child("pokedex").getChildrenCount();
                    Log.e("dex number", "number of pokemon counted: " + numInDex);
                    for (DataSnapshot ds: snapshot.child("users").child(userName).child("pokedex").getChildren()) {
                        currMonName = ds.getKey();
                        if (!currMonName.equals("pkm1"))
                            if (!currMonName.equals("pkm2"))
                                arrList.add(currMonName);
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
                    String temp = arrList.get(position);
                    Intent intent = new Intent(getBaseContext(), Individual_Pokemon_view.class);
                    intent.putExtra("nickname", temp);
                    Toast.makeText(PersonalDex.this, arrList.get(position) + "", Toast.LENGTH_LONG).show();
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
