package com.helloworld.homework02;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MusicAdapter extends ArrayAdapter {

    public MusicAdapter(@NonNull Context context, int resource, @NonNull List<MusicInfo> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MusicInfo musicInfo = (MusicInfo) getItem(position);
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_tracks,parent,false);
            TextView textViewTrack = convertView.findViewById(R.id.textViewTrack);
            TextView textViewPrice = convertView.findViewById(R.id.textViewPrice);
            TextView textViewArtist = convertView.findViewById(R.id.textViewArtistName);
            TextView textViewDate = convertView.findViewById(R.id.textViewDate);
            viewHolder = new ViewHolder(textViewTrack, textViewPrice, textViewArtist, textViewDate);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textViewTrack.setText(musicInfo.track_name);
        viewHolder.textViewPrice.setText(String.valueOf(musicInfo.track_price)+" $");
        viewHolder.textViewArtist.setText(musicInfo.artist);
        Date date = null;
        String releaseDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat OutputFormat = new SimpleDateFormat("MM-dd-yyyy");
        try{
            date = format.parse(musicInfo.release_date);
            releaseDate = OutputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.textViewDate.setText(releaseDate);

        return convertView;
    }

    public static class ViewHolder{
        TextView textViewTrack,textViewPrice,textViewArtist,textViewDate;

        public ViewHolder(TextView textViewTrack, TextView textViewPrice, TextView textViewArtist, TextView textViewDate) {
            this.textViewTrack = textViewTrack;
            this.textViewPrice = textViewPrice;
            this.textViewArtist = textViewArtist;
            this.textViewDate = textViewDate;
        }
    }
}
