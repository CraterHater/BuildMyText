package bmt.craterhater.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import bmt.craterhater.commandhandler.MasterCommand;
import bmt.craterhater.datahandler.Configuration;
import bmt.craterhater.datahandler.DataHandler;
import bmt.craterhater.datahandler.DataPath;
import bmt.craterhater.playerinput.Halign;
import bmt.craterhater.playerinput.PlayerInput;
import bmt.craterhater.playerinput.Valign;

public class Main extends JavaPlugin {

	public static Main main;
	public void onEnable() {
		Main.main = this;
		saveDefaultConfig();
		loadCommands();
		
		DataPath dataPath = new DataPath("temp");
		FileConfiguration fc = DataHandler.getFile(dataPath);
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(fc.contains(p.getUniqueId()+".font")) {
				MasterCommand.pickedFont.put(p.getUniqueId(), fc.getString(p.getUniqueId()+".font"));
			}
			
			if(fc.contains(p.getUniqueId()+".size")) {
				MasterCommand.pickedSize.put(p.getUniqueId(), fc.getInt(p.getUniqueId()+".size"));
			}
			
			if(fc.contains(p.getUniqueId()+".thickness")) {
				MasterCommand.pickedThickness.put(p.getUniqueId(), fc.getInt(p.getUniqueId()+".thickness"));
			}
			
			if(fc.contains(p.getUniqueId()+".underline")) {
				MasterCommand.pickedUnderline.put(p.getUniqueId(), fc.getString(p.getUniqueId()+".underline").equalsIgnoreCase("true"));
			}
			
			if(fc.contains(p.getUniqueId()+".material")) {
				MasterCommand.pickedMaterial.put(p.getUniqueId(), Material.getMaterial(fc.getString(p.getUniqueId()+".material")));
			}
			
			if(fc.contains(p.getUniqueId()+".halign")) {
				MasterCommand.pickedHalign.put(p.getUniqueId(), Halign.valueOf(fc.getString(p.getUniqueId()+".halign")));
			}
			
			if(fc.contains(p.getUniqueId()+".valign")) {
				MasterCommand.pickedValign.put(p.getUniqueId(), Valign.valueOf(fc.getString(p.getUniqueId()+".valign")));
			}
		}
		
		DataHandler.deleteFile(dataPath);
	}
	
	public void onDisable() {
		DataPath dataPath = new DataPath("temp");
		FileConfiguration fc = DataHandler.getFile(dataPath);
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(PlayerInput.playerLink.containsKey(p.getUniqueId())) {	
				PlayerInput input = PlayerInput.playerLink.get(p.getUniqueId());
				input.stop();
			}
			
			if(MasterCommand.pickedFont.containsKey(p.getUniqueId())) {
				fc.set(p.getUniqueId()+".font", MasterCommand.pickedFont.get(p.getUniqueId()));
			}
			
			if(MasterCommand.pickedSize.containsKey(p.getUniqueId())) {
				fc.set(p.getUniqueId()+".size", MasterCommand.pickedSize.get(p.getUniqueId()));
			}
			
			if(MasterCommand.pickedThickness.containsKey(p.getUniqueId())) {
				fc.set(p.getUniqueId()+".thickness", MasterCommand.pickedThickness.get(p.getUniqueId()));
			}
			
			if(MasterCommand.pickedUnderline.containsKey(p.getUniqueId())) {
				fc.set(p.getUniqueId()+".underline", MasterCommand.pickedUnderline.get(p.getUniqueId()));
			}
			
			if(MasterCommand.pickedMaterial.containsKey(p.getUniqueId())) {
				fc.set(p.getUniqueId()+".material", MasterCommand.pickedMaterial.get(p.getUniqueId()).name());
			}
			
			if(MasterCommand.pickedHalign.containsKey(p.getUniqueId())) {
				fc.set(p.getUniqueId()+".halign", MasterCommand.pickedHalign.get(p.getUniqueId()).name());
			}
			
			if(MasterCommand.pickedValign.containsKey(p.getUniqueId())) {
				fc.set(p.getUniqueId()+".valign", MasterCommand.pickedValign.get(p.getUniqueId()).name());
			}
		}
		DataHandler.saveFile(fc, dataPath);
	}
	
	private void loadCommands() {
		FileConfiguration configurationFile = Configuration.getConfiguration();
		
		List<String> aliases = new ArrayList<>();
		aliases.add(configurationFile.getString("main_command"));
		for(String alias : configurationFile.getStringList("command_aliases")) {
			aliases.add(alias);
		}
		
		Standards.COMMAND_ALIASES = aliases.toArray(new String[0]);
		
		String mainCommand = configurationFile.getString("main_command");
		
		
		MasterCommand command = new MasterCommand(mainCommand, "/<command> [args]", "Used to control " + Standards.PLUGIN_NAME, aliases);
		command.register();
		command.loadCommands();
	}
}
