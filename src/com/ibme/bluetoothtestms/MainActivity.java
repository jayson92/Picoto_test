package com.ibme.bluetoothtestms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.Plot.BorderStyle;
import com.androidplot.series.XYSeries;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.widget.Widget;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.ibme.bluetoothtestms.Constants.Status;

@SuppressLint({ "NewApi", "HandlerLeak" })
public class MainActivity extends Activity {
	
	boolean connectedBool;
	LinearLayout bottomLL_main;
	LinearLayout bottomLL_graphs;
	private ManageBluetoothConnection manageBluetoothConnection;
	float MAX_DATA_IN_MEMORY 	= Constants.DEFAULT_MAX_DATA_IN_MEMORY;
	private ProgressDialog progressDialog;
	private BluetoothAdapter bluetoothAdapter;
	private UiUpdater uiUpdater = new UiUpdater();
	XYPlot newPlot;
	private int readingsSinceUnusableDataFlagSet = Constants.PULSE_OX_GRACE_PERIOD - 3;
	ImageView connectionButton;
	List<String> dataTitles = new ArrayList<String>();
	LinearLayout mainLL;
	BluetoothSocket bluetoothSocket;
	InputStream inputStream;
	OutputStream outputStream;
	TextView statusField;
	private Constants.Status status;
	private svm_model svmModel;
	ImageView babyImage; 
	File pulseOxOutputFile;
	String patientID; //= "AA01";
	int uiTypeSwitch=1 ;
	int uiTypeSwitchIndicator = 1;
	LinearLayout pictureLL;
	LinearLayout graphLL;
	private List<LinearLayout> graphLayoutList = new ArrayList<LinearLayout>();
	private List<TextView> ValueDisplayList = new ArrayList<TextView>();
	private List<PulseOxReading> pulseOxData = new ArrayList<PulseOxReading>();
	private	List<Number[]> seriesNumbers= new ArrayList<Number[]>();
	private int hrIndex = 0;
	private int o2Index = 1;
		Number[] pulseData = new Number[200];
		Number[] o2Data = new Number[200];
			
	List<XYPlot>plotList= new ArrayList<XYPlot>();
	//int i= 1;
	TextView sigQualDisplay;
	TextView hrDisplay;
	TextView o2Display;
	String selectedFile;
	
	private Handler handler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).setTitle("Bluetooth connection");
			builder.setMessage(msg.obj.toString());
			builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Close the dialog.
				}
			});
			progressDialog.dismiss();
			
			builder.show();
			if (manageBluetoothConnection != null) {
				//connectionButton.setText("Connected");
				//connectionButton.setClickable(false);
				connectedBool = true;
				//formatNormalButtons(connectionButton, 0);
				//connectionButton.setEnabled(false);	
				//connectionButton.getBackground().setColorFilter(Color.rgb(105, 170, 255), PorterDuff.Mode.MULTIPLY);
				Toast.makeText(MainActivity.this, "Bluetooth is connected successfully.", Toast.LENGTH_LONG).show();
				// Create new File
				try {
					//File newFile = null;            	
		        	String prefix = String.valueOf((DateFormat.format("yyyyMMddkkmmss", System.currentTimeMillis())));
		        	String pulseOxFileName = prefix + ".txt"; 
		        	
		        	if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
		        	          		
		        			File patientFolder= new File(Environment.getExternalStorageDirectory().getPath()+"/Napnoea_Data/" + patientID+ "/");
		        				if(!patientFolder.exists()){
		        					patientFolder.mkdirs();
		        				}
		        				
		        			pulseOxOutputFile =  new File(patientFolder.getPath()+ "/PulseOx/"+ pulseOxFileName);
		        				if(!pulseOxOutputFile.getParentFile().exists()){
		        					pulseOxOutputFile.getParentFile().mkdirs();
		        				}
		        				pulseOxOutputFile.createNewFile();        		
		        	} else {
		            		//pulseOxOutputFile = new File(pulseOxFileName);
		            		Toast.makeText(MainActivity.this, "in else statement", Toast.LENGTH_LONG).show();
		        	}
		        	//Toast.makeText(this, pulseOxOutputFile.getPath(), Toast.LENGTH_LONG).show();
				} catch(Exception e) {
					Toast.makeText(MainActivity.this, "File management error.", Toast.LENGTH_LONG).show();	
				}
			}
		}
	};
	
	public class UiUpdater implements Runnable{
		@Override
		public void run(){
			int z= 0;
			if(plotList.size()<dataTitles.size()){
				//bottomLL_main.setVisibility(LinearLayout.INVISIBLE);
				
				while (plotList.size()< dataTitles.size()){
					
					LinearLayout newLayout= setUpRowGraphs(z, new int[] {0, 100}, dataTitles.get(z));
					graphLL.addView(newLayout);
					graphLayoutList.add(z, newLayout);
					z++;
					}
				
			}
				
			if (uiTypeSwitchIndicator ==1){
				if (uiTypeSwitch == 1){ // main view
					graphLL.setVisibility(LinearLayout.GONE);
					bottomLL_graphs.setVisibility(LinearLayout.GONE);
					pictureLL.setVisibility(LinearLayout.VISIBLE);
					bottomLL_main.setVisibility(LinearLayout.VISIBLE);
					/*for(int i = 0; i< graphLayoutList.size(); i++){
						LinearLayout currentLayout= graphLayoutList.get(i);
						currentLayout.setVisibility(LinearLayout.GONE);
					}*/
					uiTypeSwitchIndicator = 0;
				
				}
				if (uiTypeSwitch == 2){ 
					bottomLL_graphs.setVisibility(LinearLayout.VISIBLE);
					bottomLL_main.setVisibility(LinearLayout.GONE);
				pictureLL.setVisibility(LinearLayout.GONE);// graph view
					graphLL.setVisibility(LinearLayout.VISIBLE);
					/*for(int i = 0; i< graphLayoutList.size(); i++){
						LinearLayout currentLayout= graphLayoutList.get(i);
						currentLayout.setVisibility(LinearLayout.VISIBLE);
					}*/
					uiTypeSwitchIndicator = 0;
				}
			}
			long start = System.currentTimeMillis();
			
			int pulseOxDataSize = pulseOxData.size();
			double latestPulseOxTime = pulseOxDataSize != 0 ? pulseOxData.get(pulseOxDataSize - 1).getTimeStamp() : 0;
			
			
			long end = System.currentTimeMillis();
			
			if (pulseOxDataSize < 2) {
				
				setStatus(Status.Grey);
			} else {
				PulseOxReading latestPulseOxReading = pulseOxData.get(pulseOxDataSize - 1);
				String hrVal = String.valueOf(latestPulseOxReading.getHeartRate());
				String o2Val = String.valueOf(latestPulseOxReading.getSpo2());
				
				TextView hrDisplaycurr = ValueDisplayList.get(hrIndex);
				TextView o2Displaycurr = ValueDisplayList.get(o2Index);
				int latestSignalQuality = latestPulseOxReading.getSignalQuality();
				
				if (!latestPulseOxReading.isUnusableData()) {
					switch (latestSignalQuality) {
					case 0:
						sigQualDisplay.setTextColor(Color.RED);
						sigQualDisplay.setText("Quality: none");
						
						break;
					case 1:
						sigQualDisplay.setText("Quality: low");
						sigQualDisplay.setTextColor(Color.rgb(255, 69, 0));
						
						break;
					case 2:
						sigQualDisplay.setText("Quality: marginal");
						sigQualDisplay.setTextColor(Color.YELLOW);
						
						break;
					case 3:
						sigQualDisplay.setText("Quality: good");
						sigQualDisplay.setTextColor(Color.rgb(0, 100, 0));
						
						break;
					default:
						sigQualDisplay.setText("Quality: -");
						sigQualDisplay.setTextColor(Color.GRAY);
						break;
					}
				
					hrDisplaycurr.setText(hrVal);
					o2Displaycurr.setText(o2Val);
					Number newDataVal= 1;
					int[] yBounds = {0, 100};
					for (int i= 0; i< dataTitles.size(); i++){
						Number[] currentSeriesNumbers= seriesNumbers.get(i);
						switch (i){
						case 0:
							
							newDataVal= (Number) latestPulseOxReading.getHeartRate();
							yBounds[0]= 45; yBounds[1]= 140;
							break;
						case 1:
							newDataVal= (Number) latestPulseOxReading.getSpo2();
							yBounds[0]= 80; yBounds[1]= 110;
							break;
						}
						currentSeriesNumbers = plotNextData(currentSeriesNumbers,newDataVal, plotList.get(i), yBounds);
						seriesNumbers.set(i, currentSeriesNumbers);
					}
				} else {
					sigQualDisplay.setText("Quality: none");
					sigQualDisplay.setTextColor(Color.RED);
				}
				
				
				List<PulseOxReading> trimmedPulseOxData = Utils.trimPulseOxDataSeries(pulseOxData, 50);
				List<Double> pulseOxHr = new ArrayList<Double>();
				List<Double> pulseOxO2 = new ArrayList<Double>();
				List<Double> pulseOxTimeStamps = new ArrayList<Double>();
				for(PulseOxReading reading : trimmedPulseOxData) {
					pulseOxHr.add((double)reading.getHeartRate());
					pulseOxO2.add((double)reading.getSpo2());
					pulseOxTimeStamps.add((reading.getTimeStamp() - latestPulseOxTime) / 1000);
					
				}
				checkStatus(latestPulseOxReading, pulseOxData.get(pulseOxDataSize - 2));
				try {
					FileWriter fileWriter = new FileWriter(pulseOxOutputFile, true);
					BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
					bufferedWriter.write(latestPulseOxReading.getCsvFormattedOutput());
					bufferedWriter.flush();					
					bufferedWriter.close();
					fileWriter.close();
				} catch (IOException e) {
					Toast.makeText(MainActivity.this, "Failed to write pulse oximeter data to file.", Toast.LENGTH_LONG).show();
				}
			}
			//i++;
			//out.setText(currentPulseOxReading);
			handler.postDelayed(uiUpdater, Math.max(1, 500-(end-start))); // repost runnable to begin 500ms after current iteration began
		}
	}
	
	
	private class ManageBluetoothConnection extends AsyncTask<Context, PulseOxReading,String>{
		
		private BluetoothSocket _bluetoothSocket;
		private InputStream _inputStream;
		private OutputStream _outputStream;
		public  ManageBluetoothConnection(BluetoothSocket socket){
			_bluetoothSocket = socket;
			InputStream tempIn = null;
			OutputStream tempOut = null;

			try {
				tempIn = socket.getInputStream();
				tempOut = socket.getOutputStream();
			} catch(IOException e){}
			_inputStream = tempIn;
			_outputStream = tempOut;
		}
		public String doInBackground(Context...  params){
			
			String noninControlMessage= "D8";
			byte[] msgBuffer= noninControlMessage.getBytes();
			try{
				_outputStream.write(msgBuffer);
			} catch(IOException e)
			{Toast.makeText(MainActivity.this, "nonin control message failure.", Toast.LENGTH_LONG).show();}
			byte[] buffer=  new byte[1024];
			while(true){
				try{
					_inputStream.read(buffer);
					PulseOxReading currentPulseOxReading = new PulseOxReading(buffer);
					publishProgress(currentPulseOxReading);		
				}catch(Exception e){
					break;
				}
			}
			return "Stopped.";
		}
		@Override
		protected void onProgressUpdate(PulseOxReading... params){
			super.onProgressUpdate(params);
			PulseOxReading incomingReading = params[0];
			boolean insideGracePeriod = readingsSinceUnusableDataFlagSet < Constants.PULSE_OX_GRACE_PERIOD;
			
			if (incomingReading.isUnusableData() || insideGracePeriod) {
				if (incomingReading.isUnusableData()) {
					// If this is the first reading with an unusable data flag, notify the user.
					if (readingsSinceUnusableDataFlagSet != 0) {
						Toast.makeText(MainActivity.this, "Device is providing unusable data (maybe finger is removed)", Toast.LENGTH_SHORT).show();
					}
					readingsSinceUnusableDataFlagSet = 0;
				} else if (insideGracePeriod) {
					// If the flag isn't set but we're inside the grace period, give the
					// pulse ox time to settle.
					
					// If this is the first good reading after an unusable data flag, notify the user.
					if (readingsSinceUnusableDataFlagSet == 0) {
						Toast.makeText(MainActivity.this, "Problem resolved - data is now usable.", Toast.LENGTH_SHORT).show();
					}
					// Increment unusable data flag count.
					readingsSinceUnusableDataFlagSet++;
				}
				
				// If data is unusable or we are inside the grace period given to the 
				// pulse ox to get its act together, prepare a dummy read to add to memory.
				incomingReading = new PulseOxReading();
			}
			pulseOxData.add(incomingReading);
			int size = pulseOxData.size();
			if (size > MAX_DATA_IN_MEMORY) {
				pulseOxData.subList((int)(MAX_DATA_IN_MEMORY * 0.8), size - 1);
			}
			
		}
		
		public void onCancelled(){
			super.onCancelled();
			onPostExecute("Cancelled");
		}
		public void onPostExecute(String result) {
			super.onPostExecute(result);
			try{
				_inputStream.close();
				_outputStream.close();
				_bluetoothSocket.close();
				
				pulseOxData.add(new PulseOxReading());
				manageBluetoothConnection = null;
				connectedBool = false;
				//connectionButton.setText("Reconnect");
				//connectionButton.setClickable(true);
				//connectionButton.setEnabled(true);
			}catch (IOException e ){}
			
			
		}
		
	}
		
	public class BluetoothConnector implements Runnable {
		private void resetConnection() {
	        if (inputStream != null) {
	                try {inputStream.close();} catch (Exception e) {}
	                inputStream = null;
	        }

	        if (outputStream != null) {
	                try {outputStream.close();} catch (Exception e) {}
	                outputStream = null;
	        }

	        if (bluetoothSocket != null) {
	                try {bluetoothSocket.close();} catch (Exception e) {}
	                bluetoothSocket = null;
	        }
		}
			@Override
			public void run() {
				resetConnection();
				StringBuilder log = new StringBuilder();
				
				// Check if connection already exists.
				if (manageBluetoothConnection == null) {
					@SuppressWarnings("unused")
					boolean question1= bluetoothAdapter.startDiscovery();
					String NONIN_ADDRESS = "00:1C:05:00:4F:43";
					// Define device.
					BluetoothDevice device = bluetoothAdapter.getRemoteDevice(NONIN_ADDRESS);
					log.append("Pulse oximeter name: \"" + device.getName() + "\"");
					log.append("\n\nMAC address: " + device.getAddress());
					Method m = null;
					try {

						 m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
						BluetoothSocket bluetoothSocket = null;
						int socketNumber = 1;
						bluetoothSocket = (BluetoothSocket) m.invoke(device, socketNumber);
						bluetoothAdapter.cancelDiscovery();
						log.append("\n\nInvoked bluetooth socket: "+ socketNumber);
						try {
							
							bluetoothSocket.connect();
							
							manageBluetoothConnection = new ManageBluetoothConnection(bluetoothSocket);	
							manageBluetoothConnection.execute(MainActivity.this);
							log.append("\n\nBluetooth connection established, data transfer link open.");
							
							
							dataTitles.clear();
							dataTitles.add(hrIndex, "Heart Rate\n(bpm)");
							dataTitles.add(o2Index, "Oxygen Saturation\n(%)");
							seriesNumbers.add(hrIndex, pulseData);
							seriesNumbers.add(o2Index, o2Data);
							uiTypeSwitchIndicator =1;
							
						} catch (IOException e) {
							try {
								bluetoothSocket.close();
								log.append("\n\nError: Connection failure, socket closed.");
							} catch (IOException e2) {
								log.append("\n\nError: Unable to close socket during connection failure.");
							}
						}
					} catch (Exception e) {
						log.append("\n\nError: Bluetooth appears to be turned off.");
					}
				} else {
					log.append("\n\nAlready connected to device.");
				}
				
				// Use handler to update and return to UI thread
				Message message = new Message();
				message.obj = log.toString();
				handler.sendMessage(message);
			}	
	}
		
		
		
	
	
	
	   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			patientID = (String) extras.get("selectedFile");
		    //patientID = selectedFile.substring(selectedFile.length()-5, selectedFile.length()-1);
		}
		// setup Opening Layout
		//patientID = selectedFile;
		
		babyImage = new ImageView(this);
		babyImage.setImageResource(R.drawable.baby_pic);
		mainLL= (LinearLayout) findViewById(R.id.rl1);
		mainLL.setBackgroundColor(Color.rgb(86,119,140));
		
		// Main Button Setup
				bottomLL_main = new LinearLayout(this);
				bottomLL_main.setLayoutDirection(LinearLayout.HORIZONTAL);
					bottomLL_main.setBackgroundColor(Color.rgb(86, 119, 140));
					//final Button historyButton= new Button (this);
					final ImageView historyButton = new ImageView(this);
					historyButton.setImageResource(R.drawable.history);
						//formatNormalButtons(historyButton, 1);
						//historyButton.setText(" History: Patient " +  patientID);
						//historyButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.buttonTextSize);
						
						historyButton.setLayoutParams(new LinearLayout.LayoutParams(200,200,1.0f));
						historyButton.setOnTouchListener(new OnTouchListener(){
							public boolean onTouch(View v, MotionEvent event) {
								historyButton.setImageResource(R.drawable.history_pressed);
								switch(event.getAction())
								{
								case MotionEvent.ACTION_DOWN:
									historyButton.setImageResource(R.drawable.history_pressed);
									return true;
								case MotionEvent.ACTION_UP :
									historyButton.setImageResource(R.drawable.history);
									Intent getUserInfo = new Intent(MainActivity.this, GetUserSavedData.class);
									getUserInfo.putExtra("patientID", patientID);
									MainActivity.this.startActivity(getUserInfo);
									return false;
								}
								return false;
				                }

						});
						final ImageView connectionButton= new ImageView(this);
						connectionButton.setImageResource(R.drawable.socks2);
						//formatNormalButtons(historyButton, 1);
						//historyButton.setText(" History: Patient " +  patientID);
						//historyButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.buttonTextSize);
						
						connectionButton.setLayoutParams(new LinearLayout.LayoutParams(200,200,1.0f));
						
						
						
						final ImageView settingsButton= new ImageView(this);
						settingsButton.setImageResource(R.drawable.settings2);
						
						settingsButton.setLayoutParams(new LinearLayout.LayoutParams(200,200,1.0f));
						settingsButton.setOnTouchListener(new OnTouchListener(){
							public boolean onTouch(View v, MotionEvent event) {
								settingsButton.setImageResource(R.drawable.settings2_pressed);
								switch(event.getAction())
								{
								case MotionEvent.ACTION_DOWN:
									settingsButton.setImageResource(R.drawable.settings2_pressed);
									return true;
								case MotionEvent.ACTION_UP :
									settingsButton.setImageResource(R.drawable.settings2);
									// to do- add functionality?
									Intent settingsIntent = new Intent(MainActivity.this, SettingsInput.class);
									//settings.putExtra("patientID", patientID);
									MainActivity.this.startActivity(settingsIntent);
									return false;
								}
								return false;
				                }

						});
						
					bottomLL_main.addView(historyButton);
					bottomLL_main.addView(connectionButton);
					bottomLL_main.addView(settingsButton);
		
					
		// graph buttons setup
					bottomLL_graphs = new LinearLayout(this);
					bottomLL_graphs.setLayoutDirection(LinearLayout.HORIZONTAL);
						bottomLL_graphs.setBackgroundColor(Color.rgb(86, 119, 140));
						//final Button historyButton= new Button (this);
						final ImageView historyButtonGraphs = new ImageView(this);
						historyButtonGraphs.setImageResource(R.drawable.history);
							//formatNormalButtons(historyButton, 1);
							//historyButton.setText(" History: Patient " +  patientID);
							//historyButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.buttonTextSize);
							
							historyButtonGraphs.setLayoutParams(new LinearLayout.LayoutParams(200,200,1.0f));
							historyButtonGraphs.setOnTouchListener(new OnTouchListener(){
								public boolean onTouch(View v, MotionEvent event) {
									historyButtonGraphs.setImageResource(R.drawable.history_pressed);
									switch(event.getAction())
									{
									case MotionEvent.ACTION_DOWN:
										historyButtonGraphs.setImageResource(R.drawable.history_pressed);
										return true;
									case MotionEvent.ACTION_UP :
										historyButtonGraphs.setImageResource(R.drawable.history);
										Intent getUserInfo = new Intent(MainActivity.this, GetUserSavedData.class);
										getUserInfo.putExtra("patientID", patientID);
										MainActivity.this.startActivity(getUserInfo);
										return false;
									}
									return false;
					                }

							});
							final ImageView homeButton= new ImageView(this);
							homeButton.setImageResource(R.drawable.home2);
							//formatNormalButtons(historyButton, 1);
							//historyButton.setText(" History: Patient " +  patientID);
							//historyButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.buttonTextSize);
							
							homeButton.setLayoutParams(new LinearLayout.LayoutParams(200,200,1.0f));
							homeButton.setOnTouchListener(new OnTouchListener(){
								public boolean onTouch(View v, MotionEvent event) {
									homeButton.setImageResource(R.drawable.home2_pressed);
									switch(event.getAction())
									{
									case MotionEvent.ACTION_DOWN:
										homeButton.setImageResource(R.drawable.home2_pressed);
										return true;
									case MotionEvent.ACTION_UP :
										homeButton.setImageResource(R.drawable.home2);
										uiTypeSwitch = 1;
										uiTypeSwitchIndicator =1;
										// to do- add functionality?
										return false;
									}
									return false;
					                }
								});
							
							final ImageView settingsButtonGraphs= new ImageView(this);
							settingsButtonGraphs.setImageResource(R.drawable.settings2);
							
							settingsButtonGraphs.setLayoutParams(new LinearLayout.LayoutParams(200,200,1.0f));
							settingsButtonGraphs.setOnTouchListener(new OnTouchListener(){
								public boolean onTouch(View v, MotionEvent event) {
									settingsButtonGraphs.setImageResource(R.drawable.settings2_pressed);
									switch(event.getAction())
									{
									case MotionEvent.ACTION_DOWN:
										settingsButtonGraphs.setImageResource(R.drawable.settings2_pressed);
										return true;
									case MotionEvent.ACTION_UP :
										settingsButtonGraphs.setImageResource(R.drawable.settings2);
										// to do- add functionality?
										Intent settingsIntent = new Intent(MainActivity.this, SettingsInput.class);
										//settings.putExtra("patientID", patientID);
										MainActivity.this.startActivity(settingsIntent);
										return false;
									}
									return false;
					                }

							});
						bottomLL_graphs.addView(homeButton);	
						bottomLL_graphs.addView(historyButtonGraphs);
						bottomLL_graphs.addView(settingsButtonGraphs);
					
					
			
				LinearLayout topLL = new LinearLayout(this);
					topLL.setBackgroundColor(Color.rgb(86, 119, 140));
					topLL.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					ImageView picotoLogoIMG = new ImageView(this);
					picotoLogoIMG.setImageResource(R.drawable.picoto_logo3);
					picotoLogoIMG.setAdjustViewBounds(true);
					picotoLogoIMG.setPadding(15, 15, 35, 0);
					picotoLogoIMG.setMaxWidth(600);
					topLL.addView(picotoLogoIMG);
					mainLL.addView(topLL);
				
				LinearLayout secondLL= new LinearLayout(this);
					secondLL.setBackgroundColor(Color.rgb(86, 119, 140));
					secondLL.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				sigQualDisplay = new TextView(this);
					sigQualDisplay.setText("Quality: --- ");
					sigQualDisplay.setBackgroundColor(Color.WHITE);
					sigQualDisplay.setTextColor(Color.GRAY);
					sigQualDisplay.setGravity(Gravity.CENTER);
					sigQualDisplay.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
				statusField= new TextView(this);
					statusField.setText("Status: ---");
					statusField.setTextColor(Color.BLACK);
					statusField.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
					statusField.setGravity(Gravity.CENTER);
					statusField.setBackgroundColor(Color.LTGRAY);
				secondLL.addView(sigQualDisplay);
				secondLL.addView(statusField);
				
				
				//mainLL.addView(secondLL);
				
				pictureLL = new LinearLayout(this);
					pictureLL.setBackgroundColor(Color.rgb(86, 119, 140));
					pictureLL.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f));
					LinearLayout.LayoutParams pcLLLP = (LinearLayout.LayoutParams) pictureLL.getLayoutParams();
					pcLLLP.gravity= Gravity.CENTER_HORIZONTAL;
					pictureLL.setPadding(0, 40, 0, 40);
					pictureLL.setLayoutParams(pcLLLP);
					
					TextView testText2= new TextView(this);
					testText2.setText("Testing 123");
					babyImage.setPadding(40, 40, 40, 40);
					babyImage.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							uiTypeSwitch = 2;
							uiTypeSwitchIndicator =1;
						}
						
					});
					//babyImage.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
					LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					vp.gravity= Gravity.CENTER;
					babyImage.setLayoutParams(vp);
					
					babyImage.setBackgroundColor(Color.GRAY);
				pictureLL.addView(babyImage);
				mainLL.addView(pictureLL);
				graphLL= new LinearLayout(this);
				graphLL.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 3.0f));
				graphLL.setBackgroundColor(Color.rgb(86,129,130));
				graphLL.setOrientation(LinearLayout.VERTICAL);
				graphLL.addView(secondLL);
				mainLL.addView(graphLL);
				mainLL.addView(bottomLL_main);
				mainLL.addView(bottomLL_graphs);
				bottomLL_graphs.setVisibility(LinearLayout.GONE);
				
				
		/** Bluetooth phone setup. */
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// Check for Bluetooth support in the first place.
		if (bluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not supported on this device.", Toast.LENGTH_LONG).show();
		} else {
			if (!bluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
				
			}
			connectedBool = false;
			connectionButton.setOnTouchListener(new OnTouchListener(){
				public boolean onTouch(View v, MotionEvent event) {
					
					if(!connectedBool){
					connectionButton.setImageResource(R.drawable.socks2_pressed);
					switch(event.getAction())
					{
					case MotionEvent.ACTION_DOWN:
						connectionButton.setImageResource(R.drawable.socks2_pressed);
						return true;
					case MotionEvent.ACTION_UP :
						connectionButton.setImageResource(R.drawable.socks2);
						progressDialog = ProgressDialog.show(MainActivity.this, "Connecting...", "Setting up Bluetooth connection", true, false);
	        			new Thread(new BluetoothConnector()).start();

						return false;
					}
					
	                }
					return false;
				}
				

			});
		}
		
		

		
		    Thread uiUpdate = new Thread() {
	        	public void run() {
	        		handler.post(uiUpdater);
	        	}
	        };
	        uiUpdate.setDaemon(true);
	        uiUpdate.start();
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		  //super.onCreateContextMenu(menu, v, menuInfo);
		  getMenuInflater().inflate(R.menu.menu, menu);
		     //menu.("Context Menu");
		return true;
	}
	
	/** Deals with results from other Activities. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
    		case Constants.REQUEST_ENABLE_BT:
    			if (resultCode == RESULT_OK) {
    				Toast.makeText(this, "Bluetooth enabled.", Toast.LENGTH_SHORT).show();
    			} else {
    				Toast.makeText(this, "Bluetooth was not enabled.", Toast.LENGTH_LONG).show();
    			}
    			break;
    		default:
    			Toast.makeText(this, "Unknown call to onActivityResult()", Toast.LENGTH_LONG).show();
    			break;
    	}
    }
    
    public Number[] plotNextData(Number[] series2PlotNumbers, Number newNumber, XYPlot currXYPlot, int[] yBounds){
   	 	series2PlotNumbers = addElement(series2PlotNumbers, newNumber);
    	LineAndPointFormatter series2PlotFormat = new LineAndPointFormatter(Color.rgb(105, 150, 173), null, 
   				null, (PointLabelFormatter) null);                                 // fill color (none)
    	Paint lineFill = new Paint();
    	lineFill.setAlpha(10);        
   	    lineFill.setShader(new LinearGradient(0, 0, 0, 600,  Color.rgb(105, 150, 173), Color.WHITE, Shader.TileMode.CLAMP));
        series2PlotFormat.setFillPaint(lineFill);
        XYSeries series2Plot = new SimpleXYSeries(Arrays.asList(series2PlotNumbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Movement");
   	    
   	    currXYPlot.clear();
   		setPlotDets(currXYPlot, yBounds);
   		currXYPlot.addSeries(series2Plot, series2PlotFormat);
        currXYPlot.redraw();
        return series2PlotNumbers;
   	 }
   	 
    
    public Number [] addElement(Number[] org, Number added) { 
	    Number[] result1 = Arrays.copyOfRange(org, 1, org.length+1);
	    Number[] result = Arrays.copyOf(result1, result1.length);
	    result[result.length-1]= added;
	    return result;
	}
    private void setPlotDets(XYPlot currXYPlot, int[] yBounds){
    	currXYPlot.setTicksPerRangeLabel(1);
        Paint paintex1 = currXYPlot.getGraphWidget().getGridBackgroundPaint();
        paintex1.setColor(Color.WHITE);

        Widget currgwidget= currXYPlot.getGraphWidget();
        	SizeMetrics sm = new SizeMetrics(0, SizeLayoutType.FILL, 0 , SizeLayoutType.FILL);
        currgwidget.setSize(sm);
        currXYPlot.getGraphWidget().setGridBackgroundPaint(paintex1);

        currXYPlot.getDomainLabelWidget().setVisible(false);
        currXYPlot.getRangeLabelWidget().setVisible(false);

       currXYPlot.getGraphWidget().getCursorLabelBackgroundPaint().setAlpha(0);
        currXYPlot.getGraphWidget().setMargins(0, 20, 25, 5); // left, top, right, bottom
        currXYPlot.setGridPadding(0, 0, 0, 0);
        currXYPlot.setPlotMargins(0, 0, 0, 0);
        currXYPlot.setPlotPadding(0, 0, 0, 0);
        currXYPlot.setRangeBoundaries(0, 25, BoundaryMode.FIXED);
        currXYPlot.setBorderStyle(BorderStyle.NONE, null, null);
        currXYPlot.getBackgroundPaint().setColor(Color.WHITE);
        
        currXYPlot.getTitleWidget().setVisible(true);
        
        currXYPlot.setDomainBoundaries(0,200, BoundaryMode.FIXED);
        currXYPlot.getLegendWidget().setVisible(false);
    	
    	
    	
       currXYPlot.setRangeBoundaries(yBounds[0], yBounds[1], BoundaryMode.FIXED);

	}
    LinearLayout setUpRowGraphs(int i, int[] yBounds, String graphTitle){
		LinearLayout newrowLL= new LinearLayout(this); // encompasses row
			newrowLL.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			newrowLL.setOrientation(LinearLayout.HORIZONTAL);
			newrowLL.setBackgroundResource(R.drawable.metric_border_blue);
			newrowLL.setPadding(0, 10, 10, 10);
			LinearLayout subLL_Left = new LinearLayout(this); // splits row LL (left for text and right for plot)
				subLL_Left.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, 4.0f));
				subLL_Left.setOrientation(LinearLayout.VERTICAL);
				//newrowLL.addView(subLL_Left);
				
				TextView newTV = new TextView(this);
				LinearLayout.LayoutParams tvLP= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					newTV.setLayoutParams(tvLP);
					newTV.setGravity(Gravity.CENTER);
					newTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,Constants.normalTextSize);
					newTV.setTextColor(Color.LTGRAY);
					//tV1List.add(i,newTV);
				TextView newTV2 = new TextView(this);
					LinearLayout.LayoutParams tv2LP= tvLP;
					tv2LP.setMargins(0, 70, 0, 0);
					newTV2.setLayoutParams(tv2LP);
					newTV2.setGravity(Gravity.CENTER);
					newTV2.setTextSize(TypedValue.COMPLEX_UNIT_SP,Constants.valueTextSize);
					newTV2.setTextColor(Color.WHITE);

				
				subLL_Left.addView(newTV);
				subLL_Left.addView(newTV2);
				
			LinearLayout subLL_Right = new LinearLayout(this);
				subLL_Right.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, 1.0f));
				subLL_Right.setOrientation(LinearLayout.HORIZONTAL);
				subLL_Right.setBackgroundColor(Color.DKGRAY);
				
				XYPlot newPlot = new XYPlot(this, "");
					newPlot.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 400));
					setPlotDets(newPlot, yBounds);
					plotList.add(i, newPlot);
				subLL_Right.addView(newPlot);
		
				
		//int currPatient = i+1;
		newTV.setText(graphTitle);
		
		newTV2.setText("---");
		ValueDisplayList.add(i, newTV2);
		
		
		
		newrowLL.addView(subLL_Left);
		newrowLL.addView(subLL_Right);
		return newrowLL;
		
	}
public void checkStatus(PulseOxReading latestPulseOxReading, PulseOxReading secondLatestPulseOxReading) {
		
		if (latestPulseOxReading.isUnusableData()) {
			// Most recent data is unusable, status is unknown.
			setStatus(Status.Grey);
			changeImageStatus(0);
			return;
		}
		
		if (latestPulseOxReading.getSpo2() >= Constants.DESATURATION_THRESHOLD) {
			// Patient is not hypoxic.
			setStatus(Status.Green);
			changeImageStatus(1);
			return;
		}
		
		// Patient is hypoxic.
		if (!(secondLatestPulseOxReading.getSpo2() < Constants.DESATURATION_THRESHOLD)) {
			// Desaturation has *just* occurred.
			setStatus(Status.Amber);
			changeImageStatus(2);
			// Run the SVM.
			//double svmClassification = runMachineLearning();
			double svmClassification =1;
			if (svmClassification == 1) {
				// Desaturation is apnoea-related.
				setStatus(Status.Red);
				changeImageStatus(3);
			} else {
				// Desaturation is false.
			}
			return;
		}
		
		// Patient is hypoxic but this is an existing condition - do nothing.
		return;
	}
private double runMachineLearning() {
	
	List<Integer> windowedSpo2 = new ArrayList<Integer>();
	List<Integer> windowedSqi = new ArrayList<Integer>();
	int size = pulseOxData.size();
	int i = size - 1;
	while (i >= 0 && pulseOxData.get(i).getTimeStamp() > pulseOxData.get(size - 1).getTimeStamp() - 5000) {
		windowedSpo2.add(pulseOxData.get(i).getSpo2());
		windowedSqi.add(pulseOxData.get(i).getSignalQuality());
		i--;
	}
	double minSpo2InWindow = Collections.min(windowedSpo2);
	double normalisedMinSpo2InWindow = (minSpo2InWindow - 63.3752) / 46.3712;
	double minSqiInWindow = Collections.min(windowedSqi);
	double normalisedMinSqiInWindow = ((minSqiInWindow/4) - 0.0883) / 0.1355;
	
	
	svm_node[] svmNode = new svm_node[2];
	
	svmNode[0] = new svm_node();
	svmNode[0].index = 1;
	svmNode[0].value = normalisedMinSpo2InWindow;
	
	svmNode[1] = new svm_node();
	svmNode[1].index = 2;
	svmNode[1].value = normalisedMinSqiInWindow;
	
	return svm.svm_predict(svmModel, svmNode);
}
protected void setStatus(Constants.Status newStatus) {
	if (newStatus.equals(status)) {
		return;
	}
	
	//TextView statusField = (TextView)findViewById(R.id.status);
	switch(newStatus) {  
	case Grey:
		Utils.setStatus(statusField,"Status: Unknown", 0xFFBBBBBB, Color.BLACK);
		return;
	case Green:
		Utils.setStatus(statusField,"Status: Normal", Color.rgb(127,202,76), Color.BLACK);
		//if (isAlarm) alarmDialog.cancel();
		return;
	case Amber:
		Utils.setStatus(statusField, "Status: Possible apnoea", 0xFFFFDD00, Color.BLACK);
		return;
	case Red:
	default:
		Utils.setStatus(statusField, "Status: Apnoea- try to rouse infant", Color.RED, Color.WHITE);  		
		//if (!isAlarm) startAlarm();
		return;    		
	}
}

private void changeImageStatus(int statusInt){
	switch(statusInt){
	case 0:// unknown
		babyImage.setBackgroundColor(Color.GRAY);
		break;
	case 1:// healthy
		babyImage.setBackgroundColor(Color.rgb(127,202,76));
		break;
	case 2:// possible apnea
		babyImage.setBackgroundColor(Color.rgb(255, 255, 0));
		break;
	case 3: // apnea
		babyImage.setBackgroundColor(Color.RED);
	}
}

private void setupMainButtons(){
	bottomLL_main = new LinearLayout(this);
	bottomLL_main.setLayoutDirection(LinearLayout.HORIZONTAL);
		bottomLL_main.setBackgroundColor(Color.rgb(86, 119, 140));
		//final Button historyButton= new Button (this);
		final ImageView historyButton = new ImageView(this);
		historyButton.setImageResource(R.drawable.history);
			//formatNormalButtons(historyButton, 1);
			//historyButton.setText(" History: Patient " +  patientID);
			//historyButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.buttonTextSize);
			
			historyButton.setLayoutParams(new LinearLayout.LayoutParams(200,200,1.0f));
			historyButton.setOnTouchListener(new OnTouchListener(){
				public boolean onTouch(View v, MotionEvent event) {
					historyButton.setImageResource(R.drawable.history_pressed);
					switch(event.getAction())
					{
					case MotionEvent.ACTION_DOWN:
						historyButton.setImageResource(R.drawable.history_pressed);
						return true;
					case MotionEvent.ACTION_UP :
						historyButton.setImageResource(R.drawable.history);
						Intent getUserInfo = new Intent(MainActivity.this, GetUserSavedData.class);
						getUserInfo.putExtra("patientID", patientID);
						MainActivity.this.startActivity(getUserInfo);
						return false;
					}
					return false;
	                }

			});
			final ImageView connectionButton= new ImageView(this);
			connectionButton.setImageResource(R.drawable.socks2);
			//formatNormalButtons(historyButton, 1);
			//historyButton.setText(" History: Patient " +  patientID);
			//historyButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.buttonTextSize);
			
			connectionButton.setLayoutParams(new LinearLayout.LayoutParams(200,200,1.0f));
			
			
			
			final ImageView settingsButton= new ImageView(this);
			settingsButton.setImageResource(R.drawable.settings2);
			
			settingsButton.setLayoutParams(new LinearLayout.LayoutParams(200,200,1.0f));
			settingsButton.setOnTouchListener(new OnTouchListener(){
				public boolean onTouch(View v, MotionEvent event) {
					settingsButton.setImageResource(R.drawable.settings2_pressed);
					switch(event.getAction())
					{
					case MotionEvent.ACTION_DOWN:
						settingsButton.setImageResource(R.drawable.settings2_pressed);
						return true;
					case MotionEvent.ACTION_UP :
						settingsButton.setImageResource(R.drawable.settings2);
						// to 
						return false;
					}
					return false;
	                }

			});
			
		bottomLL_main.addView(historyButton);
		bottomLL_main.addView(connectionButton);
		bottomLL_main.addView(settingsButton);
}
private void formatNormalButtons(Button currentButton, int msColorCode){
	currentButton.setTextSize(16);
	currentButton.setTextColor(Color.DKGRAY);
	switch(msColorCode){
	case 0: // blue
		//currentButton.setBackgroundDrawable(defaultButtonbkgd);
		currentButton.getBackground().setColorFilter(Color.rgb(105, 170, 255), PorterDuff.Mode.MULTIPLY);
		break;
	case 1: // green
		//currentButton.setBackgroundDrawable(defaultButtonbkgd);
		currentButton.getBackground().setColorFilter(Color.rgb(105, 170, 100), PorterDuff.Mode.MULTIPLY); //105, 170, 100
		break;
	case 2: // red
		//currentButton.setBackgroundDrawable(defaultButtonbkgd);
		currentButton.getBackground().setColorFilter(Color.rgb(205, 92, 92), PorterDuff.Mode.MULTIPLY);
		
		break;
	
	case 3:
		//currentButton.setBackgroundDrawable(defaultButtonbkgd);
		break;
	case 4: // yellow
		currentButton.getBackground().setColorFilter(Color.rgb(238,232,170), PorterDuff.Mode.DST_IN);
		break;
	}
}

}


	
	

