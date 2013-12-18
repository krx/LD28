package ld28.map;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import jgame.Animation;
import jgame.Renderable;
import jgame.math.Vector2;
import ld28.LD28;
import ld28.player.Player;

public class Map implements Renderable {
	private Tile[][] data;
	private int width;
	private int height;
	private int tileWidth;
	private int tileHeight;
	
	public Map(Tile[][] data, int width, int height, int tileWidth, int tileHeight) {
		this.data = data;
		this.width = width;
		this.height = height;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}
	
	public void draw(Graphics g, int dx, int dy) {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				if(data[y][x].getBounds().intersects(LD28.cam.viewport)) {
					data[y][x].draw(g, dx, dy);
				}
			}
		}
	}
	
	public Tile[][] getData() {
		return data;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getTileWidth() {
		return tileWidth;
	}
	
	public int getTileHeight() {
		return tileHeight;
	}
	
	public int getPxWidth() {
		return width * tileWidth;
	}
	
	public int getPxHeight() {
		return height * tileHeight;
	}
	
	public boolean collide(Rectangle bounds) {
		for(Tile[] t : data) {
			for(Tile tt : t) {
				if(bounds.intersects(tt.getBounds()) && tt.getGid() != 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean collide(Vector2 pos, Vector2 size) {
		return collide(new Rectangle((int) pos.x, (int) pos.y, (int) size.x, (int) size.y));
	}
	
	public Rectangle getCollisionBounds(Rectangle bounds) {
		for(Tile[] t : data) {
			for(Tile tt : t) {
				if(bounds.intersects(tt.getBounds()) && tt.getGid() != 0) {
					return tt.getBounds().intersection(bounds);
				}
			}
		}
		return null;
	}
	
	public Rectangle getCollisionBounds(Vector2 pos, Vector2 size) {
		return getCollisionBounds(new Rectangle((int) pos.x, (int) pos.y, (int) size.x, (int) size.y));
	}
	
	public void createExit(Player p) {
		// List of possible exits to choose from
		ArrayList<Tile> candidates = new ArrayList<Tile>();
		// Check left side wall for possible exits
		for(int i = data.length - 1; i >= 0; i--) {
			if(data[i][1].getGid() != 0) { // Tile is part of a floor
				int added = 0;
				int j = i;
				// Add next 3 wall tiles to possible exits
				while(added < 3 && j >= 0) {
					if(data[j][1].getGid() == 0) {
						candidates.add(data[j][0]);
						added++;
					}
					j--;
				}
				i = j + 1;
			}
		}
		// Check right side wall for possible exits
		for(int i = data.length - 1; i >= 0; i--) {
			if(data[i][width - 2].getGid() != 0) { // Tile is part of a floor
				int added = 0;
				int j = i;
				// Add next 3 wall tiles to possible exits
				while(added < 3 && j >= 0) {
					if(data[j][width - 2].getGid() == 0) {
						candidates.add(data[j][width - 1]);
						added++;
					}
					j--;
				}
				i = j + 1;
			}
		}
		// Exit can't be too close to player
		for(int i = 0; i < candidates.size(); i++) {
			Tile t = candidates.get(i);
			Vector2 tp = new Vector2(t.getBounds().x, t.getBounds().y);
			double dist = tp.dist(p.getPosition());
			if(dist < 500.0) {
				candidates.remove(i--);
			}
		}
		// Replace exit wall tile with blank tile
		Tile exit = candidates.get((int) (Math.random() * candidates.size()));
		int ty = exit.getY(), tx = exit.getX();
		data[ty][tx] = new Tile(new Animation(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)), tx, ty, tileWidth, tileHeight, 0);
	}
}