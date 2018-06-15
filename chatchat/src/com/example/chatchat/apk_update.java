package com.example.chatchat;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

class apk_update extends AsyncTask<Void, Void , Void>{


    HttpURLConnection conn;
    InputStream is;
    Bitmap mybitmap;
    ImageView image;
    String fileUri;
    String path;
    Message message;
    byte[] byteURI;
    Context mCon;
    String FilePath;


    public apk_update(String uri, Context context)
    {
        this.fileUri = uri;
        this.mCon = context;
        System.out.println("apk_update excute");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        URL url;

        try {
            url = new URL(fileUri);


            try {
                conn = (HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);
                conn.setDoInput(true);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            is = conn.getInputStream();

            byte[] bytes = IOUtils.toByteArray(is);

            FilePath = Environment.getExternalStorageDirectory()+"/chatchat.apk";
            Log.d("FIlePath", FilePath );

            writeFile(bytes, FilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
    public void writeFile(byte[] data, String fileName) throws IOException{
        FileOutputStream out = new FileOutputStream(fileName);
        out.write(data);
        out.close();
    }

    @Override
    protected void onPostExecute(Void downloaded_Image) {
        super.onPostExecute(downloaded_Image);

        installApp(FilePath);

    }
    
	void installApp(String fileName) {
        Intent intent = new Intent(Intent.ACTION_VIEW)
            .setDataAndType(Uri.parse("file://" + fileName), "application/vnd.android.package-archive");
        mCon.startActivity(intent);
    }
	
}
