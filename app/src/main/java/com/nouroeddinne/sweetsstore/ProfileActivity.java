package com.nouroeddinne.sweetsstore;

import static Utel.UtelsDB.FIREBASE_TABLE_USERS;
import static Utel.UtelsDB.SHAREDPREFERNCES_FILENAME_NAME;
import static Utel.UtelsDB.SHAREDPREFERNCES_FILENAME_USER;
import static Utel.UtelsDB.SHAREDPREFERNCES_SALLER;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

import Model.ModelCart;
import Model.ModelProfile;
import Utel.UtelsDB;

public class ProfileActivity extends AppCompatActivity implements OnBackPressedDispatcherOwner {
    private OnBackPressedCallback callback;
    ImageView back;
    TextView name,textView_number_of_purches,textView_total_sepend;
    SharedPreferences sharedPreferences;
//    DataBaseAccess db = DataBaseAccess.getInstance(this);

    FirebaseAuth auth;
    DatabaseReference databaseReferencere;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    Switch saller;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        back = findViewById(R.id.imageView17);
        name = findViewById(R.id.textView_name);
        textView_total_sepend = findViewById(R.id.textView_total_sepend);
        textView_number_of_purches = findViewById(R.id.textView_number_of_purches);
        saller = findViewById(R.id.switch_saller);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferencere = firebaseDatabase.getReference();

        sharedPreferences = getSharedPreferences(SHAREDPREFERNCES_SALLER, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(SHAREDPREFERNCES_SALLER,false) != false) {
            saller.setChecked(true);
        }

        saller.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPreferences.edit().putBoolean(SHAREDPREFERNCES_SALLER, true).apply();
                } else {
                    sharedPreferences.edit().putBoolean(SHAREDPREFERNCES_SALLER, false).apply();
                }
                Intent intent = new Intent(ProfileActivity.this, SplashScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });


        databaseReferencere.child(FIREBASE_TABLE_USERS).child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ModelProfile profile = snapshot.getValue(ModelProfile.class);

                name.setText(profile.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



//
//
//        // update the number of purchess & total spend
//
//        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_USERS).child(auth.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CHECKOUT).child(auth.getUid()).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        int numberPurchases = (int) snapshot.getChildrenCount();
//                        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_USERS).child(auth.getUid()).child("numberPurchases").setValue(numberPurchases);
//
//
//                        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CHECKOUT).child(auth.getUid()).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                double totalSpend = 0.0;
//                                for (DataSnapshot checkoutSnapshot : snapshot.getChildren()){
//                                    if (checkoutSnapshot.child("total").getValue() != null){
//                                        totalSpend += Double.parseDouble(String.valueOf(checkoutSnapshot.child("total").getValue()));
//                                    }
//                                }
//                                Toast.makeText(ProfileActivity.this, "tot is "+totalSpend, Toast.LENGTH_SHORT).show();
//                                databaseReferencere.child(UtelsDB.FIREBASE_TABLE_USERS).child(auth.getUid()).child("totalSpend").setValue(totalSpend);
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
////                                    mp.setTotalSpend(totalSpend);
////
////                                    databaseReferencere.child(UtelsDB.FIREBASE_TABLE_USERS).child(auth.getUid()).setValue(mp).addOnCompleteListener(new OnCompleteListener<Void>() {
////                                        @Override
////                                        public void onComplete(@NonNull Task<Void> task) {
////                                            if (task.isSuccessful()) {
////                                                Toast.makeText(CartActivity.this, "Checkout successful!", Toast.LENGTH_SHORT).show();
////                                            } else {
////                                                Toast.makeText(CartActivity.this, "Failed ", Toast.LENGTH_SHORT).show();
////                                            }
////                                        }
////                                    });
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//
//
//
//
//





        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CHECKOUT).child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numberPurchases = (int) snapshot.getChildrenCount();
                textView_number_of_purches.setText(String.valueOf(numberPurchases));
                databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CHECKOUT).child(auth.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        double totalSpend = 0.0;
                        for (DataSnapshot checkoutSnapshot : snapshot.getChildren()){
                            if (checkoutSnapshot.child("totalSpend").getValue() != null){
                                totalSpend += Double.parseDouble(String.valueOf(checkoutSnapshot.child("totalSpend").getValue()));
                            }
                        }
                        textView_total_sepend.setText(String.valueOf(formatTotal(totalSpend)));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });










//        db.open();
//        ModelProfile profile =db.getPersonByEmail(HomeActivity.EMAIL);
//        db.close();


//        Log.d("TAG", "onCreate: "+profile.getId()+" "+profile.getName()+" "+profile.getEmail()+" "+profile.getPassword()+" "+profile.getNumberPurchases()+" "+profile.getTotalSpend());

//        if (profile == null){
//            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
//        }


        callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        };

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }



    public Double formatTotal(Double t){
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.valueOf(df.format(t));

    }
















}