package com.tencent.tmsecure.demo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;

public class LogUtil {

	public static boolean writeToDefaultFile(String aLog) {
		File SDfiles = Environment.getExternalStorageDirectory(); // SDCards
		if (!SDfiles.canWrite()) {
			return false;
		}
		return writeRubbishLog(SDfiles.getAbsolutePath() + File.separator + "tencnet_sdk_demo_default.log", aLog);
	}

	public static boolean writeToSpecialFile(String aFileName, String aLog) {
		File SDfiles = Environment.getExternalStorageDirectory(); // SDCards
		if (!SDfiles.canWrite()) {
			return false;
		}
		return writeRubbishLog(SDfiles.getAbsolutePath() + File.separator + aFileName, aLog);
	}

	public static boolean writeRubbishLog(String aLogPath, String aLog) {
		FileWriter fw = null;
		try {
			File logFile = new File(aLogPath);
			try {
				if (!logFile.exists()) {
					logFile.createNewFile();
				}
				fw = new FileWriter(logFile, true);
			} catch (Exception e) {
			}
			fw.write(aLog + "\n");
			fw.flush();
		} catch (Exception e) {
			return false;
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
}