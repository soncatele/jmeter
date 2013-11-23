package org.apache.jmeter.config.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class asdasd {
	 public static void main(String[] args) {

         JPanel jpanel_0 = new JPanel();
         jpanel_0.setBackground(Color.pink);
         for(int i=0;i<3;++i) jpanel_0.add(new JLabel("label"+i));

         JPanel jpanel_1 = new JPanel(new GridLayout(0,1));
         jpanel_1.setBackground(Color.cyan);
         for(int i=0;i<7;++i) jpanel_1.add(new JLabel("  label"+i+"  "));

         JFrame frame = new JFrame();
         Container cp = frame.getContentPane();
         cp.add(jpanel_0,BorderLayout.WEST);
         cp.add(jpanel_1,BorderLayout.EAST);

         frame.pack();
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.setVisible(true);

      }
}
