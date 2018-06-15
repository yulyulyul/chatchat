package com.example.chatchat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView.FindListener;
import android.widget.Toast;


class MembershipTophp extends AsyncTask<Void, Integer , Void>{

	HttpURLConnection conn;
	BufferedWriter bw;
	BufferedReader br;
	String server_uri;
	String Data;
	Message message;
	Context myContext;
	Activity membershipActivity;
	
	public MembershipTophp(String uri, String _Data, Context contex, Activity _ac) 
	{
		this.server_uri = uri;
		this.Data = _Data;
		this.myContext = contex;
		this.membershipActivity = _ac;
		
		//System.out.println("Data : "+Data);
		
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		
		//System.out.println("여기는 onProgressUpdate()");
		
		switch (values[0]) {
		case 0: 
			
			//Activity memAct = (Activity)f
			
			Toast.makeText(myContext, "회원가입 성공", Toast.LENGTH_SHORT).show();	
			membershipActivity.finish();
					break;
		case 1: 
					Membership.et_nickname.setText("");
					Toast.makeText(myContext, "이미 등록된 닉네임입니다. 다시 설정해주세요.", Toast.LENGTH_LONG).show();	 break;
					
		default:
					Toast.makeText(myContext, "잘 못된 접근입니다..", Toast.LENGTH_SHORT).show();
			
			break;
		}
		
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		//System.out.println("In MembershipTophp onPreExecute");
		
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
		//System.out.println("In MembershipTophp doInBackground");

		conn.setDoInput(true);
		try {
			bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
			String request="request="+Data;
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
				
		        if(response.equals("success")){
		        	publishProgress(0); // 회원가입 성공!
		        
		     }	
		     else{
		    	 publishProgress(1); // 회원가입 실패! --> 이미 nickname이 존재함.
		     }
				
				
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