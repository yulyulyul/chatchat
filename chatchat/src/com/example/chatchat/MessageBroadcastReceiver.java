package com.example.chatchat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

public class MessageBroadcastReceiver extends BroadcastReceiver {

	Context mContext;
	SharedPreferences chk_login;
	String friend_list;
	String myNick;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.mContext = context;
		String getIntent = intent.getAction();
		// String fromGcmIntent = intent.getExtras().getString("msg");
		// Toast.makeText(context, fromGcmIntent, Toast.LENGTH_LONG).show();

		Message mess;
		Boolean pass, pleaseWait;

		chk_login = mContext.getSharedPreferences("LoginCheck",mContext.MODE_PRIVATE);
		friend_list = chk_login.getString("friends", "[]");
		myNick = chk_login.getString("nickname","fail");

		/*
		 * 앱이 비활성화 되면 메시지를 받아도 main ui 들이 활성화가 되지 않았으므로 내부 저장장치에 저장해둬야함.
		 */

		DBManager sqlitedb = new DBManager(mContext, "chat.db", null, 1);

		String str_type = intent.getExtras().getString("type");
		int seperating = Integer.parseInt(str_type);

		switch (seperating) {

		case 2: // 1:1 대화에서 일반 메시지 전송.

			String user = "";
			String msg = "";
			String member = "";
			String created_time = "";
			String time = "";
			String key = "";
			Integer type;
			try {

				// 앱의 현재 상태를 비교해서 현재 내가 같은 방이면 저장 안하고 바로 표출
				// 반대로 내가 다른 상태이면 메시지 자체를 저장.
				//System.out.println("In user2");

				user = intent.getExtras().getString("user");
				msg = intent.getExtras().getString("MSG");
				member = intent.getExtras().getString("member");
				created_time = intent.getExtras().getString("created_time");
//				time = intent.getExtras().getString("time");
				time = currentTime();
				key = intent.getExtras().getString("key");

				//Log.d("MessageBroadcastReceiver", "전송 받음.");

				Log.d("MessageBroadcastReceiver", created_time+"");
				Log.d("MessageBroadcastReceiver", time+"");
				
				
				String tmp[] = member.split(",");

				String f_nick;

				if (tmp[0].equals(MainActivity.MyNickname)) {
					f_nick = tmp[1];
				} else {
					f_nick = tmp[0];
				}

				personalChat.With_friend = f_nick;

				/*
				 * 메시지를 받을때 마다 db에 저장을 함.// 또한 3일 이상이인 메시지는 지우도록 ! (쿼리를 만들어서
				 * 날림.)
				 * 
				 * 대화방을 터치하면 채팅방의 시퀀스 번호에 해당하는 모든 대화를 가져오는 쿼리를 날린다. 가져온 데이터들을
				 * 정리해서 adapter에 순차적으로 넣어준다.
				 * 
				 * 또한 메시지를 받을 때마다 친구의 사진 정보, 마지막 대화, 채팅방의 시퀀스 번호, 시간 정보를 로컬에 동기화
				 * 시켜야한다.(Destroy)
				 */

				if (MainActivity.state.equals(key)) {
					////System.out.println("state와 key가 같다.");

					if (user.equals(MainActivity.MyNickname)) {
						mess = new Message(0, user, msg, time);
						type = 0;
					} else {
						mess = new Message(2, user, msg, time);
						type = 2;
					}

					MainActivity.dbManager.sql_query("insert into chat_log(chat_sequence, state, nick, msg, created_date, created_time, type) values('"
									+ key
									+ "',"
									+ 1
									+ ", '"
									+ user
									+ "' , '"
									+ msg
									+ "' , datetime('now','localtime'),'"
									+ time + "'," + type + " );");

					pass = false;

					MainActivity.adapter2.add(mess);
					personalChat.lv.smoothScrollToPosition(MainActivity.adapter2.getCount());
					MainActivity.adapter2.notifyDataSetChanged();

					pass = true;
					pleaseWait = true;

					while (true) {
						if (pass = true && pleaseWait == true)
							break;
					}

				} else {
					//System.out.println("state와 key가 다르다. ");
					//Log.d("state", MainActivity.state + "");
					//Log.d("key", key + "");
					
//					Log.d("MessageBroadcastReceiver", "여기까지 올까?");

					if (user.equals(myNick)) {
						type = 0;
					} else {
						type = 2;
					}

					MainActivity.dbManager
							.sql_query("insert into chat_log(chat_sequence, state, nick, msg, created_date, created_time, type) values('"
									+ key
									+ "',"
									+ 0
									+ ", '"
									+ user
									+ "' , '"
									+ msg
									+ "' , datetime('now','localtime'),'"
									+ time + "',"
									+ type + " );");

					pass = true;
					pleaseWait = true;

					// ///////////////////////////////////////////////////////////////////////////////////////////////////

					String chat_list = MainActivity.chk_login.getString("chat_list", "[]"); // jsonString 형태이겠쥐.
					//Log.d("personalChat", "chat_list : " + chat_list);

					JSONArray jarr = new JSONArray();
					JSONObject job = new JSONObject();
					JSONParser jParser = new JSONParser();

					//System.out.println("어뎁터 크기 : "+ MainActivity.adapter2.message.size());

					if (chat_list.equals("[]"))// 아직 채팅이 존재하지 않았던 경우.
					{
						job.put("seq", key);
						job.put("f_nick", f_nick);
						job.put("last", msg);
						job.put("time", time);
						jarr.add(job);

						String list = jarr.toJSONString();
						//Log.d("In personalChat", "로컬에 채팅 리스트 저장 확인 : " + list);
						MainActivity.editor.putString("chat_list", list);
						MainActivity.editor.commit();
					}
					/*
					 * 추가하려는 채팅 리스트에 이미 기존에 있는 리스트와 겹치는지 비교해야함.
					 */

					else // 채팅 리스트가 존재한 경우.
					{
						try {

							boolean pass2 = true;

							jarr = (JSONArray) jParser.parse(chat_list);

							//System.out.println("jarr 크기 :" + jarr.size());
							for (int i = 0; i < jarr.size(); i++) {
								JSONObject j = (JSONObject) jarr.get(i);

								String title = (String) j.get("f_nick");

								if (title.equals(f_nick)) {
									jarr.remove(i);
									job.put("seq", key);
									job.put("f_nick", f_nick);
									job.put("last", msg);
									job.put("time", time);
									jarr.add(job);

									String list = jarr.toJSONString();
									//Log.d("In personalChat", "로컬에 채팅 리스트 저장 확인 : "+ list);
									MainActivity.editor.putString("chat_list",
											list);
									MainActivity.editor.commit();

									pass2 = false;
									break;
								}
							}

							if (pass2 == true) // 겹치지 않은 경우
							{
								jarr = (JSONArray) jParser.parse(chat_list);
								job.put("seq", key);
								job.put("f_nick", f_nick);
								job.put("last", msg);
								job.put("time", time);
								jarr.add(job);

								String list = jarr.toJSONString();
								//Log.d("In personalChat", "로컬에 채팅 리스트 저장 확인 : "+ list);
								MainActivity.editor
										.putString("chat_list", list);
								MainActivity.editor.commit();
							}

						} catch (ParseException e) {
							e.printStackTrace();
						}

					}

					customTostMessage(msg, f_nick);
				}

				// 방을 만들엇!

				if ((MainActivity.NICK_and_BITMAP.get(f_nick) != null)) // 사용자가
																		// 프로필을
																		// 설정을
																		// 했을때.
				{
					boolean pass2 = true;

					for (int i = 0; i < MainActivity.f_chat_adapter.mainview.size(); i++) 
					{
						if (MainActivity.f_chat_adapter.mainview.get(i).getName().equals(f_nick)) 
						{
							pass2 = false;
							MainActivity.f_chat_adapter.mainview.get(i).setLast(msg);
							MainActivity.f_chat_adapter.mainview.get(i).setTime(time);
							MainActivity.f_chat_adapter.notifyDataSetChanged();
						}
					}

					if (pass2 == true) {
						Bitmap sizingBmp2 = Bitmap.createScaledBitmap(MainActivity.NICK_and_BITMAP.get(f_nick), 100, 100, true);
						RoundedAvatarDrawable tmpRoundedAvatarDrawable2 = new RoundedAvatarDrawable(sizingBmp2);

						FriendView3 fv3 = new FriendView3(tmpRoundedAvatarDrawable2, f_nick, msg, time,key);
						MainActivity.f_chat_adapter.add(fv3);
					}

				} else {
					boolean pass2 = true;

					for (int i = 0; i < MainActivity.f_chat_adapter.mainview.size(); i++) {
						if (MainActivity.f_chat_adapter.mainview.get(i).getName().equals(f_nick)) 
						{
							pass2 = false;
							MainActivity.f_chat_adapter.mainview.get(i).setLast(msg);
							MainActivity.f_chat_adapter.mainview.get(i).setTime(time);
							MainActivity.f_chat_adapter.notifyDataSetChanged();
						}
					}

					if (pass2 == true) {
						Bitmap default_img = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.person);
						Bitmap sizingBmp2 = Bitmap.createScaledBitmap(default_img, 100, 100, true);
						RoundedAvatarDrawable tmpRoundedAvatarDrawable2 = new RoundedAvatarDrawable(sizingBmp2);

						FriendView3 fv3 = new FriendView3(tmpRoundedAvatarDrawable2, f_nick, msg, time,key);
						MainActivity.f_chat_adapter.add(fv3);
					}

				}
			} catch (NullPointerException ne) {
				System.out.println("NullPointException IN MessageBr~~");
				ne.printStackTrace();
				customTostMessage(msg, user);
				
				if (user.equals(myNick)) {
					type = 0;
				} else {
					type = 2;
				}

				DBManager dbManager = new DBManager(mContext, "chat.db", null, 1);
				
				dbManager
						.sql_query("insert into chat_log(chat_sequence, state, nick, msg, created_date, created_time, type) values('"
								+ key
								+ "',"
								+ 0
								+ ", '"
								+ user
								+ "' , '"
								+ msg
								+ "' ,datetime('now','localtime'),'"
								+ time + "',"
								+ type + " );");
				
				SharedPreferences go_chatroom = mContext.getSharedPreferences("go_chatroom", mContext.MODE_PRIVATE);
				SharedPreferences.Editor editor = go_chatroom.edit();
				editor.putString("name", user);
				editor.commit();
				

			} catch (Exception e) {
				e.printStackTrace();
			}

			break;

		case 3:

			String fileURI = intent.getExtras().getString("URI");
			String userID = intent.getExtras().getString("user");
			String pic_member = intent.getExtras().getString("member");
			String pic_key = intent.getExtras().getString("key");
			String pic_time = currentTime();
			Integer pic_type;

			String tmp[] = pic_member.split(",");

			String f_nick = "";

			try {
				if (tmp[0].equals(MainActivity.MyNickname)) {
					f_nick = tmp[1];
				} else {
					f_nick = tmp[0];
				}

				personalChat.With_friend = f_nick;

				if (MainActivity.state.equals(pic_key)) {
					//System.out.println("state와 pic_key가 같다.");

					if (userID.equals(MainActivity.MyNickname)) {

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
							new downloadImage(fileURI, 3, userID, pic_time, 1,
									mContext, pic_key).executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR,
									(Void[]) null);
						else
							new downloadImage(fileURI, 3, userID, pic_time, 1,
									mContext, pic_key).execute((Void[]) null);

					}

					else {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
							new downloadImage(fileURI, 4, userID, pic_time, 1,
									mContext, pic_key).executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR,
									(Void[]) null);
						else
							new downloadImage(fileURI, 4, userID, pic_time, 1,
									mContext, pic_key).execute((Void[]) null);
					}
				}

				else {
					//System.out.println("state와 pic_key가 다르다. ");
					//Log.d("state", MainActivity.state + "");
					//Log.d("pic_key", pic_key + "");

					GregorianCalendar gc3 = new GregorianCalendar();

					SimpleDateFormat sf3 = new SimpleDateFormat(
							"yyyyMMddHHmmss"); // 기본 데이타베이스 저장 타입
					Date d3 = gc3.getTime(); // Date -> util 패키지
					String str3 = sf3.format(d3);
					//System.out.println(str3);

					if (userID.equals(MainActivity.MyNickname)) {
						// MainActivity.dbManager.sql_query("insert into chat_log(chat_sequence, state, nick, msg, created_date, created_time, pic_type) values('"+pic_key+"',"+0+", '"
						// + userID + "' , '"+"사진" + "' , '"+str3+"','"+
						// pic_time+"',"+3+" );");

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
							new downloadImage(fileURI, 3, userID, pic_time, 3,
									mContext, pic_key).executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR,
									(Void[]) null);
						else
							new downloadImage(fileURI, 3, userID, pic_time, 3,
									mContext, pic_key).execute((Void[]) null);
					} else {
						// MainActivity.dbManager.sql_query("insert into chat_log(chat_sequence, state, nick, msg, created_date, created_time, pic_type) values('"+pic_key+"',"+0+", '"
						// + userID + "' , '"+"사진" + "' , '"+str3+"','"+
						// pic_time+"',"+4+" );");

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
							new downloadImage(fileURI, 4, userID, pic_time, 3,
									mContext, pic_key).executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR,
									(Void[]) null);
						else
							new downloadImage(fileURI, 4, userID, pic_time, 3,
									mContext, pic_key).execute((Void[]) null);
					}

					// ///////////////////////////////////////////////////////////////////////////////////////////////////

					String chat_list = MainActivity.chk_login.getString(
							"chat_list", "[]"); // jsonString 형태이겠쥐.
					//Log.d("personalChat", "chat_list : " + chat_list);

					JSONArray jarr = new JSONArray();
					JSONObject job = new JSONObject();
					JSONParser jParser = new JSONParser();

					//System.out.println("어뎁터 크기 : "+ MainActivity.adapter2.message.size());

					if (chat_list.equals("[]"))// 아직 채팅이 존재하지 않았던 경우.
					{

						job.put("seq", pic_key);
						job.put("f_nick", f_nick);
						job.put("last", "사진");
						job.put("time", pic_time);
						jarr.add(job);

						String list = jarr.toJSONString();
						//Log.d("In personalChat", "로컬에 채팅 리스트 저장 확인 : " + list);
						MainActivity.editor.putString("chat_list", list);
						MainActivity.editor.commit();

					}
					/*
					 * 추가하려는 채팅 리스트에 이미 기존에 있는 리스트와 겹치는지 비교해야함.
					 */

					else // 채팅 리스트가 존재한 경우.
					{
						try {

							boolean pass3 = true;

							jarr = (JSONArray) jParser.parse(chat_list);

							//System.out.println("jarr 크기 :" + jarr.size());
							for (int i = 0; i < jarr.size(); i++) {
								JSONObject j = (JSONObject) jarr.get(i);

								String title = (String) j.get("f_nick");

								if (title.equals(f_nick)) {
									jarr.remove(i);
									job.put("seq", pic_key);
									job.put("f_nick", f_nick);
									job.put("last", "사진");
									job.put("time", pic_time);
									jarr.add(job);

									String list = jarr.toJSONString();
									//Log.d("In personalChat", "로컬에 채팅 리스트 저장 확인 : "+ list);
									MainActivity.editor.putString("chat_list",
											list);
									MainActivity.editor.commit();

									pass3 = false;
									break;
								}
							}

							if (pass3 == true) // 겹치지 않은 경우
							{
								jarr = (JSONArray) jParser.parse(chat_list);
								job.put("seq", pic_key);
								job.put("f_nick", f_nick);
								job.put("last", "사진");
								job.put("time", pic_time);
								jarr.add(job);

								String list = jarr.toJSONString();
								//Log.d("In personalChat", "로컬에 채팅 리스트 저장 확인 : "+ list);
								MainActivity.editor
										.putString("chat_list", list);
								MainActivity.editor.commit();
							}

						} catch (ParseException e) {
							e.printStackTrace();
						}

					}

//					LayoutInflater inflater = (LayoutInflater) context
//							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//					View layout = inflater.inflate(
//							R.layout.activity_chatting_toast, null);
//					ImageView image = (ImageView) layout
//							.findViewById(R.id.pro_img);
//					TextView name = (TextView) layout.findViewById(R.id.name);
//					TextView message = (TextView) layout.findViewById(R.id.msg);
//					Toast mToast = new Toast(context.getApplicationContext());
//
//					RoundedAvatarDrawable tmpRoundedAvatarDrawable;
//
//					try {
//						Bitmap sizingBmp = Bitmap.createScaledBitmap(
//								MainActivity.NICK_and_BITMAP.get(f_nick), 150,
//								150, true);
//						tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(
//								sizingBmp);
//					} catch (NullPointerException ne) {
//						Bitmap bm = BitmapFactory.decodeResource(
//								mContext.getResources(), R.drawable.person);
//						Bitmap sizingBmp = Bitmap.createScaledBitmap(bm, 150,
//								150, true);
//						tmpRoundedAvatarDrawable = new RoundedAvatarDrawable(
//								sizingBmp);
//					}
//
//					layout.setBackground(mContext.getResources().getDrawable(
//							R.drawable.custom_toast));
//					image.setImageDrawable(tmpRoundedAvatarDrawable);
//					message.setText("사진");
//					message.setTextColor(Color.WHITE);
//					name.setText(userID);
//					name.setTextColor(Color.WHITE);
//					mToast.setGravity(Gravity.TOP, 0, 250);
//					mToast.setDuration(Toast.LENGTH_SHORT);
//					mToast.setView(layout);
//					mToast.show();

					customTostMessage("사진",f_nick);
					
				}

				if ((MainActivity.NICK_and_BITMAP.get(f_nick) != null)) // 사용자가
																		// 프로필을
																		// 설정을
																		// 했을때.
				{
					//System.out.println("MainActivity.NICK_and_BITMAP : "+ MainActivity.NICK_and_BITMAP.get(f_nick));
					//System.out.println("In messagebroadcastReceiver`s picture Part 사용자가 프로필 설정을 했을 경우임.");
					boolean pass4 = true;

					for (int i = 0; i < MainActivity.f_chat_adapter.mainview.size(); i++) {
						if (MainActivity.f_chat_adapter.mainview.get(i).getName().equals(f_nick)) {
							//System.out.println("찾음! ");
							pass4 = false;
							MainActivity.f_chat_adapter.mainview.get(i).setLast("사진");
							MainActivity.f_chat_adapter.mainview.get(i).setTime(pic_time);
							MainActivity.f_chat_adapter.notifyDataSetChanged();
						}
					}

					if (pass4 == true) {
						Bitmap sizingBmp2 = Bitmap.createScaledBitmap(MainActivity.NICK_and_BITMAP.get(f_nick), 100,100, true);
						RoundedAvatarDrawable tmpRoundedAvatarDrawable2 = new RoundedAvatarDrawable(sizingBmp2);
						FriendView3 fv3 = new FriendView3(tmpRoundedAvatarDrawable2, f_nick, "사진",pic_time, pic_key);
						MainActivity.f_chat_adapter.add(fv3);
					}
				} else {
					System.out.println("In messagebroadcastReceiver`s picture Part 사용자가 프로필 설정을 하지 않았을 경우임.");

					boolean pass5 = true;

					for (int i = 0; i < MainActivity.f_chat_adapter.mainview.size(); i++) {
						if (MainActivity.f_chat_adapter.mainview.get(i).getName().equals(f_nick)) {
							pass5 = false;
							MainActivity.f_chat_adapter.mainview.get(i).setLast("사진");
							MainActivity.f_chat_adapter.mainview.get(i).setTime(pic_time);
							MainActivity.f_chat_adapter.notifyDataSetChanged();
						}
					}

					if (pass5 == true) {
						Bitmap default_img = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.person);
						Bitmap sizingBmp2 = Bitmap.createScaledBitmap(default_img, 100, 100, true);
						RoundedAvatarDrawable tmpRoundedAvatarDrawable2 = new RoundedAvatarDrawable(sizingBmp2);

						FriendView3 fv3 = new FriendView3(tmpRoundedAvatarDrawable2, f_nick, "사진",pic_time, pic_key);
						MainActivity.f_chat_adapter.add(fv3);
					}

				}
			} 
			catch (NullPointerException ne) 
			{
				System.out.println("NullPointException! In case3!, MessageBroadcastR~");
				ne.printStackTrace();
				
				if (userID.equals(myNick)) {
					// MainActivity.dbManager.sql_query("insert into chat_log(chat_sequence, state, nick, msg, created_date, created_time, pic_type) values('"+pic_key+"',"+0+", '"
					// + userID + "' , '"+"사진" + "' , '"+str3+"','"+
					// pic_time+"',"+3+" );");

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						new downloadImage(fileURI, 3, userID, pic_time, 3,
								mContext, pic_key).executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR,
								(Void[]) null);
					else
						new downloadImage(fileURI, 3, userID, pic_time, 3,
								mContext, pic_key).execute((Void[]) null);
					
					SharedPreferences go_chatroom = mContext.getSharedPreferences("go_chatroom", mContext.MODE_PRIVATE);
					SharedPreferences.Editor editor = go_chatroom.edit();
					editor.putString("name", userID);
					editor.commit();
					
				} else {
					// MainActivity.dbManager.sql_query("insert into chat_log(chat_sequence, state, nick, msg, created_date, created_time, pic_type) values('"+pic_key+"',"+0+", '"
					// + userID + "' , '"+"사진" + "' , '"+str3+"','"+
					// pic_time+"',"+4+" );");

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						new downloadImage(fileURI, 4, userID, pic_time, 3,
								mContext, pic_key).executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR,
								(Void[]) null);
					else
						new downloadImage(fileURI, 4, userID, pic_time, 3,
								mContext, pic_key).execute((Void[]) null);
					
					SharedPreferences go_chatroom = mContext.getSharedPreferences("go_chatroom", mContext.MODE_PRIVATE);
					SharedPreferences.Editor editor = go_chatroom.edit();
					editor.putString("name", userID);
					editor.commit();
					
				}
				customTostMessage("사진",f_nick);

//			    Log.d("In NullPointerException, Pic", fileURI + "");
//				DBManager dbManager = new DBManager(mContext, "chat.db", null, 1);
//				
//				dbManager.sql_query("insert into chat_log(chat_sequence, state, nick, msg, created_date, created_time, type) values('"+key+"',"+0+", '" + userID + "' , '"
//				+"data/data/com.example.chatchat/files/"+ file3 +"' , '"+str3+"','"+ time+"',"+type+" );");
				
			}

			break;

		default:
			break;
		}
	}

	public String get_friend_path(String f_name) {
		String path = "";
		if (friend_list.equals("[]") == false) // 친구가 존재한다면!
		{
			JSONParser jsonParser2 = new JSONParser();

			try {

				JSONArray jarr2 = (JSONArray) jsonParser2.parse(friend_list);

				for (int i = 0; i < jarr2.size(); i++) {
					JSONObject j = (JSONObject) jarr2.get(i);
//					System.out.println("nick[" + i + "] : "
//							+ (String) j.get("nick"));
//					System.out.println("path[" + i + "] : "
//							+ (String) j.get("path"));
//					System.out.println("state_msg[" + i + "] : "
//							+ (String) j.get("state_msg"));
//					System.out.println("값[" + i + "] : " + jarr2.get(i));

					final String f_nick = (String) j.get("nick");
					final String f_path = (String) j.get("path");

					if (f_nick.equals(f_name))
						return f_path;

				}

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return path;
	}

	public void customTostMessage(final String msg, final String user) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.activity_chatting_toast, null);
		ImageView image = (ImageView) layout.findViewById(R.id.pro_img);
		TextView name = (TextView) layout.findViewById(R.id.name);
		TextView message = (TextView) layout.findViewById(R.id.msg);
		Toast mToast = new Toast(mContext.getApplicationContext());

		final String path = get_friend_path(user);
		Uri uri = Uri.parse(path);

		image.setImageURI(uri);

		Glide.with(mContext).load(path).override(50, 50)
				.error(R.drawable.person)
				.transform(new CircleTransform(mContext)).into(image);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Bitmap bmp = Glide.with(mContext).load(path).asBitmap()
							.error(R.drawable.person)
							.transform(new CircleTransform(mContext))
							.into(100, 100).get();

					Intent intent;
					
					if(MainActivity.state != null)
					{
						intent = new Intent(mContext, Header.class);
						intent.putExtra("f_name", user);
					}
					else
					{
						intent = new Intent(mContext, MainActivity.class);
						intent.putExtra("f_name", user);
					}

					NotificationManager mNotificationManager = (NotificationManager) mContext
							.getSystemService(Context.NOTIFICATION_SERVICE);
					PendingIntent contentIntent;
					NotificationCompat.Builder mBuilder;
					contentIntent = PendingIntent.getActivity(mContext, 0,
							intent, PendingIntent.FLAG_CANCEL_CURRENT);
					mBuilder = new NotificationCompat.Builder(mContext)
							.setSmallIcon(R.drawable.icon1)
							.setLargeIcon(bmp)
							.setContentTitle(user)
							.setStyle(
									new NotificationCompat.BigTextStyle()
											.bigText(msg)).setContentText(msg)
							.setAutoCancel(true).setTicker(msg)
							.setDefaults(Notification.DEFAULT_VIBRATE)
							.setWhen(System.currentTimeMillis());
					mBuilder.setContentIntent(contentIntent);
					mNotificationManager.notify(1, mBuilder.build());

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}).start();
		;

		layout.setBackground(mContext.getResources().getDrawable(
				R.drawable.custom_toast));
		message.setText(msg);
		message.setTextColor(Color.WHITE);
		name.setText(user);
		name.setTextColor(Color.WHITE);
		mToast.setGravity(Gravity.TOP, 0, 250);
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.setView(layout);
		mToast.show();

		PushWakeLock.acquireCpuWakeLock(mContext);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(5000);
					PushWakeLock.releaseCpuLock();
					KeyguardManager km = (KeyguardManager) mContext
							.getSystemService(Context.KEYGUARD_SERVICE);
					KeyguardLock keyLock = km
							.newKeyguardLock(Context.KEYGUARD_SERVICE);
					keyLock.reenableKeyguard();

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}).start();

	}

	public String currentTime() {
		StringBuffer sb = new StringBuffer(new java.text.SimpleDateFormat(
				"HHmm").format(new java.util.Date()));
		sb.insert(2, ":");

		if (Integer.parseInt(sb.substring(0, 2)) >= 12) {
			sb.append("pm");
		} else {
			sb.append("am");
		}
		return sb.toString();
	}

	public static class CircleTransform extends BitmapTransformation {
		public CircleTransform(Context context) {
			super(context);
		}

		@Override
		protected Bitmap transform(BitmapPool pool, Bitmap toTransform,
				int outWidth, int outHeight) {
			return circleCrop(pool, toTransform);
		}

		private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
			if (source == null)
				return null;

			int size = Math.min(source.getWidth(), source.getHeight());
			int x = (source.getWidth() - size) / 2;
			int y = (source.getHeight() - size) / 2;

			// TODO this could be acquired from the pool too
			Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

			Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
			if (result == null) {
				result = Bitmap.createBitmap(size, size,
						Bitmap.Config.ARGB_8888);
			}

			Canvas canvas = new Canvas(result);
			Paint paint = new Paint();
			paint.setShader(new BitmapShader(squared,
					BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
			paint.setAntiAlias(true);
			float r = size / 2f;
			canvas.drawCircle(r, r, r, paint);
			return result;
		}

		@Override
		public String getId() {
			return getClass().getName();
		}
	}

}
