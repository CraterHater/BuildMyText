package bmt.craterhater.eventlisteners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import bmt.craterhater.commandhandler.PlayerSettings;
import bmt.craterhater.main.Main;

public class QuitListener implements Listener {

	public QuitListener() {
		Bukkit.getPluginManager().registerEvents(this, Main.main);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		
		PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(p);
		playerSetting.stopEditing(p);
	}
}
