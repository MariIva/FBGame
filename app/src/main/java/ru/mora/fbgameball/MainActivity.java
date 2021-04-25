package ru.mora.fbgameball;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 0;
    static final int RC_CREATE = 1;
    static final int RC_LOGIN = 2;

    RecyclerView recyclerView;
    FloatingActionButton fab;

    ArrayList<Game> games = new ArrayList<>();
    CardAdapter adapter;
    String mail;

    FB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionStatus_write = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionStatus_write == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,     // эта активность
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},     // список размешений
                    PERMISSION_WRITE_EXTERNAL_STORAGE);   // код запроса разрешения
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView = findViewById(R.id.rv_card);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        // создаем адаптер заполняющий список
        adapter = new CardAdapter(this, games);
        // устанавливаем адаптер
        recyclerView.setAdapter(adapter);

        // получение доступа к БД и обновление списка
        database = new FB();
        database.updateGameList(games, adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //если пользователь вошел в систему
                if (mail != null){
                    // то создание объекта
                    Intent intent = new Intent(MainActivity.this, CreateGameActivity.class);
                    startActivityForResult(intent, RC_CREATE);
                }
                else{
                    // иначе вход с помощью гугла
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, RC_LOGIN);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_CREATE:
                if(resultCode==RESULT_OK){
                    Game game = new Game();
                    if (data != null) {
                        game.id = database.count;
                        game.name = data.getStringExtra("NAME");
                        game.disc = data.getStringExtra("DISC");
                        game.local_img = data.getStringExtra("IMG");
                     try {
                         // отправка картинки в хранилище
                         game.fb_img = database.putImage(game.id, game.local_img);
                         // отправка записи в БД
                         database.addGame(game);
                         // изменение количества записей
                         database.updateCount();
                     }
                     catch (Exception e){
                         Log.e("onActivityResult", "Load fail", e);
                     }
                    }
                }
                break;
            case RC_LOGIN:
                if(resultCode==RESULT_OK){
                    if (data != null) {
                        // получение почты пользователя
                        mail = data.getStringExtra("ACC");
                        Toast.makeText(this, mail, Toast.LENGTH_SHORT).show();
                    }

                }
        }
    }

}