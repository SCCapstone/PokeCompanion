package com.example.template;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    FirebaseDatabase db =  FirebaseDatabase.getInstance();
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                updateUI(user);

                String userId = user.getUid();
                String userName = user.getEmail().replace('.', ',');
                DatabaseReference mRef = db.getReference().child("users").child(userName);
                mRef.child("uid").setValue(userId);
                mRef.child("pokedex").child("pkm1").child("species").setValue("Pikachu");
                mRef.child("pokedex").child("pkm2").setValue("dummy");

                //DatabaseReference mRef = db.getReference().child("users").push();
                //String userId = user.getUid();
                //mRef.setValue(userId);
                Toast.makeText(this, "sign in successful", Toast.LENGTH_LONG).show();
                // ...
            }
            else {
                Toast.makeText(this,"sign in failed",Toast.LENGTH_LONG).show();
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

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

    // temp UI update, will eventually take the user to a home screen where
    // they can access the other screens of the app
    public void updateUI(FirebaseUser account){
        if(account != null){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userId = user.getUid();
            String userName = user.getEmail().replace('.', ',');
            DatabaseReference mRef = db.getReference().child("users").child(userName);
            mRef.child("uid").setValue(userId);
            mRef.child("pokedex").child("pkm1").child("species").setValue("Pikachu");
            mRef.child("pokedex").child("pkm2").setValue("dummy");

            startActivity(new Intent(this, Main_menu_view.class));
        }
        else {
            Toast.makeText(this,"something went wrong",Toast.LENGTH_LONG).show();
        }
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
        Intent intent = new Intent(this, Individual_Pokemon_view.class);
        startActivity(intent);
    }

    public void gotoSettingsView(View view) {
        Intent intent = new Intent(this, Main_menu_view.class);
        startActivity(intent);
    }
}
