package com.example.kshitijverma.beat_my_recipie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import Model.User;

public class SetupAccountActivity extends AppCompatActivity {
    ImageView setup_account_image;
    EditText name;
    EditText username;
    EditText website;
    EditText bio;
    Button update_account_settings;
    private String user_id;
    String image_url="";
    Uri image_uri=null;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    FirebaseFirestore firebaseFirestore;
    Toolbar toolbar;
    StorageReference filestorage;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);
        setup_account_image=findViewById(R.id.setup_account_profile_image);
        name=findViewById(R.id.name);
        website=findViewById(R.id.website);
        bio=findViewById(R.id.bio);
        bio.setText("I love to Cook");
        username=findViewById(R.id.username);
        final HashMap<String,Object> user_details=new HashMap<>();
        update_account_settings=findViewById(R.id.setup_account_button);
        mAuth=FirebaseAuth.getInstance();
        context=getApplicationContext();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user==null)
        {
            Intent intent=new Intent(getApplicationContext(),SignUpLogin.class);
            startActivity(intent);
        }
        else
        {
            user_id=user.getUid();
        }
        toolbar=findViewById(R.id.setupAccount_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toHomeIntent=new Intent(SetupAccountActivity.this,MainActivity.class);
                startActivity(toHomeIntent);
                Log.i("checking finish","checking finish");
                finish();

            }
        });
        FirebaseDatabase.getInstance().getReference().child("users").child(user_id).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
                    User user=snapshot.getValue(User.class);
                    name.setText(user.getName());
                    bio.setText(user.getBio());
                    website.setText(user.getWebsite());
                    username.setText(user.getUsername());
                    if(snapshot.child("image_url").exists()) {
                        image_url=snapshot.child("image_url").getValue().toString();
                        if(isValidContextForGlide(context))
                        {
                            Glide.with(context)
                                    .load(image_url)
                                    .into(setup_account_image);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        update_account_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_details.clear();
                if(TextUtils.isEmpty(name.getText().toString()))
                {
                    Toast.makeText(SetupAccountActivity.this,"Please mention name",Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(username.getText().toString()))
                {
                    Toast.makeText(SetupAccountActivity.this,"Please mention your name",Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(image_uri!=null)
                    {
                        filestorage = FirebaseStorage.getInstance().getReference().child("user_images").
                                child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()+"."+"jpg");
                    StorageTask upload_task=filestorage.putFile(image_uri);
                    upload_task.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {
                            if(!task.isSuccessful())
                            {
                                throw task.getException();
                            }
                            else
                            {
                                return filestorage.getDownloadUrl();
                            }
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri download_url = task.getResult();
                            image_url = download_url.toString();
                            FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("image_url").setValue(image_url);

                        }
                    });
                    }
                    user_details.put("name",name.getText().toString());
                    user_details.put("user_id",user_id);
                    if(TextUtils.isEmpty(website.getText().toString()))
                    {
                        user_details.put("website","");
                    }
                    else
                    {
                        user_details.put("website",website.getText().toString());
                    }
                    user_details.put("username",username.getText().toString());
                    if(TextUtils.isEmpty(bio.getText().toString()))
                    {
                        user_details.put("bio","");
                    }
                    else
                    {
                        user_details.put("bio",bio.getText().toString());
                    }
                    databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
                    databaseReference.updateChildren(user_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(SetupAccountActivity.this,"Account Updated Successfully",Toast.LENGTH_LONG).show();
                                Intent toHomeIntent=new Intent(SetupAccountActivity.this,MainActivity.class);
                                startActivity(toHomeIntent);
                            }
                            else
                            {
                                Toast.makeText(SetupAccountActivity.this,"Error Updating values",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
        setup_account_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SetupAccountActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(SetupAccountActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(SetupAccountActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                } else {
                    BringImagePicker();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent toHomeIntent=new Intent(SetupAccountActivity.this,MainActivity.class);
        startActivity(toHomeIntent);
    }
    private void BringImagePicker() {

        CropImage.activity().setFixAspectRatio(true)
                .start( SetupAccountActivity.this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                image_uri = result.getUri();
                setup_account_image.setImageURI(image_uri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SetupAccountActivity.this, "Oops, error setting Picture. Please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }
    private String getFileExtension(Uri image_uri)
    {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(SetupAccountActivity.this.getContentResolver().getType(image_uri));
    }
    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }


}
