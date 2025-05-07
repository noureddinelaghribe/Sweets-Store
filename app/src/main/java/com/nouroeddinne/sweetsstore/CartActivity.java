package com.nouroeddinne.sweetsstore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Controlar.AdapterCart;
import Fragment.FragmentQuickAlert;
import Model.Model;
import Model.ModelCart;

import Model.ModelProfile;
import Utel.UtelsDB;

public class CartActivity extends AppCompatActivity implements OnBackPressedDispatcherOwner , FragmentQuickAlert.OnButtonLisner {
    private OnBackPressedCallback callback;
    RecyclerView recyclerView;
    Button button;
    ImageView back;
    private TextView totalPrice;
    static RecyclerView.Adapter adapter;
    ArrayList<ModelCart> dessertListCart = new ArrayList<>();
//    DataBaseAccess db = DataBaseAccess.getInstance(this);

    FirebaseAuth auth;
    DatabaseReference databaseReferencere;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    private Double total;
    private int numberOfItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        recyclerView = findViewById(R.id.recyclerView);
        button = findViewById(R.id.button);
        back = findViewById(R.id.imageView17);
        totalPrice = findViewById(R.id.textView14);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferencere = firebaseDatabase.getReference();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));;

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        };

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        db.open();
//        dessertListCart=db.getAllDESSERTCart();
//        db.close();


        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CART).child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dessertListCart.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ModelCart modelCart = snapshot1.getValue(ModelCart.class);
                    if (modelCart.getId() != null) {
                        dessertListCart.add(modelCart);
                    }
                }
                load();
                //                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (HomeActivity.EMAIL!=null){
                    if (total>0){
                        FragmentQuickAlert fragment2 = FragmentQuickAlert.newInstance("Success","Payment complited successfully!",String.valueOf(R.color.green),String.valueOf(R.drawable.baseline_done_all_24));
                        fragment2.show(getSupportFragmentManager(),null);
                        fragment2.setCancelable(false);
                    }else {
                        Toast.makeText(CartActivity.this, "Sorry the cart is empty!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(CartActivity.this, "login or creat acount!", Toast.LENGTH_SHORT).show();
                }


            }
        });



    }



    public void load(){

        this.numberOfItems = dessertListCart.size();

        adapter = new AdapterCart(this,dessertListCart);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                getTotal();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                getTotal();
            }
        });

        recyclerView.setAdapter(adapter);

        getTotal();

        numberOfItems=0;
        total=0.0;
        for (ModelCart model : dessertListCart){
            total+=Double.valueOf(model.getPrice());
            numberOfItems++;
        }


    }

    @Override
    public void onCklick() {

        pushCheckoutData();

//        String key = databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CHECKOUT).child(auth.getUid()).push().getKey();
//
//        Map<String,Object> data = new HashMap<>();
//        for (ModelCart model : dessertListCart){
//            data.put(databaseReferencere.push().getKey(),model.getId());
//        }
//        data.put("total",total);
//
//        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CHECKOUT).child(auth.getUid()).child(key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//                    DatabaseReference ref = databaseReferencere
//                            .child(UtelsDB.FIREBASE_TABLE_DESSERT_CART)
//                            .child(auth.getUid());
//                    ref.removeValue();
//                }
//            }
//        });


//        boolean newCartStatus = false;
//        db.open();
//        Iterator<ModelCart> iterator = dessertListCart.iterator();
//
//        while (iterator.hasNext()) {
//            ModelCart mc = iterator.next();
//            Model model = db.getDessertCartById(Integer.parseInt(mc.getIdd()));
//            model.setCart(newCartStatus);
//            db.updateDessert(model);
//            db.deleteDessertCart(mc);
//            iterator.remove();
//        }
//
//        ModelProfile profile = db.getPersonByEmail(HomeActivity.EMAIL);
//        int nof = profile.getNumberPurchases()+numberOfItems;
//        profile.setNumberPurchases(nof);
//        double ts = profile.getTotalSpend()+total;
//        profile.setTotalSpend(ts);
//        db.updatePerson(profile);
//        db.close();
//        adapter.notifyDataSetChanged();
//
//        Intent intent = new Intent(CartActivity.this,HomeActivity.class);
//        startActivity(intent);
//        finish();

    }




    public void getTotal(){
        DecimalFormat df = new DecimalFormat("#.##");
        total=0.0;
        for (ModelCart model : dessertListCart){
            total+=Double.valueOf(model.getCount())*Double.valueOf(model.getPrice());
        }
        totalPrice.setText(String.valueOf(df.format(total)));

    }


    private void pushCheckoutData() {

        String key = databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CHECKOUT).child(auth.getUid()).push().getKey();
        if (key == null) {
            return;
        }

        Map<String, Object> data = new HashMap<>();

        for (ModelCart model : dessertListCart) {
            data.put(model.getId(), model.getId());
        }

        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CHECKOUT).child(auth.getUid()).child(key).child("totalSpend").setValue( totalPrice.getText());
        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CHECKOUT).child(auth.getUid()).child(key).child("ids").setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Remove items from the cart after successful checkout
                            databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CART).child(auth.getUid()).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Checkout successful and cart cleared
                                                Toast.makeText(CartActivity.this, "Checkout successful!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Handle failure in removing cart items
                                                Toast.makeText(CartActivity.this, "Failed to clear cart", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // Handle failure in pushing checkout data
                            Toast.makeText(CartActivity.this, "Failed to complete checkout", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    @Override
    protected void onStart() {
        super.onStart();

        //get all hiistory checkout

        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CHECKOUT).child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot checkoutSnapshot : snapshot.getChildren()){
                    Log.d("TAG", "checkout total : "+checkoutSnapshot.child("totalSpend").getValue());
                    for (DataSnapshot checkoutSnapshot2 : checkoutSnapshot.getChildren()){
                        for (DataSnapshot checkoutSnapshot3 : checkoutSnapshot2.getChildren()){

                            Log.d("TAG", "checkout ids: "+checkoutSnapshot3.getValue());

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });










    }














}