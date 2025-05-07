package Controlar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nouroeddinne.sweetsstore.R;
import com.nouroeddinne.sweetsstore.ShowDessertActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import Model.Model;
import Model.ModelCart;
import Utel.UtelsDB;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.ViweHolder>{

    private Context context;
//    private ArrayList<ModelCart> dessertListCart;
    ArrayList<ModelCart> idDessertList = new ArrayList<>();

    FirebaseAuth auth;
    DatabaseReference databaseReferencere;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    Model model;


    public AdapterCart(Context context, ArrayList<ModelCart> idDessertList) {
        this.context = context;
        this.idDessertList = idDessertList;

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferencere = firebaseDatabase.getReference();
    }

    @NonNull
    @Override
    public AdapterCart.ViweHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viwe = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_cart, parent, false);
        return new AdapterCart.ViweHolder(viwe);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCart.ViweHolder holder, int position) {
//        db = DataBaseAccess.getInstance(context);
        int currentPosition =holder.getAdapterPosition();
        Log.d("TAG", "onDataChange: "+idDessertList.size());


        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT).child(idDessertList.get(currentPosition).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                model = snapshot.getValue(Model.class);



//                ModelCart modelCart = dessertListCart.get(currentPosition);
//                db.open();
//                Model model = db.getDessertCartById(Integer.parseInt(modelCart.getIdd()));
//                db.close();
//
                if (model != null){
                    holder.textName.setText(shorterWord(model.getName(),25));
                    Glide.with(context).load(model.getImg()).into(holder.imgDessert);

//            Log.d("TAG", "onBindViewHolder: "+model.getImg());
                }

                holder.textPrice.setText(model.getPrice());
                holder.textCount.setText(String.valueOf(idDessertList.get(currentPosition).getCount()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        holder.imgDessert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowDessertActivity.class);
                intent.putExtra("position",model.getid());
                context.startActivity(intent);
            }
        });


        holder.imgDell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentPosition =holder.getAdapterPosition();

                DatabaseReference ref = databaseReferencere
                        .child(UtelsDB.FIREBASE_TABLE_DESSERT_CART)
                        .child(auth.getUid())
                        .child(idDessertList.get(currentPosition).getId());

                ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Log.d("Firebase", "Field removed successfully.");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Log.w("Firebase", "Failed to remove field.", e);
                            }
                        });

//                int currentPosition =holder.getAdapterPosition();
//                boolean newFavoriteStatus = !model.getFavorite();
//                model.setFavorite(newFavoriteStatus);
//
//                db.open();
//                db.updateDessert(model);
//                db.close();
//
                idDessertList.remove(currentPosition);
                notifyItemRemoved(currentPosition);



//                int currentPosition =holder.getAdapterPosition();
//                boolean newCartStatus = !model.getCart();
//                model.setCart(newCartStatus);
//                db.open();
//                db.updateDessert(model);
//                db.deleteDessertCart(modelCart);
//                db.close();
//                dessertListCart.remove(currentPosition);
//                notifyItemRemoved(currentPosition);

            }
        });

        holder.imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentPosition =holder.getAdapterPosition();

                int count= idDessertList.get(currentPosition).getCount();
                count++;
                databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CART).child(auth.getUid()).child(idDessertList.get(currentPosition).getId()).child("count").setValue(count);



//                Double price = Double.valueOf(count*Double.valueOf(model.getPrice()));
//
//                modelCart.setCount(String.valueOf(count));
//                modelCart.setTotalPrice(String.valueOf(price));
//                db.open();
//                db.updateDessertCart(modelCart);
//                db.close();
//                notifyDataSetChanged();
//
//                holder.textCount.setText(String.valueOf(count));
//                holder.textPrice.setText(String.valueOf(price));

            }
        });

        holder.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int count= idDessertList.get(currentPosition).getCount();
                if (count>1){
                    count--;
                    databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CART).child(auth.getUid()).child(idDessertList.get(currentPosition).getId()).child("count").setValue(count);
                }



//                int count= Integer.parseInt(modelCart.getCount());
//                if (count>1){
//                    count--;
//                    Double price = Double.valueOf(count*Double.valueOf(model.getPrice()));
//                    modelCart.setCount(String.valueOf(count));
//                    modelCart.setTotalPrice(String.valueOf(price));
//                    db.open();
//                    db.updateDessertCart(modelCart);
//                    db.close();
//                    notifyDataSetChanged();
//
//                    holder.textCount.setText(String.valueOf(count));
//                    holder.textPrice.setText(String.valueOf(price));
//                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return idDessertList.size();
    }

    public class ViweHolder extends RecyclerView.ViewHolder{

        TextView textName,textPrice,textCount;
        ImageView imgDessert,imgDell,imgPlus,imgMinus;
        public ViweHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textView_item_cart_name);
            textPrice = itemView.findViewById(R.id.textView_item_cart_priceItem);
            textCount = itemView.findViewById(R.id.textView_item_cart_counts);
            imgDessert = itemView.findViewById(R.id.imageView_item_cart_img);
            imgDell = itemView.findViewById(R.id.imageViewimageView_item_cart_dell);
            imgPlus = itemView.findViewById(R.id.imageView_item_cart_plus);
            imgMinus = itemView.findViewById(R.id.imageView_item_cart_minus);

        }
    }

    public String shorterWord(String s,int n){

        int length = s.length();
        if(length>n){
            s = s.substring(0,n);
            s = s+"...";
            return s;
        }
        return s;
    }

}



