package com.nouroeddinne.sweetsstore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import Model.Model;
import Model.ModelCart;
import Utel.UtelsDB;

public class ShowDessertActivity extends AppCompatActivity implements OnBackPressedDispatcherOwner {
    private OnBackPressedCallback callback;
    ImageView imgDessert,imgFavorate,back;
    TextView textName,textPrice,textDescription,textIngredients,textTotal;
    Button button1,button3,button5,button10,buttonAddToCart;
    EditText editTextCustom;
    private int itemCount = 0 ;
    Double total = 0.0;
    Model model;
    boolean newStatus = false;

    //DataBaseAccess db = DataBaseAccess.getInstance(this);
    //Model model;

    FirebaseAuth auth;
    DatabaseReference databaseReferencere;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_dessert);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgDessert = findViewById(R.id.imageView14_show_img);
        imgFavorate = findViewById(R.id.imageView15_show_favorate);
        back = findViewById(R.id.imageView17);
        textName = findViewById(R.id.textView10_show_name);
        textPrice = findViewById(R.id.textView22_show_price);
        textTotal = findViewById(R.id.textView14);
        textDescription = findViewById(R.id.textView21__show_description);
        textIngredients = findViewById(R.id.textView26_show_ingredients);
        button1 = findViewById(R.id.button_show_count_1);
        button3 = findViewById(R.id.button_show_count_3);
        button5 = findViewById(R.id.button_show_count_5);
        button10 = findViewById(R.id.button_show_count_10);
        editTextCustom = findViewById(R.id.editText_show_count);
        buttonAddToCart = findViewById(R.id.button);

        button1.setBackgroundResource(R.drawable.backgroun_item_cart);
        button3.setBackgroundResource(R.drawable.backgroun_item_cart);
        button5.setBackgroundResource(R.drawable.backgroun_item_cart);
        button10.setBackgroundResource(R.drawable.backgroun_item_cart);

        DecimalFormat df = new DecimalFormat("#.##");

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferencere = firebaseDatabase.getReference();

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ShowDessertActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        };

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowDessertActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });


        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            model = extras.getParcelable("model");

//            db.open();
//
            Glide.with(this).load(model.getImg()).into(imgDessert);
            textName.setText(model.getName());
            textPrice.setText(model.getPrice());



            databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_FAVORATE).child(auth.getUid())
                    .child(model.getid()).child("id").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.getValue()!=null && snapshot.getValue().toString().equals(model.getid())){
                        imgFavorate.setImageResource(R.drawable.favorite_full);
                        newStatus = true;
                    }else{
                        imgFavorate.setImageResource(R.drawable.favorite_empty);
                        newStatus = false;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });





//            if (db.getDessertById(p).getFavorite()){
//                imgFavorate.setImageResource(R.drawable.favorite_full);
//            }else {
//                imgFavorate.setImageResource(R.drawable.favorite_empty);
//            }

//
//            model = new Model(
//                    db.getDessertById(p).getId(),
//                    db.getDessertById(p).getName(),
//                    db.getDessertById(p).getPrice(),
//                    db.getDessertById(p).getType(),
//                    db.getDessertById(p).getImg(),
//                    db.getDessertById(p).getFavorite(),
//                    db.getDessertById(p).getCart());
//
//            db.close();

            imgFavorate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    newStatus =! newStatus;

                    if (newStatus){

                        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_FAVORATE).child(auth.getUid()).child(model.getid()).child("id").setValue(model.getid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    imgFavorate.setImageResource(R.drawable.favorite_full);
                                }else{
                                    imgFavorate.setImageResource(R.drawable.favorite_empty);
                                }
                            }
                        });

                    }else {

                        imgFavorate.setImageResource(R.drawable.favorite_empty);

                        DatabaseReference ref = databaseReferencere
                                .child(UtelsDB.FIREBASE_TABLE_DESSERT_FAVORATE)
                                .child(auth.getUid())
                                .child(model.getid());

                        ref.removeValue();

                    }


//                    db.open();
//
//                    boolean newFavoriteStatus = !db.getDessertById(p).getFavorite();
//                    model.setFavorite(newFavoriteStatus);
//                    db.updateDessert(model);
//                    db.close();

//                    if (newFavoriteStatus) {
//                        imgFavorate.setImageResource(R.drawable.favorite_full);
//                    } else {
//                        imgFavorate.setImageResource(R.drawable.favorite_empty);
//                    }
                }
            });

            buttonAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ModelCart mc = new ModelCart(model.getPrice(),itemCount,model.getid());

                    databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CART).child(auth.getUid()).child(model.getid()).setValue(mc).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                 Intent intent = new Intent(ShowDessertActivity.this,CartActivity.class);
                                 startActivity(intent);
                                 finish();
                            }else{
                                Toast.makeText(ShowDessertActivity.this, "This Dessert Is Already In Cart", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


//                    db.open();
//                    boolean newCartStatus = !db.getDessertById(p).getCart();
//
//                    if (newCartStatus) {
//                        model.setCart(newCartStatus);
//                        db.updateDessert(model);
//                        db.addDessertCart(new ModelCart(String.valueOf(model.getId()),String.valueOf(itemCount),String.valueOf(model.getPrice()),String.valueOf(total)));
//                        db.close();
//                        Intent intent = new Intent(ShowDessertActivity.this,CartActivity.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Toast.makeText(ShowDessertActivity.this, "This Dessert Is Already In Cart", Toast.LENGTH_SHORT).show();
//
//                    }


                }
            });



        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setBackgroundResource(R.drawable.background_button_checkout);
                button3.setBackgroundResource(R.drawable.backgroun_item_cart);
                button5.setBackgroundResource(R.drawable.backgroun_item_cart);
                button10.setBackgroundResource(R.drawable.backgroun_item_cart);
                editTextCustom.setBackgroundResource(R.drawable.backgroun_item_cart);

                itemCount = 1;
                total= Double.valueOf(df.format(Double.valueOf(model.getPrice())*itemCount));
                textTotal.setText(String.valueOf(total));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setBackgroundResource(R.drawable.backgroun_item_cart);
                button3.setBackgroundResource(R.drawable.background_button_checkout);
                button5.setBackgroundResource(R.drawable.backgroun_item_cart);
                button10.setBackgroundResource(R.drawable.backgroun_item_cart);
                editTextCustom.setBackgroundResource(R.drawable.backgroun_item_cart);

                itemCount = 3;
                total= Double.valueOf(df.format(Double.valueOf(model.getPrice())*itemCount));
                textTotal.setText(String.valueOf(total));
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setBackgroundResource(R.drawable.backgroun_item_cart);
                button3.setBackgroundResource(R.drawable.backgroun_item_cart);
                button5.setBackgroundResource(R.drawable.background_button_checkout);
                button10.setBackgroundResource(R.drawable.backgroun_item_cart);
                editTextCustom.setBackgroundResource(R.drawable.backgroun_item_cart);

                itemCount = 5;
                total= Double.valueOf(df.format(Double.valueOf(model.getPrice())*itemCount));
                textTotal.setText(String.valueOf(total));
            }
        });

        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setBackgroundResource(R.drawable.backgroun_item_cart);
                button3.setBackgroundResource(R.drawable.backgroun_item_cart);
                button5.setBackgroundResource(R.drawable.backgroun_item_cart);
                button10.setBackgroundResource(R.drawable.background_button_checkout);
                editTextCustom.setBackgroundResource(R.drawable.backgroun_item_cart);

                itemCount = 10;
                total= Double.valueOf(df.format(Double.valueOf(model.getPrice())*itemCount));
                textTotal.setText(String.valueOf(total));
            }
        });



        editTextCustom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                button1.setBackgroundResource(R.drawable.backgroun_item_cart);
                button3.setBackgroundResource(R.drawable.backgroun_item_cart);
                button5.setBackgroundResource(R.drawable.backgroun_item_cart);
                button10.setBackgroundResource(R.drawable.backgroun_item_cart);
                editTextCustom.setBackgroundResource(R.drawable.background_button_checkout);

                if (editTextCustom.getText().toString().isEmpty()){
                    itemCount = 0;
                }else {
                    itemCount = Integer.parseInt(String.valueOf(editTextCustom.getText()));
                }
                total= Double.valueOf(df.format(Double.valueOf(model.getPrice())*itemCount));
                textTotal.setText(String.valueOf(total));
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });


        }







    }




















}