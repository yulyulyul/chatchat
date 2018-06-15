package com.example.chatchat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.coobird.thumbnailator.Thumbnails;

import org.json.simple.JSONObject;

import com.example.chatchat.Profile.del_privious_profile_pic;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


	public class uploadProfilethumb extends AsyncTask<Void, Integer, Void> //선택한 이미지를 서버로 업로드.
	{
		
		String sourceFileUri;
		File sourceFile;
		String fileName;
        String upLoadServerUri = "http://115.71.237.99/";
        FileInputStream fileInputStream;
        HttpURLConnection conn = null;
        DataOutputStream dos = null; 
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String phpName;
        String userSeq;
        String thumbnail_name;
        String chatchat_profile_file_uri;
        String uriFromphp;
        utils ut;
        int degree ;
        int error;
        int  serverResponseCode = 0;
        int bytesRead, bytesAvailable, bufferSize;
        Context myContext;
        byte[] buffer;
       // int maxBufferSize = 1 * 1024 * 1024;
        int maxBufferSize = 1 * 2048 * 2048;
        
        boolean pass = true; // 파일 크기가 너무 크면 doInbackground 않 돌아가게 만듬.
		
		public uploadProfilethumb(String uri, String phpfilename) { // 파일의 경로 , php 파일 이름, 사용자 시퀀스
			this.sourceFileUri = uri;
			this.phpName = phpfilename;
			this.upLoadServerUri += phpfilename;
			
			thumbnail_name = sourceFileUri.substring(sourceFileUri.lastIndexOf("/")+1, sourceFileUri.length());
		
			//System.out.println("In uploadProfilethumb");
			//Log.d("생성자, sourceFileUri", sourceFileUri);
			//Log.d("생성자, phpName", phpName);
			//Log.d("생성자, upLoadServerUri", upLoadServerUri);
			//Log.d("생성자, thumbnail_name", thumbnail_name);
		
			
			

		}
		

		@Override
		protected void onPostExecute(Void result) { 
			super.onPostExecute(result);
			
		}

		
		@Override
		protected void onProgressUpdate(Integer... values) { //  error 변수를 입력받아 그에 맞는 대처를 함.
			super.onProgressUpdate(values);
			
			switch (values[0]) {
			case 0:
				
				Profile.adapter.notifyDataSetChanged();
				//System.out.println("In uploadProfilethumb, onProgressUpdate"); // 디비에 프로필 사진 경로 갱신.
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
	            	new update_profile_thumbnail_pathTophp("http://115.71.237.99/chatchat/update_profile_thumbnail_path.php",
	            			MainActivity.MyNickname, uriFromphp).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
	            else
	            	new update_profile_thumbnail_pathTophp("http://115.71.237.99/chatchat/update_profile_thumbnail_path.php",
	            			MainActivity.MyNickname, uriFromphp).execute((Void[])null);	 break;
			

			default:
				
				break;
			}
		}

		@Override
		protected void onPreExecute() { 
			super.onPreExecute();
			
			pass = true;
			
			//System.out.println("in uploadProfilethumb. onPreExecute");
			
			File oFile = new File(sourceFileUri);
		
			if (oFile.exists() )// 파일이 존재한다면! 
			{
				
	        	try { // 새로운 파일을 InputStream에 넣어주고 전송하기 전 httpUrlconnection의 옵션을 설정한다.
				     // open a URL connection to the Servlet
	                fileInputStream = new FileInputStream(sourceFileUri);
	               
	                URL url = new URL(upLoadServerUri);
	                // Open a HTTP  connection to  the URL
	                conn = (HttpURLConnection) url.openConnection();
	                conn.setDoInput(true); // Allow Inputs
	                conn.setDoOutput(true); // Allow Outputs
	                conn.setUseCaches(false); // Don't use a Cached Copy
	                conn.setRequestMethod("POST"); // 보내는 형식은 post ( 설정을 안하면 default값인 get 형식으로 보냄)
	                conn.setRequestProperty("Connection", "Keep-Alive");
	                conn.setRequestProperty("ENCTYPE", "multipart/form-data"); 
	                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
	                conn.setRequestProperty("uploaded_file", thumbnail_name);
	        	}
	        	catch(Exception e)
	        	{
	        		e.printStackTrace();
	        	}
	      }
			
			else
			{
			     //System.out.println("파일 없음.");
			     error = 0;
			     pass = false;
			}
			
		}
		
		@Override
		protected Void doInBackground(Void... params) { // 리사이징된 임시 파일을 서버로 전송
			
			//System.out.println("in uploadProfilethumb, doInBackground");

			if(pass == true)
			{
				try {
					
					dos = new DataOutputStream(conn.getOutputStream());
	                dos.writeBytes(twoHyphens + boundary + lineEnd);
	                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + thumbnail_name + "\"" + lineEnd);
	                dos.writeBytes(lineEnd);
		              
		              // create a buffer of  maximum size
		              bytesAvailable = fileInputStream.available();
		              bufferSize = Math.min(bytesAvailable, maxBufferSize);
		              buffer = new byte[bufferSize];
		              
		              // read file and write it into form...
		              bytesRead = fileInputStream.read(buffer, 0, bufferSize); 
		              while (bytesRead > 0) {
		                  //System.out.println("bytesRead : " + bytesRead);
		                  dos.write(buffer, 0, bufferSize);
			              bytesAvailable = fileInputStream.available();
			              bufferSize = Math.min(bytesAvailable, maxBufferSize);
			              bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
		               }
		    
		              // send multipart form data necesssary after file data...
		              dos.writeBytes(lineEnd);
		              
		              dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
		              
		              // Responses from the server (code and message)
		              serverResponseCode = conn.getResponseCode();
		              
		              //Log.d("servertResposeCode", serverResponseCode+"");
		              String serverResponseMessage = conn.getResponseMessage();
		               
//		              Log.i("uploadFile", "HTTP Response is : "+ serverResponseMessage + ": " + serverResponseCode);
		             if(serverResponseCode == HttpURLConnection.HTTP_OK){ //만약 HTTP_OK 신호가 들어온다면! (200)

		            	    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));  
							StringBuilder sb = new StringBuilder();
							String line;
							
							while((line=br.readLine()) != null){
								sb.append(line+"\n");
							}
							
							br.close(); 
							
							String response=sb.toString();
							//System.out.println("in uploadProfilethumb,받아온 값 : "+response);
							
							String parse_respon[] = response.split(","); // 받아온 값을 파싱한다 . [0] => 업로드한 파일이 위치한 주소, [1] => 성공 여부
							
							uriFromphp = "http://115.71.237.99/"+parse_respon[0];
//							Log.d("uploadProfilethumb", "uriFromphp : " + uriFromphp);
							if(parse_respon[1].equals("success"+"\n")) // 만약 업로드에 성공하였다면!!
							{
								//System.out.println("썸네일 이미지 전송 성공!");
								String profile_url = MainActivity.chk_login.getString("Profile_Picture", "nothing");
								String past_thumbnail_path = MainActivity.chk_login.getString("profile_thumbnail", "nothing");
								System.out.println("past_thumbnail_path : " + past_thumbnail_path);
								
								try
								{
									String past_thumbnail_name = past_thumbnail_path.substring(past_thumbnail_path.lastIndexOf("/")+1, past_thumbnail_path.lastIndexOf("."));
									
									 new del_privious_profile_thumb_pic(past_thumbnail_name).start();
								}
								catch(StringIndexOutOfBoundsException SIB)
								{
									SIB.printStackTrace();
								}
								
//								String past_thumbnail_name = past_thumbnail_path.substring(past_thumbnail_path.lastIndexOf("/")+1, past_thumbnail_path.lastIndexOf("."));
//								Log.d("past_thumbnail_name", past_thumbnail_name+"");
//								 new del_privious_profile_thumb_pic(past_thumbnail_name).start();
								//Log.d("In uploadProfileThumb, profile_url(222)", uriFromphp+"");
								
//								//System.out.println("=== 검사(upload) == ");
//								for(int i=0;i<uriFromphp.length();i++)
//								{
//									//System.out.println("uriFromphp["+i+"] : " + uriFromphp.charAt(i));
//								}
//								//System.out.println("\n===================\n");
								
								String real_path = uriFromphp.substring(0, 21).concat(uriFromphp.substring(22,uriFromphp.length()));
								
//								//System.out.println("=== 검사(upload) 후 == ");
//								for(int i=0;i<real_path.length();i++)
//								{
//									//System.out.println("real_path["+i+"] : " + real_path.charAt(i));
//								}
//								//System.out.println("\n===================\n");
								
								
								
								//Log.d("썸네일 서버 주소", parse_respon[0]+"");

								SharedPreferences.Editor editor = MainActivity.chk_login.edit();
								editor.putString("profile_thumbnail", real_path);
								editor.commit();
								
//								Log.d("업로드 thumbnail path", real_path);
//								
								JSONObject Packet2 = new JSONObject();
								Packet2.put("Command","profile_thumbnail");
								Packet2.put("profile_thumbnail", real_path);
								Packet2.put("nickname",MainActivity.MyNickname+"");
								Packet2.put("profile_url", profile_url);
								String pac2 = Packet2.toJSONString();

								MainActivity.out.println(pac2); 
								MainActivity.out.flush();
							
								publishProgress(0);
							}
							else
							{
								//System.out.println("전송 실패!!");
							}
						}
		              
		              
		              fileInputStream.close();
			             dos.flush();
			             dos.close();
			             
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			else
			{
				publishProgress(error);
			}
              
			return null;
		}
		
		class del_privious_profile_thumb_pic extends Thread
		{
			String privious_file;
			HttpURLConnection conn;
			URL url;
			BufferedWriter bw;
			BufferedReader br;
			
			public del_privious_profile_thumb_pic(String _privious_file) 
			{
				this.privious_file = _privious_file;
			}
			
			 @Override
			public void run() {
				super.run();
				
				try{
					url = new URL("http://115.71.237.99/chatchat/del_privious_profile_thumb_pic.php");
					
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
							
//							System.out.println("이전 프로필 thumbnail 삭제 : " + response);
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

