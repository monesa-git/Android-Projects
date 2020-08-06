package com.helloworld.homework02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class MusicInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_info);

        TextView textViewTrackName = findViewById(R.id.textViewTrackName);
        TextView textViewGenreName = findViewById(R.id.textViewGenreName);
        TextView textViewArtistName = findViewById(R.id.textView3);
        TextView textViewAlbumName = findViewById(R.id.textViewAlbumName);
        TextView textViewTrackPrice = findViewById(R.id.textViewTrackPrice);
        TextView textViewCollectionPrice = findViewById(R.id.textViewCollectionPrice);
        ImageView imageView = findViewById(R.id.imageView);

        MusicInfo musicInfoIntent = (MusicInfo) getIntent().getExtras().getSerializable("MusicInfoAttributes");

        Picasso.get()
                .load(musicInfoIntent.trackUrl)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(MusicInfoActivity.this, "No Image found", Toast.LENGTH_SHORT).show();
                    }
                });

        textViewTrackName.setText("Track : "+musicInfoIntent.track_name);
        textViewGenreName.setText("Genre : "+musicInfoIntent.genre);
        textViewArtistName.setText("Artist : "+musicInfoIntent.artist);
        textViewAlbumName.setText("Album : "+musicInfoIntent.album);
        textViewTrackPrice.setText("Track Price : "+musicInfoIntent.track_price+" $");
        textViewCollectionPrice.setText("Album Price : "+musicInfoIntent.collection_price+" $");


        findViewById(R.id.buttonFinish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }
}
