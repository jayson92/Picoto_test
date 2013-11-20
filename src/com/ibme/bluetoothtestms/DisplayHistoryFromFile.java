package com.ibme.bluetoothtestms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class DisplayHistoryFromFile extends Activity {
	List<XYPlot>plotList= new ArrayList<XYPlot>();
	List<String> dataTitles = new ArrayList<String>();
	File currFile;
	//StringBuilder text2Disp;
	List<TextView> valueDisplayList = new ArrayList<TextView>();
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.displayhistoryfromfile);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    currFile = (File) extras.get("currFile");
		}
		dataTitles.add(0, "Heart Rate (bpm)");
		dataTitles.add(1, "Oxygen Saturation (%)");
		
		String datetimeString = extractDateInfo(currFile.getName()); // currFile should be in Napnoea/PatientID/PulseOx/datetime.txt
		File useless = currFile.getParentFile(); // PulseOx
		String PatientID = useless.getParentFile().getName(); // PatientID
		
		int z= 0;
		LinearLayout mainLL = (LinearLayout) findViewById(R.id.history_mainLL);

		LinearLayout topLayout = new LinearLayout(this);
		topLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		topLayout.setBackgroundColor(Color.rgb(214,229,242)); 
		topLayout.setPadding(8, 10, 5 , 10);
		
		TextView titleTV=  new TextView(this);
			titleTV.setText("Patient: "+ PatientID + "- "+ datetimeString);
			titleTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.activityTitleTextSize);
			titleTV.setTextColor(Color.DKGRAY);
			titleTV.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			titleTV.setGravity(Gravity.LEFT);
			//titleTV.setTypeface(null,Typeface.BOLD);
			topLayout.addView(titleTV);
		mainLL.addView(topLayout);
		
		while (plotList.size()< dataTitles.size()){
			LinearLayout newLayout= setUpRowGraphs(z, new int[] {0, 100}, dataTitles.get(z));
			mainLL.addView(newLayout);
			z++;
			} 
		List<Double> timeOut= readCSVData(currFile, 0);
		if(!timeOut.isEmpty()){
			double minusFirstTime = timeOut.get(0);
			minusFirstTime = -1* minusFirstTime;
			timeOut= addVal2List(timeOut, minusFirstTime);
			List<Double> hrOut= readCSVData(currFile,1);
			List<Double> spo2Out= readCSVData(currFile,2);
			for (int i = 0; i< plotList.size(); i++){
				XYPlot currPlot = plotList.get(i);
				
				List<Double> currentData= new ArrayList<Double>();
				
				
				int[] yBounds = {0, 100};
				switch (i) {
					case 0:
						currentData= hrOut;
						yBounds[0]= 40; yBounds[1]= 120;
						break;
					case 1:
						currentData = spo2Out;
						yBounds[0]= 70; yBounds[1]= 105;
						break;
						}
				
				XYSeries series2Plot = new SimpleXYSeries(timeOut, currentData, dataTitles.get(0));
					LineAndPointFormatter series2PlotFormat = new LineAndPointFormatter(Color.rgb(105, 150, 173), null, 
		   				null, (PointLabelFormatter) null);                                 
				currPlot.addSeries(series2Plot, series2PlotFormat);
				setPlotDets(currPlot, yBounds);
				}
			}
		}
	
	
	Double getAvgList(List<Double> inList){
		double sum= 0;
		int count = 0;
		for (double currVal:inList){
			if(!Double.isNaN(currVal)&& currVal!=0){
				sum +=currVal;
				count++;
			}
		}
		double avg= sum/count;
			DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(avg));
		}
	
	List<Double> addVal2List(List<Double> inList, double val2Add){
		List<Double> outList= new ArrayList<Double>();
		for(int i = 0; i<inList.size(); i++){
			double currentVal= inList.get(i);
			double outVal= (currentVal+val2Add)/1000; 
			outList.add(i, outVal);
			}
		return outList;
		}
	
	List<Double> readCSVData(File csvFile, int index){
		String line;
		List<Double> dataOut = new ArrayList<Double>();
		int n = 0;
		try {
		    BufferedReader br = new BufferedReader(new FileReader(currFile));
		    while ((line = br.readLine()) != null) {
		    	String[] token = line.split(",");
		    	dataOut.add(n, Double.parseDouble(token[index]));
		    	n++;
		    	}
		    } catch (IOException e) {
		    	e.printStackTrace();
		    	}
		return dataOut;
		}
	
	LinearLayout setUpRowGraphs(int i, int[] yBounds, String graphTitle){
		LinearLayout newrowLL= new LinearLayout(this); // encompasses row
			newrowLL.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			newrowLL.setOrientation(LinearLayout.HORIZONTAL);
			newrowLL.setBackgroundResource(R.drawable.metric_border);
			newrowLL.setPadding(10, 3, 10, 10);
			
			LinearLayout subLL_Right = new LinearLayout(this);
				subLL_Right.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT, 1.0f));
				subLL_Right.setOrientation(LinearLayout.VERTICAL);
				subLL_Right.setBackgroundColor(Color.WHITE);
				
				TextView newTV = new TextView(this);
					LinearLayout.LayoutParams tvLP= new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					newTV.setLayoutParams(tvLP);
					newTV.setGravity(Gravity.LEFT);
					newTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,Constants.normalTextSize);
					newTV.setTextColor(Color.rgb(105, 150, 173));
					newTV.setPadding(0, 3, 0, 6);
					newTV.setText(graphTitle);
				
				XYPlot newPlot = new XYPlot(this, "");
					newPlot.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 600));
					setPlotDets(newPlot, yBounds);
					plotList.add(i, newPlot);
				subLL_Right.addView(newTV);
				subLL_Right.addView(newPlot);
				
		newrowLL.addView(subLL_Right);
		return newrowLL;
		}
	 private void setPlotDets(XYPlot currXYPlot, int[] yBounds){

	        Paint paintex1 = currXYPlot.getGraphWidget().getGridBackgroundPaint();
	        paintex1.setColor(Color.WHITE);

	        Widget currgwidget= currXYPlot.getGraphWidget();
	        	SizeMetrics sm = new SizeMetrics(0, SizeLayoutType.FILL, 0 , SizeLayoutType.FILL);
	        currgwidget.setSize(sm);
	        
		 	currXYPlot.setRangeValueFormat(new DecimalFormat("#.0"));
		 	currXYPlot.setDomainValueFormat(new DecimalFormat("#"));
	    	currXYPlot.setTicksPerRangeLabel(1);
	    	currXYPlot.setTicksPerDomainLabel(3);
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
	        currXYPlot.getLegendWidget().setVisible(false);
	    	currXYPlot.setRangeBoundaries(yBounds[0], yBounds[1], BoundaryMode.FIXED);

		}
		public String extractDateInfo(String fileName){
			String dateVal = fileName.substring(4,6) + "/" + fileName.substring(6,8) + "/" + fileName.substring(0, 4);
			String timeVal = fileName.substring(8,10) +":"+ fileName.substring(10,12)+ ":" + fileName.substring(12,14);		
			String str2Return = "Date: " + dateVal + " - Time: " + timeVal;
			return str2Return;

		}
}
