package ld28.map;

import java.awt.Graphics;
import java.awt.Rectangle;

import jgame.Animation;
import jgame.Renderable;

public class Tile implements Renderable {
	private Animation anim;
	private Rectangle bounds;
	private int x, y, gid;
	
	public Tile(Animation anim, int x, int y, int width, int height, int gid) {
		this.anim = anim;
		this.x = x;
		this.y = y;
		this.gid = gid;
		bounds = new Rectangle(x * width, y * height, width, height);
	}
	
	public void draw(Graphics g, int dx, int dy) {
		anim.draw(g, x*bounds.width-dx, y*bounds.height-dy);
	}
	
	public Rectangle getBounds() {
		return bounds;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getGid() {
		return gid;
	}
}