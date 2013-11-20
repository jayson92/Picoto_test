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

import android.graphics.Color;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;

public class Constants {
	public static final String PREFS_NAME = "NeonatalApnoeaDetectorPreferences";	
	public static final int REQUEST_ENABLE_BT = 1;
	public static final long[] VIBRATE_PATTERN = {0, 300, 540}; //ms
	public static final int PULSE_OX_GRACE_PERIOD = 5; //readings
	public static final int SENSOR_EVENT_RATE_MICROS = 80000; //microseconds
	public static final int AUDIO_WAV_SAMPLING_RATE_HZ = 8000; //hz
	public static final int AUDIO_RAW_SAMPLING_RATE_MILLIS = 100; //ms
	public static final float REFERENCE_AMPLITUDE = 0.01f;
	public static final String TEXT_BLUE = "#222277";
	public static final String BACKGROUND_BLUE = "#E1E1F5";
	public static final String SOFTWARE_VERSION = "1.4.0";
	public static final int ALARM_TYPE = 1;
	public static final String ALARM_TEXT = "apnoea";
	public static final long LOCATION_UPDATE_FREQUENCY = 60000; //ms
	public static final LineAndPointFormatter DATASERIES_FORMAT = new LineAndPointFormatter(Color.RED, Color.TRANSPARENT, Color.TRANSPARENT, (PointLabelFormatter) null);
	public static final float 	GRAVITY = 9.82f; //ms^-2
	public static final String 	SMS_SENT = "SMS_SENT";
	public static final int valueTextSize = 40;
	public static final int activityTitleTextSize = 17;
	public static final int buttonTextSize = 17;
	public static final int normalTextSize= 15;	
	// Default user settings.
	public static final boolean DEFAULT_ALARM_ENABLED = true;
	public static final boolean DEFAULT_VIBRATE_ENABLED = true;
	public static final boolean DEFAULT_SMS_ENABLED = false;
	public static final boolean DEFAULT_CALL_ENABLED = false;
	public static final boolean DEFAULT_RECORD_AUDIO = false;
	public static final float 	DEFAULT_DOMAIN_EXTENT = 60;
	public static final float 	DEFAULT_HR_MAX_RANGE = 92;
	public static final float 	DEFAULT_HR_MIN_RANGE = 70;
	public static final float 	DEFAULT_AUD_MAX_RANGE = 0.2f;
	public static final float 	DEFAULT_AUD_MIN_RANGE = 0;
	public static final float 	DEFAULT_O2_MAX_RANGE = 100;
	public static final float 	DEFAULT_O2_MIN_RANGE = 90;
	public static final float 	DEFAULT_ACC_MAX_RANGE = 0.1f;
	public static final float 	DEFAULT_ACC_MIN_RANGE = 0;
	public static final float 	DEFAULT_MAX_DATA_IN_MEMORY = 10000;
	public static final int 	DEFAULT_ALARM_VOLUME = 7;
	public static final String 	DEFAULT_NONIN_ADDRESS = "00:1C:05:00:4F:70";
	public static final String 	DEFAULT_ALARM_SMS_RECIPIENT = "00447531760886";
	public static final String 	DEFAULT_EMERGENCY_CONTACT = "999";	
	public static final int 	DEFAULT_WINDOW_SIZE = 50; //samples
	public static final double DESATURATION_THRESHOLD = 93;
	
	public static enum Status {
    	Green,
    	Amber,
    	Red,
    	Grey
    }	
}
