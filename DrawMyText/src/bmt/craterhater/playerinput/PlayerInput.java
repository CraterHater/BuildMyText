package bmt.craterhater.playerinput;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import bmt.craterhater.main.Main;
import net.md_5.bungee.api.ChatColor;

public class PlayerInput implements Listener {

	public static Map<UUID, PlayerInput> playerLink = new HashMap<>();
	
	private Player player;
	public PlayerStringInput call;
	public boolean callAnyways;
	
	public PlayerInput(Player p, PlayerStringInput call, boolean callAnyways) {
		if(playerLink.containsKey(p.getUniqueId())) {	
			PlayerInput input = playerLink.get(p.getUniqueId());
			input.stop();
		}
		
		playerLink.put(p.getUniqueId(), this);
		Bukkit.getPluginManager().registerEvents(this, Main.main);
		this.player = p;
		this.call = call;
		this.callAnyways = callAnyways;
	}
	
	public void stop() {
		HandlerList.unregisterAll(this);
		
		if(callAnyways) {
			//call.call("No");
		}
	}
	
	@EventHandler
	public void onExit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		
		if(!p.equals(player)) {
			return;
		}
		
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		
		if(!p.equals(player)) {
			return;
		}
		
		event.setCancelled(true);
		
		HandlerList.unregisterAll(this);
		
		if(event.getMessage().equalsIgnoreCase("cancel")) {
			p.sendMessage(ChatColor.GREEN+"Cancelled!");
			return;
		}
		
		playerLink.remove(p.getUniqueId());
		
		new BukkitRunnable() {
	        @Override
	        public void run() {
	        	call.call(event.getMessage());
	        }
	     }.runTask(Main.main);
	}
}
	