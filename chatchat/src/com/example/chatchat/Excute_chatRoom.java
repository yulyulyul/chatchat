package com.example.chatchat;

import java.io.PrintWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Excute_chatRoom  extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		
		String getIntent = intent.getAction();
		String msg = intent.getExtras().getString("MSG");
		String f_name = intent.getExtras().getString("f_name");
		
		MainActivity ma = new MainActivity();
		utils ut = new utils();
		
		//System.out.println("In Excute_chatRoom : "+msg);
		

		 MainActivity.dbManager.sql_query("update chat_log set state = 1 where state = 0;"); // 이전에 받았던 모든 메시지들의 state를 전부 1로 바꿈.
		
		 personalChat.With_friend = f_name;

		 String sequence = null;
		 String chat_list = MainActivity.chk_login.getString("chat_list", "[]");
		 
		 //Log.d("chat_list", chat_list+"");
		 
		 JSONParser jsonParser2 = new JSONParser();
		 JSONArray jarr2;
		 try 
		 {
			 jarr2 = (JSONArray) jsonParser2.parse(chat_list);
			 
			 //Log.d("jarr2.size()", jarr2.size()+"");
			 for(int i=0;i<jarr2.size();i++)
			 {
				 JSONObject j = (JSONObject)jarr2.get(i);
				 
				 String seq = (String)j.get("seq");
				 String f_nick = (String)j.get("f_nick");
				 
				 //Log.d("seq", seq+"");
				 //Log.d("f_nick", f_nick+"");
				 //Log.d("f_name", f_name+"");
				 
				 if(f_name.equals(f_nick))
				 {
					 sequence = seq;
					 MainActivity.currentKey = seq;
					
					 JSONObject packet = new JSONObject();
					 packet.put("Command", "save_KEY_USERS");
					 packet.put("seq", seq);
					 packet.put("my_nick", MainActivity.MyNickname);
					 packet.put("f_nick", f_nick);
						
					 String pac3 = packet.toJSONString();
					 MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
					 MainActivity.out.println(pac3);
					 MainActivity.out.flush();
						
					 //Log.d("currentKey", MainActivity.currentKey+"");
					 Intent execute = new Intent(context, personalChat.class);
					 execute.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 execute.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					 execute.putExtra("Title", f_nick);
					 context.startActivity(execute);
					 
					 break;
				 }
			 } 

		 } 
		 catch (ParseException e) 
		 {
	 		 e.printStackTrace();
		 } 
		
		 SQLiteDatabase db = MainActivity.dbManager.getReadableDatabase();
		 //Log.d("sequence", sequence);
		 
		 String str = "";
		 Cursor cursor = db.rawQuery("select * from chat_log where chat_sequence = '"+sequence+"';", null);
	        while(cursor.moveToNext()) {
	            str= cursor.getString(1)+","+cursor.getInt(2)+","+cursor.getString(3)+","+cursor.getString(4)+","+cursor.getString(5)
	            		+","+cursor.getString(6)+","+","+cursor.getInt(7);
//	            Log.d("chat_log", str);
	            
	            Message message = null;
	            switch (cursor.getInt(7)) {
				
	            case 0:
	            	message = new Message(cursor.getInt(7), cursor.getString(3), cursor.getString(4),cursor.getString(6));
//	            	//Log.d("case 0", cursor.getInt(7)+","+ cursor.getString(3)+","+ cursor.getString(4)+","+cursor.getString(6));
	            	
	            	MainActivity.adapter2.add(message);
					MainActivity.adapter2.notifyDataSetChanged();
					
					break;
	           
	            case 1:
	            	message = new Message(cursor.getInt(7), cursor.getString(4));
//	            	//Log.d("case 1", cursor.getInt(7)+","+ cursor.getString(4));
	            	
	            	MainActivity.adapter2.add(message);
					MainActivity.adapter2.notifyDataSetChanged();
					
					break;
					
					
	            case 2:
	            	
	            	message = new Message(cursor.getInt(7), cursor.getString(3), cursor.getString(4),cursor.getString(6));
//	            	//Log.d("case 2", cursor.getInt(7)+","+ cursor.getString(3)+","+ cursor.getString(4)+","+cursor.getString(6));
					
	            	MainActivity.adapter2.add(message);
					MainActivity.adapter2.notifyDataSetChanged();
					
	            	break;
	            	
	            case 3:
	            	String path = cursor.getString(4);
	            	Bitmap bm = BitmapFactory.decodeFile(path);
	            	
	            	byte[] bytes = ut.bitmapToByteArray(bm);
	            	
	            	message = new Message(cursor.getInt(7),cursor.getString(3),bytes, cursor.getString(6));
//	            	//Log.d("case 3", cursor.getInt(7)+","+ cursor.getString(3)+","+ cursor.getString(4)+","+cursor.getString(6));
	            	
	            	MainActivity.adapter2.add(message);
					MainActivity.adapter2.notifyDataSetChanged();
	            	break;
	            	
	            case 4:
	            	String path2 = cursor.getString(4);
	            	Bitmap bm2 = BitmapFactory.decodeFile(path2);
	            	
	            	byte[] bytes2 = ut.bitmapToByteArray(bm2);
	            	
	            	message = new Message(cursor.getInt(7),cursor.getString(3),bytes2, cursor.getString(6));		
//	            	//Log.d("case 4", cursor.getInt(7)+","+ cursor.getString(3)+","+ cursor.getString(4)+","+cursor.getString(6));
	            	
	            	MainActivity.adapter2.add(message);
					MainActivity.adapter2.notifyDataSetChanged();
	            	break;

				default:
					
					break;
				}
	        }
	
	        
	}

}
