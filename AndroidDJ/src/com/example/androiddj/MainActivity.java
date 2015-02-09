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
    private List<String> List_file;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(tag,"Going to call oncreate");
		super.onCreate(savedInstanceState);
		Log.i(tag,"calling setcontent view");
		setContentView(R.layout.activity_main);
		Log.i(tag,"Going to create list_file");
		List_file = new ArrayList<String>();
		Log.i(tag,"Going to create list");
        list = (ListView)findViewById(R.id.listview);
		Log.i(tag,"Going to call create list view");
        CreateListView();
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
	
	/*
	 * This function will create the list view, put this function in files whereever you require
	 * any scroll list
	*/
	private void CreateListView()
    {
		for(int i=0;i<10;i++)
		{
			List_file.add("Song"+Integer.toString(i));
		}
		final int indexChanged[] = new int[1];
		final String initialValue[] = new String[1];
		indexChanged[0] = -1;
		initialValue[0] = "";
         list.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,List_file));
         list.setOnItemClickListener(new OnItemClickListener()
           {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, final int position,long id)
                {
                    //args2 is the listViews Selected index
                	if(indexChanged[0] != -1)
                	{
                		List_file.set(indexChanged[0], initialValue[0]);
                	}
                	indexChanged[0] = position;
                	initialValue[0] = List_file.get(position);
                	//List_file.set(position, List_file.get(position) + " is selected");
                	view.animate().setDuration(200).alpha(0).withEndAction(new Runnable() {
                      @Override
                      public void run() {
                        List_file.set(position, List_file.get(position) + " is selected");
                        ArrayAdapter adapter = (ArrayAdapter) list.getAdapter();
                        adapter.notifyDataSetChanged();
                        view.setAlpha(1);
                      }
                    });
                }
           });
    }
}
