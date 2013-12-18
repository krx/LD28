package ld28;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import jgame.Camera;
import jgame.JGame;
import jgame.Sprite;
import jgame.input.KeyEventType;
import jgame.input.Keyboard;
import jgame.input.KeyboardAction;
import jgame.math.Vector2;
import jgame.util.FileIOHelper;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import ld28.gui.Dialog;
import ld28.gui.Menu;
import ld28.gui.PopupMenu;
import ld28.map.Map;
import ld28.map.TMXParser;
import ld28.player.Bullet;
import ld28.player.GoalObject;
import ld28.player.GoalObject.GColor;
import ld28.player.Player;
import ld28.player.Turret;

public class LD28 extends JGame {
	public enum SCREEN {
		MENU,
		INSTRUCTIONS,
		GAME
	}
	
	public enum SCREENSTATE {
		DIALOG,
		PAUSE,
		PLAY,
		ENDGAME
	}
	
	public enum LEVEL {
		ONE,
		TWO
	}
	
	public static Camera cam;
	private static Map map;
	public static Player p;
	private static SCREEN screen = SCREEN.MENU;
	private static SCREENSTATE screenstate = SCREENSTATE.PLAY;
	private static LEVEL level = LEVEL.ONE;
	private static Dialog d = null;
	private static PopupMenu pause = null;
	private static PopupMenu endGame = null;
	private static Menu mainMenu;
	private static Menu instructions;
	public static final int WIDTH = 1024, HEIGHT = 768;
	private static GoalObject go = null;
	public static ArrayList<Turret> turrets = new ArrayList<Turret>();
	public static ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	public static ArrayList<Sprite> toDispose = new ArrayList<Sprite>();
	private static boolean lockdown = false;
	private static Font rexlia = null;
	private static Font digital = null;
	
	// Sounds
	public static Sound shoot;
	public static Sound grab;
	public static Music buzz;
	public static Music siren;
	
	public LD28() {
		super();
		setResolution(WIDTH, HEIGHT);
		frame.setResizable(false);
		initControls();
		loadSounds();
		loadLevel();
		try {
			rexlia = Font.createFont(Font.TRUETYPE_FONT, FileIOHelper.loadResource("/font/rexlia rg.ttf"));
			digital = Font.createFont(Font.TRUETYPE_FONT, FileIOHelper.loadResource("/font/digital-7.ttf"));
		} catch (FontFormatException  e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		screen = SCREEN.MENU;
		mainMenu = new Menu("Master Thief", WIDTH, HEIGHT, true, 108f, "",  "Level 1", "Level 2", "Instructions", "Quit");
		mainMenu.setFont(rexlia);
		mainMenu.setFontSize(84f);
		instructions = new Menu("Instructions", WIDTH, HEIGHT, false, 72f, " ", "WAD / Space / Arrow keys to move", " ", "You have been hired to steal a", "valuable gem, once you find it,","you have one minute to find", "the exit and escape."," "," ", "ESC to return to main menu");
		instructions.setFont(rexlia);
		instructions.setFontSize(48f);
		
		
		init(1.0 / 60.0);
	}
	
	private void loadSounds() {
		shoot = TinySound.loadSound("/snd/shoot.wav");
		grab = TinySound.loadSound("/snd/grab.wav");
		buzz = TinySound.loadMusic("/snd/buzz.wav");
		siren = TinySound.loadMusic("/snd/siren.wav");
	}
	
	private void initControls() {
		Keyboard.addKeyAction(KeyEventType.KEY_RELEASED, new KeyboardAction() {
			public void actionPerformed(KeyEvent e) {
				switch(e.getKeyCode()) {
					case KeyEvent.VK_ESCAPE:
						if(screen == SCREEN.GAME) {
							switch(screenstate) {
								case DIALOG:
									d = null;
									screenstate = SCREENSTATE.PLAY;
									break;
								case PAUSE:
									screenstate = SCREENSTATE.PLAY;
									pause.setSelectIndex(0);
									break;
								case PLAY:
									screenstate = SCREENSTATE.PAUSE;
									pause = new PopupMenu("PAUSED", WIDTH, HEIGHT, "Resume", "Main Menu");
									pause.setFont(rexlia);
									pause.setFontSize(48f);
									break;
							}
						}
						if(screen == SCREEN.INSTRUCTIONS) {
							gotoMenu();
						}
						break;
					case KeyEvent.VK_SPACE:
						if(screenstate == SCREENSTATE.DIALOG) {
							d.iterateMessages();
							if(d.isDoneDialog()) {
								d = null;
								screenstate = SCREENSTATE.PLAY;
							}
						}
						// intentional no break
					case KeyEvent.VK_ENTER:
						if(screen == SCREEN.MENU) {
							switch(mainMenu.getSelectIndex()) {
								case 0:
									level = LEVEL.ONE;
									loadLevel();
									break;
								case 1:
									level = LEVEL.TWO;
									loadLevel();
									break;
								case 2:
									screen = SCREEN.INSTRUCTIONS;
									break;
								case 3:
									stop();
									break;
							}
						}
						if(screen == SCREEN.GAME) {
							if(screenstate == SCREENSTATE.PAUSE) {
								switch(pause.getSelectIndex()) {
									case 0:
										screenstate = SCREENSTATE.PLAY;
										pause.setSelectIndex(0);
										break;
									case 1:
										gotoMenu();
										break;
								}
							}
							if(screenstate == SCREENSTATE.ENDGAME) {
								switch(endGame.getSelectIndex()) {
									case 0:
										loadLevel();
										break;
									case 1:
										gotoMenu();
										break;
								}
							}
						}
						
						break;
					case KeyEvent.VK_W:
					case KeyEvent.VK_UP:
						if(screen == SCREEN.GAME) {
							if(screenstate == SCREENSTATE.PAUSE) {
								pause.selectUp();
							}
							if(screenstate == SCREENSTATE.ENDGAME) {
								endGame.selectUp();
							}
						}
						if(screen == SCREEN.MENU) {
							mainMenu.selectUp();
						}
						break;
					case KeyEvent.VK_S:
					case KeyEvent.VK_DOWN:
						if(screen == SCREEN.GAME) {
							if(screenstate == SCREENSTATE.PAUSE) {
								pause.selectDown();
							}
							if(screenstate == SCREENSTATE.ENDGAME) {
								endGame.selectDown();
							}
						}
						if(screen == SCREEN.MENU) {
							mainMenu.selectDown();
						}
						break;
				}
			}
		});
	}
	
	private void gotoMenu() {
		screen = SCREEN.MENU;
		mainMenu.resetSelector();
		buzz.stop();
		siren.stop();
	}
	
	public void showDialog(float size, String... messages) {
		d = new Dialog(WIDTH, HEIGHT, messages);
		d.setFont(rexlia);
		d.setFontSize(size);
		screenstate = SCREENSTATE.DIALOG;
	}
	
	private static double countdown = 0.0;
	private DecimalFormat df = new DecimalFormat("0.##");
	
	public void paintComponent(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		switch(screen) {
			case GAME:
				map.draw(g, (int) cam.off.x, (int) cam.off.y);
				go.draw(g, cam);
				for(int i = 0, j = bullets.size(); i < j; i++) {
					try {
						if(bullets.get(i).getBounds().intersects(cam.viewport)) {
							bullets.get(i).draw(g, cam);
						}
					} catch (IndexOutOfBoundsException e) {
						// e.printStackTrace();
					}
				}
				for(int i = 0, j = turrets.size(); i < j; i++) {
					if(turrets.get(i).getBounds().intersects(cam.viewport)) {
						turrets.get(i).draw(g, cam);
					}
				}
				p.draw(g, cam);
				if(lockdown) {
					drawCountdown(g);
				}
				if(screenstate == SCREENSTATE.DIALOG) {
					d.draw(g);
				}
				if(screenstate == SCREENSTATE.PAUSE) {
					pause.draw(g);
				}
				if(screenstate == SCREENSTATE.ENDGAME) {
					endGame.draw(g);
				}
				break;
			case INSTRUCTIONS:
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, WIDTH, HEIGHT);
				instructions.draw(g);
				break;
			case MENU:
				Font oldFont = g.getFont();
				g.setFont(rexlia);
				g.setFont(g.getFont().deriveFont(18f));
				FontMetrics fm = g.getFontMetrics();
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, WIDTH, HEIGHT);
				mainMenu.draw(g);
				g.setColor(new Color(0, 255, 0));
				g.drawString("Game made in 48 hours", 5, HEIGHT - fm.getHeight() + 10);
				g.drawString("#LD28", WIDTH - fm.stringWidth("#LD28") - 5, HEIGHT - fm.getHeight() + 10);
				g.setFont(oldFont);
				break;
		}
		disposeSprites();
	}
	
	private void drawCountdown(Graphics g) {
		Font old = g.getFont();
		g.setFont(digital);
		g.setFont(g.getFont().deriveFont(48f));
		g.setColor(Color.RED);
		String time = df.format(countdown);
		if(time.contains(".")) {
			time = time.substring(0, time.indexOf('.')) + ":" + time.substring(time.indexOf('.') + 1);
		}
		g.drawString(time, WIDTH - 150, HEIGHT - 10);
		g.setFont(old);
	}
	
	public void update(float dt) {
		frame.setTitle("LD28 - " + getFPS());
		if(screen == SCREEN.GAME) {
			switch(screenstate) {
				case DIALOG:
					d.iterateCharacters();
					break;
				case PLAY:
					p.update(dt);
					go.update(dt);
					for(int i = 0, j = bullets.size(); i < j; i++) {
						try {
							bullets.get(i).update(dt);
						} catch (IndexOutOfBoundsException e) {
							// e.printStackTrace();
						}
					}
					for(int i = 0, j = turrets.size(); i < j; i++) {
						turrets.get(i).update(dt);
					}
					cam.follow(p.getPosition(), 10);
					if(lockdown) {
						countdown = Math.max(countdown - dt, 0);
						if(countdown == 0) {
							levelLose();
						}
					}
					break;
			}
		}
		disposeSprites();
	}
	
	private void disposeSprites() {
		for(int i = 0, j = toDispose.size(); i < j; i++) {
			try {
				Sprite s = toDispose.get(i);
				if(bullets.contains(s)) {
					bullets.remove(s);
				}
				if(turrets.contains(s)) {
					turrets.remove(s);
				}
			} catch (IndexOutOfBoundsException e) {
				// e.printStackTrace();
			}
		}
		toDispose.clear();
	}
	
	public void loadLevel() {
		screen = SCREEN.GAME;
		switch(level) {
			case ONE:
				setupLevel1();
				showDialog(48f, "Boss: Alright, I'm counting on you to steal this gem, this is the only chance we'll ever get.",
						"Unfortunately... you are by far, without a doubt, the absolute worst thief I have ever laid eyes on!",
						"This building is equipped with top notch security, so if, er.. when you set off the alarm, you'll only have 1 minute to get the hell out alive!",
						"Me: Alive? But don't I get multiple lives or something?",
						"Boss: Excuse me?! Multiple what?? Do you think this is some kind of game?! If you get your ass shot dead, that's it! YOU'RE DEAD!",
						"GET IT?! ONE MINUTE! ONE LIFE! NOW GET ME THAT GEM!!");
				break;
			case TWO:
				setupLevel2();
				showDialog(48f, "Boss: I'll never understand how you managed to get me that last gem, but to be honest, I don't give a shit if you die. You know the drill: GET THAT GEM!");
				break;
		}
		
	}
	
	private void resetData() {
		d = null;
		pause = null;
		endGame = null;
		lockdown = false;
		bullets.clear();
		turrets.clear();
		screenstate = SCREENSTATE.PLAY;
	}
	
	public void setupLevel1() {
		resetData();
		
		map = TMXParser.loadTMXMap("/maps/data/map1.tmx");
		cam = new Camera(frame.getWidth(), frame.getHeight(), new Rectangle(map.getPxWidth(), map.getPxHeight()));
		go = new GoalObject(new Vector2(32 * 64 + 16, 64 * 10 + 32), map, GColor.RED);
		p = new Player(new Vector2(64 * 1, 64 * 6), map, go);
		
		for(int x = 6; x <= 28; x += 2) {
			turrets.add(new Turret(new Vector2(64 * x, 64 * 1), p, map));
			turrets.add(new Turret(new Vector2(64 * x, 64 * 12), p, map));
		}
		for(int x = 9; x <= 26; x += 2) {
			turrets.add(new Turret(new Vector2(64 * x, 64 * 4), p, map));
		}
		for(int x = 12; x <= 21; x += 3) {
			turrets.add(new Turret(new Vector2(64 * x, 64 * 15 + 32), p, map));
		}
		for(int x = 11; x <= 23; x += 3) {
			turrets.add(new Turret(new Vector2(64 * x, 64 * 7 + 32), p, map));
		}
		turrets.add(new Turret(new Vector2(64 * 8, 64 * 10 + 32), p, map));
		turrets.add(new Turret(new Vector2(64 * 9, 64 * 9 + 32), p, map));
		turrets.add(new Turret(new Vector2(64 * 10, 64 * 8 + 32), p, map));
		turrets.add(new Turret(new Vector2(64 * 24, 64 * 8 + 32), p, map));
		turrets.add(new Turret(new Vector2(64 * 25, 64 * 9 + 32), p, map));
		turrets.add(new Turret(new Vector2(64 * 26, 64 * 10 + 32), p, map));
	}
	
	public void setupLevel2() {
		resetData();
		
		map = TMXParser.loadTMXMap("/maps/data/map2.tmx");
		cam = new Camera(frame.getWidth(), frame.getHeight(), new Rectangle(map.getPxWidth(), map.getPxHeight()));
		go = new GoalObject(new Vector2(64 * 2 + 16, 64 * 26 + 32), map, GColor.BLUE);
		p = new Player(new Vector2(64 * 1, 64 * 2), map, go);
		
		// Add a metric fuckton of turrets
		for(int x = 1; x <= 43; x += 2) {
			turrets.add(new Turret(new Vector2(64 * x, 64 * 1), p, map));
		}
		for(int x = 7; x <= 15; x += 2) {
			turrets.add(new Turret(new Vector2(64 * x, 64 * 26), p, map));
		}
		for(int x = 23; x <= 42; x += 2) {
			turrets.add(new Turret(new Vector2(64 * x, 64 * 21), p, map));
		}
		for(int x = 7; x <= 22; x += 3) {
			turrets.add(new Turret(new Vector2(64 * x, 64 * 17), p, map));
		}
		for(int x = 4; x <= 22; x += 2) {
			turrets.add(new Turret(new Vector2(64 * x, 64 * 14), p, map));
		}
		for(int x = 26; x <= 42; x += 2) {
			turrets.add(new Turret(new Vector2(64 * x, 64 * 18), p, map));
		}
		for(int x = 21; x <= 43; x += 2) {
			turrets.add(new Turret(new Vector2(64 * x, 64 * 8), p, map));
		}
		for(int x = 25; x <= 43; x += 2) {
			turrets.add(new Turret(new Vector2(64 * x, 64 * 11), p, map));
		}
		for(int x = 2; x <= 12; x += 2) {
			turrets.add(new Turret(new Vector2(64 * x, 64 * 9 + 32), p, map));
		}
		for(int x = 27; x <= 43; x += 2) {
			turrets.add(new Turret(new Vector2(64 * x, 64 * 16 + 32), p, map));
		}
		for(int x = 1; x <= 16; x += 3) {
			turrets.add(new Turret(new Vector2(64 * x, 64 * 6 + 32), p, map));
		}
		turrets.add(new Turret(new Vector2(64 * 9, 64 * 21), p, map));
		turrets.add(new Turret(new Vector2(64 * 12, 64 * 21), p, map));
		turrets.add(new Turret(new Vector2(64 * 18, 64 * 28 + 32), p, map));
		turrets.add(new Turret(new Vector2(64 * 20, 64 * 28 + 32), p, map));
	}
	
	public static void levelWin() {
		endGame = new PopupMenu("LEVEL COMPLETE", WIDTH, HEIGHT, "Play Again", "Main Menu");
		endGame.setFont(rexlia);
		endGame.setFontSize(48f);
		screenstate = SCREENSTATE.ENDGAME;
		buzz.stop();
		siren.stop();
	}
	
	public static void levelLose() {
		endGame = new PopupMenu("LEVEL FAILED", WIDTH, HEIGHT, "Play Again", "Main Menu");
		endGame.setFont(rexlia);
		endGame.setFontSize(48f);
		screenstate = SCREENSTATE.ENDGAME;
		buzz.stop();
		siren.stop();
	}
	
	// NICE F*CKING JOB
	public static void SECURITYLOCKDOWN() {
		for(Turret turret : turrets) {
			turret.activate();
		}
		lockdown = true; // F*CKED MODE
		countdown = 60.0; // GTFO
		map.createExit(p);
		buzz.play(true, 2);
		siren.play(true, 2);
	}
}