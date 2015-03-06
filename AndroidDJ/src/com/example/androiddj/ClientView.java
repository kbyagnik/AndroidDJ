package com.example.androiddj;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;

import com.example.androiddj.database.DatabaseHandler;
import com.example.androiddj.database.Songs;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ClientView extends Activity {
	private String tag = "DJ Debugging";
	ListView list;
	ListViewAdapterClient adapter;
	final int pos = -1;
    private ArrayList<Songs> songs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(tag,"Going to call oncreate");
		super.onCreate(savedInstanceState);
		Log.i(tag,"calling setcontent view");
		setContentView(R.layout.activity_main);
		Log.i(tag,"Going to create list_file");
		
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
		Log.i(tag,"Going to call create list view");
		Log.i(tag,"Finished create list view");
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
	
	private ArrayList<Songs> addSongs()
	{
		ArrayList<Songs> songs = new ArrayList<Songs>();
		for(int i=0;i<10;i++)
		{
			songs.add(new Songs(i+1,"Song " + Integer.toString(i + 1)));
		}
		
		return songs;
	}
	
	
	@Override
    public void onRestart() {
        super.onRestart();
        songs.clear();
        adapter.notifyDataSetChanged();
    }
	
	
	@Override
    public void onStop() {
        super.onStop();
        Log.i(tag, "Activity is stopped");
        songs.clear();
    }
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(tag, "Activity is destroyed");
        songs.clear();
    }
	
}
