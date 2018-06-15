package com.example.chatchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Header extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_header);
		
		Intent intent = getIntent();
		String f_name = intent.getExtras().getString("f_name");
		
		Intent intent2 = new Intent("com.example.chatchat.sendReceiver.RECEIVE2");
		intent2.putExtra("MSG", "receive!");
		intent2.putExtra("f_name", f_name);
		
		sendBroadcast(intent2);
		
		finish(); 
		
		
	}
	
}
