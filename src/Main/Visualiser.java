package Main;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;



public class Visualiser
{ 
	static Color[] colors;
	static ArrayList<Individual> individuals;
	static ArrayList<String> names;
	
	static public double scale;
	
	
	static public double scaleMin;
	static public double scaleMax;
    static ProblemInstance problemInstance;
    static public double[] dep_x,dep_y,cus_x,cus_y;
    static double center_x,center_y;
    static int[] d_x,d_y,c_x,c_y;

    static public int window_width = 1200;
    static public int drawingBoard_width = 800;
    static public int optionPanel_width = 400;
    static public int height = 650;
    static Window window;
	
	static public int selectedIndividual = -1;
	public Visualiser(ProblemInstance problemInstance) 
	{
		// TODO Auto-generated constructor stub
			
		individuals = new ArrayList<Individual>();
		names = new ArrayList<String>();
		colors = new Color[problemInstance.vehicleCount];
		
		
		for(int i=0;i<problemInstance.vehicleCount;i++)
		{
			colors[i] = new Color(Utility.randomIntInclusive(255), Utility.randomIntInclusive(255), Utility.randomIntInclusive(255));
		}
		
        this.problemInstance=problemInstance;
        dep_x = problemInstance.dep_x;
        dep_y = problemInstance.dep_y;
        cus_x = problemInstance.cus_x;
        cus_y = problemInstance.cus_y;
        
/*        dep_x=new double[problemInstance.depotCount];
    	dep_y=new double[problemInstance.depotCount];
    	cus_x=new double[problemInstance.customerCount];
    	cus_y=new double[problemInstance.customerCount];
*/    	
    	d_x=new int[problemInstance.depotCount];
    	d_y=new int[problemInstance.depotCount];
    	c_x=new int[problemInstance.customerCount];
    	c_y=new int[problemInstance.customerCount];
    	
        double max_x,max_y,min_x,min_y;
        
        max_x=Maximum(dep_x,cus_x);
        min_x=Minimum(dep_x,cus_x);        
        max_y=Maximum(dep_y, cus_y);
        min_y=Minimum(dep_y, cus_y);
        
       // System.out.println("Max x, min x, maxy min y : "+max_x+" "+min_x+" "+max_y+" "+min_y);
             
        double x_factor=((double)drawingBoard_width*0.80/(max_x-min_x));
        double y_factor=((double)height*0.80/(max_y-min_y));
        
        for(int i=0;i<dep_x.length;i++)
        {
        	dep_x[i] += (Math.abs(min_x) + 5);
        	dep_y[i] += (Math.abs(min_y) + 5);	
        }
        
        for(int i=0;i<cus_x.length;i++)
        {
        	cus_x[i] += (Math.abs(min_x) + 5);
        	cus_y[i] += (Math.abs(min_y) + 5);	
        }
        
        max_x=Maximum(cus_x,dep_x);
        min_x=Minimum(cus_x,dep_x);
        max_y=Maximum(dep_y, cus_y);
        min_y=Minimum(cus_y, dep_y);
        
        double scale_x = Visualiser.drawingBoard_width / max_x *0.95 ;
        double scale_y = Visualiser.height / max_y *0.95;
        
        scale = Math.min(scale_x, scale_y);
        scaleMin = scale;
        scaleMax = scale * 5;
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                window = new Window();
                window.setVisible(true);
                //Window.surface.repaint();
            }
        });
	}
	
	
	private double Minimum(double[] dep_x2, double[] cus_x2) {
		// TODO Auto-generated method stub
		double min=dep_x2[0];
		for(int i=0;i<dep_x2.length;i++){
			if(dep_x2[i]<min) min=dep_x2[i];
		}
		for(int i=0;i<cus_x2.length;i++){
			if(cus_x2[i]<min) min=cus_x2[i];
		}
		
		return min;
	}
	private double Maximum(double[] dep_x2, double[] cus_x2) 
	{
		// TODO Auto-generated method stub
		double min=dep_x2[0];
		for(int i=0;i<dep_x2.length;i++){
			if(dep_x2[i]>min) min=dep_x2[i];
		}
		for(int i=0;i<cus_x2.length;i++){
			if(cus_x2[i]>min) min=cus_x2[i];
		}
		
		return min;
	}
	


	public void drawIndividual(Individual individual,String name) 
	{
		//System.out.println("VIS - ADDING : "+name);
		individuals.add(individual);
		names.add(name);
		
		try
		{
			window.optionsPanel.updateOptionPane();
		}
		catch (Exception e) {
			// TODO: handle exception
			System.err.println("EXCEPTION IN VISUALIZER");
			e.printStackTrace();
		}
			//Window.surface.repaint();
	}
}

class Window extends JFrame {

	static public Surface surface=null;  
    static public OptionsPanel optionsPanel; 
    
    public Window() 
    {
        setTitle("VISUALISING MDPVRP");
        setSize(Visualiser.window_width+18, Visualiser.height+40);
       // setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //setLocationRelativeTo(this);
        
        surface = new Surface();  
        optionsPanel = new OptionsPanel(); 
        
        JPanel wholePanel = new JPanel();

        wholePanel.setSize(Visualiser.window_width,Visualiser.height);
        surface.setSize(Visualiser.window_width,Visualiser.height);
        wholePanel.setLayout(new BorderLayout());
        
        surface.setLayout(null);
//        surface.setLocation(new Point(0,0));
        
        //optionsPanel.setLayout(null);
  //      optionsPanel.setLocation(new Point(Visualiser.drawingBoard_width,0));
        
        JScrollPane scrollPane = new JScrollPane(surface);
        scrollPane.setPreferredSize(new Dimension(Visualiser.drawingBoard_width-10,Visualiser.height-20));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
       
        
        wholePanel.add(scrollPane,BorderLayout.WEST);
        
        wholePanel.add(optionsPanel,BorderLayout.EAST);
        
        getContentPane().add(wholePanel);
    }


}

class Surface extends JPanel {

	static int selectedPeriod = 0;
	static ArrayList<Integer> selectedVehicles;
	public Surface() {
		// TODO Auto-generated constructor stub
		//setSize(Visualiser.drawingBoard_width,Visualiser.height);
		//setBorder(BorderFactory.createLineBorder(Color.red));
		selectedVehicles = new ArrayList<Integer>();
		//for(int i=0;i<Visualiser.problemInstance.vehicleCount;i++)
    	//	selectedVehicles.add(i);
        setPreferredSize(new Dimension(Visualiser.drawingBoard_width*5,Visualiser.height*5));

		setBackground(Color.WHITE);
	}
    private void doDrawing(Graphics g) 
    {

    	if(Visualiser.individuals.size()==0 || Visualiser.selectedIndividual==-1)return;
    	Individual individual = Visualiser.individuals.get(Visualiser.selectedIndividual);
    	
    	
    	//Scale
        for(int i=0;i<Visualiser.problemInstance.depotCount;i++)
        {
        	double x = Visualiser.dep_x[i]*Visualiser.scale;
        	double y = Visualiser.dep_y[i]*Visualiser.scale;
        	
        	Visualiser.d_x[i]=(int)x;
        	Visualiser.d_y[i]=(int)y;
        }
        for(int i=0;i<Visualiser.problemInstance.customerCount;i++)
        {
        	double x = Visualiser.cus_x[i]*Visualiser.scale;
        	double y = Visualiser.cus_y[i]*Visualiser.scale;

        	Visualiser.c_x[i]=(int)(x);
        	Visualiser.c_y[i]=(int)(y);
        }

        Graphics2D g2d = (Graphics2D) g;
        
        //draw depots
        
        int depotRadius = 8;
        g2d.setColor(Color.GREEN);
        for(int i=0;i<Visualiser.d_x.length;i++)
        {
            g2d.fillOval(Visualiser.d_x[i] - depotRadius, Visualiser.d_y[i] - depotRadius, depotRadius*2, depotRadius*2); 
            g2d.drawString("D"+i, Visualiser.d_x[i]+depotRadius, Visualiser.d_y[i]);
            //System.out.println("Drawing Depot on : "+Visualiser.d_x[i]+" "+Visualiser.d_y[i]);
        }
        
        int clientRadius = 4;
        for(int i=0;i<Visualiser.c_x.length;i++)
        {
        	if(individual.periodAssignment[selectedPeriod][i]==true) g2d.setColor(Color.CYAN);
        	else g2d.setColor(new Color(245,245,240));
            g2d.fillOval(Visualiser.c_x[i] - clientRadius , Visualiser.c_y[i] - clientRadius, clientRadius*2, clientRadius*2);
            

        	if(individual.periodAssignment[selectedPeriod][i]==true)
    		{
        		g2d.setColor(Color.black);
                g2d.drawString(""+i, Visualiser.c_x[i] + clientRadius ,Visualiser.c_y[i]- clientRadius );
    		}
        	else g2d.setColor(Color.lightGray);
        }
        
        //drawing routes
        for(int i=0;i<selectedVehicles.size();i++)
        {
        	int selectedVehicle = selectedVehicles.get(i);
        	ArrayList<Integer> route = individual.routes.get(selectedPeriod).get(selectedVehicle);
        	
        	g2d.setColor(Visualiser.colors[i]);
 
        	if(route.size()==0)continue;
        	
        	int depot  = individual.problemInstance.depotAllocation[selectedVehicle];
        	
        	g2d.setStroke(new BasicStroke(3));
        	g2d.drawLine(Visualiser.d_x[depot], Visualiser.d_y[depot], Visualiser.c_x[route.get(0)], Visualiser.c_y[route.get(0)]);
        	for(int clientIndex=1;clientIndex<route.size();clientIndex++)
        	{
        		int prev = route.get(clientIndex-1);
        		int cur = route.get(clientIndex);
        		g2d.drawLine(Visualiser.c_x[prev], Visualiser.c_y[prev], Visualiser.c_x[cur], Visualiser.c_y[cur]);
        	}
        	int last= route.get(route.size()-1);
        	g2d.drawLine(Visualiser.d_x[depot], Visualiser.d_y[depot], Visualiser.c_x[last], Visualiser.c_y[last]);

        }
    
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }
}

class OptionsPanel extends JPanel
{
	JComboBox periodCombo;
	JComboBox individualCombo;
	JCheckBox vehicleCheckBox;
	JSlider zoomSlider;
	JTextArea info;
	
	public void updateOptionPane() 
	{
		
		individualCombo.removeAllItems();
		for(int i=0;i<Visualiser.individuals.size();i++)
		{
			individualCombo.addItem(Visualiser.names.get(i));
			individualCombo.addActionListener(new IndividualComboActionListener());
			Window.surface.repaint();
		}

	}
	public OptionsPanel() {

		//Individual COmbo
		individualCombo = new JComboBox();
		add(individualCombo);
		
		//PERIOD SELECTOR
		periodCombo = new JComboBox();
		for(int i=0;i<Visualiser.problemInstance.periodCount;i++) periodCombo.addItem("Period "+i);
		periodCombo.addActionListener(new PeriodComboActionListener());		
		add(periodCombo);
		
		
		
		
		/////////////////////////////////////////////////////////////////////
		//route panel
		JPanel routePanel= new JPanel();
		routePanel.setBackground(null);
		routePanel.setPreferredSize(new Dimension(Visualiser.optionPanel_width*8/10,Visualiser.height*3/10));
		

		for(int i=0;i<Visualiser.problemInstance.vehicleCount;i++)
		{
			vehicleCheckBox = new JCheckBox("Route "+i);
			
			vehicleCheckBox.addItemListener(new RouteSelectionItemStateChanged());
			vehicleCheckBox.setSelected(true);
			routePanel.add(vehicleCheckBox);
			//Window.surface.repaint();
		}
		add(routePanel);

		/////////////////////////////////////////////////////////////////////
		
		zoomSlider = new JSlider(JSlider.HORIZONTAL, 1,100 , 1);
	    zoomSlider.setPreferredSize(new Dimension(Visualiser.optionPanel_width*8/10,Visualiser.height*1/25));
	    zoomSlider.addChangeListener(new ZoomChangeListener());
		add(zoomSlider);
		
		
		info = new JTextArea();
		info.setPreferredSize(new Dimension(Visualiser.optionPanel_width*8/10,Visualiser.height*3/10));
		info.setEditable(false);
		add(info);
		
		

		updateOptionPane();
		updateTextArea();
		
		// TODO Auto-generated constructor stub
		setSize(Visualiser.optionPanel_width,Visualiser.height);
		setPreferredSize(new Dimension( Visualiser.optionPanel_width,Visualiser.height));
		setBackground(Color.DARK_GRAY);

	}
	
	class ZoomChangeListener implements ChangeListener
	{

		@Override
		public void stateChanged(ChangeEvent e) 
		{
			// TODO Auto-generated method stub
			JSlider source = (JSlider)e.getSource();
		    if (!source.getValueIsAdjusting()) 
		    {
		        int level = source.getValue();
		        
		        Visualiser.scale = Visualiser.scaleMin + (Visualiser.scaleMax - Visualiser.scaleMin)*level /100;
		        Window.surface.repaint();
		    }
		}
		
	}
	void updateTextArea()
	{
		if(!Visualiser.individuals.isEmpty() && Visualiser.selectedIndividual!=-1)
		{
		
			Individual individual = Visualiser.individuals.get(Visualiser.selectedIndividual);
			info.setText("");
			info.append("Individual : "+ Visualiser.names.get(Visualiser.selectedIndividual));
			info.append("\nRoute Time VIolation : "+ individual.totalRouteTimeViolation);
			info.append("\nTotal Load VIolation : "+ individual.totalLoadViolation);
			info.append("\nCost : "+ individual.cost);
			info.append("\nCost With Penalty : "+individual.costWithPenalty);
		}
	}
	private static final long serialVersionUID = 5188642461382060738L;
	
	private class RouteSelectionItemStateChanged implements ItemListener
	{

		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			JCheckBox cb = (JCheckBox)e.getItem();
			String text  = cb.getText();
			int route = new Integer(text.substring(6)).intValue();
			
			if(cb.isSelected())
			{
				if(!Surface.selectedVehicles.contains(route))
				{
					Surface.selectedVehicles.add(route);
				}
			}
			else
			{
				if(Surface.selectedVehicles.contains(route))
				{
					Surface.selectedVehicles.remove(new Integer(route));
				}

			}
			Window.surface.repaint();
		}
		
	}
	
	class PeriodComboActionListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			Surface.selectedPeriod = periodCombo.getSelectedIndex();
			Window.surface.repaint();
			
			//System.out.println(period+ " " +((JComboBox)e.getSource()).getSelectedItem());
		}
		
	}

	
	class IndividualComboActionListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			Visualiser.selectedIndividual = individualCombo.getSelectedIndex();
			updateTextArea();
			Window.surface.repaint();
			
			//System.out.println(period+ " " +((JComboBox)e.getSource()).getSelectedItem());
		}
		
	}


}

