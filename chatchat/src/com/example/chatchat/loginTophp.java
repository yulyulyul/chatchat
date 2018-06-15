package com.example.chatchat;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.PublicKey;

import org.json.simple.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


class loginTophp extends AsyncTask<Void, Integer , Void>{

	HttpURLConnection conn;
	BufferedWriter bw;
	BufferedReader br;
	String server_uri;
	String password;
	String id;
	Message message;
	Context myContext;
	String nickname;
	String server_profile_pic_path;
	String server_profile_thumbnail_path;
	utils ut;
	
	public loginTophp(String uri, String _id, String _password, Context contex) 
	{
		this.server_uri = uri;
		this.id = _id;
		this.password = _password;
		this.myContext = contex;
		nickname = null;
		
		////System.out.println("id : " + id);
		////System.out.println("password : "+password);
		
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		
		////System.out.println("여기는 onProgressUpdate()");
		
		switch (values[0]) {
		case 0: 
			
					String myip = MainActivity.MyIp;
					MainActivity.MyNickname = nickname;
			        MainActivity.LoginPage.setVisibility(View.INVISIBLE);
			        MainActivity.MainPage.setVisibility(View.VISIBLE);
			        
			        JSONObject packet2 = new JSONObject(); // 서버에 ip와 nickname을 저장.
		    		packet2.put("Command", "Save_nickname_and_ip");
		    		packet2.put("nickname", nickname+"");
		    		String pac = packet2.toJSONString();
		    		
		    		////System.out.println("pac : " + pac);

		    		MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
		    		MainActivity.out.println(pac);
		    		MainActivity.out.flush();
		    		
			        JSONObject packet3 = new JSONObject(); // 서버에 ip와 nickname을 저장.
		    		packet3.put("Command", "profile_thumbnail");
		    		packet3.put("profile_thumbnail", server_profile_thumbnail_path);
		    		packet3.put("profile_url", server_profile_pic_path);
		    		packet3.put("nickname", MainActivity.MyNickname);
		    		String pac2 = packet3.toJSONString();
		    		
		    		////System.out.println("pac : " + pac2);

		    		MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
		    		MainActivity.out.println(pac2);
		    		MainActivity.out.flush();
		    		
		    		
		    		//System.out.println("======== mainactivity. server_profile_pic_path ============");
					for(int i=0;i<server_profile_pic_path.length();i++)
					{
						//System.out.println("server_profile_pic_path["+i+"] : " + server_profile_pic_path.charAt(i));
					}
		    				    		
		    		
					MainActivity ma = new MainActivity();
					ma.ifLOGIN();
		    		
			        
					Toast.makeText(myContext, nickname + "님 환영합니다.", Toast.LENGTH_SHORT).show();	 break;
		case 1: 
			 		MainActivity.username.setText("");
			 		MainActivity.password.setText("");
					Toast.makeText(myContext, "로그인 실패", Toast.LENGTH_SHORT).show();	 break;
					
	   
		case 2:     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        			{
						new download_Profile_Image(server_profile_pic_path,MainActivity.MyNickname).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null); break;
        			}
		
        			else new download_Profile_Image(server_profile_pic_path,MainActivity.MyNickname).execute((Void[])null); break;
					
	  case 3:      
			
					  new Thread(new Runnable() {
							
							@Override
							public void run() {
								
								HttpURLConnection conn;
								URL url;
								BufferedWriter bw;
								BufferedReader br;
								
								try {
									url = new URL("http://115.71.237.99/chatchat/get_Thumbnail_PATH.php");
									
									try {
										conn = (HttpURLConnection)url.openConnection();
										conn.setConnectTimeout(2000);
										conn.setReadTimeout(2000);
										conn.setRequestMethod("POST");
										conn.setRequestProperty("Cache-Control", "no-cache");
										conn.setDoInput(true);
										conn.setDoOutput(true);
										
										bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
										String request="request="+id;
										//Log.d("loginTophp", request);
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
											
											//System.out.println("(썸네일 주소)받아온 값 : "+response);
											
											SharedPreferences.Editor editor = MainActivity.chk_login.edit();
											editor.putString("profile_thumbnail", response);
									        editor.commit();
									        
											
									
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
			
					   break;
					   
		default:
					Toast.makeText(myContext, "잘 못된 접근입니다..", Toast.LENGTH_SHORT).show();
			
			break;
		}
		
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		//System.out.println("In loginTophp onPreExecute");
		
		URL url;			
		ut = new utils();
		
		try {
			url = new URL(server_uri);
			
			try {
				conn = (HttpURLConnection)url.openConnection();
				conn.setConnectTimeout(2000);
				conn.setReadTimeout(2000);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Cache-Control", "no-cache");
				conn.setDoInput(true);
				conn.setDoOutput(true);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		//System.out.println("In loginTophp doInBackground");

		conn.setDoInput(true);
		try {
			bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
			String request="request="+id+","+password;
			//Log.d("request", request+"");
			String temp = request.trim();
			bw.write(temp);
			bw.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String response = null;
		
		try {
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
			StringBuilder sb = new StringBuilder();
			String line;
			while((line=br.readLine()) != null){
				sb.append(line);
			}
			
			br.close();
			
			response=sb.toString();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // EUC-KR 로 인코딩하여 받아옴. 
		
		
		
		//System.out.println("받아온 값 : "+response);
		try {
			
			int responseCode = conn.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK){ //만약 HTTP_OK 신호가 들어온다면! (200)

//				br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8")); // EUC-KR 로 인코딩하여 받아옴. 
//				StringBuilder sb = new StringBuilder();
//				String line;
//				
//				while((line=br.readLine()) != null){
//					sb.append(line);
//				}
//				
//				br.close();
//				
//				response=sb.toString();
//				
//				//System.out.println("받아온 값 : "+response);
				
				if(response.equals("fail") == false)
				{
					// 첫번째 배열의 값은 성공 여부 , 두번째 배열의 값은 닉네임.
					String successOrfail_data[] = response.split("a,qea");
					
					SharedPreferences.Editor editor = MainActivity.chk_login.edit();
//			        editor.putBoolean("login", true);
//			        editor.putString("nickname", nickname);
			        editor.commit();
			        
					for(int i=0;i<successOrfail_data.length;i++)
					{
						System.out.println("successOrfail_data"+"["+i+"] : " + successOrfail_data[i]);
						
						switch (i) 
						{
						case 0:  editor.putBoolean("login", true); 	break;
						
						case 1:  editor.putString("seq", successOrfail_data[1]); 	break;
						
						case 2:  editor.putString("nickname", successOrfail_data[2]); break;
						
						case 3:  if(successOrfail_data[3].equals("male")) editor.putString("gender", "남자");
								 else editor.putString("gender", "여자"); break;
						
						case 4: editor.putString("age", successOrfail_data[4]); break; 
						
						case 5: editor.putString("socket", successOrfail_data[5]); break;
						
						case 6: editor.putString("Profile_Picture", successOrfail_data[6]); break;
						
						case 7: editor.putString("friends", successOrfail_data[7]); break;
						
						case 8: editor.putString("state", successOrfail_data[8]); break;
						
						case 9: editor.putString("chatting_room", successOrfail_data[9]); break;
									
						case 10: editor.putString("state_message", successOrfail_data[10]); break;
						
						case 11: server_profile_thumbnail_path = successOrfail_data[11]; 
							     editor.putString("profile_thumbnail", successOrfail_data[11]);break;
							     
						case 12: editor.putString("req_friends", successOrfail_data[12]); break;
					     
						default:
							break;
						}
						
						editor.commit();
						String msg =  MainActivity.chk_login.getString("state_message", "no");
						//System.out.println(msg);
			
					}
					nickname = successOrfail_data[2];
					MainActivity.MySeq = successOrfail_data[1];
					//Log.d("MySeq", MainActivity.MySeq+"");
					
					
					String local_profilePATH = MainActivity.chk_login.getString("local_profilePATH", "nothing");
		            
		            //Log.d("local_profilePATH", local_profilePATH+"");
		            
		            
		            if(local_profilePATH.equals("nothing")) // 만약 기기에 내 프로필 사진의 주소가 없다면! -> 다른 기기에서 로그인을 한 상황.
		            {
		            	
		            	 server_profile_pic_path = MainActivity.chk_login.getString("Profile_Picture", "not_exit"); // 서버에 저장된 주소가 있나 확인. 만약 없으면 아직 내가 프로필 사진을 
//		            	 																								업로드한 적이 없는거임. 만약 있다면 다른 기기에서 로그인을 했거나 내 기기가 초기화된 경우.
//		            	 																								프로필 사진을 설정했지만 내 기기에 사진이 없는 경우임.
		            	
		            	//Log.d("In MainActivity, server_profile_pic_path", server_profile_pic_path);
//		            	
		            	if(server_profile_pic_path.equals("no") == false && server_profile_pic_path.equals("not_exit") == false) // 내가 사진을 업로드한 적이 있는 경우임. 서버에 저장된 주소로 부터 사진을 다운받음.
		            	{
		            		//Log.d("server_profile_pic_path", server_profile_pic_path+"");
		            		//System.out.println("실행댐.");
		            		
		            		String str = server_profile_pic_path.substring(0, 21).concat(server_profile_pic_path.substring(22,server_profile_pic_path.length()));
		            		server_profile_pic_path = str;
		            		
		            		//System.out.println("server_profile_pic_path : " + server_profile_pic_path);
		            		
		            		publishProgress(3); // 썸네일 주소를 기기에 저장해라.
		            		publishProgress(2); // 프로필 사진을 다운받아라
		            		
		            		
		            		
		            		/*
		            		 * 여기서 이제 서버에 저장된 썸네일 사진을 다운받는 코드를 작성해야함.
		            		 * 
		            		 * 
		            		 * 
		            		 *  
		            		 */
		            		
		            	}
		            	
		            	
		            }
					
					publishProgress(0); // 로그인 성공!
				}
		      else{
		    	 
		    	 publishProgress(1); // 로그인 실패!
		     }
				
			}
			else
			{
				//System.out.println("http 연결 실패");
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return null;
		
	}
		
	@Override
	protected void onPostExecute(Void downloaded_Image) {
		super.onPostExecute(downloaded_Image);
		
		ut.dialog.dismiss();
		
	}
}