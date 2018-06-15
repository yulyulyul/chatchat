package com.example.chatchat;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Friend extends Activity {
	
	int sibalFInal;
	utils ut;
	static public View f_touch; // 친구 추가 요청에 있는 목록에 존재하는 사람의 View
	static public ImageView f_friend_profile;
	static public Button chat;
	
	@Override
	protected void onResume() {
		super.onResume();
		MainActivity.abar.setTitle("친구");
		MainActivity.req_f_count.setVisibility(View.INVISIBLE);
		
		MainActivity.state = "Friend";
		//Log.d("STATE", MainActivity.state);
	}
	
	@Override
	public void onBackPressed() {
			

			if(MainActivity.detect_f_profile_touch == true)
			{
				Friend.f_touch.setVisibility(View.INVISIBLE);
				MainActivity.detect_f_profile_touch = false;
				return;
			}
			
			else
			{
				super.onBackPressed();
			}
			
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend); // activity_friends가 친구 리스트 !
		
		ut = new utils();
		
		final ListView list = (ListView)findViewById(R.id.f_List2);
		View header = getLayoutInflater().inflate(R.layout.activity_header2, null, false);
		TextView title = (TextView)header.findViewById(R.id.header);
		title.setText("친구");
		
		f_touch = (View)findViewById(R.id.f_profile);
		f_friend_profile = (ImageView)findViewById(R.id.f_profile_IMAGE);
		chat = (Button)findViewById(R.id.chat_btn);
		
		//list.addHeaderView(header);
		list.setAdapter(MainActivity.f_list_adapter);
		list.setBackgroundColor(Color.WHITE);
		list.setEmptyView(findViewById(R.id.empty2));
		list.setDividerHeight(2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friend, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
}


class FriendView2 // 친구 리스트에 올라가는 데이터에 대한 객체화.!
{
	RoundedAvatarDrawable img;
	String name;
	String state_msg;
	
	public FriendView2(RoundedAvatarDrawable _img, String _name, String _state_msg) 
	{
		this.img = _img;
		this.name = _name;
		this.state_msg = _state_msg;
	}
	
	public RoundedAvatarDrawable getImg() {
		return img;
	}
	public void setImg(RoundedAvatarDrawable img) {
		this.img = img;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getState_msg() {
		return state_msg;
	}
	public void setState_msg(String state_msg) {
		this.state_msg = state_msg;
	}
}

class friends_list
{
	String img_path;
	String name;
	String state_msg;
	
	public friends_list(String _img_path, String _name, String _state_msg)
	{
		this.img_path = _img_path;
		this.name = _name;
		this.state_msg = _state_msg;
	}
	
	public String getImg_path() {
		return img_path;
	}
	public void setImg_path(String img_path) {
		this.img_path = img_path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getState_msg() {
		return state_msg;
	}
	public void setState_msg(String state_msg) {
		this.state_msg = state_msg;
	}
	
	
}