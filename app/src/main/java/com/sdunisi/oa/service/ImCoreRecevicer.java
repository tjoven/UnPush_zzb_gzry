package com.sdunisi.oa.service;

import com.sdunisi.oa.im.WakeUpHelper;

import unigo.utility.Log;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

public class ImCoreRecevicer extends BroadcastReceiver
{
	public void onReceive(Context context, Intent intent)
	{
		boolean b = intent.getBooleanExtra("bAlarm", false);
	    try
		{
	    	Log.write(2, "ImRecevicer.onReceive", "message push>>service Receive==","");
			if(!WakeUpHelper.isNetworkAvailable(context))
			{
				Log.write(2, "ImRecevicer.onReceive", "network111111111====="+WakeUpHelper.isNetworkAvailable(context),"");
				WakeUpHelper.acquireStaticLock(context);
				WakeUpHelper.releaseStaticLock(context);
				Log.write(2, "ImRecevicer.onReceive", "network222222222====="+WakeUpHelper.isNetworkAvailable(context),"");
			}
				Intent i = new Intent(context, ImCoreService.class);
				context.startService(i);
		}
		catch (Exception e)
		{
		}
	} 
}
