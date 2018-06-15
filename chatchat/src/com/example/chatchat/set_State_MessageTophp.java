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


class set_State_MessageTophp extends AsyncTask<Void, Integer , Void>{

	HttpURLConnection conn;
	BufferedWriter bw;
	BufferedReader br;
	String id;
	String server_uri;
	String state_msg;
	Message message;
	Context myContext;
	String nickname;
	
	public set_State_MessageTophp(String uri,String _id, String _state_msg, Context contex) 
	{
		this.server_uri = uri;
		this.id = _id;
		this.state_msg = _state_msg;
		this.myContext = contex;
		nickname = null;
		
		//System.out.println("state_msg : " + state_msg);
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		
		//System.out.println("여기는 onProgressUpdate()");
		
		switch (values[0]) {
		case 0: 
					Profile.adapter.notifyDataSetChanged();
					Toast.makeText(myContext,"성공", Toast.LENGTH_SHORT).show();	 break;
		case 1: 
					Toast.makeText(myContext, "실패", Toast.LENGTH_SHORT).show();	 break;

		default:
					Toast.makeText(myContext, "잘 못된 접근입니다..", Toast.LENGTH_SHORT).show();
			
			break;
		}
		
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		//System.out.println("In set_State_MessageTophp onPreExecute");
		
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
		//System.out.println("In set_State_MessageTophp doInBackground");

		conn.setDoInput(true);
		try {
			bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
			String request="request="+id+","+state_msg;
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

				br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8")); // EUC-KR 로 인코딩하여 받아옴. 
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
					// 첫번째 배열의 값은 성공 여부 , 두번째 배열의 값은 닉네임.
					
					SharedPreferences.Editor editor = MainActivity.chk_login.edit();
						
						editor.putString("state_message", state_msg);
						editor.commit();
						publishProgress(0);
				}
					 // 로그인 성공!
				
		      else{
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