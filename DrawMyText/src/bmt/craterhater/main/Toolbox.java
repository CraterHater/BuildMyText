package bmt.craterhater.main;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class Toolbox {

	public static boolean isNumeric(String s) {
		try {
			Double.parseDouble(s);
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	
	public static boolean worldExists(String world) {
		for(World w : Bukkit.getWorlds()) {
			if(w.getName().equals(world)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static double diff(double one, double two) {
		double three = one-two;
		if(three < 0) {
			three *= -1;
		}
		return three;
	}
	
	public static Float getRandom(double rangeMin, double rangeMax) {
		Random r = new Random();
		return (float)(rangeMin + (rangeMax - rangeMin) * r.nextDouble());
	}
	
	public static ItemStack createCustomSkull(int amount, String displayName, List<String> lore, String texture) {
        texture = "http://textures.minecraft.net/texture/" + texture;
       
        Material head = VersionDetector.getMaterial("PLAYER_HEAD", "SKULL_ITEM");
        
        ItemStack skull = new ItemStack(head, amount);
        if (texture.isEmpty()) {
            return skull;
        }
       
        SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();
        skullMeta.setDisplayName(displayName);
        skullMeta.setLore(lore);
       
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", texture).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        }
        catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        assert profileField != null;
        profileField.setAccessible(true);
        try {
            profileField.set(skullMeta, profile);
        }
        catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }
	
	public static String getEnumeration(List<String> list) {
		try {
			String header = "";
			
			if(!list.isEmpty()) {
				for(int i = 0; i < list.size(); i++) {
					if(list.size() == (i+1)) {header += ChatColor.translateAlternateColorCodes('&', " &7and ");}
					if(list.size() != (i+1)) {header += ChatColor.translateAlternateColorCodes('&', "&7, ");}
					
					header += ChatColor.translateAlternateColorCodes('&', "&f")+capitalizeWords(list.get(i).toLowerCase());
				}
				
				if(list.size() > 1) {
					header = header.replaceFirst(ChatColor.translateAlternateColorCodes('&', "&7, "), "");
				}else {
					header = header.replaceFirst(ChatColor.translateAlternateColorCodes('&', " &7and "), "");
				}
				
				return header;
			}
			
			return null;
		}catch(Exception e) {
			ErrorHandler.handleError(e);
			return null;
		}
	}
	
	public static String capitalizeWords(String text) {
		try {
			text = text.toLowerCase();
		    StringBuilder sb = new StringBuilder();
		    if(text.length()>0){
		        sb.append(Character.toUpperCase(text.charAt(0)));
		    }
		    for (int i=1; i<text.length(); i++){
		        String chPrev = String.valueOf(text.charAt(i-1));
		        String ch = String.valueOf(text.charAt(i));
	
		        if(Objects.equals(chPrev, " ")){
		            sb.append(ch.toUpperCase());
		        }else {
		            sb.append(ch);
		        }
	
		    }
	
		    return sb.toString();
		}catch(Exception e) {
			ErrorHandler.handleError(e);
			return text;
		}
	}
	
	public static List<Location> getHollowCube(Location corner1, Location corner2, double particleDistance) {
        List<Location> result = new ArrayList<Location>();
        World world = corner1.getWorld();
        int startX = (int)Math.min(corner1.getX(), corner2.getX());
        int startY = (int)Math.min(corner1.getY(), corner2.getY());
        int startZ = (int)Math.min(corner1.getZ(), corner2.getZ());
        int endX = (int)Math.max(corner1.getX(), corner2.getX());
        int endY = (int)Math.max(corner1.getY(), corner2.getY());
        int endZ = (int)Math.max(corner1.getZ(), corner2.getZ());
     
        for (double x = startX; x <= endX + 1; x++) {
            for (double y = startY; y <= endY + 1; y++) {
                for (double z = startZ; z <= endZ + 1; z++) {
                        boolean edge = false;
                        if (((int) x == startX || (int) x == endX + 1) &&
                                ((int) y == startY || (int) y == endY + 1)) edge = true;
                        if (((int) z == startZ || (int) z == endZ + 1) &&
                                ((int) y == startY || (int) y == endY + 1)) edge = true;
                        if (((int) x == startX || (int) x == endX + 1) &&
                                ((int) z == startZ || (int) z == endZ + 1)) edge = true;

                        if (edge) {
                           result.add(new Location(world, x, y, z));
                        }
                }
                }
            }
     
        return result;
    }
	
	public static BlockFace getCardinalDirection(Location loc) {
		double rotation = (loc.getYaw() - 90.0F) % 360.0F;

		if (rotation < 0.0D) {
		rotation += 360.0D;
		}
		if ((0.0D <= rotation) && (rotation < 45.0D))
		return BlockFace.WEST;
		if ((45.0D <= rotation) && (rotation < 135.0D))
		return BlockFace.NORTH;
		if ((135.0D <= rotation) && (rotation < 225.0D))
		return BlockFace.EAST;
		if ((225.0D <= rotation) && (rotation < 315.0D))
		return BlockFace.SOUTH;
		if ((315.0D <= rotation) && (rotation < 360.0D)) {
		return BlockFace.WEST;
		}
		return null;
	}
	
	public static String g() {
		return ChatColor.GREEN+""+ChatColor.BOLD+"";
	}
	
	public static String c() {
		return ChatColor.LIGHT_PURPLE+""+ChatColor.BOLD+"";
	}
	
	public static String b() {
		return ChatColor.DARK_RED+""+ChatColor.BOLD+"";
	}
	
	public static String r() {
		return ChatColor.RESET+""+ChatColor.WHITE+"";
	}
}
