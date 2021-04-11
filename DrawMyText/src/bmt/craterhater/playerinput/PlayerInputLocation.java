package bmt.craterhater.playerinput;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import bmt.craterhater.main.Main;

public class PlayerInputLocation implements Listener {

	public static Map<UUID, PlayerInputLocation> playerLink = new HashMap<>();
	
	private Player player;
	private PlayerLocationInput call;
	
	public PlayerInputLocation(Player p, PlayerLocationInput call) {
		if(playerLink.containsKey(p.getUniqueId())) {	
			PlayerInputLocation input = playerLink.get(p.getUniqueId());
			HandlerList.unregisterAll(input);
		}
		
		playerLink.put(p.getUniqueId(), this);
		Bukkit.getPluginManager().registerEvents(this, Main.main);
		this.player = p;
		this.call = call;
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
	public void onClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		
		if(!p.equals(player)) {
			return;
		}
		
		if(event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
			return;
		}
		
		event.setCancelled(true);

		playerLink.remove(p.getUniqueId());
		HandlerList.unregisterAll(this);
		
	    call.call(p.getLocation());
	}
}
	