package com.ibme.bluetoothtestms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

@SuppressLint("ShowToast")
public class GetUserSavedData extends Activity implements OnClickListener{
	List<Button> buttonList = new ArrayList<Button>();
	File[] fileList;
	PopupWindow popUp;
	LinearLayout mainLL;
	ScrollView sv_main;
	String patientID;
	boolean click = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo_getter);
		
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		     patientID = (String) extras.get("patientID");
		}
		
		//sv_main= (ScrollView) findViewById(R.id)
		mainLL = (LinearLayout) findViewById(R.id.mainLL2);
			mainLL.setBackgroundColor(Color.WHITE);
			
		mainLL.setBackgroundColor(Color.WHITE);
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
    	    
    		
			File baseFile= new File(Environment.getExternalStorageDirectory().getPath()+"/Napnoea_Data/"+ patientID + "/PulseOx");
			
			if(baseFile.isDirectory()){
				
			
			LinearLayout topLayout = new LinearLayout(this);
				topLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				topLayout.setBackgroundColor(Color.rgb(214,229,242)); 
				topLayout.setPadding(5, 15, 5 , 15);
				
				TextView titleTV=  new TextView(this);
					titleTV.setText("Previous Saved Data for Patient: "+ patientID);
					titleTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.activityTitleTextSize);
					titleTV.setTextColor(Color.DKGRAY);
					titleTV.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					titleTV.setGravity(Gravity.LEFT);
					//titleTV.setTypeface(null,Typeface.BOLD);
					topLayout.addView(titleTV);
				mainLL.addView(topLayout);
					
			LinearLayout secondLayout = new LinearLayout(this);
				secondLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				secondLayout.setBackgroundColor(Color.WHITE); 
				secondLayout.setOrientation(LinearLayout.VERTICAL);
				secondLayout.setBackgroundColor(Color.LTGRAY);
				secondLayout.setPadding(0,5,0,0);
				
			 fileList= baseFile.listFiles();
			for(int i = 0; i<fileList.length; i++){
				
				LinearLayout newRow = new LinearLayout(this);
					//newRow.setBackgroundResource(R.drawable.metric_border);
					newRow.setPadding(0, 0, 0, 0);
					newRow.setBackgroundResource(R.drawable.metric_border);
					newRow.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					Button newButton = new Button(this);
						
						//newButton.setBackgroundResource(R.drawable.metric_border);
						newButton.setTextColor(Color.DKGRAY);
						newButton.setText(extractDateInfo(fileList[i].getName()));
						newButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,Constants.normalTextSize);
						newButton.setBackgroundColor(Color.WHITE);
						newButton.setGravity(Gravity.LEFT);
						newButton.setTextColor(Color.rgb(105, 150, 173));
						LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
						param1.gravity= Gravity.CENTER_HORIZONTAL;
						param1.bottomMargin= 5;
						param1.topMargin = 5;
						buttonList.add(i, newButton);
						buttonList.get(i).setId(i);
						newButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 3.0f));
						newButton.setOnClickListener((OnClickListener) GetUserSavedData.this);
					
					newRow.addView(newButton,param1);
				secondLayout.addView(newRow);
			}
			mainLL.addView(secondLayout);			
			
		}
		}
	}
	public void onClick(View v) {
		int index= v.getId();
		
		File currFile = fileList[index];
		
		Intent intent = new Intent(this, DisplayHistoryFromFile.class);
			intent.putExtra("currFile", currFile);
		startActivity(intent);
		
	}
	
	public String extractDateInfo(String fileName){
		String dateVal = fileName.substring(4,6) + "/" + fileName.substring(6,8) + "/" + fileName.substring(0, 4);
		String timeVal = fileName.substring(8,10) +":"+ fileName.substring(10,12)+ ":" + fileName.substring(12,14);		
		String str2Return = "Date: " + dateVal + " - Time: " + timeVal;
		return str2Return;

	}
}
