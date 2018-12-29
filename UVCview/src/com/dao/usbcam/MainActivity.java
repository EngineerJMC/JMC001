package com.dao.usbcam;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {
	private ImageView im_back;
	private int width = 640;
	private int height = 480;
	private String devname = "/dev/video1";
	private byte[] mdata;
	private Handler mHandler;
	private int numbuf = 0;
	private int index = 0;
	private int ret = 0;
	public Button mcap;
	private Bitmap bitmap;
	private boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUVC();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	releaseUVC();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }
    
	private void initUVC() {
		im_back = (ImageView) findViewById(R.id.img);
		numbuf = 4;
		mdata = new byte[width * height * numbuf];
		ret = Fimcgzsd.open(devname.getBytes());
		if (ret < 0)
			return;
		ret = Fimcgzsd.init(width, height, numbuf);
		if (ret < 0)
			return;
		ret = Fimcgzsd.streamon();
		if (ret < 0)
			return;
		mHandler = new Handler();
		flag = true;
		new StartThread().start();
	}
	
	private void releaseUVC(){
		flag = false;
		Fimcgzsd.streamoff();
		Fimcgzsd.release();
	}
	
	final Runnable mUpdateUI = new Runnable() {

		@Override
		public void run() {
			im_back.setImageBitmap(bitmap);
		}
	};

	class StartThread extends Thread {

		@Override
		public void run() {
			while (flag) {
				index = Fimcgzsd.dqbuf(mdata);
				if (index < 0) {
					onDestroy();
					break;
				}
				mHandler.post(mUpdateUI);
				bitmap = byteToBitmap(mdata);
				Fimcgzsd.qbuf(index);
			}
		}

	}

	BitmapFactory.Options options = new BitmapFactory.Options();

	public Bitmap byteToBitmap(byte[] imgByte) {
		InputStream input = null;
		Bitmap bitmap = null;
		options.inSampleSize = 8;
		input = new ByteArrayInputStream(imgByte);
		bitmap = BitmapFactory.decodeStream(input, null, options);
		if (imgByte != null) {
			imgByte = null;
		}
		try {
			if (input != null) {
				input.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
