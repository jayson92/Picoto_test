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

import android.hardware.SensorEvent;

public class AccelerometerReading {
	private double timeStamp;
	private double x;
	private double y;
	private double z;
	private double magnitude;
	private double integratedMagnitude;
	
	public AccelerometerReading(SensorEvent event) {
		timeStamp = System.currentTimeMillis();
		x = event.values[0];
		y = event.values[1];
		z = event.values[2];
		
		// Geometric mean of accelerometer components.
		magnitude = Math.sqrt(x*x + y*y + z*z);
	}
	
	public double getTimeStamp() {
		return timeStamp;		
	}
	
	public double getMagnitude() {
		return magnitude;
	}
	
	public double getXVal() {
		return x;
	}
	
	public double getYVal() {
		return y;
	}
	
	public double getZVal() {
		return z;
	}
	
	public double getIntegratedMagnitude() {
		return integratedMagnitude;
	}
	
	public void setIntegratedMagnitude(double value) {
		integratedMagnitude = value;
	}
	
	public String getCsvFormattedOutput() {
		return timeStamp + "," + x + "," + y + "," + z + "," + magnitude + "," + integratedMagnitude + "\n";
	}
}
