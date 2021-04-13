package bmt.craterhater.commandhandler;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import bmt.craterhater.datahandler.DataHandler;
import bmt.craterhater.datahandler.DataPath;
import bmt.craterhater.main.ErrorHandler;
import bmt.craterhater.main.Standards;
import bmt.craterhater.main.Toolbox;
import bmt.craterhater.playerinput.PlayerInput;
import bmt.craterhater.playerinput.PlayerInputLocation;
import bmt.craterhater.playerinput.PlayerInputMaterial;
import bmt.craterhater.playerinput.PlayerLocationInput;
import bmt.craterhater.playerinput.PlayerMaterialInput;
import bmt.craterhater.playerinput.PlayerStringInput;
import bmt.craterhater.text.FontConverter;
import bmt.craterhater.text.Halign;
import bmt.craterhater.text.Valign;
public class MasterCommand extends AbstractCommand{

	 public MasterCommand(String command, String usage, String description, List<String> aliases) {
        super(command, usage, description, null, aliases);
    }

	private ArrayList<CECommand> allCommands = new ArrayList<>();
	
	public void loadCommands() {
		allCommands.clear();
		
		allCommands.add(new CECommand("Shows you all available commands on that page.",Standards.MAIN_PERMISSION+".help",Standards.COMMAND_CATEGORIES[0],true,true,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					int cmd = Integer.parseInt(arguments[0]);
					
					if(cmd < 1) {cmd=0;}
					if(cmd > getMaxPages()) {cmd = getMaxPages();}
					
					sendHelpMessage(p, cmd);
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument(ArgumentType.INTEGER, "page")));
		
		allCommands.add(new CECommand("Shows you additional information about this command. Use it to get a better understanding of the command.",Standards.MAIN_PERMISSION+".help",Standards.COMMAND_CATEGORIES[0],true,true,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					int cmd = Integer.parseInt(arguments[1])-1;
					
					if(cmd < 0) {cmd=0;}
					if(cmd > allCommands.size()-1) {cmd = allCommands.size()-1;}
					
					CECommand command = allCommands.get(cmd);
					
					String s = "";
					
					for(int i = 0; i < command.getArgumentCount(); i++) {
						s += ChatColor.DARK_GRAY + ", " + ChatColor.GRAY + command.getDescription(i);
					}
					
					s = s.replaceFirst(ChatColor.DARK_GRAY + ", ", "");
					
					p.sendMessage("");
					p.sendMessage(ChatColor.GREEN+""+ChatColor.BOLD+"Information");
					p.sendMessage(ChatColor.DARK_PURPLE+"/"+ChatColor.LIGHT_PURPLE+Standards.COMMAND_ALIASES[0] + " " + s);
					p.sendMessage(ChatColor.WHITE+command.getHelpMessage());
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("help"), new Argument(ArgumentType.INTEGER, "command")));
		
		allCommands.add(new CECommand("Shows you all settings you currently have configured. Changes to these settings are stored even if the server restarts so it is useful to check what settings you have configured.",Standards.MAIN_PERMISSION+".info",Standards.COMMAND_CATEGORIES[0],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					q.sendMessage("");
					q.sendMessage(Toolbox.c()+"Okay! "+Toolbox.r()+"Here are your current settings:");
					
					PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(q);
					
					String text = playerSetting.getText();
					String font = playerSetting.getFont();
					int size = playerSetting.getSize();
					Material material = playerSetting.getMaterial();
					int thickness = playerSetting.getThickness();
					int fontType = playerSetting.getFontType();
					boolean underline = playerSetting.isUnderline();
					Halign halign = playerSetting.getHalign();
					Valign valign = playerSetting.getValign();
					
					if(text != null) {
						q.sendMessage(Toolbox.g()+"Text: " + Toolbox.r() + text);
					}else {
						q.sendMessage(Toolbox.g()+"Text: " + Toolbox.r() + "n/a");
					}
					
					q.sendMessage(Toolbox.g()+"Size: " + Toolbox.r() + size);
					q.sendMessage(Toolbox.g()+"Material: " + Toolbox.r() + Toolbox.capitalizeWords(material.name()));
					q.sendMessage(Toolbox.g()+"Font: " + Toolbox.r() + font);
					
					if(fontType == 0) {
						q.sendMessage(Toolbox.g()+"FontType: " + Toolbox.r() + "Normal");
					}else if(fontType == 1) {
						q.sendMessage(Toolbox.g()+"FontType: " + Toolbox.r() + "Bold");
					}else if(fontType == 2) {
						q.sendMessage(Toolbox.g()+"FontType: " + Toolbox.r() + "Italics");
					}
					
					q.sendMessage(Toolbox.g()+"Thickness: " + Toolbox.r() + thickness);
					q.sendMessage(Toolbox.g()+"Underline: " + Toolbox.r() + Toolbox.capitalizeWords((underline+"").toLowerCase()));
					q.sendMessage(Toolbox.g()+"Horizontal Alignment: " + Toolbox.r() + Toolbox.capitalizeWords(halign.name()));
					q.sendMessage(Toolbox.g()+"Vertical Alignment: " + Toolbox.r() + Toolbox.capitalizeWords(valign.name()));
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("info")));
		
		allCommands.add(new CECommand("Allows you to set what text will appear when you use the /text build, command. Use '<>' to signify a line-break.",Standards.MAIN_PERMISSION+".text",Standards.COMMAND_CATEGORIES[1],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					q.sendMessage(Toolbox.c()+"Okay! "+Toolbox.r()+"Please write the text you want displayed in chat.");
					
					new PlayerInput(q, new PlayerStringInput() {
						public void call(String answer) {
							PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(q);
							playerSetting.setText(answer);
							playerSetting.update(q);
							q.sendMessage(Toolbox.g()+"Success!");
						}
					}, false);
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("text")));
		
		allCommands.add(new CECommand("Allows you to set the size of the text.",Standards.MAIN_PERMISSION+".size",Standards.COMMAND_CATEGORIES[1],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;

					int size = Integer.parseInt(arguments[1]);
					if(size > 100) {
						q.sendMessage(Toolbox.b()+"Problem! "+Toolbox.r()+" That size is far too large! It won't fit!");
						return;
					}
					
					PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(q);
					playerSetting.setSize(size);
					playerSetting.update(q);
					
					String msg = Toolbox.g()+"Success! "+Toolbox.r()+size + " is very large... Be careful...";
					
					if(size > 60) {
						msg = Toolbox.g()+"Success! "+Toolbox.r()+size + " is quite large... Be careful...";
					}else if(size > 40) {
						msg = Toolbox.g()+"Success! "+Toolbox.r()+size + " seems like a good, but still quite large, size.";
					}else if(size > 10) {
						msg = Toolbox.g()+"Success! "+Toolbox.r()+size + " is a good size.";
					}else if(size > 5) {
						msg = Toolbox.g()+"Success! "+Toolbox.r()+size + " is a charming size.";
					}else{
						msg = Toolbox.g()+"Success! "+Toolbox.r()+size + " is quite a small size.";
					}
					
					q.sendMessage(msg);
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("size"), new Argument(ArgumentType.INTEGER, "fontSize")));
		
		allCommands.add(new CECommand("Allows you to set the material you want for the text.",Standards.MAIN_PERMISSION+".material",Standards.COMMAND_CATEGORIES[1],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					q.sendMessage(Toolbox.c()+"Okay! "+Toolbox.r()+"Please right click with the item you want in your hand.");
					
					new PlayerInputMaterial(q, new PlayerMaterialInput() {
						public void call(ItemStack answer) {
							if(answer == null) {
								return;
							}
							
							Material mat = answer.getType();
							if(mat.isBlock()) {
								q.sendMessage(Toolbox.g()+"Success! "+Toolbox.r()+Toolbox.capitalizeWords(mat.name()) + " is a great choice!");
							}else {
								q.sendMessage(Toolbox.b()+"Problem! "+Toolbox.r()+Toolbox.capitalizeWords(mat.name()) + " is a weird choice... Are you sure it can be used as a block for the text?");
								return;
							}
							
							PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(q);
							playerSetting.setMaterial(mat);
							playerSetting.update(q);
						}	
					});
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("material")));
		
		allCommands.add(new CECommand("Allows you to set the font of the text. Something like 'Arial' or 'Times_New_Roman'. Please use underscores instead of spaces.",Standards.MAIN_PERMISSION+".font",Standards.COMMAND_CATEGORIES[1],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					String fontFound = FontConverter.getCorrespondingFont(arguments[1]);
					
					if(fontFound == null) {
						q.sendMessage(Toolbox.c()+"Oops! "+Toolbox.r()+"that font could not be found... "+Toolbox.c()+"Try Again! "+Toolbox.r()+"Only fonts installed on the server computer will work.");
						return;
					}
					
					q.sendMessage(Toolbox.g()+"Success! "+Toolbox.r()+fontFound + " seems like a perfect choice!");
					
					PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(q);
					playerSetting.setFont(fontFound);
					playerSetting.update(q);
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("font"), new Argument(ArgumentType.STRING, "fontName")));
		
		allCommands.add(new CECommand("Allows you to actually create new text!",Standards.MAIN_PERMISSION+".build",Standards.COMMAND_CATEGORIES[3],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(q);
					if(!playerSetting.prerequisitesMet(q)) {return;}
					playerSetting.stopEditing(q);
					
					Valign valign = playerSetting.getValign();
					Halign halign = playerSetting.getHalign();
					
					q.sendMessage(Toolbox.r()+ "Move to a location that will be the " + Toolbox.c() + "vertical " + valign.name().toLowerCase() + Toolbox.r() + " and " + Toolbox.c() + "horizontal " + halign.name().toLowerCase() + Toolbox.r() +" of the text. " + Toolbox.c()+"Look " + Toolbox.r()+"in the direction you want the text to appear in. " + Toolbox.c()+"Left Click " + Toolbox.r()+"when you are ready. A preview will appear, allowing you to confirm.");
					
					new PlayerInputLocation(q, new PlayerLocationInput() {
						public void call(Location answer) {
							PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(q);
							if(!playerSetting.prerequisitesMet(q)) {return;}
							playerSetting.startEditing(q, answer, false);
						}
					});
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("build")));
		
		allCommands.add(new CECommand("Allows you to edit the nearest text, allowing you to make changes even after having finished editing the text.",Standards.MAIN_PERMISSION+".edit",Standards.COMMAND_CATEGORIES[3],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					DataPath dataPath = new DataPath("text");
					FileConfiguration fc = DataHandler.getFile(dataPath);
					
					if(!fc.contains("Text")) {
						q.sendMessage(Toolbox.b()+"Problem! " + Toolbox.r() + "No texts found...");
						return;
					}
					
					String closest = null;
					double dx = 0;
					for(String s : fc.getConfigurationSection("Text").getKeys(false)) {
						String[] parts = s.split(",");
						String world = parts[0];
						int x = Integer.parseInt(parts[1]);
						int y = Integer.parseInt(parts[2]);
						int z = Integer.parseInt(parts[3]);
						
						if(world.equalsIgnoreCase(q.getWorld().getName())) {
							Location loc = new Location(Bukkit.getWorld(world), x, y, z);
							double distance = loc.distance(q.getLocation());
							
							if(closest == null || dx > distance) {
								dx = distance;
								closest = s;
							}
						}
					}
					
					if(closest == null || dx > 200) {
						q.sendMessage(Toolbox.b()+"Problem! " + Toolbox.r() + "No text nearby...");
						return;
					}
					
					q.sendMessage(Toolbox.g()+"Success! " + Toolbox.r() + "You are now editing the text that reads: " + ChatColor.GRAY + fc.getString("Text."+closest+".text"));
					
					String[] parts = closest.split(",");
					String world = parts[0];
					int x = Integer.parseInt(parts[1]);
					int y = Integer.parseInt(parts[2]);
					int z = Integer.parseInt(parts[3]);
					double yaw = Double.parseDouble(parts[4]);
					Location loc = new Location(Bukkit.getWorld(world), x, y, z, (float)yaw, (float)yaw);
					
					Material mat = Material.valueOf(fc.getString("Text."+closest+".material"));
					String text = fc.getString("Text."+closest+".text");
					String font = fc.getString("Text."+closest+".font");
					int fontType = fc.getInt("Text."+closest+".fontType");
					int size = fc.getInt("Text."+closest+".size");
					boolean underline = fc.getString("Text."+closest+".underline").equalsIgnoreCase("true");
					int thickness = fc.getInt("Text."+closest+".thickness");
					Valign valign = Valign.valueOf(fc.getString("Text."+closest+".valign"));
					Halign halign = Halign.valueOf(fc.getString("Text."+closest+".halign"));
					
					PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(q);
					playerSetting.setMaterial(mat);
					playerSetting.setText(text);
					playerSetting.setFont(font);
					playerSetting.setFontType(fontType);
					playerSetting.setSize(size);
					playerSetting.setUnderline(underline);
					playerSetting.setThickness(thickness);
					playerSetting.setValign(valign);
					playerSetting.setHalign(halign);
					
					playerSetting.startEditing(q, loc, true);
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("edit")));
		
		allCommands.add(new CECommand("Allows you to apply your changes. Alternatively you can type 'yes' in chat.",Standards.MAIN_PERMISSION+".confirm",Standards.COMMAND_CATEGORIES[3],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(q);
					playerSetting.confirmChances(q);
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("confirm")));
		
		allCommands.add(new CECommand("Allows you to cancel your changes. Alternatively you can type 'no' in chat.",Standards.MAIN_PERMISSION+".reject",Standards.COMMAND_CATEGORIES[3],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(q);
					playerSetting.stopEditing(q);
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("reject")));
		
		allCommands.add(new CECommand("Allows you to choose either normal, bold or italic text.",Standards.MAIN_PERMISSION+".type",Standards.COMMAND_CATEGORIES[2],true,false,2,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(q);
					
					if(arguments[1].equalsIgnoreCase("bold")) {
						playerSetting.setFontType(Font.BOLD);
						q.sendMessage(Toolbox.g()+"Success! "+Toolbox.r()+ "Bold will look great!");
					}else if(arguments[1].equalsIgnoreCase("italic")) {
						playerSetting.setFontType(Font.ITALIC);
						q.sendMessage(Toolbox.g()+"Success! "+Toolbox.r()+ "Italic will look great!");
					}else if(arguments[1].equalsIgnoreCase("plain") || arguments[1].equalsIgnoreCase("normal")) {
						playerSetting.setFontType(Font.PLAIN);
						q.sendMessage(Toolbox.g()+"Success! "+Toolbox.r()+ "Normal will look great!");
					}else {
						q.sendMessage(Toolbox.c()+"Problem! "+Toolbox.r()+ "Write either bold, italic or normal.");
					}
					
					playerSetting.update(q);
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("type"), new Argument(ArgumentType.STRING, "bold, italic or normal")));
		
		allCommands.add(new CECommand("Allows you to set the thickness of the text.",Standards.MAIN_PERMISSION+".thickness",Standards.COMMAND_CATEGORIES[2],true,false,2,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					int thickness = Integer.parseInt(arguments[1]);
					
					if(thickness > 40) {
						q.sendMessage(Toolbox.b()+"Problem! "+Toolbox.r()+thickness + " is far too large!");
						return;
					}
					
					PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(q);
					playerSetting.setThickness(thickness);
					playerSetting.update(q);
					
					String msg = Toolbox.g()+"Success! "+Toolbox.r()+thickness + " is quite large... Be careful...";
					
					if(thickness > 10) {
						msg = Toolbox.g()+"Success! "+Toolbox.r()+thickness + " is large.";
					}else {
						msg = Toolbox.g()+"Success! "+Toolbox.r()+thickness + " is a good thickness";
					}
					
					q.sendMessage(msg);
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("thickness"), new Argument(ArgumentType.INTEGER, "thickness")));
		
		allCommands.add(new CECommand("Allows you to set whether the text has an underline. Run this command to toggle it either on or off.",Standards.MAIN_PERMISSION+".underline",Standards.COMMAND_CATEGORIES[2],true,false,2,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(q);
					
					boolean underline = !playerSetting.isUnderline();
					
					playerSetting.setUnderline(underline);

					if(underline) {
						q.sendMessage(Toolbox.g()+"Success! " + Toolbox.r() + "Turned underline on!");
					}else {
						q.sendMessage(Toolbox.g()+"Success! " + Toolbox.r() + "Turned underline off!");
					}
					
					playerSetting.update(q);
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("underline")));
		
		allCommands.add(new CECommand("Changes the vertical 'anchor point' when pasting the text down. Default is center of the text.",Standards.MAIN_PERMISSION+".valign",Standards.COMMAND_CATEGORIES[2],true,false,2,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					try {
						Valign valign = Valign.valueOf(arguments[1].toUpperCase());
						q.sendMessage(Toolbox.g()+"Success! " + Toolbox.r() + "Set the vertical align to "+valign.name().toLowerCase()+". Your position will now indicate the "+valign.name().toLowerCase()+" of the text.");
						
						PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(q);
						playerSetting.setValign(valign);
					}catch(Exception e) {
						q.sendMessage(Toolbox.b()+"Problem! " + Toolbox.r() + "Pick either top, bottom or center.");
					}
					
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("valign"), new Argument(ArgumentType.STRING, "top, bottom or center")));
		
		allCommands.add(new CECommand("Changes the horizontal 'anchor point' when pasting the text down. Default is center of the text.",Standards.MAIN_PERMISSION+".halign",Standards.COMMAND_CATEGORIES[2],true,false,2,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					try {
						Halign halign = Halign.valueOf(arguments[1].toUpperCase());
						q.sendMessage(Toolbox.g()+"Success! " + Toolbox.r() + "Set the horizontal align to "+halign.name().toLowerCase()+". Your position will now indicate the "+halign.name().toLowerCase()+" of the text.");
						
						PlayerSettings playerSetting = PlayerSettings.getPlayerSettings(q);
						playerSetting.setHalign(halign);
					}catch(Exception e) {
						q.sendMessage(Toolbox.b()+"Problem! " + Toolbox.r() + "Pick either left, right or center.");
					}
					
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("halign"), new Argument(ArgumentType.STRING, "left, right or center")));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		boolean isCommandValid = false;
		
		for(String viableCommand : Standards.COMMAND_ALIASES) {
			if(!viableCommand.equalsIgnoreCase(commandLabel)) {
				continue;
			}
			isCommandValid = true;
			break;
		}
		
		if(!isCommandValid) {
			return true;
		}
		
		if(args.length == 0) {
			sendHelpMessage(sender, 1);
			return true;
		}
		
		boolean valid = false;
		for(CECommand command : allCommands) {
			if(!command.canConsole() && !(sender instanceof Player)) {
				continue;
			}
			
			if(command.isOP() && !sender.isOp() && !sender.hasPermission(command.getPermission()) && !sender.hasPermission(Standards.MAIN_PERMISSION+".*")) {
				sender.sendMessage(ChatColor.RED+"Invalid Permissions");
				break;
			}
			
			if(!command.checkPlayerExecutes(sender, args)) {
				continue;
			}
			
			valid = true;
			break;
		}
	
		if(!valid) {
			sendHelpMessage(sender, 1);
		}
		
		return true;
	}
	
	private void sendHelpMessage(CommandSender p, int page) {		
		boolean console = true;
		if(p instanceof Player) {
			console = false;
		}
		
		p.sendMessage("");
		p.sendMessage(ChatColor.GOLD+"                   " + Standards.PLUGIN_NAME);
		p.sendMessage(ChatColor.GRAY+"                        "+page+"/"+getMaxPages());
		p.sendMessage(ChatColor.YELLOW+"Use /text <page>, to see other pages.");
		p.sendMessage("");
		
		String chapter = null;
		
		int ID = 0;
		for(CECommand command : allCommands) {
			ID++;
			
			if(command.isOP() && !p.isOp() && !p.hasPermission(command.getPermission()) && !p.hasPermission(Standards.MAIN_PERMISSION+".*")) {
				continue;
			}
			
			if(console && !command.canConsole()) {
				continue;
			}
			
			if(command.getPage() != page) {
				continue;
			}
			
			if(chapter == null || !chapter.equals(command.getChapter())){
				p.sendMessage("");
				p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+command.getChapter());
				chapter = command.getChapter();
			}
			
			String allArguments = "";
			for(int i = 0; i < command.getArgumentCount(); i++) {
				if(command.getArgumentType(i) == ArgumentType.LABEL) {
					allArguments += " " + command.getLabel(i);
				}else {
					allArguments += " " + "<"+command.getDescription(i)+">";
				}
			}
			
			p.sendMessage(ChatColor.GRAY+"#"+ChatColor.WHITE+ID+": /" + ChatColor.YELLOW + Standards.COMMAND_ALIASES[0] + ChatColor.GRAY + allArguments);
		}
		
		if(page != getMaxPages()) {
			p.sendMessage("");
			p.sendMessage(ChatColor.YELLOW+"See the next page for more commands!");
		}else {
			p.sendMessage("");
			p.sendMessage(ChatColor.YELLOW+"See the previous page for more commands!");
		}
	}
	
	public int getMaxPages() {
		int maxPages = 0;
		for(CECommand command : allCommands) {
			if(command.getPage() > maxPages) {
				maxPages = command.getPage();
			}
		}
		return maxPages;
	}
}

