package com.example.kshitijverma.beat_my_recipie;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Post_your_recipie#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Post_your_recipie extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String user_id;
    private FirebaseAuth mAuth;
    EditText recipe_name;
    ImageView dish_new_image;

    ProgressBar pd;
    EditText hrs_input;
    EditText mins_input;
    EditText servings;
    Button enter_ingredients;
    Button enter_steps;
    EditText chef_notes;
    Button review_post;
    Button new_post;
    String recipe_name_java;
    String mins_java;
    String hrs_java;
    String servings_java = null;
    String chef_note_java;
    Uri dish_image_uri = null;
    String download_image_url = null;
    TextView ingredients_textView;
    EditText ingredients_editText;
    Button ingredients_button;
    RelativeLayout relativeLayout;
    RelativeLayout addStepsRelative;
    EditText steps_editText;
    Button steps_button;
    LinearLayout steps_linearLayout;
    LinearLayout linearLayout;
    int count_steps=-1;
    StorageReference filestorage;
    int count_textview=-1;
    EditText ingredients_editTextDisplay;
    EditText steps_editTextDisplay;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Post_your_recipie() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Post_your_recipie.
     */
    // TODO: Rename and change types and number of parameters
    public static Post_your_recipie newInstance(String param1, String param2) {
        Post_your_recipie fragment = new Post_your_recipie();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        Log.i("Hello", user_id);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_your_recipie, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        pd=(ProgressBar)getView().findViewById(R.id.spin_kit_newPost);
        pd.setVisibility(View.INVISIBLE);
        recipe_name = (EditText) getView().findViewById(R.id.recipe_name_xml);
        dish_new_image = (ImageView) getView().findViewById(R.id.dish_newImage_xml);
        hrs_input = getView().findViewById(R.id.hrs_xml);
        mins_input = getView().findViewById(R.id.mins_xml);
        servings = getView().findViewById(R.id.people_it_can_serve_xml);
        enter_ingredients = getView().findViewById(R.id.enter_ingredients_button_xml);
        enter_steps = getView().findViewById(R.id.enter_steps_button_xml);
        chef_notes = getView().findViewById(R.id.chef_note_xml);
        review_post = getView().findViewById(R.id.review_post_xml);
        new_post = getView().findViewById(R.id.post_xml);
        ingredients_button=getView().findViewById(R.id.ingredients_button);
        ingredients_editText=getView().findViewById(R.id.ingredients_editText);
        ingredients_textView=getView().findViewById(R.id.ingredients_textView);
        relativeLayout=getView().findViewById(R.id.add_ingredients_relative);
        relativeLayout.setVisibility(View.GONE);
        ingredients_textView.setVisibility(View.GONE);
        linearLayout=getView().findViewById(R.id.ingredienst_linearLayout);
        linearLayout.setVisibility(View.GONE);
        addStepsRelative=getView().findViewById(R.id.add_steps_relative);
        addStepsRelative.setVisibility(View.GONE);
        steps_linearLayout=getView().findViewById(R.id.steps_linearLayout);
        steps_linearLayout.setVisibility(View.GONE);
        steps_editText=getView().findViewById(R.id.steps_editText);
        steps_button=getView().findViewById(R.id.steps_button);
        ingredients_editTextDisplay=getView().findViewById(R.id.ingredients_editTextDisplay);
        ingredients_editTextDisplay.setVisibility(View.GONE);
        steps_editTextDisplay=getView().findViewById(R.id.steps_editTextDisplay);


        dish_new_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                } else {
                    BringImagePicker();
                }
            }
        });
        enter_ingredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.VISIBLE);
            }
        });
        ingredients_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                linearLayout.setVisibility(View.VISIBLE);ingredients_editTextDisplay.setVisibility(View.VISIBLE);

                if(!TextUtils.isEmpty(ingredients_editText.getText().toString()))
                {
                    /*count_textview++;
                    EditText editText = new EditText(getActivity());
                    editText.setId(count_textview);
                    editText.setBackgroundColor(Color.TRANSPARENT);
                    editText.setPadding(0,2,0,0);
                    editText.setTextSize(16);
                    mEditTextList.add(editText);
                    linearLayout.addView(editText);
                    editText.setText(ingredients_editText.getText().toString());
                    ingredients_editText.setText("");

                     */
                    ingredients_editTextDisplay.setBackgroundColor(Color.TRANSPARENT);
                    ingredients_editTextDisplay.setTextSize(16);
                    String ingredientsString=ingredients_editTextDisplay.getText().toString();
                    ingredientsString=ingredientsString+"\n"+ingredients_editText.getText().toString();
                    ingredients_editTextDisplay.setText(ingredientsString);
                    ingredients_editText.setText("");
                }
            }
        });
        enter_steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStepsRelative.setVisibility(View.VISIBLE);
            }
        });
        steps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                steps_linearLayout.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(steps_editText.getText().toString()))
                {
                    /*
                    count_steps++;
                    EditText editText = new EditText(getActivity());
                    editText.setId(count_steps);
                    editText.setBackgroundColor(Color.TRANSPARENT);
                    editText.setPadding(0,2,0,0);
                    editText.setTextSize(16);
                    steps_linearLayout.addView(editText);
                    editText.setText(steps_editText.getText().toString());
                    steps_editText.setText("");

                     */
                    String stepsString=steps_editTextDisplay.getText().toString();
                    stepsString=stepsString+"\n"+steps_editText.getText().toString();
                    steps_editTextDisplay.setText(stepsString);
                    steps_editText.setText("");

                }
            }
        });
        new_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recipe_name_java = recipe_name.getText().toString();
                mins_java = mins_input.getText().toString();
                hrs_java = hrs_input.getText().toString();
                chef_note_java = chef_notes.getText().toString();
                servings_java = servings.getText().toString();
                if (TextUtils.isEmpty(recipe_name_java)) {
                    Toast.makeText(getActivity(), "Please Enter Recipe Name", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(mins_java)) {
                    Toast.makeText(getActivity(), "Please Enter Mins", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(hrs_java)) {
                    Toast.makeText(getActivity(), "Please Enter Hrs", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(chef_note_java)) {
                    Toast.makeText(getActivity(), "Please Enter Chef's notes", Toast.LENGTH_LONG).show();
                } else if (dish_image_uri == null) {
                    Toast.makeText(getActivity(), "Please Enter Image of dish", Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(ingredients_editTextDisplay.getText().toString())) {
                    Toast.makeText(getActivity(), "Please Enter atleast one ingredient", Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(steps_editTextDisplay.getText().toString())) {
                    Toast.makeText(getActivity(), "Please Enter atleast one step", Toast.LENGTH_LONG).show();
                }
                else{
                    Sprite wanderingCubes = new WanderingCubes();
                    pd.setIndeterminateDrawable(wanderingCubes);
                    pd.setVisibility(View.VISIBLE);
                    final HashMap<String, Object> post_hashmap=new HashMap<>();
                    filestorage = FirebaseStorage.getInstance().getReference().child("posts_images").child(System.currentTimeMillis()+"."+getFileExtension(dish_image_uri));
                    StorageTask upload_task=filestorage.putFile(dish_image_uri);
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
                            Uri download_url= task.getResult();
                            download_image_url=download_url.toString();
                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("posts");
                            String postId=ref.push().getKey();
                            Date c= Calendar.getInstance().getTime();
                            post_hashmap.clear();
                            post_hashmap.put("post_id", postId);
                            post_hashmap.put("Dish_name", recipe_name_java);
                            post_hashmap.put("user_id", user_id);
                            post_hashmap.put("image_url", download_image_url);
                            post_hashmap.put("hrs", hrs_java);
                            post_hashmap.put("mins", mins_java);
                            post_hashmap.put("datedesc",(-1*new Date().getTime()));
                            post_hashmap.put("servings", servings_java);
                            post_hashmap.put("date",c);
                            post_hashmap.put("ingredients",ingredients_editTextDisplay.getText().toString());
                            post_hashmap.put("steps",steps_editTextDisplay.getText().toString());
                            ref.child(postId).setValue(post_hashmap);
                            Toast.makeText(getActivity(),"Post Successful", Toast.LENGTH_LONG).show();
                            pd.setVisibility(View.INVISIBLE);
                            Intent toHomeIntent=new Intent(getActivity(),MainActivity.class);
                            startActivity(toHomeIntent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }




        });
    }

    private boolean isValidIngredients(ArrayList<EditText>mEditList) {

        for(int i=1;i<=mEditList.size();i++)
        {
            if(!TextUtils.isEmpty(mEditList.get(i).getText()))
            {
               return true;
            }
        }
        return false;
    }

    private void BringImagePicker() {

        CropImage.activity().setFixAspectRatio(true)
                .start(getContext(), this);
    }
    private String getFileExtension(Uri dish_image_uri)
    {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getActivity().getContentResolver().getType(dish_image_uri));
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                dish_image_uri = result.getUri();
                dish_new_image.setImageURI(dish_image_uri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getActivity(), "Oops, error setting Picture. Please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
