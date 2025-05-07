package Controlar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.nouroeddinne.sweetsstore.R;
import com.nouroeddinne.sweetsstore.ShowDessertActivity;

import java.util.ArrayList;
import Model.Model;
import Utel.UtelsDB;

public class AdapterFavorate extends RecyclerView.Adapter<AdapterFavorate.ViweHolder> {
    //private DataBaseAccess db ;

    FirebaseAuth auth;
    DatabaseReference databaseReferencere;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    ArrayList<Model> idDessertList = new ArrayList<>();
    Context context;



    public AdapterFavorate(Context context, ArrayList<Model> idDessertList) {
        this.context = context;
        this.idDessertList = idDessertList;
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferencere = firebaseDatabase.getReference();
    }

    @NonNull
    @Override
    public ViweHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viwe = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_dessert, parent, false);
        return new AdapterFavorate.ViweHolder(viwe);
    }

    public void onBindViewHolder(@NonNull AdapterFavorate.ViweHolder holder, @SuppressLint("RecyclerView") int position) {
        int currentPosition =holder.getAdapterPosition();
        boolean[] newStatus = {false};


        Model model = idDessertList.get(currentPosition);

        if (model!=null){
            Log.d("TAG", "onDataChange: "+model);
            Log.d("TAG", "onDataChange: "+model.getid());
            Log.d("TAG", "onDataChange: "+model.getBy());
            Log.d("TAG", "onDataChange: "+model.getImg());
            Log.d("TAG", "onDataChange: "+model.getName());
            Log.d("TAG", "onDataChange: "+model.getPrice());
            Log.d("TAG", "onDataChange: "+model.getType());

            holder.textName.setText(shorterWord(model.getName(),30));
            holder.textPrice.setText(model.getPrice());
            Glide.with(context).load(model.getImg()).into(holder.imgDessert);
            holder.imgFavorate.setImageResource(R.drawable.favorite_full);
        }



//        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT).child(idDessertList.get(currentPosition)).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                Model model = snapshot.getValue(Model.class);
//
//                if (model!=null){
//                    Log.d("TAG", "onDataChange: "+model);
//                    Log.d("TAG", "onDataChange: "+model.getid());
//                    Log.d("TAG", "onDataChange: "+model.getBy());
//                    Log.d("TAG", "onDataChange: "+model.getImg());
//                    Log.d("TAG", "onDataChange: "+model.getName());
//                    Log.d("TAG", "onDataChange: "+model.getPrice());
//                    Log.d("TAG", "onDataChange: "+model.getType());
//
//                    holder.textName.setText(shorterWord(model.getName(),30));
//                    holder.textPrice.setText(model.getPrice());
//                    Glide.with(context).load(model.getImg()).into(holder.imgDessert);
//                    holder.imgFavorate.setImageResource(R.drawable.favorite_full);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CART).child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String data = String.valueOf(snapshot.child(idDessertList.get(currentPosition).getid()).child("id").getValue());
                if (data!=null){
                    if (data.equals(String.valueOf(idDessertList.get(currentPosition).getid()))){
                        holder.imgCart.setVisibility(View.GONE);
                        newStatus[0] = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        Model model = dessertList.get(currentPosition);
//        holder.textName.setText(shorterWord(model.getName(),30));
//        holder.textPrice.setText(model.getPrice());
//        Glide.with(context).load(model.getImg()).into(holder.imgDessert);
//        holder.imgFavorate.setImageResource(R.drawable.favorite_full);
//
//        holder.imgCart.setVisibility(View.GONE);
//
//        db = DataBaseAccess.getInstance(context);
//        if (model.getCart()){
//            holder.imgCart.setVisibility(View.GONE);
//        }

//        db=new Database(context);

        holder.imgDessert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowDessertActivity.class);
                intent.putExtra("position",idDessertList.get(currentPosition).getid().toString());
                context.startActivity(intent);

            }
        });


        holder.imgFavorate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentPosition =holder.getAdapterPosition();

                DatabaseReference ref = databaseReferencere
                        .child(UtelsDB.FIREBASE_TABLE_DESSERT_FAVORATE)
                        .child(auth.getUid())
                        .child(idDessertList.get(currentPosition).getid());

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

            }
        });


        holder.imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentPosition =holder.getAdapterPosition();


                newStatus[0] =!newStatus[0];

                if (newStatus[0]){

                    databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CART).child(auth.getUid()).child(idDessertList.get(currentPosition).getid()).child("id").setValue(idDessertList.get(currentPosition).getid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //Toast.makeText(context, "isSuccessful", Toast.LENGTH_SHORT).show();
                                holder.imgCart.setVisibility(View.GONE);
                            }else{
                                //Toast.makeText(context, "not Successful", Toast.LENGTH_SHORT).show();
                                holder.imgCart.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }else {
                    holder.imgCart.setVisibility(View.VISIBLE);
                }







//                boolean newCartStatus = !model.getCart();
//                model.setCart(newCartStatus);
//
//                if (newCartStatus) {
//                    holder.imgCart.setVisibility(View.GONE);
//                }
//
//                db.updateDessert(model);
//                dessertList.set(currentPosition, model);
//                db.addDessertCart(new ModelCart(String.valueOf(model.getId()),"1",String.valueOf(model.getPrice()),String.valueOf(model.getPrice())));
                notifyItemChanged(currentPosition);

            }
        });

    }

    @Override
    public int getItemCount() {
        return idDessertList.size();
    }

    public class ViweHolder extends RecyclerView.ViewHolder {
        TextView textName,textPrice;
        ImageView imgDessert,imgFavorate,imgCart;
        public ViweHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textView3);
            textPrice = itemView.findViewById(R.id.textView9);
            imgDessert = itemView.findViewById(R.id.imageView11);
            imgFavorate = itemView.findViewById(R.id.imageView10);
            imgCart = itemView.findViewById(R.id.imageView3);

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
