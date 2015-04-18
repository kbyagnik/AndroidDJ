package com.example.androiddj;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StartView extends Activity
{
	private String tag = "DJ Debugging";
	private Button hostButton;
	private Button joinButton;
    private String hostName="";
    private String CALL_TYPE="call";
    private String HOST_NAME="host";
    private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(tag,"Going to call oncreate");
		super.onCreate(savedInstanceState);
		Log.i(tag,"calling setcontent view");
		setContentView(R.layout.start_view);
		Log.i(tag,"View created");

        findViewById(R.id.join_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent=new Intent(StartView.this,WiFiDirectActivity.class);
                intent.putExtra(CALL_TYPE,"Client");
                getName("Enter Device Name");
            }
        });

        findViewById(R.id.open_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(StartView.this,WiFiDirectActivity.class);
                intent.putExtra(CALL_TYPE,"Host");
                getName("Enter Party Name");
            }
        });
	}

    public void getName(String title)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(StartView.this);
        builder.setTitle(title);

        final EditText input = new EditText(StartView.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            int set=0;
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hostName = input.getText().toString();
                if(!hostName.equals("")) {
                    intent.putExtra(HOST_NAME, hostName);
                    startActivity(intent);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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

//	public void onOpenClicked(View button)
//	{
//		Log.i(tag,"Host is clicked");
//		Intent hostIntent = new Intent(this,HostView.class);
//		startActivity(hostIntent);
//	}
//
//	public void onJoinClicked(View button)
//	{
//		Log.i(tag,"Join is clicked");
//		Intent clientIntent = new Intent(this,ClientView.class);
//        clientIntent.putExtra(CALL_TYPE,"Client");
//		startActivity(clientIntent);
//	}
}
