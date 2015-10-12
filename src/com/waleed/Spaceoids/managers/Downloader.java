package com.waleed.Spaceoids.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

import com.waleed.Spaceoids.gamestates.UpdateState;
import com.waleed.Spaceoids.main.Spaceoids;
import com.waleed.Spaceoids.main.SpaceoidsMain;

public class Downloader implements Runnable {

	String bin = SpaceoidsMain.class.getProtectionDomain().getCodeSource()
			.getLocation().getPath();
	String decodedPath;

	private File jar;
	private float percent;

	public boolean isFinished = false;

	public boolean isError = false;
	
	boolean existedBefore = true;

	public void run() {
		try {
			decodedPath = URLDecoder.decode(bin, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(decodedPath);
		jar = new File(decodedPath + "/Spaceoids.jar");
		downloadJAR();

	}

	private void downloadJAR() {
		File file = new File(jar.toString());
		String dl = new String(Spaceoids.INSTANCE.downloadURL);
		
	  if(!file.exists())
		  existedBefore = false;
		try {
			System.out.println("File is created");
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		  
		try {

			URL url = new URL(dl);

			URLConnection urlCon = url.openConnection();
			InputStream is = urlCon.getInputStream();
			FileOutputStream fos = new FileOutputStream(file);

			int filesize = urlCon.getContentLength();
			float totalDataRead = 0;

			System.out.println(urlCon.getContentType());

			byte[] buffer = new byte[2000];
			for (int bytesRead = is.read(buffer); bytesRead > 0; bytesRead = is
					.read(buffer)) {
				totalDataRead = totalDataRead + bytesRead;
				fos.write(buffer, 0, bytesRead);
				float downloadedSize = (totalDataRead * 100) / filesize;
				percent = downloadedSize;
			}

			is.close();
			fos.close();

			isFinished = true;

			System.out.println("Finished the download");
			// JOptionPane.showMessageDialog(new JFrame(),
			// "Download finished, press OK to open");

		} catch (Exception e) {
			e.printStackTrace();
			if(!existedBefore)
				file.delete();

			isError = true;
			isFinished = false;

			// JOptionPane.showMessageDialog(new JFrame(),
			// "Unable to download: " + e);
		}

	}

	public boolean isError() {
		return isError;
	}

}
