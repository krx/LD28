package ld28.player;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import jgame.Animation;
import jgame.Sprite;
import jgame.SpriteSheet;
import jgame.math.Vector2;
import jgame.util.FileIOHelper;
import ld28.LD28;
import ld28.map.Map;

public class Turret extends Sprite {
	private Player target;
	private Map world;
	private BufferedImage base = null;
	private boolean active = false;
	private long lastShotTime = 0L;
	private final long SHOOT_DELAY = 100L;
	private final double SHOOT_DIST = 500.0;
	private Animation idle;
	private Animation shoot;
	
	public Turret(Vector2 position, Player target, Map map) {
		super(position);
		this.target = target;
		size = new Vector2(64, 32);
		world = map;
		try {
			base = ImageIO.read(FileIOHelper.loadResource("img/turret base.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics g, int x, int y) {
		if(active) {
			g.drawImage(base, x, y, null);
			
			Vector2 tc = target.getPosition().add(target.getSize().scale(0.5));
			Vector2 c = getPosition().add(getSize().scale(0.5));
			double dx = tc.x - c.x;
			double dy = tc.y - c.y;
			double angle = Math.atan2(dy, dx);
			
			BufferedImage gun = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
			Graphics gGun = gun.getGraphics();
			activeAnim = c.dist(tc) <= SHOOT_DIST ? shoot : idle;
			activeAnim.draw(gGun, 0, 0);
			
			AffineTransform t = AffineTransform.getRotateInstance(angle, gun.getWidth() / 2, gun.getHeight() / 2);
			AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_BILINEAR);
			
			g.drawImage(op.filter(gun, null), x, y-16, null);
		}
	}
	
	public void update(float dt) {
		if(active) {
			if(System.currentTimeMillis() - lastShotTime >= SHOOT_DELAY) {
				Vector2 tc = target.getPosition().add(target.getSize().scale(0.5));
				Vector2 c = getPosition().add(getSize().scale(0.5));
				double dist = c.dist(tc);
				if(dist < SHOOT_DIST) {
					double dx = tc.x - c.x;
					double dy = tc.y - c.y;
					double angle = Math.atan2(dy, dx);
					LD28.bullets.add(new Bullet(c, new Vector2(Math.toDegrees(angle)).scale(15), world, target));
					lastShotTime = System.currentTimeMillis();
					LD28.shoot.play(0.1);
				}
			}
		}
	}
	
	public void loadAnimations() {
		idle = new Animation("img/turret gun.png");
		try {
			shoot = new Animation(new SpriteSheet("img/turret gun shoot.png", 1, 9, 64, 64, 0, 0, 0, 0), 0, 8, 0, 0, true);
			shoot.setRate(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		activeAnim = idle;
	}
	
	public void activate() {
		active = true;
	}
}