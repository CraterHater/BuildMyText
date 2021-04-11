package bmt.craterhater.datahandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import bmt.craterhater.main.ErrorHandler;
import bmt.craterhater.main.Main;

public class DataHandler {
	
	//private static Map<DataPath, FileConfiguration> loadedConfigurations = new HashMap<>();
	
	public static void deleteFile(DataPath dataPath) {
		try {
			String file = dataPath.getFileName();
			String[] folder = dataPath.getFilePath();
			
			String s = "";
			for(String s2 : folder) {
				s += "/"+s2;
			}
			s = s.replaceFirst("/", "");
			
			try {
				Files.createDirectories(Paths.get(Main.main.getDataFolder()+"/"+s));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			File f = new File(Main.main.getDataFolder()+"/"+s,file+".yml");
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			f.delete();
		}catch(Exception e) {
			ErrorHandler.handleError(e);
		}
	}
	
	public static boolean checkIfFileExists(DataPath dataPath) {
		try {
			String file = dataPath.getFileName();
			String[] folder = dataPath.getFilePath();
			
			String s = "";
			for(String s2 : folder) {
				s += "/"+s2;
			}
			s = s.replaceFirst("/", "");
			
			try {
				Files.createDirectories(Paths.get(Main.main.getDataFolder()+"/"+s));
			} catch (IOException e) {
				e.printStackTrace();
			}
			File f = new File(Main.main.getDataFolder()+"/"+s,file+".yml");
			return f.exists();
		}catch(Exception e) {
			ErrorHandler.handleError(e);
		}
		return false;
	}
	
	public static void saveFile(FileConfiguration fc, DataPath dataPath){
		try {
			if(fc == null) {
				return;
			}
			
			String file = dataPath.getFileName();
			String[] folder = dataPath.getFilePath();
			
			String s = "";
			for(String s2 : folder) {
				s += "/"+s2;
			}
			s = s.replaceFirst("/", "");
			
			File f = new File(Main.main.getDataFolder()+"/"+s,file+".yml");
			try {
				fc.save(f);
			} catch (IOException e) {
				ErrorHandler.handleError(e);
			}
			
			YamlConfiguration.loadConfiguration(f);
		}catch(Exception e) {
			ErrorHandler.handleError(e);
		}
	}
	
	public static ArrayList<File> getAllFilesInDirectory(String... folder){
		try {
			String s = "";
			for(String s2 : folder) {
				s += "/"+s2;
			}
			s = s.replaceFirst("/", "");
			
			try {
				Files.createDirectories(Paths.get(Main.main.getDataFolder()+"/"+s));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			List<String> results2 = new ArrayList<String>();
			
			File dir = new File(Main.main.getDataFolder()+"/"+s);

			if(dir.exists()) {
					try (Stream<Path> walk = Files.walk(Paths.get(Main.main.getDataFolder()+"/"+s))) {

						results2 = walk.filter(Files::isRegularFile)
								.map(x -> x.toString()).collect(Collectors.toList());
					} catch (IOException e) {
						ErrorHandler.handleError(e);
					}
			}
			
			ArrayList<File> results = new ArrayList<>();
			for (String fileX : results2) {
				File file = new File(fileX);
				if(file.isFile()) {
					results.add(file);
				}
			}
			
			return results;
		}catch(Exception e) {
			ErrorHandler.handleError(e);
		}
		return null;
	}
	
	public static FileConfiguration getFile(DataPath dataPath) {
		try {
			String file = dataPath.getFileName();
			String[] folder = dataPath.getFilePath();
			
			if(file == null) {
				return null;
			}
			
			String s = "";
			for(String s2 : folder) {
				s += "/"+s2;
			}
			s = s.replaceFirst("/", "");
			
			try {
				Files.createDirectories(Paths.get(Main.main.getDataFolder()+"/"+s));
			} catch (IOException e) {
				ErrorHandler.handleError(e);
			}
			
			File f = new File(Main.main.getDataFolder()+"/"+s,file+".yml");
			try {
				f.createNewFile();
			} catch (IOException e) {
				ErrorHandler.handleError(e);
			}
			
			FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
			//loadedConfigurations.put(dataPath, fc);
			
			return fc;
		}catch(Exception e) {
			ErrorHandler.handleError(e);
		}
		return null;
	}
	
	public static void copyFile(String fileName, String... path) {
		try {
			DataPath dataPath = new DataPath(fileName, path);
			if(DataHandler.checkIfFileExists(dataPath)) {
				updateFile(fileName, path);
				return;
			}
			
			DataHandler.getFile(dataPath);
			
			String s = "";
			for(String s2 : path) {
				s += "/"+s2;
			}
			s = s.replaceFirst("/", "");
			
			InputStream in = Main.main.getResource(fileName+".yml");
		
		    byte[] buffer = new byte[in.available()];
		    in.read(buffer);
		 
		    File targetFile = new File(Main.main.getDataFolder()+"/"+s+"/"+fileName+".yml");
		    OutputStream outStream = new FileOutputStream(targetFile);
		    outStream.write(buffer);
		    
		    in.close();
		    outStream.close();
		}catch(Exception e) {
			ErrorHandler.handleError(e);
		}
	}
	
	public static void updateFile(String fileName, String[] path) {
		try {
			DataPath dataPath = new DataPath(fileName, path);
			FileConfiguration currentFile = getFile(dataPath);
			
			String s = "";
			for(String s2 : path) {
				s += "/"+s2;
			}
			s = s.replaceFirst("/", "");
			
			InputStream in = Main.main.getResource(fileName+".yml");
			byte[] buffer = new byte[in.available()];
			in.read(buffer);
			File targetFile = new File(Main.main.getDataFolder()+"/"+s+"/"+fileName+"-temp.yml");
			OutputStream outStream = new FileOutputStream(targetFile);
			outStream.write(buffer);
		    
		    in.close();
		    outStream.close();

		    FileConfiguration newFile = YamlConfiguration.loadConfiguration(targetFile);
		    
		    for(String field : newFile.getConfigurationSection("").getKeys(false)) {
		    	if(currentFile.contains(field)) {
		    		continue;
		    	}
		    	
		    	currentFile.set(field, newFile.getString(field));
		    }
		    
		    saveFile(currentFile, dataPath);
		    deleteFile(new DataPath(fileName+"-temp", path));
		}catch(Exception e) {
			ErrorHandler.handleError(e);
		}
	}
}





