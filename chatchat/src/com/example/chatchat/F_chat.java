package com.example.chatchat;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class F_chat extends Activity {

	@Override
	protected void onResume() {
		super.onResume();
		MainActivity.abar.setTitle("개인 채팅방");
		MainActivity.f_chat_adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_f_chat);
		
		final ListView list = (ListView)findViewById(R.id.f_chat_List);
		View header = getLayoutInflater().inflate(R.layout.activity_header2, null, false);
		TextView title = (TextView)header.findViewById(R.id.header);
		title.setText("대화");
		
		list.setEmptyView(findViewById(R.id.empty3));
		list.setAdapter(MainActivity.f_chat_adapter);
		list.setBackgroundColor(Color.WHITE);
		list.setDividerHeight(2);
		
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.f_chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
class FriendView3 // 친구 리스트에 올라가는 데이터에 대한 객체화.!
{
	RoundedAvatarDrawable img;
	String name;
	String last;
	String time;
	String key;
	
	public FriendView3(RoundedAvatarDrawable _img, String _name, String _last, String _time, String _key) 
	{
		this.img = _img;
		this.name = _name;
		this.last = _last;
		this.time = _time;
		this.key = _key;
	}

	public RoundedAvatarDrawable getImg() {
		return img;
	}

	public void setImg(RoundedAvatarDrawable img) {
		this.img = img;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}