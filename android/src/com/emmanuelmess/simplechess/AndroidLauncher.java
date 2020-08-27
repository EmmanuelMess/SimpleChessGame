package com.emmanuelmess.simplechess;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.emmanuelmess.simplechess.SimpleChessGame;
import com.emmanuelmess.simplechess.net.Connection;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		Connection.INSTANCE.init(this);
		initialize(new SimpleChessGame(Connection.INSTANCE), config);
	}
}
