package com.example.chatchat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class utils {
	
	static ProgressDialog dialog;
	
	 public void DialogProgress(Context mContext) {
       dialog = ProgressDialog.show(mContext, "", "잠시만 기다려 주세요 ...", true);

       // 창을 내린다.
       // dialog.dismiss();
   }
	 
	public synchronized  int GetExifOrientation(String filepath)
	{
		int degree = 0;
		ExifInterface exif = null;
		try
		{
			exif = new ExifInterface(filepath);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		 
		if (exif != null)
		{
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
		 
		if (orientation != -1)
		{
		// We only recognize a subset of orientation tag values.
		switch(orientation)
		{
			case ExifInterface.ORIENTATION_ROTATE_90:
			degree = 90;
			break;
			 
			case ExifInterface.ORIENTATION_ROTATE_180:
			degree = 180;
			break;
			 
			case ExifInterface.ORIENTATION_ROTATE_270:
			degree = 270;
			break;
		}
		 
		}
	}
	 
	return degree;
	}
	
	public int getViewSizeW(View v){
		
		return v.getWidth();
	}
	
	public int getViewSizeH(View v){
		
		return v.getHeight();
	}
	
	public byte[] bitmapToByteArray( Bitmap bitM ) {  // 비트맵을 바이트어레이로 변환
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;  
        bitM.compress(CompressFormat.JPEG, 100, stream) ;  
        byte[] byteArray = stream.toByteArray() ;  
        return byteArray ;  
    }
	
	
	
	   public String saveBitmaptoJpeg(Bitmap bitmap, String folder, String name)
	   {
//		   String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
//		   String folder_name = "/"+folder+"/";
		   String file_name = name;
		   String string_path = "data/data/com.example.chatchat/chatchat_profile/";
		   String local_path = string_path+file_name;
		   Bitmap bm = bitmap;
		   
		   //Log.d("In util, saveBitmaptoJpeg", local_path+"");
		   
		   
		   try
		   {
			   File past_profile = new File(local_path);
			   
			   if(past_profile.exists())
			   {
				   //System.out.println("이미 파일이 존재하여 그 전 파일은 삭제합니다.");
				   past_profile.delete();
			   }
			   
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
		   }
		   
		   File file_path;
		   try
		   {
			   file_path = new File(string_path);
			   
			   if(!file_path.isDirectory())
			   {
				   file_path.mkdir();
			   }
			   FileOutputStream out = new FileOutputStream(local_path);
			   
			   if(out != null)
			   {
				   if(bitmap == null)
				   {
					   //System.out.println("bitmap이 null");
				   }
				   else
				   {
					   bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
					   out.close();
				   }
				   
			   }
			   else if(out == null)
			   {
				   //System.out.println("out is null");
			   }
		   }
		   catch(FileNotFoundException e)
		   {
			Log.e("FileNotFoundException", e.getMessage());   
		   }
		   catch(IOException e)
		   {
			Log.e("IOException", e.getMessage());   
		   }
		   
		   return local_path;
	   }
	   
		public byte[] readBytes(Socket socket) throws IOException {
		    // Again, probably better to store these objects references in the support class
		    InputStream in = socket.getInputStream();
		    DataInputStream dis = new DataInputStream(in);

		    int len = dis.readInt();
		    byte[] data = new byte[len];
		    if (len > 0) {
		        dis.readFully(data,0,len);
		    }
		    return data;
		}
		
		
	    public static int getDpToPixel(Context context, int DP) {
	        float px = 0;
	        try {
	            px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP, context.getResources().getDisplayMetrics());
	        } catch (Exception e) {
	             
	        }
	        return (int) px;
	    }
	    
	    
	    public  String createThumbnail(Bitmap bitmap, String strFilePath, String filename) {
	    	
//	    	Log.d("utils", "createThumbnail이 실행됨.");
	    	
	    	String string_path = "data/data/com.example.chatchat/chatchat_profile_thumbnail/";
	    	String filePath = strFilePath + filename;
	   
	    	String past_filePath = MainActivity.chk_login.getString("local_thumb_path", "not_exit");
	    	
//	    	Log.d("utils", "past_filePath : " + past_filePath);
	    	
	    	 File file_path;
			   try
			   {
				   file_path = new File(string_path);
				   
				   if(!file_path.isDirectory())
				   {
					   file_path.mkdir();
				   }
			   }
			   catch(Exception e)
			   {
				   e.printStackTrace();
				   Log.e("utils", "In craateThumbnail");   
			   }
			   
	    	if(!past_filePath.equals("not_exit"))
	    	{
	    		File file = new File(past_filePath);
	    		 
	            if (file.exists()) 
	            {
//	            	  Log.d("utils", "새로운 프로필을 업로드하여 그 전 썸네일 사진은 삭제합니다.");
					  file.delete();
	            }
	    	}
	    	else
	    	{
//	    		Log.d("utils", "이전 프로필 썸네일 사진의 경로가 존재하지 않습니다.");
	    	}
 
	    	
	    	
            File fileCacheItem = new File(filePath);
            OutputStream out = null;
 
            try {
                int height=bitmap.getHeight();
                int width=bitmap.getWidth();
                 
                fileCacheItem.createNewFile();
                out = new FileOutputStream(fileCacheItem);
                //160 부분을 자신이 원하는 크기로 변경할 수 있습니다.
                bitmap = Bitmap.createScaledBitmap(bitmap, 320, height/(width/320), true);
                bitmap.compress(CompressFormat.JPEG, 100, out);
                
                MainActivity.editor.putString("local_thumb_path",filePath);
                MainActivity.editor.commit();
                
                String chk_filePath = MainActivity.chk_login.getString("local_thumb_path", "not_exit");
//                Log.d("utils", "chk_filePath : " + chk_filePath);
                
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
			return strFilePath+filename;
        }

	    public String SaveBitmapToFileCache(Bitmap bitmap, String strFilePath, String filename) { // 비트맵을 파일로 만들어줌.
            
	        File file = new File(strFilePath);
	        
	        if (!file.exists()) {
                file.mkdirs();
                // Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            }
	        File fileCacheItem = new File(strFilePath+filename);
	        OutputStream out = null;
	 
	        try
	        {
	            fileCacheItem.createNewFile();
	            out = new FileOutputStream(fileCacheItem);
	            bitmap.compress(CompressFormat.JPEG, 100, out);
	        }
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	        }
	        finally
	        {
	            try
	            {
	                out.close();
	            }
	            catch (IOException e)
	            {
	                e.printStackTrace();
	            }
	        }
	        return strFilePath+filename;
	  }
	    
	    
	  public static Runnable httpRequest = new Runnable() {
		
		@Override
		public void run() {
			HttpURLConnection conn;
			URL url;
			BufferedWriter bw;
			BufferedReader br;
			
			try {
				url = new URL("http://115.71.237.99/chatchat/save_req_friend_list.php");
				
				try {
					conn = (HttpURLConnection)url.openConnection();
					conn.setConnectTimeout(2000);
					conn.setReadTimeout(2000);
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Cache-Control", "no-cache");
					conn.setDoInput(true);
					conn.setDoOutput(true);
					
					bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
//					String request="request="+MyNickname+"a,qea"+jarrStr; // 사람들이 잘 쓰지 않는 문자열로 나누기 위해서!
					
//					Log.d("loginTophp", request);
//					String temp = request.trim();
//					bw.write(temp);
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
						
						//System.out.println("db에 친구요청 리스트 보내고 받아온 값 : "+response);
						
//						editor.putString("friends", jarrStr);
//						editor.commit();
						 
						//System.out.println("친구 요청 리스트 저장 완료!");
						 //저장 완료!
				
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
	};
}

class httpRequest extends Thread
{
	String insertDB;
	String list;
	String phpFile;
	int type;
	SharedPreferences.Editor editor;
	
	public httpRequest(String _insertDB, String _list, String _phpFile, SharedPreferences.Editor _editor, int _type) {
		this.insertDB = _insertDB;
		this.list = _list;
		this.phpFile = _phpFile;
		this.editor = _editor;
		this.type = _type;
		
		//Log.d("utils", "insertDB : " + insertDB);
		//Log.d("utils", "list : " + list);
		
	}
	@Override
	public void run() {
		super.run();
		HttpURLConnection conn;
		URL url;
		BufferedWriter bw;
		BufferedReader br;
		
		try {
			url = new URL("http://115.71.237.99/chatchat/"+phpFile);
			
			try {
				conn = (HttpURLConnection)url.openConnection();
				conn.setConnectTimeout(2000);
				conn.setReadTimeout(2000);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Cache-Control", "no-cache");
				conn.setDoInput(true);
				conn.setDoOutput(true);
				
				bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
				String request="request="+insertDB;
				
				//Log.d("httpRequest", request);
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
					
					switch (type) {
					case 1:
						//System.out.println("db에 친구요청 리스트 보내고 받아온 값 : "+response);
						//Log.d("utils", "response : " + response);
						
						editor.putString("req_friends", list);
						editor.commit();
						 
						//System.out.println("친구 요청 리스트 저장 완료!");	
						break;

					case 2: 
						//System.out.println("db에 친구리스트 보내고 받아온 값 : "+response);
						//Log.d("utils", "response : " + response);
						
						editor.putString("friends", list);
						editor.commit();
						 
						//System.out.println("친구 리스트 저장 완료!");
						
						break;
						
					default:
						break;
					}
					
					
					 //저장 완료!
			
				}
				else
				{
					//Log.d("utils", "http 실패");
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
}

class httpRequest2 extends Thread // state를 0에서 1로 바꾸기 또는 0에서 2로 바꾸기 실행.
{
	String nick;
	String phpFile;
	RoundedAvatarDrawable tmpRoundedAvatarDrawable;
	
	public httpRequest2(String _nick , String _phpFile, RoundedAvatarDrawable _tmpRoundedAvatarDrawable) {
		this.nick = _nick;
		this.phpFile = _phpFile;
		this.tmpRoundedAvatarDrawable = _tmpRoundedAvatarDrawable;
		
		//Log.d("utils", "nick : " + nick);
		//Log.d("utils", "phpFile : " + phpFile);
		
	}
	@Override
	public void run() {
		super.run();
		HttpURLConnection conn;
		URL url;
		BufferedWriter bw;
		BufferedReader br;
		
		try {
			url = new URL("http://115.71.237.99/chatchat/"+phpFile);
			
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
				
				//Log.d("request", request);
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
					
						//System.out.println("db에서 데이터를 받아온 값 : "+response);
						//Log.d("utils", "response : " + response);
						
						String data[] = response.split("a,qea");
//						Log.d("In utils", "data[] : " + data);
//						Log.d("utils", "f_nick : " + data[0]);
//						Log.d("utils", "f_path : " + data[1]);
//						Log.d("utils", "f_state_msg : " + data[2]);
												 
						 
						String friend_list = MainActivity.chk_login.getString("friends", "[]"); // jsonString 형태이겠쥐.
						 
						//Log.d("utils", "friend_list : " + friend_list);
						 
						 
						 JSONArray friends = new JSONArray();
						 JSONObject  job = new JSONObject();
						 JSONParser jParser = new JSONParser();
							
						 if(friend_list.equals("[]"))// 아직 친구요청이 존재하지 않았던 경우.
						 {
							
							job.put("nick", data[0]);
							job.put("path", data[1]);
							job.put("state_msg", data[2]);
							friends.add(job);
							
							//System.out.println("친구 요청 리스트 생성!(json)");
							
//							FriendView2 friend2 = new FriendView2(tmpRoundedAvatarDrawable, data[0], data[2]);
//		              		MainActivity.f_list_adapter.add(friend2); // add하면 adapter에서 알아서 노티해줌.
		              		
		              		friends_list friend2 = new friends_list(data[1], data[0], data[2]);
		              		MainActivity.f_list_adapter.add(friend2);
		              		MainActivity.handler.sendEmptyMessage(1);
		              		
		              		String list = friends.toJSONString();
		              		//Log.d("In utils", "로컬에 친구 리스트 저장 확인 : " + list);
		              		MainActivity.editor.putString("friends", list);
		              		MainActivity.editor.commit();
		              		
						 }
						 else // 친구가 존재한 경우.
						 {
							 try {
								friends = (JSONArray)jParser.parse(friend_list);
								 job.put("nick", data[0]);
								 job.put("path", data[1]);
								 job.put("state_msg", data[2]);
								 
								 friends.add(job);
								
//								 FriendView2 friend2 = new FriendView2(tmpRoundedAvatarDrawable, data[0], data[2]);
//				              	 MainActivity.f_list_adapter.add(friend2); // add하면 adapter에서 알아서 노티해줌.
				              	 
				              	friends_list friend2 = new friends_list(data[1], data[0], data[2]);
			              		MainActivity.f_list_adapter.add(friend2);
			              		
				              	MainActivity.handler.sendEmptyMessage(1);
								 
			              		 String list = friends.toJSONString();
			              		 //Log.d("In utils", "로컬에 친구 리스트 저장 확인 : " + list);
			              		 MainActivity.editor.putString("friends", list);
			              		 MainActivity.editor.commit();
				              		
								 //System.out.println("친구 요청 리스트에 친구 추가!(json)");
							} catch (ParseException e) {
								e.printStackTrace();
							}
							
						 }
						
				}
				else
				{
					//Log.d("utils", "http 실패");
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
}

class httpRequest3 extends Thread // state를 0에서 2로 바꾸기 실행.
{
	String nick;
	String phpFile;
	
	public httpRequest3(String _nick , String _phpFile) {
		this.nick = _nick;
		this.phpFile = _phpFile;
		
		//Log.d("utils", "nick : " + nick);
		//Log.d("utils", "phpFile : " + phpFile);
		
	}
	@Override
	public void run() {
		super.run();
		HttpURLConnection conn;
		URL url;
		BufferedWriter bw;
		BufferedReader br;
		
		try {
			url = new URL("http://115.71.237.99/chatchat/"+phpFile);
			
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
				
				//Log.d("request", request);
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
					
						//System.out.println("db에서 데이터를 받아온 값 : "+response);
						//Log.d("utils", "response : " + response);
			
				}
				else
				{
					//Log.d("utils", "http 실패");
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
}

class get_losed_chat extends Thread // state를 0에서 2로 바꾸기 실행.
{
	String chat_seq;
	String nick;
	String msg;
	String created_date;
	String created_time;
	String Mynick;
	Context mContext;
	
	public get_losed_chat(String _chat_seq , String _nick, String _msg, String _created_date, String _created_time, String _Mynick, Context context) {
		
		this.chat_seq = _chat_seq;
		this.nick = _nick;
		this.msg = _msg;
		this.created_date = _created_date;
		this.created_time = _created_time;
		this.Mynick = _Mynick;
		this.mContext = context;
		
//		Log.d("utils", "chat_seq : " + chat_seq);
//		Log.d("utils", "nick : " + nick);
//		Log.d("utils", "msg : " + msg);
//		Log.d("utils", "created_date : " + created_date);
//		Log.d("utils", "created_time : " + created_time);
//		Log.d("utils", "Mynick : " + Mynick);
	}
	@Override
	public void run() {
		super.run();
		HttpURLConnection conn;
		URL url;
		BufferedWriter bw;
		BufferedReader br;
		
		try {
			url = new URL("http://115.71.237.99/get_losed_chat.php");
			
			try {
				conn = (HttpURLConnection)url.openConnection();
				conn.setConnectTimeout(2000);
				conn.setReadTimeout(2000);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Cache-Control", "no-cache");
				conn.setDoInput(true);
				conn.setDoOutput(true);
				
				bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
				String request="request="+chat_seq+","+nick+","+msg+","+created_date+","+created_time+","+Mynick; 
				
				//Log.d("request", request);
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
					
						//System.out.println("db에서 데이터를 받아온 값 : "+response);
						Log.d("utils", "response : " + response);
						
						JSONParser jparser = new JSONParser();
						JSONArray jarr = (JSONArray)jparser.parse(response);
						
						for(int i=0;i<jarr.size();i++)
						{
							JSONObject j = (JSONObject)jarr.get(i);
							String key = (String)j.get("chat_sequence");
							String nick = (String)j.get("nick");
							String msg = (String)j.get("msg");
							String created_date = (String)j.get("created_date");
							String created_time = (String)j.get("created_time");
							String type = (String)j.get("type");
							
							if(type.equals("text"))
							{
								if(nick.equals(MainActivity.MyNickname)) // 내가 보낸 메시지.
								{
									MainActivity.dbManager.sql_query("insert into chat_log(chat_sequence, state, nick, msg, created_date, created_time, type) "
											+ "values('"+key+"',"+0+", '" + nick + "' , '"+ msg +"' , datetime('now','localtime'),'"+ created_time+"',"+0+" );");
								}
								else // 상대방이 보낸 메시지.
								{
									MainActivity.dbManager.sql_query("insert into chat_log(chat_sequence, state, nick, msg, created_date, created_time, type) "
											+ "values('"+key+"',"+0+", '" + nick + "' , '"+ msg +"' ,datetime('now','localtime'),'"+ created_time+"',"+2+" );");
								}
							}
							else
							{
								if(nick.equals(MainActivity.MyNickname))
								{
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						            	new downloadImage("http://115.71.237.99/"+msg, 3, nick, created_time,3, mContext, key).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
						            else
						            	new downloadImage("http://115.71.237.99/"+msg, 3, nick, created_time,3, mContext, key).execute((Void[])null);
								}
								
								else
								{
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						            	new downloadImage("http://115.71.237.99/"+msg, 4, nick, created_time,3, mContext, key).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
						            else
						            	new downloadImage("http://115.71.237.99/"+msg, 4, nick, created_time,3, mContext, key).execute((Void[])null);
								}
							}
							
						}
						
			
				}
				else
				{
					//Log.d("utils", "http 실패");
					//System.out.println("http 실패");
					//System.out.println("code : " + responseCode);
				}
				
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
		
	}
}


