package ld28.gui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.io.IOException;

import jgame.Renderable;
import jgame.util.FileIOHelper;

public abstract class GUIObject implements Renderable {
	protected Font font;
	protected float fontSize;
	protected int windowWidth;
	protected int windowHeight;
	
	public GUIObject(int windowWidth, int windowHeight) {
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
	}
	
	public abstract void draw(Graphics g);
	
	public void draw(Graphics g, int x, int y) {}
	
	public void setFont(Font f) {
		this.font = f;
	}
	
	public void setFont(String path) {
		try {
			Font.createFont(Font.TRUETYPE_FONT, FileIOHelper.loadResource(path));
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setFontSize(float size) {
		this.fontSize = size;
	}
}