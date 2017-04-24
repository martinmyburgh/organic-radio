package com.solodroid.yourradioapp.activities;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.solodroid.yourradioapp.services.RadiophonyService;

class PhoneCallListener extends PhoneStateListener {

	private boolean start = false;
	private boolean notRunWhenStart = true;

	public void onCallStateChanged(int state, String incomingNumber) {
		if (notRunWhenStart)
			notRunWhenStart = false;
		else {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					try {
						if(start){
							RadiophonyService.PlayExoPlayer();
							start = false;
						}
					}catch (Exception e){
						e.getMessage();
					}
					break;

				case TelephonyManager.CALL_STATE_OFFHOOK:
					try {
						if(RadiophonyService.getInstance().isPlaying()){
							RadiophonyService.StopExoPlayer();
							start = true;
						}
					}catch (Exception e){
						e.getMessage();
					}
					break;

				case TelephonyManager.CALL_STATE_RINGING:
					try {
						if(RadiophonyService.getInstance().isPlaying()){
							RadiophonyService.StopExoPlayer();
							start = true;
						}
					}catch (Exception e){
						e.getMessage();
					}
					break;

				default:
					break;
			}
		}
	}
}