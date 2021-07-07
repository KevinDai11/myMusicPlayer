package com.example.mymusicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView songs;
    String[] items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songs = findViewById(R.id.songs);
        runtimePermission();
    }


    public void runtimePermission(){
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                displaySongs();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    public ArrayList<File> findsong (File f){
        ArrayList<File> list = new ArrayList<>();
        File[] files = f.listFiles();
        for(File g : files){
            if(g.isDirectory() && !g.isHidden()){
                list.addAll(findsong(g));
            }else{
                if(g.getName().endsWith(".mp3")||g.getName().endsWith(".wav")){
                    list.add(g);
                }
            }
        }
        return list;
    }

    public void displaySongs(){
        final ArrayList<File> mySongs = findsong(Environment.getExternalStorageDirectory());
        items = new String[mySongs.size()];
        for(int x=0;x<mySongs.size();x++){
            items[x]=mySongs.get(x).getName().toString().replace(".mp3","").replace(".wav","");

        }
        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items);
        songs.setAdapter(adapter);*/
        customAdapter cA = new customAdapter();
        songs.setAdapter(cA);

        songs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songname = songs.getItemAtPosition(position).toString();
                Intent player = new Intent(MainActivity.this, playerActivity.class);
                player.putExtra("songs", mySongs);
                player.putExtra("songname", songname);
                player.putExtra("pos",position);
                startActivity(player);
            }
        });
    }

    class customAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate((R.layout.list_item), null);
            TextView textSong = view.findViewById(R.id.topsongname);
            textSong.setSelected(true);
            textSong.setText(items[position]);
            return view;
        }
    }

}