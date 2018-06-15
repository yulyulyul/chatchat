package com.example.chatchat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;


class download_Profile_Image extends AsyncTask<Void, Void , Void>{

	
	HttpURLConnection conn;
	InputStream is;
	Bitmap mybitmap;
	ImageView image;
	String fileUri;
	String id;
	String path;
	Message message;
	byte[] byteURI;
	
	utils ut = new utils();
	
	public download_Profile_Image(String uri, String _id) 
	{
		this.fileUri = uri;
		this.id = _id;
		//System.out.println("fileuri " + fileUri);
		
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		//System.out.println("In download_Profile_Image onPreExecute");
		
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
	protected Void doInBackground(Void... params) {
		//System.out.println("In download_Profile_Image doInBackground");

		try {
			is = conn.getInputStream();
			
			byte[] bytes = IOUtils.toByteArray(is);
			
			Bitmap bitmap = byteArrayToBitmap(bytes);
			
			
			String fileName = fileUri.substring(fileUri.lastIndexOf("/")+1, fileUri.length());
			
			//System.out.println("In download_Profile_Image, fileName : " + fileName);
			String local_path = ut.saveBitmaptoJpeg(bitmap, "chatchat_profile", fileName); 
			
			//System.out.println("로컬에 저장한 파일의 경로를 sharedPreference로 기기에 저장.");
			SharedPreferences.Editor editor = MainActivity.chk_login.edit();
			editor.putString("local_profilePATH", local_path);
	        editor.commit();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public Bitmap byteArrayToBitmap( byte[] byteA ) {  // 바이트 어레이를 비트맵으로 변환
	    Bitmap bitmap = BitmapFactory.decodeByteArray( byteA, 0, byteA.length ) ;  
	    return bitmap ;  
	}
	
		
	@Override
	protected void onPostExecute(Void downloaded_Image) {
		super.onPostExecute(downloaded_Image);

		
	}
}