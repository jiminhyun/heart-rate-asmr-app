package com.example.moveair5.relatedmusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moveair5.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class StabilizeAdapter extends RecyclerView.Adapter<StabilizeAdapter.CustomViewHolder> {
    private ArrayList<Stabilize> arrayList;
    private Context context;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference databaseReference;

    public StabilizeAdapter(ArrayList<Stabilize> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_item_activated_2, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.genre = arrayList.get(position).getGenre();
        holder.route = arrayList.get(position).getRoute();
        holder.NameText.setText(arrayList.get(position).getName());
        holder.name = arrayList.get(position).getName();
    }

    @Override
    public int getItemCount() {
        // 삼항 연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView NameText;
        String name;
        int genre, route;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.NameText = itemView.findViewById(R.id.stabilize_NameText);

            mFirebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            com.example.moveair5.UserAccount account = new com.example.moveair5.UserAccount();
            account.setIdToken(firebaseUser.getUid());
            String a = account.getIdToken();
            databaseReference = FirebaseDatabase.getInstance().getReference("MoveAir/UserAccount/"+a);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StabilizeSong.setArrayList(arrayList);
                    StabilizeSong.setName(name);
                    StabilizeSong.setGenre(genre);
                    StabilizeSong.setRoute(route);
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
