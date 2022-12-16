package com.example.moveair5;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moveair5.relatedmusic.PlaySong;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText mEtEmail, mEtPwd; //로그인 입력필드
    PlaySong a = new PlaySong();
    DBHelper dbHelper;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("MoveAir");

        mEtEmail = findViewById(R.id.et_email_first);
        mEtPwd = findViewById(R.id.et_pwd_first);
        Button btn_register = findViewById(R.id.btn_register);

        dbHelper = new DBHelper(this);
        check();

        Button btn_pwd_forget = findViewById(R.id.forget_pwd);
        Button btn_login = findViewById(R.id.btn_login);

        btn_pwd_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, com.example.moveair5.SearchActivity.class);
                startActivity(intent);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그인 요청
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                if(strEmail.isEmpty() || strPwd.isEmpty()) {
                    CustomToast.showToast(getApplicationContext(), "누락된 입력값이 있습니다.");
                } else {
                    mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //로그인 성공
                                    a.setEmail(strEmail);
                                    a.setPassword(strPwd);
                                    ContentValues values = new ContentValues();
                                    values.put("email", a.getEmail());
                                    values.put("password", a.getPassword());
                                    values.put("check1", "1");
                                    database = dbHelper.getWritableDatabase();
                                    database.insert(DBContract.TABLE_NAME, null, values);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                /*intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);*/
                                startActivity(intent);
                            } else {
                                CustomToast.showToast(getApplicationContext(), "이메일 또는 비밀번호를 확인해주세요.");
                            }
                        }
                    });
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원가입 화면으로 이동
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }// oncreate

    @Override
    protected void onRestart() {
        check();
        super.onRestart();
    }

    public void check() {
        database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(DBContract.SQL_LOAD, null);
        while(cursor.moveToNext()) {
            a.setEmail(cursor.getString(0));
            a.setPassword(cursor.getString(1));
        }
        if(!(a.getEmail().isEmpty() && a.getPassword().isEmpty())) {
            mFirebaseAuth.signInWithEmailAndPassword(a.getEmail(), a.getPassword()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //로그인 성공
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }
}