package Controlar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nouroeddinne.sweetsstore.R;
import com.nouroeddinne.sweetsstore.ShowDessertActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Model.Model;
import Utel.UtelsDB;


public class AdapterSaller  extends RecyclerView.Adapter<AdapterSaller.ViweHolder>{

    Context context;
    ArrayList<Model> dessertList = new ArrayList<Model>();
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferencere;


    public AdapterSaller(Context context, ArrayList<Model> dessertList) {
        this.context = context;
        this.dessertList = dessertList;

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferencere = firebaseDatabase.getReference();

    }

    @NonNull
    @Override
    public AdapterSaller.ViweHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viwe = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_dessert, parent, false);
        return new ViweHolder(viwe);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSaller.ViweHolder holder, int position) {

        Model model;
        model = dessertList.get(position);

        if (model.getBy() == auth.getUid()){

        }

        holder.textName.setText(shorterWord(model.getName(),30));
        holder.textPrice.setText(model.getPrice());
        Glide.with(context).load(model.getImg()).into(holder.imgDessert);

        holder.imgCart.setImageResource(R.drawable.baseline_delete_forever_24);
        holder.imgFavorate.setVisibility(View.GONE);
        holder.cardView.setVisibility(View.GONE);

        holder.imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref = databaseReferencere
                        .child(UtelsDB.FIREBASE_TABLE_DESSERT)
                        .child(model.getid());
                ref.removeValue();

            }
        });

    }

    @Override
    public int getItemCount() {
        return dessertList.size();
    }



    public class ViweHolder extends RecyclerView.ViewHolder {
        TextView textName,textPrice;
        ImageView imgDessert,imgFavorate,imgCart;
        CardView cardView;
        public ViweHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textView3);
            textPrice = itemView.findViewById(R.id.textView9);
            imgDessert = itemView.findViewById(R.id.imageView11);
            imgFavorate = itemView.findViewById(R.id.imageView10);
            imgCart = itemView.findViewById(R.id.imageView3);
            cardView = itemView.findViewById(R.id.cardview_imgdessert);

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
