package com.nouroeddinne.sweetsstore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Controlar.AdapterSaller;
import Controlar.PagerAdapter;
import Fragment.HomeFragment;
import Model.Model;
import Utel.UtelsDB;

public class HomeSallerActivity extends AppCompatActivity {

    ImageView add,profile;
    static FirebaseAuth auth;
    static DatabaseReference databaseReferencere;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    ArrayList<Model> dessertList= new ArrayList<Model>();
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_saller);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        add = findViewById(R.id.imageView_add);
        profile = findViewById(R.id.imageView_profile);
        recyclerView = findViewById(R.id.recyclerView);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferencere = firebaseDatabase.getReference();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dessertList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Model m = snapshot1.getValue(Model.class);
                    if (m.getBy() != null){
                        Log.d("TAG", "onDataChange: not null ");
                        if (m.getBy().equals(auth.getUid())){
                            dessertList.add(m);
                            Log.d("TAG", "onDataChange: add dessert ");
                        }
                    }
                }

                adapter = new AdapterSaller(HomeSallerActivity.this,dessertList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });










        add.setOnClickListener(v -> {
            Intent intent = new Intent(HomeSallerActivity.this, AddDessertActivity.class);
            startActivity(intent);
        });

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeSallerActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

    }





















}