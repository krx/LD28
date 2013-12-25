package ld28.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.EnumMap;

import jgame.Animation;
import jgame.Sprite;
import jgame.SpriteSheet;
import jgame.input.Keyboard;
import jgame.math.Vector2;
import jgame.util.MathUtils;
import ld28.LD28;
import ld28.map.Map;

public class Player extends Sprite {
	public enum State {
		IDLE,
		RUN,
		JUMP,
	}
	
	private EnumMap<State, Animation> animMap;
	private State state;
	private Vector2 vel;
	private Vector2 acc;
	private Map world;
	private boolean jumping = false;
	private boolean hasGoal = false;
	private GoalObject goal;
	private double health = 500;
	private final double MAX_HEALTH = 500;
	
	public Player(Vector2 position, Map map, GoalObject goal) {
		super(position);
		world = map;
		vel = new Vector2();
		acc = new Vector2(0, 0.4);
		size = new Vector2(48, 48);
		this.goal = goal;
	}
	
	public void loadAnimations() {
		animMap = new EnumMap<State, Animation>(State.class);
		Animation idle = null, run = null, jump = null;
		try {
			idle = new Animation(new SpriteSheet("img/theif idle.png", 1, 5, 48, 48, 0, 0, 0, 0), 0, 4, 0, 0, true);
			run = new Animation(new SpriteSheet("img/theif run.png", 1, 8, 48, 48, 0, 0, 0, 0), 0, 7, 0, 0, true);
			jump = new Animation("img/theif jump.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		idle.setRate(4);
		run.setRate(4);
		state = State.IDLE;
		animMap.put(State.IDLE, idle);
		animMap.put(State.RUN, run);
		animMap.put(State.JUMP, jump);
		activeAnim = animMap.get(state);
	}
	
	public void damage(double dmg) {
		health = Math.max(health - dmg, 0);
	}
	
	public void draw(Graphics g, int x, int y) {
		activeAnim.draw(g, x, y);
		drawFlashLightEffect((Graphics2D) g, x, y);
		drawHealthBar(g);
	}
	
	private void drawFlashLightEffect(Graphics2D g, int x, int y) {
		float cx = (float) (x + size.x / 2);
		float cy = (float) (y + size.y / 2);
		int rad = 300;
		float[] range = { 0.0f, 1.0f };
		
		Color a = new Color(255, 0, 0, (int) (160 - MathUtils.lerp(0, 160, health / MAX_HEALTH)));
		Color b = Color.BLACK;
		Color[] cols = { a, b };
		
		Paint paint = new RadialGradientPaint(cx, cy, rad, range, cols);
		g.setPaint(paint);
		g.fillRect(0, 0, LD28.WIDTH, LD28.HEIGHT);
	}
	
	private void drawHealthBar(Graphics g) {
		int x1 = 5, x2 = LD28.WIDTH / 2;
		int red = (int) (255 - MathUtils.lerp(0, 255, health / MAX_HEALTH));
		int green = (int) MathUtils.lerp(0, 255, health / MAX_HEALTH);
		g.setColor(new Color(red, green, 0));
		g.fillRect(x1, LD28.HEIGHT - 25, (int) (MathUtils.lerp(x1, x2, health / MAX_HEALTH) - x1), 20);
	}
	
	public void update(double dt) {
		state = State.IDLE;
		boolean flipped = false;
		// Movement
		acc.x = 0;
		if(Keyboard.keyDown('d') || Keyboard.keyDown(KeyEvent.VK_RIGHT)) {
			acc.x += 1;
			state = State.RUN;
		}
		
		if(Keyboard.keyDown('a') || Keyboard.keyDown(KeyEvent.VK_LEFT)) {
			acc.x -= 1;
			state = State.RUN;
			flipped = true;
		}
		
		if(Keyboard.keyDown('w') || Keyboard.keyDown(' ') || Keyboard.keyDown(KeyEvent.VK_UP)) {
			if(!jumping) {
				vel.y = -11;
				jumping = true;
			}
		}
		
		state = jumping ? State.JUMP : state;
		
		activeAnim = animMap.get(state);
		activeAnim.setFlippedH(flipped);
		
		vel.add(acc).scale(0.9, 1);
		
		// X collision and movement
		if(!world.collide(new Vector2(position).add(vel.x, 0), size)) {
			position.add(vel.x, 0);
		} else {
			vel.x = 0;
		}
		
		// Y collision and movement
		if(!world.collide(new Vector2(position).add(0, vel.y), size)) {
			position.add(0, vel.y);
		} else if(vel.y > 0) {
			vel.y = 0;
			if(jumping) {
				jumping = false;
			}
		}
		
		// Goal object
		if(!hasGoal && getBounds().intersects(goal.getBounds())) {
			hasGoal = true;
			goal.grabbed = true;
			LD28.grab.play();
			LD28.SECURITYLOCKDOWN();// OH YOU'RE F*CKED NOW
		}
		
		// Jumped out exit
		if(hasGoal && position.y > world.getPxHeight()) {
			hasGoal = false;
			LD28.levelWin();
		}
		
		// You dead
		if(hasGoal && health == 0) {
			hasGoal = false;
			LD28.levelLose();
		}
	}
}