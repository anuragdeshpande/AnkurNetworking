package com.personal;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.JFrame;
	
	public class JFrameExample {
		static GraphicsConfiguration gc;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> test = new ArrayList<>();
		JFrame frame= new JFrame(gc);
		frame.setTitle("Welecome to JavaTutorial.net");
		gc.getBounds();
		gc.getDevice();
		frame.setVisible(true);
		frame.getContentPane().setSize(20, 20);
		Component component = frame.getComponent(0);
		component.setBackground(Color.BLACK);
		frame.add(component);
		test.add("Test");
	}

}
