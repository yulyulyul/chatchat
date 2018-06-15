package com.example.chatchat;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Requset_Friends extends Activity {

//	static ArrayList<FriendView> fv;
//	static friend_Adapter adapter;
	static public View f_pro_pic_touch; // 친구 추가 요청에 있는 목록에 존재하는 사람의 View
	static public ImageView f_request_friend_profile;
	static public Button minus;
	static public Button plus;
	
	
	ActionBar thisBar;
	
	@Override
	protected void onResume() {
		super.onResume();
		MainActivity.abar.setTitle("친구 요청");
		thisBar = getActionBar();
		thisBar.hide();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		thisBar.show();
		
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends); // activity_friends가 친구 추가 요청!
		
//		fv = new ArrayList<FriendView>();
//		adapter = new friend_Adapter(fv);
		final ListView list = (ListView)findViewById(R.id.friendList);
		View header = getLayoutInflater().inflate(R.layout.activity_header2, null, false);
		
		TextView title = (TextView)header.findViewById(R.id.header);
		
		f_pro_pic_touch = (View)findViewById(R.id.f_profile);
		f_request_friend_profile = (ImageView)findViewById(R.id.f_profile_IMAGE);
		minus = (Button)findViewById(R.id.f_delete_friend);
		plus = (Button)findViewById(R.id.f_add_friend);
		
		title.setText("친구 요청");
		title.setBackground(new ColorDrawable(Color.parseColor("#20BAEF")));
//		list.addHeaderView(header);
		list.setAdapter(MainActivity.Fadapter);
		list.setEmptyView(findViewById(R.id.empty88));
//		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.setBackgroundColor(Color.WHITE);
		list.setDividerHeight(2);
		
	}
	@Override
	public void onBackPressed() {
		
		//System.out.println("back 버튼이 눌렸음.");
		if(MainActivity.detect_f_profile_touch == true)
		{
			//System.out.println("여기가 실행 될까요?");
			Requset_Friends.f_pro_pic_touch.setVisibility(View.INVISIBLE);
			MainActivity.detect_f_profile_touch = false;
			return;
		}
		
		else
		{
			super.onBackPressed();
		}
		
//		return;
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friends, menu);
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

class FriendView
{
	RoundedAvatarDrawable img;
	String name;
	String state_msg;
	
	public FriendView(RoundedAvatarDrawable _img, String _name) 
	{
		this.img = _img;
		this.name = _name;
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
