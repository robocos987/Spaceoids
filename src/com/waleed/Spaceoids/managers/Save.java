package com.waleed.Spaceoids.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.badlogic.gdx.Gdx;

public class Save {
	
	public static GameData gd;
	
	public static void save() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream("highscores.sav")
			);
			out.writeObject(gd);
			out.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			Gdx.app.exit();
		}
	}
	
	public static void load() {
		try {
			if(!saveFileExists()) {
				File file = new File("highscores.sav");
				file.createNewFile();
				init();
				return;
			}
			ObjectInputStream in = new ObjectInputStream(
				new FileInputStream("highscores.sav")
			);
			gd = (GameData) in.readObject();
			in.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			Gdx.app.exit();
		}
	}
	
	public static boolean saveFileExists() {
		File f = new File("highscores.sav");
		return f.exists();
	}
	
	public static void init() {
		gd = new GameData();
		gd.init();
		save();
	}
	
}
