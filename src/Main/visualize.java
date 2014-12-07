package Main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class visualize extends Thread {
	
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		JFrame fr=new JFrame();
		fr.setSize(300, 700);
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setVisible(true);
		JTextArea ta=new JTextArea();
		ta.setSize(290, 700);
		JScrollPane jp=new JScrollPane(ta);
		
		jp.setPreferredSize(new Dimension(280,700));
		fr.add(jp);
		
		
		while(true){
			String t="";
			Scanner s;
			try {
				s = new Scanner(new File("src/interM.csv"));
				System.out.println("hello!!!");
			
				while(s.hasNextLine()){
					t=t+s.nextLine();
					t=t+"\n";
				}
				//System.out.println(t);
				s.close();
				ta.setText(t);
				
			
				Thread.sleep(1500);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}


class pane extends JPanel{
	double aray[];
	int maxX,maxY;
	double maximum;
	pane(double p[],int maxX,int maxY){
		this.aray=p;
		this.maxX=maxX;
		this.maxY=maxY;
		repaint();
	}
	public void setAray(double p[]){
		this.aray=p;
		repaint();
	}
	
	public void paint(Graphics g){
		double ratio=aray[aray.length-1];
		for(int i=0;i<aray.length-1;i++){
			
		}
	}
}