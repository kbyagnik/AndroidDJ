package com.example.androiddj;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.example.androiddj.database.DatabaseHandler;
import com.example.androiddj.database.Songs;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class HostView extends Activity {
	private String tag = "DJ Debugging";
	ListView list;
	ListViewAdapterHost adapter;
	int pos = -1;
    private ArrayList<Songs> songs;
    DatabaseHandler db;

    public TextView startTimeField,endTimeField;
    private MediaPlayer mediaPlayer;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();;
    private int forwardTime = 30000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private ImageButton playButton,pauseButton;
    public static int oneTimeOnly = 0;
    int index=0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {


		Log.i(tag,"Going to call oncreate");
		super.onCreate(savedInstanceState);
		Log.i(tag,"calling setcontent view");
		setContentView(R.layout.activity_main);
		Log.i(tag,"Going to create list_file");
		db = new DatabaseHandler(this);
		//addSongs(db);
		Log.i(tag, "Going to add song");
        ArrayList<String> songnames=init_phone_music_grid();
        db.addAllSongs(songnames);
		Log.i(tag,"Going to create list");
		songs = db.getAllSongs();
        startTimeField =(TextView)findViewById(R.id.textView1);
        endTimeField =(TextView)findViewById(R.id.textView2);
        seekbar = (SeekBar)findViewById(R.id.seekBar1);
        playButton = (ImageButton)findViewById(R.id.imageButton1);
        pauseButton = (ImageButton)findViewById(R.id.imageButton2);
        seekbar.setClickable(true);
        pauseButton.setEnabled(false);

//        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                if(mediaPlayer!=null && b)
//                    mediaPlayer.seekTo(i*1000);
//            }
////
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

        String loc= Environment.getExternalStorageDirectory()+"/AndroidDJ-Playlist/";
        File file = new File(loc+songs.get(0).getName());
        Uri uri= Uri.fromFile(file);
        mediaPlayer = MediaPlayer.create(this,uri);
//        seekbar.setMax(mediaPlayer.getDuration());
		Log.i(tag, "Song name : " + songs.get(0).getName());
		Log.i(tag,"Songs retrieved from database");
        list = (ListView)findViewById(R.id.listview);
        adapter = new ListViewAdapterHost(songs,HostView.this,pos,db);
        list.setAdapter(adapter);
        Log.i(tag,"Adapter set");
        Log.i(tag,"Defining on click listener");
        list.setOnItemClickListener(new OnItemClickListener() {
        	@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
        		if(pos == position)
        		{
        			pos = -1;
        		}
        		else
        		{
        			pos = position;
        		}
        		Log.i(tag,"Item at " + Integer.toString(position) + " is clicked");
				adapter.setPosition(pos);
				adapter.notifyDataSetChanged();
			}
		});
		Log.i(tag,"Going to call create list view");
		Log.i(tag,"Finished create list view");


	}


    public ArrayList<String> init_phone_music_grid()
    {
        String tag="Music_add";
        File song = new File(Environment.getExternalStorageDirectory()+"/AndroidDJ-Playlist/") ;
        if (!song.isDirectory())
            Log.i(tag,"Not a directory") ;
        //Log.i(tag,Environment.getExternalStorageDirectory()+"/AndroidDJ-Playlist/check this");

        File[] listOfFiles = song.listFiles();
        if (listOfFiles !=null)
            Log.i(tag,String.valueOf(listOfFiles.length)) ;
        ArrayList<String> name = new ArrayList();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                name.add(listOfFiles[i].getName()) ;
            } else if (listOfFiles[i].isDirectory()) {
                //System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
        return name;
    }



    public void play(View view){
        Toast.makeText(getApplicationContext(), "Playing sound",
                Toast.LENGTH_SHORT).show();
        String tag="";
        Log.i(tag,"usee");
        mediaPlayer.start();

        int index=1;
        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();
        if(oneTimeOnly == 0){
            seekbar.setMax((int) finalTime);
            oneTimeOnly = 1;
        }

        endTimeField.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) finalTime)))
        );
        startTimeField.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) startTime)))
        );
        seekbar.setProgress((int)startTime);
        myHandler.postDelayed(UpdateSongTime,100);
        pauseButton.setEnabled(true);
        playButton.setEnabled(false);
    }


    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            startTimeField.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) startTime)))
            );

            seekbar.setProgress((int)startTime);
            //Log.i("Media PLayer:::",startTimeField.getText().toString()+" "+endTimeField.getText().toString());
            if (startTimeField.getText().toString().equals(endTimeField.getText().toString()))
            {
                index=index+1;
                Log.i("Media PLayer:::",String.valueOf(index));

                songs=db.getAllSongs();
                String loc=Environment.getExternalStorageDirectory()+"/AndroidDJ-Playlist/";
                File file = new File(loc+songs.get(index).getName());
                Uri uri= Uri.fromFile(file);

                Log.i("Media PLayer:::",uri.toString());
                //MediaPlayer mediaPlayer = new MediaPlayer();
                //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

                Toast.makeText(getApplicationContext(), "Playing "+songs.get(index).getName()+"song",
                        Toast.LENGTH_SHORT).show();
                String tag="";
                Log.i(tag,"usee");
                mediaPlayer.start();


                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();
                if(oneTimeOnly == 0){
                    seekbar.setMax((int) finalTime);
                    oneTimeOnly = 1;
                }

                endTimeField.setText(String.format("%d min, %d sec",
                                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                toMinutes((long) finalTime)))
                );
                startTimeField.setText(String.format("%d min, %d sec",
                                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                toMinutes((long) startTime)))
                );
                seekbar.setProgress((int)startTime);

            }
            myHandler.postDelayed(this, 100);

        }
    };

    public void pause(View view){
        Toast.makeText(getApplicationContext(), "Pausing sound",
                Toast.LENGTH_SHORT).show();

        mediaPlayer.pause();
        pauseButton.setEnabled(false);
        playButton.setEnabled(true);
    }

    public void forward(View view){
        int temp = (int)startTime;
        if((temp+forwardTime)<=finalTime){
            startTime = startTime + forwardTime;
            mediaPlayer.seekTo((int) startTime);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Cannot jump forward 5 seconds",
                    Toast.LENGTH_SHORT).show();
        }

    }
    public void rewind(View view){
        int temp = (int)startTime;
        if((temp-backwardTime)>0){
            startTime = startTime - backwardTime;
            mediaPlayer.seekTo((int) startTime);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Cannot jump backward 5 seconds",
                    Toast.LENGTH_SHORT).show();
        }

    }



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void addSongs()
	{
		for(int i=0;i<10;i++)
		{
			Log.i(tag, "adding songs in database");
			db.addSong(new Songs(i + 1,"Song " + Integer.toString(i + 1)));
		}
		
		/*ArrayList<String> songs = new ArrayList<String>();
		for(int i=0;i<10;i++)
		{
			songs.add("Song "+Integer.toString(i+1));
		}
		return songs;*/
	}
	
	@Override
    public void onRestart() {
        super.onRestart();
        Log.i(tag, "Activity is restarted");
        songs = db.getAllSongs();
        adapter.notifyDataSetChanged();
    }
	
	
	@Override
    public void onStop() {
        super.onStop();
        Log.i(tag, "Activity is stopped");
        int size = songs.size();
        for(int i=0;i<size;i++)
        {
        	db.deleteSong(songs.get(i).getID());
        }
    }
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(tag, "Activity is destroyed");
        int size = songs.size();
        for(int i=0;i<size;i++)
        {
        	db.deleteSong(songs.get(i).getID());
        }
    }
	
	
}
