package com.example.chatchat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;


class downloadImage extends AsyncTask<Void, Integer , Void>{

	HttpURLConnection conn;
	InputStream is;
	Bitmap mybitmap;
	ImageView image;
	String fileUri;
	int type;
	int type2;
	String id;
	String time;
	Message message;
	Context context;
	String key;
	
	public downloadImage(String uri, int _type, String _id, String _time, int _type2) 
	{
		this.fileUri = uri;
		this.type = _type;
		this.id = _id;
		this.time = _time;
		this.type2 = _type2;
		
//		System.out.println("In downloadImage, fileUri : "+fileUri);
//		System.out.println("In downloadImage, type : "+type);
//		System.out.println("In downloadImage, id : " + id);
//		System.out.println("In downloadImage, time : " +  time);
//		System.out.println("In downloadImage, type2 : " + type2);
		
	}
	
	public downloadImage(String uri, int _type, String _id, String _time, int _type2, Context contex, String _key) 
	{
		this.fileUri = uri;
		this.type = _type;
		this.id = _id;
		this.time = _time;
		this.type2 = _type2;
		this.context = contex;
		this.key =_key;
		
//		System.out.println("In downloadImage, fileUri : "+fileUri);
//		System.out.println("In downloadImage, type : "+type);
//		System.out.println("In downloadImage, id : " + id);
//		System.out.println("In downloadImage, time : " +  time);
//		System.out.println("In downloadImage, type2 : " + type2);
//		System.out.println("In downloadImage, key : " + key);
		
		
	}
	
	@Override
	protected void onProgressUpdate(Integer[] values) {
		super.onProgressUpdate(values);
		
		
		switch (values[0]) {
		case 0:
			
			try
			{
				System.out.println("여기는 onProgressUpdate()");
				//ChatRoom.Adapter.notifyDataSetChanged();;
				MainActivity.adapter.add(message);
				Allchat.lv.smoothScrollToPosition(MainActivity.adapter.getCount());
				MainActivity.adapter.notifyDataSetChanged();
				
				MainActivity.pass = true;
				MainActivity.pleaseWait = true;
				
			}
			catch(NullPointerException ne)
			{
				System.out.println("In downloadImage, NullPointException");
			}
			
			break;

		case 1: 
			
			MainActivity.f_chat_adapter.notifyDataSetChanged();
			
			break;
			
		case 2:
			try
			{
				System.out.println("여기는 onProgressUpdate()");
				//ChatRoom.Adapter.notifyDataSetChanged();;
				MainActivity.adapter2.add(message);
				personalChat.lv.smoothScrollToPosition(MainActivity.adapter2.getCount());
				MainActivity.adapter2.notifyDataSetChanged();
				MainActivity.pass = true;
				MainActivity.pleaseWait = true;
				
			}
			catch(NullPointerException ne)
			{
				System.out.println("In downloadImage, NullPointException");
			}
			
			
			break;
		default:
			break;
		}
		
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
//		System.out.println("In downloadImage onPreExecute");
		
		URL url;			
		
		try {
			url = new URL(fileUri);
			
			try {
				conn = (HttpURLConnection)url.openConnection();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
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
		System.out.println("In downloadImage doInBackground");

		try {
			is = conn.getInputStream();
			
			byte[] bytes = IOUtils.toByteArray(is);
			
			switch (type2) {
			case 0:
				message = new Message(type,id,bytes, time);
				publishProgress(0);
				break;
				
			case 1: // 개인 채팅방에서 사진을 다운로드 받을때.
				//System.out.println("case 1");
				Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				
				/*
				 * 파일의 경로를 얻고 경로와 함께 sqlite에 동기화해야함.
				 * */
				
				Long ff = System.currentTimeMillis();
				String file = ff.toString()+".jpg";
				
				FileOutputStream out = context.openFileOutput(file, context.MODE_PRIVATE);
				bitmap.compress(CompressFormat.JPEG, 100, out);
				
				 GregorianCalendar gc = new GregorianCalendar();
					
//				 SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss"); // 기본 데이타베이스 저장 타입
//			     Date d =  gc.getTime(); // Date -> util 패키지
//			     String str = sf.format(d);
			        //System.out.println(str);
			        
			        //System.out.println("파일 이름 : " + " data/data/com.example.chatchat/files/"+ file);
			        
				MainActivity.dbManager.sql_query("insert into chat_log(chat_sequence, state, nick, msg, created_date, created_time, type)"
						+ " values('"+key+"',"+1+", '" + id + "' , '"+"data/data/com.example.chatchat/files/"+ file +"' , datetime('now','localtime'),'"+ time+"',"+type+" );");
				
				message = new Message(type, id,bytes, time);
				publishProgress(2);
				
				break;

			case 2: 
//				System.out.println("case 2");
				Bitmap bitmap2 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				
				/*
				 * 파일의 경로를 얻고 경로와 함께 sqlite에 동기화해야함.
				 * */
				
				Long ff2 = System.currentTimeMillis();
				String file2 = ff2.toString()+".jpg";
				
				FileOutputStream out2 = context.openFileOutput(file2, context.MODE_PRIVATE);
				
				bitmap2.compress(CompressFormat.JPEG, 100, out2);
				
				 GregorianCalendar gc2 = new GregorianCalendar();
					
//				 SimpleDateFormat sf2 = new SimpleDateFormat("yyyyMMddHHmmss"); // 기본 데이타베이스 저장 타입
//			        Date d2 =  gc2.getTime(); // Date -> util 패키지
//			        String str2 = sf2.format(d2);
			        //System.out.println(str2);
			        
			        //System.out.println("파일 이름2 : " + " data/data/com.example.chatchat/files/"+ file2);
			        
				MainActivity.dbManager.sql_query("insert into chat_log(chat_sequence, state, nick, msg, created_date, created_time, type) "
						+ "values('"+key+"',"+0+", '" + id + "' , '"+"data/data/com.example.chatchat/files/"+ file2 +"' , datetime('now','localtime'),'"+ time+"',"+type+" );");
				
				MainActivity.pass = true;
				MainActivity.pleaseWait = true;
				
				break;
				
			case 3: 
				//System.out.println("case 3");
				Bitmap bitmap3 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				
				/*
				 * 파일의 경로를 얻고 경로와 함께 sqlite에 동기화해야함.
				 * */
				
				Long ff3 = System.currentTimeMillis();
				String file3 = ff3.toString()+".jpg";
				
				FileOutputStream out3 = context.openFileOutput(file3, context.MODE_PRIVATE);
				bitmap3.compress(CompressFormat.JPEG, 100, out3);
				
				GregorianCalendar gc3 = new GregorianCalendar();
					
//				SimpleDateFormat sf3 = new SimpleDateFormat("yyyyMMddHHmmss"); // 기본 데이타베이스 저장 타입
//		        Date d3 =  gc3.getTime(); // Date -> util 패키지
//		        String str3 = sf3.format(d3);
		        //System.out.println(str3);
		        
		        //System.out.println("파일 이름 : " + " data/data/com.example.chatchat/files/"+ file3);
			        
		        DBManager dbManager = new DBManager(context, "chat.db", null, 1);
		        
				dbManager.sql_query("insert into chat_log(chat_sequence, state, nick, msg, created_date, created_time, type)"
						+ " values('"+key+"',"+0+", '" + id + "' , '"+"data/data/com.example.chatchat/files/"+ file3 +"' , datetime('now','localtime'),'"+ time+"',"+type+" );");
				
//				publishProgress(2);
				
				break;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
		
	@Override
	protected void onPostExecute(Void downloaded_Image) {
		super.onPostExecute(downloaded_Image);
		
	}
}