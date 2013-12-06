package external;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import physics.*;

 
@SuppressWarnings("serial")
public class SimPanel extends JPanel implements Runnable, KeyListener {
	private static int fps = 30;
	private static int dt;
	private static int WIDTH;
	private static int HEIGHT;
	private final String folder = "imagefiles";
	public static final File savedir = new File(new File(System.getProperty("user.home")), ".liquidsim");
	private static int bufferSize = 10;
	private static int buffertick = 0;
	private static int numsmall = 100;
	private static double time=5; //seconds
	
	
	private int frame;
	private int tick;
	private boolean complete;
	private boolean playing;
	private boolean buffering;
	private long timebase;
	private int currentfps;
	
	public static SimPanel me;
	
	public static JFrame parentWindow;
	
	public static JPanel form;
	public static JTextField framesInput;
	public static JTextField timeInput;
	public static JTextField numInput;
	
	private static ArrayDeque<BufferedImage> buffer;
	
	private static int totalframes = 1;
	
	public static void main(String[] args) throws IOException
    {

		parentWindow = new JFrame("Liquid Simulation v0");
    	
		WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    	HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
		
    	parentWindow.setResizable(false);
        parentWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        form = new JPanel();
        
        framesInput = new JTextField(3);
        JLabel framesLabel = new JLabel("Frames per Second: ");
        
        timeInput = new JTextField(3);
        JLabel timeLabel = new JLabel("Time to Simulate (s): ");
        
        numInput = new JTextField(3);
        JLabel numLabel = new JLabel("Number of Particles: ");
        
        JButton submit = new JButton("Submit");
        
        submit.addActionListener(
                new ActionListener(){
                    public void actionPerformed(
                            ActionEvent e){
                                            finish();
                                          }
                                    }
                            );



        form.add(framesLabel);
        form.add(framesInput);
        
        form.add(timeLabel);
        form.add(timeInput);
        
        form.add(numLabel);
        form.add(numInput);
        form.add(submit);
    	
        parentWindow.add(form);
        
        
        
        parentWindow.pack();
        parentWindow.setLocationRelativeTo(null);
        parentWindow.setVisible(true);
        
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                
            	
            	
                	purgeDirectory(savedir);
                   System.exit(0);
            }
        };
        parentWindow.addWindowListener(exitListener);
        
    	
       
        
    }
	
	private static void purgeDirectory(File dir) {
	    for (File file: dir.listFiles()) {
	        if (file.isDirectory()) purgeDirectory(file);
	        file.delete();
	    }
	}
	
	public static int getInteger(String s) {
		int num = 0;
	    try { 
	        num = Integer.parseInt(s); 
	    } catch(NumberFormatException e) {
	    	System.exit(-1);
	        //return 0; 
	        
	    }
	    // only got here if we didn't return false
	    return num;
	}
	
	public static double getDouble(String s) {
		double num = 0;
	    try { 
	        num = Double.parseDouble(s); 
	    } catch(NumberFormatException e) {
	    	System.exit(-1);
	        //return 0; 
	        
	    }
	    // only got here if we didn't return false
	    return num;
	}
	
	public static void finish(){
		
		fps = getInteger(framesInput.getText());
		numsmall = getInteger(numInput.getText());
		time = getDouble(timeInput.getText());
		
		totalframes = (int)(fps * time);
		
		dt = 1000/fps;
		
		//bufferSize = Math.min(totalframes, 500);
		
		//simulate
    	parentWindow.setSize(500, 100);
    	parentWindow.remove(form);
        
        me = new SimPanel();

    	
    	parentWindow.getContentPane().add(me);
        parentWindow.setLocationRelativeTo(null);
        me.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        
        
	}
	
	public ArrayList<Element> elements = new ArrayList<Element>();
	

	public SimPanel(){
		

		
		//elements.add(new Element(new Vec2(0,50), new Vec2(0,0), ElementType.ROCK, elements, 20));
		//elements.add(new Element(new Vec2(100,50), new Vec2(0,0), ElementType.WATER, elements, 20));
		//elements.add(new Element(new Vec2(250,40), new Vec2(0,0), ElementType.ROCK, elements, 20));
		//elements.add(new Element(new Vec2(100,250), new Vec2(1,0), ElementType.ROCK, elements, 20));
		elements.add(new Element(new Vec2(1000,500), new Vec2(0,0), ElementType.ROCK, elements, Physics.rockDist));
		
		
		for(int i = 0; i < 0; i++){
			double x = Math.random() * 1000+500;
			double y = Math.random() * 800+200;
			
			
			elements.add(new Element(new Vec2(x,y), new Vec2(0,0), ElementType.ROCK, elements, Physics.rockDist));
		}
		
		for(int i = 0; i < numsmall; i++){
			double x = Math.random() * 2000;
			double y = Math.random() * 1000;
			
			
			elements.add(new Element(new Vec2(x,y), new Vec2(0,0), ElementType.WATER, elements, Physics.waterDist));
		}
		

		setFocusable(true);
		addKeyListener(this);
		System.out.println(totalframes);
		
		buffer = new ArrayDeque<BufferedImage>(bufferSize);
		
		new Thread(this).start();
	}
	
	 public void update(){
	    
		 if(tick == 0){
			 //System.err.println("YEAH");
			 elements.add(new Element(new Vec2(700,500), new Vec2(0,2), ElementType.ROCK, elements, 40));
			 //added = true;
		 }
		 
	    	for(int i = 0; i < elements.size(); i++){
	    		
	    		
	    		for(int j = i+1; j< elements.size(); j++){
	    			Physics.applyGravity(new Manifold(elements.get(i), elements.get(j)));
	    			Physics.applySpring(new Manifold(elements.get(i), elements.get(j)));
	    		}
	    		
	    		elements.get(i).update();
	    	}

			
	    }
	
    public void run() {
    	// Remember the starting time
    	long tm = System.currentTimeMillis();
    	
    	
        while(!complete){
        	if(tick == totalframes/* && !complete*/){
        		//System.out.println("BING");
        		tick = 0;
        		complete = true;
        		
        	}
            
        	update();
            drawImg();
            repaint();
            
            getFPS();
            frame++;
            tick++;
            
            /*try {
                tm += dt;
                Thread.sleep(!complete ? 0 : Math.max(0, tm - System.currentTimeMillis()));
            }
            catch(InterruptedException e)
            {
            	System.err.println(e);
            }*/
        }
        tick = bufferSize;
        playing = true;
        
        buffering = true;
        parentWindow.setSize(1920, 1080);
        parentWindow.setLocation(0, 0);
        while(buffertick < bufferSize){
        	if(getImageFromFile(buffertick) == null){
        		//System.err.println("HOLY BALLS: "+i);
        		System.exit(0);
        	}
        	
        	buffer.add(getImageFromFile(buffertick));
        	repaint();
        	 buffertick++;
        }
        buffering = false;
        while(playing){
        	try{
        		//if(buffer.size() < bufferSize)
        			buffer.add(getImageFromFile(tick));
        	}catch(Exception e){
        		
        	}
        	repaint();
            tick++;
            //System.out.println("olo");
            

            
            if(tick > totalframes){
            	tick=0;
            	//System.out.println("Yolo");
            	//playing = false;
            }
            try {
                tm += dt;
                Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
            }
            catch(InterruptedException e)
            {
            	System.err.println(e);
            }
        }
    }
    
    private void getFPS(){
    	long time = System.currentTimeMillis();
    	if(time - timebase > 1000){
    		
        	currentfps = (int) ( (long)frame*1000/(time - timebase) );
        	timebase = time;
        	frame = 0;
        }
        
    }
	
    private BufferedImage getImageFromFile(int num){
    	BufferedImage img = null;
    	try {
    	    img = ImageIO.read(new File(savedir, "\\"+folder+"\\"+((num<totalframes)?num:(totalframes-1))+".png"));
    	} catch (IOException e) {
    	}
    	
    	return img;
    }
    
    private Vec2 getCenterOfMass(ArrayList<Element> elements){
    	
    	if(elements.size() == 0)
    		return new Vec2(0,0);
    	
    	double mass = 0;
    	double Mx = 0;
    	double My = 0;
    	
    	for(Element e : elements){
    		double m = e.getMass();
    		mass += m;
    		Mx += m * e.getX();
    		My += m * e.getY();
    	}
    	
    	return new Vec2(Mx/mass, My/mass);
    }
    
    private void drawImg(){
    	BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)bi.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
		        RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		
		
		
		g.fillRect(0, 0, WIDTH, HEIGHT);

		for(int i = 0; i < elements.size(); i++){
			elements.get(i).draw(g);
		}
		
		final BufferedImage fin = bi.getSubimage(0, 0, WIDTH, HEIGHT);
		final int ftick = tick;
		//System.out.println("tick = "+ftick);
		Thread one = new Thread() {
			    public void run() {

			        	write(fin, folder, ftick);


			    }  
			};
		try{
			one.start();
			one.join();
		}catch(InterruptedException e){
			System.err.println(e);
		}
    }
    
	public void paint(Graphics f1){
		super.paint(f1);
		
		Graphics2D f = (Graphics2D)f1;
		f.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
		        RenderingHints.VALUE_ANTIALIAS_ON);
		
		if(!complete){
			
			f.setFont(new Font("Lucida Sans", Font.BOLD, (int)(parentWindow.getContentPane().getHeight()*0.75))); 
			
			String s = new DecimalFormat("00.00").format(tick*1.0/fps) + "/" + totalframes/fps + " sec";
			FontMetrics fm   = f.getFontMetrics(f.getFont());
			
			java.awt.geom.Rectangle2D rect = fm.getStringBounds(s, f);
			
			int textHeight = (int)(rect.getHeight()); 
			int textWidth  = (int)(rect.getWidth());
			int panelHeight= parentWindow.getContentPane().getHeight();
			int panelWidth = parentWindow.getContentPane().getWidth();
			
			int x = (panelWidth  - textWidth)  / 2;
			int y = (panelHeight - textHeight) / 2  + fm.getAscent();
			
			//progress
			double percent = tick/(double)totalframes;
			f.setColor(Color.black);
			f.fillRect(0, 0, parentWindow.getWidth(), parentWindow.getHeight());
			f.setColor(Color.red);
			f.fillRect(1, 1, (int)(percent*(parentWindow.getWidth()-1)), parentWindow.getContentPane().getHeight());
			
			f.setColor(Color.WHITE);
			

			
			f.drawString(s, x, y);
			
		}else if(buffering){
			
			double percent = buffertick/(double)bufferSize;
			
			int x = parentWindow.getWidth() / 2 - 100;
			int y = parentWindow.getHeight() / 2 - 10;
			
			int width = 200;
			int height = 20;
			
			f.setColor(Color.black);
			f.fillRect(x, y, width, height);
			f.setColor(Color.blue);
			f.fillRect(x+1, y+1, (int)(percent*(width-2)), height-2);
			
		}else if (playing){
			//if(tick == totalframes) return;
			//System.out.println(tick);
			f.drawImage(buffer.pollFirst(), 0, 0, WIDTH, HEIGHT, null);
		}
	
		
		
	}
	
	private void write(BufferedImage bi, String folder, int ftick){
		File f;
		try{
			f = new File(savedir, "\\"+folder+"\\"+ftick+".png");
			f.mkdirs();
			f.createNewFile();
			ImageIO.write(bi, "png", f);
		}catch(IOException e){
			System.err.println(e);
		}

	}
	
	public void keyPressed(KeyEvent e) {

	    //int key = e.getKeyCode();

	
	}
	
	public void keyReleased(KeyEvent e) {

	    //int key = e.getKeyCode();
	    
	    
	}
	
	public void keyTyped(KeyEvent e) {

	}
}