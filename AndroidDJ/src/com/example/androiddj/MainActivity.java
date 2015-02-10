package com.example.androiddj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity
{
    private String tag = "DJ Debugging";
    private Button hostButton;
    private Button joinButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(tag,"Going to call oncreate");
        super.onCreate(savedInstanceState);
        Log.i(tag,"calling setcontent view");
        setContentView(R.layout.activity_main);
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
        Intent hostActivity = new Intent(this,StartView.class);
        startActivity(hostActivity);
    }

    public void onJoinClicked(View button)
    {
        Log.i(tag,"Join is clicked");
        Intent joinActivity = new Intent(this,StartView.class);
        startActivity(joinActivity);
    }
}