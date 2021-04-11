package bmt.craterhater.text;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import bmt.craterhater.playerinput.Halign;
import bmt.craterhater.playerinput.Valign;

public class Text {

	public static List<Block> getAllAffectedBlocks(BufferedImage image, Location loc, BlockFace direction, int thickness, boolean underline, Halign halign, Valign valign){
		List<Block> blocks = new ArrayList<>();
		
		if(thickness < 1) {thickness = 1;}
		
		if(halign == Halign.LEFT) {
			if(direction == BlockFace.EAST) {
				loc.subtract(image.getWidth(),0,0);
			}else if(direction == BlockFace.WEST) {
				loc.add(image.getWidth(),0,0);
			}else if(direction == BlockFace.SOUTH) {
				loc.subtract(0,0,image.getWidth());
			}else if(direction == BlockFace.NORTH) {
				loc.add(0,0,image.getWidth());
			}
		}else if(halign == Halign.RIGHT) {
			if(direction == BlockFace.EAST) {
				loc.add(image.getWidth(),0,0);
			}else if(direction == BlockFace.WEST) {
				loc.subtract(image.getWidth(),0,0);
			}else if(direction == BlockFace.SOUTH) {
				loc.add(0,0,image.getWidth());
			}else if(direction == BlockFace.NORTH) {
				loc.subtract(0,0,image.getWidth());
			}
		}
		
		if(valign == Valign.TOP) {
			loc.subtract(0,image.getHeight()/2,0);
		}else if(valign == Valign.BOTTOM) {
			loc.add(0,image.getHeight()/2,0);
		}
		
		int lowest_y = -1;
		
		for (int y = 0; y < image.getHeight(); y++) {
		    for (int x = 0; x < image.getWidth(); x++) {
		        int  clr   = image.getRGB(x, y); 
		        int  red   = (clr & 0x00ff0000) >> 16;
		        int  green = (clr & 0x0000ff00) >> 8;
		        int  blue  =  clr & 0x000000ff;
		        
		        if(red != 0 || green != 0 || blue != 0) {
		        	if(direction == BlockFace.EAST) {
		        		for(int i = 0; i < thickness; i++) {
		        			int yval = (image.getHeight()/2)-y;
		        			Block block = loc.getBlock().getLocation().clone().add((image.getWidth()/2)-x, yval, i).getBlock();
			        		blocks.add(block);
			        		
			        		if(lowest_y == -1 || lowest_y > yval){
			        			lowest_y = yval;
			        		}
		        		}
		        	}else if(direction == BlockFace.WEST) {
		        		for(int i = 0; i < thickness; i++) {
		        			int yval = (image.getHeight()/2)-y;
			        		Block block = loc.getBlock().getLocation().clone().add((-(image.getWidth()/2))+x, yval, -i).getBlock();
			        		blocks.add(block);
			        		
			        		if(lowest_y == -1 || lowest_y > yval){
			        			lowest_y = yval;
			        		}
		        		}
		        	}else if(direction == BlockFace.SOUTH) {
		        		for(int i = 0; i < thickness; i++) {
		        			int yval = (image.getHeight()/2)-y;
		        			Block block = loc.getBlock().getLocation().clone().add(i, yval, (image.getWidth()/2)-x).getBlock();
		        			blocks.add(block);
		        			
		        			if(lowest_y == -1 || lowest_y > yval){
			        			lowest_y = yval;
			        		}
		        		}
		        	}else if(direction == BlockFace.NORTH) {
		        		for(int i = 0; i < thickness; i++) {
		        			int yval = (image.getHeight()/2)-y;
			        		Block block = loc.getBlock().getLocation().clone().add(-i, yval, (-(image.getWidth()/2))+x).getBlock();
			        		blocks.add(block);
			        		
			        		if(lowest_y == -1 || lowest_y > yval){
			        			lowest_y = yval;
			        		}
		        		}
		        	}
		        }	
		    }
		}
		
		if(underline) {
			for (int x = 0; x < image.getWidth(); x++) {
				for(int i = 0; i < thickness; i++) {
					if(direction == BlockFace.EAST) {
						Block block = loc.getBlock().getLocation().clone().add((image.getWidth()/2)-x, lowest_y-2, i).getBlock();
			    		blocks.add(block);
					}else if(direction == BlockFace.WEST) {
						Block block = loc.getBlock().getLocation().clone().add((-(image.getWidth()/2))+x, lowest_y-2, -i).getBlock();
			    		blocks.add(block);
					}else if(direction == BlockFace.SOUTH) {
						Block block = loc.getBlock().getLocation().clone().add(i, lowest_y-2, (image.getWidth()/2)-x).getBlock();
			    		blocks.add(block);
					}else if(direction == BlockFace.NORTH) {
						Block block = loc.getBlock().getLocation().clone().add(-i, lowest_y-2, (-(image.getWidth()/2))+x).getBlock();
			    		blocks.add(block);
					}
				}
			}
		}
		
		return blocks;
	}
}
