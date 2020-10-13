
package com.sunchip.adw.cloudphotoframe.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Receiver class which shows notifications when the Device Administrator status
 * of the application changes.
 */
public class ScreenOffAdminReceiver extends DeviceAdminReceiver {


	@Override
	public void onEnabled(Context context, Intent intent) {
//		showToast(context,
//				"设备管理器使用");
	}

	@Override
	public void onDisabled(Context context, Intent intent) {
//		showToast(context,
//				"设备管理器没有使用");
	}

}
