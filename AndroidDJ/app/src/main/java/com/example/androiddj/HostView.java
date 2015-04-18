package com.example.androiddj;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androiddj.database.DatabaseHandler;
import com.example.androiddj.database.Songs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;



public class HostView extends Activity {
    private static String tag = "DJ Debugging";
    ListView list;
    private boolean downloading = false;
    private boolean getVotes = false;
    public static boolean micUsing = false;
    private ListViewAdapterHost adapter;
    int pos = -1;
    private ArrayList<String> song_names;
    boolean flag = true;
    private Handler playlistHandler;
    public String folder;
    private int plist_size = 0;
    private ArrayList<Songs> songs;
    private DatabaseHandler db;
    final private int updateTime = 5000;
    private FileObserver plistObserver;
    public TextView startTimeField, endTimeField;
    private MediaPlayer mediaPlayer;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler mediaHandler;
    private Handler downloadHandler;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private ImageButton playButton, pauseButton;
    public static int oneTimeOnly = 0;
    int index = 0;


    private Button youtubeButton;


//    AudioRecord recorder;
    private AudioTrack speaker;
//    private AudioRecord audiorecord;
    //Audio Configuration.
    private int sampleRate = 44100;      //How much will be ideal?
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int MinBufSize;
//    static AudioFormat format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(tag, "Going to call oncreate");
        super.onCreate(savedInstanceState);
        Log.i(tag, "calling setcontent view");
        setContentView(R.layout.activity_main);
        mediaHandler = new Handler();
        downloadHandler = new Handler();
        playlistHandler = new Handler();
//        customHandler.postDelayed(updateSongsList, 0);
        folder = Environment.getExternalStorageDirectory() + "/AndroidDJ-Playlist/";
        File dirs = new File(folder);

        if (!dirs.exists())
            dirs.mkdirs();

        Log.i(tag, "Going to create list_file");
        db = new DatabaseHandler(this);
        //addSongs(db);
        Log.i(tag, "Going to add song");
        db.deleteAllSongs();

        song_names = updatePlaylist();

        db.addAllSongs(song_names);

        Log.i(tag, "Going to create list: db size : " + String.valueOf(db.getSongsCount()));

        songs = db.getAllSongs();
        plist_size = songs.size();
        startTimeField = (TextView) findViewById(R.id.startTime);
        endTimeField = (TextView) findViewById(R.id.endTime);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        playButton = (ImageButton) findViewById(R.id.play);
        pauseButton = (ImageButton) findViewById(R.id.pause);
        seekbar.setClickable(true);
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);

        pauseButton.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.VISIBLE);

        Log.i(tag, "Songs retrieved from database");
        list = (ListView) findViewById(R.id.listview);
        adapter = new ListViewAdapterHost(songs, HostView.this, pos, db);
        list.setAdapter(adapter);
        Log.i(tag, "Adapter set");
        Log.i(tag, "Defining on click listener");

        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (pos == position) {
                    pos = -1;
                } else {
                    pos = position;
                }
                Log.i(tag, "Item at " + Integer.toString(position) + " is clicked");
                adapter.setPosition(pos);
                adapter.notifyDataSetChanged();
            }
        });

        Runnable downloadFile = new Runnable() {
            @Override
            public void run() {
                int port = 8988;

                try{
                    Log.d(tag,"create download serversocket");
                    ServerSocket fileServer = new ServerSocket();
                    fileServer.setReuseAddress(true);
                    fileServer.bind(new InetSocketAddress(port));
                    Log.d(tag,"Reuse download address "+fileServer.getReuseAddress());

                    while(true)
                    {
                        try {
                            Log.d(tag,"create download client");
                            Log.d(tag,"accept download client on "+fileServer.getReuseAddress());
                            Socket client = fileServer.accept();
                            Log.d(tag, "Starting Download from........."+client.getInetAddress().toString());
                            FileServerAsyncTask file = new FileServerAsyncTask(HostView.this);
                            Log.d(tag,"start download asyctask");
                            file.execute(new Socket[]{client});

                        }catch (IOException ex)
                        {
                            ex.printStackTrace();
                            break;
                        }
                    }
                }
                catch (IOException ex)
                {
                    Log.d(tag,ex.getMessage());
                }

//                Log.d(tag, "handler attached....");
            }
        };

        Thread download = new Thread(downloadFile);
        download.start();
		
		Runnable sendFile = new Runnable() {
            @Override
            public void run() {
                int port = 8986;

                try{
                    Log.d(tag,"create serversocket");
                    ServerSocket fileServer = new ServerSocket();
                    fileServer.setReuseAddress(true);
                    fileServer.bind(new InetSocketAddress(port));
                    Log.d(tag,"Reuse address "+fileServer.getReuseAddress());

                    while(true)
                    {
                        try {
                            Log.d(tag,"create client");
                            Log.d(tag,"accept client on "+fileServer.getReuseAddress());
                            Socket client = fileServer.accept();
                            Log.d(tag, "Starting Download from........."+client.getInetAddress().toString());
                            FileServerAsyncTask file = new FileServerAsyncTask(HostView.this);
                            Log.d(tag,"start asyctask");
                            file.execute(new Socket[]{client});

                        }catch (IOException ex)
                        {
                            ex.printStackTrace();
                            break;
                        }
                    }
                }
                catch (IOException ex)
                {
                    Log.d(tag,ex.getMessage());
                }

//                Log.d(tag, "handler attached....");
            }
        };

        Thread send = new Thread(sendFile);
        send.start();

        Runnable micRunnable = new Runnable() {
            @Override
            public void run() {
                int port = 8987;

                try{
                    Log.d(tag,"create mic serversocket");
                    ServerSocket micServer = new ServerSocket();
                    micServer.setReuseAddress(true);
                    micServer.bind(new InetSocketAddress(port));
                    Log.d(tag,"Reuse mic  address "+micServer.getReuseAddress());

                    while(true)
                    {
                        try {
                            Log.d(tag,"create mic client");
                            Log.d(tag,"accept mic client on "+micServer.getReuseAddress());
                            Socket client = micServer.accept();
                            Log.d(tag, "Starting mic streaming from........."+client.getInetAddress().toString());
                            MicAsyncTask micTask = new MicAsyncTask(HostView.this);
                            Log.d(tag,"start mic asyctask");
                            micTask.execute(new Socket[]{client});

                        }catch (IOException ex)
                        {
                            ex.printStackTrace();
                            break;
                        }
                    }
                }
                catch (IOException ex)
                {
                    Log.d(tag,ex.getMessage());
                }

//                Log.d(tag, "handler attached....");
            }
        };

        Thread micThread = new Thread(micRunnable);
        micThread.start();

        Runnable voteRunnable = new Runnable() {
            @Override
            public void run() {
                int port = 8111;

                try{
                    Log.d(tag,"create serversocket");
                    ServerSocket votesServer = new ServerSocket();
                    votesServer.setReuseAddress(true);
                    votesServer.bind(new InetSocketAddress(port));
                    Log.d(tag,"Reuse address "+votesServer.getReuseAddress());

                    while(true)
                    {
                        try {
                            Log.d(tag,"create client");
                            Log.d(tag,"accept client on "+votesServer.getReuseAddress());
                            Socket client = votesServer.accept();
                            Log.d(tag, "Starting Download from........."+client.getInetAddress().toString());
                            GetVotes voteTask = new GetVotes(HostView.this);
                            Log.d(tag,"start asyctask");
                            voteTask.execute(new Socket[]{client});

                        }catch (IOException ex)
                        {
                            ex.printStackTrace();
                            break;
                        }
                    }
                }
                catch (IOException ex)
                {
                    Log.d(tag,ex.getMessage());
                }

//                Log.d(tag, "handler attached....");
            }
        };

        Thread votesThread = new Thread(voteRunnable);
        votesThread.start();

        Runnable playlist = new Runnable() {
            @Override
            public void run() {
                sendPlaylist();
                Log.d(tag, "Sending Playlist....");
                playlistHandler.postDelayed(this, 3000);
            }
        };

        Thread playlistSend = new Thread(playlist);
        playlistSend.start();

        Log.i(tag, "Going to call create list view");
        Log.i(tag, "Finished create list view");


    }


    public ArrayList<String> updatePlaylist() {
        String tag = "Music_add";
        File dir = new File(folder);
        if (!dir.isDirectory())
            Log.i(tag, "Not a directory");

        File[] listOfFiles = dir.listFiles();

        if (listOfFiles != null)
            Log.i(tag, String.valueOf(listOfFiles.length));

        ArrayList<String> name = new ArrayList();
        Log.d(tag, "Adding songs");

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                name.add(listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                ;//System.out.println("Directory " + listOfFiles[i].getName());
            }
        }

        Log.d(tag, "Complete");

        return name;
    }


    public void play(View view) {
        if (songs.size() > 0) {
            if (flag) {
                File file = new File(folder + songs.get(0).getName());
                Uri uri = Uri.fromFile(file);
                mediaPlayer = MediaPlayer.create(this, uri);
            }

            Log.i(tag, "usee");
            mediaPlayer.start();

            int index = 1;
            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            if (oneTimeOnly == 0) {
                seekbar.setMax((int) finalTime);
                oneTimeOnly = 1;

                seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                        Toast.makeText(getApplicationContext(), "Changing seekbar progress", Toast.LENGTH_SHORT);
                        if (fromUser)
                            mediaPlayer.seekTo(progressValue);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }

            endTimeField.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) finalTime)))
            );
            startTimeField.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) startTime)))
            );

            seekbar.setProgress((int) startTime);
            mediaHandler.postDelayed(UpdateSongTime, 100);
            pauseButton.setEnabled(true);
            playButton.setEnabled(false);

            pauseButton.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.INVISIBLE);
        } else {
            Toast.makeText(this, "Playlist Empty", Toast.LENGTH_SHORT).show();
            flag = true;
        }
    }


    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if (mediaPlayer.isPlaying()) {
                startTime = mediaPlayer.getCurrentPosition();
                seekbar.setProgress((int) startTime);

                startTimeField.setText(String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                toMinutes((long) startTime)))
                );

                //Log.i("Media PLayer:::",startTimeField.getText().toString()+" "+endTimeField.getText().toString());

                if (startTimeField.getText().toString().equals(endTimeField.getText().toString())) {
//                songs=db.getAllSongs();
//                index=index+1;
                    db.deleteSong(songs.get(index).getID());
                    songs.remove(index);
                    sortList();
//                  adapter.setList(songs);
//                  adapter.notifyDataSetChanged();

                    Log.i("adapter","adapter size: "+adapter.getCount()+" Song size :"+songs.size());

                    if (songs.size() > 0) {
                        Log.i(tag, "Media PLayer list index - " + String.valueOf(index) + " songs size - " + String.valueOf(songs.size()));
//                        String loc = Environment.getExternalStorageDirectory() + "/AndroidDJ-Playlist/";
                        File file = new File(folder + songs.get(index).getName());
                        Uri uri = Uri.fromFile(file);

                        Log.i("Media PLayer:::", uri.toString());
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

                        Toast.makeText(getApplicationContext(), "Playing " + songs.get(index).getName() + "song",
                                Toast.LENGTH_SHORT).show();

                        Log.i(tag, "usee");
                        mediaPlayer.start();

                        finalTime = mediaPlayer.getDuration();
                        startTime = mediaPlayer.getCurrentPosition();
                        oneTimeOnly=0;

                        if (oneTimeOnly == 0) {
                            seekbar.setMax((int) finalTime);
                            oneTimeOnly = 1;
                        }

                        endTimeField.setText(String.format("%02d:%02d",
                                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                        toMinutes((long) finalTime)))
                        );
                        startTimeField.setText(String.format("%02d:%02d",
                                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                        toMinutes((long) startTime)))
                        );
                        seekbar.setProgress((int) startTime);
                    } else {
                        mediaPlayer.stop();
                        flag = true;
                    }
                }
            }

            if (mediaPlayer.isPlaying())
                mediaHandler.postDelayed(this, 100);
        }
    };

    public void pause(View view) {
//        Toast.makeText(getApplicationContext(), "Pausing sound",
//                Toast.LENGTH_SHORT).show();
        mediaPlayer.pause();
        pauseButton.setEnabled(false);
        playButton.setEnabled(true);
        flag = false;
        pauseButton.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.VISIBLE);
    }

    public void forward(View view) {
        int temp = (int) startTime;
        if ((temp + forwardTime) <= finalTime) {
            startTime = startTime + forwardTime;
            mediaPlayer.seekTo((int) startTime);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Cannot jump forward 5 seconds",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void rewind(View view) {
        int temp = (int) startTime;
        if ((temp - backwardTime) > 0) {
            startTime = startTime - backwardTime;
            mediaPlayer.seekTo((int) startTime);
        } else {
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

    public void sortList()
    {
        ArrayList<Songs> tempSongs = new ArrayList<Songs>();
        if(songs.size() != 0) {
            tempSongs.add(songs.get(0));
            tempSongs.addAll(db.getAllSongsSorted(songs.get(0).getID()));
            songs = tempSongs;
        }
        else
        {
            songs = db.getAllSongsSorted();
        }
        adapter.setList(songs);
//        adapter.notifyDataSetChanged();
    }

    private synchronized void addSong(String name) {
        Songs s = new Songs(++plist_size, name);
        db.addSong(s);
        songs.add(s);
        Log.d(tag, "Song added : " + s);
        sortList();
    }

    public static String append(String filename,String time)
    {
        String arr[]=filename.split("\\.",2);
        if (arr.length==2)
            return arr[0]+"_"+time+"."+arr[1];
        else
            return arr[0];
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.i(tag, "Activity is restarted");
//        songs = db.getAllSongs();
//        adapter.notifyDataSetChanged();
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.i(tag, "Activity is stopped");
//        songs.clear();
//        db.deleteAllSongs();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        songs.clear();
//        db.deleteAllSongs();
        Log.i(tag, "Activity is destroyed:" + String.valueOf(db.getSongsCount()) + " " + String.valueOf(songs.size()));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                moveTaskToBack(true);

                return true;
        }
        return false;
    }

    public JSONArray arrayToJSON()
    {
        JSONArray jsonArray = new JSONArray();
        for(Songs s:songs) {
            JSONObject jsonobject = new JSONObject();
            try {
                jsonobject.put("id",s.getID());
                jsonobject.put("name",s.getName());
                jsonobject.put("upvotes",s.getUpvotes());
                jsonobject.put("downvotes",s.getDownvotes());
                jsonArray.put(jsonobject);
            }
            catch(Exception e)
            {
                Log.i(tag,e.getMessage());
            }
        }
        return jsonArray;
    }

    public void sendPlaylist() {

        // json playlist to send
        Log.i("playlist",arrayToJSON().toString());
        String playlist = arrayToJSON().toString();
        ArrayList<String> address = getIp();
        for(String addr:address)
        {
            Intent serviceIntent = new Intent(this, PlaylistTransferService.class);
            Log.d(tag,addr);
            serviceIntent.setAction(PlaylistTransferService.ACTION_SEND_FILE);
            serviceIntent.putExtra(PlaylistTransferService.EXTRAS_PLAYLIST, playlist);
            serviceIntent.putExtra(PlaylistTransferService.EXTRAS_CLIENT_ADDRESS, addr);
            serviceIntent.putExtra(PlaylistTransferService.EXTRAS_CLIENT_PORT, 8122);
            startService(serviceIntent);
        }

    }

    public ArrayList<String> getIp() {
        BufferedReader br = null;
        ArrayList<String> clientIP=new ArrayList<String>();

        try {

            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            int i=1;
            while ((line = br.readLine()) != null) {
                Log.d(tag,line);
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    // Basic sanity check
                    String device = splitted[5];
                    if (device.matches(".*p2p-p2p0.*")){
                        String mac = splitted[3];
                        if (mac.matches("..:..:..:..:..:..")) {
                            String ip=splitted[0];
                            Log.d(tag,"Address "+String.valueOf(i)+" - "+ip);
                            i++;
                            clientIP.add(ip);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return clientIP;
    }

    private class MicAsyncTask extends AsyncTask<Socket, Void, String> {

        private Context context;
        private DatagramSocket dataServer;
        private int dataPort = 8989;

        /**
         * @param context
         */
        public MicAsyncTask(Context context) throws IOException {
            this.context = context;
            dataServer = new DatagramSocket(null);
            dataServer.setReuseAddress(true);
            dataServer.bind(new InetSocketAddress(dataPort));
        }

        @Override
        protected String doInBackground(Socket... params) {

            Socket client = params[0];

            try {

            InputStream inputstream = client.getInputStream();

            InputStreamReader isr = new InputStreamReader(inputstream);

            BufferedReader br = new BufferedReader(isr);
            String recieved_fname = br.readLine();

            Log.d("MicUsing","Recieved File name: "+recieved_fname);
            OutputStream out_stream = client.getOutputStream();
            PrintWriter pw = new PrintWriter(out_stream);
            Log.i("MicUsing1",micUsing?"yes":"no");

            if(!micUsing)
            {
                pw.println("yes");
                pw.flush();
//                        Log.d(WiFiDirectActivity.TAG, "microphone android dj");
                micUsing=true;
                Log.i("MicUsing2",micUsing?"yes":"no");
//                        Log.d(tag, "recieved file name" + " " + recieved_fname);
//                        Log.d("VS", "Recorder initialized");
                MinBufSize=1024*3;

                speaker = new AudioTrack(AudioManager.STREAM_VOICE_CALL,sampleRate,AudioFormat.CHANNEL_OUT_STEREO,audioFormat,MinBufSize,AudioTrack.MODE_STREAM);

                byte[] receiveData = new byte[3*1024];

                String data = "";

                //   int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

                // speaker = new AudioTrack(AudioManager.STREAM_MUSIC,sampleRate,AudioFormat.CHANNEL_OUT_STEREO,audioFormat,MinBufSize,AudioTrack.MODE_STREAM);
                speaker = new AudioTrack(AudioManager.STREAM_VOICE_CALL,sampleRate,AudioFormat.CHANNEL_OUT_STEREO,audioFormat,MinBufSize,AudioTrack.MODE_STREAM);

                speaker.setPlaybackRate(22100);
                speaker.play();

                String sentence = "";
                Log.i("MicUsing","Start mic receiving");

                while(!sentence.equals("end"))
                {
//                            Log.d("in while loop", "in while loop" + " " + recieved_fname);
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//                            Log.i("VR", "Socket Created");
                    dataServer.receive(receivePacket);
                    //  out.write(receivePacket.getData(), 0, receiveData.length);
//                            Log.i(tag,"receiving streaming");
                    sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    data = receivePacket.getData().toString();

//                            System.out.println("RECEIVED: " + data);
//
                    receiveData = receivePacket.getData();
                    speaker.write(receiveData, 0, receiveData.length);
                    // speaker.write(buffer, 0, MinBufSize);
//                            Log.d("VR", "Writing buffer content to speaker");
                    ///AudioTrack(AudioManager.STREAM_MUSIC,sampleRate,channelConfig,audioFormat,minBufSize,AudioTrack.MODE_STREAM);

                }

//                        Log.d("Streaming","dataserver closing");
                dataServer.close();
//                        Log.d("Streaming","dataserver closed");
                client.close();
//                        Log.d("Streaming","client closed");
                Log.i("MicUsing3",micUsing?"yes":"no");
                //   play(findViewById(R.id.play));
            }
            else
            {
                Log.i("MicUsing4",micUsing?"yes":"no");
                pw.println("no"); // if mic already used
                pw.flush();
                client.close();
            }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Complete";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("Streaming Complete", result);
            if (result != null) {
                micUsing=false;
                Log.d("MicUsing",result+" : " +(micUsing?"yes":"no"));
            }

        }

    }

    private class FileServerAsyncTask extends AsyncTask<Socket, Void, String> {

        private Context context;

        /**
         * @param context
         */
        public FileServerAsyncTask(Context context) throws IOException {
            this.context = context;
//            serverSocket = new ServerSocket(serverPort);
        }

        @Override
        protected String doInBackground(Socket... params) {
            try {
                Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
                downloading = true;
                Log.d(tag, "Downloading started: " + downloading);

                Socket client = params[0];

                String recieved_fname = "";
                Log.d(tag, "Server: connection done "+client.getInetAddress().toString());

                InputStream inputstream = client.getInputStream();
                InputStreamReader isr = new InputStreamReader(inputstream);

                BufferedReader br = new BufferedReader(isr);
                recieved_fname = br.readLine();
                Log.d("fileName", "receiving file "+recieved_fname);


               if(recieved_fname.startsWith("SEND_SONG")){
                   String id= br.readLine();
                   String songName=db.getSong(Integer.parseInt(id)).getName();
                    Log.d(WiFiDirectActivity.TAG, "id - "+id+" recieved file name" + " " + songName);
                    Uri fileUri=Uri.fromFile(new File(folder+"/"+songName));
                    ContentResolver cr = context.getContentResolver();
                    InputStream is = null;
                    try {
                        is = cr.openInputStream(Uri.parse(String.valueOf(fileUri)));
                        Log.d("Fileuri", "uri - "+Uri.parse(String.valueOf(fileUri)));
                    } catch (FileNotFoundException e) {
                        Log.d(WiFiDirectActivity.TAG, e.toString());
                    }
                    copyFile(is, client.getOutputStream());
                    client.close();
                    return null;

                }

				else {
                Log.d(WiFiDirectActivity.TAG, "recieved file name" + " " + recieved_fname);
                String filename = append(recieved_fname,String.valueOf(System.currentTimeMillis()));
                final File f = new File(folder + filename);
                f.createNewFile();
                Log.d(WiFiDirectActivity.TAG, "recieved file written " + f.getAbsolutePath());
                Log.d(WiFiDirectActivity.TAG, "server: copying files " + f.toString());
                copyFile(inputstream, new FileOutputStream(f));
                client.close();
//                    dataServer.close();
                Log.d(tag,"client socket closed");
                return filename;

                }


            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
                downloading = false;
                Log.d(tag, "Downloading Error: " + downloading);
                return null;
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
//            setUsing(false);
            if (result != null) {
                addSong(result);
//                downloading = false;
                Log.d(tag, "Downloading Completed");
            }

        }

    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(WiFiDirectActivity.TAG, "In copy: " + e.toString());
            return false;
        }
        return true;
    }

    public String getUpvotes(Socket client) {
        try {

            InputStream inputstream = client.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputstream);
            BufferedReader br = new BufferedReader(isr);
            Log.d(tag, "recieving upvotes");
            String votesJSON = br.readLine();
            Log.d(tag, "recieved upvotes " + votesJSON);
            client.close();
            //votesServerSocket.close();
            return votesJSON;
        } catch (IOException e) {
            Log.e(tag, e.getMessage());
            getVotes = false;
            Log.d(tag, "Downloading Error: " + getVotes);
            return null;
        }
    }



    private class GetVotes extends AsyncTask<Socket, Void, String> {

        private Context context;

        /**
         * @param context
         */
        public GetVotes(Context context) throws IOException {
            this.context = context;
        }

        @Override
        protected String doInBackground(Socket... params) {

            String votesJSON = getUpvotes(params[0]);
            return votesJSON;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.i("votes_received", result);
            if (result != null) {
                getVotes = false;
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getString("type").equals("upvote")) {
                        int id = jsonObject.getInt("id");
                        Songs song = db.getSong(id);
                        song.setUpvotes(song.getUpvotes() + 1);

                        db.updateSongUp(id, song.getUpvotes()) ;
                        sortList();
                        adapter.notifyDataSetChanged();
                    } else {
                        int id = jsonObject.getInt("id");
                        Songs song = db.getSong(id);
                        song.setDownvotes(song.getDownvotes() + 1);
                        db.updateSongDown(id, song.getDownvotes()) ;
                        sortList();
                        adapter.notifyDataSetChanged();
                    }

                    Log.d(tag, "Upvote updating Completed");
                } catch (Exception e) {
                    Log.d("Voting update error", "Entered catch exception");
                    e.printStackTrace();
                }

            }

        }

    }


}
