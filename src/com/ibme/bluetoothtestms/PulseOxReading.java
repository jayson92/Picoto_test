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

public class PulseOxReading {
	private double timeStamp;
	private int hr;
	private int o2;
	private boolean unusableDataFlag;
	private boolean lowBatteryFlag;
	private boolean ootFlag;
	private boolean artefactFlag;
	private boolean lowSigQualFlag;
	private boolean margSigQualFlag;
	private boolean spaFlag;
	
	// Constructor for creation of empty reading.
	public PulseOxReading() {
		timeStamp = System.currentTimeMillis();
		hr = o2 = 0;
		unusableDataFlag = true;
	}
	
	public PulseOxReading(byte[] buffer) {
		timeStamp = System.currentTimeMillis();
		
		/** Buffer input documentation
		 * 
		 * Input consists of 4 bytes transmitted once per second. 
		 * 
		 * Byte 1 - Status
		 * 1 | R | OOT | LPRF | MPRF | ARTF | HR8 | HR7 
		 * 
		 * Byte 2 - Heart Rate
		 * 0 | HR6 | HR5 | HR4 | HR3 | HR2 | HR1 | HR0
		 * 
		 * Byte 3 - SpO2
		 * 0 | SP6 | SP5 | SP4 | SP3 | SP2 | SP1 | SP0 
		 * 
		 * Byte 4 - Status2
		 * 0 | R | SPA | R | SNSA | R | R | LOW BAT
		 * 
		 * Key:
		 * ARTF - Artifact indicated on each pulse
		 * OOT - Out of track, an absence of consecutive good pulse signals
		 * LPRF - Low perfusion, amplitude representation of low/no signal quality
		 * MPRF - Marginal perfusion, amplitude representation of low/marginal signal quality
		 * SNSA - Sensor alarm, device is providing unusable data for analysis (finger removed)
		 * SPA - SmartPoint algorithm, high quality SmartPoint measurement
		 * LOW BAT - low battery
		 * HR8-HR0 - 4-beat pulse rate average, formatted for display
		 * SP6-SP0 - 4-beat SpO2 average, formatted for display
		 * R - reserved for future use
		 * */
		
		byte byte1 = buffer[0];
		byte byte2 = buffer[1];
		byte byte3 = buffer[2];
		byte byte4 = buffer[3];
		
		// HR obtained by combining MSBs and LSBs, which are in different bytes.
		hr = ((byte1 & 0x03) << 7 ) + (byte2 & 0x7F);
		
		// SpO2 obtained from byte 3 (except first bit).
		o2 = byte3 & 0x7F;
		
		unusableDataFlag = (byte4 & 0x08) != 0;
		lowBatteryFlag = (byte4 & 0x01) != 0;
		ootFlag = (byte1 & 0x20) != 0;
		artefactFlag = (byte1 & 0x04) != 0;
		
		lowSigQualFlag = (byte1 & 0x10) != 0;
		margSigQualFlag = (byte1 & 0x08) != 0;
		spaFlag = (byte4 & 0x20) != 0;
		
		// Check to see pulse ox hasn't sent HR=511 or SpO2=127 (Which mean "can't be computed").
		if (hr == 511 || o2 == 127) {
			hr = 0;
			o2 = 0;
			unusableDataFlag = true;
		}
	}

	public double getTimeStamp() {
		return timeStamp;		
	}
	
	public int getHeartRate() {
		return hr;
	}
	
	public int getSpo2() {
		return o2;
	}
	
	public boolean hasUnusableDataFlag() {
		return unusableDataFlag;
	}
	
	public boolean hasLowBatteryFlag() {
		return lowBatteryFlag;
	} 
	
	public boolean hasOotFlag() {
		return ootFlag;
	}
	
	public boolean hasArtefactFlag() {
		return artefactFlag;
	}
	
	public boolean hasLowSignalQualityFlag() {
		return lowSigQualFlag;
	}
	
	public boolean hasMargSignalQualityFlag() {
		return lowSigQualFlag;
	}
	
	public boolean hasSpaFlag() {
		return spaFlag;
	}
	
	public boolean isUnusableData() {
		return unusableDataFlag || ootFlag;
	}
	
	public int getSignalQuality() {
		if (unusableDataFlag) {
			return 0;
		}
		
		// Return 3 if neither flag is set, 2 if marginal flag is set and 1 otherwise.
		if (!lowSigQualFlag && !margSigQualFlag) {
			return 3;
		}
		return lowSigQualFlag ? 1 : 2;
	}
	
	public String getCsvFormattedOutput() {
		return timeStamp + "," + hr + "," + o2 + ","+ getSignalQuality() + "\n";
	}

}
