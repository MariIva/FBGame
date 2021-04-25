package ru.mora.fbgameball;

import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;

public class Game {
    public long id;
    public String name;
    public String disc;
    public String fb_img;
    // игнорирование поля про добавлении в БД
    @Exclude
    public String local_img;
    @Exclude
    public Bitmap bitmap;

    public Game() {
    }
}
