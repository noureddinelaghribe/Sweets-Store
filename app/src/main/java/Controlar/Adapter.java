package Controlar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nouroeddinne.sweetsstore.HomeActivity;
import com.nouroeddinne.sweetsstore.R;
import com.nouroeddinne.sweetsstore.ShowDessertActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Model;
import Model.ModelCart;
import Utel.UtelsDB;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<Model> dessertList = new ArrayList<Model>();
    //private DataBaseAccess db ;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferencere;

    private Map<String, Boolean> favoriteMap = new HashMap<>();
    private Map<String, Boolean> cartMap = new HashMap<>();

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private boolean isLoadingAdded = false;


    public Adapter(Context context, ArrayList<Model> dessertList) {
        this.context = context;
        this.dessertList = dessertList;

        auth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferencere = firebaseDatabase.getReference();

        fetchFavoriteAndCartData();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_dessert, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }

//        View viwe = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_dessert, parent, false);
//        return new ViweHolder(viwe);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        if (holder instanceof ItemViewHolder) {
            // Bind your data to the view holder
            Model item = dessertList.get(position);
            ((ItemViewHolder) holder).bind(item);
        } else if (holder instanceof LoadingViewHolder) {
            // Configure the loading view if needed
            ((LoadingViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
        }

    }



    @Override
    public int getItemCount() {
        return dessertList == null ? 0 : dessertList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == dessertList.size() - 1 && isLoadingAdded) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    // Add a list of items
    public void addItems(List<Model> items) {
        dessertList.addAll(items);
        notifyDataSetChanged();
    }

    // Add loading footer
    public void addLoadingFooter() {
        isLoadingAdded = true;
        dessertList.add(new Model()); // Add empty item
        notifyItemInserted(dessertList.size() - 1);
    }

    // Remove loading footer
    public void removeLoadingFooter() {
        if (isLoadingAdded && dessertList.size() > 0) {
            isLoadingAdded = false;
            int position = dessertList.size() - 1;
            dessertList.remove(position);
            notifyItemRemoved(position);
        }
    }



    // View holder for normal items
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textName,textPrice;
        ImageView imgDessert,imgFavorate,imgCart;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textView3);
            textPrice = itemView.findViewById(R.id.textView9);
            imgDessert = itemView.findViewById(R.id.imageView11);
            imgFavorate = itemView.findViewById(R.id.imageView10);
            imgCart = itemView.findViewById(R.id.imageView3);
        }

        public void bind(Model model) {


            boolean[] newStatus = {false,false};
            int currentPosition = getAdapterPosition();
            //model = dessertList.get(currentPosition);
            textName.setText(shorterWord(model.getName(),30));
            textPrice.setText(model.getPrice());
            Glide.with(context).load(model.getImg()).into(imgDessert);


            if (favoriteMap.containsKey(model.getid())) {
                imgFavorate.setImageResource(R.drawable.favorite_full);
            } else {
                imgFavorate.setImageResource(R.drawable.favorite_empty);
            }

            if (cartMap.containsKey(model.getid())) {
                imgCart.setVisibility(View.GONE);
            } else {
                imgCart.setVisibility(View.VISIBLE);
            }



            imgDessert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("TAG", "onCreate: "+model.getid());

                    Intent intent = new Intent(context, ShowDessertActivity.class);
                    intent.putExtra("model",model);
                    context.startActivity(intent);
                }
            });





            imgFavorate.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {
                    newStatus[0] =!newStatus[0];

                    if (newStatus[0]){

                        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_FAVORATE).child(auth.getUid()).child(model.getid()).child("id").setValue(model.getid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    //Toast.makeText(context, "isSuccessful", Toast.LENGTH_SHORT).show();
                                    imgFavorate.setImageResource(R.drawable.favorite_full);
                                }else{
                                    //Toast.makeText(context, "not Successful", Toast.LENGTH_SHORT).show();
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

                    notifyDataSetChanged();

                    dessertList.set(currentPosition, model);

                }
            });

            imgCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    newStatus[1] =!newStatus[1];

                    if (newStatus[1]){

                        ModelCart mc = new ModelCart(model.getPrice(),1,model.getid());

                        databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CART).child(auth.getUid()).child(model.getid()).setValue(mc).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    imgCart.setVisibility(View.GONE);
                                }else{
                                    imgCart.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    }else {
                        imgCart.setVisibility(View.VISIBLE);
                    }



                }
            });

        }
    }

    // View holder for the loading indicator
    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }




//    public class ViweHolder extends RecyclerView.ViewHolder {
//        TextView textName,textPrice;
//        ImageView imgDessert,imgFavorate,imgCart;
//        public ViweHolder(@NonNull View itemView) {
//            super(itemView);
//
//            textName = itemView.findViewById(R.id.textView3);
//            textPrice = itemView.findViewById(R.id.textView9);
//            imgDessert = itemView.findViewById(R.id.imageView11);
//            imgFavorate = itemView.findViewById(R.id.imageView10);
//            imgCart = itemView.findViewById(R.id.imageView3);
//
//        }
//    }



    public String shorterWord(String s,int n){

        int length = s.length();
        if(length>n){
            s = s.substring(0,n);
            s = s+"...";
            return s;
        }
        return s;
    }






    private void fetchFavoriteAndCartData() {
        DatabaseReference favRef = databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_FAVORATE).child(auth.getUid());
        favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteMap.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String id = ds.child("id").getValue(String.class);
                    if (id != null) {
                        favoriteMap.put(id, true);
                    }
                }
                notifyDataSetChanged(); // Notify adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        DatabaseReference cartRef = databaseReferencere.child(UtelsDB.FIREBASE_TABLE_DESSERT_CART).child(auth.getUid());
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartMap.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String id = ds.child("id").getValue(String.class);
                    if (id != null) {
                        cartMap.put(id, true);
                    }
                }
                notifyDataSetChanged(); // Notify adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
















}



