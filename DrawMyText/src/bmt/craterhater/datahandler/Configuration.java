package bmt.craterhater.datahandler;

import org.bukkit.configuration.file.FileConfiguration;

import bmt.craterhater.main.Main;

public class Configuration {

	public static FileConfiguration getConfiguration() {
		try {
			return Main.main.getConfig();
		}catch(Exception e) {
			return null;
		}
	}
}
