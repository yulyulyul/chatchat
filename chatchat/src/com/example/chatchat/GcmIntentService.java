package com.example.chatchat;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService{
	
	public static final String TAG = "ji";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    

	public GcmIntentService() {
		super("GCMIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		  Bundle extras = intent.getExtras();	        
		  GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
	        // The getMessageType() intent parameter must be the intent you received
	        // in your BroadcastReceiver.
	        String messageType = gcm.getMessageType(intent);
	        
	        //Log.d("messageType", messageType+"");
	        //Log.d("extras", extras+
	        
	          if(!extras.isEmpty()) {
	           if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) 
	           {
	               Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
	               
	               // Post notification of received message.
	               
	               if(extras.getString("type") != null)
	               {
	            	   //System.out.println("Type : " + extras.getString("type"));
		               
	            	   String str_type = extras.getString("type");
	       	           int type  = Integer.parseInt(str_type);
	       	           
	       	           switch (type) 
	       	           {
					
	       	           case 1:
						
							sendNotification(intent);
							 Log.i(TAG, "Received: " + extras.toString());
							break;
							
	       	           case 2:
	       	        	   
	       	        	    String user = extras.getString("user");
	       	        	    String MSG = extras.getString("MSG");
	       	        	    String member = extras.getString("member");
		       	        	String created_time = extras.getString("created_time");
		       	        	String time = extras.getString("time");
		       	        	String key = extras.getString("key");
		       	        	String type2 = extras.getString("type");
		       	        	
		       	        	Intent toMain2 = new Intent("com.example.chatchat.sendReceiver.RECEIVE");
			                toMain2.putExtra("user", user);
			                toMain2.putExtra("MSG", MSG);
			                toMain2.putExtra("member", member);
			                toMain2.putExtra("created_time", created_time);
			                toMain2.putExtra("time", time);
			                toMain2.putExtra("key", key);
			                toMain2.putExtra("type", type2);
		   	        	    
			                try
							{
								sendBroadcast(toMain2);
							}
							catch(Exception e)
							{
								//System.out.println("toMain2 전송 실팬 ");
								e.printStackTrace();
							}
			                
			               break;
			               
	       	           case 3:
	       	        	   
	       	        	String user3 = extras.getString("user");
       	        	    String member3 = extras.getString("member");
	       	        	String key3 = extras.getString("key");
	       	        	String url3 = extras.getString("URI");
	       	        	String type3 = extras.getString("type");
	       	        	
	       	        	Intent toMain3 = new Intent("com.example.chatchat.sendReceiver.RECEIVE");
		                toMain3.putExtra("user", user3);
		                toMain3.putExtra("member", member3);
		                toMain3.putExtra("key", key3);
		                toMain3.putExtra("URI", url3);
		                toMain3.putExtra("type", type3);
		   	        	    
			                try
							{
								sendBroadcast(toMain3);
							}
							catch(Exception e)
							{
								//System.out.println("toMain3 전송 실패 ");
								e.printStackTrace();
							}
			                
	       	        	   break;

	       	           default:
						
   	       	        	    break;
					}
	               }
	            }
	        }
	        // Release the wake lock provided by the WakefulBroadcastReceiver.
	        GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	
	  private void sendNotification(Intent intent) {
		  
		  	//System.out.println("In sendNotification");
		  
	        mNotificationManager = (NotificationManager)
	                this.getSystemService(Context.NOTIFICATION_SERVICE);
	        
	        PendingIntent contentIntent;
	        NotificationCompat.Builder mBuilder;
	        
	        Bundle bb = intent.getExtras();

	        String str_type = bb.getString("type");
	        int type  = Integer.parseInt(str_type);
	        
	        switch (type) {
			case 1:
				
				
                contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, Requset_Friends.class),0);
                
                String msg = bb.getString("content");

				
                 mBuilder = new NotificationCompat.Builder(this)
	                .setSmallIcon(R.drawable.title)
	                .setContentTitle("친구요청")
	                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
	                .setContentText(msg)
	                .setAutoCancel(true)
	                .setTicker("친구요청이 들어왔습니다.")
	                .setDefaults(Notification.DEFAULT_VIBRATE)
	                .setWhen(System.currentTimeMillis());
				
                 mBuilder.setContentIntent(contentIntent);
     	        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
     	        
     	       WakeUpScreen.acquire(getApplicationContext(), 10000); //10초후 자동릴리즈
				break;

			default:
				break;
			}
	        
	    }
	  

}
