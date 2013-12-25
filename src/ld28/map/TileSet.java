package ld28.map;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import jgame.SpriteSheet;
import jgame.util.FileIOHelper;

public class TileSet {
	private String name;
	private SpriteSheet tiles;
	private int gidBegin;
	private int gidEnd;
	private int tileWidth;
	private int tileHeight;
	
	public TileSet(String name, String path, int gidBegin, int tileWidth, int tileHeight) {
		BufferedImage original = FileIOHelper.loadImage(path);
		tiles = new SpriteSheet(original, original.getHeight() / tileHeight, original.getWidth() / tileWidth, tileWidth, tileHeight, 0, 0, 0, 0);
		this.name = name;
		this.gidBegin = gidBegin;
		gidEnd = gidBegin + tiles.getAllSubImages().size() - 1;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}
	
	public SpriteSheet getTiles() {
		return tiles;
	}
	
	public String getName() {
		return name;
	}
	
	public int getTileWidth() {
		return tileWidth;
	}
	
	public int getTileHeight() {
		return tileHeight;
	}
	
	public int getGidBegin() {
		return gidBegin;
	}
	
	public int getGidEnd() {
		return gidEnd;
	}
}