package ld28.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

public class PopupMenu extends GUIObject {
	private String title;
	private String[] options;
	private int sIndex;
	
	public PopupMenu(String title, int windowWidth, int windowHeight,  String... options) {
		super(windowWidth, windowHeight);
		this.title = title;
		this.options = options;
		sIndex = 0;
	}
	
	public void selectUp() {
		sIndex--;
		if(sIndex < 0) {
			sIndex = options.length - 1;
		}
	}
	
	public void selectDown() {
		sIndex++;
		if(sIndex >= options.length) {
			sIndex = 0;
		}
	}
	
	public int getSelectIndex() {
		return sIndex;
	}
	
	public void setSelectIndex(int sIndex) {
		this.sIndex = sIndex;
	}
	
	public void draw(Graphics gg) {
		Graphics2D g = (Graphics2D) gg;
		Font oldFont = g.getFont();
		if(font != null) {
			g.setFont(font);
		}
		g.setFont(g.getFont().deriveFont(fontSize));
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(8));
		FontMetrics fm = g.getFontMetrics();
		int padding = 20;
		int alpha = 150;
		int radius = 75;
		Rectangle menu = new Rectangle();
		menu.height = fm.getHeight() * (options.length + 1) + (2 * padding);
		menu.width = fm.stringWidth(title) + (2 * padding);
		for(String o : options) {
			menu.width = Math.max(menu.width, fm.stringWidth(o) + (2 * padding));
		}
		menu.setLocation((windowWidth - menu.width) / 2, (windowHeight - menu.height) / 2);
		
		g.setColor(new Color(0, 40, 0, alpha));
		g.drawRoundRect(menu.x, menu.y, menu.width, menu.height, radius, radius);
		g.setColor(new Color(0, 100, 0, alpha));
		g.fillRoundRect(menu.x, menu.y, menu.width, menu.height, radius, radius);
		
		Color selected = new Color(0,255,0);
		Color unSelected = new Color(0, 32, 0);
		
		int dx = windowWidth / 2;
		int dy = menu.y;
		g.setColor(selected);
		g.drawString(title, dx - fm.getStringBounds(title, g).getBounds().width / 2, dy += fm.getHeight());
		for(int i = 0; i < options.length; i++) {
			g.setColor(i == sIndex ? selected : unSelected);
			g.drawString(options[i], dx - fm.getStringBounds(options[i], g).getBounds().width / 2, dy += fm.getHeight());
		}
		
		g.setStroke(oldStroke);
		g.setFont(oldFont);
	}
}