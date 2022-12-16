package com.example.moveair5;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moveair5.relatedmusic.PlaySong;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ModifyFragment extends Fragment {

    UserAccount userAccount;
    private FirebaseAuth mFirebaseAuth;
    DatabaseReference databaseReference;
    TextView textView, textView2;
    EditText editText, editText2;
    DBHelper dbHelper;
    SQLiteDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modify, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();

        textView = view.findViewById(R.id.EmailAddress);
        textView2 = view.findViewById(R.id.YobTextview);
        editText = view.findViewById(R.id.editTextTextPassword2);
        editText2 = view.findViewById(R.id.editTextTextPasswordCheck);

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        UserAccount account = new UserAccount();
        account.setIdToken(firebaseUser.getUid());
        String a = account.getIdToken();
        databaseReference = FirebaseDatabase.getInstance().getReference("MoveAir/UserAccount/"+a);

        databaseReference.child("user/yob/data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    YobData yobData = snapshot.getValue(YobData.class);
                    textView2.setText("생년월일 정보: "+yobData.getYob());
                } catch (NullPointerException e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userAccount = dataSnapshot.getValue(UserAccount.class);
                    textView.setText("Email: "+ userAccount.getEmailId());
                }
                //탈퇴
                view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                        ad.setMessage("정말로 탈퇴하시겠습니까?");

                        ad.setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                databaseReference.removeValue();
                                firebaseUser.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(PlaySong.mediaPlayer != null) {
                                                    PlaySong.mediaPlayer.release();
                                                    PlaySong.mediaPlayer = null;
                                                    getActivity().stopService(new Intent(getActivity(),PlaySong.class));
                                                }
                                                dbHelper = new DBHelper(getContext());
                                                database = dbHelper.getWritableDatabase();
                                                database.delete(DBContract.TABLE_NAME, "check1=?",
                                                        new String[] {String.valueOf(1)});
                                                CustomToast.showToast(getActivity().getApplicationContext(), "탈퇴처리가 정상적으로 진행되었습니다.");
                                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                        });
                            }
                        });

                        ad.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        ad.show();
                    }
                });
                //수정
                view.findViewById(R.id.button24).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String a = editText.getText().toString().trim();
                        String b = editText2.getText().toString().trim();

                        if(a.isEmpty() || b.isEmpty()) {
                            CustomToast.showToast(getActivity().getApplicationContext(), "누락된 입력값이 있습니다.");
                        } else if(a.length() < 6) {
                            CustomToast.showToast(getActivity().getApplicationContext(), "비밀번호 6글자 이상 입력하세요.");
                        } else if(!(a.equals(b))) {
                            CustomToast.showToast(getActivity().getApplicationContext(), "비밀번호를 다시 확인해주세요.");
                        } else {
                            firebaseUser.updatePassword(b).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    dbHelper = new DBHelper(getContext());
                                    database = dbHelper.getWritableDatabase();
                                    database.delete(DBContract.TABLE_NAME, "check1=?",
                                            new String[] {String.valueOf(1)});
                                    ContentValues values = new ContentValues();
                                    values.put("email", userAccount.getEmailId());
                                    values.put("password", a);
                                    values.put("check1", "1");
                                    database.insert(DBContract.TABLE_NAME, null, values);
                                    CustomToast.showToast(getActivity().getApplicationContext(), "비밀번호 수정이 성공적으로 처리되었습니다.");
                                    NavHostFragment.findNavController(ModifyFragment.this).navigate(R.id.action_nav_member_edit_to_nav_loginafter);
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //뒤로가기
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(ModifyFragment.this).popBackStack(R.id.nav_loginafter, false);
            }
        });
    }// oncreate
}