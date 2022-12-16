package com.example.moveair5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText mEtEmail, mEtPwd, mEtPwdCheck, mEtAge; //회원가입 입력필드
    private Button mBtnRegister; // 회원가입 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("MoveAir");

        mEtEmail = findViewById(R.id.et_email_second);
        mEtPwd = findViewById(R.id.et_pwd_second);
        mEtPwdCheck = findViewById(R.id.et_pwd_check);
        mEtAge = findViewById(R.id.et_age);
        mBtnRegister = findViewById(R.id.btn_register);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원가입 처리 시작
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();
                String strPwdCheck = mEtPwdCheck.getText().toString();
                String strYob = mEtAge.getText().toString();
                try {
                    String[] check_age = strYob.split(",");
                    int check = check_age.length;
                    int[] get_age = new int[] {Integer.parseInt(check_age[0]), Integer.parseInt(check_age[1]), Integer.parseInt(check_age[2])};
                    if(strEmail.isEmpty() || strPwd.isEmpty() || strPwdCheck.isEmpty()) {
                        CustomToast.showToast(getApplicationContext(), "누락된 입력값이 있습니다.");
                    } else if(!(strPwd.equals(strPwdCheck))) {
                        CustomToast.showToast(getApplicationContext(), "비밀번호를 다시 확인해주세요.");
                    } else if(strPwd.length() < 6) {
                        CustomToast.showToast(getApplicationContext(), "비밀번호 6글자 이상 입력하세요.");
                    } else if(getAge(get_age[0], get_age[1], get_age[2]) < 20 || check > 3) {
                        CustomToast.showToast(getApplicationContext(), "생년월일을 제대로 입력해주세요.");
                    } else {
                        // firebase auth 진행
                        mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                    UserAccount account = new UserAccount();
                                    YobData yobData = new YobData();
                                    account.setIdToken(firebaseUser.getUid());
                                    account.setEmailId(firebaseUser.getEmail());
                                    yobData.setYob(strYob);

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(strYob)
                                            .build();

                                    firebaseUser.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                    }
                                                }
                                            });

                                    // setvalue : database에 insert (삽입 행위)
                                    mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("user").setValue(account);
                                    mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).child("user/yob/data").setValue(yobData);

                                    CustomToast.showToast(getApplicationContext(), "회원가입에 성공하였습니다.");
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    CustomToast.showToast(getApplicationContext(), "회원가입에 실패하였습니다.");
                                }
                            }
                        });
                    }
                } catch (NumberFormatException e) {
                    CustomToast.showToast(getApplicationContext(), "생년월일을 제대로 입력해주세요.");
                } catch (IndexOutOfBoundsException e2) {
                    CustomToast.showToast(getApplicationContext(), "생년월일을 제대로 입력해주세요.");
                }
            }
        });
    }// oncreate

    public int getAge(int birthYear, int birthMonth, int birthDay)
    {
        Calendar current = Calendar.getInstance();

        int currentYear  = current.get(Calendar.YEAR);
        int currentMonth = current.get(Calendar.MONTH) + 1;
        int currentDay   = current.get(Calendar.DAY_OF_MONTH);

        // 만 나이 구하기 2022-1995=27 (현재년-태어난년)
        int age = currentYear - birthYear;
        // 만약 생일이 지나지 않았으면 -1
        if (birthMonth * 100 + birthDay > currentMonth * 100 + currentDay)
            age--;
        // 5월 26일 생은 526
        // 현재날짜 5월 25일은 525
        // 두 수를 비교 했을 때 생일이 더 클 경우 생일이 지나지 않은 것이다.
        return age;
    }
}