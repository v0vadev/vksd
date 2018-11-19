package ru.vovadev.vkstories;
import android.app.*;
import com.vk.sdk.*;
import android.widget.*;
import android.content.*;
import android.content.pm.*;

public class Application extends android.app.Application
{
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		VKSdk.initialize(this);
	}
	
	
}
