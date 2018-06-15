package com.example.chatchat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
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


	public class uploadProfile extends AsyncTask<Void, Integer, Void> //선택한 이미지를 서버로 업로드.
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
        String changeFileNameBySeq;
        String chatchat_profile_file_uri;
        String uriFromphp;
        String thumnail_path;
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
		
		public uploadProfile(String uri, String filename, String _seq, Context contex) {
			this.sourceFileUri = uri;
			this.phpName = filename;
			this.myContext = contex;
			this.userSeq = _seq;
			this.upLoadServerUri += filename;
			
			//Log.d("uploadProfile", "userSeq = "+userSeq+"");
			
			//Log.d("생성자", sourceFileUri);
		}
		

		@Override
		protected void onPostExecute(Void result) { // 생성한 임시 파일을 삭제해줌.
			super.onPostExecute(result);
//			if(sourceFile.delete())
//			{
//				//System.out.println("생성한 임시 파일이 삭제되었습니다.");
//			}
//			else
//			{
//				//System.out.println("임시 파일 삭제 실패! ");
//				
//			}
			
		}

		
		@Override
		protected void onProgressUpdate(Integer... values) { //  error 변수를 입력받아 그에 맞는 대처를 함.
			super.onProgressUpdate(values);
			
			switch (values[0]) {
			case 0:
				
				Profile.adapter.notifyDataSetChanged();
				//System.out.println("In uploadProfile, onProgressUpdate"); // 디비에 프로필 사진 경로 갱신.
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
	            	new update_profile_pathTophp("http://115.71.237.99/chatchat/update_profile_path.php",
	            			MainActivity.MyNickname, uriFromphp).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
	            else
	            	new update_profile_pathTophp("http://115.71.237.99/chatchat/update_profile_path.php",
	            			MainActivity.MyNickname, uriFromphp).execute((Void[])null);	 
	            			
				//System.out.println("=================================================================================");
/*
 * 
 * 실험!!
 * 
 * */				
				
//				//System.out.println("chatchat_profile_file_uri : " + chatchat_profile_file_uri);
//				String folder = chatchat_profile_file_uri.substring(0, chatchat_profile_file_uri.lastIndexOf("/")+1);
//				String fileNAME = "thumb"+MainActivity.MySeq+".jpg";
//				String rename = folder+fileNAME;
//
//				//Log.d("rename", rename+"");
				
				
    			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
	            	new uploadProfilethumb(thumnail_path, "uploadThumbnail.php")
    			        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
	            else
	            	new uploadProfilethumb(thumnail_path, "uploadThumbnail.php").execute((Void[])null);	 break;
			
//	            	new del_privious_profile_pic(profile_File_name).start();
	            	

			default:
				Toast.makeText(myContext, "잘 못된 접근입니다..", Toast.LENGTH_SHORT).show();
				
				break;
			}
			
			
		}


		@Override
		protected void onPreExecute() { // 파일을 전송하기 전에 선택한 파일의 크기에 따라서 비트맵의 크기를 줄여주고 줄여준 비트맵을 다시 임시 파일로 만들어줌.
			super.onPreExecute();
			
			pass = true;
			
			//System.out.println("onPreExecute");
			
			File oFile = new File(sourceFileUri);
			long FileSize=0;
		
			if (oFile.exists() )// 파일이 존재한다면! 
			{
				FileSize =  oFile.length(); // 파일의 크기를 구함. 
				
			      //Log.d("FileSize", FileSize+"");
			      
			      ut = new utils();
			      degree = ut.GetExifOrientation(sourceFileUri);
			      

			      int scaleFactor =0;
			      
			      if(FileSize > 5*1024*1024) // 파일 크기가 5메가 이상이라면 전송 불가
			      {
			    	  //System.out.println("파일 크기가 5메가 이상임");
			    	  error=1;
			    	  pass = false;
			      }
			      
			      else if(FileSize < 1*1024*1024) // 파일 크기가 1메가 이하라면 그냥 전송
			      {
			    	  scaleFactor = 1;
			      }
			      else // 리사이징
			      {
						scaleFactor = (int) (FileSize / (1024*1024/2));
			      }	
					
			  	//Log.d("scaleFactor", scaleFactor+"");
				// Decode the image file into a Bitmap sized to fill the View
				BitmapFactory.Options bmOptions = new BitmapFactory.Options(); // 이미지 크기 줄이기!
				bmOptions.inJustDecodeBounds = false;
				bmOptions.inSampleSize = scaleFactor;
				bmOptions.inPurgeable = true;
				  
				Bitmap bitmap = BitmapFactory.decodeFile(sourceFileUri, bmOptions); 
				Bitmap bitmap2 = GetRotatedBitmap(bitmap, degree);
				
				//Log.d("bitmapW", bitmap.getWidth()+"");
				//Log.d("bitmapH", bitmap.getHeight()+"");
				
				
			    fileName = sourceFileUri.substring(sourceFileUri.lastIndexOf("/")+1, sourceFileUri.length()); // 기존 파일의 이름
			  //  changeFileNameBySeq = fileName.replaceAll(fileName.substring(0, fileName.indexOf(".")),userSeq);
			    
				//String newFileUri = sourceFileUri.replaceAll(fileName, changeFileNameBySeq); 
				//SaveBitmapToFileCache(bitmap2, newFileUri); // 새로운 파일 생성

			    saveBitmaptoJpeg(bitmap2, "chatchat_profile", userSeq);
			    
			    int resizeThumbnailW = 160*bitmap.getWidth()/bitmap.getHeight();
//			    thumnail_path = ut.saveBitmaptoJpeg(ThumbnailUtils.extractThumbnail(bitmap2, 160, resizeThumbnailW), "chatchat_profile_thumbnail", "thumb"+MainActivity.MySeq); // 업로드할 사진이미지를 thumbnail화 시켜서 저장한다.
			    
//			    String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
			    thumnail_path = ut.createThumbnail(bitmap2, "data/data/com.example.chatchat/chatchat_profile_thumbnail/", "th"+userSeq+".jpg");
			    
			    //System.out.println("썸네일 생성!!");
				
			    //Log.d("thumnail_path", thumnail_path+"");
				//Log.d("changeFileNameBySeq", changeFileNameBySeq);
				//Log.d("새로운 fileName", ""+changeFileNameBySeq);

				
				sourceFile = new File(chatchat_profile_file_uri);
				
		        	try { // 새로운 파일을 InputStream에 넣어주고 전송하기 전 httpUrlconnection의 옵션을 설정한다.
					     // open a URL connection to the Servlet
		                fileInputStream = new FileInputStream(sourceFile);
		               
		                //Log.d("sourceFile", sourceFile+"");
		                //Log.d("fileName", changeFileNameBySeq+"");
		                
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
		                conn.setRequestProperty("uploaded_file", changeFileNameBySeq);
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
		
		
		
		public synchronized Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees)
		{
			int [][] arr1 = new int[4][4];
			
			if ( degrees != 0 && bitmap != null )
			{
				Matrix m = new Matrix();
				m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
					
				try
				{
					Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
					if (bitmap != b2)
					{
						bitmap.recycle();
						bitmap = b2;
					}
					
				}
				
				catch (OutOfMemoryError ex)
				{
				// We have no memory to rotate. Return the original bitmap.
				}
			}
		 
			return bitmap;
		}
		
		
		@Override
		protected Void doInBackground(Void... params) { // 리사이징된 임시 파일을 서버로 전송
			
			//System.out.println("doInBackground");

			if(pass == true)
			{
				try {
					
					dos = new DataOutputStream(conn.getOutputStream());
	                dos.writeBytes(twoHyphens + boundary + lineEnd);
	                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + changeFileNameBySeq + "\"" + lineEnd);
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
		               
//		              Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
		             if(serverResponseCode == HttpURLConnection.HTTP_OK){ //만약 HTTP_OK 신호가 들어온다면! (200)

		            	    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));  
							StringBuilder sb = new StringBuilder();
							String line;
							
							while((line=br.readLine()) != null){
								sb.append(line+"\n");
							}
							
							br.close(); 
							
							String response=sb.toString();
							//System.out.println("받아온 값 : "+response);
							
							String parse_respon[] = response.split(","); // 받아온 값을 파싱한다 . [0] => 업로드한 파일이 위치한 주소, [1] => 성공 여부
							
							uriFromphp = "http://115.71.237.99/"+parse_respon[0];
							
							//System.out.println("======== uploadProfile. uriFromphp ============");
							for(int i=0;i<uriFromphp.length();i++)
							{
								//System.out.println("uriFromphp["+i+"] : " + uriFromphp.charAt(i));
							}
							String real_path = uriFromphp.substring(0, 21).concat(uriFromphp.substring(22,uriFromphp.length())); // 21번째에 숨어있는 공백문자 제거.
	
							for(int i=0;i<real_path.length();i++)
							{
								//System.out.println("real_path["+i+"] : " + real_path.charAt(i));
							}
							
							
							if(parse_respon[1].equals("success"+"\n")) // 만약 업로드에 성공하였다면!!
							{
								//System.out.println("전송 성공!");

								String path = "http://115.71.237.99/"+parse_respon[0];
								
								//System.out.println("======== uploadProfile. path ============");
//								for(int i=0;i<path.length();i++)
//								{
//									System.out.println("path["+i+"] : " + path.charAt(i));
//								}
								String real_path2 = path.substring(0, 21).concat(path.substring(22,path.length())); // 21번째에 숨어있는 공백문자 제거.
								
								SharedPreferences.Editor editor = MainActivity.chk_login.edit();
								
								
								//Log.d("real_path2", real_path2+"");
								//Log.d("chatchat_profile_file_uri", chatchat_profile_file_uri+"");

								 String past_profile_path = MainActivity.chk_login.getString("local_profilePATH", "not_exit");
								   
								   try
								   {
									   File past_profile = new File(past_profile_path);
									   
									   if(past_profile.exists())
									   {
//										   System.out.println("새로운 파일을 업로드하여 그 전 파일은 삭제합니다.");
										   past_profile.delete();
									   }
								   }
								   catch(Exception e)
								   {
									   e.printStackTrace();
									   Log.d("uploadProfile", "error!!");
								   }

								
								editor.putString("Profile_Picture", real_path2);
								editor.putString("local_profilePATH", chatchat_profile_file_uri);
								editor.commit();
								
//								 String past_profile_path = MainActivity.chk_login.getString("local_profilePATH", "not_exit");
//								   
//								   try
//								   {
//									   File past_profile = new File(past_profile_path);
//									   
//									   if(past_profile.exists())
//									   {
//										   System.out.println("새로운 파일을 업로드하여 그 전 파일은 삭제합니다.");
//										   past_profile.delete();
//									   }
//								   }
//								   catch(Exception e)
//								   {
//									   e.printStackTrace();
//									   Log.d("saveBitmaptoJpeg", "error!!");
//								   }
								   
								
//								JSONObject Packet2 = new JSONObject();
//								Packet2.put("Command","profile_picture");
//								Packet2.put("profile_picture", parse_respon[0]);
//								Packet2.put("nickname",MainActivity.MyNickname+"");
//								String pac2 = Packet2.toJSONString();
//
//								MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
//								MainActivity.out.println(pac2); 
//								MainActivity.out.flush();
							
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
		
	   public void saveBitmaptoJpeg(Bitmap bitmap, String folder, String name)
	   {
//		   String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
//		   String folder_name = "/"+folder+"/";
		   String file_name = name + ".jpg";
		   String string_path = "data/data/com.example.chatchat/chatchat_profile/";
		   chatchat_profile_file_uri = string_path+file_name;
		   changeFileNameBySeq = file_name;
		   
//		   String past_profile_path = MainActivity.chk_login.getString("local_profilePATH", "not_exit");
//		   
//		   try
//		   {
//			   File past_profile = new File(past_profile_path);
//			   
//			   if(past_profile.exists())
//			   {
//				   System.out.println("새로운 파일을 업로드하여 그 전 파일은 삭제합니다.");
//				   past_profile.delete();
//			   }
//		   }
//		   catch(Exception e)
//		   {
//			   e.printStackTrace();
//			   Log.d("saveBitmaptoJpeg", "error!!");
//		   }
		   
		   File file_path;
		   try
		   {
			   file_path = new File(string_path);
			   
			   if(!file_path.isDirectory())
			   {
				   file_path.mkdir();
			   }
			   FileOutputStream out = new FileOutputStream(string_path+file_name);
			   bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			   out.close();
			   
//			   /*
//			    *  새로 지정한 프로필사진이 디바이스에 저장된 절대경로를 저장한다.
//			    */
//			   SharedPreferences.Editor editor = MainActivity.chk_login.edit();
//			   editor.putString("profile_pic_in_device", string_path+file_name);
//			   editor.commit();
		   }
		   catch(FileNotFoundException e)
		   {
			Log.e("FileNotFoundException", e.getMessage());   
		   }
		   catch(IOException e)
		   {
			Log.e("IOException", e.getMessage());   
		   }
	   }
	}

