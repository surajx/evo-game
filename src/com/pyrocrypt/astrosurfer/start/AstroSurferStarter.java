package com.pyrocrypt.astrosurfer.start;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.pyrocrypt.astrosurfer.game.AstroSurferGame;

public class AstroSurferStarter extends AndroidApplication {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration gameConfig = new AndroidApplicationConfiguration();
		gameConfig.useGL20 = true;
		AstroSurferGame game = new AstroSurferGame();
		initialize(game, gameConfig);
	}

	@Override
	public void onBackPressed() {
		AstroSurferStarter.this.exit();
		super.onBackPressed();
	}

}
