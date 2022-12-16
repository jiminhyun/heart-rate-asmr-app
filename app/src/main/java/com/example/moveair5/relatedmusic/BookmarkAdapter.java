package com.example.moveair5.relatedmusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moveair5.CustomToast;
import com.example.moveair5.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.CustomViewHolder> {
    private ArrayList<Bookmark> arrayList;
    private Context context;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference databaseReference, databaseReference2;
    Boolean check2;

    public BookmarkAdapter(ArrayList<Bookmark> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private BookmarkAdapter.OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(BookmarkAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_item_activated_1, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.genre = arrayList.get(position).getGenre();
        holder.route = arrayList.get(position).getRoute();
        holder.NameText.setText(arrayList.get(position).getName());
        holder.name = arrayList.get(position).getName();
        holder.cf = "data"+String.valueOf(holder.genre*100+holder.route+100);
        holder.check(holder.cf);
    }

    @Override
    public int getItemCount() {
        // 삼항 연산자
        return (arrayList != null ? arrayList.size() : 0); //아이템 수
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView NameText;
        String name;
        int genre, route;
        String cf;
        ImageView bookmark;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.NameText = itemView.findViewById(R.id.NameText);
            this.bookmark = itemView.findViewById(R.id.bookmark);

            mFirebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            com.example.moveair5.UserAccount account = new com.example.moveair5.UserAccount();
            account.setIdToken(firebaseUser.getUid());
            String a = account.getIdToken();
            databaseReference2 = FirebaseDatabase.getInstance().getReference("MoveAir/UserAccount/"+a);
            databaseReference = FirebaseDatabase.getInstance().getReference("MoveAir/UserAccount/"+a+"/user/bookmark");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlaySong.setArrayList(arrayList);
                    PlaySong.setName(name);
                    PlaySong.setGenre(genre);
                    PlaySong.setRoute(route);
                    PlaySong.setGenrename("목록: 즐겨찾기");
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });

            bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    check2 = true;
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(check2.equals(true)) {
                                if(snapshot.hasChild(cf)) {
                                    databaseReference.child(cf).removeValue();
                                    bookmark.setImageResource(R.drawable.bookmark_add);
                                    CustomToast.showToast(context, "즐겨찾기에 삭제되었습니다.");
                                    check2 = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }
        public void check(String check) {// 북마크 체크 여부 메소드
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(check)) {
                        bookmark.setImageResource(R.drawable.bookmark_remove);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
