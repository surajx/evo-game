package com.pyrocrypt.astrosurfer.start;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.pyrocrypt.astrosurfer.R;

public class SplashScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash_screen);
		final ImageView myImageView = (ImageView) findViewById(R.id.SplashScreenImage);
		ViewTreeObserver vto = myImageView.getViewTreeObserver();      
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {          
		     @Override          
		     public void onGlobalLayout() {
		    	 new Wait4SplashAsyncTask().execute();
		        myImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this); //New method only supported from API16 - we are targeting ICS onwards. 
		     }      
		}); 		
	}

	class Wait4SplashAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			MediaPlayer mPlayer = MediaPlayer.create(SplashScreenActivity.this, R.raw.boom);
			mPlayer.start();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			startActivity(new Intent(SplashScreenActivity.this, AstroSurferStarter.class));
			finish();
		}
	}
}
