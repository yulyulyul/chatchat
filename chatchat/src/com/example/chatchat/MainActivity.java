package com.example.chatchat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.chatchat.MessageBroadcastReceiver.CircleTransform;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.picasso.Picasso;


public class MainActivity extends TabActivity {
	
	static ArrayList<FriendView> fv;
	static friend_Adapter Fadapter;
	static ArrayList<friends_list> f_list;
	static friend_Adapter2 f_list_adapter;
	static ArrayList<FriendView3> f_chat_al;
	static friend_Adapter3 f_chat_adapter; 
	static String state;
	static ArrayList<Message> store2;
	static ArrayList<Message> store22;
	static dataAdapter adapter;
	static dataAdapter2 adapter2;
	 
	public static Context Maincontext;
	
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	// SharedPreferences에 저장할 때 key 값으로 사용됨.
    public static final String PROPERTY_REG_ID = "registration_id";

    // SharedPreferences에 저장할 때 key 값으로 사용됨.
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String TAG = "ICELANCER";

    String SENDER_ID = "287040294825";

    GoogleCloudMessaging gcm;
    SharedPreferences prefs;
    Context context;
    public static String regid;
    
    public static boolean detect_f_profile_touch = false;
    
	public static TabHost tabhost;
    public static ActionBar abar;
    
    static String currentKey =  null;
    static boolean onlyOneExecute = true;
    
    private String strMsg = ""; 
	private String read = "";
	private String html = "";
	public static Socket socket;                                                                                                                                                                                                                                                                                                                                      
	public static BufferedReader networkReader;
	public static BufferedWriter networkWriter;
	public static PrintWriter out;
	private String ip = "115.71.237.99";
	private int port  = 9765;
	String roomT;
	String tmp;
	Message mess;
	Message offical;
	static boolean pass;
	static boolean pleaseWait = true;
	
	static ArrayList<JSONObject> Seq = new ArrayList<JSONObject>();
	
	public static String MyIp;
	public static String MyNickname;
	public static String MySeq;
	
	 static View LoginPage, MainPage;
	 public static EditText  username=null;
	 public static EditText  password=null;
     private TextView attempts;
     private Button login;
     private Button btn_mem;
     
     public static SharedPreferences chk_login; // 로그인 부분을 담당함.
     public static SharedPreferences save_request_friends;// 새로 친구 요청이 들어오면 그 부분을 담당함.
     
     public static HashMap<String, Bitmap> NICK_and_BITMAP = new HashMap<String, Bitmap>(); 
     String bb;
     String server_profile_pic_path;
     
     public int sibalFInal;
     public String sibalFInal_str;
     public String jarrStr;
     
     public SharedPreferences.Editor save_fri_req_editor;
     public static SharedPreferences.Editor editor;
     
     String ex_storage;
     utils ut;
     
     String req_nick;
     String req_path;
     
     public static TextView req_f_count;
     public static Handler handler;
     public static MenuItem friend_recommend_icon;
     public static DBManager dbManager;
     public  RoundedAvatarDrawable sibalFINAL;
     
     public long startTime = 0;
     public long endTime = 0;
     
     private PackageReceiver mPackageReceiver;

	@Override
	protected void onPause() {
		super.onPause();
		
		//System.out.println("In MainActivity onPause!!!!!!!!!!!!!");
		
		//System.out.println("Seq : " +  Seq);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try
		{
			//Log.d("DMSG", "Socket close....ok");
			
			JSONObject Packet = new JSONObject();
			Packet.put("Command", "OutApp");
			
			//Log.d("onDestoy : currentKey : " , currentKey);
			
			if(currentKey == null)
			{
				Packet.put("KEY","not_exit");
			}
			else
			{
				Packet.put("KEY", currentKey+"");
			}
		
			
			String pac = Packet.toJSONString();
			
			out = new PrintWriter(networkWriter, true);
			out.println(pac);
			out.flush();
			
			out.close();
			networkWriter.close();
			networkReader.close();
			socket.close();
			//System.out.println("out.close() // socket.close()");
			
			android.os.Process.killProcess(android.os.Process.myPid());
			
		}
		catch(IOException e)
		{
			//Log.d("DMSG", "Socket close fail..");
		} 
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		checkPlayServices();
		 
        if(chk_login.getBoolean("login", false) == true)
		{
        	completelogin();
		}
        
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mPackageReceiver = new PackageReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
		intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		intentFilter.addDataScheme("package");
		registerReceiver(mPackageReceiver, intentFilter);
		
		 context = getApplicationContext();
		 Maincontext = MainActivity.this;

	        if (checkPlayServices()) {
	            gcm = GoogleCloudMessaging.getInstance(this);
	            regid = getRegistrationId(context);

	            if (regid.isEmpty()) {
	                registerInBackground();
	            }
	            
	        } else {
	            Log.i(TAG, "No valid Google Play Services APK found.");
	        }
	        
		//////////////////////////////////////////////////////////////////////////////////////////////////
		
		chk_login = getSharedPreferences("LoginCheck", MODE_PRIVATE);
		editor = MainActivity.chk_login.edit();
		
		///////////////////////////////////////////////////////////////////////////////////////// 로그인 파트
		
		save_request_friends = getSharedPreferences("save_req_friends", MODE_PRIVATE);
		save_fri_req_editor = MainActivity.save_request_friends.edit();
		
		ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath(); // 주소.
		ut = new utils();
		
		
		f_list = new ArrayList<friends_list>();
		f_list_adapter = new friend_Adapter2(f_list);
		
		f_chat_al = new ArrayList<FriendView3>();
		f_chat_adapter = new friend_Adapter3(f_chat_al);
		
		store2 = new ArrayList<Message>();
		store22 = new ArrayList<Message>();
		
		adapter = new dataAdapter(store2);
		adapter2 = new dataAdapter2(store22);
		
		
		LoginPage = findViewById(R.id.Login);
		MainPage = findViewById(R.id.MainPage);
		
		username = (EditText)findViewById(R.id.editText1);
        password = (EditText)findViewById(R.id.editText2);
        login = (Button)findViewById(R.id.button1);
        btn_mem = (Button)findViewById(R.id.btn_mem);
        
        btn_mem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(MainActivity.this, Membership.class);
				startActivity(intent);
			}
		}); 
        
        
        
		//////////////////////////////////////////////////////////////////////////////////////////
		abar = getActionBar();
        abar.hide();
		abar.setDisplayShowHomeEnabled(false);
        tabhost = getTabHost();
        
        abar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#20BAEF")));
		 
        TabSpec firstTabSpec = tabhost.newTabSpec("전체 대화방");
        TabSpec SecondTabSpec = tabhost.newTabSpec("친구요청");
        TabSpec ThirdTabSpec = tabhost.newTabSpec("친구");
        TabSpec ForthTabSpec = tabhost.newTabSpec("프로필");
       
        LayoutInflater vi1 = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
//        View View_1 = (View)vi1.inflate(R.layout.tab_row_item1, null);
//        RelativeLayout Layout_1 = (RelativeLayout)View_1.findViewById(R.id.RelativeLayout1);
//        Layout_1.setBackgroundResource(R.drawable.tab_1_bg);
//        View View_2 = (View)vi1.inflate(R.layout.tab_row_item2, null);
//        RelativeLayout Layout_2 = (RelativeLayout)View_2.findViewById(R.id.RelativeLayout1);
//        Layout_2.setBackgroundResource(R.drawable.tab_2_bg);
        
        View View_3 = (View)vi1.inflate(R.layout.tab_row_item3, null);
//        RelativeLayout Layout_3 = (RelativeLayout)View_3.findViewById(R.id.RelativeLayout1);
//        Layout_3.setBackgroundResource(R.drawable.tab_3_bg);
        req_f_count = (TextView)View_3.findViewById(R.id.count);
        
//        View View_4 = (View)vi1.inflate(R.layout.tab_row_item4, null);
//        RelativeLayout Layout_4 = (RelativeLayout)View_4.findViewById(R.id.RelativeLayout1);
//        Layout_4.setBackgroundResource(R.drawable.tab_4_bg);
       
        
        firstTabSpec.setIndicator("",getResources().getDrawable(R.drawable.tab_1_bg)).setContent(new Intent(this,ChatRoom.class));
        SecondTabSpec.setIndicator("",getResources().getDrawable(R.drawable.tab_3_bg)).setContent(new Intent(this,F_chat.class));
        ThirdTabSpec.setIndicator("",getResources().getDrawable(R.drawable.tab_2_bg)).setContent(new Intent(this,Friend.class));
        ForthTabSpec.setIndicator("",getResources().getDrawable(R.drawable.tab_4_bg)).setContent(new Intent(this,Profile.class));
        
        tabhost.addTab(firstTabSpec);
        tabhost.addTab(ThirdTabSpec);
        tabhost.addTab(SecondTabSpec);
        tabhost.addTab(ForthTabSpec);
        
        dbManager = new DBManager(getApplicationContext(), "chat.db", null, 1);
        
        if(isNetWork()==false)
        {
        	
        	AlertDialog.Builder aDialog = new AlertDialog.Builder(this);

    		aDialog.setTitle("네트워크 상태");
    		aDialog.setMessage("네트워크에 연결이 되어있지 않습니다. 연결 상태를 확인해주세요.");
    		aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {				
    			public void onClick(DialogInterface dialog, int which) 
    			{
    	        	System.exit(0);
    			}
    						
    		});

    		final AlertDialog ad = aDialog.create();
    		ad.show();
    		
        	Toast.makeText(this, "네트워크에 연결이 되어있지 않습니다. \n 연결 상태를 확인해주세요.", Toast.LENGTH_LONG).show();
        }
        else
        {
        	chk_versionCode();
        	clear_chat_log();
        }
        
//        chk_versionCode();
        new chatTask().execute();
        
        if(chk_login.getBoolean("login", false) == true)
		{
        	abar.show();
        	completelogin();
        	
    		SharedPreferences go_chatroom = getSharedPreferences("go_chatroom", MODE_PRIVATE);
    		String gogo = go_chatroom.getString("name", "no");
    		
    		 if(!gogo.equals("no"))
				 {
					Intent intent = new Intent(MainActivity.this, Header.class);
					intent.putExtra("f_name", gogo);
					startActivity(intent);
					
					SharedPreferences.Editor editor = go_chatroom.edit();
					editor.putString("name", "no");
					editor.commit();
				}
    		
		}
        
        fv = new ArrayList<FriendView>();
        Fadapter = new friend_Adapter(fv);
        
        handler = new Handler()
        {
			@Override
			public void handleMessage(android.os.Message msg) 
			{
				super.handleMessage(msg);
				
				if(msg.what == 1)
				{
					 req_f_count.setText(1+"");
					 req_f_count.setVisibility(View.VISIBLE);
					 Fadapter.notifyDataSetChanged();
				}
				else if(msg.what == 2)
				{
					Toast.makeText(MainActivity.this, "서버가 닫혀있습니다.", Toast.LENGTH_LONG).show();
				}
				else if(msg.what == 3)
				{
					finish();
				}
			}
        };
        
        new Thread(new Runnable() {
			@Override
			public void run() {
				
				while(true)
				{
					try 
					{
						Thread.sleep(5000);
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
					
					try
					{
						if(isNetWork())
						{
							if(!MainActivity.socket.isClosed())
							{
//								//System.out.println("MainActivity.socket이 연결되어있습니다.");
								JSONObject Packet2 = new JSONObject();
								Packet2.put("Command", "check_network");
								String pac2 = Packet2.toJSONString();
								
								MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
								MainActivity.out.println(pac2); 
								MainActivity.out.flush();
							}
							else
							{
								//System.out.println("MainActivity.socket이 연결되어있지 않습니다.");
//								setSocket(ip, port);
								new chatTask().execute();
							}
						}
						else
						{
							
							runOnUiThread(new Runnable() {
								
								  public void run() 
								  {
									  
									  if(onlyOneExecute == true)
									  {
 										    onlyOneExecute = false;
											AlertDialog.Builder aDialog = new AlertDialog.Builder(getCurrentActivity());
											 
	  							    		aDialog.setTitle("네트워크 상태");
	  							    		aDialog.setMessage("네트워크에 연결이 되어있지 않습니다. 연결 상태를 확인해주세요.");
	  							    		aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {				
  							    			
  							    			public void onClick(DialogInterface dialog, int which) 
  							    			{
  							    	        	System.exit(0);
  							    			}
  							    		});
  
  							    		final AlertDialog ad = aDialog.create();
  							    		ad.show();
									  }
									  
									  Toast.makeText(MainActivity.this, "네트워크에 연결되어 있지 않습니다.", Toast.LENGTH_LONG).show();
								  }
								});
							
						}						
					}
					catch(NullPointerException ne)
					{
						handler.sendEmptyMessage(2);
					}
				}
			}
		}).start();;
		
//		Intent intent = new Intent();
//		if(intent.getAction() != null)
//		{
//			//Log.d("MAINACTIVITY, INTENT", "intent는 null이 아닙니다.");
//		}
//		else
//		{
//			//Log.d("MAINACTIVITY, INTENT", "intent는 null 입니다.");
//		}
//		SharedPreferences go_chatroom = getSharedPreferences("go_chatroom", MODE_PRIVATE);
//		String gogo = go_chatroom.getString("name", "no");
//		if(!gogo.equals("no"))
//		{
//			//Log.d("친구 이름!", gogo);
//			
//			Intent intent = new Intent(MainActivity.this, Header.class);
//			intent.putExtra("f_name", gogo);
//			startActivity(intent);
//			
//			SharedPreferences.Editor editor = go_chatroom.edit();
//			editor.putString("name", "no");
//			editor.commit();
//			
//		}
//		else
//		{
//			//Log.d("없어", "없어");
//		}		
	}
	
	private void chk_versionCode()
	{

		int versionCode;
        try
        {
            PackageInfo pi= context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
            Log.d("versionCode",versionCode+"");
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
    			new getVersionCode("http://115.71.237.99/chatchat/getVersionCode.php",MainActivity.this, versionCode+"").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
            else
            	new getVersionCode("http://115.71.237.99/chatchat/getVersionCode.php",MainActivity.this, versionCode+"").execute((Void[])null);
            
        }
        catch (Exception e) 
        {
        	
        }
	}
	
	
	private void clear_chat_log()
	{
//		dbManager.sql_query("");
		SQLiteDatabase db = MainActivity.dbManager.getReadableDatabase();
		
//		 Cursor cursor = db.rawQuery("select msg from chat_log where type=3 or type=4;", null);
		 Cursor cursor = db.rawQuery("select msg from chat_log where created_date < datetime('now','localtime', '-72 hour') and (type=3 or type=4)", null);
//		 Cursor cursor = db.rawQuery("select datetime('now', '-1 hour') from chat_log", null);
//		 Cursor cursor = db.rawQuery("select datetime('now','localtime', '-1 hour') from chat_log", null);
//		 Cursor cursor = db.rawQuery("select created_date from chat_log", null);
//		 Cursor cursor = db.rawQuery("select strftime('%S','now') from chat_log", null);
				 		 
	     String path="";
		 while(cursor.moveToNext())// 3일이 지난 사진 파일을 삭제시켜줌. 
         {
			path = cursor.getString(0);
			
			 try
			   {
				   File past_file = new File(path);
				   
				   if(past_file.exists())
				   {
					   past_file.delete();
				   }
			   }
			   catch(Exception e)
			   {
				   e.printStackTrace();
			   }
			
//			System.out.println("Data : " + path + " " + time);
//            Log.d("clear_chat_log", cursor.getCount()+"");
			 
//			 int count = cursor.getInt(0);
//			 Log.d("clear_chat_log", count+"");
         }
		 
		 Cursor cursor2 = db.rawQuery("delete from chat_log where created_date < datetime('now','localtime', '-72 hour')", null);
		 
		 String msg="";
		 while(cursor2.moveToNext())// 3일이 지난 사진 파일을 삭제시켜줌. 
         {
			msg = cursor2.getString(0);
			Log.d("Data", msg+"");
         }
	        
	}
	
	
	private Boolean isNetWork(){
		
		try
		{
			ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
			boolean isMobileAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
			boolean isMobileConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
			boolean isWifiAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
			boolean isWifiConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
			if ((isWifiAvailable && isWifiConnect) || (isMobileAvailable && isMobileConnect))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch(NullPointerException np)
		{
			//System.out.println("isNetwork(), NullPointerException!");
		}
		
		return true;
	}
	
	public class friend_Adapter3 extends BaseAdapter
	{
		private LayoutInflater m_inflater = null;
		public ArrayList<FriendView3> mainview;
		
		public friend_Adapter3(ArrayList<FriendView3> items)
		{
			mainview = items;
			m_inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public void add(FriendView3 M)
		{
			mainview.add(M);
			runOnUiThread(new Runnable() {
				
				  public void run() 
				  {
					  notifyDataSetChanged();
				  }
				});
		}
		
		@Override
		public int getCount() 
		{
			return mainview.size();
		}


		@Override
		public Object getItem(int position) 
		{
			return mainview.get(position);
		}


		@Override
		public long getItemId(int position) 
		{
			return position;
		}
		

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) 
		{
			View view=null;
			
			if(convertView == null)
			{
					view = m_inflater.inflate(R.layout.activity_f_chat_item, null);
			}
			else
			{
				view = convertView;
			}
			
			final FriendView3 line = mainview.get(position);
			//System.out.println("mainview.size() : " + mainview.size());
			 
			sibalFInal = position; // 밑에 final 문제를 해결하기위하여 사용함.
			
			if(line != null)
			{
					  SQLiteDatabase db = MainActivity.dbManager.getReadableDatabase();
					  ImageView profile = (ImageView) view.findViewById(R.id.friend_pro);
                      TextView name = (TextView) view.findViewById(R.id.name);
                      TextView last_dialog = (TextView) view.findViewById(R.id.last_dialog);
                      TextView time = (TextView) view.findViewById(R.id.time);
                      TextView alim = (TextView) view.findViewById(R.id.count2);
                      
                     
                      if(profile != null)
                      {
                    	  profile.setImageDrawable(line.getImg());
                    	  
                      }
                      
                      if (name != null)
                      {
                    	  name.setText(line.getName()+"");  
                      }
                      
                      if(last_dialog != null)
                      {
                    	  //System.out.println("line.getKey() : " + line.getKey()); 
                    	  Cursor lastValue = db.rawQuery("select msg from chat_log where chat_sequence = '"+line.getKey()+"' order by seq desc limit 1;", null);
                    	  lastValue.moveToNext();
                    	  
                    	  String last;
                    	  try
                    	  {
                    		  last = lastValue.getString(0);                    		  
                    	  }
                    	  catch(CursorIndexOutOfBoundsException e)
                    	  {
                    		  last = "마지막 대화가 없습니다.";
                    		  e.printStackTrace();
                    	  }
//                    	  //System.out.println("last : " + last);
                    	  
                    	  if(last.contains("data/data/com.") && last.contains(".jpg"))
                    	  {
                    		  last_dialog.setText("사진");
                    	  }
                    	  else
                    	  {
                        	  last_dialog.setText(last);
                    	  }
                    	  //System.out.println("마지막 대화 내용 : " + last);
                      }
                      
                      if (time != null)
                      {
                    	  time.setText(line.getTime()+"");                            
                      }
                  		Cursor cursor = db.rawQuery("select count(*) from chat_log where state = 0 and nick = '"+line.getName()+"';", null);
                  		cursor.moveToNext();
                  		int count = cursor.getInt(0);
                  		
                  		//Log.d("검사", "count : " + count);
                  		if(count == 0) // 몇개의 메시지가 있는지 표시하는 부분
                  		{
                  			if(alim != null)
                  			{
                  				alim.setVisibility(View.INVISIBLE);				
                  			}
                  			else
                  			{
                  				//System.out.println("null ㅡㅡ");
                  			}
                  		}
                  		else
                  		{
                  			if(alim != null)
                  			{
                  				alim.setVisibility(View.VISIBLE);
                  				alim.setText(count+"");

                  			}

                  	}
			}
			
			
			view.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) 
				{
//					Toast.makeText(MainActivity.this, "길게 눌렀음", Toast.LENGTH_LONG).show();
					AlertDialog.Builder aDialog = new AlertDialog.Builder(MainActivity.this);
					aDialog.setTitle("삭제");
					aDialog.setMessage("대화 내용을 삭제하시겠습니까?");
					aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) 
						{
							
						}

					});
					
					aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {				
						public void onClick(DialogInterface dialog, int which) 
						{
							Toast.makeText(getApplicationContext(), "삭제하였습니다.", Toast.LENGTH_SHORT).show();
							String chat_list = chk_login.getString("chat_list", "[]");
							 
							JSONParser jsonParser = new JSONParser();
							JSONArray jarr2;
							try {
								
								jarr2 = (JSONArray) jsonParser.parse(chat_list);
								
								for(int i=0;i<jarr2.size();i++)
								{
									JSONObject j = (JSONObject)jarr2.get(i);
									String f_nick = (String)j.get("f_nick");
									
									if(f_nick.equals(line.getName()))
									{
										jarr2.remove(i);
										String jarr = jarr2.toJSONString();
										MainActivity.editor.putString("chat_list", jarr);
										MainActivity.editor.commit();
										
										mainview.remove(position);
										
										runOnUiThread(new Runnable() 
										{
											  public void run() 
											  {
												  notifyDataSetChanged();
											  }
										});
										
									}
									
								}
								
							}
							catch (ParseException e) 
							{
								e.printStackTrace();
							}
								
							
						}

				});
					AlertDialog ad = aDialog.create();
					ad.show();
					
					return true;
				}
			});
			
			view.setOnClickListener(new OnClickListener() {
				
				/*
				 * 방이 새로 열리면서 기존에 대화했던 내용을 동기화해야함.
				 * 
				 * */
				
				SQLiteDatabase db = MainActivity.dbManager.getReadableDatabase();
				String str;
				
				@Override
				public void onClick(View v) 
				{
				
					//System.out.println("view 터치 클릭 리스너 실행!!");

					MainActivity.dbManager.sql_query("update chat_log set state = 1 where state = 0;"); // 이전에 받았던 모든 메시지들의 state를 전부 1로 바꿈.
					
					 personalChat.With_friend = line.getName();

					 String sequence = null;
					 String chat_list = chk_login.getString("chat_list", "[]");
					 
					 //Log.d("chat_list", chat_list+"");
					 
					 JSONParser jsonParser2 = new JSONParser();
					 JSONArray jarr2;
					 try 
					 {
						 jarr2 = (JSONArray) jsonParser2.parse(chat_list);
						 
						 //Log.d("jarr2.size()", jarr2.size()+"");
						 for(int i=0;i<jarr2.size();i++)
						 {
							 JSONObject j = (JSONObject)jarr2.get(i);
							 
							 String seq = (String)j.get("seq");
							 String f_nick = (String)j.get("f_nick");
							 
							 //Log.d("seq", seq+"");
							 //Log.d("f_nick", f_nick+"");
							 
							 if(line.getName().equals(f_nick))
							 {
								 sequence = seq;
								 MainActivity.currentKey = seq;
								
								    JSONObject packet = new JSONObject();
									packet.put("Command", "save_KEY_USERS");
									packet.put("seq", seq);
									packet.put("my_nick", MainActivity.MyNickname);
									packet.put("f_nick", f_nick);
									
									String pac3 = packet.toJSONString();
									MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
									MainActivity.out.println(pac3);
									MainActivity.out.flush();
									
								 //Log.d("currentKey", MainActivity.currentKey+"");
								 createChatRoom3(f_nick, seq);
								 
								 break;
							 }
						 } 

					 } 
					 catch (ParseException e) 
					 {
				 		 e.printStackTrace();
					 } 
					
					 SQLiteDatabase db = MainActivity.dbManager.getReadableDatabase();
					 //System.out.println("`");
					 //Log.d("sequence", sequence);
					 
					 
					 Cursor cursor = db.rawQuery("select * from chat_log where chat_sequence = '"+sequence+"';", null);
				        while(cursor.moveToNext()) {
				            str= cursor.getString(1)+","+cursor.getInt(2)+","+cursor.getString(3)+","+cursor.getString(4)+","+cursor.getString(5)
				            		+","+cursor.getString(6)+","+","+cursor.getInt(7);
//				            //Log.d("PrintData", str);
				            
				            Message message = null;
				            switch (cursor.getInt(7)) {
							
				            case 0:
				            	message = new Message(cursor.getInt(7), cursor.getString(3), cursor.getString(4),cursor.getString(6));
//				            	//Log.d("case 0", cursor.getInt(7)+","+ cursor.getString(3)+","+ cursor.getString(4)+","+cursor.getString(6));
				            	
				            	MainActivity.adapter2.add(message);
								MainActivity.adapter2.notifyDataSetChanged();
								
								break;
				           
				            case 1:
				            	message = new Message(cursor.getInt(7), cursor.getString(4));
//				            	//Log.d("case 1", cursor.getInt(7)+","+ cursor.getString(4));
				            	
				            	MainActivity.adapter2.add(message);
								MainActivity.adapter2.notifyDataSetChanged();
								
								break;
								
								
				            case 2:
				            	
				            	message = new Message(cursor.getInt(7), cursor.getString(3), cursor.getString(4),cursor.getString(6));
//				            	//Log.d("case 2", cursor.getInt(7)+","+ cursor.getString(3)+","+ cursor.getString(4)+","+cursor.getString(6));
								
				            	MainActivity.adapter2.add(message);
								MainActivity.adapter2.notifyDataSetChanged();
								
				            	break;
				            	
				            case 3:
				            	String path = cursor.getString(4);
				            	Bitmap bm = BitmapFactory.decodeFile(path);
				            	
				            	byte[] bytes = ut.bitmapToByteArray(bm);
				            	
				            	message = new Message(cursor.getInt(7),cursor.getString(3),bytes, cursor.getString(6));
//				            	//Log.d("case 3", cursor.getInt(7)+","+ cursor.getString(3)+","+ cursor.getString(4)+","+cursor.getString(6));
				            	
				            	MainActivity.adapter2.add(message);
								MainActivity.adapter2.notifyDataSetChanged();
				            	break;
				            	
				            case 4:
				            	String path2 = cursor.getString(4);
				            	Bitmap bm2 = BitmapFactory.decodeFile(path2);
				            	
				            	byte[] bytes2 = ut.bitmapToByteArray(bm2);
				            	
				            	message = new Message(cursor.getInt(7),cursor.getString(3),bytes2, cursor.getString(6));		
//				            	//Log.d("case 4", cursor.getInt(7)+","+ cursor.getString(3)+","+ cursor.getString(4)+","+cursor.getString(6));
				            	
				            	MainActivity.adapter2.add(message);
								MainActivity.adapter2.notifyDataSetChanged();
				            	break;

							default:
								
								break;
							}
				        }
				}
				
			});
			
			return view;
		}
	}
	
	
	public class friend_Adapter2 extends BaseAdapter
	{
		private LayoutInflater m_inflater = null;
		public ArrayList<friends_list> mainview;
		
		public friend_Adapter2(ArrayList<friends_list> items)
		{
			mainview = items;
			m_inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public void add(friends_list M)
		{
			mainview.add(M);
			runOnUiThread(new Runnable() {
				
				  public void run() 
				  {
					  notifyDataSetChanged(); 
				  }
				});
		}
		


		@Override
		public int getCount() 
		{
			return mainview.size();
		}

		@Override
		public Object getItem(int position) 
		{
			return mainview.get(position);
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			
			View view=null;
			
			if(convertView == null)
			{
					view = m_inflater.inflate(R.layout.activity_friend_list, null);
			}
			else
			{
				view = convertView;
			}
			
			final friends_list line = mainview.get(position);
			
			sibalFInal = position; // 밑에 final 문제를 해결하기위하여 사용함.
			
			if(line != null)
			{
					  final ImageView profile = (ImageView) view.findViewById(R.id.friend_pro);
                      TextView name = (TextView) view.findViewById(R.id.name);
                      TextView state_msg = (TextView) view.findViewById(R.id.state_msg);
                     
                      if(profile != null)
                      {
//                    	  Log.d("line.getImg_path() : ", line.getImg_path());
                    	  if(! line.getImg_path().equals("no"))//프사를 설정한 경우.
                    	  {
//								  Glide.with(getApplicationContext()).load(line.getImg_path())
//	              				  .transform(new CircleTransform(getApplicationContext()))
//	              				  .into(profile);
                    		  
                    		  Picasso.with(getApplicationContext()).load(line.getImg_path()).error(R.drawable.person).transform(new CircleTransform2())
                    		  .resize(100, 100).into(profile);
                    		  
                    	  }
                    	  else // 프사를 설정을 안한경우.
                    	  {
//                    		  Glide.with(getApplicationContext()).load(R.drawable.person).override(100, 100)
//              				  .error(R.drawable.person).transform(new CircleTransform(getApplicationContext())).into(profile);
                    		  
                    		  Picasso.with(getApplicationContext()).load(R.drawable.person).error(R.drawable.person).transform(new CircleTransform2())
                    		  .resize(100, 100).into(profile);
                    		  
                    	  }
                      }
                      if (name != null)
                      {
                    	  name.setText(line.getName()+"");                            
                      }
                      
                      if(state_msg != null)
                      {
                    	  if(!line.getState_msg().equals("no")) //상대방이 상태메시지를 설정하지 않은 경우.
                    	  {    
                    		  //Log.d("MainActivity", "상대방이 상태메시지를 설정한 경우");
                    		  //Log.d("nick", line.getName());
                    		  //Log.d("mainView의 크기", mainview.size()+"");
                        	  state_msg.setText(line.getState_msg()+"");
                    	  }
                    	  else
                    	  {
                    		  //Log.d("MainActivity", "상대방이 상태메시지를 설정하지 않은 경우");
                    		  //Log.d("nick", line.getName());
                    		  //Log.d("mainView의 크기", mainview.size()+"");
                    		  state_msg.setText("");
//                    		  state_msg.setVisibility(View.INVISIBLE);
                    	  }
                      }
                      
                      view.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
          					
          					Friend.f_touch.setVisibility(View.VISIBLE);
          					detect_f_profile_touch = true;
          					
          					Friend.f_touch.setOnClickListener(new OnClickListener() {
          							
          							@Override
          							public void onClick(View v) {
          								//System.out.println("배경이 터치되었습니다.");
          							}
          						});
          					if( (MainActivity.NICK_and_BITMAP.get(line.getName()) != null) ||  (req_path != null) ) // 사용자가 프로필을 설정을 했을때.
          					{
          							Bitmap sizingBmp2 = Bitmap.createScaledBitmap(MainActivity.NICK_and_BITMAP.get(line.getName()), Profile.pic_w, Profile.pic_w, true);
          							RoundedAvatarDrawable tmpRoundedAvatarDrawable2 = new RoundedAvatarDrawable(sizingBmp2);
          							Friend.f_friend_profile.setImageDrawable(tmpRoundedAvatarDrawable2);
              					
          							Friend.f_friend_profile.setOnClickListener(new OnClickListener() 
                  					{
        								@Override
        								public void onClick(View v) 
        								{
        									//System.out.println("사진이 클릭되었습니다.");
        									JSONObject Packet2 = new JSONObject();
        									Packet2.put("Command", "give_me_the_url");
        									Packet2.put("nickname",line.getName());
        									String pac2 = Packet2.toJSONString();
        									
        									//Log.d("give me the url", pac2);
        									
        									MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
        									MainActivity.out.println(pac2); //실험!!!!
        									MainActivity.out.flush();
        									
        									ut.DialogProgress(MainActivity.this);
        									Intent intent = new Intent(MainActivity.this, Over_profile_pic.class);
        									startActivity(intent);
        									
        								}
        							});
                  					
              					//Log.d("MainActivity", "사용자가 프로필을 설정을 한 경우!");
          					}
          					else
          					{
          						Bitmap default_img = BitmapFactory.decodeResource(getResources(), R.drawable.person);
          						Bitmap sizingBmp2 = Bitmap.createScaledBitmap(default_img, Profile.pic_w, Profile.pic_w, true);
                         		RoundedAvatarDrawable tmpRoundedAvatarDrawable2 = new RoundedAvatarDrawable(sizingBmp2);
                         		Friend.f_friend_profile.setImageDrawable(tmpRoundedAvatarDrawable2);
              					
              					//Log.d("MainActivity", "사용자가 프로필을 설정을 안한 경우!");
          					}
          					
          					Friend.chat.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									
									startTime = System.currentTimeMillis();
									
									long lTime = startTime -  endTime;
									
									if(lTime > 0 && lTime<1000) // 짧은 시간내에 여러번 터치시 1번만 작동하게.
									{
										
									}
									else
									{
										String chat_list = chk_login.getString("chat_list", "[]");
										
										if(chat_list.equals("[]")==false)
										{
											 
											JSONParser jsonParser = new JSONParser();
											JSONArray jarr2;
											try {
												
												jarr2 = (JSONArray) jsonParser.parse(chat_list);
												ArrayList<String> f_name_list = new ArrayList<String>();
												
												for(int i=0;i<jarr2.size();i++)
												{
													JSONObject j = (JSONObject)jarr2.get(i);
													String f_nick = (String)j.get("f_nick");
													
													f_name_list.add(f_nick);
												}
												
												if(f_name_list.contains(line.getName()))
												{
													Intent intent = new Intent(MainActivity.this, Header.class);
													intent.putExtra("f_name", line.getName());
													startActivity(intent);
												}
												else
												{
													personalChat.With_friend = line.getName();
													
													JSONObject Packet2 = new JSONObject();
													Packet2.put("Command", "friend_chat");
													Packet2.put("f_name",line.getName());
													String pac2 = Packet2.toJSONString();
													
													MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
													MainActivity.out.println(pac2); 
													MainActivity.out.flush();
												}
												
											}
											catch (ParseException e) 
											{
												e.printStackTrace();
											}
//											Intent intent = new Intent(MainActivity.this, Header.class);
//											intent.putExtra("f_name", line.getName());
//											startActivity(intent);
										}
										else
										{
											personalChat.With_friend = line.getName();
											
											JSONObject Packet2 = new JSONObject();
											Packet2.put("Command", "friend_chat");
											Packet2.put("f_name",line.getName());
											String pac2 = Packet2.toJSONString();
											
											MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
											MainActivity.out.println(pac2); 
											MainActivity.out.flush();
										}
									}
									
									endTime = startTime;
									
								}
							});
						}
					});
                      
			}
			
			return view;
		}
	}
	
	
	public class friend_Adapter extends BaseAdapter
	{
		private LayoutInflater m_inflater = null;
		public ArrayList<FriendView> mainview;
		
		public friend_Adapter(ArrayList<FriendView> items)
		{
			mainview = items;
			m_inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
		}
		
		public void add(FriendView M)
		{
			mainview.add(M);
			runOnUiThread(new Runnable() {
				
				  public void run() 
				  {
					  notifyDataSetChanged();
				  }
				});
		}
		


		@Override
		public int getCount() 
		{
			return mainview.size();
		}


		@Override
		public Object getItem(int position) 
		{
			return mainview.get(position);
		}


		@Override
		public long getItemId(int position) 
		{
			return position;
		}
		


		@Override
		public View getView(final int position, View convertView, ViewGroup parent) 
		{
			View view=null;
			
			if(convertView == null)
			{
					view = m_inflater.inflate(R.layout.activity_friend_list_item, null);
			}
			else
			{
				view = convertView;
			}
			
			final FriendView line = mainview.get(position);
			
			sibalFInal = position; // 밑에 final 문제를 해결하기위하여 사용함.
			
			if(line != null)
			{
					  ImageView profile = (ImageView) view.findViewById(R.id.friend_pro);
                      TextView name = (TextView) view.findViewById(R.id.name);
                      Button plus = (Button) view.findViewById(R.id.plus);
                     
                      if(profile != null)
                      {
                    	  profile.setImageDrawable(line.getImg());
                    	  
                      }
                      if (name != null)
                      {
                    	  name.setText(line.getName()+"");                            
                      }
                      
                      plus.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							Toast.makeText(MainActivity.this, line.getName() + "님을 친구로 추가하셨습니다.", Toast.LENGTH_SHORT).show();
							
							//Log.d("MainActivity", "In plus.setOnClickListener");
							RoundedAvatarDrawable tmpRoundedAvatarDrawable;
		              		
		              		// db로 부터 nickname, path, state_msg 정보를 가져온다.
		              		
		              		if( MainActivity.NICK_and_BITMAP.get(line.getName()) != null)
							{
							 Bitmap target_bmp = Bitmap.createScaledBitmap(MainActivity.NICK_and_BITMAP.get(line.getName()),100, 100, true);
							 tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(target_bmp);
							}
							else
							{
								 Bitmap default_img = BitmapFactory.decodeResource(getResources(), R.drawable.person);
								 Bitmap target_bmp = Bitmap.createScaledBitmap(default_img,100, 100, true);
								 tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(target_bmp);
							}
		              		
		              		// path와 state_msg  정보가 부족함. 얻어와야함.
		              		new httpRequest2(line.getName()+","+MyNickname, "get_data.php", tmpRoundedAvatarDrawable).start();
		              		
		              		// minus 버튼과 똑같음.
		              		fv.remove(sibalFInal);
							
							String jarrStr = chk_login.getString("req_friends", "[]");
							//Log.d("MainActivity", "jarrStr : " + jarrStr);
							
							JSONParser Jparser = new JSONParser();
							try {
								JSONArray jarry = (JSONArray) Jparser.parse(jarrStr);
								jarry.remove(sibalFInal);
								
								//System.out.println("제거 확인! jarry : " + jarry);
								String tophp = jarry.toJSONString();
//								new httpRequest(MyNickname, tophp, "save_req_friend_list.php", editor, 1).start();
								
								editor.putString("req_friends", tophp);
								editor.commit();
								
							} catch (ParseException e) {
								e.printStackTrace();
							}
							////
							
							Requset_Friends.f_pro_pic_touch.setVisibility(View.INVISIBLE);
							detect_f_profile_touch = false;
							Fadapter.notifyDataSetChanged();

							//서버에 친구가 됬다는것을 알리곳 상대방에게도 변화를 주어야된다!
							JSONObject Packet2 = new JSONObject();
							Packet2.put("Command", "we've got friend");
							Packet2.put("Requester",line.getName());
							Packet2.put("My", MainActivity.MyNickname);
							
							String pac2 = Packet2.toJSONString();
							
							MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
							MainActivity.out.println(pac2); //실험!!!!
							MainActivity.out.flush();
						}
					});
                      
                      view.setOnTouchListener(new OnTouchListener() {
          				
          				@Override
          				public boolean onTouch(View v, MotionEvent event) {
          					
          					Requset_Friends.f_pro_pic_touch.setVisibility(View.VISIBLE);
          					detect_f_profile_touch = true;
          					
          					Requset_Friends.f_pro_pic_touch.setOnClickListener(new OnClickListener() {
          							
          							@Override
          							public void onClick(View v) {
          								//System.out.println("배경이 터치되었습니다.");
          							}
          						});
          					if( (MainActivity.NICK_and_BITMAP.get(line.getName()) != null) ||  (req_path != null) ) // 사용자가 프로필을 설정을 했을때.
          					{
          							Bitmap sizingBmp2 = Bitmap.createScaledBitmap(MainActivity.NICK_and_BITMAP.get(line.getName()), Profile.pic_w, Profile.pic_w, true);
          							RoundedAvatarDrawable tmpRoundedAvatarDrawable2 = new RoundedAvatarDrawable(sizingBmp2);
                  					Requset_Friends.f_request_friend_profile.setImageDrawable(tmpRoundedAvatarDrawable2);
                  					Requset_Friends.f_request_friend_profile.setOnClickListener(new OnClickListener() 
                  					{
        								
        								@Override
        								public void onClick(View v) 
        								{
        									//System.out.println("사진이 클릭되었습니다.");
        									JSONObject Packet2 = new JSONObject();
        									Packet2.put("Command", "give_me_the_url");
        									Packet2.put("nickname",line.getName());
        									String pac2 = Packet2.toJSONString();
        									
        									MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
        									MainActivity.out.println(pac2); //실험!!!!
        									MainActivity.out.flush();
        									
        									ut.DialogProgress(MainActivity.this);
        									Intent intent = new Intent(MainActivity.this, Over_profile_pic.class);
        									startActivity(intent);
        								}
        							});
                  					
              					//Log.d("MainActivity", "사용자가 프로필을 설정을 한 경우!");
          					}
          					else
          					{
          						Bitmap default_img = BitmapFactory.decodeResource(getResources(), R.drawable.person);
          						Bitmap sizingBmp2 = Bitmap.createScaledBitmap(default_img, Profile.pic_w, Profile.pic_w, true);
                         		RoundedAvatarDrawable tmpRoundedAvatarDrawable2 = new RoundedAvatarDrawable(sizingBmp2);
              					Requset_Friends.f_request_friend_profile.setImageDrawable(tmpRoundedAvatarDrawable2);
              					
              					//Log.d("MainActivity", "사용자가 프로필을 설정을 안한 경우!");
          					}
          					
          					Requset_Friends.minus.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									
									//Log.d("Requset_Friends.minus.setOnClickListener ,sibalFInal", sibalFInal+"");
									fv.remove(position);
									
									// 리스트에서 제거해준 값을 db와 로컬에 동기화해야함.
									String jarrStr = chk_login.getString("req_friends", "[]");
									//Log.d("MainActivity", "jarrStr : " + jarrStr);
									
									JSONParser Jparser = new JSONParser();
									try {
										JSONArray jarry = (JSONArray) Jparser.parse(jarrStr);
										jarry.remove(sibalFInal);
										
										//System.out.println("제거 확인! jarry : " + jarry);
										String tophp = jarry.toJSONString();
//										new httpRequest(MyNickname, tophp, "save_req_friend_list.php", editor, 1).start();
										
										editor.putString("req_friends", tophp);
										editor.commit();
										
										new httpRequest3(line.getName()+","+MyNickname, "change_state.php").start(); // db에서 state를 0에서 2로 바꿔줌.(친구 거부기능)
										
									} catch (ParseException e) {
										e.printStackTrace();
									}
									////
									
									Requset_Friends.f_pro_pic_touch.setVisibility(View.INVISIBLE);
									detect_f_profile_touch = false;
									Fadapter.notifyDataSetChanged();
								}
							});
          					
          					Requset_Friends.plus.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) 
								{
									
									Toast.makeText(MainActivity.this, line.getName() + "님을 친구로 추가하셨습니다.", Toast.LENGTH_SHORT).show();
									
									//Log.d("MainActivity", "In plus.setOnClickListener");
									RoundedAvatarDrawable tmpRoundedAvatarDrawable;
				              		
				              		// db로 부터 nickname, path, state_msg 정보를 가져온다.
				              		
				              		if( MainActivity.NICK_and_BITMAP.get(line.getName()) != null)
									{
									 Bitmap target_bmp = Bitmap.createScaledBitmap(MainActivity.NICK_and_BITMAP.get(line.getName()),100, 100, true);
									 tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(target_bmp);
									}
									else
									{
										 Bitmap default_img = BitmapFactory.decodeResource(getResources(), R.drawable.person);
										 Bitmap target_bmp = Bitmap.createScaledBitmap(default_img,100, 100, true);
										 tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(target_bmp);
									}
				              		
				              		// path와 state_msg  정보가 부족함. 얻어와야함.
				              		new httpRequest2(line.getName()+","+MyNickname, "get_data.php", tmpRoundedAvatarDrawable).start();
									
				              	// minus 버튼과 똑같음.
				              		fv.remove(sibalFInal);
									
									String jarrStr = chk_login.getString("req_friends", "[]");
									//Log.d("MainActivity", "jarrStr : " + jarrStr);
									
									JSONParser Jparser = new JSONParser();
									try {
										JSONArray jarry = (JSONArray) Jparser.parse(jarrStr);
										jarry.remove(sibalFInal);
										
										//System.out.println("제거 확인! jarry : " + jarry);
										String tophp = jarry.toJSONString();
//										new httpRequest(MyNickname, tophp, "save_req_friend_list.php", editor, 1).start();
										
										editor.putString("req_friends", tophp);
										editor.commit();
										
									} catch (ParseException e) {
										e.printStackTrace();
									}
									////
									
									Requset_Friends.f_pro_pic_touch.setVisibility(View.INVISIBLE);
									detect_f_profile_touch = false;
									Fadapter.notifyDataSetChanged();
									 ////
									//서버에 친구가 됬다는것을 알리곳 상대방에게도 변화를 주어야된다!
									JSONObject Packet2 = new JSONObject();
									Packet2.put("Command", "we've got friend");
									Packet2.put("Requester",line.getName());
									Packet2.put("My", MainActivity.MyNickname);
									
									String pac2 = Packet2.toJSONString();
									
									MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
									MainActivity.out.println(pac2); //실험!!!!
									MainActivity.out.flush();
									
								}
							});
          					
          					 
          					
          					return false;
          				}
          			});
                      
			}
			
			return view;
		}
	}
	
	class dataAdapter extends BaseAdapter
	{
		private LayoutInflater m_inflater = null;
		ArrayList<Message> message;
		int pos;
		String nick;
		
		public dataAdapter(ArrayList<Message> items)
		{
			message = items;
			m_inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
		}
		
		public void add(Message M)
		{
			message.add(M);
			notifyDataSetChanged();
		}
		

		@Override
		public int getItemViewType(int position) 
		{
			return message.get(position).getType();
		}


		@Override
		public int getViewTypeCount() 
		{
			return 5;
		}


		@Override
		public int getCount() 
		{
			return message.size();
		}


		@Override
		public Object getItem(int position) 
		{
			return message.get(position);
		}


		@Override
		public long getItemId(int position) 
		{
			return position;
		}
		


		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			View view=null;
			pos = position;
			int type = getItemViewType(position);
			
//			//Log.d("scroll position", Allchat.lv.getFirstVisiblePosition()+"");
			
//			Allchat.lv.smoothScrollToPosition(MainActivity.adapter.getCount());
			
			if(convertView == null)
			{
				switch(type)
				{
				case 0:
					view = m_inflater.inflate(R.layout.my_message, null); // 나의 텍스트 메시지
					break;
				case 1:
					view = m_inflater.inflate(R.layout.activity_enterance, null); // 등장, 퇴장
					break;
				case 2:
					view = m_inflater.inflate(R.layout.message2, null); // 다른 사람의 텍스트 메시지.
					break;
					
				case 3:
					view = m_inflater.inflate(R.layout.activity_m_picture, null); // 나의 이미지 메시지.
					break;

				case 4:
					view = m_inflater.inflate(R.layout.activity_y_picture, null); // 다른 사람의 이미지 메시지.
					break;
				}
				
				
			}
			else
			{
				view = convertView;
			}
			
			Message msg = message.get(position);
			
			String key = MainActivity.currentKey;
			Long key2 = Long.parseLong(key);
			
			boolean bl = true;
			
			if(key2<10000) bl = false;
				
			if(msg != null)
			{
				if(type == 0)
				{
                      TextView user_txt = (TextView) view.findViewById(R.id.user_txt);
                      TextView time = (TextView) view.findViewById(R.id.time);
                      //TextView read = (TextView) view.findViewById(R.id.read);
                      
                      if(user_txt != null)
                      {
                    	  user_txt.setText(msg.getUser_txt());
                      }
                      if(time != null)
                      {
                    	  time.setText(msg.getTime());
                      }
//                      if(read != null && bl == true)
//                      {
//                      	 read.setText(1+"");
//                      }
                      
				}
				
				else if(type == 1)
				{
					TextView enterance = (TextView) view.findViewById(R.id.enterance);
					
                    if (enterance != null)
                    {
                    	enterance.setText(msg.getOffical());
                    	
                    }
				}
				else if(type == 2)
				{
					  ImageView profile_img = (ImageView) view.findViewById(R.id.profile_img);
					  TextView user_id = (TextView) view.findViewById(R.id.user_id);
                      TextView user_txt = (TextView) view.findViewById(R.id.user_txt);
                      TextView time = (TextView) view.findViewById(R.id.time);
                     // TextView read = (TextView) view.findViewById(R.id.read);
                      
                      final String nickname = msg.getUser_id();
                      
//                      //System.out.println("type2 : nickname " + nickname);
//                      //System.out.println("NICK_and_BITMAP의 크기 : " + MainActivity.NICK_and_BITMAP.size());
                      
                      if(MainActivity.NICK_and_BITMAP.get(nickname) == null)
                      {
//                    	  //System.out.println("MainActivity.NICK_and_BITMAP : " +MainActivity.NICK_and_BITMAP);
                      }

                     
                     
                      
                      RoundedAvatarDrawable tmpRoundedAvatarDrawable = null;
//                      RoundedAvatarDrawable tmpRoundedAvatarDrawable2 = null;
                      
                      if(MainActivity.NICK_and_BITMAP.get(nickname) != null) // null이라면 사용자가 프로필 사진을 설정하지 않은거임.
                      {
                    	  Bitmap sizingBmp = Bitmap.createScaledBitmap(MainActivity.NICK_and_BITMAP.get(nickname), 70, 70, true);
                    	  Bitmap sizingBmp2 = Bitmap.createScaledBitmap(MainActivity.NICK_and_BITMAP.get(nickname), Profile.pic_w, Profile.pic_w, true);
                    	  tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(sizingBmp);
                    	  sibalFINAL = new RoundedAvatarDrawable(sizingBmp2);
                      }
                      
                      
                    if (user_id != null)
                    {
                  	  user_id.setText(msg.getUser_id());                            
                    }
                    if(user_txt != null)
                    {
                  	  user_txt.setText(msg.getUser_txt());
                    }
                    if(time != null)
                    {
                      time.setText(msg.getTime());
                    }
//                    if(read != null && bl == true)
//                    {
//                      read.setText(1+"");
//                    }
                    
                    
                    if (profile_img != null)
                    {
                    	 
//                    	 //System.out.println("in type2 user_id : " + nickname);
                    	
                    	 
                  	  if(MainActivity.NICK_and_BITMAP.get(nickname) != null)
                  	  {
                      		  profile_img.setImageDrawable(tmpRoundedAvatarDrawable);
                  	  }
                  	  else
                  	  {
                  		  //System.out.println("해당 유저는 프로필 사진을 등록하지 않았습니다.");
                  		  profile_img.setImageResource(R.drawable.person); // 기본이미지 설정.
                  	  }
                    }
                    
                    
                    profile_img.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) 
						{
							Allchat.detect_profile_touch = true;
						    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						    imm.hideSoftInputFromWindow(Allchat.et.getWindowToken(), 0);
						    
						    Allchat.pro_pic_touch.setVisibility(View.VISIBLE);
						    Allchat.chatting_view.setClickable(false);
						    
						    if(MainActivity.NICK_and_BITMAP.get(nickname) != null) // null이라면 사용자가 프로필 사진을 설정하지 않은거임.
		                    {
						    	Allchat.add_friend_profile.setImageDrawable(sibalFINAL);
						    	Allchat.add_friend_profile.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										//System.out.println("사진이 클릭되었습니다.");
										JSONObject Packet2 = new JSONObject();
										Packet2.put("Command", "give_me_the_url");
										Packet2.put("nickname",nickname);
										String pac2 = Packet2.toJSONString();
										
										MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
										MainActivity.out.println(pac2); //실험!!!!
										MainActivity.out.flush();
										
										ut.DialogProgress(MainActivity.this);
										Intent intent = new Intent(getApplicationContext(), Over_profile_pic.class);
										startActivity(intent);
										
									}
								});

		                    }
						    else
						    {
						    	Allchat.add_friend_profile.setImageResource(R.drawable.person);
						    }
						    
						    Allchat.pro_pic_touch.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									//System.out.println("배경이 터치되었습니다.");
									
								}
							});
						    
						    Long key = Long.parseLong(MainActivity.currentKey);
						    if(key > 1000000)
						    {
						    	Allchat.add_friend_btn.setVisibility(View.INVISIBLE);
						    }
						    Allchat.add_friend_btn.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									
									Allchat.pro_pic_touch.setVisibility(View.INVISIBLE);
									//System.out.println("버튼이 클릭되었습니다.");
									
									Allchat.detect_profile_touch = false;
									
									JSONObject Packet2 = new JSONObject();
									Packet2.put("Command", "friend_request");
									Packet2.put("Target_nickname",nickname);
									Packet2.put("My_nickname", MainActivity.MyNickname);
									Packet2.put("Seq", MainActivity.MySeq+"");
									String pac2 = Packet2.toJSONString();
									
									MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
									MainActivity.out.println(pac2); //실험!!!!
									MainActivity.out.flush(); 
									
								}
							});
						    
						}
					});
                    
                    
				}
				else if(type == 3)
				{
//					  TextView user_id = (TextView) view.findViewById(R.id.user_id);
					  ImageView img = (ImageView)view.findViewById(R.id.img);
					  TextView time = (TextView) view.findViewById(R.id.time);
					  final int posi = pos;
//                    if (user_id != null)
//                    {
//                  	  user_id.setText(msg.getUser_id());                            
//                    }
                    if(img != null)
                    {
                    	int targetW;
                    	int targetH;
                    	if(Allchat.targetWidth == -5 || Allchat.targetHeight == -5)
                    	{
                    		img.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);

                        	targetW =  img.getMeasuredWidth();;
            				targetH =  img.getMeasuredHeight();
            				
            				Allchat.targetWidth = targetW;
            				Allchat.targetHeight = targetH;
            				
                    	}
                    	else
                    	{
                        	targetW =  Allchat.targetWidth;
            				targetH = Allchat.targetHeight;
                    	}
                    	 if(time != null)
                         {
                    		 time.setText(msg.getTime());
                         }
                    	 

        				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        				bmOptions.inJustDecodeBounds = true;
        				
        				BitmapFactory.decodeByteArray(msg.getBy() , 0, msg.getBy().length, bmOptions);
        				int photoW = bmOptions.outWidth;
        				int photoH = bmOptions.outHeight;
        				
        				  
        				// Determine how much to scale down the image
        				int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        				
        				//System.out.println("나~~~\ntargetW : " + targetW +"  "
//							     + "targetH : " + targetH+"  "
//							     + "photoW : " + photoW+"  "
//							     + "photoH : " + photoH+"  ");
        				
        				//Log.d("inSampleSize", scaleFactor+"");
        				  
        				// Decode the image file into a Bitmap sized to fill the View
        				bmOptions.inJustDecodeBounds = false;
        				bmOptions.inSampleSize = scaleFactor;
        				bmOptions.inPurgeable = true;
        				  
        				Bitmap bitmap =BitmapFactory.decodeByteArray(msg.getBy() , 0, msg.getBy().length, bmOptions);
        				img.setImageBitmap(bitmap);
        				//bitmap.recycle();
                    }
                    img.setOnClickListener(new OnClickListener() {
                    	
						@Override
						public void onClick(View v) 
						{
							
							Intent intent = new Intent(getApplicationContext(), Over.class);
				               intent.putExtra("position", posi);
				               intent.putExtra("type", 0);
				               startActivity(intent);
							
						}
					});
 
				}
				else if(type == 4)
				{
					  ImageView profile_img = (ImageView) view.findViewById(R.id.profile_img);
					  TextView user_id = (TextView) view.findViewById(R.id.user_id);
					  ImageView img = (ImageView)view.findViewById(R.id.img);
					  TextView time = (TextView) view.findViewById(R.id.time);
					  final String nickname = msg.getUser_id();
					  
					  Bitmap sizingBmp;
					  Bitmap sizingBmp2;
					  
					  try
						{
						  sizingBmp = Bitmap.createScaledBitmap(MainActivity.NICK_and_BITMAP.get(nickname), 70, 70, true);
						  sizingBmp2 = Bitmap.createScaledBitmap(MainActivity.NICK_and_BITMAP.get(nickname), Profile.pic_w, Profile.pic_w, true);
						}
						catch(NullPointerException ne)
						{
							Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.person);
							sizingBmp = Bitmap.createScaledBitmap(bm, 70, 70, true);
							sizingBmp2 = Bitmap.createScaledBitmap(bm, Profile.pic_w, Profile.pic_w, true);
						}
					  
					  final RoundedAvatarDrawable tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(sizingBmp);
					  final RoundedAvatarDrawable tmpRoundedAvatarDrawable2 = new RoundedAvatarDrawable(sizingBmp2);
					  final int posi = pos;
					  
                    if (user_id != null)
                    {
                  	  user_id.setText(msg.getUser_id());                            
                    }
                    if(img != null)
                    {
                    	 int targetW;
                    	 int targetH;
                    	 if(Allchat.targetWidth == -5 || Allchat.targetHeight == -5)
                    	 { 
                    		img.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);

                        	targetW =  img.getMeasuredWidth();;
            				targetH =  img.getMeasuredHeight();
            				
            				Allchat.targetWidth = targetW;
            				Allchat.targetHeight = targetH;
            				
                    	 }
                    	 else
                    	 {
                        	targetW =  Allchat.targetWidth;
            				targetH = Allchat.targetHeight;
                    		
                    	 } 
	                	 if(time != null)
	                     {
	                		 time.setText(msg.getTime());
	                     }
                    	
        				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        				bmOptions.inJustDecodeBounds = true;
        				
        				BitmapFactory.decodeByteArray(msg.getBy() , 0, msg.getBy().length, bmOptions);
        				int photoW = bmOptions.outWidth;
        				int photoH = bmOptions.outHeight;
        				//System.out.println("남~~~\ntargetW : " + targetW +"  "
//        									     + "targetH : " + targetH+"  "
//        									     + "photoW : " + photoW+"  "
//        									     + "photoH : " + photoH+"  ");
        				  
        				// Determine how much to scale down the image
        				int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        				  
        				//Log.d("inSampleSize", scaleFactor+"");
        				// Decode the image file into a Bitmap sized to fill the View
        				bmOptions.inJustDecodeBounds = false;
        				bmOptions.inSampleSize = scaleFactor;
        				bmOptions.inPurgeable = true;
        				  
        				Bitmap bitmap =BitmapFactory.decodeByteArray(msg.getBy() , 0, msg.getBy().length, bmOptions);
        				img.setImageBitmap(bitmap);
        				//bitmap.recycle();
                    }
                    
                    if (profile_img != null)
                    {
               	  
//                   	 //System.out.println("in type4 user_id : " + nickname);
                   	 
                  	  if(MainActivity.NICK_and_BITMAP.get(nickname) != null)
                  	  {
                  		  profile_img.setImageDrawable(tmpRoundedAvatarDrawable);
                  	   }
                  	  else
                  	  {
                  		  //System.out.println("해당 유저는 프로필 사진을 등록하지 않았습니다.");
                  	  }
                    }
                    
                    
                    	profile_img.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) 
						{
							
							Allchat.detect_profile_touch = true;
						    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						    imm.hideSoftInputFromWindow(Allchat.et.getWindowToken(), 0);
						    
						    Allchat.pro_pic_touch.setVisibility(View.VISIBLE);
						    Allchat.chatting_view.setClickable(false);
						    
						    Allchat.add_friend_profile.setImageDrawable(tmpRoundedAvatarDrawable2);
						    
						    Allchat.pro_pic_touch.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									//System.out.println("배경이 터치되었습니다.");
								}
							});
						    
						    
						    Allchat.add_friend_btn.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									
									Allchat.pro_pic_touch.setVisibility(View.INVISIBLE);
									//System.out.println("버튼이 클릭되었습니다.");
									
									Allchat.detect_profile_touch = false;

									JSONObject Packet2 = new JSONObject();
									Packet2.put("Command", "friend_request");
									Packet2.put("nickname",nickname);
									String pac2 = Packet2.toJSONString();
									
									MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
									MainActivity.out.println(pac2); //실험!!!!
									MainActivity.out.flush();
									
								}
							});
						    
						    
//						   Intent intent = new Intent(Allchat.this, Over_profile_pic.class);
//			               intent.putExtra("nickname", nickname);
//			               startActivity(intent);
							
//							   Intent intent = new Intent(Allchat.this, add_friends.class);
//				               intent.putExtra("nickname", nickname);
//				               startActivity(intent);
						
						}
					});
 
                    img.setOnClickListener(new OnClickListener() {
                    	
						@Override
						public void onClick(View v) 
						{
							
							Intent intent = new Intent(getApplicationContext(), Over.class);
				               intent.putExtra("position", posi);
				               intent.putExtra("type", 0);
				               startActivity(intent);
							
						}
					});
                    
				}
			}
			
			return view;
		}
	}
	
	
	class dataAdapter2 extends BaseAdapter
	{
		private LayoutInflater m_inflater = null;
		ArrayList<Message> message;
		int pos;
		String nick;
		
		public dataAdapter2(ArrayList<Message> items)
		{
			message = items;
			m_inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
		}
		
		public void add(Message M)
		{
			message.add(M);
			notifyDataSetChanged();
		}
		

		@Override
		public int getItemViewType(int position) 
		{
			return message.get(position).getType();
		}


		@Override
		public int getViewTypeCount() 
		{
			return 5;
		}


		@Override
		public int getCount() 
		{
			return message.size();
		}


		@Override
		public Object getItem(int position) 
		{
			return message.get(position);
		}


		@Override
		public long getItemId(int position) 
		{
			return position;
		}
		


		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			View view=null;
			pos = position;
			int type = getItemViewType(position);
			
//			//Log.d("scroll position", Allchat.lv.getFirstVisiblePosition()+"");
			
//			Allchat.lv.smoothScrollToPosition(MainActivity.adapter.getCount());
			
			if(convertView == null)
			{
				switch(type)
				{
				case 0:
					view = m_inflater.inflate(R.layout.my_message, null); // 나의 텍스트 메시지
					break;
				case 1:
					view = m_inflater.inflate(R.layout.activity_enterance, null); // 등장, 퇴장
					break;
				case 2:
					view = m_inflater.inflate(R.layout.message2, null); // 다른 사람의 텍스트 메시지.
					break;
					
				case 3:
					view = m_inflater.inflate(R.layout.activity_m_picture, null); // 나의 이미지 메시지.
					break;

				case 4:
					view = m_inflater.inflate(R.layout.activity_y_picture, null); // 다른 사람의 이미지 메시지.
					break;
				}
				
				
			}
			else
			{
				view = convertView;
			}
			
			Message msg = message.get(position);
			
			String key = MainActivity.currentKey;
			Long key2 = Long.parseLong(key);
			
			boolean bl = true;
			
			if(key2<10000) bl = false;
				
			if(msg != null)
			{
				if(type == 0)
				{
                      TextView user_txt = (TextView) view.findViewById(R.id.user_txt);
                      TextView time = (TextView) view.findViewById(R.id.time);
                      //TextView read = (TextView) view.findViewById(R.id.read);
                      
                      if(user_txt != null)
                      {
                    	  user_txt.setText(msg.getUser_txt());
                      }
                      if(time != null)
                      {
                    	  time.setText(msg.getTime());
                      }
//                      if(read != null && bl == true)
//                      {
//                      	 read.setText(1+"");
//                      }
                      
				}
				
				else if(type == 1)
				{
					TextView enterance = (TextView) view.findViewById(R.id.enterance);
					
                    if (enterance != null)
                    {
                    	enterance.setText(msg.getOffical());
                    	
                    }
				}
				else if(type == 2)
				{
					  ImageView profile_img = (ImageView) view.findViewById(R.id.profile_img);
					  TextView user_id = (TextView) view.findViewById(R.id.user_id);
                      TextView user_txt = (TextView) view.findViewById(R.id.user_txt);
                      TextView time = (TextView) view.findViewById(R.id.time);
                     // TextView read = (TextView) view.findViewById(R.id.read);
                      
                      final String nickname = msg.getUser_id();
                      
//                      //System.out.println("type2 : nickname " + nickname);
//                      //System.out.println("NICK_and_BITMAP의 크기 : " + MainActivity.NICK_and_BITMAP.size());
                      
                      if(MainActivity.NICK_and_BITMAP.get(nickname) == null)
                      {
//                    	  //System.out.println("MainActivity.NICK_and_BITMAP : " +MainActivity.NICK_and_BITMAP);
                      }

                     
                     
                      
                      RoundedAvatarDrawable tmpRoundedAvatarDrawable = null;
//                      RoundedAvatarDrawable tmpRoundedAvatarDrawable2 = null;
                      
                      if(MainActivity.NICK_and_BITMAP.get(nickname) != null) // null이라면 사용자가 프로필 사진을 설정하지 않은거임.
                      {
                    	  Bitmap sizingBmp = Bitmap.createScaledBitmap(MainActivity.NICK_and_BITMAP.get(nickname), 70, 70, true);
                    	  Bitmap sizingBmp2 = Bitmap.createScaledBitmap(MainActivity.NICK_and_BITMAP.get(nickname), Profile.pic_w, Profile.pic_w, true);
                    	  tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(sizingBmp);
                    	  sibalFINAL = new RoundedAvatarDrawable(sizingBmp2);
                      }
                      
                      
                    if (user_id != null)
                    {
                  	  user_id.setText(msg.getUser_id());                            
                    }
                    if(user_txt != null)
                    {
                  	  user_txt.setText(msg.getUser_txt());
                    }
                    if(time != null)
                    {
                      time.setText(msg.getTime());
                    }
//                    if(read != null && bl == true)
//                    {
//                      read.setText(1+"");
//                    }
                    
                    
                    if (profile_img != null)
                    {
                    	 
//                    	 //System.out.println("in type2 user_id : " + nickname);
                    	
                    	 
                  	  if(MainActivity.NICK_and_BITMAP.get(nickname) != null)
                  	  {
                      		  profile_img.setImageDrawable(tmpRoundedAvatarDrawable);
                  	  }
                  	  else
                  	  {
                  		  //System.out.println("해당 유저는 프로필 사진을 등록하지 않았습니다.");
                  		  profile_img.setImageResource(R.drawable.person); // 기본이미지 설정.
                  	  }
                    }
                    
                    
                    profile_img.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) 
						{
							personalChat.detect_profile_touch = true;
						    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						    imm.hideSoftInputFromWindow(personalChat.et.getWindowToken(), 0);
						    
						    personalChat.pro_pic_touch.setVisibility(View.VISIBLE);
						    personalChat.chatting_view.setClickable(false);
						    
						    if(MainActivity.NICK_and_BITMAP.get(nickname) != null) // null이라면 사용자가 프로필 사진을 설정하지 않은거임.
		                    {
						    	personalChat.add_friend_profile.setImageDrawable(sibalFINAL);
						    	personalChat.add_friend_profile.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										//System.out.println("사진이 클릭되었습니다.");
										JSONObject Packet2 = new JSONObject();
										Packet2.put("Command", "give_me_the_url");
										Packet2.put("nickname",nickname);
										String pac2 = Packet2.toJSONString();
										
										MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
										MainActivity.out.println(pac2); //실험!!!!
										MainActivity.out.flush();
										
										ut.DialogProgress(MainActivity.this);
										Intent intent = new Intent(getApplicationContext(), Over_profile_pic.class);
										startActivity(intent);
										
									}
								});

		                    }
						    else
						    {
						    	personalChat.add_friend_profile.setImageResource(R.drawable.person);
						    }
						    
						    personalChat.pro_pic_touch.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									//System.out.println("배경이 터치되었습니다.");
									
								}
							});
						    
						    Long key = Long.parseLong(MainActivity.currentKey);
						    if(key > 1000000)
						    {
						    	personalChat.add_friend_btn.setVisibility(View.INVISIBLE);
						    }
						    personalChat.add_friend_btn.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									
									personalChat.pro_pic_touch.setVisibility(View.INVISIBLE);
									//System.out.println("버튼이 클릭되었습니다.");
									
									personalChat.detect_profile_touch = false;
									
									JSONObject Packet2 = new JSONObject();
									Packet2.put("Command", "friend_request");
									Packet2.put("Target_nickname",nickname);
									Packet2.put("My_nickname", MainActivity.MyNickname);
									Packet2.put("Seq", MainActivity.MySeq+"");
									String pac2 = Packet2.toJSONString();
									
									MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
									MainActivity.out.println(pac2); //실험!!!!
									MainActivity.out.flush(); 
									
								}
							});
						    
						}
					});
                    
                    
				}
				else if(type == 3)
				{
//					  TextView user_id = (TextView) view.findViewById(R.id.user_id);
					  ImageView img = (ImageView)view.findViewById(R.id.img);
					  TextView time = (TextView) view.findViewById(R.id.time);
					  final int posi = pos;
//                    if (user_id != null)
//                    {
//                  	  user_id.setText(msg.getUser_id());                            
//                    }
                    if(img != null)
                    {
                    	int targetW;
                    	int targetH;
                    	if(personalChat.targetWidth == -5 || personalChat.targetHeight == -5)
                    	{
                    		img.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);

                        	targetW =  img.getMeasuredWidth();;
            				targetH =  img.getMeasuredHeight();
            				
            				personalChat.targetWidth = targetW;
            				personalChat.targetHeight = targetH;
            				
                    	}
                    	else
                    	{
                        	targetW =  personalChat.targetWidth;
            				targetH = personalChat.targetHeight;
                    	}
                    	 if(time != null)
                         {
                    		 time.setText(msg.getTime());
                         }
                    	 

        				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        				bmOptions.inJustDecodeBounds = true;
        				
        				BitmapFactory.decodeByteArray(msg.getBy() , 0, msg.getBy().length, bmOptions);
        				int photoW = bmOptions.outWidth;
        				int photoH = bmOptions.outHeight;
        				
        				  
        				// Determine how much to scale down the image
        				int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        				
        				//System.out.println("나~~~\ntargetW : " + targetW +"  "
//							     + "targetH : " + targetH+"  "
//							     + "photoW : " + photoW+"  "
//							     + "photoH : " + photoH+"  ");
//        				
//        				//Log.d("inSampleSize", scaleFactor+"");
        				  
        				// Decode the image file into a Bitmap sized to fill the View
        				bmOptions.inJustDecodeBounds = false;
        				bmOptions.inSampleSize = scaleFactor;
        				bmOptions.inPurgeable = true;
        				  
        				Bitmap bitmap =BitmapFactory.decodeByteArray(msg.getBy() , 0, msg.getBy().length, bmOptions);
        				img.setImageBitmap(bitmap);
        				//bitmap.recycle();
                    }
                    img.setOnClickListener(new OnClickListener() {
                    	
						@Override
						public void onClick(View v) 
						{
							
							Intent intent = new Intent(getApplicationContext(), Over.class);
				               intent.putExtra("position", posi);
				               intent.putExtra("type", 1);
				               startActivity(intent);
							
						}
					});
 
				}
				else if(type == 4)
				{
					  ImageView profile_img = (ImageView) view.findViewById(R.id.profile_img);
					  TextView user_id = (TextView) view.findViewById(R.id.user_id);
					  ImageView img = (ImageView)view.findViewById(R.id.img);
					  TextView time = (TextView) view.findViewById(R.id.time);
					  final String nickname = msg.getUser_id();
					  
					  Bitmap sizingBmp;
					  Bitmap sizingBmp2;
					  
					  try
						{
						  sizingBmp = Bitmap.createScaledBitmap(MainActivity.NICK_and_BITMAP.get(nickname), 70, 70, true);
						  sizingBmp2 = Bitmap.createScaledBitmap(MainActivity.NICK_and_BITMAP.get(nickname), Profile.pic_w, Profile.pic_w, true);
						}
						catch(NullPointerException ne)
						{
							Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.person);
							sizingBmp = Bitmap.createScaledBitmap(bm, 70, 70, true);
							sizingBmp2 = Bitmap.createScaledBitmap(bm, Profile.pic_w, Profile.pic_w, true);
						}
					  
					  final RoundedAvatarDrawable tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(sizingBmp);
					  final RoundedAvatarDrawable tmpRoundedAvatarDrawable2 = new RoundedAvatarDrawable(sizingBmp2);
					  final int posi = pos;
					  
                    if (user_id != null)
                    {
                  	  user_id.setText(msg.getUser_id());                            
                    }
                    if(img != null)
                    {
                    	 int targetW;
                    	 int targetH;
                    	 if(personalChat.targetWidth == -5 || personalChat.targetHeight == -5)
                    	 { 
                    		img.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);

                        	targetW =  img.getMeasuredWidth();;
            				targetH =  img.getMeasuredHeight();
            				
            				personalChat.targetWidth = targetW;
            				personalChat.targetHeight = targetH;
            				
                    	 }
                    	 else
                    	 {
                        	targetW =  personalChat.targetWidth;
            				targetH = personalChat.targetHeight;
                    		
                    	 } 
	                	 if(time != null)
	                     {
	                		 time.setText(msg.getTime());
	                     }
                    	
        				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        				bmOptions.inJustDecodeBounds = true;
        				
        				BitmapFactory.decodeByteArray(msg.getBy() , 0, msg.getBy().length, bmOptions);
        				int photoW = bmOptions.outWidth;
        				int photoH = bmOptions.outHeight;
        				//System.out.println("남~~~\ntargetW : " + targetW +"  "
//        									     + "targetH : " + targetH+"  "
//        									     + "photoW : " + photoW+"  "
//        									     + "photoH : " + photoH+"  ");
        				  
        				// Determine how much to scale down the image
        				int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        				  
        				//Log.d("inSampleSize", scaleFactor+"");
        				// Decode the image file into a Bitmap sized to fill the View
        				bmOptions.inJustDecodeBounds = false;
        				bmOptions.inSampleSize = scaleFactor;
        				bmOptions.inPurgeable = true;
        				  
        				Bitmap bitmap =BitmapFactory.decodeByteArray(msg.getBy() , 0, msg.getBy().length, bmOptions);
        				img.setImageBitmap(bitmap);
        				//bitmap.recycle();
                    }
                    
                    if (profile_img != null)
                    {
               	  
//                   	 //System.out.println("in type4 user_id : " + nickname);
                   	 
                  	  if(MainActivity.NICK_and_BITMAP.get(nickname) != null)
                  	  {
                  		  profile_img.setImageDrawable(tmpRoundedAvatarDrawable);
                  	   }
                  	  else
                  	  {
                  		  //System.out.println("해당 유저는 프로필 사진을 등록하지 않았습니다.");
                  	  }
                    }
                    
                    
                    	profile_img.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) 
						{
							
							personalChat.detect_profile_touch = true;
						    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						    imm.hideSoftInputFromWindow(personalChat.et.getWindowToken(), 0);
						    
						    personalChat.pro_pic_touch.setVisibility(View.VISIBLE);
						    personalChat.chatting_view.setClickable(false);
						    
						    personalChat.add_friend_profile.setImageDrawable(tmpRoundedAvatarDrawable2);
						    
						    personalChat.pro_pic_touch.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									//System.out.println("배경이 터치되었습니다.");
								}
							});
						    
						    
						    personalChat.add_friend_btn.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									
									personalChat.pro_pic_touch.setVisibility(View.INVISIBLE);
									//System.out.println("버튼이 클릭되었습니다.");
									
									personalChat.detect_profile_touch = false;

									JSONObject Packet2 = new JSONObject();
									Packet2.put("Command", "friend_request");
									Packet2.put("nickname",nickname);
									String pac2 = Packet2.toJSONString();
									
									MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
									MainActivity.out.println(pac2); //실험!!!!
									MainActivity.out.flush();
									
								}
							});
						    
						    
//						   Intent intent = new Intent(Allchat.this, Over_profile_pic.class);
//			               intent.putExtra("nickname", nickname);
//			               startActivity(intent);
							
//							   Intent intent = new Intent(Allchat.this, add_friends.class);
//				               intent.putExtra("nickname", nickname);
//				               startActivity(intent);
						
						}
					});
 
                    img.setOnClickListener(new OnClickListener() {
                    	
						@Override
						public void onClick(View v) 
						{
							
							Intent intent = new Intent(getApplicationContext(), Over.class);
				               intent.putExtra("position", posi);
				               intent.putExtra("type", 1);
				               startActivity(intent);
							
						}
					});
                    
				}
			}
			
			return view;
		}
	}
	
	public void completelogin()
	{
		MainActivity.LoginPage.setVisibility(View.INVISIBLE);
        MainActivity.MainPage.setVisibility(View.VISIBLE);
	}
	
	public void login(View view){

		ut.DialogProgress(MainActivity.this);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			new loginTophp("http://115.71.237.99/chatchat/login.php", username.getText().toString(), password.getText().toString(),MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
        else
        	new loginTophp("http://115.71.237.99/chatchat/login.php", username.getText().toString(), password.getText().toString(),MainActivity.this).execute((Void[])null);

		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); // 키패드에 자동으로 포커스가 주는것 방지하는 코드! 
		imm.hideSoftInputFromWindow(username.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
		
   }
	
	
	class chatTask extends AsyncTask< Void, Void , Void>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			//System.out.println("서버와 연결중!");
			doInBackground();
			
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			//System.out.println("여기는 onProgressUpdate()");
			//ChatRoom.Adapter.notifyDataSetChanged();;
			MainActivity.adapter.add(mess);
			Allchat.lv.smoothScrollToPosition(MainActivity.adapter.getCount());
			MainActivity.adapter.notifyDataSetChanged();
			
			pass = true;
			pleaseWait = true;
			
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			//System.out.println("doInBackground");
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			int screenWidth = metrics.widthPixels;
			int screenHeight = metrics.heightPixels;
			
			
			//Log.d("screenWidth", screenWidth+"");
			//Log.d("screenHeight", screenHeight+"");
			
			Profile.pic_w = screenWidth/3;
			Profile.pic_h = screenWidth/3;
			
			try
			{
				setSocket(ip,port);
				//Log.d("DMSG", "OK");
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
				//Log.d("DMSG", "Init fail..");
				//Log.d("DMSG", "Error : " + e1);
			}
			
			new Thread(new Runnable() 
			{
				
				@Override
				public void run() 
				{
					try 
					{
						while(true)
						{
							Thread.sleep(5*1000);
							JSONObject Packet2 = new JSONObject();
							Packet2.put("Command", "nothing");
							String pac2 = Packet2.toJSONString();
							
							if(MainActivity.out != null)
							{
								MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
								MainActivity.out.println(pac2);
								MainActivity.out.flush();								
							}
							else
							{
								//System.out.println("null 값");
							}
//								
//								try {
//									
//									networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//									networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//								
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//								
//								if(out != null)
//								{
//									out = new PrintWriter(MainActivity.networkWriter, true);
//									MainActivity.out.println(pac2);
//									MainActivity.out.flush();
//								}
//								else
//								{
//									//System.out.println("또 null값");
//								}
//							}
						}
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
			}).start();
			
			try
			{
				while(true)
				{
					Log.w("DMSG", "Chatting is running" );
					
					try
					{
						Thread.sleep(1000);
					}
					catch(Exception e)
					{
						Log.w("DMSG", "Error : " + e);
					}
					read = null;
					
					while((read = networkReader.readLine()) != null)
					{
							//Log.d("받아온 msg", read);
							
							JSONParser jsonParser = new JSONParser();
							final JSONObject packet = (JSONObject)jsonParser.parse(read);
							
							
								if(packet.get("Command").equals("yourIp")) // 처음에 접속을 하면 내 아이피(ID)를 서버로 부터 받는다.
								{
									//String str[] = read.split(",");
									//MyID = str[1];
									MyIp = (String)packet.get("Ip");
									//System.out.println("MYIp : " + MyIp);
									
									//Log.d("chk_login.getBoolean(\"login\", false)", chk_login.getBoolean("login", false)+"");

									if(chk_login.getBoolean("login", false) == true) // 로그인이 되있는 상태.
										{
											ifLOGIN();
										}
									
								}
								
								if(packet.get("Command").equals("sendRoomKey")) // 키를 받아오면 방 제목이랑 같이 저장. 그리고 나서 tempTitle을 초기화.
								{
									
									String key = (String)packet.get("KEY");
									String Cur_Peo = (String)packet.get("Cur_Peo");
									String Max_Peo = (String)packet.get("Max_Peo");
									
									//System.out.println("서버로 부터 받아온 key : "+key);
									MainActivity.currentKey = key+""; // currentKey에 서버로 부터 받아온 key를 저장함으로써 현재 채팅하고 있는 키가 무엇인지 정의
									//System.out.println("ChatRoom.tempTitle : " +ChatRoom.tempTitle); //tmp에 현재 생성된 방의 제목을 정의하기 위해서 만든건대 여기서는 확인용으로 print해봄
									tmp = ChatRoom.tempTitle;
									
									
									ChatRoom.TitleKey.put(tmp, Integer.parseInt(key));
									//System.out.println("TitleKey 의 값 : " + ChatRoom.TitleKey.get(tmp));

									runOnUiThread(new Runnable() {
										
										  public void run() {
											  createChatRoom(tmp);										  
											  }
										});
									
									ChatRoom.tempTitle="";
									
								}
								
								if(packet.get("Command").equals("nothing")) //
								{
									JSONObject Packet2 = new JSONObject();
									Packet2.put("Command", "check_network");
									String pac2 = Packet2.toJSONString();
									
									MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
									MainActivity.out.println(pac2); //실험!!!!
									MainActivity.out.flush();
								}
								
								if(packet.get("Command").equals("sendRoomKey2")) //
								{
									
									final String key = (String)packet.get("KEY");
									final String f_name = (String)packet.get("f_name");
									
									//System.out.println("서버로 부터 받아온 key : "+key);
									MainActivity.currentKey =  key; // currentKey에 서버로 부터 받아온 key를 저장함으로써 현재 채팅하고 있는 키가 무엇인지 정의
									MainActivity.state = key;
									
									runOnUiThread(new Runnable() {
										
										  public void run() {
											  createChatRoom2(f_name, key);										  
											  }
										});
									
								}
								
								if(packet.get("Command").equals("f_Room")) //
								{
									
									String key = (String)packet.get("KEY");
									String member = (String)packet.get("member");
									
									//System.out.println("서버로 부터 받아온 key : "+key);
								}
								
								
								if(packet.get("Command").equals("RoomList")) // 기존의 존재했던 방을 만든다.
									{
										//System.out.println("기존의 방을 만들어!");
										
										Runnable RoomList = new Runnable() { 

											public void run() {
												
												runOnUiThread(new Runnable() {
													
													  public void run() {
														  
														  JSONArray jarr = (JSONArray) packet.get("RoomList");
															
															//System.out.println("jarr : " + jarr);
															
															for(int i=0;i<jarr.size();i++)
															{
																//System.out.println("jarr[" + i + "] : " + jarr.get(i));
																String str = (String) jarr.get(i);
																//System.out.println("str : " + str);
																
																String str2[] = str.split(",");
																MainView mv = new MainView(str2[0], Integer.parseInt(str2[1]), Integer.parseInt(str2[2])); // chatRoom 화면에 띄울 객체를 생성
																ChatRoom.mainView.add(mv);// 이렇게 넣어놨다가 나중에 adapter.add로 바꿔도 보자!			
															}
															    ChatRoom.Madapter.notifyDataSetChanged();
																//System.out.println("runOnUiThread 끝");
															}
													});
											}
										};
										new Thread(RoomList).start();
									}
								
								if(packet.get("Command").equals("broadcastRoom")) // 다른 사용자가 만든 방을 내 폰에도 만들어랏.
								{
									//System.out.println("방을 만들어!!!");
									
									final String MaxP = (String)packet.get("Max_Peo");
									final String CurP = (String)packet.get("Cur_Peo");
									final String Title = (String)packet.get("Title");
									
									//System.out.println(" In MainActivity , At broadcastRoom // MaxP : " + MaxP +" CurP : " + CurP + "  Title : " + Title);

									Runnable makingRoom = new Runnable() { 

										public void run() {
											
											runOnUiThread(new Runnable() {
												
												  public void run() {
													  MainView mv = new MainView(Title, Integer.parseInt(CurP), Integer.parseInt(MaxP)); // chatRoom 화면에 띄울 객체를 생성
														ChatRoom.mainView.add(mv);// 이렇게 넣어놨다가 나중에 adapter.add로 바꿔도 보자!
														ChatRoom.Madapter.notifyDataSetChanged();
														//System.out.println("runOnUiThread 끝");
														}
												});
										}
									};
									new Thread(makingRoom).start();
								}
								
								if(packet.get("Command").equals("Enterance")) // 누구누구 님이 입장하셨습니다. 
								{
									String inviter = (String)packet.get("inviter");
									String key = (String)packet.get("key");
									String msg = (String)packet.get("msg");
									
									Long aa = Long.parseLong(key);
									
									if(aa > 10000)
									{
										//Log.d("key", key);
										//Log.d("state", MainActivity.state);
										if(MainActivity.state.equals(key))
										{
											//Log.d("In Enterance", "key와 state가 같다.");
											MainActivity.dbManager.sql_query("insert into chat_log(chat_sequence, state, msg, created_date, type) values('"+key+"',1, '" + msg + "',datetime('now','localtime'),1);");
										}
										else
										{
											//Log.d("In Enterance", "key와 state가 다르다.");
											MainActivity.dbManager.sql_query("insert into chat_log(chat_sequence, state, msg, created_date, type) values('"+key+"',0, '" + msg + "' ,datetime('now','localtime'),1);");
										}
											//System.out.println("inviter : " + inviter);
											//System.out.println("In personalChat's msg : "+msg);
											
											if(inviter.equals(MainActivity.MyNickname))
											{
												offical = new Message(1,msg);
												
												runOnUiThread(new Runnable(){
													@Override
													public void run() 
													{
														//System.out.println("runOnUi 실행댐!!");
														MainActivity.adapter2.add(offical);
														try
														{
															personalChat.lv.smoothScrollToPosition(MainActivity.adapter2.getCount());
														}
														catch(NullPointerException ne)
														{
															
														}
														MainActivity.adapter2.notifyDataSetChanged();
													}
												});									
											}
											
									}
									
									else
									{
//										Allchat.msg = (String)packet.toJSONString();
										
										try
										{
											if(packet.get("Command").equals("Enterance") && MainActivity.currentKey.equals(key))
											{
												String msg2 = (String)packet.get("msg");
												//System.out.println("In Allchat's msg : "+msg2);
												offical = new Message(1,msg2);
												
												runOnUiThread(new Runnable(){
													@Override
													public void run() {
														//System.out.println("runOnUi 실행댐!!");
														MainActivity.adapter.add(offical);
														try
														{
															Allchat.lv.smoothScrollToPosition(MainActivity.adapter.getCount());
															MainActivity.adapter.notifyDataSetChanged();

														}
														catch(NullPointerException e)
														{
															//System.out.println("NullPointException!");
														}
													}
												});
											}
											
											
										}
										catch(Exception e)
										{
											//System.out.println("여기서 오류났어....");
										}
									}
									
									
								}
								
								if(packet.get("Command").equals("Chatkey"))
								{
//									String str[] = read.split(","); //str[1] = key, str[2] = 방 제목.
//									//System.out.println("서버로 부터 넘어온 key 값 : " + str[1]);
									
									String key = (String)packet.get("KEY");
									String Title = (String)packet.get("title");
									
									ChatRoom.TitleKey.put(Title,Integer.parseInt(key)); // 받아온 key와 방 제목을 TitleKey에 저장
									
									currentKey =  key+""; // currentKey를 사용하여 메시지를 보낼때 항상 같이 보냄으로써 어떤 키를 가지고 있는지 판별!
									roomT = Title; //방 제목
									
									//System.out.println("roomT의 값 : " + roomT);
					
									runOnUiThread(new Runnable() {
										
										  public void run() {
											  createChatRoom(roomT);									  
											  }
										});
								}
								
								if(packet.get("Command").equals("accepted_friend")) //내가 친구 요청을 한 상대방이 나를 친구로 수락했을때!
								{
									final String nick = (String)packet.get("nick");
									final String path = (String)packet.get("path");
									final String state = (String)packet.get("state");
									final String msg_seq = (String)packet.get("msg_seq");
									
									JSONObject Packet2 = new JSONObject();
									Packet2.put("Command", "received_msg");
									Packet2.put("msg_seq",msg_seq);
									String pac2 = Packet2.toJSONString();
									
									MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
									MainActivity.out.println(pac2); 
									MainActivity.out.flush();
									
					    			runOnUiThread(new Runnable() {
									
									  public void run() 
									  {
										  	//Log.d("IN MainActivity, path", path+"");
											//Log.d("IN MainActivity, nick", nick+"");

											if(path != null && !path.equals("no"))
											{
												//Log.d("IN MainActivity"," path = null??" + path);
												runOnUiThread(new Runnable(){
													@Override
													public void run() 
													{
														 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
												            	new download_Profile_thumbnail_Image(path, nick, state,3)
														 		.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
												            else
												            	new download_Profile_thumbnail_Image(path, nick, state, 3).execute((Void[])null);
														 
														 friends_list friends = new friends_list(path, nick, state);
														 MainActivity.f_list_adapter.add(friends);
														 
														 req_f_count.setText(1+"");
														 req_f_count.setVisibility(View.VISIBLE);
														 Toast.makeText(getApplicationContext(), nick+"님이 친구요청을 수락하셨습니다.", Toast.LENGTH_SHORT).show();
														 
													}
												});	
											}
											else
											{
//												Bitmap default_img = BitmapFactory.decodeResource(getResources(), R.drawable.person);
//												Bitmap target_bmp = Bitmap.createScaledBitmap(default_img,100, 100, true);
//												RoundedAvatarDrawable tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(target_bmp);
//												
//												FriendView2 friend = new FriendView2(tmpRoundedAvatarDrawable, nick, state);
												
												friends_list friends = new friends_list("no", nick, state);
												
							              		f_list_adapter.add(friends);
							              		 
							              		runOnUiThread(new Runnable() {
													
													  public void run() 
													  {
														  Toast.makeText(getApplicationContext(), nick+"님이 친구요청을 수락하셨습니다.", Toast.LENGTH_SHORT).show();
														  
														  req_f_count.setVisibility(View.VISIBLE);
														  req_f_count.setText(1+"");
														  Fadapter.notifyDataSetChanged();
													  }
													});
											}
									  }
									});
					    			
					    			String friend_list = MainActivity.chk_login.getString("friends", "[]"); // jsonString 형태이겠쥐.
									//Log.d("MainActivity", "friend_list : " + friend_list);
									 
									 JSONArray friends = new JSONArray();
									 JSONObject  job = new JSONObject();
									 JSONParser jParser = new JSONParser();
										
									 if(friend_list.equals("[]"))// 아직 친구가 존재하지 않았던 경우.
									 {
										
										job.put("nick", nick);
										job.put("path", path);
										job.put("state_msg", state);
										friends.add(job);
										
										//System.out.println("친구 요청 리스트 생성!(json)");
					              		
					              		
					              		String list = friends.toJSONString();
					              		//Log.d("In MainActivity", "로컬에 친구 리스트 저장 확인 : " + list);
					              		MainActivity.editor.putString("friends", list);
					              		MainActivity.editor.commit();
					              		
									 }
									 else // 친구가 존재한 경우.
									 {
										 try {
											friends = (JSONArray)jParser.parse(friend_list);
											job.put("nick", nick);
											job.put("path", path);
											job.put("state_msg", state);
											friends.add(job);
											
						              		 String list = friends.toJSONString();
						              		 //Log.d("In MainActivity", "로컬에 친구 리스트 저장 확인 : " + list);
						              		 MainActivity.editor.putString("friends", list);
						              		 MainActivity.editor.commit();
							              		
										} catch (ParseException e) {
											e.printStackTrace();
										}
										
									 }
								}
								
								if(packet.get("Command").equals("add_friend_request_list"))
								{
									
									String Requester_nick = (String)packet.get("Requester");
									String Requseter_path = (String)packet.get("path");
									String Requseter_Seq = (String)packet.get("Seq");
									String msg_seq = (String)packet.get("msg_seq");
									
									JSONObject Packet2 = new JSONObject();
									Packet2.put("Command", "received_msg");
									Packet2.put("msg_seq",msg_seq);
									String pac2 = Packet2.toJSONString();
									
									MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
									MainActivity.out.println(pac2); 
									MainActivity.out.flush();
									
									//Log.d("Requester_nick", Requester_nick+"");
									RoundedAvatarDrawable tmpRoundedAvatarDrawable;
									
//									if( MainActivity.NICK_and_BITMAP.get(Requester_nick) == null)
//									{
										//System.out.println("in add_friend_request_list, null...");
//									}
//									else
//									{
//										 //System.out.println("null은 아냐");
										if( MainActivity.NICK_and_BITMAP.get(Requester_nick) != null)
										{
										 Bitmap target_bmp = Bitmap.createScaledBitmap(MainActivity.NICK_and_BITMAP.get(Requester_nick),100, 100, true);
										 tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(target_bmp);
										}
										else
										{
											 Bitmap default_img = BitmapFactory.decodeResource(getResources(), R.drawable.person);
											 Bitmap target_bmp = Bitmap.createScaledBitmap(default_img,100, 100, true);
											 tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(target_bmp);
										}
//										 String re_f_thumnail_path = ut.SaveBitmapToFileCache(target_bmp, ex_storage+"/"+"chatchat_req_profile_thumbnail"+"/", Requester_nick+".jpg");
										 
										 //Log.d("MainActivity", "Requseter_path : " + Requseter_path);
										 
										 String req_friend_list = chk_login.getString("req_friends", "[]"); // jsonString 형태이겠쥐.
										 
										 //Log.d("MainActivity", "req_friend_list : " + req_friend_list);
										 
										 
										 JSONArray req_friends = new JSONArray();
										 JSONObject  job = new JSONObject();
										 JSONParser jParser = new JSONParser();
											
										 if(req_friend_list.equals("[]"))// 아직 친구요청이 존재하지 않았던 경우.
										 {
											
											job.put("nick", Requester_nick);
											job.put("path", Requseter_path);
											req_friends.add(job);
											
											//System.out.println("친구 요청 리스트 생성!(json)");
											
											FriendView friend = new FriendView(tmpRoundedAvatarDrawable, Requester_nick);
						              		Fadapter.add(friend);
						              		
						              		runOnUiThread(new Runnable() {
												
												  public void run() 
												  {
													  friend_recommend_icon.setIcon(getResources().getDrawable(R.drawable.on));
													  //Log.d("in runOnUi", "실행되..나?");
												  }
												});
						              		jarrStr = req_friends.toJSONString();
											 
											 
											 //db에 친구리스트를 업로드해야함.
											 
											 sibalFInal_str = Requester_nick; //final 하기 싫어서 만듬.
											 
											 String insertDB = Requseter_Seq+","+MySeq+","+"0";
											 
											 new httpRequest(insertDB, jarrStr, "save_req_friend_list.php", editor,1).start(); // db에 업로드, 로컬에 저장.
											 
						              		
										 }
										 else
										 {
											 boolean passa = true;
											 req_friends = (JSONArray)jParser.parse(req_friend_list);
											 job.put("nick", Requester_nick);
											 job.put("path", Requseter_path);
											 
											for(int i=0;i<req_friends.size();i++) // 겹치는지 검사.
											{
												JSONObject j = (JSONObject)req_friends.get(i);
												
												if(j.get("nick").equals(job.get("nick")))
												{
												   passa = false;
												   break;
												}
											}
											
											if(passa == true)
												{
													req_friends.add(job);
													passa = false;
													//System.out.println("친구가 겹치지 않습니다.!");
													
													FriendView friend = new FriendView(tmpRoundedAvatarDrawable, Requester_nick);
 								              		Fadapter.add(friend); // 화면에 동기화
								              		 
								              		runOnUiThread(new Runnable() { //리스트뷰 변화 감지, 빨간색 알림.
														
														  public void run() 
														  {
															  friend_recommend_icon.setIcon(getResources().getDrawable(R.drawable.on));
															  //Log.d("in runOnUi", "실행되..나?");
														  }
														});
								              		
								              		jarrStr = req_friends.toJSONString();
													 
													 
													 //db에 친구리스트를 업로드해야함.
													 
													 sibalFInal_str = Requester_nick; //final 하기 싫어서 만듬.
													 String insertDB = Requseter_Seq+","+MySeq+","+"0";
													 new httpRequest(insertDB, jarrStr, "save_req_friend_list.php", editor,1).start(); // db에 업로드, 로컬에 저장.
												}
											else
											{
												//System.out.println("친구가 겹쳐 추가되지 않습니다.");
											}
											 
											 //System.out.println("친구 요청 리스트에 친구 추가!(json)");
										 }
								}
								
								if(packet.get("Command").equals("target_profile_url"))
								{
									final String url = (String)packet.get("url");
									
									System.out.println("In Mainactivity, target_profile_url, url : " + url);
									
									runOnUiThread(new Runnable(){
										@Override
										public void run() 
										{
											 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
									            	new download_Target_Profile_Image(url)
											 		.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
									            else
									            	new download_Target_Profile_Image(url).execute((Void[])null);
										}
									});	
								}
								
								if(packet.get("Command").equals("download_user_thumbnail"))
								{
									
									final String nickname = (String)packet.get("nickname");
									final String path = (String)packet.get("path");
									
									runOnUiThread(new Runnable() {
										
										  public void run() 
										  {
											  	//Log.d("IN download_user_thumbnail, path", path+"");
												//Log.d("IN download_user_thumbnail, nickname", nickname+"");

												if(!path.equals("null") && !path.equals("no") && !path.equals("nothing") && !path.equals("not_exit") && (path != null))
												{
													runOnUiThread(new Runnable(){
														@Override
														public void run() 
														{
															 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
													            	new download_Profile_thumbnail_Image(path, nickname,1)
															 		.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
													            else
													            	new download_Profile_thumbnail_Image(path, nickname,1).execute((Void[])null);
														}
													});	
												}
												else
												{
													//System.out.println("해쉬맵에 null이 들어감.");
													NICK_and_BITMAP.put(nickname, null); // 원래는 Bitmap 객체가 들어가야되지만. 등록된 path가 없다면 null을 저장.
												}
										  }
										});
								}
								
								if(packet.get("Command").equals("USER") || packet.get("Command").equals("PICTURE")) // 서버로 부터 들어온 메시지를 처리! (텍스트나 사진)
								{
									
									Seq.add(packet); // 서버로 부터 받은 메시지들은 일단 무조건 Seq arraylist에 들어간다. (모든 기기들과 싱크를 맞추기 위해서! 또 중간에 처리되다가 누실되는
															// 데이터나 변질되는 데이터를 막기 위하여!) Seq에 0번째 인덱스에 해당하는 패킷을 처리하고 0번째 인덱스의 값을 삭제한다. 
															// Queue 방식! first In first Out! 
																
									////System.out.println("등록되는 packet = " +packet);
//									//System.out.println("seq 등록!");
									
									while(Seq.size() != 0 && pleaseWait == true)
									{
										pleaseWait = false;
										final JSONObject Packet = Seq.get(0);
										Seq.remove(0);
										
										synchronized (Packet) 
										{
//											//System.out.println("In synchronized");
											if(Packet.get("Command").equals("USER"))
											{
												//System.out.println("In user");
												
												String msg = (String)Packet.get("MSG");
												String user = (String)Packet.get("user");
												
												String time = currentTime();
												
												if(user.equals(MainActivity.MyNickname))
												{
													  mess = new Message(0,user,msg, time);
												}
												else
												{
													 mess = new Message(2,user,msg, time);
												}
													
													pass = false;
													publishProgress();
													
													while(true)
													{
														if(pass = true && pleaseWait == true) break;
														
													}
														
											}
											
											if(Packet.get("Command").equals("PICTURE"))
											{
												
												String fileURI = (String)packet.get("URI");
												String userID = (String)Packet.get("user");
												
//												//Log.d("fileURI", fileURI+"");
//												//Log.d("userID", userID+"");
												
												String time = currentTime();
												
												if(userID.equals(MainActivity.MyNickname))
												{
													
													if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
										            	new downloadImage(fileURI, 3, userID, time,0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
										            else
										            	new downloadImage(fileURI, 3, userID, time,0).execute((Void[])null);
													
													 //mess = new Message(3,userID,rece_byte);
												}
												
												else
												{
													if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
										            	new downloadImage(fileURI, 4, userID, time,0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
										            else
										            	new downloadImage(fileURI, 4, userID, time,0).execute((Void[])null);
															 //mess = new Message(4,userID,rece_byte);
												}
												
													
													//publishProgress();
											}
											
											while(true)
											{
												if(pass = true && pleaseWait == true) break;
												else
												{
//													//Log.d("pass", pass+"");
//													//Log.d("pleaseWait", pleaseWait+"");
												}
											}
											
											if(Seq.size() > 0) 
												{
//													//System.out.println("pass = " + pass);
//													//System.out.println("pleaseWait = " + pleaseWait);
													continue;
												}
										} 
										
//										//System.out.println("while 실행!");

									}
									
//									//System.out.println("while 탈출!");
								}
								
								if(packet.get("Command").equals("DELETEROOM"))
								{
									String target = (String)packet.get("Target");
									//ChatRoom.mainView.remove(ChatRoom.mainView.get);
									for(int i=0;i<ChatRoom.mainView.size();i++)
									{
										if(ChatRoom.mainView.get(i).getTitle().equals(target))
										{
											ChatRoom.mainView.remove(i);
										}
									}
									
									runOnUiThread(new Runnable(){
										@Override
										public void run() 
										{
											ChatRoom.Madapter.notifyDataSetChanged();
										}
									});	
									
								}
								
								if(packet.get("Command").equals("user_profile_path_info")) // 서버로부터 받아온 path를 이용해 user의 프로필 사진(thumbnail)을 다운 받는다.
								{
									JSONObject pathinfo = (JSONObject)packet.get("user_profile_path_info");
									
									JSONArray jarr = (JSONArray) pathinfo.get("user_profile_path");
									
									for(int i=0;i<jarr.size();i++)
									{
										JSONObject job = (JSONObject) jarr.get(i);
										
										final String path = (String)job.get("path");
										final String nickname = (String)job.get("nickname");
										
										//Log.d("path", path+"");
										//Log.d("nickname", nickname+"");

										if(!path.equals("null") && !path.equals("no") && !path.equals("nothing") && !path.equals("not_exit") && (path != null))
										{
											runOnUiThread(new Runnable(){
												@Override
												public void run() 
												{
													 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
											            	new download_Profile_thumbnail_Image(path, nickname,1)
													 		.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
											            else
											            	new download_Profile_thumbnail_Image(path, nickname,1).execute((Void[])null);
												}
											});	
										}
										else
										{
											//System.out.println("해쉬맵에 null이 들어감.");
											NICK_and_BITMAP.put(nickname, null); // 원래는 Bitmap 객체가 들어가야되지만. 등록된 path가 없다면 null을 저장.
										}
									}
									
									
									
//									Iterator<JSONArray>iterator = jarr.iterator();
//				                    //System.out.println("**JsonList**");
//				                    while(iterator.hasNext()) {
//				                    	
//				                           //System.out.println(iterator.next());
//				                    }
								}
								
								 if(packet.get("Command").equals("change_Cur_Peo"))
								{
									String title = (String)packet.get("title");
									String Cur_People = (String)packet.get("Cur_People");
									
									for(int i=0;i<ChatRoom.mainView.size();i++)
									{
										if(ChatRoom.mainView.get(i).getTitle().equals(title)) // 만약 현재 사람의 값을  변경해야하는 채팅방의 값과 넘겨받은 값이 같다면!
										{
											ChatRoom.mainView.get(i).setCur_people(Integer.parseInt(Cur_People));
											runOnUiThread(new Runnable() {
												
												  public void run() {
													  ChatRoom.Madapter.notifyDataSetChanged();
												  }
												});
										}
									}
								}
								 
								 if(packet.get("Command").equals("noExtra")) 
									{
									 	//System.out.println("In noExtra ");
									 	
									 	runOnUiThread(new Runnable() {
											
											  public void run() {
												 	Toast.makeText(getApplicationContext(), "더 이상 대화방에 참여할 수 없습니다.", Toast.LENGTH_LONG).show();
											  }
											});
									}
							}
					
					if((read = networkReader.readLine()) == null)
					{
						//System.out.println("networkReader == null");
						break;
					}
				}
			}
			catch(Exception e)
			{
				Log.w("DMSG", "Client Thread Error! : " + e);
				e.printStackTrace();
				
				//Log.d("DMSG", "Socket close....ok");
				
				
				try {
					if(MainActivity.socket != null)
					{
						MainActivity.socket.close();
						MainActivity.out.close();
						//System.out.println("연결 해지...??");
					}
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
					//Log.d("Error", "MainActivity.socket.close();");
				}
				
			}
			finally
			{
				
			}
			
			return null;
		}



		
    }
	
//	 public String PrintData(String key) {
//	        SQLiteDatabase db = MainActivity.dbManager.getReadableDatabase();
//	        String str = "";
//	         
//	        Cursor cursor = db.rawQuery("select msg from chat_log where chat_sequence ="+"'"+key+"'", null);
//	        while(cursor.moveToNext()) {
//	            str= cursor.getString(0);
//	            
//	            //Log.d("PrintData", str);
//	        }
//	         
//	        return str;
//	    }
	
	 public void PrintData() {
	        SQLiteDatabase db = MainActivity.dbManager.getReadableDatabase();
	        String str = "";
	         
	        Cursor cursor = db.rawQuery("select * from chat_log", null);
	        while(cursor.moveToNext()) {
	            str= cursor.getInt(0)+","+cursor.getString(1)+","+cursor.getString(2)+","+cursor.getString(3)+","+cursor.getString(4)
	            		+","+cursor.getString(5)+","+","+cursor.getInt(6);
	            
	            //Log.d("PrintData", str);
	        }
	    }

	 
	 private boolean checkPlayServices() {
	        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	        if (resultCode != ConnectionResult.SUCCESS) {
	            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
	            } else {
	                Log.i("ICELANCER", "This device is not supported.");
	                finish();
	            }
	            return false;
	        }
	        return true;
	    }
	 
	 private String getRegistrationId(Context context) {
	        final SharedPreferences prefs = getGCMPreferences(context);
	        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	        if (registrationId.isEmpty()) {
	            Log.i(TAG, "Registration not found.");
	            return "";
	        }
	        
	        // 앱이 ㄹ 되었는지 확인하고, 업데이트 되었다면 기존 등록 아이디를 제거한다.
	        // 새로운 버전에서도 기존 등록 아이디가 정상적으로 동작하는지를 보장할 수 없기 때문이다.
	        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	        int currentVersion = getAppVersion(context);
	        if (registeredVersion != currentVersion) {
	            Log.i(TAG, "App version changed.");
	            return "";
	        }
	        return registrationId;
	    }

	    private SharedPreferences getGCMPreferences(Context context) {
	        return getSharedPreferences(MainActivity.class.getSimpleName(),
	                Context.MODE_PRIVATE);
	    }

	    private static int getAppVersion(Context context) {
	        try {
	            PackageInfo packageInfo = context.getPackageManager()
	                    .getPackageInfo(context.getPackageName(), 0);
	            return packageInfo.versionCode;
	        } catch (PackageManager.NameNotFoundException e) {
	            // should never happen
	            throw new RuntimeException("Could not get package name: " + e);
	        }
	    }
	
	    private void registerInBackground() {
	        new AsyncTask<Void, Void, String>() {
	            @Override
	            protected String doInBackground(Void... params) {
	                String msg = "";
	                try {
	                    if (gcm == null) {
	                        gcm = GoogleCloudMessaging.getInstance(context);
	                    }
	                    regid = gcm.register(SENDER_ID);
	                    msg = "Device registered, registration ID=" + regid;

	                    // 서버에 발급받은 등록 아이디를 전송한다.
	                    // 등록 아이디는 서버에서 앱에 푸쉬 메시지를 전송할 때 사용된다.
	                   // sendRegistrationIdToBackend();

	                    // 등록 아이디를 저장해 등록 아이디를 매번 받지 않도록 한다.
	                    storeRegistrationId(context, regid);
	                } catch (IOException ex) {
	                    msg = "Error :" + ex.getMessage();
	                    // If there is an error, don't just keep trying to register.
	                    // Require the user to click a button again, or perform
	                    // exponential back-off.
	                }
	                return msg;
	            }

	            @Override
	            protected void onPostExecute(String msg) {
	            	
	            	
	            }

	        }.execute(null, null, null);
	    }

	    private void storeRegistrationId(Context context, String regid) {
	        final SharedPreferences prefs = getGCMPreferences(context);
	        int appVersion = getAppVersion(context);
	        Log.i(TAG, "Saving regId on app version " + appVersion);
	        SharedPreferences.Editor editor = prefs.edit();
	        editor.putString(PROPERTY_REG_ID, regid);
	        editor.putInt(PROPERTY_APP_VERSION, appVersion);
	        editor.commit();
	    }
	    
	    private void sendRegistrationIdToBackend() {

	    	runOnUiThread(new Runnable(){

				@Override
				public void run() {
					JSONObject Packet = new JSONObject();
					Packet.put("Command", "saveRegisterID");
					Packet.put("nickname", MyNickname);
					Packet.put("regid", regid);
				
					String pac = Packet.toJSONString();
					//Log.d("In sendRegistrationIdToBackend pac", pac+"");
					
					if(MainActivity.out != null)
					{
						MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
			    		MainActivity.out.println(pac);
			    		MainActivity.out.flush();
					}
					else
					{
						//System.out.println("Main.out == null");
					}
				}
			});
	    }
	    
	public String currentTime()
	{
		StringBuffer sb = new StringBuffer(new java.text.SimpleDateFormat("HHmm").format(new java.util.Date()));
		sb.insert(2, ":");
		
		if(Integer.parseInt(sb.substring(0, 2))  >= 12)
		{
			sb.append("pm");
		}
		else
		{
			sb.append("am");
		}
		return sb.toString();
	}
	
	public void createChatRoom(final String chatTitle) //채팅방을 만들어라
	{
		JSONObject packet = new JSONObject();
		packet.put("Command", "Enterance");
		packet.put("KEY", ChatRoom.TitleKey.get(chatTitle)+"");
		String pac = packet.toJSONString();
		
		MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
		MainActivity.out.println(pac);
		MainActivity.out.flush();
		
		Intent intent = new Intent(this, Allchat.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.putExtra("Title", chatTitle);
		startActivity(intent);
	}
	
	public void createChatRoom2(final String chatTitle, String key) //채팅방을 만들어라
	{
		JSONObject packet = new JSONObject();
		packet.put("Command", "Enterance2");
		packet.put("KEY", key);
		String pac = packet.toJSONString();
		
		MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
		MainActivity.out.println(pac);
		MainActivity.out.flush();
		
		Intent intent = new Intent(this, personalChat.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.putExtra("Title", chatTitle);
		startActivity(intent);
	}
	
	public void createChatRoom3(final String chatTitle, String key) //채팅방을 만들어라
	{
		//Log.d("createChatRoom3", "실행");
		Intent intent = new Intent(this, personalChat.class);
		intent.putExtra("Title", chatTitle);
		startActivity(intent);
	}
	
	
	public void setSocket(String ip, int port) 
	{
		try
		{
			socket = new Socket(ip,port);
			networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
			
			//Log.d("DMSG", "Client Socket init....");
		}
		catch(IOException e)
		{
			//System.out.println(e);
			
			e.printStackTrace();
			//Log.d("DMSG", "Client Socket Init fail");
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		
		friend_recommend_icon = menu.findItem(R.id.friend_Recommend);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.friend_Recommend) {
			
			friend_recommend_icon.setIcon(getResources().getDrawable(R.drawable.off));
			Intent intent = new Intent(getApplicationContext(), Requset_Friends.class);
			startActivity(intent);
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void ifLOGIN()
	{
		abar.show();
		
	  	String nickname = chk_login.getString("nickname", "fail");
    	MyNickname = nickname;
    	
    	String Seq = chk_login.getString("seq", "nothing");
        MySeq = Seq;
        
        String local_profilePATH = chk_login.getString("local_profilePATH", "nothing");
        String profile_thumbnail = chk_login.getString("profile_thumbnail", "nothing");
        String profile_url = chk_login.getString("Profile_Picture", "nothing");
        String friend_list = chk_login.getString("friends", "[]");
        String req_friend_list = chk_login.getString("req_friends", "[]");
        String chat_list = chk_login.getString("chat_list", "[]");
		String profile_File_name = chk_login.getString("profile_File_name", "no");
		//Log.d("profile_File_name", profile_File_name+"");

        
        JSONObject packet3 = new JSONObject();
		packet3.put("Command", "profile_thumbnail");
		packet3.put("profile_thumbnail", profile_thumbnail);
		packet3.put("nickname", MyNickname);
		packet3.put("profile_url", profile_url);
		String pac2 = packet3.toJSONString();
		
		MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
		MainActivity.out.println(pac2);
		MainActivity.out.flush();
		
		
		//System.out.println("regid 전송!");
		//System.out.println("regid : " + regid);
        sendRegistrationIdToBackend();
		
//		//System.out.println("======== mainactivity. 서버로 보낸 uri ============");
//		for(int i=0;i<profile_url.length();i++)
//		{
//			//System.out.println("profile_url["+i+"] : " + profile_url.charAt(i));
//		}
		
        
        //Log.d("local_profilePATH", local_profilePATH+"");
        if(local_profilePATH.equals("nothing"))
        {
        	server_profile_pic_path = chk_login.getString("Profile_Picture", "not_exit");
        	
        	//Log.d("In MainActivity, server_profile_pic_path", server_profile_pic_path);
//        	
        	if(server_profile_pic_path.equals("no") == false && server_profile_pic_path.equals("not_exit") == false)
        	{
        		final String str = server_profile_pic_path.substring(0, 21).concat(server_profile_pic_path.substring(22,server_profile_pic_path.length()));
        		
        		if("http://115.71.237.99/profile/3.jpg".equals(str))
        		{
        			//System.out.println("같다");
        		}
        		
        		runOnUiThread(new Runnable(){// 만약 db에 파일의 경로가 존재한다면 다운로드!
					// 그렇지 않다면 로컬 경로도 없고 서버 파일 경로도 없으므로 사용자가 업로드를 시도하지 않은 것임.

					@Override
					public void run() 
					{
						 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				            	new download_Profile_Image(str,
				            			MainActivity.MyNickname).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
				            else
				            	new download_Profile_Image(str,
				            			MainActivity.MyNickname).execute((Void[])null);
					}
				});
        	}
        }
        else
        {
        	Log.d("local_profilePATH", local_profilePATH);
        }
        
    	//Log.d("nickname", nickname+"");
    	//Log.d("seq", MySeq+"");
    	
    	JSONObject packet2 = new JSONObject();
		packet2.put("Command", "Save_nickname_and_ip");
		packet2.put("nickname", nickname+"");
		String pac = packet2.toJSONString();
		MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
		MainActivity.out.println(pac);
		MainActivity.out.flush();
		
		
		
		//친구요청에 영속성을 부여해야함.
		
		//Log.d("MainActivity", "req_friend_list : " + req_friend_list);
		
		if(req_friend_list.equals("[]")==false) // 친구요청자가 존재한다면!
		{
			JSONParser jsonParser2 = new JSONParser();
			
			try {
				
				JSONArray jarr2 = (JSONArray) jsonParser2.parse(req_friend_list);
				
				for(int i=0;i<jarr2.size();i++)
				{
					JSONObject j = (JSONObject)jarr2.get(i);
					//System.out.println("nick["+i+"] : " + (String)j.get("nick"));
					//System.out.println("path["+i+"] : " + (String)j.get("path"));
					//System.out.println("값["+i+"] : " + jarr2.get(i));
					
					final String reqest_nick = (String)j.get("nick");
					final String reqest_path = (String)j.get("path");
					
	    			runOnUiThread(new Runnable() {
					
					  public void run() 
					  {
						  	//Log.d("IN MainActivity, path", reqest_path+"");
							//Log.d("IN MainActivity, nickname", reqest_nick+"");

							if(reqest_path != null && !reqest_path.equals("no"))
							{
								//Log.d("IN MainActivity"," req_path = null??" + reqest_path);
								runOnUiThread(new Runnable(){
									@Override
									public void run() 
									{
										 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
								            	new download_Profile_thumbnail_Image(reqest_path, reqest_nick,2)
										 		.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
								            else
								            	new download_Profile_thumbnail_Image(reqest_path, reqest_nick,2).execute((Void[])null);
									}
								});	
							}
							else
							{
//								//System.out.println("해쉬맵에 null이 들어감.");
//								NICK_and_BITMAP.put(req_nick, null); // 원래는 Bitmap 객체가 들어가야되지만. 등록된 path가 없다면 null을 저장.
								Bitmap default_img = BitmapFactory.decodeResource(getResources(), R.drawable.person);
								Bitmap target_bmp = Bitmap.createScaledBitmap(default_img,100, 100, true);
								RoundedAvatarDrawable tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(target_bmp);
								
								FriendView friend = new FriendView(tmpRoundedAvatarDrawable, reqest_nick);
			              		Fadapter.add(friend);
			              		 
			              		runOnUiThread(new Runnable() {
									
									  public void run() 
									  {
										  Fadapter.notifyDataSetChanged();
									  }
									});
								
								
							}
					  }
					});
					
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Log.d("MainActivity", "friend_list : " + friend_list);
		
		if(friend_list.equals("[]")==false) // 친구가 존재한다면!
		{
			JSONParser jsonParser2 = new JSONParser();
			
			try {
				
				JSONArray jarr2 = (JSONArray) jsonParser2.parse(friend_list);
				
				for(int i=0;i<jarr2.size();i++)
				{
					JSONObject j = (JSONObject)jarr2.get(i);
					//System.out.println("nick["+i+"] : " + (String)j.get("nick"));
					//System.out.println("path["+i+"] : " + (String)j.get("path"));
					//System.out.println("state_msg["+i+"] : " + (String)j.get("state_msg"));
					//System.out.println("값["+i+"] : " + jarr2.get(i));
					
					final String f_nick = (String)j.get("nick");
					final String f_path = (String)j.get("path");
					final String f_state_msg = (String)j.get("state_msg");
					
					
	    			runOnUiThread(new Runnable() {
					
					  public void run() 
					  {
						  	//Log.d("IN MainActivity, 친구 path", f_path+"");
							//Log.d("IN MainActivity, 친구 f_nick", f_nick+"");

							if(f_path != null && !f_path.equals("no") && f_path.length() != 0)
							{
								//Log.d("IN MainActivity"," req_path = null??" + f_path);
								runOnUiThread(new Runnable(){
									@Override
									public void run() 
									{
										 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
								            	new download_Profile_thumbnail_Image(f_path, f_nick, f_state_msg,3)
										 		.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
								            else
								            	new download_Profile_thumbnail_Image(f_path, f_nick, f_state_msg, 3).execute((Void[])null);
										 
										 friends_list friends = new friends_list(f_path, f_nick, f_state_msg);
						              	 f_list_adapter.add(friends);
						              		
									}
								});	
							}
							else
							{
//								//System.out.println("해쉬맵에 null이 들어감.");
//								NICK_and_BITMAP.put(req_nick, null); // 원래는 Bitmap 객체가 들어가야되지만. 등록된 path가 없다면 null을 저장.
								
								
								Bitmap default_img = null;
          						try
          						{
//          							default_img = BitmapFactory.decodeResource(getResources(), R.drawable.person);
//          							Bitmap target_bmp = Bitmap.createScaledBitmap(default_img,100, 100, true);
//    								RoundedAvatarDrawable tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(target_bmp);
//    								
//    								FriendView2 friend = new FriendView2(tmpRoundedAvatarDrawable, f_nick, f_state_msg);
//    			              		f_list_adapter.add(friend);
    			              		
    			              		friends_list friends = new friends_list("no", f_nick, f_state_msg);
									
				              		f_list_adapter.add(friends);
				              		
    			              		runOnUiThread(new Runnable() {
    									
    									  public void run() 
    									  {
    										  Fadapter.notifyDataSetChanged();
    									  }
    									});          							
          						}
          						catch(NullPointerException ne)
          						{
          							ne.printStackTrace();
          							
//          							if(default_img != null)
//          							{
//          								Bitmap target_bmp = Bitmap.createScaledBitmap(default_img, 100, 100, true);
//        								RoundedAvatarDrawable tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(target_bmp);
//        								
//        								FriendView2 friend = new FriendView2(tmpRoundedAvatarDrawable, f_nick, f_state_msg);
//        			              		f_list_adapter.add(friend);
//        			              		 
//        			              		runOnUiThread(new Runnable() {
//        									
//        									  public void run() 
//        									  {
//        										  Fadapter.notifyDataSetChanged();
//        									  }
//        									});          					      
//          							}
//          							else
//          							{
//          								Log.d("MainActivity", "default_img == null");
//          							}
          						}
          						
								
							}
					  }
					});
					
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(chat_list.equals("[]")==false) // 채팅 리스트가 존재한다면!
		{
			//System.out.println("chat_list가 존재합니다.");
			JSONParser jsonParser2 = new JSONParser();
			
			try {
				
				JSONArray jarr2 = (JSONArray) jsonParser2.parse(chat_list);
				
				for(int i=0;i<jarr2.size();i++)
				{
					JSONObject j = (JSONObject)jarr2.get(i);
					//System.out.println("seq["+i+"] : " + (String)j.get("seq"));
					//System.out.println("f_nick["+i+"] : " + (String)j.get("f_nick"));
					//System.out.println("last["+i+"] : " + (String)j.get("last"));
					//System.out.println("time["+i+"] : " + (String)j.get("time"));
					//System.out.println("값["+i+"] : " + jarr2.get(i));
					
					final String seq = (String)j.get("seq");
					final String f_nick = (String)j.get("f_nick");
					final String last = (String)j.get("last");
					final String time = (String)j.get("time");
					
					JSONObject packet = new JSONObject();
					packet.put("Command", "save_KEY_USERS");
					packet.put("seq", seq);
					packet.put("my_nick", MainActivity.MyNickname);
					packet.put("f_nick", f_nick);
					
					String pac3 = packet.toJSONString();
					MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
					MainActivity.out.println(pac3);
					MainActivity.out.flush();
					
					new Thread(new Runnable() {
						
						public void run() {
							HttpURLConnection conn;
							URL url;
							BufferedWriter bw;
							BufferedReader br;
							
							try {
								url = new URL("http://115.71.237.99/chatchat/get_thumbnail_path.php");
								
								try {
									conn = (HttpURLConnection)url.openConnection();
									conn.setConnectTimeout(2000);
									conn.setReadTimeout(2000);
									conn.setRequestMethod("POST");
									conn.setRequestProperty("Cache-Control", "no-cache");
									conn.setDoInput(true);
									conn.setDoOutput(true);
									
									bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
									String request="request="+f_nick;
									
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
										
										final String res = response;
										//System.out.println("In MainActivity, 받아온 값 : "+response);
											
										 if(!response.equals("no")) // 프로필 사진을 업로드한 적이 있는 친구라면. 
										 {
											 runOnUiThread(new Runnable(){
													@Override
													public void run() 
													{
														 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
												            	new download_Profile_thumbnail_Image(res, f_nick, last, time, 4, seq, MainActivity.this)
														 		.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
												            else
												            	new download_Profile_thumbnail_Image(res, f_nick, last, time, 4, seq, MainActivity.this).execute((Void[])null);
													}
												});
										 }
										 else // 프로필 사진을 업로드한 적이 없는 친구라면.
										 {
											 Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.person);
											 
											 MainActivity.NICK_and_BITMAP.put(f_nick, bm);
											 Bitmap target_bmp3 = Bitmap.createScaledBitmap(bm,100, 100, true);
											 RoundedAvatarDrawable tmpRoundedAvatarDrawable3 = new RoundedAvatarDrawable(target_bmp3);
											 FriendView3 fv3 = new FriendView3(tmpRoundedAvatarDrawable3, f_nick, last, time, seq);
											 MainActivity.f_chat_adapter.add(fv3);
										 }
									}
									else
									{
										//System.out.println("http 실패");
										//System.out.println("code : " + responseCode);
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
					}).start();
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		SQLiteDatabase db = MainActivity.dbManager.getReadableDatabase();
		 
		 String str2;
		 Cursor cursor = db.rawQuery("select * from chat_log ORDER BY seq DESC LIMIT 1;", null);
	        while(cursor.moveToNext()) {
	            str2= cursor.getString(0)+","+cursor.getString(1)+","+cursor.getInt(2)+","+cursor.getString(3)+","+cursor.getString(4)+","+cursor.getString(5)
	            		+","+cursor.getString(6)+","+cursor.getInt(7);
	            //Log.d("마지막 값(sqltie) : ", str2);
	           
	            new get_losed_chat(cursor.getString(1), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), MainActivity.MyNickname, MainActivity.this).start();
	        }
	        
		
	}
}
