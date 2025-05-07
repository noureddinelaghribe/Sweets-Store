package com.nouroeddinne.sweetsstore;

import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;


import Controlar.AdapterSpinner;
import Fragment.HomeFragment;
import Model.Model;
import Utel.UtelsDB;

public class AddDessertActivity extends AppCompatActivity {

    Spinner spinner;
    ArrayList<String> listType = new ArrayList<>(Arrays.asList("Chosse Catigory","capcake","dounet","cookies","candy"));
    AdapterSpinner adapterSpinner;

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferencere;

    FirebaseStorage fs;
    StorageReference storageReference;

    EditText name,price;
    Button button;
    ImageView imageView;

    String type;

    boolean img = false;
    Uri imageUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_dessert);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferencere = firebaseDatabase.getReference();

        fs = FirebaseStorage.getInstance();
        storageReference = fs.getReference();

        name = findViewById(R.id.editTextText_name_dessert);
        price = findViewById(R.id.editTextText_price_dessert);
        button = findViewById(R.id.button_add_dessert);
        spinner = findViewById(R.id.spinner);
        imageView = findViewById(R.id.imageView12);


        adapterSpinner = new AdapterSpinner(this, listType);
        spinner.setAdapter(adapterSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = listType.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                choseImg();

            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (name.getText().toString() != null && !name.getText().toString().isEmpty()){
                    if (price.getText().toString() != null && !price.getText().toString().isEmpty()){
                        if (type != null && !type.isEmpty()){

                            addDessert(name.getText().toString(),price.getText().toString(),type);

                        }else {
                            Toast.makeText(AddDessertActivity.this, "Plase chosse type", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        price.setError("Price enter price like '0.99' ");
                    }
                }else {
                    name.setError("name should be between 3 and 30 characters");
                }

            }
        });






















    }



    public void choseImg(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launchResult.launch(intent);
    }


    ActivityResultLauncher<Intent> launchResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {

                    if (o!=null&&o.getResultCode()==RESULT_OK){
                        imageUri = o.getData().getData();
                        Picasso.get().load(imageUri).into(imageView);
                        img = true;
                    }else {
                        img = false;
                    }

                }
            });


    public void addDessert(String name, String price,String type){

        final String[] urlImg = new String[1];

        String key = databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT).push().getKey();

        if (img){
            UUID randomID = UUID.randomUUID();
            String image = "images/"+randomID+".jpg";
            storageReference.child(image).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference mystorageReference = fs.getReference(image);
                    mystorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            urlImg[0] = uri.toString();
                            Model m = new Model(key,auth.getUid(),name,price,urlImg[0],type);
                            databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT).child(key).setValue(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("TAGs", "onComplete: "+key);
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(AddDessertActivity.this,HomeSallerActivity.class);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(AddDessertActivity.this, "faild", LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }else {
            Toast.makeText(this, "Plase chose Image", LENGTH_SHORT).show();
        }

    }




































}