package com.example.chatchat;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Membership extends Activity {

	Button finish_btn;
	String[] spin;
	CheckBox male;
	CheckBox female;
	String click_item;
	String gender;

	EditText et_id;
	EditText et_pw;
	static EditText et_nickname;
	Activity mem;
	Spinner spiner;
	
	boolean chk_male = false;
	boolean chk_female = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_membership);

		male = (CheckBox) findViewById(R.id.checkBox1);
		female = (CheckBox) findViewById(R.id.checkBox2);

		finish_btn = (Button) findViewById(R.id.finish);
		spiner = (Spinner) findViewById(R.id.spinner1);

		et_id = (EditText) findViewById(R.id.et_id);
		et_pw = (EditText) findViewById(R.id.et_pw);
		et_nickname = (EditText) findViewById(R.id.et_nickname);

		
		ActionBar abar = getActionBar();
		abar.hide();
		
		
		mem = Membership.this;
		
		//Log.d("mem",mem+ "");

		spin = getResources().getStringArray(R.array.Age);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, spin);
		spiner.setAdapter(adapter);

		male.setOnCheckedChangeListener(new OnCheckedChangeListener() {  // 체크 박스에 남자가 체크됬는지 확인.
																										// 만약 여성과 같이 체크가 됬다면 남자 체크박스를 해제 시킴.

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {

					chk_male = true;
					gender = "male";

					if (chk_female == true) {
						female.setChecked(false);
					}
				}
			}
		});

		female.setOnCheckedChangeListener(new OnCheckedChangeListener() {// 체크 박스에 여성이 체크됬는지 확인.
																										// 만약 남자와 같이 체크가 됬다면 남자 체크박스를 해제 시킴.

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {

					chk_female = true;
					gender = "female";
					if (chk_male == true) {
						male.setChecked(false);
					}
				}

			}
		});

		spiner.setOnItemSelectedListener(new OnItemSelectedListener() { // 나이를 선택했을때.! 

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				click_item = spin[position];
//				//Log.d("click_item", click_item + "");
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		finish_btn.setOnClickListener(new OnClickListener() { // AsyncTask를 통해서 php로 값을 넘김.

			@Override
			public void onClick(View v) {

				if(et_id.getText().length() > 20)
				{
					Toast.makeText(getApplicationContext(), "id를 20자 이내로 입력해주세요.", Toast.LENGTH_LONG).show();
				}
				else if(et_pw.getText().length() > 20)
				{
					Toast.makeText(getApplicationContext(), "패스워드를 20자 이내로 입력해주세요.", Toast.LENGTH_LONG).show();
				}
				else if(et_nickname.getText().length() > 30)
				{
					Toast.makeText(getApplicationContext(), "닉네임을 30자 이내로 입력해주세요.", Toast.LENGTH_LONG).show();
				}
				else
				{
					String result = et_id.getText().toString() + ","
							+ et_pw.getText().toString() + ","
							+ et_nickname.getText().toString() + ","
							+ gender + "," 
							+ click_item;

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						new MembershipTophp("http://115.71.237.99/chatchat/membership.php", result,  Membership.this, mem).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
			        else
			        	new MembershipTophp("http://115.71.237.99/chatchat/membership.php", result,Membership.this, mem).execute((Void[])null);
					
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.membership, menu);
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
