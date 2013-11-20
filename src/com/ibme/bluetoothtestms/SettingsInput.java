package com.ibme.bluetoothtestms;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class SettingsInput extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingspage);
		
		ImageView homeButton = (ImageView) findViewById(R.id.homeButton);
		homeButton.setOnClickListener(new OnClickListener(){

			//@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			finish();
			}
			
		});
	}

}
