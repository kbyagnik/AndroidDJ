package com.example.androiddj;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	private String tag = "DJ Debugging";
	ListView list;
	ListViewAdapter adapter;
	final int pos = -1;
    private ArrayList<String> songs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(tag,"Going to call oncreate");
		super.onCreate(savedInstanceState);
		Log.i(tag,"calling setcontent view");
		setContentView(R.layout.activity_main);
		Log.i(tag,"Going to create list_file");
		songs = addSongs();
		Log.i(tag,"Going to create list");
        list = (ListView)findViewById(R.id.listview);
        adapter = new ListViewAdapter(songs,MainActivity.this,pos);
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
	
	private ArrayList<String> addSongs()
	{
		ArrayList<String> songs = new ArrayList<String>();
		for(int i=0;i<10;i++)
		{
			songs.add("Song "+Integer.toString(i+1));
		}
		return songs;
	}
	
	/*
	 * This function will create the list view, put this function in files whereever you require
	 * any scroll list
	*/
	/*
	 package com.example.androiddj;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListViewAdapter
{
	final List<String> songs;
	final ListView list;
	final Context context;
	
	public ListViewAdapter(ArrayList<String> songs,ListView list,Context context)
	{		
        this.songs = songs;
        this.list = list;
        this.context = context;
		CreateListView();
	}
	
	public void CreateListView()
    {
		for(int i=0;i<10;i++)
		{
			((List<String>) ((View) songs).getParent()).add("Song"+Integer.toString(i));
		}
		final int indexChanged[] = new int[1];
		final String initialValue[] = new String[1];
		indexChanged[0] = -1;
		initialValue[0] = "";
         list.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,songs));
         list.setOnItemClickListener(new OnItemClickListener()
           {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, final int position,long id)
                {
                    //args2 is the listViews Selected index
                	if(indexChanged[0] != -1)
                	{
                		songs.set(indexChanged[0], initialValue[0]);
                	}
                	indexChanged[0] = position;
                	initialValue[0] = songs.get(position);
                	//songs.set(position, songs.get(position) + " is selected");
                	view.animate().setDuration(200).alpha(0).withEndAction(new Runnable() {
                      @Override
                      public void run() {
                        songs.set(position, songs.get(position) + " is selected");
                        ArrayAdapter adapter = (ArrayAdapter) list.getAdapter();
                        adapter.notifyDataSetChanged();
                        view.setAlpha(1);
                      }
                    });
                }
           });
    }
	
}

	 */
	
	
}
