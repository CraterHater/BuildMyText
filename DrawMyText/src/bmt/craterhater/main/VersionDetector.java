package bmt.craterhater.main;

import org.bukkit.Bukkit;
import org.bukkit.Material;

public class VersionDetector {

	public static int getVersion() {
		try {
			String ver = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
			String[] parts = ver.split("_");
			
			return Integer.parseInt(parts[1]);
		}catch(Exception e) {
			return 0;
		}
	}
	
	public static Material getMaterial(String... strings) {
		for(String s : strings) {
			try {
				Material mat = Material.getMaterial(s);
				return mat;
			}catch(Exception e) {
				
			}
		}
		
		return null;
	}
}
