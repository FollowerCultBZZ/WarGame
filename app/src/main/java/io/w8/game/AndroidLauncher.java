package io.w8.game;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import io.w8.game.core.Core;

public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config =
                new AndroidApplicationConfiguration();

        config.useImmersiveMode = true;
        initialize(new Core(), config);
    }
}
