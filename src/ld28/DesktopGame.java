package ld28;

import javax.swing.JFrame;

public class DesktopGame extends JFrame {
	
	public DesktopGame() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		LD28 game = new LD28(false);
		setSize(game.getPreferredSize());
		setResizable(false);
		add(game);
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new DesktopGame();
	}
}
