package bmt.craterhater.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

public class FontConverter {

	public static BufferedImage stringToBufferedImage(String text, String f, int fontSize, int fo){
		String[] textrows = text.split("<>");
		
		if(!text.contains("<>")) {textrows = new String[]{text};}

		BufferedImage helperImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = helperImg.createGraphics();
		Font font = new Font(f, fo, fontSize);
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();
		String longestText = "";
		for(String row: textrows){
		   if(row.length()>longestText.length()){
		      longestText = row;
		   }
		}
		int width = fm.stringWidth(longestText);
		int height = fm.getHeight()*textrows.length;
		g2d.dispose();
		
		
		BufferedImage finalImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g2d = finalImg.createGraphics();
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, width, height);
		g2d.setFont(font);
		fm = g2d.getFontMetrics();
		g2d.setColor(Color.red);
		int y = fm.getAscent();
		  
		for(String row: textrows){
		   g2d.drawString(row, 0, y);
		   y += fm.getHeight();
		}
		g2d.dispose();
		return finalImg;
	}
	
	public static String getCorrespondingFont(String f) {
		String font = f.toLowerCase().replaceAll("_", " ");
		
		String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for(int i = 0; i < fonts.length; i++) {
			String font1 = fonts[i];
			if(font.equalsIgnoreCase(font1)) {
				return font1;
			}
		}
		
		return null;
	}
}
