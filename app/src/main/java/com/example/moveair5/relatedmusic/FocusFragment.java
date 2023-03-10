package com.example.moveair5.relatedmusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moveair5.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FocusFragment extends Fragment {

    private RecyclerView recyclerView;
    private FocusAdapter adapter;
    private  RecyclerView.LayoutManager layoutManager;
    private ArrayList<Focus> arrayList;
    int checkint = 1;

    ImageButton music, next, previous;
    TextView songname;
    LinearLayout linearLayout;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_focus, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        music = view.findViewById(R.id.focusing_music);
        previous = view.findViewById(R.id.focusing_previous);
        next = view.findViewById(R.id.focusing_next);
        songname = view.findViewById(R.id.focusingnameView);
        linearLayout = view.findViewById(R.id.focusingbar);

        recyclerView = view.findViewById(R.id.focusingview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("song/genre/focus");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Focus focus = dataSnapshot.getValue(Focus.class);
                    arrayList.add(focus);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });// ?????? ????????? ????????????

        adapter = new FocusAdapter(arrayList, getContext());
        adapter.setOnItemClickListener(new FocusAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                PlaySong.itemcount = adapter.getItemCount() - 1;
                PlaySong.position = pos;
                preset2();
            }
        });// ???????????? ?????? ?????????
        recyclerView.setAdapter(adapter);

        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preset();
            }
        });//play, stop ??????

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previous();
            }
        });//previous ??????

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });//next ??????

        if(checkint == 1) {
            music_init();
            checkint++;
        }//??????????????? ?????? ??? ?????????

        //????????????
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(FocusFragment.this).popBackStack(R.id.nav_loginafter, false);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter("Change"));
        }//broadcaster ?????? (??? ????????? ??????)

    } // oncreate

    public void preset2() {
        PlaySong.setAciton(PlaySong.ACTION_INIT);
        getActivity().startService(new Intent(getContext(), PlaySong.class));
    }//????????? ????????? ???????????? ???????????? ?????????

    public void preset() {
        PlaySong.setAciton(PlaySong.ACTION_PLAY);
        getActivity().startService(new Intent(getContext(), PlaySong.class));
    }//music ?????? ????????? ???????????? ?????????

    public void previous() {
        PlaySong.setAciton(PlaySong.ACTION_PREVIUOS);
        getActivity().startService(new Intent(getContext(), PlaySong.class));
    }

    public void next() {
        PlaySong.setAciton(PlaySong.ACTION_NEXT);
        getActivity().startService(new Intent(getContext(), PlaySong.class));
    }// next?????? ????????? ???????????? ?????????

    public void music_init() {
        if(PlaySong.getArrayList() != null) {
            if(PlaySong.mediaPlayer != null) {
                songname.setText("?????? ???????????? ??????: "+PlaySong.getName()+"\n"+PlaySong.getGenrename());
                music.setImageResource(R.drawable.pause);
            } else {
                songname.setText("?????? ???????????? ??????: "+PlaySong.getName()+"\n"+PlaySong.getGenrename());
                music.setImageResource(R.drawable.play);
            }
        }
    }//??? ????????? ???????????? ???????????? ?????????

    //?????? ????????? ?????? ??? ?????????
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("action");

            switch (action){
                case "change":
                    if(PlaySong.mediaPlayer != null) {
                        songname.setText("?????? ???????????? ??????: "+PlaySong.getName()+"\n"+PlaySong.getGenrename());
                        music.setImageResource(R.drawable.pause);
                    } else {
                        songname.setText("?????? ???????????? ??????: "+PlaySong.getName()+"\n"+PlaySong.getGenrename());
                        music.setImageResource(R.drawable.play);
                    }
                    break;
            }
        }
    }; // broadcast?????? ?????? ????????? ?????? ???????????? ????????? ??????

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}