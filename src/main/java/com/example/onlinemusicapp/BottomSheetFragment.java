package com.example.onlinemusicapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
public class BottomSheetFragment extends BottomSheetDialogFragment {
    OnlineMediaPlayer ol;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        // Set your lyrics here
        String lyrics = "Your lyrics go here...";
        //String songTitle = ol.currentSong.getSongTitle();
        //String artistName = ol.currentSong.getArtist();
        String songTitle = "channa mereya";
        String artistName = "Arijit singh";
        TextView tvLyrics = view.findViewById(R.id.lyricsTextView);
        LyricsScraper.searchAndPrintLyrics(songTitle, artistName, tvLyrics);
        return view;
    }
}
