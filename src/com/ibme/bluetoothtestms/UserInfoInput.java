package com.ibme.bluetoothtestms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserInfoInput extends Activity implements OnClickListener{
LinearLayout mainLL;
LinearLayout subLL1;
TextView tvName;
Button existingUserButton;
Button newUserButton;
Button continueButton;
File selectedFile;
File[] fileList;
TextView existingUserTV;
LinearLayout existingUserLL;
LinearLayout newUserLL;
EditText newUserID_et;
TextView validIDOut_tv;
Button createNewFile_button;
String acceptedText;
Drawable defaultButtonbkgd;


List<Button> buttonList = new ArrayList<Button>();
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo_input);
		
		
		LinearLayout mainLL = (LinearLayout) findViewById(R.id.userinputLL);
			//mainLL.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mainLL.setLayoutDirection(LinearLayout.VERTICAL);
		
		LinearLayout bottomLL= new LinearLayout(this);
			bottomLL.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			bottomLL.setPadding(0, 30, 0, 0);
			bottomLL.setOrientation(LinearLayout.VERTICAL);
			
		continueButton = new Button(this);
		
		continueButton.setOnClickListener((OnClickListener) UserInfoInput.this);
			continueButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		continueButton.setId(3);
		continueButton.setClickable(false);
		continueButton.setText("Submit");
		formatNormalButtons(continueButton, 3);
		
		ImageView oximg = new ImageView(this);
			LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(200, 200);
			vp.gravity= Gravity.CENTER;
			oximg.setImageResource(R.drawable.oxford_crest);
			oximg.setLayoutParams(vp);
			oximg.setAlpha(.4f);
			oximg.setPadding(0, 20, 0, 0);
		bottomLL.addView(continueButton);
		bottomLL.addView(oximg);
		
		TextView title_tv= new TextView(this);
			title_tv.setText("Neonatal Apnea Detector");
			title_tv.setGravity(Gravity.CENTER);
			title_tv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			title_tv.setTextColor(Color.rgb(105, 150, 173));
			title_tv.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
			title_tv.setTextSize(35);
			title_tv.setPadding(0, 35, 0, 0);
		LinearLayout separator = new LinearLayout(this); // probably a better way to do this :P
			separator.setBackgroundResource(R.drawable.title_metric_border);
			separator.setAlpha(.45f);
			separator.setLayoutParams(new LinearLayout.LayoutParams(400, 15));
			mainLL.setGravity(Gravity.CENTER_HORIZONTAL);
			//separator.setPadding(25, 0, 25, 25);
		mainLL.addView(title_tv);
		mainLL.addView(separator);
		existingUserButton = new Button(this);
			existingUserButton.setOnClickListener((OnClickListener) UserInfoInput.this);
			existingUserButton.setId(1);
			existingUserButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
			existingUserButton.setText("Existing User");
			formatNormalButtons(existingUserButton, 0);
		newUserButton = new Button(this);
			newUserButton.setOnClickListener((OnClickListener) UserInfoInput.this);
			newUserButton.setId(2);
			newUserButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
			newUserButton.setText("New User");
			formatNormalButtons(newUserButton, 0);
		
			LinearLayout topLL= new LinearLayout(this); // contains buttons for new user and existing User
			topLL.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			topLL.setPadding(0, 20, 0, 0);
			topLL.addView(existingUserButton);
			topLL.addView(newUserButton);
		mainLL.addView(topLL);
					
		//Existing User stuff
			existingUserLL= new LinearLayout(this);
				existingUserLL.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				existingUserLL.setOrientation(LinearLayout.VERTICAL);
				//existingUserLL.setBackgroundResource(R.drawable.metric_border);
			existingUserLL =createExistingUserList();
			mainLL.addView(existingUserLL);
			existingUserLL.setVisibility(View.GONE);
			
			
			
		// New User Stuff
			newUserLL = new LinearLayout(this);
			newUserLL.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			newUserLL.setOrientation(LinearLayout.VERTICAL);
			newUserLL.setPadding(0, 40, 0, 0);
			//LinearLayout newUserTopRow = new LinearLayout(this);
			
				
			LinearLayout sublayout1= new LinearLayout(this);
				sublayout1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				sublayout1.setWeightSum(4.0f);
				newUserLL.addView(sublayout1);
				//sublayout1.setOrientation(LinearLayout.HORIZONTAL);
				
			newUserID_et= new EditText(this); 
				newUserID_et.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.5f));
				sublayout1.addView(newUserID_et);
			Button newUserID_button= new Button(this);
				newUserID_button.setId(4);
				newUserID_button.setOnClickListener(this);
				newUserID_button.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 2.5f));
				newUserID_button.setText("Check ID Availibility");
				formatNormalButtons(newUserID_button, 4);
				sublayout1.addView(newUserID_button);
			validIDOut_tv = new TextView(this);
				validIDOut_tv.setText("Invalid ID");
				validIDOut_tv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
				
				
			//newUserLL.addView(newUserID_et);
			//newUserLL.addView(sublayout1);
				newUserLL.addView(validIDOut_tv);
			//newUserLL.addView(newUserID_button);
			//newUserLL.addView(newUserTopRow);
			
			createNewFile_button= new Button(this);
				createNewFile_button.setText("Create ID");
				createNewFile_button.setId(5);
				createNewFile_button.setVisibility(View.GONE);
				createNewFile_button.setClickable(false);
				createNewFile_button.setOnClickListener(this);
				formatNormalButtons(createNewFile_button, 1); // green
			newUserLL.addView(createNewFile_button);
			
			
			mainLL.addView(newUserLL);
			newUserLL.setVisibility(View.INVISIBLE);
		//
		//mainLL.addView(subLL1);
		mainLL.addView(bottomLL);
		
		
	}
	public boolean checkValidID(String idIn){
		boolean idValid = false;
		
		if (idIn.length() == 4){
			idValid = true;
			for(int i = 0; i< fileList.length; i++){
				if (idIn.equalsIgnoreCase(fileList[i].getName())){
					idValid = false;
				}
			}
		}
		return idValid;
		
	}
	
	public LinearLayout createExistingUserList(){
		existingUserLL.removeAllViews();
			existingUserTV= new TextView(this);
			existingUserTV.setText(" Please select a user profile and press Submit:");
			existingUserTV.setTextColor(Color.GRAY);
			existingUserTV.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
	LinearLayout existingUserTopRowLL= new LinearLayout(this);
		existingUserTopRowLL.setPadding(0, 0, 0, 0);
		existingUserTopRowLL.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		existingUserTopRowLL.addView(existingUserTV);
		existingUserLL.addView(existingUserTopRowLL);
	if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
		File baseFile= new File(Environment.getExternalStorageDirectory().getPath()+"/Napnoea_Data/");
		if (!baseFile.exists()){
			baseFile.mkdirs();
		}
		fileList= baseFile.listFiles();
		if (fileList.length > 0){
			for(int i = 0; i<fileList.length; i++){
				
				LinearLayout newRow = new LinearLayout(this);
					//newRow.setOrientation(LinearLayout.HORIZONTAL);
					//newRow.setBackgroundResource(R.drawable.metric_border);
					newRow.setPadding(0, 0, 0, 0);
					newRow.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
					Button newButton = new Button(this);
						
						newButton.setBackgroundResource(R.drawable.metric_border);
						newButton.setTextColor(Color.DKGRAY);
						newButton.setText(fileList[i].getName());
						//newButton.setText(extractDateInfo(fileList[i].getName()));
						newButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,Constants.normalTextSize);
						
						newButton.setGravity(Gravity.CENTER);
						//newButton.setTextColor(Color.rgb(105, 150, 173));
						buttonList.add(i, newButton);
						buttonList.get(i).setId(11+i);
						newButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
						newButton.setOnClickListener(this);
					
					newRow.addView(newButton);
					existingUserLL.addView(newRow);
					}
			} else{
				existingUserTV.setText("No Files Available: Create New User");
				}
		}else {
			existingUserTV.setText("No External Storage Available");
		}
	return existingUserLL;
}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int currID= v.getId();
		
		switch (currID){
		case 1: // Existing User
			//int isVisible= 2;
			deselectButtons();
			continueButton.setText("Submit");
			formatNormalButtons(continueButton, 2);
			continueButton.setClickable(false);
			selectedFile = null;
			newUserLL.setVisibility(View.GONE);
			existingUserLL.setVisibility(TextView.VISIBLE);
			break;
		case 2: // New User
			continueButton.setText("Submit");
			continueButton.setClickable(false);
			formatNormalButtons(continueButton, 2);
			validIDOut_tv.setText(" Please Enter a 4 Digit AlphaNumeric Sequence");
			validIDOut_tv.setTextColor(Color.GRAY);
			newUserID_et.setText("");
			selectedFile = null;
			existingUserLL.setVisibility(View.GONE);
			newUserLL.setVisibility(TextView.VISIBLE);
			break;
		case 3:
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("selectedFile", selectedFile.getName());
			//intent.putExtra("currFile", currFile);
			startActivity(intent);
			finish();
			break;
			
		case 4:
			
			String inputText= newUserID_et.getText().toString().toUpperCase(new Locale("ENGLISH"));
			if(checkValidID(inputText)){
				validIDOut_tv.setText(" \"" + inputText  + "\" is an Valid ID");
				validIDOut_tv.setTextColor(Color.GREEN);
				createNewFile_button.setVisibility(View.VISIBLE);
				createNewFile_button.setClickable(true);
				acceptedText = inputText;
				createNewFile_button.setText("Create new ID: "+ acceptedText);
			}else{
				validIDOut_tv.setText(" \"" + inputText  + "\" is an invalid ID or is already being used");
				validIDOut_tv.setTextColor(Color.RED);
				createNewFile_button.setVisibility(View.GONE);
				createNewFile_button.setClickable(false);
			}
			break;
		case 5: // create new file using id
			String folder2create= acceptedText;
			
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				File baseFile= new File(Environment.getExternalStorageDirectory().getPath()+"/Napnoea_Data/"+ folder2create +"/PulseOx");
				if (!baseFile.exists()){
					baseFile.mkdirs();
				}
				selectedFile = baseFile.getParentFile();
			}
			
			
			continueButton.setText("Submit: ID \"" + folder2create + "\"");
			formatNormalButtons(continueButton, 1); // green
			existingUserLL =createExistingUserList();
			continueButton.setClickable(true);
			validIDOut_tv.setText("Id \"" + acceptedText + "\" has been created. Please press Submit.");
			validIDOut_tv.setTextColor(Color.DKGRAY);
			createNewFile_button.setVisibility(View.GONE);
			break;
		default:
			
			if (currID> 10){
				int newFileID = currID-11;
				deselectButtons();
				buttonList.get(newFileID).setBackgroundResource(R.drawable.metric_border_shaded);
				selectedFile = fileList[newFileID];
				//"ID Selected: " + selectedFile.getName() + "\n\n
				continueButton.setText("Submit");
				continueButton.setClickable(true);
				formatNormalButtons(continueButton, 0); // green
			}
		}
		
	}
	private void formatNormalButtons(Button currentButton, int msColorCode){
		currentButton.setTextSize(16);
		currentButton.setTextColor(Color.DKGRAY);
		switch(msColorCode){
		case 0: // blue
			
			currentButton.getBackground().setColorFilter(Color.rgb(105, 170, 255), PorterDuff.Mode.MULTIPLY);
			break;
		case 1: // green
			
			currentButton.getBackground().setColorFilter(Color.rgb(105, 170, 100), PorterDuff.Mode.MULTIPLY);
			break;
		case 2: // red
			
			currentButton.getBackground().setColorFilter(Color.rgb(205, 92, 92), PorterDuff.Mode.MULTIPLY);
			
			break;
		case 3:
			
			break;
		}
	}
	private void deselectButtons(){
		for(int i = 0; i< buttonList.size(); i++){	
			buttonList.get(i).setBackgroundResource(R.drawable.metric_border);
		}
	}

}
