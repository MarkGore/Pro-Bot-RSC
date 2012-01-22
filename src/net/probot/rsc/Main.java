package net.probot.rsc;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.probot.rsc.UI.BotUI;

/**
 * @author Mark Gore
 */
public class Main {

	/**
	 * @param args
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new BotUI();
	}

}
