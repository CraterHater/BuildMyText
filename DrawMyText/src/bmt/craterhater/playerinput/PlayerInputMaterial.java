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
import bmt.craterhater.main.VersionDetector;

public class PlayerInputMaterial implements Listener {

	public static Map<UUID, PlayerInputMaterial> playerLink = new HashMap<>();
	
	private Player player;
	private PlayerMaterialInput call;
	
	public PlayerInputMaterial(Player p, PlayerMaterialInput call) {
		if(playerLink.containsKey(p.getUniqueId())) {	
			PlayerInputMaterial input = playerLink.get(p.getUniqueId());
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
		
		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		
		event.setCancelled(true);

		playerLink.remove(p.getUniqueId());
		HandlerList.unregisterAll(this);
		
		if(VersionDetector.getVersion() <= 8) {
			call.call(p.getInventory().getItemInHand());
		}else {
			call.call(p.getInventory().getItemInMainHand());
		}
	}
}
	