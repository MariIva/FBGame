package ru.mora.fbgameball;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardHolder>{

    private final LayoutInflater inflater;
    private final ArrayList<Game> games;
    private Context mContext;
    int w=-1, h=-1;

    public CardAdapter(Context context, ArrayList<Game> games) {
        this.inflater = LayoutInflater.from(context);
        this.games = games;
        this.mContext = context;
    }
    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_card, parent, false);
        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        holder.tv_title.setText(games.get(position).name);
        holder.tv_disc.setText(games.get(position).disc);

        if (games.get(position).local_img !=null){
            if(w<=0) w = holder.imageView.getWidth();
            if(h<=0) h = holder.imageView.getHeight();
            holder.imageView.setImageURI(Uri.parse(games.get(position).local_img));
        }
        else if (games.get(position).bitmap != null) {
            if(w<=0) w = holder.imageView.getWidth();
            if(h<=0) h = holder.imageView.getHeight();
            holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(games.get(position).bitmap, w, h, false));
        }

    }

    @Override
    public int getItemCount() {
        return games.size();
    }
}

class CardHolder extends RecyclerView.ViewHolder{

    public ImageView imageView;
    public TextView tv_title;
    public TextView tv_disc;

    public CardHolder(@NonNull View itemView) {
        super(itemView);
        // получение всех элементов
        imageView = (ImageView) itemView.findViewById(R.id.img);
        tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        tv_disc = (TextView) itemView.findViewById(R.id.tv_disc);

    }
}