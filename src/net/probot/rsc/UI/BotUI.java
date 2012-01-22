package net.probot.rsc.UI;
import java.awt.*;
import javax.swing.*;

import net.probot.rsc.bot.Bot;
/*
 * Created by JFormDesigner on Sun Jan 22 05:35:20 GMT 2012
 */

/**
 * @author Mark Gore
 */
public class BotUI extends JFrame {

	public BotUI() {
		initComponents();
		setVisible(true);
		newBot(2);
	}
	
	public void newBot(int world) {
		final Bot bot = new Bot();
		bot.init();
		tabbedPane1.add("Bot", bot.getApplet());
		tabbedPane1.setSelectedIndex(tabbedPane1.getTabCount() - 1);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		tabbedPane1 = new JTabbedPane();
		panel1 = new JPanel();

		//======== this ========
		setTitle("Pro-Bot #4");
		Container contentPane = getContentPane();
		contentPane.setLayout(null);

		//======== tabbedPane1 ========
		{

			//======== panel1 ========
			{
				panel1.setLayout(null);

				{ // compute preferred size
					Dimension preferredSize = new Dimension();
					for(int i = 0; i < panel1.getComponentCount(); i++) {
						Rectangle bounds = panel1.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
					}
					Insets insets = panel1.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					panel1.setMinimumSize(preferredSize);
					panel1.setPreferredSize(preferredSize);
				}
			}
			tabbedPane1.addTab("Main", panel1);

		}
		contentPane.add(tabbedPane1);
		tabbedPane1.setBounds(0, 0, 520, 365);

		{ // compute preferred size
			Dimension preferredSize = new Dimension();
			for(int i = 0; i < contentPane.getComponentCount(); i++) {
				Rectangle bounds = contentPane.getComponent(i).getBounds();
				preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
				preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
			}
			Insets insets = contentPane.getInsets();
			preferredSize.width += insets.right;
			preferredSize.height += insets.bottom;
			contentPane.setMinimumSize(preferredSize);
			contentPane.setPreferredSize(preferredSize);
		}
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JTabbedPane tabbedPane1;
	private JPanel panel1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
