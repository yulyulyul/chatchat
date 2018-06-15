package com.example.chatchat;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class personalChat extends Activity{
	
	 ActionBar abar;
	 static  String msg = null;
	 static Bitmap bitmap;
	 static String With_friend;
	
	 public boolean go_thread = false;

	 ArrayList<Message> store;
	 Message MSG;
	 Message offical;
	 static ListView lv;
	 Runnable perceiveMessage;
	 Thread th;
	 public static EditText et;
	 int pro_img_size_pixel;
	 
	 final int REQ_ACTION_PICK = 100;
	 
	 public static int targetWidth = -5;
	 public static int targetHeight = -5; // 만약 targetW와 targetH가 정의되지 않았다면!! (자꾸 저 값이 변하여 사진의 크기가 변하기 때문에 여기서 고정 시켜줄 필요가 있을듯!)
	 
	 utils ut = new utils();
	 
	 public static boolean detect_profile_touch = false;
	 
	 public static View pro_pic_touch;
	 public static View chatting_view;
	 public static Button add_friend_btn;
	 public static ImageView add_friend_profile;
	 Intent intent;
	 
	 public  RoundedAvatarDrawable sibalFINAL;
	@Override
	protected void onPause() 
	{
		super.onPause();
		
		//System.out.println("onPause!!!!!!!!!!!!!");
		
		for(int i=0;i<MainActivity.Seq.size();i++)
		{
			//System.out.println("Seq["+i+"]  : " + MainActivity.Seq.get(i));
		}
		
		//System.out.println("seq 실행 끝!");
	}

	@Override
	protected void onStop() 
	{
		super.onStop();
		//System.out.println("onStop!!!!!!!!!!!!!!!");
	}
	
	

	@Override
	protected void onResume() {
		super.onResume();
		
		MainActivity.state = MainActivity.currentKey;
//		//Log.d("STATE", MainActivity.state);
		
	}

	/*
	 * Destory에서 방을 나가는 순간! 여태까지 대화했던 모든 기록이 저장되어야함.
	 * 
	 * 
	 * */
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		//System.out.println("onDestory!!!!!!!!!!!!");
		//System.out.println("방을 나갑니다!!");
		
//		th.interrupt();
		MainActivity.adapter2.notifyDataSetChanged();
		
		go_thread = false;
		System.gc();
		
		//RecycleUtils ru = new RecycleUtils();
		//ru.recursiveRecycle(getWindow().getDecorView());
		
		try
		{
			Long key = Long.parseLong(MainActivity.currentKey);
			
		
				String chat_list = MainActivity.chk_login.getString("chat_list", "[]"); // jsonString 형태이겠쥐.
				//Log.d("personalChat", "chat_list : " + chat_list);
				 
				 JSONArray jarr = new JSONArray();
				 JSONObject  job = new JSONObject();
				 JSONParser jParser = new JSONParser();
					
				 //System.out.println("어뎁터 크기 : "  + MainActivity.adapter2.message.size());
				 
				 if(chat_list.equals("[]"))// 아직 채팅이 존재하지 않았던 경우.
				 {
					 	for(int i=0;i<MainActivity.adapter2.message.size();i++)
						{
							//System.out.println("["+i+"] : " + MainActivity.adapter2.message.get(i).getType());
						}
					 
						job.put("seq", MainActivity.currentKey);
						job.put("f_nick", With_friend);
						
						if(MainActivity.adapter2.message.get(MainActivity.adapter2.message.size()-1).equals("null"))
						{
							job.put("last", "사진");
						}
						else
						{
							job.put("last", MainActivity.adapter2.message.get(MainActivity.adapter2.message.size()-1).getUser_txt());
						}
						job.put("time", MainActivity.adapter2.message.get(MainActivity.adapter2.message.size()-1).getTime());
						
						jarr.add(job);
					
	          		
	          		String list = jarr.toJSONString();
	          		//Log.d("In personalChat", "로컬에 채팅 리스트 저장 확인 : " + list);
	          		MainActivity.editor.putString("chat_list", list);
	          		MainActivity.editor.commit();
	          		
				 }
				 /*
				  * 추가하려는 채팅 리스트에 이미 기존에 있는 리스트와 겹치는지 비교해야함.
				  * */
				 
				 else // 채팅 리스트가 존재한 경우.
				 {
					 try {
						 
						 boolean pass = true;
						 
						 jarr = (JSONArray) jParser.parse(chat_list);
						 
						 //System.out.println("jarr 크기 :"  + jarr.size());
						 for(int i=0;i<jarr.size();i++)
						{
							 JSONObject j = (JSONObject)jarr.get(i);
							 
							 String title = (String)j.get("f_nick");
							 
							 if(title.equals(With_friend))
							 {
								 jarr.remove(i);
								 job.put("seq", MainActivity.currentKey);
								 job.put("f_nick", With_friend);
								 if(MainActivity.adapter2.message.get(MainActivity.adapter2.message.size()-1).equals("null"))
									{
										job.put("last", "사진");
									}
									else
									{
										job.put("last", MainActivity.adapter2.message.get(MainActivity.adapter2.message.size()-1).getUser_txt());
									}
								 job.put("time", MainActivity.adapter2.message.get(MainActivity.adapter2.message.size()-1).getTime());
								 jarr.add(job);
								 
								 String list = jarr.toJSONString();
			              		 //Log.d("In personalChat", "로컬에 채팅 리스트 저장 확인 : " + list);
			              		 MainActivity.editor.putString("chat_list", list);
			              		 MainActivity.editor.commit();
				              		
								 pass = false;
								 break;
							 }
						}
						 
						 if(pass == true)
						 {
							jarr = (JSONArray)jParser.parse(chat_list);
							job.put("seq", MainActivity.currentKey);
							job.put("f_nick", With_friend);
							if(MainActivity.adapter2.message.get(MainActivity.adapter2.message.size()-1).equals("null"))
							{
								job.put("last", "사진");
							}
							else
							{
								job.put("last", MainActivity.adapter2.message.get(MainActivity.adapter2.message.size()-1).getUser_txt());
							}
							job.put("time", MainActivity.adapter2.message.get(MainActivity.adapter2.message.size()-1).getTime());
							jarr.add(job);
							
		              		String list = jarr.toJSONString();
		              		//Log.d("In personalChat", "로컬에 채팅 리스트 저장 확인 : " + list);
		              		MainActivity.editor.putString("chat_list", list);
		              		MainActivity.editor.commit();
						 }
		              		
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
				 }
				 MainActivity.state = "개인 채팅방";
				 MainActivity.store22.clear();
				 MainActivity.adapter2.message.clear();
				///////////////////////////////////////////////
				 
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//Log.d("personalChat, onDestroy()", "Exception!");
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_room); //layout 설정!!
		
		//System.out.println("personalChat 시작!!!!!!!");
		
		
		pro_pic_touch = (View)findViewById(R.id.profile_addfriend);
		chatting_view = (View)findViewById(R.id.chatting);
		add_friend_btn = (Button)findViewById(R.id.add_friend);
		add_friend_profile = (ImageView)findViewById(R.id.profile_IMAGE);
		
		intent = getIntent();
    	String Title = intent.getStringExtra("Title");
    	
    	//System.out.println("Title = "+Title);
    	
		abar = getActionBar();
		abar.setTitle(Title);
		abar.setDisplayShowHomeEnabled(false);
		abar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#20BAEF")));
		
		
		Button btn = (Button)findViewById(R.id.btn);
		et = (EditText)findViewById(R.id.et);
		store = new ArrayList<Message>();
		
		
		lv = (ListView)findViewById(R.id.in);
		
		lv.setDivider(null);
		lv.setAdapter(MainActivity.adapter2);
		
		try
		{
			personalChat.lv.setSelection(MainActivity.adapter2.getCount()-1);
		}
		catch(NullPointerException np)
		{
			//System.out.println("null 발생! ");
			finish();
		}
		pro_img_size_pixel = ut.getDpToPixel(personalChat.this, 35);
		
		//Log.d("In personalChat, pro_img_size_pixel", pro_img_size_pixel+"");
		
		
		if(!et.getText().toString().equals("") || et.getText().toString().length() != 0) // 사용자가 메시지를 보낼때 빈 값인지 체크.
		{
			btn.setEnabled(false);
			//System.out.println("눌리지 않았어!!");
		}
		else
		{
			//System.out.println("눌렸어!!");
			btn.setEnabled(true);
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) 
				{
					if(!et.getText().toString().equals("") || et.getText().toString().length() != 0)
					{
							JSONObject Packet2 = new JSONObject();
							Packet2.put("Command", "MESSAGE2");
							Packet2.put("KEY",MainActivity.currentKey+"");
							Packet2.put("msg", et.getText().toString());
							Packet2.put("friend", With_friend);
							String pac2 = Packet2.toJSONString();
							
							MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
							MainActivity.out.println(pac2); //실험!!!!
							MainActivity.out.flush();
							et.setText("");
					}
				}
			});
		}
			
			Button pic = (Button)findViewById(R.id.pic);
			pic.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Intent pickerIntent = new Intent(Intent.ACTION_PICK);
					pickerIntent.setType("image/*");
					startActivityForResult(pickerIntent, REQ_ACTION_PICK);
				}
			});
			
			
	}
	
	@Override
	public void onBackPressed() {
		
		//System.out.println("back 버튼이 눌렸음.");
		if(detect_profile_touch == true)
		{
			//System.out.println("여기가 실행 될까요?");
			pro_pic_touch.setVisibility(View.INVISIBLE);
			detect_profile_touch = false;
			return;
		}
		
		else
		{
			super.onBackPressed();
		}
		
//		return;
		
	}

	public String getPathFromUri(Uri uri){ // 주어진 uri로 부터 절대 경로를 얻어냄.
		Cursor cursor = getContentResolver().query(uri, null, null, null, null );
		cursor.moveToNext(); 
		String path = cursor.getString( cursor.getColumnIndex( "_data" ) );

		return path;
		}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if(resultCode == RESULT_OK){
	        if(requestCode == REQ_ACTION_PICK){
	            Uri uri = data.getData();
	            
	            final String path = getPathFromUri(uri);

	            //Log.d("getPathFromUri", path);
	            
	            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
	            	new uploadFile(path,"UploadToServer.php","AaAa"+System.nanoTime(),personalChat.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
	            else
	            	new uploadFile(path,"UploadToServer.php","AaAa"+System.nanoTime(), personalChat.this).execute((Void[])null);
	            
	        }
	    }
	     super.onActivityResult(requestCode, resultCode, data);
	};
	
	public Bitmap byteArrayToBitmap( byte[] byteA ) {  // 바이트 어레이를 비트맵으로 변환
	    Bitmap bitmap = BitmapFactory.decodeByteArray( byteA, 0, byteA.length ) ;  
	    return bitmap ;  
	}  
	
	private void personalChat() {
		// TODO Auto-generated method stub
		
	}


	

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.allchat, menu);
//		return true;
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}




