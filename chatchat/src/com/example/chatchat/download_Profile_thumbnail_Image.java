package com.example.chatchat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;


class download_Profile_thumbnail_Image extends AsyncTask<Void, Void , Void>{

	HttpURLConnection conn;
	InputStream is;
	Bitmap mybitmap;
	ImageView image;
	String fileUri;
	String id;
	String path;
	String state_msg;
	String last;
	String time;
	Message message;
	String seq;
	Context mContext;
	int type;
	byte[] byteURI;
	SharedPreferences go_chatroom;
	String gogo;
	
	utils ut = new utils();
	
	public download_Profile_thumbnail_Image(String uri, String _id, int _type) 
	{
		this.fileUri = uri;
		this.id = _id;
		this.type = _type;
		
		//System.out.println("download_Profile_thumbnail_Image, fileuri " + fileUri);
		//System.out.println("download_Profile_thumbnail_Image, id : " + id);
		//System.out.println("download_Profile_thumbnail_Image, type : " + type);
		
		
//		System.out.println("=== 검사 == ");
//		for(int i=0;i<fileUri.length();i++)
//		{
//			System.out.println("fileUri["+i+"] : " + fileUri.charAt(i));
//		}
//		System.out.println("\n===================\n");
		
	}
	
	public download_Profile_thumbnail_Image(String uri, String _id, String _state_msg, int _type) 
	{
		this.fileUri = uri;
		this.id = _id;
		this.state_msg = _state_msg;
		this.type = _type;
		
//		System.out.println("download_Profile_thumbnail_Image, fileuri " + fileUri);
//		System.out.println("download_Profile_thumbnail_Image, id : " + id);
//		System.out.println("download_Profile_thumbnail_Image, state_msg : " + state_msg);
//		System.out.println("download_Profile_thumbnail_Image, type : " + type);
		
		
//		System.out.println("=== 검사 == ");
//		for(int i=0;i<fileUri.length();i++)
//		{
//			System.out.println("fileUri["+i+"] : " + fileUri.charAt(i));
//		}
//		System.out.println("\n===================\n");
		
	}
	
	public download_Profile_thumbnail_Image(String uri, String _id, String _last, String _time, int _type) 
	{
		this.fileUri = uri;
		this.id = _id;
		this.last = _last;
		this.time = _time;
		this.type = _type;

//		System.out.println("download_Profile_thumbnail_Image, fileuri " + fileUri);
//		System.out.println("download_Profile_thumbnail_Image, id : " + id);
//		System.out.println("download_Profile_thumbnail_Image, last : " + last);
//		System.out.println("download_Profile_thumbnail_Image, time : " + time);
//		System.out.println("download_Profile_thumbnail_Image, type : " + type);
	}
	
	public download_Profile_thumbnail_Image(String uri, String _id, String _last, String _time, int _type, String _seq, Context _mContext) 
	{
		this.fileUri = uri;
		this.id = _id;
		this.last = _last;
		this.time = _time;
		this.type = _type;
		this.seq = _seq;
		this.mContext = _mContext;
		
		go_chatroom = mContext.getSharedPreferences("go_chatroom", mContext.MODE_PRIVATE);
		gogo = go_chatroom.getString("name", "no");
		
//		System.out.println("download_Profile_thumbnail_Image, fileuri " + fileUri);
//		System.out.println("download_Profile_thumbnail_Image, id : " + id);
//		System.out.println("download_Profile_thumbnail_Image, last : " + last);
//		System.out.println("download_Profile_thumbnail_Image, time : " + time);
//		System.out.println("download_Profile_thumbnail_Image, type : " + type);
//		System.out.println("download_Profile_thumbnail_Image, seq : " + seq);
		
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
//		System.out.println("In download_Profile_thumbnail_Image onPreExecute");
		
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
//		System.out.println("In download_Profile_thumbnail_Image doInBackground");
		 
		try {
			is = conn.getInputStream();
			
			byte[] bytes = IOUtils.toByteArray(is);
			
			Bitmap bitmap = byteArrayToBitmap(bytes);
			
			switch (type) {
			
			case 1: 
				MainActivity.NICK_and_BITMAP.put(id, bitmap);
//				System.out.println("NICK_and_BITMAP의 크기 : " + MainActivity.NICK_and_BITMAP.size());
//				System.out.println("id : " + id);
				
				break;
				
			case 2: //썸네일 비트맵을 다운받아 friendView 객체에 집어넣는다.
				MainActivity.NICK_and_BITMAP.put(id, bitmap);
				 Bitmap target_bmp = Bitmap.createScaledBitmap(bitmap,100, 100, true);
				 RoundedAvatarDrawable tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(target_bmp);
				 
				 FriendView fv = new FriendView(tmpRoundedAvatarDrawable, id);
				 MainActivity.Fadapter.add(fv);
				
				break;
				
			case 3:
				 MainActivity.NICK_and_BITMAP.put(id, bitmap);
//				 Bitmap target_bmp2 = Bitmap.createScaledBitmap(bitmap,100, 100, true);
//				 RoundedAvatarDrawable tmpRoundedAvatarDrawable2 = new RoundedAvatarDrawable(target_bmp2);
//				 FriendView2 fv2 = new FriendView2(tmpRoundedAvatarDrawable2, id, state_msg);
//				 MainActivity.f_list_adapter.add(fv2);
				 
				 break;
				 
			case 4:
				
				 MainActivity.NICK_and_BITMAP.put(id, bitmap);
				 Bitmap target_bmp3 = Bitmap.createScaledBitmap(bitmap,100, 100, true);
				 RoundedAvatarDrawable tmpRoundedAvatarDrawable3 = new RoundedAvatarDrawable(target_bmp3);
				 FriendView3 fv3 = new FriendView3(tmpRoundedAvatarDrawable3, id, last, time, seq);
				 MainActivity.f_chat_adapter.add(fv3);
				 
//				 if(!gogo.equals("no") && id.equals(gogo))
//				 {
//				 	//Log.d("친구 이름!", gogo);
//					
//					Intent intent = new Intent(mContext, Header.class);
//					intent.putExtra("f_name", gogo);
//					mContext.startActivity(intent);
//					
//					SharedPreferences.Editor editor = go_chatroom.edit();
//					editor.putString("name", "no");
//					editor.commit();
//					
//				}
				 break;
			default:
				break;
			}
			
			
			
//			String local_path = ut.saveBitmaptoJpeg(bitmap, "chatchat_profile", MainActivity.MySeq);
//			
//			System.out.println("로컬에 저장한 파일의 경로를 sharedPreference로 기기에 저장.");
//			SharedPreferences.Editor editor = MainActivity.chk_login.edit();
//			editor.putString("local_profilePATH", local_path);
//	        editor.commit();
			
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