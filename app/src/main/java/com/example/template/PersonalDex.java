package com.example.template;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.jar.Attributes;

public class PersonalDex extends AppCompatActivity {


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
