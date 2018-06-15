package com.example.chatchat;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;


class getVersionCode extends AsyncTask<Void, Integer , String>{

	HttpURLConnection conn;
	BufferedWriter bw;
	BufferedReader br;
	String server_uri;
	Context myContext;
	String current_version_code;
	
	public getVersionCode(String uri, Context mContext, String _current_version_code) 
	{
		this.server_uri = uri;
		this.myContext = mContext;
		this.current_version_code = _current_version_code;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		
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
	protected String doInBackground(Void... params) {
		//System.out.println("In loginTophp doInBackground");


		String response="";
		try {
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
			StringBuilder sb = new StringBuilder();
			String line;
			while((line=br.readLine()) != null){
				sb.append(line);
			}
			
			br.close();
			
			response=sb.toString();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // EUC-KR 로 인코딩하여 받아옴. 
		String version="";
		try {
			int responseCode = conn.getResponseCode();

			if(responseCode == HttpURLConnection.HTTP_OK){ //만약 HTTP_OK 신호가 들어온다면! (200)

				Log.d("response", response);
				String versionCode[] = response.split(",");
				version = versionCode[1];
			}
			else
			{
				Log.d("fail, responseCode", responseCode + "");
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return version;
		
	}
	 
	@Override
	protected void onPostExecute(String Code) {
		super.onPostExecute(Code);
		
//		Toast.makeText(myContext, Code + "", Toast.LENGTH_LONG).show();
		
		if(Code.equals(current_version_code))
		{
//			Toast.makeText(myContext, "현재 어플의 version Code와 서버의 version Code가 동일합니다.", Toast.LENGTH_LONG).show();
		}
		else
		{
			
        	AlertDialog.Builder aDialog = new AlertDialog.Builder(myContext);

    		aDialog.setTitle("업데이트");
    		aDialog.setMessage("새로운 버전이 존재합니다.\n 업데이트를 진행 하시겠습니까?");
    		aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {				
    			public void onClick(DialogInterface dialog, int which) 
    			{
    				Toast.makeText(myContext, "어플의 다운로드를 시작합니다.", Toast.LENGTH_LONG).show();
    				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
    	    			new apk_update("http://115.71.237.99/newVersion/chatchat.apk", myContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    	            else
    	            	new apk_update("http://115.71.237.99/newVersion/chatchat.apk",myContext).execute((Void[])null);	
    			}
    						
    		});
    		
    		aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					System.exit(0);
					
				}
			});

    		final AlertDialog ad = aDialog.create();
    		ad.show();
			
			
//			MainActivity.handler.sendEmptyMessage(3);
			
		}
		
		
	}
}