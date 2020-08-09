package com.example.kshitijverma.beat_my_recipie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.varunest.sparkbutton.SparkButton;

import Model.User;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN=123;
    private Button googlesigninbutton;
    private FirebaseAuth mAuth;
    DatabaseReference testing_d;
    private NavigationView navigationView;
    ImageView userProfileImage;
    TextView userName;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ImageView notification_button;

// ...
// Initialize Firebase Auth

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user==null)
        {
            Intent intent=new Intent(getApplicationContext(),SignUpLogin.class);
            startActivity(intent);
        }
        else {
            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.child("name").exists()) {
                                Intent toAccountSettingsIntent = new Intent(getApplicationContext(), SetupAccountActivity.class);
                                startActivity(toAccountSettingsIntent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            Intent intent = new Intent(getApplicationContext(), SignUpLogin.class);
            startActivity(intent);
        } else {
            notification_button=findViewById(R.id.notification_button);
            navigationView = findViewById(R.id.navigation_view);
            userProfileImage = navigationView.getHeaderView(0).findViewById(R.id.user_profile_imageheader);
            userName = navigationView.getHeaderView(0).findViewById(R.id.user_name_header);
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (snapshot.child("image_url").exists()) {
                            Glide.with(MainActivity.this)
                                    .load(user.getImage_url())
                                    .into(userProfileImage);
                        }
                        userName.setText(user.getName());
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            final DrawerLayout drawerLayout = findViewById(R.id.drawerlayout);
            findViewById(R.id.image_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
            NavigationView navigationView = findViewById(R.id.navigation_view);
            navigationView.setItemIconTintList(null);
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupWithNavController(navigationView, navController);
            mAuth = FirebaseAuth.getInstance();
            createRequest();

            testing_d = FirebaseDatabase.getInstance().getReference().child("explore_adapter_class").child("c1");
            testing_d.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String dish_testing = snapshot.child("dish").getValue().toString();
                    Log.i("testing database", "Test Successful" + dish_testing);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("explore_adapter_class");
            notification_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toNotificationIntent=new Intent(MainActivity.this,notification_activity.class);
                    startActivity(toNotificationIntent);
                }
            });


        }
    }
    private void createRequest()
    {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Intent intent=new Intent(getApplicationContext(),SignUpLogin.class);
                            startActivity(intent);
                        }

                        // ...
                    }
                });
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
