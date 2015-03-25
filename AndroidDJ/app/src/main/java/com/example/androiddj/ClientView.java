package com.example.androiddj;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
<<<<<<< HEAD
import android.view.KeyEvent;
=======
>>>>>>> d659045139ab3ef91b3c77c0ffaeb89c9db7bc51
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.androiddj.database.DatabaseHandler;
import com.example.androiddj.database.Songs;

import java.util.ArrayList;

public class ClientView extends Activity {
	private String tag = "DJ Debugging";
	ListView list;
	ListViewAdapterClient adapter;
	final int pos = -1;
    private ArrayList<Songs> songs;
    private View layout = null;
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(tag,"Going to call oncreate");
		super.onCreate(savedInstanceState);
		Log.i(tag,"calling setcontent view");
		setContentView(R.layout.activity_main);

		Log.i(tag,"Going to create list_file");
        findViewById(R.id.host_layout).setVisibility(View.INVISIBLE);
		findViewById(R.id.client_layout).setVisibility(View.VISIBLE);
		/*
		 * Here json string has to be used to get the list of the songs
		 */
		
		songs = addSongs();
		Log.i(tag,"Going to create list");
        list = (ListView)findViewById(R.id.listview);
        adapter = new ListViewAdapterClient(songs,ClientView.this,pos,new DatabaseHandler(this));
        list.setAdapter(adapter);
        Log.i(tag,"Adapter set");
        Log.i(tag,"Defining on click listener");

        list.setOnItemClickListener(new OnItemClickListener() {
        	@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
        		Log.i(tag,"Item at " + Integer.toString(position) + " is clicked");
				adapter.setPosition(position);
				position = pos;
				adapter.notifyDataSetChanged();
			}
		});

<<<<<<< HEAD
        findViewById(R.id.add_file).setEnabled(true);
=======
>>>>>>> d659045139ab3ef91b3c77c0ffaeb89c9db7bc51
        findViewById(R.id.add_file).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // Allow user to pick an audio from File-Manager or other
                        // registered apps
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("audio/*");
                        Log.d(WiFiDirectActivity.TAG, "Start sending file");
                        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
                    }
                });

		Log.i(tag,"Going to call create list view");
		Log.i(tag,"Finished create list view");
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            Log.d(WiFiDirectActivity.TAG, "Error in sending");
            Toast.makeText(this, "Error in sending", Toast.LENGTH_SHORT);
            return;
        }

        if(requestCode == CHOOSE_FILE_RESULT_CODE)
        {
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();

            Log.d(WiFiDirectActivity.TAG, "filepath" + " " + cursor.getString(nameIndex));

            Log.d(WiFiDirectActivity.TAG, "Intent----------- " + uri);
            Intent serviceIntent = new Intent(this, FileTransferService.class);
            serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
            serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
            serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_NAME,cursor.getString(nameIndex));
            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                    DeviceDetailFragment.info.groupOwnerAddress.getHostAddress());
            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
<<<<<<< HEAD
            Toast.makeText(this,cursor.getString(nameIndex)+" send to Host",Toast.LENGTH_SHORT).show();
=======
>>>>>>> d659045139ab3ef91b3c77c0ffaeb89c9db7bc51
            startService(serviceIntent);
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
<<<<<<< HEAD
        else if(id==R.id.leave)
        {
            finish();
        }
=======
>>>>>>> d659045139ab3ef91b3c77c0ffaeb89c9db7bc51
		return super.onOptionsItemSelected(item);
	}
	
	private ArrayList<Songs> addSongs()
	{
		ArrayList<Songs> songs = new ArrayList<Songs>();
		for(int i=0;i<10;i++)
		{
<<<<<<< HEAD
			songs.add(new Songs(i+1,"Song " + Integer.toString(i + 1)));
=======
			songs.add(new Songs(i+1,"Song fbjnbsdhjsnbdhfbsdgfhbsdhfbsahfbahbfabnahnbawajnbf af hf afjsndfjkj" + Integer.toString(i + 1)));
>>>>>>> d659045139ab3ef91b3c77c0ffaeb89c9db7bc51
    }
		
		return songs;
	}
	
	
	@Override
    public void onRestart() {
        super.onRestart();
        Log.i(tag, "Activity is restarted");
//        songs = addSongs();
//        adapter.notifyDataSetChanged();
    }
	
	
	@Override
    public void onStop() {
        super.onStop();
        Log.i(tag, "Activity is stopped");
//        songs.clear();
    }
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(tag, "Activity is destroyed");
        songs.clear();
    }

<<<<<<< HEAD
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:

                moveTaskToBack(true);

                return true;
        }
        return false;
    }
	
=======
public String getPlaylist(){
        int port = 0;
        ServerSocket serverSocket=null;
        try {
             serverSocket=new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(tag, "Socket not opened on port"+Integer.toString(port));
        }
        try {
            Log.d(WiFiDirectActivity.TAG, "Server-Socket opened for playlist transfer");

            Socket client = serverSocket.accept();
            Log.d(WiFiDirectActivity.TAG, "connection done");

            InputStream inputstream = client.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputstream);
            BufferedReader br = new BufferedReader(isr);
            Log.d(WiFiDirectActivity.TAG, "recieving playlist");
            String playlist = br.readLine();
            Log.d(WiFiDirectActivity.TAG, "recieved Playlist");
            serverSocket.close();

            return playlist;
        } catch (IOException e) {
            Log.e(WiFiDirectActivity.TAG, e.getMessage());
            boolean downloading = false;
            Log.d(tag, "Downloading Error: " + downloading);
            return null;
        }
    }
    public void updateList(){
        String db=getPlaylist();
        //UpdateListView
    }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        switch(keyCode)
//        {
//            case KeyEvent.KEYCODE_BACK:
//
//                moveTaskToBack(true);
//
//                return true;
//        }
//        return false;
//    }
>>>>>>> d659045139ab3ef91b3c77c0ffaeb89c9db7bc51
	
}
