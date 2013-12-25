package ld28.player;

import java.awt.Graphics;
import java.io.IOException;

import jgame.Animation;
import jgame.Sprite;
import jgame.SpriteSheet;
import jgame.math.Vector2;
import ld28.map.Map;

public class GoalObject extends Sprite {
	public enum GColor {
		RED, BLUE, GREEN
	}
	private Map world;
	public boolean grabbed = false;
	
	public GoalObject(Vector2 position, Map map, GColor col) {
		super(position);
		size = new Vector2(32, 32);
		world = map;
		String path = "img/gem ";
		switch(col) {
			case BLUE:
				path +="blue.png";
				break;
			case GREEN:
				path +="green.png";
				break;
			case RED:
				path +="red.png";
				break;
		}
		try {
			activeAnim = new Animation(new SpriteSheet(path, 1, 2, 32, 32, 0, 0, 0, 0), 0, 1, 0, 0, true);
			activeAnim.setRate(4);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadAnimations() {
	}
	
	public void draw(Graphics g, int x, int y) {
		if(!grabbed) {
			activeAnim.draw(g, x, y);
		}
	}
	
	public void update(double dt) {}
}