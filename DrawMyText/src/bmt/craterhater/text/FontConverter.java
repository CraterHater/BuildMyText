package bmt.craterhater.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class FontConverter {

	public static BufferedImage stringToBufferedImage(String s, String font, int fontSize) {
	    //First, we have to calculate the string's width and height

		if(fontSize < 1) {fontSize = 1;}
		
	    BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
	    Graphics g = img.getGraphics();

	    //Set the font to be used when drawing the string
	    Font f = new Font(font, Font.PLAIN, fontSize);
	    g.setFont(f);

	    //Get the string visual bounds
	    FontRenderContext frc = g.getFontMetrics().getFontRenderContext();
	    Rectangle2D rect = f.getStringBounds(s, frc);
	    //Release resources
	    g.dispose();

	    //Then, we have to draw the string on the final image

	    //Create a new image where to print the character
	    img = new BufferedImage((int) Math.ceil(rect.getWidth()), (int) Math.ceil(rect.getHeight()), BufferedImage.TYPE_4BYTE_ABGR);
	    g = img.getGraphics();
	    g.setColor(Color.red); //Otherwise the text would be white
	    g.setFont(f);

	    //Calculate x and y for that string
	    FontMetrics fm = g.getFontMetrics();
	    int x = 0;
	    int y = fm.getAscent(); //getAscent() = baseline
	    g.drawString(s, x, y);

	    //Release resources
	    g.dispose();

	    //Return the image
	    return img;
	}
}
