package com.example.chatchat;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatRoom extends Activity {
	
	static ArrayList<MainView> mainView;
	
	EditText Et_SetTitle; //전체 대화방 설정에서 방 제목이 쓰여질 EditText
	EditText Et_MaxPeople; // 전체 대화방 설정에서 최대 인원이 쓰여질 EditText.
	
	static MAdapter Madapter;
	static HashMap<String, Integer> TitleKey; // 전체 대화방 제목과 서버로 부터 받은 key를 저장.
	static String tempTitle; 
	
	public long startTime = 0;
	public long endTime = 0;
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//System.out.println("onResume");
		MainActivity.abar.setTitle("전체 대화방");
		
		MainActivity.state = "ChatRoom";
		//Log.d("STATE", MainActivity.state);
		
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_room);
		
		TitleKey = new HashMap<String, Integer>();
		mainView = new ArrayList<MainView>();
		
		Madapter =  new MAdapter(mainView);
		//Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, chattingRoom);
		final ListView list = (ListView)findViewById(R.id.listView1);
		
		Button btn = (Button)findViewById(R.id.plus);
		
		list.setAdapter(Madapter);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.setBackgroundColor(Color.WHITE);
		list.setEmptyView(findViewById(R.id.empty77));
		list.setDividerHeight(2);
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				settingDialog();
			}
		});
		
		
//		new Thread(new Runnable() { // chattingRoom의 크기를 저장해놓고 그 크기가 변화한다면 -> 새로운 값이 추가 된다면! Adapter를 notify 시켜준다.!
//			
//			int arraySize = mainView.size();
//			
//			@Override
//			public void run() {
//				
//				while(true)
//				{
//					if(arraySize != mainView.size())
//					{
//						System.out.println("데이터 변화!~!");
//						
//						try
//						{
//							runOnUiThread(new Runnable(){
//
//								@Override
//								public void run() {
//									System.out.println("1");
//									Madapter.notifyDataSetChanged();
//									System.out.println("2");
//								}
//								
//							});
//							
//						}catch(Exception e)
//						{
//							
//						}
//						arraySize = mainView.size();
//						System.out.println("숫자 변화!");
//					}
//					
//				}
//			}
//		}).start();
		
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				
				startTime = System.currentTimeMillis();
				
				long lTime = startTime -  endTime;
				
				if(lTime > 0 && lTime<1000)
				{
					
				}
				else
				{
					try{
						//System.out.println("position의 값 : "+position);
						String TITLE;
						//String TITLE = chattingRoom.get(position-1);
						if(mainView.get(position).getTitle() != null)
						{
							TITLE = mainView.get(position).getTitle(); //여기서 싱크가 않맞을 수 도 있음.
						}
						else{
							TITLE = "position's error";
						}
						
						JSONObject packet = new JSONObject();
						packet.put("Command", "EnjoyChat");
						packet.put("Title", TITLE);
						String pac = packet.toJSONString();
						
						MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
						MainActivity.out.println(pac); // 방 제목 전달
						MainActivity.out.flush();

					}
					catch(Exception e)
					{
						System.out.println("오류 발생!!");
					}
				}
				
				
				endTime = startTime;
			}
		});
	}
	
	
	public void settingDialog() // 전체 대화방을 생성할때 설정하는 기본 설정.
	{
		LayoutInflater inflater = (LayoutInflater) ChatRoom.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        
    	View layout2 = new  View(this);
    	
    	ViewGroup app = ((ViewGroup) findViewById(R.id.LinearLayout2)); 
    	layout2 = inflater.inflate(R.layout.activity_setting_chat_room,app,false);

		AlertDialog.Builder aDialog = new AlertDialog.Builder(ChatRoom.this);

		//aDialog.setTitle("설정");
		aDialog.setView(layout2);

		final AlertDialog ad = aDialog.create();
		ad.show();
		
		TextView SetTitle = (TextView)ad.findViewById(R.id.SetTitle);
		TextView MaxPeople = (TextView)ad.findViewById(R.id.MaxPeople);
		Button negative_btn = (Button)ad.findViewById(R.id.nbtn);
		Button positive_btn = (Button)ad.findViewById(R.id.pbtn);
		
		
		Et_SetTitle = (EditText)ad.findViewById(R.id.Et_SetTitle);
		Et_MaxPeople = (EditText)ad.findViewById(R.id.Et_MaxPeople);
		
		
		negative_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(ChatRoom.this, "설정이 취소되었습니다.", Toast.LENGTH_SHORT).show();
				
				ad.dismiss();
			}
		});
		
		positive_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try
				{
					if(Et_MaxPeople.getText().toString().equals("") || Et_MaxPeople.getText().toString().length() == 0 
							|| Et_SetTitle.getText().toString().equals("") || Et_SetTitle.getText().toString().length() == 0)
					{
						Toast.makeText(getApplicationContext(), "빈 값 이 있는지 확인해주세요.", Toast.LENGTH_SHORT).show();
					}
					
					else if(Integer.parseInt(Et_MaxPeople.getText().toString()) == 0)
					{
						Toast.makeText(getApplicationContext(), "최대 인원을 1명 이상으로 설정해주세요.", Toast.LENGTH_SHORT).show();
					}
					
					else
					{
						JSONObject packet = new JSONObject();
						packet.put("Command", "makeTheRoom");
						packet.put("Title", Et_SetTitle.getText().toString());
						packet.put("MaxPeople", Et_MaxPeople.getText().toString());
						
						String pac = packet.toJSONString();
						
						MainActivity.out = new PrintWriter(MainActivity.networkWriter, true);
						//String return_msg = "makeTheRoom"+","+Et_SetTitle.getText().toString()+","+Et_MaxPeople.getText().toString(); // "방을 만들어라"라는 신호 / 방 제목 / 최대 인원.
						MainActivity.out.println(pac);
						MainActivity.out.flush();
						String strMsg = "Send Message : "+pac;
						//Log.d("DMSG", "Msg = " + pac);
						
						ChatRoom.tempTitle =  Et_SetTitle.getText().toString();
//						createChatRoom(Et_SetTitle.getText().toString());	
					}
				}
				catch(NumberFormatException NFE)
				{
					Toast.makeText(getApplicationContext(), "최대 인원을 너무 큰 수로 설정하였습니다.", Toast.LENGTH_LONG).show();
				}
				
				
				ad.dismiss();
			}
		});
		
		
		SetTitle.setTextSize(15);
		MaxPeople.setTextSize(15);
		

	}
	
	class MAdapter extends BaseAdapter
	{
		private LayoutInflater m_inflater = null;
		private ArrayList<MainView> mainview;
		
		public MAdapter(ArrayList<MainView> items)
		{
			mainview = items;
			m_inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
		}
		
		public void add(MainView M)
		{
			mainview.add(M);
			notifyDataSetChanged();
		}
		


		@Override
		public int getCount() 
		{
			return mainview.size();
		}


		@Override
		public Object getItem(int position) 
		{
			return mainview.get(position);
		}


		@Override
		public long getItemId(int position) 
		{
			return position;
		}
		


		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			View view=null;
			
			if(convertView == null)
			{
					view = m_inflater.inflate(R.layout.activity_main_chat, null);
			}
			else
			{
				view = convertView;
			}
			
			MainView line = mainview.get(position);
			
			
			if(line != null)
			{
					  TextView title = (TextView) view.findViewById(R.id.title);
                      TextView cur_people = (TextView) view.findViewById(R.id.cur_people);
                      TextView max_people = (TextView) view.findViewById(R.id.max_people);
                     
                      if(max_people != null)
                      {
                    	  max_people.setText("/ "+line.getMax_people()+"");
                      }
                      if (title != null)
                      {
                    	  title.setText(line.getTitle()+"");                            
                      }
                      if(cur_people != null)
                      {
                    	  cur_people.setText(line.getCur_people()+"");
                      }
			}
			
			return view;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat_room, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		 Handle action bar item clicks here. The action bar will
//		 automatically handle clicks on the Home/Up button, so long
//		 as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

class MainView
{
	
	String title;
	int cur_people;
	int max_people;
	
	
	public MainView(String _title, int _cur_people, int _max_people)
	{
		this.title = _title;
		this.cur_people = _cur_people;
		this.max_people = _max_people;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public int getCur_people() {
		return cur_people;
	}


	public void setCur_people(int cur_people) {
		this.cur_people = cur_people;
	}


	public int getMax_people() {
		return max_people;
	}


	public void setMax_people(int max_people) {
		this.max_people = max_people;
	}
	
}


