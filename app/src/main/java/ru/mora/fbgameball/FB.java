package ru.mora.fbgameball;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

public class FB {

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    FirebaseStorage storage ;
    StorageReference storageRef;

    long count;

    public FB() {
        // доступ к базе данных
        database = FirebaseDatabase.getInstance();
        getCount();
        // доступ к хранилищу
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        // доступ к базе пользователей
        mAuth = FirebaseAuth.getInstance();
    }

    // получение количества записей
    public void getCount(){
        // получение ссылки на запись с ключем count
        DatabaseReference myCount = database.getReference("count");
        // получение данных
        myCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                count = dataSnapshot.getValue(Long.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    // обновление количесва записи
    public void updateCount(){
        count++;
        // получение ссылки на запись с ключем count
        DatabaseReference myCount = database.getReference("count");
        // установка значений
        myCount.setValue(count);
    }

    // обносление списка
    public void updateGameList(ArrayList<Game> games, CardAdapter adapter){
        // получение ссылки на запись с ключем game
        databaseReference = database.getReference("game");
        databaseReference.addValueEventListener(new  ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                games.clear();
                // получение объектов класса из БД
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // перевод элемента в объект
                    Game post = postSnapshot.getValue(Game.class);
                    // получение картинки
                    loadImage(post, adapter);
                    games.add(post);
                }
                // обновление списка
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    // отправка объекта в бд
    public void addGame(Game game){
        // получение ссылки на БД
        DatabaseReference myRef = database.getReference();
        // установка значений
        myRef.child("game").child(""+count).setValue(game);
    }

    // отправка картинки на хранилище
    public String putImage(long id, String uri){
        // составление имени картинки
        String url = id+".jpg";
        // создание и получение ссылки на картинку
        StorageReference reference = storageRef.child("images/"+url);
        // получение пути к картинке
        Uri file = Uri.parse(uri);
        // отправка картинки в хранилище
        UploadTask  uploadTask = reference.putFile(file);

        // отслеживание результата загрузки
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e("putImage/onFailure", exception.getMessage(), exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.i("putImage/onSuccess", taskSnapshot.toString());
            }
        });
        return url;
    }

    // загрузка картинки из хранилища
    public void loadImage(Game game, CardAdapter adapter){
        // получение ссылки на картинку
        StorageReference islandRef = storageRef.child("images/"+game.fb_img);
        //  ограничение на размер картинки
        final long ONE_MEGABYTE = 4000 * 4000;
        // получение картинки из хранилища
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // если картинка загрузилась
                Log.i("onSuccess/onSuccess", "byte[] bytes");
                game.bitmap = BitmapFactory.decodeByteArray(bytes, 0,bytes.length);
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.e("loadImage/onFailure", exception.getMessage(), exception);
            }
        });
    }

}
