package com.example.androiddj;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class StartView extends Activity
{
	private String tag = "DJ Debugging";
	private Button hostButton;
	private Button joinButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(tag,"Going to call oncreate");
		super.onCreate(savedInstanceState);
		Log.i(tag,"calling setcontent view");
		setContentView(R.layout.start_view);
		Log.i(tag,"View created");
		hostButton = (Button) findViewById(R.id.host_button);
		joinButton = (Button) findViewById(R.id.join_button);
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
	
	public void onHostClicked(View button)
	{
		Log.i(tag,"Host is clicked");
		Intent i = new Intent(this,MainActivity.class);
		startActivity(i);
	}
	
	public void onJoinClicked(View button)
	{
		Log.i(tag,"Join is clicked");
		Intent i = new Intent(this,MainActivity.class);
		startActivity(i);
	}
}
