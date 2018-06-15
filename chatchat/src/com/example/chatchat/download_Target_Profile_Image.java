package com.example.chatchat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.ImageView;


class download_Target_Profile_Image extends AsyncTask<Void, Void , Bitmap>{

	HttpURLConnection conn;
	InputStream is;
	Bitmap mybitmap;
	ImageView image;
	String fileUri;
	String path;
	Message message;
	byte[] byteURI;
	Bitmap bitmap;
	
	utils ut = new utils();
	
	public download_Target_Profile_Image(String uri) 
	{
		this.fileUri = uri;
//		System.out.println("fileuri " + fileUri);
//		
//		
//		for(int i=0;i<fileUri.length();i++)
//		{
//			System.out.println("["+i+"] : " + fileUri.charAt(i));
//		}
		
	}
	
	 
	
	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
//		System.out.println("In download_Profile_Image onPreExecute");

		URL url;			
		
		try {
			url = new URL(fileUri);
			
			
			try {
				conn = (HttpURLConnection)url.openConnection();
				conn.setConnectTimeout(2000);
				conn.setReadTimeout(2000);
				conn.setDoInput(true);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
//		System.out.println("In download_Profile_Image doInBackground");

		
		try {
			
			try
			{
				is = conn.getInputStream();
				
				byte[] bytes = IOUtils.toByteArray(is);
				
				bitmap = byteArrayToBitmap(bytes);	
			}
			catch(FileNotFoundException e)
			{
				String real_path = fileUri.substring(0, 21).concat(fileUri.substring(22,fileUri.length())); // 21번째에 숨어있는 공백문자 제거.
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
	            	new download_Target_Profile_Image(real_path)
			 		.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
	            else
	            	new download_Target_Profile_Image(real_path).execute((Void[])null);
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		if(bitmap == null)
//		{
//			System.out.println("하아/!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"); 
//		}
//		else
//		{
//			System.out.println("그럼 뭐가 문제냣@@!!!!!!!!!!!!!");
//		}
		
		return bitmap;
		
	}
	
	public Bitmap byteArrayToBitmap( byte[] byteA ) {  // 바이트 어레이를 비트맵으로 변환
	    Bitmap bitmap = BitmapFactory.decodeByteArray( byteA, 0, byteA.length ) ;  
	    return bitmap ;  
	}
	
		
	@Override
	protected void onPostExecute(Bitmap downloaded_Image) {
		super.onPostExecute(downloaded_Image);
		ut.dialog.dismiss();
		
		if(downloaded_Image == null)
		{
			System.out.println("onPostExecute, null"); 
		}
		else
		{
//			System.out.println("onPostExecute not null");
			Over_profile_pic.img.setImageBitmap(downloaded_Image);
		}

		
		
	}
}