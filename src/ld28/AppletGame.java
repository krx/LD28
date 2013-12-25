package ld28;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JApplet;

public class AppletGame extends JApplet {
	private BufferedImage img;
	private BufferedImage onScreen;
	private LD28 game;
	
	public void init() {
		game = new LD28(true);
		setSize(game.getPreferredSize());
		
		add(game);
	}
	
	public void paint(Graphics g) {
		g.drawImage(onScreen, 0, 0, null);
	}
	
	public void update(Graphics g) {
		img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		img.getGraphics().setColor(Color.WHITE);
		img.getGraphics().fillRect(0, 0, img.getWidth(), img.getHeight());
		game.paint(img.getGraphics());
		g.drawImage(img, 0, 0, null);
		onScreen = img;
	}
}