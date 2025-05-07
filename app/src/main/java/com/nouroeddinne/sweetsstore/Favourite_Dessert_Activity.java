package com.nouroeddinne.sweetsstore;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import Controlar.AdapterFavorate;
import Model.Model;
import Utel.UtelsDB;

public class Favourite_Dessert_Activity extends AppCompatActivity implements OnBackPressedDispatcherOwner {
    private OnBackPressedCallback callback;
    ImageView back;
    RecyclerView recyclerView;
    AdapterFavorate adapter;
    ArrayList<Model> idDessertList = new ArrayList<>();
    ArrayList<String> DessertList = new ArrayList<>();
    //DataBaseAccess db = DataBaseAccess.getInstance(this);

    FirebaseAuth auth;
    DatabaseReference databaseReferencere;
    DatabaseReference databaseReferencere2;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_dessert);


        back = findViewById(R.id.imageView17);
        recyclerView = findViewById(R.id.recyclerView);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferencere = firebaseDatabase.getReference();
        databaseReferencere2 = firebaseDatabase.getReference();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

//        db.open();
//        dessertList=db.getFavorateDESSERT();
//        db.close();

        loadData();


        Toast.makeText(this, ""+idDessertList.size(), Toast.LENGTH_SHORT).show();

//        adapter = new AdapterFavorate(this, idDessertList);
//        recyclerView.setAdapter(adapter);


        callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(Favourite_Dessert_Activity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        };


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Favourite_Dessert_Activity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }


    private void loadData() {

        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_FAVORATE).child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {

                for (DataSnapshot snapshotIdModel : snapshot2.getChildren()) {
                    String idModel = snapshotIdModel.getKey();
                    //DessertList.add(idModel);
                    Log.d("TAG", "loadData onDataChange 1 : "+idModel);

                    if (idModel != null){
                        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT).child(idModel).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshotM) {

                                try {

                                    Log.d("TAG", "loadData onDataChange 2 : "+snapshotM);
                                    Model m = snapshotM.getValue(Model.class);
                                    idDessertList.add(m);
                                    adapter = new AdapterFavorate(Favourite_Dessert_Activity.this, idDessertList);
                                    recyclerView.setAdapter(adapter);

                                } catch (Exception e) {
                                    Log.e("TAG", "Error parsing model", e);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





//
//        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_FAVORATE).child(auth.getUid()).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Log.d("Firebase", "onChildAdded loadData: "+snapshot.getKey());
//                idDessertList.add(snapshot.getKey());
//                adapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        Toast.makeText(this, ""+idDessertList.size(), Toast.LENGTH_SHORT).show();


    }


}