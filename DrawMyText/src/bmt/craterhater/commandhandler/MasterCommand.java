package bmt.craterhater.commandhandler;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import bmt.craterhater.main.ErrorHandler;
import bmt.craterhater.main.Standards;
import bmt.craterhater.main.Toolbox;
import bmt.craterhater.main.VersionDetector;
import bmt.craterhater.playerinput.Halign;
import bmt.craterhater.playerinput.PlayerInput;
import bmt.craterhater.playerinput.PlayerInputLocation;
import bmt.craterhater.playerinput.PlayerInputMaterial;
import bmt.craterhater.playerinput.PlayerLocationInput;
import bmt.craterhater.playerinput.PlayerMaterialInput;
import bmt.craterhater.playerinput.PlayerStringInput;
import bmt.craterhater.playerinput.Valign;
import bmt.craterhater.text.FontConverter;
import bmt.craterhater.text.Text;
public class MasterCommand extends AbstractCommand{

	 public MasterCommand(String command, String usage, String description, List<String> aliases) {
        super(command, usage, description, null, aliases);
    }

	private ArrayList<CECommand> allCommands = new ArrayList<>();
	
	//String permission, String chapter, boolean isOP, boolean console, int page, Action action, Argument... arguments
	
	public static Map<UUID, List<Block>> affected = new HashMap<>();
	
	public static Map<UUID, String> pickedFont = new HashMap<>();
	public static Map<UUID, Integer> pickedSize = new HashMap<>();
	public static Map<UUID, Material> pickedMaterial = new HashMap<>();
	
	public static Map<UUID, Integer> pickedThickness = new HashMap<>();
	public static Map<UUID, Boolean> pickedUnderline = new HashMap<>();
	
	public static Map<UUID, Halign> pickedHalign = new HashMap<>();
	public static Map<UUID, Valign> pickedValign = new HashMap<>();
	
	public void loadCommands() {
		allCommands.clear();
		
		allCommands.add(new CECommand(Standards.MAIN_PERMISSION+".help",Standards.COMMAND_CATEGORIES[0],true,true,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					int page = Integer.parseInt(arguments[1]);
					if(page < 1) {page = 1;}
					int maxPages = getMaxPages();
					if(page > maxPages) {page = maxPages;}
					
					sendHelpMessage(p, page);
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("help"), new Argument(ArgumentType.INTEGER, "page")));
		
		allCommands.add(new CECommand(Standards.MAIN_PERMISSION+".info",Standards.COMMAND_CATEGORIES[0],true,true,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					q.sendMessage("");
					q.sendMessage(Toolbox.c()+"Okay! "+Toolbox.r()+"Here are your current settings:");
					
					if(pickedSize.containsKey(q.getUniqueId())) {
						q.sendMessage(Toolbox.g()+"Size: " + Toolbox.r() + pickedSize.get(q.getUniqueId()));
					}else {
						q.sendMessage(Toolbox.g()+"Size: " + Toolbox.r() + "n/a");
					}
					
					if(pickedMaterial.containsKey(q.getUniqueId())) {
						q.sendMessage(Toolbox.g()+"Material: " + Toolbox.r() + Toolbox.capitalizeWords(pickedMaterial.get(q.getUniqueId()).name()));
					}else {
						q.sendMessage(Toolbox.g()+"Material: " + Toolbox.r() + "n/a");
					}
					
					if(pickedFont.containsKey(q.getUniqueId())) {
						q.sendMessage(Toolbox.g()+"Font: " + Toolbox.r() + pickedFont.get(q.getUniqueId()));
					}else {
						q.sendMessage(Toolbox.g()+"Font: " + Toolbox.r() + "n/a");
					}
					
					if(pickedThickness.containsKey(q.getUniqueId())) {
						q.sendMessage(Toolbox.g()+"Thickness: " + Toolbox.r() + pickedThickness.get(q.getUniqueId()));
					}else {
						q.sendMessage(Toolbox.g()+"Thickness: " + Toolbox.r() + "1");
					}
					
					if(pickedUnderline.containsKey(q.getUniqueId())) {
						q.sendMessage(Toolbox.g()+"Underline: " + Toolbox.r() + Toolbox.capitalizeWords(pickedUnderline.get(q.getUniqueId()).toString().toLowerCase()));
					}else {
						q.sendMessage(Toolbox.g()+"Underline: " + Toolbox.r() + "False");
					}
					
					if(pickedHalign.containsKey(q.getUniqueId())) {
						q.sendMessage(Toolbox.g()+"Horizontal Alignment: " + Toolbox.r() + Toolbox.capitalizeWords(pickedHalign.get(q.getUniqueId()).name()));
					}else {
						q.sendMessage(Toolbox.g()+"Horizontal Alignment: " + Toolbox.r() + "Center");
					}
					
					if(pickedValign.containsKey(q.getUniqueId())) {
						q.sendMessage(Toolbox.g()+"Vertical Alignment: " + Toolbox.r() + Toolbox.capitalizeWords(pickedValign.get(q.getUniqueId()).name()));
					}else {
						q.sendMessage(Toolbox.g()+"Vertical Alignment: " + Toolbox.r() + "Center");
					}
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("info")));
		
		allCommands.add(new CECommand(Standards.MAIN_PERMISSION+".size",Standards.COMMAND_CATEGORIES[1],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;

					int size = Integer.parseInt(arguments[1]);
					if(size > 100) {
						q.sendMessage(Toolbox.b()+"Problem! "+Toolbox.r()+" That size is far too large! It won't fit!");
						return;
					}
					
					pickedSize.put(q.getUniqueId(), size);
					
					if(PlayerInput.playerLink.containsKey(q.getUniqueId())) {	
						PlayerInput input = PlayerInput.playerLink.get(q.getUniqueId());
						if(input.callAnyways) {
							input.call.call("update");
							input.callAnyways = false;
							input.stop();
						}
					}
					
					if(size > 80) {
						q.sendMessage(Toolbox.g()+"Success! "+Toolbox.r()+size + " is very large... Be careful...");
						return;
					}
					
					if(size > 60) {
						q.sendMessage(Toolbox.g()+"Success! "+Toolbox.r()+size + " is quite large... Be careful...");
						return;
					}
					
					if(size > 40) {
						q.sendMessage(Toolbox.g()+"Success! "+Toolbox.r()+size + " seems like a good, but still quite large, size.");
						return;
					}
					
					if(size > 10) {
						q.sendMessage(Toolbox.g()+"Success! "+Toolbox.r()+size + " is a good size.");
						return;
					}
					
					if(size <= 10 && size > 5) {
						q.sendMessage(Toolbox.g()+"Success! "+Toolbox.r()+size + " is a charming size.");
						return;
					}
					
					q.sendMessage(Toolbox.g()+"Success! "+Toolbox.r()+size + " is quite a small size.");
					return;
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("size"), new Argument(ArgumentType.INTEGER, "fontSize")));
		
		allCommands.add(new CECommand(Standards.MAIN_PERMISSION+".material",Standards.COMMAND_CATEGORIES[1],true,false,1,new Action() {
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
								pickedMaterial.put(q.getUniqueId(), mat);
							}else {
								q.sendMessage(Toolbox.g()+"Success! "+Toolbox.r()+Toolbox.capitalizeWords(mat.name()) + " is a weird choice... Are you sure it can be used as a block for the text? Okay, if you say so!");
								pickedMaterial.put(q.getUniqueId(), mat);
							}
							
							if(PlayerInput.playerLink.containsKey(q.getUniqueId())) {	
								PlayerInput input = PlayerInput.playerLink.get(q.getUniqueId());
								if(input.callAnyways) {
									input.call.call("update");
									input.callAnyways = false;
									input.stop();
								}
							}
						}	
					});
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("material")));
		
		allCommands.add(new CECommand(Standards.MAIN_PERMISSION+".font",Standards.COMMAND_CATEGORIES[1],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					String fontFound = null;
					
					String font = arguments[1].toLowerCase().replaceAll("_", " ");
					String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
					for(int i = 0; i < fonts.length; i++) {
						String font1 = fonts[i];
						if(font.equalsIgnoreCase(font1)) {
							fontFound = font1;
							break;
						}
					}
					
					if(fontFound == null) {
						q.sendMessage(Toolbox.c()+"Oops! "+Toolbox.r()+"that font could not be found... "+Toolbox.c()+"Try Again! "+Toolbox.r()+"Only fonts installed on the server computer will work.");
						return;
					}
					
					q.sendMessage("");
					q.sendMessage(Toolbox.g()+"Success! "+Toolbox.r()+fontFound + " seems like a perfect choice!");
					pickedFont.put(q.getUniqueId(), fontFound);
					
					if(PlayerInput.playerLink.containsKey(q.getUniqueId())) {	
						PlayerInput input = PlayerInput.playerLink.get(q.getUniqueId());
						if(input.callAnyways) {
							input.call.call("update");
							input.callAnyways = false;
							input.stop();
						}
					}
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("font"), new Argument(ArgumentType.STRING, "fontName")));
		
		allCommands.add(new CECommand(Standards.MAIN_PERMISSION+".thickness",Standards.COMMAND_CATEGORIES[2],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					int thickness = Integer.parseInt(arguments[1]);
					
					if(thickness > 40) {
						q.sendMessage(Toolbox.b()+"Problem! "+Toolbox.r()+thickness + " is far too large!");
						return;
					}
					
					if(thickness > 20) {
						q.sendMessage(Toolbox.g()+"Success! "+Toolbox.r()+thickness + " is quite large... Be careful...");
						pickedThickness.put(q.getUniqueId(), thickness);
						return;
					}
					
					if(thickness > 10) {
						q.sendMessage(Toolbox.g()+"Success! "+Toolbox.r()+thickness + " is large.");
						pickedThickness.put(q.getUniqueId(), thickness);
						return;
					}
					
					q.sendMessage(Toolbox.g()+"Success! "+Toolbox.r()+thickness + " is a good thickness");
					pickedThickness.put(q.getUniqueId(), thickness);
					
					if(PlayerInput.playerLink.containsKey(q.getUniqueId())) {	
						PlayerInput input = PlayerInput.playerLink.get(q.getUniqueId());
						if(input.callAnyways) {
							input.call.call("update");
							input.callAnyways = false;
							input.stop();
						}
					}
					return;
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("thickness"), new Argument(ArgumentType.INTEGER, "thickness")));
		
		allCommands.add(new CECommand(Standards.MAIN_PERMISSION+".underline",Standards.COMMAND_CATEGORIES[2],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					boolean underline = false;
					if(pickedUnderline.containsKey(q.getUniqueId())) {
						underline = pickedUnderline.get(q.getUniqueId());
					}
					
					underline = !underline;
					
					if(underline) {
						q.sendMessage(Toolbox.g()+"Success! " + Toolbox.r() + "Turned underline on!");
					}else {
						q.sendMessage(Toolbox.g()+"Success! " + Toolbox.r() + "Turned underline off!");
					}
					
					pickedUnderline.put(q.getUniqueId(), underline);
					
					if(PlayerInput.playerLink.containsKey(q.getUniqueId())) {	
						PlayerInput input = PlayerInput.playerLink.get(q.getUniqueId());
						if(input.callAnyways) {
							input.call.call("update");
							input.callAnyways = false;
							input.stop();
						}
					}
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("underline")));
		
		allCommands.add(new CECommand(Standards.MAIN_PERMISSION+".valign",Standards.COMMAND_CATEGORIES[2],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					try {
						Valign valign = Valign.valueOf(arguments[1].toUpperCase());
						q.sendMessage(Toolbox.g()+"Success! " + Toolbox.r() + "Set the vertical align to "+valign.name().toLowerCase()+". Your position will now indicate the "+valign.name().toLowerCase()+" of the text.");
						pickedValign.put(q.getUniqueId(), valign);
					}catch(Exception e) {
						q.sendMessage(Toolbox.b()+"Problem! " + Toolbox.r() + "Pick either top, bottom or center.");
					}
					
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("valign"), new Argument(ArgumentType.STRING, "top, bottom or center")));
		
		allCommands.add(new CECommand(Standards.MAIN_PERMISSION+".valign",Standards.COMMAND_CATEGORIES[2],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					try {
						Halign halign = Halign.valueOf(arguments[1].toUpperCase());
						q.sendMessage(Toolbox.g()+"Success! " + Toolbox.r() + "Set the horizontal align to "+halign.name().toLowerCase()+". Your position will now indicate the "+halign.name().toLowerCase()+" of the text.");
						pickedHalign.put(q.getUniqueId(), halign);
					}catch(Exception e) {
						q.sendMessage(Toolbox.b()+"Problem! " + Toolbox.r() + "Pick either left, right or center.");
					}
					
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("halign"), new Argument(ArgumentType.STRING, "left, right or center")));
		
		allCommands.add(new CECommand(Standards.MAIN_PERMISSION+".build",Standards.COMMAND_CATEGORIES[3],true,false,1,new Action() {
			public void call(CommandSender p, String... arguments) {
				try {
					Player q = (Player)p;
					
					if(!prerequisitesMet(q)) {return;}
					
					q.sendMessage(Toolbox.r()+ "Please write the text you want displayed in chat.");
					new PlayerInput(q, new PlayerStringInput() {
						public void call(String answer) {
							Valign valign = Valign.CENTER;
							if(pickedValign.containsKey(q.getUniqueId())) {
								valign = pickedValign.get(q.getUniqueId());
							}
							
							Halign halign = Halign.CENTER;
							if(pickedHalign.containsKey(q.getUniqueId())) {
								halign = pickedHalign.get(q.getUniqueId());
							}
							
							q.sendMessage(Toolbox.r()+ "Move to a location that will be the " + Toolbox.c() + "vertical " + valign.name().toLowerCase() + Toolbox.r() + " and " + Toolbox.c() + "horizontal " + halign.name().toLowerCase() + Toolbox.r() +" of the text. " + Toolbox.c()+"Look " + Toolbox.r()+"in the direction you want the text to appear in. " + Toolbox.c()+"Left Click " + Toolbox.r()+"when you are ready. A preview will appear, allowing you to confirm.");
							
							final String text = answer;
							new PlayerInputLocation(q, new PlayerLocationInput() {
								@SuppressWarnings("deprecation")
								public void call(Location answer) {
									if(!prerequisitesMet(q)) {return;}
									
									q.sendMessage("");
									q.sendMessage(Toolbox.g()+"Success! " + Toolbox.r() + "Please take a look at the result. Is this what you want? This is a preview, it will disappear if you log out and only you can see it.");
									q.sendMessage(Toolbox.r()+ "If this is correct then write " + Toolbox.c() + "'yes' " + Toolbox.r() +"if it isn't correct write " + Toolbox.c()+"'no'");
									
									q.sendMessage(Toolbox.r()+"Additionally, you can nudge the text by typing in the chat. Like so: 'x,y,z'. For example: 10,0,0. This would make the text move 10 blocks to the positive x.");
									
									BufferedImage image = FontConverter.stringToBufferedImage(text, pickedFont.get(q.getUniqueId()), pickedSize.get(q.getUniqueId()));
									
									Material mat = pickedMaterial.get(q.getUniqueId());
									
									int thickness = 1;
									if(pickedThickness.containsKey(q.getUniqueId())) {
										thickness = pickedThickness.get(q.getUniqueId());
									}
									
									boolean underline = false;
									if(pickedUnderline.containsKey(q.getUniqueId())) {
										underline = pickedUnderline.get(q.getUniqueId());
									}
									
									try {
										Halign halign = Halign.CENTER;
										if(pickedHalign.containsKey(q.getUniqueId())) {
											halign = pickedHalign.get(q.getUniqueId());
										}
										
										Valign valign = Valign.CENTER;
										if(pickedValign.containsKey(q.getUniqueId())) {
											valign = pickedValign.get(q.getUniqueId());
										}
										
										BlockFace face = Toolbox.getCardinalDirection(answer);
										
										//Map<Location, BlockData> prev = new HashMap<>();
										List<Block> blockList = Text.getAllAffectedBlocks(image, answer, face, thickness, underline, halign, valign);
										
										for(Block b : blockList) {
											if(VersionDetector.getVersion() <= 12) {
												q.sendBlockChange(b.getLocation(), mat, (byte) 0);
											}else {
												q.sendBlockChange(b.getLocation(), mat.createBlockData());
											}
										}
										
										affected.put(q.getUniqueId(), blockList);
										
										playerMessages(q, mat, face, text, halign, valign, answer);
									}catch(Exception e) {
										if(!mat.isBlock()) {
											q.sendMessage(Toolbox.b()+"Problem! " + Toolbox.r() + "The material you choose cannot be used as a block. Please set a different material.");
										}else {
											q.sendMessage(Toolbox.b()+"Problem! " + Toolbox.r() + "Something isn't quite right...");
										}
									}
								}
							});
						}
					}, false);
				}catch(Exception e) {
					ErrorHandler.handleError(e);
				}
			}
		}, new Argument("build")));
	}
	
	public void playerMessages(Player q, Material mat, BlockFace face, String text, Halign halign, Valign valign, Location loc) {
		new PlayerInput(q, new PlayerStringInput() {
			public void call(String answer) {
				if(answer.equalsIgnoreCase("update")) {
					if(!affected.containsKey(q.getUniqueId())) {
						return;
					}
					
					Material m = pickedMaterial.get(q.getUniqueId());
					
					List<Block> blocks = affected.get(q.getUniqueId());
					
					for(Block b : blocks) {
						if(VersionDetector.getVersion() <= 12) {
							q.sendBlockChange(b.getLocation(), b.getType(), b.getData());
						}else {
							q.sendBlockChange(b.getLocation(), b.getType().createBlockData());
						}
					}
					
					BufferedImage image = FontConverter.stringToBufferedImage(text, pickedFont.get(q.getUniqueId()), pickedSize.get(q.getUniqueId()));
					
					int thickness = 1;
					if(pickedThickness.containsKey(q.getUniqueId())) {
						thickness = pickedThickness.get(q.getUniqueId());
					}
					
					boolean underline = false;
					if(pickedUnderline.containsKey(q.getUniqueId())) {
						underline = pickedUnderline.get(q.getUniqueId());
					}
					
					List<Block> blockList = Text.getAllAffectedBlocks(image, loc, face, thickness, underline, halign, valign);
					
					for(Block b : blockList) {
						if(VersionDetector.getVersion() <= 12) {
							q.sendBlockChange(b.getLocation(), m, (byte) 0);
						}else {
							q.sendBlockChange(b.getLocation(), m.createBlockData());
						}
					}
					
					affected.put(q.getUniqueId(), blockList);
					
					playerMessages(q, m, face, text, halign, valign, loc);
					return;
				}
				
				if(answer.equalsIgnoreCase("yes")) {
					if(!affected.containsKey(q.getUniqueId())) {
						return;
					}
					
					q.sendMessage(Toolbox.g()+"Success! " + Toolbox.r() + "The blocks are now solid!");
					
					
					List<Block> blocks = affected.get(q.getUniqueId());
					
					for(Block b : blocks) {
						b.setType(mat);
					}
				}else if(answer.equalsIgnoreCase("no")){
					if(!affected.containsKey(q.getUniqueId())) {
						return;
					}
					
					q.sendMessage(Toolbox.g()+"Success! " + Toolbox.r() + "Removed the preview.");
					
					List<Block> blocks = affected.get(q.getUniqueId());
					
					for(Block b : blocks) {
						if(VersionDetector.getVersion() <= 12) {
							q.sendBlockChange(b.getLocation(), b.getType(), b.getData());
						}else {
							q.sendBlockChange(b.getLocation(), b.getType().createBlockData());
						}
					}
				}else {
					int nudgeX = 0;
					int nudgeY = 0;
					int nudgeZ = 0;
					if(answer.contains(",")) {
						answer = answer.replaceAll(" ", "");
						String[] parts = answer.split(",");
						if(parts.length > 0) {
							if(Toolbox.isNumeric(parts[0])) {
								nudgeX = Integer.parseInt(parts[0]);
							}
						}
						
						if(parts.length > 1) {
							if(Toolbox.isNumeric(parts[1])) {
								nudgeY = Integer.parseInt(parts[1]);
							}
						}
						
						if(parts.length > 2) {
							if(Toolbox.isNumeric(parts[2])) {
								nudgeZ = Integer.parseInt(parts[2]);
							}
						}
					}else {
						q.sendMessage(ChatColor.RED+"You are still editing the text! Please write either 'yes' or 'no'. This is to confirm whether the preview should stay.");
					}
					
					if(nudgeX != 0 || nudgeY != 0 || nudgeZ != 0) {
						q.sendMessage(ChatColor.WHITE+"Nudged the text by: ("+nudgeX+","+nudgeY+","+nudgeZ+")");
						
						List<Block> blocks = affected.get(q.getUniqueId());
						List<Block> movedList = new ArrayList<>();
						
						for(Block b : blocks) {
							if(VersionDetector.getVersion() <= 12) {
								q.sendBlockChange(b.getLocation(), b.getType(), b.getData());
							}else {
								q.sendBlockChange(b.getLocation(), b.getType().createBlockData());
							}
							movedList.add(b.getLocation().clone().add(nudgeX, nudgeY, nudgeZ).getBlock());
						}
						
						for(Block b : movedList) {
							if(VersionDetector.getVersion() <= 12) {
								q.sendBlockChange(b.getLocation(), mat, (byte) 0);
							}else {
								q.sendBlockChange(b.getLocation(), mat.createBlockData());
							}
						}
						
						affected.put(q.getUniqueId(), movedList);
					}
					
					playerMessages(q, mat, face, text, halign, valign, loc);
				}
			}
		}, true);
	}
	
	public boolean prerequisitesMet(Player q) {
		if(!pickedFont.containsKey(q.getUniqueId())) {
			q.sendMessage(Toolbox.b()+"Problem! "+Toolbox.r()+" You haven't set a font yet. Use /"+Standards.COMMAND_ALIASES[0]+" font <fontName>, to set a font.");
			return false;
		}
		
		if(!pickedSize.containsKey(q.getUniqueId())) {
			q.sendMessage(Toolbox.b()+"Problem! "+Toolbox.r()+" You haven't set a size yet. Use /"+Standards.COMMAND_ALIASES[0]+" size <fontSize>, to set the font size.");
			return false;
		}
		
		if(!pickedMaterial.containsKey(q.getUniqueId())) {
			q.sendMessage(Toolbox.b()+"Problem! "+Toolbox.r()+" You haven't set a material yet. Use /"+Standards.COMMAND_ALIASES[0]+" material, to set a material.");
			return false;
		}
				
		return true;
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
		p.sendMessage(ChatColor.GRAY+""+page+"/"+getMaxPages());
		p.sendMessage("");
		
		String chapter = null;
		
		int ID = 0;
		for(CECommand command : allCommands) {
			if(command.getPage() != page) {
				continue;
			}
			
			if(command.isOP() && !p.isOp() && !p.hasPermission(command.getPermission()) && !p.hasPermission(Standards.MAIN_PERMISSION+".*")) {
				continue;
			}
			
			if(console && !command.canConsole()) {
				continue;
			}
			
			if(chapter == null || !chapter.equals(command.getChapter())){
				p.sendMessage("");
				p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+command.getChapter());
				chapter = command.getChapter();
			}
			
			ID++;
			
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

