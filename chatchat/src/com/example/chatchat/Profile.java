package com.example.chatchat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Profile extends Activity {

	
	static profile_adapter adapter;
	final int REQ_ACTION_PICK = 100;
	public static ImageView img;
	EditText msg;
	View vi;
	String messg2;
	int degree;
	int cnt = 1;
    static int pic_w;
	static int pic_h;
	utils ut = new utils();
	 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		System.setProperty("java.awt.headless", "true"); 
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		ListView list = (ListView)findViewById(R.id.profile_list);
		
		 adapter = new profile_adapter();
		 
		list.setAdapter(adapter);
		
		adapter.notifyDataSetChanged();
		
		list.setOnItemClickListener(new OnItemClickListener() { // 프로필 탭에서 리스트뷰를 선택했을 때 !

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				
				int a = adapter.getItemViewType(position);
				
				switch (a) {
				case 0: 
					
//					vi = view;
//					Intent pickerIntent = new Intent(Intent.ACTION_PICK);
//					pickerIntent.setType("image/*");
//					startActivityForResult(pickerIntent, REQ_ACTION_PICK);
					DialogRadio(view);

					
					break;
					
				case 2: 
					vi = view;
					createDialog(view);
					
					break;
					

				default:
					break;
				}
			}
		});
		
		Button logout_btn = (Button)findViewById(R.id.logout_btn);
		
		logout_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) { // 로그 아웃을 하면 입력했던 내용을 모두 초기화 시키고 화면을 전환.
				
			  	SharedPreferences.Editor editor = MainActivity.chk_login.edit();
		        editor.putBoolean("login", false);
		        editor.remove("nickname");
		        editor.remove("state_message");
		        editor.remove("local_profilePATH");
		        editor.clear();
		        editor.commit();
		        
		        MainActivity.f_list_adapter.mainview.clear();
		        MainActivity.Fadapter.mainview.clear();
		        MainActivity.f_chat_adapter.mainview.clear();
		        
		        //System.out.println("삭제하였습니다.");
		      
		        MainActivity.username.setText("");
		        MainActivity.password.setText("");
		        MainActivity.tabhost.setCurrentTab(0);
		        
		        MainActivity.LoginPage.setVisibility(View.VISIBLE);
		        MainActivity.MainPage.setVisibility(View.INVISIBLE);
		        
		        MainActivity.abar.hide();
		        
		        //Log.d("profile.java", "닉네임 : " + MainActivity.MyNickname);
		        new del_regid(MainActivity.MyNickname).start();
		        
			}
		});
		
	}
	
	private void DialogRadio(final View v){
		final CharSequence[] PhoneModels = {"사진 앨범", "기본 이미지"};
		        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		        //alt_bld.setIcon(R.drawable.person);
		        alt_bld.setTitle("사진 선택");
		        alt_bld.setSingleChoiceItems(PhoneModels, -1, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int item) {
		               
		            	switch (item) {
						case 0: 					
							vi = v;
							Intent pickerIntent = new Intent(Intent.ACTION_PICK);
							pickerIntent.setType("image/*");
							startActivityForResult(pickerIntent, REQ_ACTION_PICK);
							
							break;
							
						case 1: // 원래 디폴트 이미지로 바꾼다. => db에 저장된 값을 no로 바꾼다. => 자바 서버에 주소 값을 삭제한다. => 로컬에 저장된 주소를 삭제한다. => 로컬에 저장한 서버주소를 삭제한다.
							ImageView img = (ImageView) v.findViewById(R.id.img);
							img.setImageResource(R.drawable.person);
							
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				            	new update_profile_pathTophp("http://115.71.237.99/chatchat/update_profile_path.php",
				            			MainActivity.MyNickname, "no").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
				            else
				            	new update_profile_pathTophp("http://115.71.237.99/chatchat/update_profile_path.php",
				            			MainActivity.MyNickname, "no").execute((Void[])null);
						
							//db에 저장된 프로필 사진의 path를 no로 수정함.
							//System.out.println("================================================================================");
							
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				            	new update_profile_pathTophp("http://115.71.237.99/chatchat/update_profile_thumbnail_path.php",
				            			MainActivity.MyNickname, "no").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
				            else
				            	new update_profile_pathTophp("http://115.71.237.99/chatchat/update_profile_thumbnail_path.php",
				            			MainActivity.MyNickname, "no").execute((Void[])null);
							
							//db에 저장된 프로필 사진의 thumbnail path를 no로 수정함.
							
							JSONObject packet = new JSONObject(); 
				    		packet.put("Command", "delete_profile_pic_path");
				    		packet.put("nickname", MainActivity.MyNickname);
				    		String pac = packet.toJSONString();
				    		
				    		//System.out.println("pac : " + pac);

				    		MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
				    		MainActivity.out.println(pac);
				    		MainActivity.out.flush();

				    		//System.out.println("서버로 이미지 주소와 이미지 썸네일 주소를 삭제하라고 패킷을 날림.");
				    		
				    		SharedPreferences.Editor editor = MainActivity.chk_login.edit();
					        editor.remove("local_profilePATH");
					        editor.remove("Profile_Picture");
					        editor.remove("profile_thumbnail");
					        editor.commit();
							
					        //System.out.println("로컬에 저장된 프로필 관련 path들을 삭제.");
					        
					        break;

						default:
							break;
						}
		            	
		                 dialog.cancel();
		            }
		        });
		        AlertDialog alert = alt_bld.create();
		        alert.show();
		}
	
	private void createDialog(View vi) {
		LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

		View layout = inflater.inflate(R.layout.activity_profile_state_message_dialog,(ViewGroup) findViewById(R.id.linear1));

		final TextView st_msg = (TextView)vi.findViewById(R.id.state_message);
		
//		
		AlertDialog.Builder aDialog = new AlertDialog.Builder(Profile.this);
		aDialog.setTitle("상태 메시지");
		aDialog.setView(layout);
		aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				
			}

		});
		
		aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {				
			public void onClick(DialogInterface dialog, int which) {
				
				if(msg.getText().toString().length()>20)
				{
					Toast.makeText(getApplicationContext(), "상태 메시지를 20자 이하로 설정해주세요.", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "상태 메시지를 설정하셨습니다.", Toast.LENGTH_SHORT).show();
					
					final String messg = msg.getText().toString();
					st_msg.setText(messg);
					
					messg2 = messg;
					
					if(messg2.equals(""))
					{
						messg2 = null;
					}
					
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					            	new set_State_MessageTophp("http://115.71.237.99/chatchat/setStateMessage.php",MainActivity.MyNickname,messg2,Profile.this ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
					            else
					            	new set_State_MessageTophp("http://115.71.237.99/chatchat/setStateMessage.php",MainActivity.MyNickname,messg2,Profile.this ).execute((Void[])null);
		
						}
					});
				}
		}

	});
		AlertDialog ad = aDialog.create();
		ad.show();
		
		msg = (EditText)ad.findViewById(R.id.state_message_dialog);
		msg.setText(st_msg.getText().toString());
		msg.setTextColor(Color.BLACK);
		
		
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

	            degree = ut.GetExifOrientation(path);
	            //Log.d("degree", degree+"");
	            
	            ImageView imgvi = (ImageView)vi.findViewById(R.id.img);
				
				// Get the dimensions of the bitmap
				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
				bmOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(path, bmOptions);
				int photoW = bmOptions.outWidth;
				int photoH = bmOptions.outHeight;
				
				//Log.d("photoW", photoW+"");
				//Log.d("photoH", photoH+"");
				
				
				
				// Determine how much to scale down the image
				int scaleFactor;
				
				if(photoW * photoH <1000*1000)
				{
					scaleFactor = 0;
				}
				else
				{
					scaleFactor = Math.min(photoW/pic_w, photoH/pic_h);
					
				}
				//Log.d("scaleFactor", scaleFactor+"");
				
				// Decode the image file into a Bitmap sized to fill the View
				bmOptions.inJustDecodeBounds = false;
				bmOptions.inSampleSize = scaleFactor;
				bmOptions.inPurgeable = true;
				  
				Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
				
				int W = bmOptions.outWidth;
				int H = bmOptions.outHeight;
				
				//Log.d("W", W+"");
				//Log.d("H", H+"");
				
//				int viewHeight = targetH;
//				float width = bitmap.getWidth();
//				float height = bitmap.getHeight();
//				
//				//Log.d("width", width+"");
//				//Log.d("height", height+"");
//				 
//				// Calculate image's size by maintain the image's aspect ratio
//				if(height > viewHeight)
//				{
//				    float percente = (float)(height / 100);
//				    float scale = (float)(viewHeight / percente);
//				    width *= (scale / 100);
//				    height *= (scale / 100);
//				}
//				 
//				//Log.d("최종 width", width+"");
//				//Log.d("최종 height", height+"");
//				if(width>height)
//				{
//					float tmp = 0;
//					tmp = width;
//					width = height;
//					height = tmp;
//				}
				
				// Resizing image
				Bitmap sizingBmp = Bitmap.createScaledBitmap(bitmap, (int) pic_w, (int) pic_h, true);
				RoundedAvatarDrawable tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(sizingBmp);
				

				imgvi.setRotation(degree);
				//Log.d("degree", degree+"");
//				imgvi.setImageAlpha(100);
				imgvi.setImageDrawable(tmpRoundedAvatarDrawable);
				
//				String pro = MainActivity.MyNickname+"_";
//				//System.out.println("pro : " + pro);
				
				String profile_File_name = MainActivity.chk_login.getString("profile_File_name", "no");
				//Log.d("profile_File_name", profile_File_name+"");
				
				
				StringBuilder uploadName = new StringBuilder();
				uploadName.append("pro").append(System.nanoTime()+""); // 서버로 업로드할 프로필 사진 이름 설정.
				
				MainActivity.editor.putString("profile_File_name", uploadName.toString());
				MainActivity.editor.commit();
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				{
	            	new uploadProfile(path,"hangle2.php", uploadName.toString(), Profile.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
				}
	            else
	            {
	            	new uploadProfile(path,"hangle2.php", uploadName.toString(), Profile.this).execute((Void[])null);
	            }
				
	            new del_privious_profile_pic(profile_File_name).start();
				
				
				
//				imgvi.setAlpha(0);
//				imgvi.setBackground(tmpRoundedAvatarDrawable);
				
	        }
	    }
	     super.onActivityResult(requestCode, resultCode, data);
	};
	
	public void onWindowFocusChanged(boolean hasFocus, View view) {
        
        ImageView profileIMG = (ImageView) view.findViewById(R.id.img);

        Log.w("Layout Width - ", String.valueOf(profileIMG.getWidth()));
        Log.w("Layout Height - ", String.valueOf(profileIMG.getHeight()));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//System.out.println("onResume");
		adapter.notifyDataSetChanged();
		MainActivity.abar.setTitle("프로필");
		
		MainActivity.state = "profile";
		//Log.d("STATE", MainActivity.state);
		
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

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
	
	
//	public static Bitmap setRoundCorner(Bitmap bitmap, int pixel) {
//	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), 
//	                            Config.ARGB_8888);
//	    Canvas canvas = new Canvas(output);
//	         
//	    int color = 0xff424242;
//	    Paint paint = new Paint();
//	    Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//	    RectF rectF = new RectF(rect);
//	         
//	    paint.setAntiAlias(true);
//	    paint.setColor(color);
//	    canvas.drawARGB(0, 0, 0, 0);
//	    canvas.drawRoundRect(rectF, pixel, pixel, paint);
//	         
//	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//	    canvas.drawBitmap(bitmap, rect, rect, paint);
//	         
//	    return output;
//	}
	
	// 이미지의 모서리를 둥그렇게 처리하는 부분! 하지만 위의 코드에서는 다른 코드를 써서 사용하지는 않음.
	
	class del_privious_profile_pic extends Thread
	{
		String privious_file;
		HttpURLConnection conn;
		URL url;
		BufferedWriter bw;
		BufferedReader br;
		
		public del_privious_profile_pic(String _privious_file) 
		{
			this.privious_file = _privious_file;
		}
		
		 @Override
		public void run() {
			super.run();
			
			try{
				url = new URL("http://115.71.237.99/chatchat/del_privious_profile_pic.php");
				
				try {
					conn = (HttpURLConnection)url.openConnection();
					conn.setConnectTimeout(2000);
					conn.setReadTimeout(2000);
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Cache-Control", "no-cache");
					conn.setDoInput(true);
					conn.setDoOutput(true);
					
					bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
					String request="request="+privious_file;
					String temp = request.trim();
					bw.write(temp);
					bw.flush();
					
					String response = null;
					
					int responseCode = conn.getResponseCode();
					if(responseCode == HttpURLConnection.HTTP_OK)
					{ //만약 HTTP_OK 신호가 들어온다면! (200)

						br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8")); // EUC-KR 로 인코딩하여 받아옴. 
						StringBuilder sb = new StringBuilder();
						String line;
						
						while((line=br.readLine()) != null){
							sb.append(line);
						}
						
						br.close();
						
						response=sb.toString();
						
						//System.out.println("이전 프로필 삭제 : " + response);
					}
					
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				
			}
			catch (MalformedURLException e) 
			{
				e.printStackTrace();
			}
			
		}
	}
	
	
	class profile_adapter extends BaseAdapter
	{
		private LayoutInflater m_inflater = null;
		ArrayList<Integer> profile_view = new ArrayList<Integer>(); 
		
		public profile_adapter()
		{
			for(int i=0;i<4;i++)
			{
				profile_view.add(i);
			}
			m_inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
		}
		
		@Override
		public int getItemViewType(int position) 
		{
			return profile_view.get(position);
		}


		@Override
		public int getViewTypeCount() 
		{
			return 4;
		}


		@Override
		public int getCount() 
		{
			return profile_view.size();
		}

		public View getView(int position, View convertView, ViewGroup parent) 
		{
			View view=null;
			int type = getItemViewType(position);
			
			if(convertView == null)
			{
				switch(type)
				{
				case 0:
					view = m_inflater.inflate(R.layout.activity_profile_modify_pic, null);
					break;
				case 1:
					view = m_inflater.inflate(R.layout.activity_profile_nickname, null);
					break;
				case 2:
					view = m_inflater.inflate(R.layout.activity_profile__state_message, null);
					break;
				case 3:
					view = m_inflater.inflate(R.layout.activity_profile_age, null);
					break;
				}
			}
			else
			{
				view = convertView;
			}
			
				if(type == 0)
				{
                      final ImageView profileIMG = (ImageView) view.findViewById(R.id.img);
                      
                      
                      if(profileIMG != null)
                      {
//                    	  String nick = MainActivity.chk_login.getString("Profile_Picture", "no");
//                    	  if(nick.equals("no"))
//                    	  {
//                    	  }
//                    	  else
//                    	  {
////                    		  profileIMG.setText("nickname을 불러오는대 실패하였습니다.");
//                    	  }
                    	  
//                    	  String local_profile_path = MainActivity.chk_login.getString("", "not_exit");
                    	  
                    	  
                    	  String profile_path = MainActivity.chk_login.getString("local_profilePATH", "not_exit");
                    	  String server_profile_pic_path = MainActivity.chk_login.getString("Profile_Picture", "not_exit");
                    	  
				          	if(profile_path.equals("not_exit"))
				          	{
				          		//System.out.println("In profile, 로걸의 사진 경로가 존재 안함. not_exit");
				          		//서버로 부터 파일을 다운로드.=> 로컬에 사진을 저장. => 경로를 저장.
				          		//만약 로컬에도 프로필사진의 경로가 저장되지 않고 db에도 프로필 사진의 경로가 저장되있지 않는다면
				          		// 사용자가 아직 프로필 사진을 설정을 안한것.! 
				          		// default 값으로 지정해야함.
				          		
				          		if(server_profile_pic_path.equals("no") || server_profile_pic_path.equals("not_exit"))
				          		{
					          		//System.out.println("로컬에도 파일의 경로가 존재하지 않고 db에도 존재하지 않음.");
					          		Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.person);
					          		profileIMG.setImageBitmap(bm);	
				          		}
				          		
				          		else
				          		{
				          			//System.out.println("로컬에는 존재하지는 않지만 db에는 존재함.");
				          			
				          			final String str = server_profile_pic_path.substring(0, 21).concat(server_profile_pic_path.substring(22,server_profile_pic_path.length()));
				          			 
				          			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
							            	new download_Profile_Image(str, MainActivity.MyNickname).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
							            else
							            	new download_Profile_Image(str, MainActivity.MyNickname).execute((Void[])null);
				          			
				          			 String Re_profile_path = MainActivity.chk_login.getString("local_profilePATH", "not_exit");
				          			 
				          			 if(!Re_profile_path.equals("not_exit"))
				          			 {
				          				 //System.out.println("다운 받은 이미지를 다시 화면으로 표출하기 위하여 원형으로 깎기 전 과정.");
				          				 
				          				Bitmap bitmap = BitmapFactory.decodeFile(Re_profile_path); // 로컬에 저장된 경로의 파일로부터 비트맵을 만든다.
						          		
						          		
						          		Bitmap sizingBmp = Bitmap.createScaledBitmap(bitmap, (int) pic_w, (int) pic_h, true);
										RoundedAvatarDrawable tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(sizingBmp);

										
										profileIMG.setImageDrawable(tmpRoundedAvatarDrawable);
				          			 }
				          			 else
				          			 {
				          				//System.out.println("노답");
				          			 }
				          			 
				          			 
				          		}
				          		
				          		
				          		
				          	}
				          	else // 로컬에 사용자의 프로필 사진 파일이 존재함.
				          	{
				          		//System.out.println("In profile, 로컬의 사진 경로가 존재함. ");
				          		
				          		//System.out.println("profile_path"+profile_path);
//				          		Log.d("로컬에 저장된 파일 절대경로.", profile_path+"");
//				          		Log.d("Profile.java", "로컬에 사용자의 프로필 사진 파일이 존재함.");
				          		Bitmap bitmap = BitmapFactory.decodeFile(profile_path); // 로컬에 저장된 경로의 파일로부터 비트맵을 만든다.
				          		Bitmap sizingBmp = Bitmap.createScaledBitmap(bitmap, (int) pic_w, (int) pic_h, true);
								RoundedAvatarDrawable tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(sizingBmp);
								
								profileIMG.setRotation(0);
								profileIMG.setImageDrawable(tmpRoundedAvatarDrawable);
				          	}
                      }
				}
				
				else if(type == 1)
				{
                   TextView nickname = (TextView) view.findViewById(R.id.nickname);
                    
                    if(nickname != null)
                    {
                  	  String nick = MainActivity.chk_login.getString("nickname", "fail");
                  	  if(nick.equals("fail") == false)
                  	  {
                  		  nickname.setText(nick);
                  	  }
                  	  else
                  	  {
                         	  nickname.setText("nickname을 불러오는대 실패하였습니다.");
                  	  }
                    }

				}
				else if(type == 2)
				{
					TextView state_message = (TextView) view.findViewById(R.id.state_message);
					 String st_msg = MainActivity.chk_login.getString("state_message", "no");
					 
                     if(state_message != null)
                     {
                   	  if(st_msg.equals("no") == false)
                   	  {
                   		state_message.setText(st_msg);
                   	  }
                   	  else
                   	  {
                   		state_message.setText("");
                   		state_message.setHint("상태 메시지를 설정해주세요.");
                   	  }
                     }
				}				
				else if(type == 3)
				{
					TextView age = (TextView) view.findViewById(R.id.age);
                      
					 String AGE = MainActivity.chk_login.getString("age", "fail");
					 
                      if(age != null)
                      {
                    	  if(AGE.equals("fail") == false)
                    	  {
                    		  age.setText(AGE);
                    	  }
                    	  else
                    	  {
                           	  age.setText("나이를 불러오는대 실패하였습니다.");
                    	  }
                      }
				}
			return view;
		}
		
		
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	
	class del_regid extends Thread
	{
		String nick;
		HttpURLConnection conn;
		URL url;
		BufferedWriter bw;
		BufferedReader br;
		
		public del_regid(String _nick) 
		{
			this.nick = _nick;
		}
		
		 @Override
		public void run() {
			super.run();
			
			try{
				url = new URL("http://115.71.237.99/chatchat/del_regid.php");
				
				try {
					conn = (HttpURLConnection)url.openConnection();
					conn.setConnectTimeout(2000);
					conn.setReadTimeout(2000);
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Cache-Control", "no-cache");
					conn.setDoInput(true);
					conn.setDoOutput(true);
					
					bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
					String request="request="+nick;
					
					String temp = request.trim();
					bw.write(temp);
					bw.flush();
					
					String response = null;
					
					int responseCode = conn.getResponseCode();
					if(responseCode == HttpURLConnection.HTTP_OK)
					{ //만약 HTTP_OK 신호가 들어온다면! (200)

						br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8")); // EUC-KR 로 인코딩하여 받아옴. 
						StringBuilder sb = new StringBuilder();
						String line;
						
						while((line=br.readLine()) != null)
						{
							sb.append(line);
						}
						
						br.close();
						
						response=sb.toString();
						//System.out.println("del_regid : 삭제 결과 : " + response);
						
					}
					
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				
			}
			catch (MalformedURLException e) 
			{
				e.printStackTrace();
			}
			
		}
	}
}
