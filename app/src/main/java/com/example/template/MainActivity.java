package com.example.template;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    private FirebaseAuth mAuth;
    //establishes which providers are being used with Firebase to authenticate users
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //establishes an instance of Firebase
        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.login_screen)
                .setGoogleButtonId(R.id.googleSignIn)
                .setEmailButtonId(R.id.emailSignIn)
                .build();

        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setAuthMethodPickerLayout(customLayout)
                        .build(),
                RC_SIGN_IN);
        
    }

    //defines a reference to Firebase to store user information later on
    FirebaseDatabase db =  FirebaseDatabase.getInstance();
    //runs after the login activity has completed
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //checks to see if the user has signed in
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in, returns the user information store in Firebase authentication process(ie. email/uid)
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                //creates a node in the database for the user to store their unique pokemon and stats
                String userId = user.getUid();
                //replaces all '.' with ',' before storing email in database because Firebase does not like commas
                String userName = user.getEmail().replace('.', ',');
                DatabaseReference mRef = db.getReference().child("users").child(userName);
                mRef.child("uid").setValue(userId);
                //makes test dummy pokemon for testing with Nick/Jacob's code
                mRef.child("pokedex").child("pkm1").child("species").setValue("Pikachu");
                mRef.child("pokedex").child("pkm2").setValue("dummy");
                //takes user to the RSS feed/home screen
                Toast.makeText(this, "sign in successful", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, RSS_view.class));
            }
            //gives an error code and lets the user try to login again
            else {
                Toast.makeText(this,"sign in failed: error code " + response.getError().getErrorCode(),Toast.LENGTH_LONG).show();
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    //checks to see if a user is still logged in when the app is opened, and if so, takes them straight to the RSS view
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            updateUI(currentUser);
        }
        else {
            super.onStart();
        }
    }

    //method for updating the ui if the user has already been logged in when the app is opened
    public void updateUI(FirebaseUser account){
        if(account != null){
            //adds user info to database if for some reason it was not added before
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userId = user.getUid();
            String userName = user.getEmail().replace('.', ',');
            DatabaseReference mRef = db.getReference().child("users").child(userName);
            mRef.child("uid").setValue(userId);
            mRef.child("pokedex").child("pkm1").child("species").setValue("Pikachu");
            mRef.child("pokedex").child("pkm2").setValue("dummy");

            startActivity(new Intent(this, RSS_view.class));
        }
        else {
            Toast.makeText(this,"Error signing in",Toast.LENGTH_LONG).show();
        }
    }

    // this is the code for transitioning between views with the buttons on the bottom of the screen
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
}
