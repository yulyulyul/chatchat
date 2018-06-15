package com.example.chatchat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class Over extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_over);
		
		ActionBar abar = getActionBar();
	      abar.hide();
	      
	      ImageView img = (ImageView)findViewById(R.id.img);
	      
	      Intent intent = getIntent();
	       int position = intent.getIntExtra("position", 0);
	       int standard = intent.getIntExtra("type", 2);
	       
//	       Log.d("In Over, intent, position", position+"");
	       
	       Message msg = null;
	       
	       switch (standard) {
		case 0:
			  msg = MainActivity.adapter.message.get(position);
		      
			break;
		case 1:
			  msg = MainActivity.adapter2.message.get(position);
		      			
			break;
		case 2:
			
			Toast.makeText(Over.this, "사진을 로드하는데 실패하였습니다.", Toast.LENGTH_LONG).show();
			break;
		}
	      Bitmap bitmap =BitmapFactory.decodeByteArray(msg.getBy() , 0, msg.getBy().length);
	      img.setImageBitmap(bitmap);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.over, menu);
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
