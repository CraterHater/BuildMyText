package bmt.craterhater.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import bmt.craterhater.commandhandler.MasterCommand;
import bmt.craterhater.commandhandler.PlayerSettings;
import bmt.craterhater.datahandler.Configuration;
import bmt.craterhater.datahandler.DataHandler;
import bmt.craterhater.datahandler.DataPath;
import bmt.craterhater.eventlisteners.QuitListener;
import bmt.craterhater.text.Halign;
import bmt.craterhater.text.Valign;

public class Main extends JavaPlugin {
	
	public static Main main;
	public void onEnable() {
		Main.main = this;
		saveDefaultConfig();
		loadCommands();
		
		new QuitListener();
		
		DataPath dataPath = new DataPath("temp");
		FileConfiguration fc = DataHandler.getFile(dataPath);
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(!fc.contains(p.getUniqueId()+".material")) {continue;}
			Material mat = Material.valueOf(fc.getString(p.getUniqueId()+".material"));
			String text = fc.getString(p.getUniqueId()+".text");
			String font = fc.getString(p.getUniqueId()+".font");
			int fontType = fc.getInt(p.getUniqueId()+".fontType");
			int size = fc.getInt(p.getUniqueId()+".size");
			boolean underline = fc.getString(p.getUniqueId()+".underline").equalsIgnoreCase("true");
			int thickness = fc.getInt(p.getUniqueId()+".thickness");
			Valign valign = Valign.valueOf(fc.getString(p.getUniqueId()+".valign"));
			Halign halign = Halign.valueOf(fc.getString(p.getUniqueId()+".halign"));
			
			PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(p);
			playerSetting.setMaterial(mat);
			playerSetting.setText(text);
			playerSetting.setFont(font);
			playerSetting.setFontType(fontType);
			playerSetting.setSize(size);
			playerSetting.setUnderline(underline);
			playerSetting.setThickness(thickness);
			playerSetting.setValign(valign);
			playerSetting.setHalign(halign);
		}
		
		DataHandler.deleteFile(dataPath);
	}
	
	public void onDisable() {
		DataPath dataPath = new DataPath("temp");
		FileConfiguration fc = DataHandler.getFile(dataPath);
		for(Player p : Bukkit.getOnlinePlayers()) {
			PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(p);
			fc.set(p.getUniqueId()+".font", playerSetting.getFont());
			fc.set(p.getUniqueId()+".text", playerSetting.getText());
			fc.set(p.getUniqueId()+".size", playerSetting.getSize());
			fc.set(p.getUniqueId()+".fontType", playerSetting.getFontType());
			fc.set(p.getUniqueId()+".thickness", playerSetting.getThickness());
			fc.set(p.getUniqueId()+".underline", playerSetting.isUnderline()+"");
			fc.set(p.getUniqueId()+".material", playerSetting.getMaterial().name());
			fc.set(p.getUniqueId()+".halign", playerSetting.getHalign().name());
			fc.set(p.getUniqueId()+".valign", playerSetting.getValign().name());
			playerSetting.stopEditing(p);		
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
