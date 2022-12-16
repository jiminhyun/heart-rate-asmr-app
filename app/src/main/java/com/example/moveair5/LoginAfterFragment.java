package com.example.moveair5;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.moveair5.relatedmusic.PlaySong;
import com.example.moveair5.relatedmusic.StabilizeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginAfterFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    DatabaseReference databaseReference;
    PlaySong a = new PlaySong();
    DBHelper dbHelper;
    SQLiteDatabase database;
    boolean doubleBackToExitPressedOnce;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        doubleBackToExitPressedOnce = false;
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loginafter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        com.example.moveair5.UserAccount account = new com.example.moveair5.UserAccount();
        account.setIdToken(firebaseUser.getUid());
        String b = account.getIdToken();
        databaseReference = FirebaseDatabase.getInstance().getReference("MoveAir/UserAccount/"+b);

        dbHelper = new DBHelper(getContext());


        //명상 장르 화면
        view.findViewById(R.id.MeditateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LoginAfterFragment.this).navigate(R.id.action_nav_loginafter_to_nav_meditate);
            }
        });

        //수면 장르 화면
        view.findViewById(R.id.SleepButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LoginAfterFragment.this).navigate(R.id.action_nav_loginafter_to_nav_sleep);
            }
        });

        //집중 장르 화면
        view.findViewById(R.id.FocusButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LoginAfterFragment.this).navigate(R.id.action_nav_loginafter_to_nav_focus);
            }
        });

        //안정 장르 화면
        view.findViewById(R.id.StabilizeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                NavHostFragment.findNavController(LoginAfterFragment.this).navigate(R.id.action_nav_loginafter_to_nav_stabilize);
                Intent intent = new Intent(getContext(), StabilizeActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        //뒤로가기 설정
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {//음악 정보 초기화, 해제, 자동 로그인 설정 해제
                if (doubleBackToExitPressedOnce) {
                    getActivity().stopService(new Intent(getActivity(),PlaySong.class));
                    dbHelper = new DBHelper(getContext());
                    a.setEmail("");
                    a.setPassword("");
                    database = dbHelper.getWritableDatabase();
                    database.delete(DBContract.TABLE_NAME, "check1=?",
                            new String[] {String.valueOf(1)});
                    mFirebaseAuth = FirebaseAuth.getInstance();
                    mFirebaseAuth.signOut();
                    CustomToast.showToast(getActivity().getApplicationContext(), "로그아웃 되었습니다.");
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return;
                }

                doubleBackToExitPressedOnce = true;
                CustomToast.showToast(getActivity().getApplicationContext(), "뒤로가기를 한번 더 누르면 로그아웃됩니다.");

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
            }
        });

    }// oncreate
}