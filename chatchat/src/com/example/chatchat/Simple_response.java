package com.example.chatchat;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.WindowManager;

public class Simple_response extends Activity{

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().addFlags( WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.activity_simple_response);
        
        ActionBar abar = getActionBar();
        
        abar.hide();
        
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Message")
//               .setTitle("Title")
//               .setCancelable(false)
//               .setPositiveButton("OK", new DialogInterface.OnClickListener()
//               {
//                   public void onClick(DialogInterface dialog, int id) 
//                   {
//                       Simple_response.this.finish();
//                   }
//               });
//   
//   
//        AlertDialog alert = builder.create();
//        alert.show();
    }
	
	public void displayAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?").setCancelable(
            false).setPositiveButton("Yes",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            }).setNegativeButton("No",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        AlertDialog alert = builder.create();
        alert.show();
    }
	
}
