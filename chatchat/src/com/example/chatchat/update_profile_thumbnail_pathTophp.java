package com.example.chatchat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.simple.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


class update_profile_thumbnail_pathTophp extends AsyncTask<Void, Integer , Void>{

	HttpURLConnection conn;
	BufferedWriter bw;
	BufferedReader br;
	String id;
	String server_uri;
	String path;
	Message message;
	String nickname;
	
	public update_profile_thumbnail_pathTophp(String uri,String _id, String _path) 
	{
		this.server_uri = uri;
		this.id = _id;
		this.path = _path;
		nickname = null;
		
		//System.out.println("서버로 보내기 전! 주소 : " + path);
		
//		//System.out.println("=== 검사(update_profile_thumbnail_pathTophp) == ");
//		for(int i=0;i<path.length();i++)
//		{
//			//System.out.println("uriFromphp["+i+"] : " + path.charAt(i));
//		}
//		//System.out.println("\n===================\n");
		
		String real_path = path.substring(0, 21).concat(path.substring(22,path.length()));
		
//		//System.out.println("=== 검사(update_profile_thumbnail_pathTophp) 후 == ");
//		for(int i=0;i<real_path.length();i++)
//		{
//			//System.out.println("real_path["+i+"] : " + real_path.charAt(i));
//		}
//		//System.out.println("\n===================\n");
		
		path = real_path;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		
//		//System.out.println("여기는 onProgressUpdate()");
//		
//		switch (values[0]) {
//		case 0: 
//					Profile.adapter.notifyDataSetChanged();
//					Toast.makeText(myContext,"성공", Toast.LENGTH_SHORT).show();	 break;
//		case 1: 
//					Toast.makeText(myContext, "실패", Toast.LENGTH_SHORT).show();	 break;
//
//		default:
//					Toast.makeText(myContext, "잘 못된 접근입니다..", Toast.LENGTH_SHORT).show();
//			
//			break;
//		}
		
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		//System.out.println("In update_profile_thumbnail_pathTophp onPreExecute");
		
		URL url;			
		
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
		//System.out.println("In update_profile_thumbnail_pathTophp doInBackground");

		conn.setDoInput(true);
		try {
			bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
			String request="request="+id+"," + path;
			//Log.d("request", request+"");
			String temp = request.trim();
			bw.write(temp);
			bw.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String response = null;
		
		try {
			
			int responseCode = conn.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK){ //만약 HTTP_OK 신호가 들어온다면! (200)

				br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8")); 
				StringBuilder sb = new StringBuilder();
				String line;
				
				while((line=br.readLine()) != null){
					sb.append(line);
				}
				
				br.close();
				
				response=sb.toString();
				
				//System.out.println("받아온 값 : "+response);
				
				if(response.equals("1"))
				{
					//System.out.println("db에 썸네일 주소 수정 완료!");
						publishProgress(0);
				}
				
		      else
		      	{
		      	}
		
			}
			else
			{
				//System.out.println("http 실패");
			}
			
			
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return null;
		
	}
		
	@Override
	protected void onPostExecute(Void downloaded_Image) {
		super.onPostExecute(downloaded_Image);
		
	}
}