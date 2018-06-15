package com.example.chatchat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


	public class uploadFile extends AsyncTask<Void, Integer, Void> //선택한 이미지를 서버로 업로드.
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
        String seperation;
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
		
		public uploadFile(String uri, String filename, String _seperation, Context contex) {
			this.sourceFileUri = uri;
			this.phpName = filename;
			this.myContext = contex;
			this.seperation = _seperation;
			this.upLoadServerUri += filename;
			
			//Log.d("생성자", sourceFileUri);
		}
		

		@Override
		protected void onPostExecute(Void result) { // 생성한 임시 파일을 삭제해줌.
			super.onPostExecute(result);
			if(sourceFile.delete())
			{
				//System.out.println("생성한 임시 파일이 삭제되었습니다.");
			}
			else
			{
				//System.out.println("임시 파일 삭제 실패! ");
				
			}
			
		}

		
		@Override
		protected void onProgressUpdate(Integer... values) { //  error 변수를 입력받아 그에 맞는 대처를 함.
			super.onProgressUpdate(values);
			
			switch (values[0]) {
			case 0: 
						Toast.makeText(myContext, "파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();	 break;
			case 1: 
				Toast.makeText(myContext, "5mb이상의 파일은 전송이 불가능합니다.", Toast.LENGTH_SHORT).show();	 break;
			
			

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
			    //Log.d("수정 전!", fileName+"");
			    
			    
				String newFileUri = sourceFileUri.replaceAll(fileName, seperation); 
				//Log.d("newFileUri", newFileUri+"");
				SaveBitmapToFileCache(bitmap2, newFileUri); // 새로운 파일 생성
				
				fileName =  seperation;
				
				//Log.d("fileName", fileName);
				
				sourceFile = new File(newFileUri);
		        
				//Log.d("새로운 fileName", ""+fileName);
				//Log.d("newFileUri", newFileUri+"");
				
				
		        	try { // 새로운 파일을 InputStream에 넣어주고 전송하기 전 httpUrlconnection의 옵션을 설정한다.
					     // open a URL connection to the Servlet
		                fileInputStream = new FileInputStream(sourceFile);
		               
		                //Log.d("sourceFile", sourceFile+"");
		                //Log.d("fileName", fileName+"");
		                
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
		                conn.setRequestProperty("uploaded_file", fileName);
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
	                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
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
							//System.out.println("받아온 값 : "+response);
							
							String parse_respon[] = response.split(","); // 받아온 값을 파싱한다 . [0] => 업로드한 파일이 위치한 주소, [1] => 성공 여부
							
							
							if(parse_respon[1].equals("success"+"\n")) // 만약 업로드에 성공하였다면!!
							{
								JSONObject Packet2 = new JSONObject();
								Packet2.put("Command", "send_complete");
								Packet2.put("KEY",MainActivity.currentKey+"");
								Packet2.put("FileURI", parse_respon[0]);
								String pac2 = Packet2.toJSONString();

								MainActivity.out.println(pac2); 
								MainActivity.out.flush();
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
		
		private void SaveBitmapToFileCache(Bitmap bitmap, String strFilePath) { // 비트맵을 파일로 만들어줌.
            
	        File fileCacheItem = new File(strFilePath);
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
	  }
		
	}

