package com.example.androiddj.youtubeparser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.androiddj.R;

/**
 *
 * Created by Ashish Singh on 05-Apr-15.
 */
public class ListViewHome extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.codelearn_list_home);
		Button baseAdapter = (Button)findViewById(R.id.button2);
        final EditText query = (EditText)findViewById(R.id.query);

		baseAdapter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent base = new Intent(ListViewHome.this,ListViewWithBaseAdapter.class);
                String str = String.valueOf(query.getText());
                base.putExtra("query",str);
				startActivity(base);
				
			}
		});

	}

}
