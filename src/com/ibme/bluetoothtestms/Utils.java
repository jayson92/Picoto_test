/**
 * Copyright (c) 2012, Jonathan Daly, Dr Gari Clifford (University of Oxford).
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 * 
 * 	1. 	Redistributions of source code must retain the above copyright notice, this 
 * 		list of conditions and the following disclaimer.
 * 	2.	Redistributions in binary form must reproduce the above copyright notice, 
 * 		this list of conditions and the following disclaimer in the documentation
 * 		and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * NOT MEDICAL SOFTWARE.
 * 
 * This software is provided for informational or research purposes only, and is not
 * for professional medical use, diagnosis, treatment or care, nor is it intended to
 * be a substitute therefor. Always seek the advice of a physician or other qualified
 * health provider properly licensed to practice medicine or general healthcare in
 * your jurisdiction concerning any questions you may have regarding any health
 * problem. Never disregard professional medical advice or delay in seeking it
 * because of something you have observed through the use of this software. Always
 * consult with your physician or other qualified health care provider before
 * embarking on a new treatment, diet or fitness programme.
 * 
 * Graphical charts copyright (c) AndroidPlot (http://androidplot.com/), SVM 
 * component copyright (c) LIBSVM (http://www.csie.ntu.edu.tw/~cjlin/libsvm/) - all 
 * rights reserved.
 * */

package com.ibme.bluetoothtestms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Color;
import android.graphics.Paint;
import android.text.method.LinkMovementMethod;
import android.widget.EditText;
import android.widget.TextView;

import com.androidplot.Plot;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.XLayoutStyle;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.androidplot.xy.YLayoutStyle;

public class Utils {
	private static Paint createPaint(int color, int alpha) {
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setAlpha(alpha);
		return paint;
	}
	
	@SuppressWarnings("deprecation")
	private static void doGenericPlotJobs(XYPlot plot) {
		plot.disableAllMarkup();
		plot.setTicksPerRangeLabel(1);
		plot.getLayoutManager().remove(plot.getLegendWidget());
		plot.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
    	plot.setBorderPaint(null);
    	plot.getGraphWidget().getRangeLabelPaint().setColor(Color.WHITE);
    	plot.getGraphWidget().getDomainLabelPaint().setColor(Color.WHITE);
    	plot.getGraphWidget().getRangeOriginLabelPaint().setColor(Color.WHITE);
    	plot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.WHITE);
    	plot.getGraphWidget().getBackgroundPaint().setAlpha(0);
    	plot.getGraphWidget().setGridBackgroundPaint(Utils.createPaint(Color.WHITE, 255));
	}
    
    public static void ReplotGraph(XYPlot plot, List<Double> data, List<Double> timeStamps, double maxLowerRange, double minUpperRange) {
    	double maxRange = Math.max(Collections.max(data), minUpperRange);
    	double minRange = Math.min(Collections.min(data), maxLowerRange);
    	
    	plot.setRangeBoundaries(minRange, maxRange, BoundaryMode.FIXED);
    	plot.clear();
    	plot.addSeries(new DynamicSeries(data, timeStamps), Constants.DATASERIES_FORMAT);
    	plot.redraw();
	}
    
    public static void setUpDynamicPlot(XYPlot plot) {
    	doGenericPlotJobs(plot);
    	plot.setDomainLabel("");
    	plot.setRangeLabel("");
    	plot.setDomainStep(XYStepMode.INCREMENT_BY_PIXELS, 45);
    	plot.setRangeStep(XYStepMode.INCREMENT_BY_PIXELS, 28);    	
    	plot.setPlotPadding(-20, -10, 1, -13);
    	plot.getGraphWidget().setGridLinePaint(Utils.createPaint(Color.parseColor(Constants.TEXT_BLUE), 255));
    	plot.getBackgroundPaint().setColor(Color.parseColor(Constants.TEXT_BLUE));
    	plot.getBackgroundPaint().setAlpha(255);    	
    }
    
    public static void setUpExpandedPlot(XYPlot plot) {
    	doGenericPlotJobs(plot);
		plot.setDomainLabel("Time (s)");
		plot.setDomainStep(XYStepMode.INCREMENT_BY_PIXELS, 62);
    	plot.setRangeStep(XYStepMode.INCREMENT_BY_PIXELS, 43);		
		plot.setPlotPadding(10, 0, 0, 10);
		plot.position(plot.getRangeLabelWidget(),
    			-5, XLayoutStyle.ABSOLUTE_FROM_LEFT,
    			180, YLayoutStyle.ABSOLUTE_FROM_BOTTOM, 
    			AnchorPosition.LEFT_MIDDLE);
		plot.position(plot.getDomainLabelWidget(),
    			200, XLayoutStyle.ABSOLUTE_FROM_LEFT,
    			-16, YLayoutStyle.ABSOLUTE_FROM_BOTTOM, 
    			AnchorPosition.BOTTOM_MIDDLE);
    	plot.getGraphWidget().setGridLinePaint(Utils.createPaint(Color.BLACK, 255));
    	plot.getBackgroundPaint().setColor(Color.BLACK);
    	plot.getBackgroundPaint().setAlpha(255);
    	plot.getRangeLabelWidget().getLabelPaint().setTextSize(20);
    	plot.getDomainLabelWidget().getLabelPaint().setTextSize(20);
    	plot.getDomainLabelWidget().setWidth(70);
    	plot.getDomainLabelWidget().setHeight(25);
		plot.getGraphWidget().getRangeLabelPaint().setTextSize(14);
		plot.getGraphWidget().getDomainLabelPaint().setTextSize(14);
		plot.getGraphWidget().getDomainOriginLabelPaint().setTextSize(14);
		plot.getGraphWidget().getRangeOriginLabelPaint().setTextSize(14);
	}
    
    public static void setUpHistoricalPlot(XYPlot plot) {
    	doGenericPlotJobs(plot);
		plot.setDomainLabel("");
		plot.setRangeLabel("");
		plot.setDomainStep(XYStepMode.INCREMENT_BY_PIXELS, 71);
		plot.setRangeStep(XYStepMode.INCREMENT_BY_PIXELS, 26);
		plot.setPlotPadding(10, -12, 5, -10);
		plot.position(plot.getRangeLabelWidget(),
    			-3, XLayoutStyle.ABSOLUTE_FROM_LEFT,
    			90, YLayoutStyle.ABSOLUTE_FROM_BOTTOM, 
    			AnchorPosition.LEFT_MIDDLE);
		plot.getGraphWidget().setGridLinePaint(Utils.createPaint(Color.parseColor(Constants.TEXT_BLUE), 255));
		plot.getBackgroundPaint().setColor(Color.parseColor(Constants.TEXT_BLUE));
    	plot.getBackgroundPaint().setAlpha(255);
		plot.getRangeLabelWidget().getLabelPaint().setTextSize(16);
		plot.getGraphWidget().getRangeLabelPaint().setTextSize(14);
		plot.getGraphWidget().getDomainLabelPaint().setTextSize(14);
		plot.getGraphWidget().getDomainOriginLabelPaint().setTextSize(14);
		plot.getGraphWidget().getRangeOriginLabelPaint().setTextSize(14);	
	}
    
    public static List<List<Double>> parseCsvFile(String path, double numberOfSeconds) {
    	String line;    	
    	List<List<Double>> parsedOutput = new ArrayList<List<Double>>();
    	
    	try {    		
    		BufferedReader br = new BufferedReader(new FileReader(path));
    		while ((line = br.readLine()) != null) { 
    			List<Double> currentLineTokens = new ArrayList<Double>();
    			for (String token : line.split(",")) {
    				currentLineTokens.add(Double.parseDouble(token)); 
    			}
    	       
    			parsedOutput.add(currentLineTokens);
    		}    	
    		br.close();
    	} catch (IOException e) {}
    	
    	int size = parsedOutput.size();    	
    	if (size != 0) {    
    		double latestTime = parsedOutput.get(size - 1).get(0);
    		double earliestTime = parsedOutput.get(0).get(0);
    		
    		if (numberOfSeconds == 0 || latestTime - earliestTime < numberOfSeconds * 1000) {
    			return parsedOutput;
    		}
    		else {
    			int lastIndex = size - 1;
    		
    			// While the timestamp on the row is within the time limit, decrement i.
    			while (parsedOutput.get(lastIndex).get(0) > latestTime - (numberOfSeconds * 1000) && lastIndex > 0) {
    				lastIndex--;
    			}
    		
    			// lastIndex + 1 is now the index of the first row which should be returned.    		
    			return parsedOutput.subList(lastIndex, size - 1);
    		}
    	}
    	return null;
    }
    
    public static List<AccelerometerReading> trimDataSeries(List<AccelerometerReading> data, double numberOfSeconds) {
    	int size = data.size();
    	double latestTime = data.get(size - 1).getTimeStamp();
		double earliestTime = data.get(0).getTimeStamp();
		
		if (latestTime - earliestTime < numberOfSeconds * 1000) {
			// The data does not need to be trimmed.
			return data;
		}
		else {
			int i = size - 1;
		
			// While the timestamp on the row is within the time limit, decrement i.
			while (data.get(i).getTimeStamp() > latestTime - (numberOfSeconds * 1000)) {
				i--;
			}
		
			// i + 1 is now the index of the first row which should be returned.    		
			return data.subList(i + 1, size - 1);
		}
    }
    
    public static List<PulseOxReading> trimPulseOxDataSeries(List<PulseOxReading> data, double numberOfSeconds) {
    	int size = data.size();
    	double latestTime = data.get(size - 1).getTimeStamp();
		double earliestTime = data.get(0).getTimeStamp();
		
		if (latestTime - earliestTime < numberOfSeconds * 1000) {
			// The data does not need to be trimmed.
			return data;
		}
		else {
			int i = size - 1;
		
			// While the timestamp on the row is within the time limit, decrement i.
			while (data.get(i).getTimeStamp() > latestTime - (numberOfSeconds * 1000)) {
				i--;
			}
		
			// i + 1 is now the index of the first row which should be returned.    		
			return data.subList(i + 1, size - 1);
		}
	}
    
    public static List<AudioReading> trimAudioDataSeries(List<AudioReading> data, double numberOfSeconds) {
    	int size = data.size();
    	double latestTime = data.get(size - 1).getTimeStamp();
		double earliestTime = data.get(0).getTimeStamp();
		
		if (latestTime - earliestTime < numberOfSeconds * 1000) {
			// The data does not need to be trimmed.
			return data;
		}
		else {
			int i = size - 1;
		
			// While the timestamp on the row is within the time limit, decrement i.
			while (data.get(i).getTimeStamp() > latestTime - (numberOfSeconds * 1000)) {
				i--;
			}
		
			// i is now the index of the first row which should be returned.    		
			return data.subList(i + 1, size - 1);
		}
	}
    
    public static float getUserSettingFloat(EditText userField, float existingValue) {
    	String newSettingValue = userField.getText().toString();
		if (newSettingValue != null && newSettingValue.length() != 0) {
			return Float.parseFloat(newSettingValue);
		}
		return existingValue;
	}
    
    public static String getUserSettingString(EditText userField, String existingValue) {
    	String newSettingValue = userField.getText().toString();
		if (newSettingValue != null && newSettingValue.length() != 0) {
			return newSettingValue;
		}
		return existingValue;
	}

	public static void setStatus(TextView textView, String displayText, int backgroundColour, int textColour) {
		textView.setText(displayText);
		textView.setBackgroundColor(backgroundColour);
		textView.setTextColor(textColour);
	}

	public static void enableHyperlinks(TextView view) {
        view.setMovementMethod(LinkMovementMethod.getInstance());		
	}
}
