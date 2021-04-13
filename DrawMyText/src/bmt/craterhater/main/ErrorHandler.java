package bmt.craterhater.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;

public class ErrorHandler {

	public static List<Exception> thrownException = new ArrayList<>();
	public static void handleError(Exception errorLog) {
		if(thrownException.contains(errorLog)) {
			Bukkit.getConsoleSender().sendMessage("[Build My Text] Repeat error...");
			return;
		}
		
		thrownException.add(errorLog);
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED+""+ChatColor.BOLD+"");
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED+""+ChatColor.BOLD+"                    Build My Text Error");
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED+""+ChatColor.BOLD+"          Please report to developer with stacktrace below");
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED+""+ChatColor.BOLD+"");
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED+""+ChatColor.BOLD+"Error Stacktrace: ");
		errorLog.printStackTrace();
	}
}
