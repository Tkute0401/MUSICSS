package com.example.onlinemusicapp.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinemusicapp.Model.Getsongs;
import com.example.onlinemusicapp.Model.Utility;
import com.example.onlinemusicapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

public class JcSongsAdapter extends RecyclerView.Adapter<JcSongsAdapter.SongAdapterViewHolder> {

    private int selectedPosition;
    Context context;
    List<Getsongs> arraylistsongs;
    private RecyclerItemClickListener listener;
    StorageReference storageReference, mreference;
    private static final String TAG = "JcSongsAdapter";

    public JcSongsAdapter(Context context, List<Getsongs> arraylistsongs, RecyclerItemClickListener listener) {
        this.context = context;
        this.arraylistsongs = arraylistsongs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.songs_row, viewGroup, false);
        return new SongAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapterViewHolder songAdapterViewHolder, int i) {
        Getsongs getsongs = arraylistsongs.get(i);

        if (getsongs != null) {
            if (selectedPosition == i) {
                songAdapterViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_200));
                songAdapterViewHolder.iv_play_active.setVisibility(View.VISIBLE);
            } else {
                songAdapterViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                songAdapterViewHolder.iv_play_active.setVisibility(View.INVISIBLE);
            }
        }

        songAdapterViewHolder.tv_title.setText(getsongs.getSongTitle());
        songAdapterViewHolder.tv_artist.setText(getsongs.getArtist());
        String duration = Utility.converDuration(Long.parseLong(getsongs.getsongDuration()));
        songAdapterViewHolder.tv_duration.setText(duration);
        songAdapterViewHolder.bind(getsongs, listener);

        songAdapterViewHolder.downloadsong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    int position = songAdapterViewHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        downloadSong(position);
                    }
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arraylistsongs.size();
    }

    private void downloadSong(int position) {
        storageReference = FirebaseStorage.getInstance().getReference().child("songs");
        mreference = storageReference.child(arraylistsongs.get(position).getSongTitle() + ".mp3");
        File localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), arraylistsongs.get(position).getSongTitle() + ".mp3");

        long ONE_MEGABYTE = 1024 * 1024;
        mreference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "File downloaded: " + localFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "On Success: File Downloaded " + localFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Download failed", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Download failed", e);
            }
        });
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true; // Permission is granted on lower Android versions by default
        }
    }

    public class SongAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title, tv_artist, tv_duration;
        ImageView iv_play_active, downloadsong;
        RelativeLayout songlayout;

        public SongAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_artist = itemView.findViewById(R.id.tv_artist);
            tv_duration = itemView.findViewById(R.id.tv_duration);
            tv_title = itemView.findViewById(R.id.tv_title);
            iv_play_active = itemView.findViewById(R.id.play_active);
            songlayout = itemView.findViewById(R.id.song_layout);
            downloadsong = itemView.findViewById(R.id.downloadsong);
        }

        public void bind(Getsongs getsongs, RecyclerItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onClickListener(getsongs, position);
                    }
                }
            });
        }
    }

    public interface RecyclerItemClickListener {
        void onClickListener(Getsongs getsongs, int position);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
}
