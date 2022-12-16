package com.example.moveair5.relatedmusic;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.moveair5.MainActivity;
import com.example.moveair5.R;
import com.example.moveair5.Services.NotificationActionService;

import java.util.ArrayList;

public class PlaySong extends Service {
    public static String name, genrename;
    String email = "";
    String password = "";
    public static int genre, route, position, itemcount, checkid = 0;
    public static MediaPlayer mediaPlayer;
    public static ArrayList<Music> arrayList = null;
    public static String CHANNEL_ID = "channel_music";
    public static Context context;
    public static NotificationManagerCompat notificationManager;

    public static RemoteViews remoteViews;
    public static final String ACTION_PREVIUOS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_CANCEL = "actioncancel";
    public static final String ACTION_INIT = "actioninit";
    public static String aciton = null;

    public static String getAciton() {
        return aciton;
    }

    public static void setAciton(String aciton) {
        PlaySong.aciton = aciton;
    }

    public NotificationCompat.Builder builder;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static int getItemcount() {
        return itemcount;
    }

    public static String getGenrename() {
        return genrename;
    }

    public static void setGenrename(String genrename) {
        PlaySong.genrename = genrename;
    }

    public static void setItemcount(int itemcount) {
        PlaySong.itemcount = itemcount;
    }

    public static int getPosition() {
        return position;
    }

    public static void setPosition(int position) {
        PlaySong.position = position;
    }

    public static ArrayList getArrayList() {
        return arrayList;
    }

    public static void setArrayList(ArrayList arrayList) {
        PlaySong.arrayList = arrayList;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        PlaySong.name = name;
    }

    public static int getGenre() {
        return genre;
    }

    public static void setGenre(int genre) {
        PlaySong.genre = genre;
    }

    public static int getRoute() {
        return route;
    }

    public static void setRoute(int route) {
        PlaySong.route = route;
    }

    public PlaySong() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //Service객체와 Activity사이에서 통신을 할 때 사용되는 메서드
        //데이터 전달이 필요 없으면 null
        return null;
    }

    @Override
    public void onCreate() {
        //서비스에서 가장 먼저 호출(최초한번)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
        }//broadcaster 등록
        createNotificationChannel();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();
        return super.onStartCommand(intent, flags, startId);
    } // startService()에 의해 이 메소드가 실행된다. 얘는 한번만 호출되는 것이 아니기 때문에 여러번 호출이 필요할 경우 여기서 메소드 넣기

    public void startForegroundService() {
        if(getAciton() != null && checkid == 0) {
            switch (getAciton()){
                case ACTION_PREVIUOS:
                    if(arrayList == null) {

                    } else {
                        if(position == 0) {
                            position = itemcount;
                        } else {
                            position = position - 1;
                        }
                        setName(arrayList.get(position).getName());
                        setGenre(arrayList.get(position).getGenre());
                        setRoute(arrayList.get(position).getRoute());
                    }
                    if(PlaySong.mediaPlayer != null) {
                        PlaySong.mediaPlayer.release();
                        PlaySong.mediaPlayer = null;
                    }
                    PlaySong.mediaPlayer = MediaPlayer.create(getApplicationContext(), Song.resid[getGenre()][getRoute()]);
                    mediaPlayer.setLooping(true);
                    PlaySong.mediaPlayer.start();
                    checkid = 1;
                    break;
                case ACTION_PLAY:
                case ACTION_INIT:
                    if (mediaPlayer != null) { //음악 재생 중
                        PlaySong.mediaPlayer.release();
                        PlaySong.mediaPlayer = null;
                    }
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), Song.resid[getGenre()][getRoute()]);
                    mediaPlayer.setLooping(true);
                    PlaySong.mediaPlayer.start();
                    checkid = 1;
                    break;
                case ACTION_NEXT:
                    if(arrayList == null) {

                    } else {
                        if(position == itemcount) {
                            position = 0;
                        } else {
                            position = position + 1;
                        }
                        setName(arrayList.get(position).getName());
                        setGenre(arrayList.get(position).getGenre());
                        setRoute(arrayList.get(position).getRoute());
                    }
                    if(PlaySong.mediaPlayer != null) {
                        PlaySong.mediaPlayer.release();
                        PlaySong.mediaPlayer = null;
                    }
                    PlaySong.mediaPlayer = MediaPlayer.create(getApplicationContext(), Song.resid[getGenre()][getRoute()]);
                    mediaPlayer.setLooping(true);
                    PlaySong.mediaPlayer.start();
                    checkid = 1;
                    break;
            }
            Intent notifyIntent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(),0,notifyIntent,PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
            remoteViews.setImageViewResource(R.id.titleView, R.drawable.title);
            remoteViews.setImageViewResource(R.id.music_previous, R.drawable.previous);
            remoteViews.setImageViewResource(R.id.music_play, R.drawable.pause);
            remoteViews.setImageViewResource(R.id.cancel_button, R.drawable.cancel);
            remoteViews.setImageViewResource(R.id.music_next, R.drawable.next);
            remoteViews.setTextViewText(R.id.genreView, PlaySong.getGenrename());
            remoteViews.setTextViewText(R.id.nameView, "노래제목: "+PlaySong.getName());
            remoteViews.setOnClickPendingIntent(R.id.titleView, pIntent);
            Intent intentPrevious = new Intent(getApplicationContext(), NotificationActionService.class)
                    .setAction(ACTION_PREVIUOS);
            PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    intentPrevious, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            Intent intentPlay = new Intent(getApplicationContext(), NotificationActionService.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    intentPlay, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            Intent intentNext = new Intent(getApplicationContext(), NotificationActionService.class)
                    .setAction(ACTION_NEXT);
            PendingIntent pendingIntentNext = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    intentNext, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            Intent intentCancel = new Intent(getApplicationContext(), NotificationActionService.class)
                    .setAction(ACTION_CANCEL);
            PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    intentCancel, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.music_previous, pendingIntentPrevious);
            remoteViews.setOnClickPendingIntent(R.id.music_play, pendingIntentPlay);
            remoteViews.setOnClickPendingIntent(R.id.music_next, pendingIntentNext);
            remoteViews.setOnClickPendingIntent(R.id.cancel_button, pendingIntentCancel);

            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_music_note)
                    .setCustomBigContentView(remoteViews)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            startForeground(1,builder.build()); // 알림 제거 불가
            setAciton(null);
            checkid = 1;
            //알림뷰 있을 때 밑에는
        } else if(getAciton() == ACTION_PREVIUOS) {
            setAciton(null);
            previous_response();
        } else if(getAciton() == ACTION_PLAY) {
            setAciton(null);
            preset_response();
        } else if(getAciton() == ACTION_NEXT) {
            setAciton(null);
            next_response();
        } else if(getAciton() == ACTION_INIT) {
            Log.i("생성되는가2", "그렇다");
            setAciton(null);
            init_response();
        } else {

        }
        //checkid = 1; notification 생성 중
        if(checkid == 1 && getAciton() == null) {
            updateNotification();
        }
        sendBroadcast(new Intent("Change").putExtra("action", "change"));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PlaySong.context = getApplicationContext();
            CharSequence name = "moveair";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(PlaySong.CHANNEL_ID, name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void updateNotification() {
        remoteViews.setTextViewText(R.id.genreView, getGenrename());
        remoteViews.setTextViewText(R.id.nameView, "노래제목: " + getName());
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1,builder.build());
    }

    @Override
    public void onDestroy() {
        setArrayList(null);
        setGenrename(null);
        setName(null);
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        checkid = 0;
        sendBroadcast(new Intent("Change").putExtra("action", "change"));
        stopForeground(true);
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    } // stopService()에 의해 이 메소드가 실행된다.

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action){
                case PlaySong.ACTION_PREVIUOS:
                    previous_response();
                    break;
                case PlaySong.ACTION_PLAY:
                    preset_response();
                    break;
                case PlaySong.ACTION_NEXT:
                    next_response();
                    break;
                case PlaySong.ACTION_CANCEL:
                    cancel_response();
                    break;
                case PlaySong.ACTION_INIT:
                    init_response();
                    break;
            }
        }
    }; // broadcast에서 받은 내용에 따라 호출되는 메소드 설정

    //broadcast에 따라 곡 정보, 뷰와 이미지 재설정
    public void previous_response() {
        if(arrayList == null) {

        } else {
            if(position == 0) {
                position = itemcount;
            } else {
                position = position - 1;
            }
            setName(arrayList.get(position).getName());
            setGenre(arrayList.get(position).getGenre());
            setRoute(arrayList.get(position).getRoute());
        }
        if(PlaySong.mediaPlayer != null) {
            PlaySong.mediaPlayer.release();
            PlaySong.mediaPlayer = null;
        }
        PlaySong.mediaPlayer = MediaPlayer.create(getApplicationContext(), Song.resid[getGenre()][getRoute()]);
        mediaPlayer.setLooping(true);
        PlaySong.mediaPlayer.start();
        remoteViews.setImageViewResource(R.id.music_play, R.drawable.pause);
        checkid = 1;
        startForegroundService();
    }

    public void preset_response() {
        if (mediaPlayer != null) { //음악 재생 중
            PlaySong.mediaPlayer.release();
            PlaySong.mediaPlayer = null;
            remoteViews.setImageViewResource(R.id.music_play, R.drawable.play);
        } else { //음악 재생 없음
            mediaPlayer = MediaPlayer.create(getApplicationContext(), Song.resid[getGenre()][getRoute()]);
            mediaPlayer.setLooping(true);
            PlaySong.mediaPlayer.start();
            remoteViews.setImageViewResource(R.id.music_play, R.drawable.pause);
        }
        checkid = 1;
        startForegroundService();
    }

    public void next_response() {
        if(arrayList == null) {

        } else {
            if(position == itemcount) {
                position = 0;
            } else {
                position = position + 1;
            }
            setName(arrayList.get(position).getName());
            setGenre(arrayList.get(position).getGenre());
            setRoute(arrayList.get(position).getRoute());
        }
        if(PlaySong.mediaPlayer != null) {
            PlaySong.mediaPlayer.release();
            PlaySong.mediaPlayer = null;
        }
        PlaySong.mediaPlayer = MediaPlayer.create(getApplicationContext(), Song.resid[getGenre()][getRoute()]);
        mediaPlayer.setLooping(true);
        PlaySong.mediaPlayer.start();
        remoteViews.setImageViewResource(R.id.music_play, R.drawable.pause);
        checkid = 1;
        startForegroundService();
    }

    public void cancel_response() {
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        checkid = 0;
        sendBroadcast(new Intent("Change").putExtra("action", "change"));
        stopForeground(true);
    }

    public void init_response() {
        if(PlaySong.mediaPlayer != null) {
            PlaySong.mediaPlayer.release();
            PlaySong.mediaPlayer = null;
        }
        PlaySong.mediaPlayer = MediaPlayer.create(getApplicationContext(), Song.resid[getGenre()][getRoute()]);
        mediaPlayer.setLooping(true);
        PlaySong.mediaPlayer.start();
        remoteViews.setImageViewResource(R.id.music_play, R.drawable.pause);
        checkid = 1;
        startForegroundService();
    }
}
