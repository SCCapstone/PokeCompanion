package com.example.template;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.jar.Attributes;

// this class is coded by
public class PokedexView extends AppCompatActivity {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    ListView dex;
    ArrayList<String> arrList = new ArrayList<>();

    ArrayAdapter<String> arrAdapter;
    DatabaseReference dbRefBase = db.getReference().child("Pokemon");
    DataSnapshot snap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        populate();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedexview);
        dex = (ListView)findViewById(R.id.listviewtxt);
        arrAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, arrList);
        dex.setAdapter(arrAdapter);


    }
    private void populate() {
        // 'i' will represent our spot in the pokedex
        String currMon;
        String currMonName;
        long numInDex = snap.child("Pokemon").getChildrenCount();
        for (int i = 1; i < numInDex; i++) {
            // all pokemon have a number with three digits from 001 to currently 898
            // if that number is < 10, it has to be populated by a "00" to the left in order to
            // match up with our database, and so on for >10 but <100
            if(i < 10)
                currMon = "00" + i;
            else if(i < 100)
                currMon = "0" + i;
            else
                currMon = "" + i;
            // finds the name of the pokemon associated with the current number
            currMonName = (String)snap.child("Pokemon").child(currMon).getValue(true);
            // add that pokemon to the array list
            arrList.add(currMon + "\t" + currMonName);

        }
    }

}
