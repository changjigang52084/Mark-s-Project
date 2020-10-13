package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.lzkj.aidlservice.R;
import com.lzkj.aidlservice.app.CommunicationApp;

public class UnbindReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
//		new UnbindDeviceManager().unbindDevice();
		Toast.makeText(CommunicationApp.get(), R.string.response_unbind, Toast.LENGTH_LONG).show();
	}

}
