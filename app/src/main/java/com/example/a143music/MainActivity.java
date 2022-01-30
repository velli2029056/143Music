package com.example.a143music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lst;
    String[] items;
    SlidingUpPanelLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* layout=findViewById(R.id.sliding_layout);
        layout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
           //     findViewById(R.id.view).setAlpha(1-slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }
        });*/
        lst=findViewById(R.id.songs);
        runtimePermission();
        displaySongs();
    }
    public ArrayList<Track> findSong(File file){
        ArrayList<Track> al=new ArrayList<>();
        /*File[] files=file.listFiles();
        for(File singleFile:files){
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                al.addAll(findSong(singleFile));
            }
            else
            {
                if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav"))
                {
                    al.add(singleFile);
                }
            }
        }*/
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.AlbumColumns.ALBUM,
                MediaStore.Audio.AlbumColumns.ARTIST,
                MediaStore.Audio.AlbumColumns.,
                MediaStore.Audio.Media.DISPLAY_NAME
        };
        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor c = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
        if(c!=null) {
            c.moveToFirst();
            while (c.moveToNext()) {
                Track sd = new Track();
                @SuppressLint("Range") String title = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                @SuppressLint("Range") String key = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY));
                @SuppressLint("Range") String artist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                @SuppressLint("Range") String album = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                @SuppressLint("Range") long albumId = c.getLong(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
               // @SuppressLint("Range") String composer = c.getString(c.getColumnIndex(MediaStore.Audio.Media.COMPOSER));
                sd.setTitle(title);
                sd.setAlbum(album);
                sd.setArtist(artist);
                sd.setAlbumId(albumId);
                al.add(sd);
            }
        }
        System.out.println("---------"+al.size()+"------------");
        return al;
    }
    public void displaySongs()
    {
        final ArrayList<Track> mySongs=findSong(Environment.getExternalStorageDirectory());
        items =new String[mySongs.size()];
        for(int i=0;i<mySongs.size();i++){
            items[i]=mySongs.get(i).getTitle().toString().replace(".mp3","").replace(".wav","");
        }
        ArrayAdapter<String> myadap=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items);
        lst.setAdapter(myadap);
        customAdapter csad=new customAdapter();
        lst.setAdapter(csad);
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName=(String) lst.getItemAtPosition(position);
               // layout.setDragView(R.layout.activity_player);
                startActivity(new Intent(getApplicationContext(),PlayerActivity.class)
                .putExtra("songs",mySongs)
                .putExtra("songname",songName)
                .putExtra("pos",position));
              //  layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });
    }
    public void runtimePermission()
    {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},1);
        }
    }

    class customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View myview=getLayoutInflater().inflate(R.layout.list_item,null);
            TextView textSong=myview.findViewById(R.id.name);
            textSong.setSelected(true);
            textSong.setText(items[position]);
            return myview;
        }
    }
}