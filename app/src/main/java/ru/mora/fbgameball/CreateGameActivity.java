package ru.mora.fbgameball;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;

public class CreateGameActivity extends AppCompatActivity {

    final static int SELECT_PICTURE = 1;

    Button bt_add;
    TextInputLayout et_name;
    TextInputLayout et_disc;
    ImageView iv_img;
    ConstraintLayout cl_CGA;

    String name, disc, img="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        cl_CGA = (ConstraintLayout) findViewById(R.id.cl_CGA);
        iv_img = (ImageView) findViewById(R.id.iv_img);
        et_name = (TextInputLayout) findViewById(R.id.et_name);
        et_disc = (TextInputLayout) findViewById(R.id.et_dist);
        bt_add = (Button)  findViewById(R.id.bt_add);

        iv_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // считываем данные
                name = et_name.getEditText().getText().toString();
                disc = et_disc.getEditText().getText().toString();

                if (name.equals("") || disc.equals("") || img.equals("")){
                    // если не все данные
                    Snackbar snackbar = Snackbar.make(cl_CGA, "Введите все данные", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                else{
                    // отправка данных в главную активность
                    Intent i = new Intent();
                    i.putExtra("NAME", name);
                    i.putExtra("DISC", disc);
                    i.putExtra("IMG", img);
                    setResult(RESULT_OK, i);
                    finish();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // если пользователь выбирал картинку
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // получаем Uri
                Uri selectedImageURI = data.getData();
                // переводим полученных Uri в реальный Uri
                File imageFile = new File(getRealPathFromURI(this, selectedImageURI));
                selectedImageURI = Uri.fromFile(imageFile);
                // отображаем картинку
                iv_img.setImageURI(selectedImageURI);
                img = selectedImageURI.toString();
            }

        }
    }

    // метод переводящий полученную Uri в реальный Uri
    public static String getRealPathFromURI(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

}