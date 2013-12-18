package ld28.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.io.IOException;
import java.util.List;

import jgame.Renderable;
import jgame.util.FileIOHelper;
import jgame.util.StringUtils;

public class Dialog extends GUIObject {
	private String[] messages;
	private int mIndex;
	private int cIndex;
	
	public Dialog(int windowWidth, int windowHeight, String... messages) {
		super(windowWidth, windowHeight);
		this.messages = messages;
		mIndex = 0;
		cIndex = 0;
	}
	
	public boolean isDoneDialog() {
		return mIndex >= messages.length;
	}
	
	public void iterateMessages() {
		if(cIndex == messages[mIndex].length()) {
			mIndex++;
			cIndex = 0;
		} else {
			cIndex = messages[mIndex].length();
		}
	}
	
	public void iterateCharacters() {
		cIndex = Math.min(cIndex + 1, messages[mIndex].length());
	}
	
	public void draw(Graphics gg) {
		Graphics2D g = (Graphics2D) gg;
		Font oldFont = g.getFont();
		if(font != null) {
			g.setFont(font);
		}
		g.setFont(g.getFont().deriveFont(fontSize));
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(5));
		FontMetrics fm = g.getFontMetrics();
		int padding = 10;
		int alpha = 150;
		int radius = 50;
		List<String> lines = StringUtils.wrap(messages[mIndex], fm, windowWidth - (4 * padding));
		int height = fm.getHeight() * lines.size();

		Rectangle dialog = new Rectangle(padding, windowHeight - (3 * padding) - height, windowWidth - (2 * padding), height + (2 * padding));
		
		g.setColor(new Color(0, 40, 0, alpha));
		g.drawRoundRect(dialog.x, dialog.y, dialog.width, dialog.height, radius, radius);
		g.setColor(new Color(0, 100, 0, alpha));
		g.fillRoundRect(dialog.x, dialog.y, dialog.width, dialog.height, radius, radius);
		
		g.setColor(new Color(0, 255, 0));
		
		drawString(g, lines, 2 * padding, windowHeight - (4 * padding) - height);
		
		g.setFont(g.getFont().deriveFont(14f));
		g.drawString("Space...", dialog.x + dialog.width - g.getFontMetrics().stringWidth("Space...") - padding, dialog.y + dialog.height - padding);
		
		g.setStroke(oldStroke);
		g.setFont(oldFont);
	}
	
	private void drawString(Graphics g, List<String> lines, int x, int y) {
		int charDraw = cIndex;
		
		for(int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if(charDraw > line.length()) {
				charDraw -= line.length();
			} else {
				lines.set(i, line.substring(0, charDraw));
				charDraw = 0;
			}
		}
		
		for(String line : lines) {
			g.drawString(line, x, y += g.getFontMetrics().getHeight());
		}
	}
}