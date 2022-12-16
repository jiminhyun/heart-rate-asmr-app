package com.example.moveair5.relatedmusic;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.moveair5.R;

import java.util.ArrayList;

public class StabilizeSong {

    public static String name, statename;
    public static int genre, route, position, itemcount;
    public static MediaPlayer mediaPlayer;
    public static ArrayList<Music> arrayList = null;

    public static String getStatename() {
        return statename;
    }

    public static void setStatename(String statename) {
        StabilizeSong.statename = statename;
    }

    public static int getItemcount() {
        return itemcount;
    }

    public static void setItemcount(int itemcount) {
        StabilizeSong.itemcount = itemcount;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        StabilizeSong.name = name;
    }

    public static int getGenre() {
        return genre;
    }

    public static void setGenre(int genre) {
        StabilizeSong.genre = genre;
    }

    public static int getRoute() {
        return route;
    }

    public static void setRoute(int route) {
        StabilizeSong.route = route;
    }

    public static int getPosition() {
        return position;
    }

    public static void setPosition(int position) {
        StabilizeSong.position = position;
    }

    public static ArrayList<Music> getArrayList() {
        return arrayList;
    }

    public static void setArrayList(ArrayList arrayList) {
        StabilizeSong.arrayList = arrayList;
    }

    public StabilizeSong() {
    }

    public static void previous_response(Context context) {
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
            if(StabilizeSong.mediaPlayer != null) {
                StabilizeSong.mediaPlayer.release();
                StabilizeSong.mediaPlayer = null;
            }
            StabilizeSong.mediaPlayer = MediaPlayer.create(context, Song.resid[getGenre()][getRoute()]);
            mediaPlayer.setLooping(true);
            StabilizeSong.mediaPlayer.start();
        }
    }

    public static void next_response(Context context) {
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
            if(StabilizeSong.mediaPlayer != null) {
                StabilizeSong.mediaPlayer.release();
                StabilizeSong.mediaPlayer = null;
            }
            StabilizeSong.mediaPlayer = MediaPlayer.create(context, Song.resid[getGenre()][getRoute()]);
            mediaPlayer.setLooping(true);
            StabilizeSong.mediaPlayer.start();
        }
    }

    public static void preset_response(Context context) {
        if(arrayList == null) {

        } else {
            if (mediaPlayer != null) { //음악 재생 중
                StabilizeSong.mediaPlayer.release();
                StabilizeSong.mediaPlayer = null;
            } else { //음악 재생 없음
                mediaPlayer = MediaPlayer.create(context, Song.resid[getGenre()][getRoute()]);
                mediaPlayer.setLooping(true);
                StabilizeSong.mediaPlayer.start();
            }
        }
    }

    public static void init_response(Context context) {
        if(arrayList == null) {

        } else {
            if (mediaPlayer != null) { //음악 재생 중
                StabilizeSong.mediaPlayer.release();
                StabilizeSong.mediaPlayer = null;
            }
            mediaPlayer = MediaPlayer.create(context, Song.resid[getGenre()][getRoute()]);
            mediaPlayer.setLooping(true);
            StabilizeSong.mediaPlayer.start();
        }
    }
}
