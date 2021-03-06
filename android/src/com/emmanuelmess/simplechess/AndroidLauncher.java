package com.emmanuelmess.simplechess;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		config.numSamples = 2;
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(initializeForView(new SimpleChessGame(), config));
		setContentView(layout);
	}
}
