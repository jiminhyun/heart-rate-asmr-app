package com.example.moveair5;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.EditText;

import com.example.moveair5.databinding.ActivityMainBinding;
import com.example.moveair5.relatedmusic.PlaySong;
import com.example.moveair5.relatedmusic.StabilizeSong;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private AppBarConfiguration mAppBarConfiguration;
    FirebaseUser firebaseUser;
    private ActivityMainBinding binding;
    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mFirebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = mFirebaseAuth.getCurrentUser();
        UserAccount account = new UserAccount();
        account.setIdToken(firebaseUser.getUid());
        String a = account.getIdToken();
        databaseReference = FirebaseDatabase.getInstance().getReference("MoveAir/UserAccount/"+a+"/user/yob");

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_loginafter, R.id.nav_bookmark, R.id.nav_member_edit, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if(PlaySong.getArrayList() == null) {
            PlaySong.setName("Baldych-Lunar path");
            PlaySong.setGenrename("기본곡입니다");
        }// 아무 선택 없을 시 defalut 곡 초기화

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN}, 100);
            return;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                setting();
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setting() {
        final EditText SettingEditText = new EditText(MainActivity.this);
        AlertDialog.Builder setting = new AlertDialog.Builder(MainActivity.this);
        setting.setTitle("생년월일 설정")
                .setMessage("(ex)2000,1,1)")
                        .setView(SettingEditText);

        setting.setPositiveButton("완료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String strYob = SettingEditText.getText().toString();
                try {
                    String[] check_age = strYob.split(",");
                    int check = check_age.length;
                    int[] get_age = new int[] {Integer.parseInt(check_age[0]), Integer.parseInt(check_age[1]), Integer.parseInt(check_age[2])};
                    if(getAge(get_age[0], get_age[1], get_age[2]) < 20 || check > 3) {
                        CustomToast.showToast(getApplicationContext(), "생년월일을 제대로 입력해주세요");
                    } else {
                        databaseReference.child("data").removeValue();
                        YobData yobData = new YobData();
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
                        databaseReference.child("data").setValue(yobData);
                        /*Toast.makeText(getApplicationContext(), String.valueOf(getAge(get_age[0], get_age[1], get_age[2])), Toast.LENGTH_SHORT).show();*/
                    }
                } catch (NumberFormatException e) {
                    CustomToast.showToast(getApplicationContext(), "생년월일을 제대로 입력해주세요");
                } catch (IndexOutOfBoundsException e2) {
                    CustomToast.showToast(getApplicationContext(), "생년월일을 제대로 입력해주세요");
                }
            }
        });

        setting.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        setting.show();
    }

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
    }//생명주기
}