package com.waleed.Spaceoids.main;

import java.awt.Toolkit;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.waleed.Spaceoids.main.Spaceoids;

public class SpaceoidsMain {
	
	public static void main(String[] args) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		double width = toolkit.getScreenSize().getWidth();
		double height = toolkit.getScreenSize().getHeight();
		LwjglApplicationConfiguration cfg =
			new LwjglApplicationConfiguration();
		cfg.title = "Spaceoids";
		cfg.width = (int)width;
		cfg.height = (int)height;
		cfg.useGL20 = true;
		cfg.resizable = true;
		cfg.fullscreen = false;
		new LwjglApplication(new Spaceoids(), cfg);
	}
	
}
