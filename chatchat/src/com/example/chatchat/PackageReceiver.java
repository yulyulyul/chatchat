package com.example.chatchat;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

public class PackageReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		String action = intent.getAction();
		if(action.equals(Intent.ACTION_PACKAGE_ADDED))
		{
//			System.out.println("앱이 설치 되었을 때.");
//			Toast.makeText(context, "앱이 설치되었을 때", Toast.LENGTH_SHORT).show();
			
		}
		else if(action.equals(Intent.ACTION_PACKAGE_REMOVED))
		{
//			System.out.println("앱이 삭제 되었을 때.");
//			Toast.makeText(context, "앱이 삭제 되었을 때", Toast.LENGTH_SHORT).show();
			
		}
		else if(action.equals(Intent.ACTION_PACKAGE_REPLACED))
		{
//			Toast.makeText(context, "앱이 업데이트 되었을 때", Toast.LENGTH_SHORT).show();
			
			 try
			   {
				   File apk_file = new File(Environment.getExternalStorageDirectory()+"/chatchat.apk");
				   
				   if(apk_file.exists())
				   {
					   apk_file.delete();
				   }
			   }
			   catch(Exception e)
			   {
				   e.printStackTrace();
			   }
			 
			
		}
	}
}
