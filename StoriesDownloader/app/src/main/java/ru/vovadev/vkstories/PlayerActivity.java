package ru.vovadev.vkstories;
import android.app.*;
import android.os.*;
import android.content.*;
import android.media.*;
import android.net.*;
import java.io.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;

public class PlayerActivity extends Activity
{
	
	private MediaPlayer mp;
	private Boolean isPlaying;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.player);
		Intent intent = getIntent();
		String url = intent.getExtras().getString("url");
		mp = new MediaPlayer();
		try{
			mp.setDataSource(getApplicationContext(),Uri.parse(url));
		} catch(IOException e){
			e.printStackTrace();
		}
		try{
			mp.prepare();
		} catch(IOException e){
			e.printStackTrace();
		}
		isPlaying = false;
		playPause();
		ImageButton playBtn = findViewById(R.id.playBtn);
		playBtn.setImageDrawable(getDrawable(R.drawable.pause_icon));
		playBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				ImageButton b = (ImageButton) v;
				playPause();
				if(isPlaying){
					b.setImageDrawable(getDrawable(R.drawable.pause_icon));
				} else{
					b.setImageDrawable(getDrawable(R.drawable.play_icon));
				}
			}
		});
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
	}
	
	public void playPause(){
		if(isPlaying){
			mp.pause();
			isPlaying = false;
		} else{
			mp.start();
			isPlaying = true;
		}
	}

}
