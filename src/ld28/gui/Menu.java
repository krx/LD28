package ld28.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;

public class Menu extends GUIObject {
	private String title;
	private String[] text;
	private boolean selectable;
	private float titleSize;
	private int sIndex;
	
	public Menu(String title, int windowWidth, int windowHeight, boolean selectable, float titleSize, String... text) {
		super(windowWidth, windowHeight);
		this.title = title;
		this.titleSize = titleSize;
		this.text = text;
		this.selectable = selectable;
		resetSelector();
	}
	
	public boolean isSelectable() {
		return selectable;
	}
	
	public void resetSelector() {
		sIndex = 0;
		if(text[sIndex] == "") {
			selectDown();
		}
	}
	
	public void selectUp() {
		sIndex--;
		if(sIndex < 0) {
			sIndex = text.length - 1;
		}
		if(text[sIndex] == "") {
			selectUp();
		}
	}
	
	public void selectDown() {
		sIndex++;
		if(sIndex >= text.length) {
			sIndex = 0;
		}
		if(text[sIndex] == "") {
			selectDown();
		}
	}
	
	public int getSelectIndex() {
		int blank = 0;
		for(String s : text) {
			if (s == "") {
				blank++;
			}
		}
		return sIndex - blank;
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
		g.setFont(g.getFont().deriveFont(titleSize));
		FontMetrics fm = g.getFontMetrics();
		int padding = 20;
		
		g.setColor(new Color(0, 100, 0, 150));
		g.fillRect(0, 0, windowWidth, windowHeight);
		
		Color selected = new Color(0, 255, 0);
		Color unSelected = new Color(0, 32, 0);
		
		int dy = padding;
		
		g.setColor(selected);
		g.drawString(title, windowWidth / 2 - ((int) fm.stringWidth(title) / 2), dy += fm.getHeight());
		g.setFont(g.getFont().deriveFont(fontSize));
		fm = g.getFontMetrics();
		for(int i = 0; i < text.length; i++) {
			if(selectable) {
				g.setColor(i == sIndex ? selected : unSelected);
			}
			g.drawString(text[i], windowWidth / 2 - (fm.stringWidth(text[i])) / 2, dy += fm.getHeight());
		}
		
		g.setFont(oldFont);
	}
}