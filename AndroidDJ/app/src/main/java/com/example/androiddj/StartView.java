package com.example.androiddj;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class StartView extends Activity
{
	private String tag = "DJ Debugging";
    public String LEAVE_PARTY = "leave";
    private String hostName="";
    private String CALL_TYPE="call";
    private String HOST_NAME="host";
    private String HOST_PASS="";
    private String host_pwd="";
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

        findViewById(R.id.closed_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(StartView.this,WiFiDirectActivity.class);
                intent.putExtra(CALL_TYPE,"Host");
                getNamePwd("Enter Party Name & Password");
            }
        });
	}

    public void getName(String title)
    {
        final SharedPreferences sharedpreferences = getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE);
        if(sharedpreferences.contains("username")){
            hostName=sharedpreferences.getString("username","");
            host_pwd="open";
            intent.putExtra(HOST_NAME, hostName);
            intent.putExtra(HOST_PASS, host_pwd);
            startActivity(intent);
        }
        else{
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
                    host_pwd="open";
                    if(!hostName.equals("")) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("username",hostName);
                        editor.commit();
                        intent.putExtra(HOST_NAME, hostName);
                        intent.putExtra(HOST_PASS, host_pwd);
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
    }

    public void getNamePwd(String title)
    {
        final SharedPreferences sharedpreferences = getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE);
        final AlertDialog.Builder builder = new AlertDialog.Builder(StartView.this);
        builder.setTitle(title);
        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.builder_layout,null);
//        final EditText input = new EditText(StartView.this);
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
        final EditText name = (EditText) layout.findViewById(R.id.name);
        final EditText pass = (EditText) layout.findViewById(R.id.pass);

        if(sharedpreferences.contains("username"))
            name.setText(sharedpreferences.getString("username",""));

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            int set=0;
            @Override
            public void onClick(DialogInterface dialog, int which) {

                hostName = name.getText().toString();
                host_pwd = pass.getText().toString();

                if(!hostName.equals("") && !host_pwd.equals("")) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("username",hostName);
                    editor.commit();
                    intent.putExtra(HOST_NAME,hostName);
                    intent.putExtra(HOST_PASS,host_pwd);
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
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			return true;
		}
        if (id == R.id.action_set_username) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(StartView.this);
            builder.setTitle("Set UserName");
            final SharedPreferences sharedpreferences = getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE);
            final EditText input = new EditText(StartView.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setText(sharedpreferences.getString("username", ""));
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                int set = 0;

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = input.getText().toString();
                    if (!name.equals("")) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("username", name);
                        editor.commit();
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
		return super.onOptionsItemSelected(item);
	}
}
