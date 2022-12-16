package com.example.moveair5;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moveair5.relatedmusic.PlaySong;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutFragment extends Fragment {
    private FirebaseAuth mFirebaseAuth;
    PlaySong a = new PlaySong();
    DBHelper dbHelper;
    SQLiteDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
    }// oncreate
}