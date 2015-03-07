package com.example.jagadeesh.tointeg;

        import android.media.MediaPlayer;
        import android.os.Environment;
        import android.os.Bundle;
        import android.view.Menu;

        import java.io.File;
        import java.util.ArrayList;
        import java.util.concurrent.TimeUnit;

        import android.os.Handler;
        import android.app.Activity;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.ImageButton;
        import android.widget.SeekBar;
        import android.widget.TextView;
        import android.widget.Toast;
        import android.content.Context;
        import android.database.Cursor;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.BaseAdapter;
        import android.widget.ListView;
        import android.widget.AdapterView.OnItemClickListener;
        import android.net.Uri;
        import android.content.ContentResolver;
        import android.util.Log;

public class MainActivity extends Activity {

    ListView musiclist;
    Cursor musiccursor;
    String au[];
    public TextView startTimeField,endTimeField;
    private MediaPlayer mediaPlayer;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private ImageButton playButton,pauseButton;
    public static int oneTimeOnly = 0;
    int index=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //songName = (TextView)findViewById(R.id.textView4);
        startTimeField =(TextView)findViewById(R.id.textView1);
        endTimeField =(TextView)findViewById(R.id.textView2);
        seekbar = (SeekBar)findViewById(R.id.seekBar1);
        playButton = (ImageButton)findViewById(R.id.imageButton1);
        pauseButton = (ImageButton)findViewById(R.id.imageButton2);
        //songName.setText("song.mp3");
        ArrayList<String> songnames;
        songnames=init_phone_music_grid();
        String loc=Environment.getExternalStorageDirectory()+"/AndroidDJ-Playlist/";
        File file = new File(loc+songnames.get(0));
        Uri uri= Uri.fromFile(file);
        //MediaPlayer mediaPlayer = new MediaPlayer();
        //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer = MediaPlayer.create(this,uri);
        Log.i("","canusehere");
        seekbar.setClickable(true);
        pauseButton.setEnabled(false);
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
                 System.out.println("Directory " + listOfFiles[i].getName());
    }
}

        musiclist = (ListView) findViewById(R.id.PhoneMusicList);
        musiclist.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,name));
        return name;
    }
   /* private void init_phone_music_grid() {
        //      System.gc();

        String tag="Music_add";

        try{
            ContentResolver contentResolver = getContentResolver();

            File song = new File(Environment.getExternalStorageDirectory()+"/AndroidDJ-Playlist/") ;
            if (!song.isDirectory())
                Log.i(tag,"Not a directory") ;

            Uri uri = Uri.fromFile(song) ;

//            Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ;
            Log.i(tag,"URI is : "+uri.toString()) ;
            musiccursor = contentResolver.query(uri, null, null, null,null);
            Log.i(tag,"2");

//            int cnt = musiccursor.getCount();
            Log.i(tag,"No of files: "+String.valueOf(10));
            au=new String[10];
            Log.i(tag,"3");
            if (musiccursor == null) {
                // query failed, handle error.
                Log.i(tag,"here");
            } else if (!musiccursor.moveToFirst()) {
                // no media on the device
                Toast.makeText(this, "No sdcard presents toast", Toast.LENGTH_SHORT).show();
                Log.i(tag,"4");
            } else {
                int titleColumn = musiccursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DISPLAY_NAME);
                Log.i(tag,"5");
                ArrayList<String> name = new ArrayList();
                //int idColumn = musiccursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
                for(int i=0;i<musiccursor.getCount();i++)
                {
                    String thisTitle = musiccursor.getString(titleColumn);
                    System.out.println("raghu  "+thisTitle);
                    name.add(thisTitle) ;
                    musiccursor.moveToNext();
                }
                Log.i(tag,"6");
                musiclist = (ListView) findViewById(R.id.PhoneMusicList);
                musiclist.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,name));
                Log.i(tag,"7");
                musiclist.setOnItemClickListener(musicgridlistener);

            }
        }
        catch(Exception e){

            System.out.println("Error exception");
        }

    }*/

    private OnItemClickListener musicgridlistener = new OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position,long id) {
            System.gc();
            Toast.makeText(MainActivity.this, "Clicked item is: "+au[position], Toast.LENGTH_SHORT).show();
        }
    };

    public class MusicAdapter extends BaseAdapter {
        private Context mContext;
        public MusicAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return au.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            System.gc();
            TextView tv = new TextView(mContext.getApplicationContext());
            if (convertView == null) {
                tv.setText(au[position]);
            } else
                tv = (TextView) convertView;
            return tv;
        }
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
            Log.i("Media PLayer:::",startTimeField.getText().toString()+" "+endTimeField.getText().toString());
            if (startTimeField.getText().toString().equals(endTimeField.getText().toString()))
            {
                index=index+1;
                Log.i("Media PLayer:::",String.valueOf(index));
                ArrayList<String> songnames;
                songnames=init_phone_music_grid();
                String loc=Environment.getExternalStorageDirectory()+"/AndroidDJ-Playlist/";
                File file = new File(loc+songnames.get(index));
                Uri uri= Uri.fromFile(file);
                Log.i("Media PLayer:::",uri.toString());
                //MediaPlayer mediaPlayer = new MediaPlayer();
                //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);

                Toast.makeText(getApplicationContext(), "Playing"+uri.toString() +"song",
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
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

}