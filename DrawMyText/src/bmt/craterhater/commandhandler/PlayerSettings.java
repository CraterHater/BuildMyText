package bmt.craterhater.commandhandler;

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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import bmt.craterhater.datahandler.DataHandler;
import bmt.craterhater.datahandler.DataPath;
import bmt.craterhater.main.Main;
import bmt.craterhater.main.Standards;
import bmt.craterhater.main.Toolbox;
import bmt.craterhater.main.VersionDetector;
import bmt.craterhater.playerinput.PlayerInput;
import bmt.craterhater.playerinput.PlayerStringInput;
import bmt.craterhater.text.FontConverter;
import bmt.craterhater.text.Halign;
import bmt.craterhater.text.Text;
import bmt.craterhater.text.Valign;

public class PlayerSettings {
	
	/*
	 * Class handles real-time text editing as well as storing configuration settings. 
	 */
	
	private static Map<UUID, PlayerSettings> playerSettings = new HashMap<>();
	
	//Static function to get the playersettings object for a given player or make a new one if none yet exists.
	public static PlayerSettings getPlayerSettings(Player p) {
		if(playerSettings.containsKey(p.getUniqueId())) {
			return playerSettings.get(p.getUniqueId());
		}
		
		PlayerSettings playerSetting = new PlayerSettings();
		playerSettings.put(p.getUniqueId(), playerSetting);
		
		return playerSetting;
	}
	
	//Checks whether the player has configured all essentials.
	public boolean prerequisitesMet(Player q) {
		String fontFound = FontConverter.getCorrespondingFont(font);
		
		if(fontFound == null) {
			q.sendMessage(Toolbox.b()+"Problem! "+Toolbox.r()+" The font you have selected cannot be found. Use /"+Standards.COMMAND_ALIASES[0]+" font <fontName>, to set a new font.");
			return false;
		}
		
		if(text == null) {
			q.sendMessage(Toolbox.b()+"Problem! "+Toolbox.r()+" You haven't set the text yet. Use /"+Standards.COMMAND_ALIASES[0]+" text, to set the text.");
			return false;
		}
				
		return true;
	}
	
	//Updates the text the player is currently editing. 
	public void update(Player q) {
		if(!editing) {return;}
		
		revertAffectedBlocks(q);
		
		new Thread(()->{
			BufferedImage image = FontConverter.stringToBufferedImage(text, font, size, fontType);
			List<Block> blockList = Text.getAllAffectedBlocks(image, location, blockFace, thickness, underline, halign, valign);
			affectedBlocks = blockList;
			changeAffectedBlocks(q);
			storeTextData();
		}).start();
	}
	
	//Puts a player into edit mode.
	public void startEditing(Player q, Location location, boolean delete) {
		this.location = location;
		this.blockFace = Toolbox.getCardinalDirection(location);
		this.editing = true;
		
		q.sendTitle("", "Do not forget to confirm by typing 'yes' once done!", 10, 50, 10);
		q.sendMessage("");
		q.sendMessage(Toolbox.g()+"Success! " + Toolbox.r() + "Please take a look at the result. Is this what you want? This is a preview, it will disappear if you log out and only you can see it.");
		q.sendMessage(Toolbox.r()+ "If this is correct then write " + Toolbox.c() + "'yes' " + Toolbox.r() +"if it isn't correct write " + Toolbox.c()+"'no'");
		q.sendMessage(Toolbox.r()+"Additionally, you can nudge the text by typing in the chat. Like so: 'x,y,z'. For example: 10,0,0. This would make the text move 10 blocks to the positive x.");
		
		new Thread(()->{
			BufferedImage image = FontConverter.stringToBufferedImage(text, font, size, fontType);
			List<Block> blockList = Text.getAllAffectedBlocks(image, location, blockFace, thickness, underline, halign, valign);
			
			new BukkitRunnable() {
		        @Override
		        public void run() {
		        	if(delete) {
						for(Block b : blockList) {
							b.setType(Material.AIR);
						}
					}
		        }
		    }.runTask(Main.main);
			
			affectedBlocks = blockList;
			
			Main.main.getServer().getScheduler().scheduleSyncDelayedTask(Main.main, new Runnable() {
				public void run() {
					changeAffectedBlocks(q);
				 }
			}, 10);
			
		}).start();

		storeTextData();
		allowPlayerToEditText(q);
	}
	
	//Stops editing. Removes all blocks.
	public void stopEditing(Player q) {
		if(!editing) {return;}
		editing = false;
		DataPath dataPath = new DataPath("text");
		FileConfiguration fc = DataHandler.getFile(dataPath);
		String ID = location.getWorld().getName()+","+location.getBlockX()+","+location.getBlockY()+","+location.getBlockZ()+","+(int)location.getYaw();
		fc.set("Text."+ID, null);
		DataHandler.saveFile(fc, dataPath);
		q.sendMessage(Toolbox.g()+"Success! " + Toolbox.r() + "Removed the preview.");
		revertAffectedBlocks(q);
		
		if(PlayerInput.playerLink.containsKey(q.getUniqueId())) {	
			PlayerInput input = PlayerInput.playerLink.get(q.getUniqueId());
			input.stop();
		}
	}
	
	//Stops editing. Sets blocks.
	public void confirmChances(Player q) {
		if(!editing) {return;}
		editing = false;
		q.sendMessage(Toolbox.g()+"Success! " + Toolbox.r() + "The blocks are now solid!");
		for(Block b : affectedBlocks) {
			b.setType(material);
		}
		
		if(PlayerInput.playerLink.containsKey(q.getUniqueId())) {	
			PlayerInput input = PlayerInput.playerLink.get(q.getUniqueId());
			input.stop();
		}
	}
	
	//Function to accept player input. 
	private void allowPlayerToEditText(Player q) {
		new PlayerInput(q, new PlayerStringInput() {
			public void call(String answer) {
				if(answer.equalsIgnoreCase("yes")) {
					confirmChances(q);
					return;
				}else if(answer.equalsIgnoreCase("no")){
					stopEditing(q);
					return;
				}
				
				nudgeText(q, answer);
				update(q);
				allowPlayerToEditText(q);
				return;
			}
		}, true);
	}
	
	//Checks whether text contains a nudge command and if so moves the text.
	private void nudgeText(Player q, String text) {
		int nudgeX=0,nudgeY=0,nudgeZ=0;
		
		text = text.replaceAll(" ", "");
		String[] parts = text.split(",");
		
		try {
			nudgeX = Integer.parseInt(parts[0]);
			nudgeY = Integer.parseInt(parts[1]);
			nudgeZ = Integer.parseInt(parts[2]);
		}catch(Exception e) {}
		
		if(nudgeX+nudgeY+nudgeZ==0) {return;}
		
		q.sendMessage(ChatColor.WHITE+"Nudged the text by: ("+nudgeX+","+nudgeY+","+nudgeZ+")");
		
		DataPath dataPath = new DataPath("text");
		FileConfiguration fc = DataHandler.getFile(dataPath);
		String ID = location.getWorld().getName()+","+location.getBlockX()+","+location.getBlockY()+","+location.getBlockZ()+","+(int)location.getYaw();
		fc.set("Text."+ID, null);
		DataHandler.saveFile(fc, dataPath);
		
		location.add(nudgeX, nudgeY, nudgeZ);

		List<Block> movedList = new ArrayList<>();
		revertAffectedBlocks(q);
		for(Block b : affectedBlocks) {
			movedList.add(b.getLocation().clone().add(nudgeX, nudgeY, nudgeZ).getBlock());
		}
		affectedBlocks = movedList;
		changeAffectedBlocks(q);
	}
	
	//Function 1 for changing blocks.
	@SuppressWarnings("deprecation")
	private void changeAffectedBlocks(Player p) {
		if(VersionDetector.getVersion() <= 12) {
			for(Block b : affectedBlocks) {
				p.sendBlockChange(b.getLocation(), material, (byte) 0);
			}
		}else {
			for(Block b : affectedBlocks) {
				p.sendBlockChange(b.getLocation(), material.createBlockData());
			}
		}
	}
	
	//Function 2 for changing blocks.
	@SuppressWarnings("deprecation")
	private void revertAffectedBlocks(Player p) {
		if(VersionDetector.getVersion() <= 12) {
			for(Block b : affectedBlocks) {
				p.sendBlockChange(b.getLocation(), b.getType(), b.getData());
			}
		}else {
			for(Block b : affectedBlocks) {
				p.sendBlockChange(b.getLocation(), b.getType().createBlockData());
			}
		}
	}
	
	//Stores data to a file so player can edit later.
	private void storeTextData() {
		DataPath dataPath = new DataPath("text");
		FileConfiguration fc = DataHandler.getFile(dataPath);
		String ID = location.getWorld().getName()+","+location.getBlockX()+","+location.getBlockY()+","+location.getBlockZ()+","+(int)location.getYaw();
		fc.set("Text."+ID+".text", text);
		fc.set("Text."+ID+".font", font);
		fc.set("Text."+ID+".material", material.name());
		fc.set("Text."+ID+".fontType", fontType);
		fc.set("Text."+ID+".size", size);
		fc.set("Text."+ID+".underline", underline+"");
		fc.set("Text."+ID+".thickness", thickness);
		fc.set("Text."+ID+".valign", valign.name());
		fc.set("Text."+ID+".halign", halign.name());
		DataHandler.saveFile(fc, dataPath);
	}
	
	//Settings and defaults.
	private String text = null;
	private String font = "Arial";
	private int size = 15;
	private Material material = Material.STONE;
	private int thickness = 1;
	private int fontType = 0;
	private boolean underline = false;
	private Halign halign = Halign.CENTER;
	private Valign valign = Valign.CENTER;
	private List<Block> affectedBlocks = new ArrayList<>();
	private Location location;
	private BlockFace blockFace;
	private boolean editing = false;
	
	//Whole bunch of getters and setters.
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getFont() {
		return font;
	}
	public void setFont(String font) {
		this.font = font;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}
	public int getThickness() {
		return thickness;
	}
	public void setThickness(int thickness) {
		this.thickness = thickness;
	}
	public int getFontType() {
		return fontType;
	}
	public void setFontType(int fontType) {
		this.fontType = fontType;
	}
	public boolean isUnderline() {
		return underline;
	}
	public void setUnderline(boolean underline) {
		this.underline = underline;
	}
	public Halign getHalign() {
		return halign;
	}
	public void setHalign(Halign halign) {
		this.halign = halign;
	}
	public Valign getValign() {
		return valign;
	}
	public void setValign(Valign valign) {
		this.valign = valign;
	}
	public List<Block> getAffectedBlocks() {
		return affectedBlocks;
	}
	public void setAffectedBlocks(List<Block> affectedBlocks) {
		this.affectedBlocks = affectedBlocks;
	}
}
