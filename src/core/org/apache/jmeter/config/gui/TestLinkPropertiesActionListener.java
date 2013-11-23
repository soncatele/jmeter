package org.apache.jmeter.config.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class TestLinkPropertiesActionListener implements ActionListener,KeyListener  {
	private static final Logger log = LoggingManager.getLoggerForClass();
	@Override
	public void actionPerformed(ActionEvent e) {
//		String action = e.getActionCommand();
//		if (action.equals(DELETE)) {
//			deleteArgument();
//		} else if (action.equals(ADD)) {
//			addArgument();
//		}
		 log.info("actionPerformed");
	}

	@Override
	public void keyPressed(KeyEvent e) {
		 if (e.getKeyCode()==KeyEvent.VK_ENTER){
			 log.info("Enter pressed");
	        }
		 log.info("key pressed");
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
