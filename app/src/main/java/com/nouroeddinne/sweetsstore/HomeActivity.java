package com.nouroeddinne.sweetsstore;

import static Utel.UtelsDB.SHAREDPREFERNCES_FILENAME_EMAIL;
import static Utel.UtelsDB.SHAREDPREFERNCES_FILENAME_ITEMS;
import static Utel.UtelsDB.SHAREDPREFERNCES_FILENAME_USER;
import static Utel.UtelsDB.SHAREDPREFERNCES_SALLER;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Controlar.PagerAdapter;
import Fragment.FragmentQuickAlert;
import Fragment.FragmentQuickAlertExit;
import Fragment.HomeFragment;
import Model.Model;
import Model.ModelPager;
import Utel.UtelsDB;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener , FragmentQuickAlert.OnButtonLisner, FragmentQuickAlertExit.OnButtonExitLisner, FragmentQuickAlertExit.OnButtonCancelLisner {

    ImageView favourite, cart, search, profail;
    static TextView textViewNotifFaverate;
    static TextView textViewNotifCart;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    //static DataBaseAccess db;

    PagerAdapter adapter;

    ArrayList<Model> dessertList= new ArrayList<Model>();
    ArrayList<Model>CapcakeList= new ArrayList<Model>(), Dounetlist= new ArrayList<Model>(), CookiesList= new ArrayList<Model>(), CandyList= new ArrayList<Model>();
    ArrayList<Model> dessertListFavorate= new ArrayList<Model>(),dessertListCart = new ArrayList<Model>();
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesEmail;
    public static String EMAIL = null;

    static FirebaseAuth auth;
    static DatabaseReference databaseReferencere;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences(SHAREDPREFERNCES_SALLER, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(SHAREDPREFERNCES_SALLER,false) != false) {
            Intent intent = new Intent(this, HomeSallerActivity.class);
            startActivity(intent);
        }


        favourite = findViewById(R.id.imageView_favourite);
        cart = findViewById(R.id.imageView_cart);
        search = findViewById(R.id.imageView_search);
        profail = findViewById(R.id.imageView_profaile);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viwepager);

        textViewNotifFaverate = findViewById(R.id.textView_notif_faverate);
        textViewNotifCart = findViewById(R.id.textView_notif_cart);

        //db = DataBaseAccess.getInstance(this);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferencere = firebaseDatabase.getReference();


        sharedPreferences = getSharedPreferences(SHAREDPREFERNCES_FILENAME_USER, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(SHAREDPREFERNCES_FILENAME_EMAIL, null)!=null) {
            EMAIL=sharedPreferences.getString(SHAREDPREFERNCES_FILENAME_EMAIL, null);
        }else {
            databaseReferencere.child(UtelsDB.FIREBASE_TABLE_USERS).child(firebaseUser.getUid()).child(UtelsDB.KEY_NAME_PROFILE).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    EMAIL=snapshot.getValue(String.class);
                    sharedPreferencesEmail = getSharedPreferences(SHAREDPREFERNCES_FILENAME_EMAIL, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferencesEmail.edit();
                    editor.putString(SHAREDPREFERNCES_FILENAME_EMAIL, EMAIL);
                    editor.apply();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



        //add items

//        sharedPreferences = getSharedPreferences(SHAREDPREFERNCES_FILENAME_ITEMS, Context.MODE_PRIVATE);
//        if (sharedPreferences.getString(SHAREDPREFERNCES_FILENAME_ITEMS, null)==null) {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("addItems", "done");
//            editor.apply();
//            addItems();
//        }



        loadData();

        offer();

        tabLayout.getTabAt(1).setIcon(R.drawable.cupcake);
        tabLayout.getTabAt(2).setIcon(R.drawable.donut);
        tabLayout.getTabAt(3).setIcon(R.drawable.cookies);
        tabLayout.getTabAt(4).setIcon(R.drawable.candy);

        favourite.setOnClickListener(HomeActivity.this);
        cart.setOnClickListener(HomeActivity.this);
        search.setOnClickListener(HomeActivity.this);
        profail.setOnClickListener(HomeActivity.this);




    }



    private void loadData() {


        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dessertList.clear();
                CapcakeList.clear();
                Dounetlist.clear();
                CookiesList.clear();
                CandyList.clear();

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    Model m = snapshot1.getValue(Model.class);
                    dessertList.add(m);

                    String type = m.getType();
                    switch (String.valueOf(type)){
                        case "capcake":
                            CapcakeList.add(m);
                            break;
                        case "dounet":
                            Dounetlist.add(m);
                            break;
                        case "cookies":
                            CookiesList.add(m);
                            break;
                        case "candy":
                            CandyList.add(m);
                            break;
                        default:
                            Log.d("TAG", "onChildAdded: other");
                            break;
                    }

                }

                adapter = new PagerAdapter(getSupportFragmentManager(), getLifecycle());

                //to random all element dessertList
                Collections.shuffle(dessertList);

                adapter.addTab(new ModelPager("All", HomeFragment.newInstance("All", dessertList)));
                adapter.addTab(new ModelPager("Capcake", HomeFragment.newInstance("Capcake", CapcakeList)));
                adapter.addTab(new ModelPager("Dounet", HomeFragment.newInstance("Dounet", Dounetlist)));
                adapter.addTab(new ModelPager("Cookies", HomeFragment.newInstance("Cookies", CookiesList)));
                adapter.addTab(new ModelPager("Candy", HomeFragment.newInstance("Candy", CandyList)));

                viewPager.setAdapter(adapter);

                new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                    tab.setText(adapter.getTabName(position));
                }).attach();

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






//        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT).addChildEventListener(new ChildEventListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                Model m = snapshot.getValue(Model.class);
//                dessertList.add(m);
//
//                String type = m.getType();
//                switch (String.valueOf(type)){
//                    case "capcake":
//                        CapcakeList.add(m);
//                        break;
//                    case "dounet":
//                        Dounetlist.add(m);
//                        break;
//                    case "cookies":
//                        CookiesList.add(m);
//                        break;
//                    case "candy":
//                        CandyList.add(m);
//                        break;
//                    default:
//                        Log.d("TAG", "onChildAdded: facccccccccccccccccccccccccccccccccccccccccccccccccccccck");
//                        break;
//                }
//
//                adapter = new PagerAdapter(getSupportFragmentManager(), getLifecycle());
//
//                adapter.addTab(new ModelPager("All", HomeFragment.newInstance("All", dessertList)));
//                adapter.addTab(new ModelPager("Capcake", HomeFragment.newInstance("Capcake", CapcakeList)));
//                adapter.addTab(new ModelPager("Dounet", HomeFragment.newInstance("Dounet", Dounetlist)));
//                adapter.addTab(new ModelPager("Cookies", HomeFragment.newInstance("Cookies", CookiesList)));
//                adapter.addTab(new ModelPager("Candy", HomeFragment.newInstance("Candy", CandyList)));
//
//                viewPager.setAdapter(adapter);
//
//                new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
//                    tab.setText(adapter.getTabName(position));
//                }).attach();
//
//                adapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                dessertList.clear();
//
//                Model m = snapshot.getValue(Model.class);
//                dessertList.add(m);
//
//                String type = m.getType();
//                switch (String.valueOf(type)){
//                    case "capcake":
//                        CapcakeList.add(m);
//                        break;
//                    case "dounet":
//                        Dounetlist.add(m);
//                        break;
//                    case "cookies":
//                        CookiesList.add(m);
//                        break;
//                    case "candy":
//                        CandyList.add(m);
//                        break;
//                    default:
//                        Log.d("TAG", "onChildAdded: facccccccccccccccccccccccccccccccccccccccccccccccccccccck");
//                        break;
//                }
//
//                adapter = new PagerAdapter(getSupportFragmentManager(), getLifecycle());
//
//                adapter.addTab(new ModelPager("All", HomeFragment.newInstance("All", dessertList)));
//                adapter.addTab(new ModelPager("Capcake", HomeFragment.newInstance("Capcake", CapcakeList)));
//                adapter.addTab(new ModelPager("Dounet", HomeFragment.newInstance("Dounet", Dounetlist)));
//                adapter.addTab(new ModelPager("Cookies", HomeFragment.newInstance("Cookies", CookiesList)));
//                adapter.addTab(new ModelPager("Candy", HomeFragment.newInstance("Candy", CandyList)));
//
//                viewPager.setAdapter(adapter);
//
//                new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
//                    tab.setText(adapter.getTabName(position));
//                }).attach();
//
//                adapter.notifyDataSetChanged();
//
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


    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        if (v.getId() == R.id.imageView_favourite) {
            intent = new Intent(HomeActivity.this, Favourite_Dessert_Activity.class);
        } else if (v.getId() == R.id.imageView_cart) {
            intent = new Intent(HomeActivity.this, CartActivity.class);
        } else if (v.getId() == R.id.imageView_search) {
            intent = new Intent(HomeActivity.this, SearchActivity.class);
        } else if (v.getId() == R.id.imageView_profaile) {
            if (HomeActivity.EMAIL!=null){
                intent = new Intent(HomeActivity.this, ProfileActivity.class);
            }else {
                Toast.makeText(HomeActivity.this, "login or creat acount!", Toast.LENGTH_SHORT).show();
            }
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            FragmentQuickAlertExit fragmentExit = FragmentQuickAlertExit.newInstance();
            fragmentExit.show(getSupportFragmentManager(),null);
            fragmentExit.setCancelable(false);


            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public static void textNotif() {

        int[] count = {0,0};

        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_FAVORATE).child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count[0] = (int) snapshot.getChildrenCount();
                if (count[0] > 0) {
                    textViewNotifFaverate.setVisibility(View.VISIBLE);
                    textViewNotifFaverate.setText(String.valueOf(count[0]));
                } else {
                    textViewNotifFaverate.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CART).child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count[1] = (int) snapshot.getChildrenCount();
                if (count[1] > 0) {
                    textViewNotifCart.setVisibility(View.VISIBLE);
                    textViewNotifCart.setText(String.valueOf(count[1]));
                } else {
                    textViewNotifCart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



//        db.open();
//        dessertListFavorate = db.getFavorateDESSERT();
//        dessertListCart = db.getCartDESSERT();
//        db.close();
//



    }

    public void offer(){

        int n = sharedPreferences.getInt("my_integer_key", 0);;

        n+=1;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("my_integer_key", n);
        editor.apply();

        if ((sharedPreferences.getInt("my_integer_key", 0)%3)==0){
            FragmentQuickAlert fragment2 = FragmentQuickAlert.newInstance("Info","Buy two, get one free ",String.valueOf(R.color.yalow),String.valueOf(R.drawable.offer));
            fragment2.show(getSupportFragmentManager(),null);
            fragment2.setCancelable(false);
        }
    }

    private void addItems() {


        Model m1 = new Model("uid","fresh squeezed mimosa","5.99","capcake","https://sallysbakingaddiction.com/wp-content/uploads/2016/08/fresh-squeezed-mimosa-cupcakes-2-300x450.jpg");
        Model m2 = new Model("uid","blueberry lemon cupcakes","3.99","capcake","https://sallysbakingaddiction.com/wp-content/uploads/2016/04/blueberry-lemon-cupcakes-300x300.jpg");
        Model m3 = new Model("uid","homemade strawberry cupcakes","1.99","capcake","https://sallysbakingaddiction.com/wp-content/uploads/2018/08/homemade-strawberry-cupcakes-300x300.jpg");
        Model m4 = new Model("uid","smores brownie cupcakes","4.99","capcake","https://sallysbakingaddiction.com/wp-content/uploads/2017/08/smores-brownie-cupcakes-3-300x300.jpg");
        Model m5 = new Model("uid","Strawberry Cupcakes with Creamy Strawberry Frosting","6.99","capcake","https://sallysbakingaddiction.com/wp-content/uploads/2015/04/Strawberry-Cupcakes-with-Creamy-Strawberry-Frosting-2-300x300.jpg");
        Model m6 = new Model("uid","white chocolate peppermint cupcakes","1.99","capcake","https://sallysbakingaddiction.com/wp-content/uploads/2017/12/white-chocolate-peppermint-cupcakes-2-300x300.jpg");
        Model m7 = new Model("uid","delicious peppermint mocha cupcakes","5.99","capcake","https://sallysbakingaddiction.com/wp-content/uploads/2013/11/delicious-peppermint-mocha-cupcakes-300x300.jpg");
        Model m8 = new Model("uid","lemon cupcakes","5.99","capcake","https://sallysbakingaddiction.com/wp-content/uploads/2013/04/the-best-lemon-cupcakes-5-300x300.jpg");

        Model m11 = new Model("uid","glazed doughnuts homemade","9.99","dounet","https://sallysbakingaddiction.com/wp-content/uploads/2016/05/glazed-doughnuts-homemade-2-300x300.jpg");
        Model m12 = new Model("uid","Baked Funfetti Donuts","19.99","dounet","https://sallysbakingaddiction.com/wp-content/uploads/2014/01/Baked-Funfetti-Donuts.-These-taste-just-like-your-favorite-sprinkled-donuts-at-the-bakery.-And-theyre-so-simple-to-make-at-home-3-300x300.jpg");
        Model m13 = new Model("uid","homemade-doughnuts Cover them in a rich","3.99","dounet","https://sallysbakingaddiction.com/wp-content/uploads/2014/04/My-favorite-homemade-doughnuts-Cover-them-in-a-rich-super-easy-chocolate-ganache-and-tons-of-rainbow-sprinkles.-This-is-a-recipe-to-hold-onto-300x300.jpg");
        Model m14 = new Model("uid","donuts","6.99","dounet","https://sallysbakingaddiction.com/wp-content/uploads/2024/01/cookbook-recipes-latest-testing-5-300x300.jpg");
        Model m15 = new Model("uid","party donuts sallys baking addiction","8.99","dounet","https://sallysbakingaddiction.com/wp-content/uploads/2016/02/party-donuts-sallys-baking-addiction-300x300.jpg");
        Model m16 = new Model("uid","lemon poppy seed donuts","5.99","dounet","https://sallysbakingaddiction.com/wp-content/uploads/2016/09/lemon-poppy-seed-donuts-6-300x300.jpg");
        Model m17 = new Model("uid","pumpkin donuts","7.99","dounet","https://sallysbakingaddiction.com/wp-content/uploads/2019/09/pumpkin-donuts-300x300.jpg");
        Model m18 = new Model("uid","apple cider doughnuts","2.99","dounet","https://sallysbakingaddiction.com/wp-content/uploads/2018/09/apple-cider-doughnuts-300x300.jpg");
        Model m19 = new Model("uid","baked maple glazed-donuts","6.99","dounet","https://sallysbakingaddiction.com/wp-content/uploads/2014/09/baked-maple-glazed-donuts-2-300x300.jpg");
        Model m20 = new Model("uid","apple cider doughnuts","7.99","dounet","https://sallysbakingaddiction.com/wp-content/uploads/2018/09/apple-cider-doughnuts-300x300.jpg");
        Model m21 = new Model("uid","homemade doughnuts frostings","9.99","dounet","https://sallysbakingaddiction.com/wp-content/uploads/2016/07/homemade-doughnuts-frostings-2-300x300.jpg");

        Model m22 = new Model("uid","pumpkinsnickerdoodles","2.99","cookies","https://sallysbakingaddiction.com/wp-content/uploads/2014/09/pumpkin-snickerdoodles-2-300x450.jpg");
        Model m23 = new Model("uid","flourless monster cookies","12.99","cookies","https://sallysbakingaddiction.com/wp-content/uploads/2023/06/flourless-monster-cookies-2-300x450.jpg");
        Model m24 = new Model("uid","soft mm cookie bars","1.99","cookies","https://sallysbakingaddiction.com/wp-content/uploads/2022/12/soft-mm-cookie-bars-300x450.jpg");
        Model m25 = new Model("uid","peppermint snowballs cookies","1.99","cookies","https://sallysbakingaddiction.com/wp-content/uploads/2023/11/peppermint-snowballs-cookies-2-300x450.jpg");
        Model m26 = new Model("uid","cranberry orange icebox cookies","2.99","cookies","https://sallysbakingaddiction.com/wp-content/uploads/2022/12/cranberry-orange-icebox-cookies-3-300x450.jpg");
        Model m27 = new Model("uid","pumpkin oatmeal cookies","6.99","cookies","https://sallysbakingaddiction.com/wp-content/uploads/2016/09/pumpkin-oatmeal-cookies-2-300x450.jpg");
        Model m28 = new Model("uid","banana breakfast cookie chocolate chips","4.99","cookies","https://sallysbakingaddiction.com/wp-content/uploads/2014/01/banana-breakfast-cookie-chocolate-chips-2-300x450.jpg");
        Model m29 = new Model("uid","hot cocoa cookies with marshmallow","7.99","cookies","https://sallysbakingaddiction.com/wp-content/uploads/2023/11/hot-cocoa-cookies-with-marshmallow-300x450.jpg");
        Model m30 = new Model("uid","gf flourless peanut butter oatmeal cookies","9.99","cookies","https://sallysbakingaddiction.com/wp-content/uploads/2014/03/gf-flourless-peanut-butter-oatmeal-cookies-2-300x450.jpg");
        Model m31 = new Model("uid","peanut butter chocolate swirl cookies","4.99","cookies","https://sallysbakingaddiction.com/wp-content/uploads/2013/02/peanut-butter-chocolate-swirl-cookies-2-300x450.jpg");
        Model m32 = new Model("uid","nutella crinkles","6.99","cookies","https://sallysbakingaddiction.com/wp-content/uploads/2023/12/nutella-crinkles-photo-2-300x450.jpg");
        Model m33 = new Model("uid","lemon-curd thumbprint cookies","2.99","cookies","https://sallysbakingaddiction.com/wp-content/uploads/2023/11/lemon-curd-thumbprint-cookies-300x450.jpg");

        Model m34 = new Model("uid","Chocolate Covered Pretzels","7.99","candy","https://sallysbakingaddiction.com/wp-content/uploads/2011/12/Chocolate-Covered-Pretzels-by-sallysbakingaddiction.com-3-300x450.jpg");
        Model m35 = new Model("uid","Pretzel MM Hugs","17.99","candy","https://sallysbakingaddiction.com/wp-content/uploads/2012/10/Pretzel-MM-Hugs-300x450.jpg");
        Model m36 = new Model("uid","cookies and cream puppy chow","4.99","candy","https://sallysbakingaddiction.com/wp-content/uploads/2013/07/cookies-and-cream-puppy-chow-4-300x450.jpg");
        Model m37 = new Model("uid","Dark Chocolate Key Lime Pie Truffles","6.99","candy","https://sallysbakingaddiction.com/wp-content/uploads/2015/03/Dark-Chocolate-Key-Lime-Pie-Truffles-2-300x450.jpg");
        Model m38 = new Model("uid","Peanut Butter MM Truffles","1.99","candy","https://sallysbakingaddiction.com/wp-content/uploads/2015/06/Peanut-Butter-MM-Truffles-5-300x450.jpg");
        Model m39 = new Model("uid","pumpkin spice truffles","6.99","candy","https://sallysbakingaddiction.com/wp-content/uploads/2015/10/pumpkin-spice-truffles-3-300x450.jpg");
        Model m40 = new Model("uid","sea salt vanilla caramels","6.99","candy","https://sallysbakingaddiction.com/wp-content/uploads/2016/10/sea-salt-vanilla-caramels-5-300x450.jpg");
        Model m41 = new Model("uid","peanut butter easter egg candies","1.99","candy","https://sallysbakingaddiction.com/wp-content/uploads/2022/03/peanut-butter-easter-egg-candies-2-300x450.jpg");
        Model m42 = new Model("uid","chocolate covered peanut butter balls","4.99","candy","https://sallysbakingaddiction.com/wp-content/uploads/2023/12/chocolate-covered-peanut-butter-balls-300x450.jpg");
        Model m43 = new Model("uid","chocolate cake pops recipe","2.99","candy","https://sallysbakingaddiction.com/wp-content/uploads/2018/02/chocolate-cake-pops-recipe-300x450.jpg");



        ArrayList <Model> lm = new ArrayList<>();
        lm.add(m1);
        lm.add(m2);
        lm.add(m3);
        lm.add(m4);
        lm.add(m5);
        lm.add(m6);
        lm.add(m7);
        lm.add(m8);
        lm.add(m11);
        lm.add(m12);
        lm.add(m13);
        lm.add(m14);
        lm.add(m15);
        lm.add(m16);
        lm.add(m17);
        lm.add(m18);
        lm.add(m19);
        lm.add(m20);
        lm.add(m21);
        lm.add(m22);
        lm.add(m23);
        lm.add(m24);
        lm.add(m25);
        lm.add(m26);
        lm.add(m27);
        lm.add(m28);
        lm.add(m29);
        lm.add(m30);
        lm.add(m31);
        lm.add(m32);
        lm.add(m33);
        lm.add(m34);
        lm.add(m35);
        lm.add(m36);
        lm.add(m37);
        lm.add(m38);
        lm.add(m39);
        lm.add(m40);
        lm.add(m41);
        lm.add(m42);
        lm.add(m43);

        for (Model model : lm) {
            String key = databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT).push().getKey();
            databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT).child(key).setValue(model);
        }




    }

    @Override
    public void onCklick() {
        //Do something
    }

    @Override
    public void onCklickExit() {
        finishAffinity();
        System.exit(0);
    }

    @Override
    public void onCklickCancel() {
        // Do nothing
    }
}