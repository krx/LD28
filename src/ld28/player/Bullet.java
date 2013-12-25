package ld28.player;

import java.awt.Graphics;

import jgame.Animation;
import jgame.Sprite;
import jgame.math.Vector2;
import ld28.LD28;
import ld28.map.Map;

public class Bullet extends Sprite {
	private Vector2 vel;
	private Map world;
	private Player target;
	
	public Bullet(Vector2 position, Vector2 vel, Map map, Player target) {
		super(position);
		this.vel = vel;
		world = map;
		this.target = target;
		size = new Vector2(8,8);
	}
	
	public void draw(Graphics g, int x, int y) {
		activeAnim.draw(g, x, y);
	}
	
	public void update(double dt) {
		position.add(vel);
		
		if(world.collide(getBounds())) {
			LD28.toDispose.add(this);
		}
		
		if(getBounds().intersects(target.getBounds())) {
			target.damage(2);
			LD28.toDispose.add(this);
		}
	}
	
	public void loadAnimations() {
		activeAnim = new Animation("img/bullet.png");
	}
	
}
