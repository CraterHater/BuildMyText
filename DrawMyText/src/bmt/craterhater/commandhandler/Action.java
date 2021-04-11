package bmt.craterhater.commandhandler;

import org.bukkit.command.CommandSender;

public interface Action {

	abstract void call(CommandSender p, String... arguments);
}
