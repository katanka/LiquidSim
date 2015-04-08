import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.*;

import physics.*;
import external.Element;

 
//@SuppressWarnings("serial")
public class LiquidSim extends JPanel implements Runnable{
	private static int fps = 60;
	private static int dt;
	private static int WIDTH;
	private static int HEIGHT;
	private final String folder = "imagefiles";
	public static final File savedir = new File(new File(System.getProperty("user.home")), ".liquidsim");
	private static int bufferSize = 10;
	private static int buffertick = 0;
	private static int written = 0;
	private static int numsmall = 100;
	private static double time=5; //seconds
	
	
	private int frame;
	private int tick;
	private boolean complete;
	private boolean playing;
	private boolean buffering;
	private long timebase;
	private int currentfps;
	
	
	public static LiquidSim me;
	
	public static JFrame parentWindow;
	
	public static JPanel form;
	public static JTextField timeInput;
	public static JTextField numInput;
	
	private static ArrayDeque<BufferedImage> buffer;
	
	private static ExecutorService es = Executors.newCachedThreadPool();
	
	private static int totalframes = 1;
	
	private static HashMap<Integer, Element> hash;
	
	public static void main(String[] args) throws IOException
    {
		
		
		parentWindow = new JFrame("Liquid Simulation by Andrew Gleeson");
    	
		WIDTH = 1000;
    	HEIGHT = 700;
		
    	parentWindow.setResizable(false);
        parentWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        form = new JPanel();
        
        
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
		
		numsmall = getInteger(numInput.getText());
		hash = new HashMap<Integer, Element>(numsmall+50);
		time = getDouble(timeInput.getText());
		
		totalframes = (int)(fps * time);
		
		dt = 1000/fps;
		
		
		//simulate
    	parentWindow.setSize(500, 100);
    	parentWindow.remove(form);
        
        me = new LiquidSim();

    	
    	parentWindow.getContentPane().add(me);
        parentWindow.setLocationRelativeTo(null);
        me.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        
        
	}
	
	public static ArrayList<Element> elements = new ArrayList<Element>();
	

	public LiquidSim(){
		

		
		int i = 2;

		

		while( i < numsmall+2){
			double x = 900;
			double y = 350;


			double r = Math.random()*50 + Physics.rockDist+Physics.waterDist;
			double theta = Math.random()*2*Math.PI;

			x += r * Math.cos(theta);
			y += r * Math.sin(theta);
			
			Element e = new Element(new Vec2(x,y), new Vec2(0,0), ElementType.WATER, elements, Physics.waterDist);
			elements.add(e);
			hash.put(i, e);
			
			i++;
		}

		elements.add(new Element(new Vec2(900,350), new Vec2(0,0), ElementType.ROCK, elements, Physics.rockDist));
		
		hash.put(0, elements.get(i-2));

		elements.add(new Element(new Vec2(100,350), new Vec2(0,0), ElementType.ROCK, elements, 50));
		
		hash.put(1, elements.get(i-1));
		
		
		//elements.add(new Element(new Vec2(500,500), new Vec2(0,2), ElementType.ROCK, elements, 40));
		//hash.put(1, elements.get(i));
		setFocusable(true);
		
		buffer = new ArrayDeque<BufferedImage>(bufferSize);
		
		new Thread(this).start();
	}
	
	 public static void update(){
		 	
		 	for(int i = 0; i < hash.size(); i++){
		 			
				for(int j = i+1; j< hash.size(); j++){
			    	Physics.applyGravity(new Manifold(hash.get(i), hash.get(j)));
			    	Physics.applySpring(new Manifold(hash.get(i), hash.get(j)));
			    }

	    		
	    		hash.get(i).update();
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
            
            
        }
        
        es.shutdown();
        try{
        	while(!es.awaitTermination(1, TimeUnit.MINUTES)){}
	    }catch(InterruptedException e){
	        	
	    }
        
        
        tick = bufferSize;
        playing = true;
        
        buffering = true;
        parentWindow.setSize(WIDTH, HEIGHT);
        parentWindow.setLocation(0, 0);
        while(buffertick < bufferSize){
        	
        	buffer.add(getImageFromFile(buffertick));
        	repaint();
        	buffertick++;
        }
        buffering = false;
        while(playing){
        	try{
        		buffer.add(getImageFromFile(tick));
        	}catch(Exception e){
        		
        	}
        	repaint();
            tick++;
            

            
            if(tick > totalframes){
            	tick=0;
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
    	File f = new File(savedir, "\\"+folder+"\\"+((num<totalframes)?num:(totalframes-1))+".png");
    	
    	return loadImageCrazyFast(f);
    }
    
    /**
     * This is a bit of a hack and might change depending on which jdk you use!
     */
    public static BufferedImage loadImageCrazyFast( File src ){
        try{
            Image im = Toolkit.getDefaultToolkit().createImage( src.toURI().toURL() );
            Method method = im.getClass().getMethod( "getBufferedImage" );
            BufferedImage bim = null;
            int counter = 0;
            // load 30seconds maximum!
            while( bim == null && counter < 3000 ){
                im.getWidth( null );
                bim = (BufferedImage) method.invoke( im );
                try{ Thread.sleep( 10 ); }
                catch( InterruptedException e ){ }
                counter ++;
            }
           
            if( bim != null ){
                return bim;
            }
        }
        catch( Exception e ){
            System.err.println( "Fast loading of " + src.toString() + " failed. You might want to correct this in loadImageCrazyFast( URL )" );
            System.err.println( "Falling back to ImageIO, which is... slow!" );
        }
        try{
            return ImageIO.read( src );
        }
        catch( IOException ioe ){
            return null;
        }
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
		es.execute( new Thread() {
			    public void run() {

			        	write(fin, folder, ftick);
			        	written++;
			    }  
			});
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
			f.fillRect(1, 1, (int)(percent*(parentWindow.getWidth()-1)), parentWindow.getContentPane().getHeight()/2-2);
			
			percent = written/(double)totalframes;
			
			f.setColor(Color.blue);
			f.fillRect(1, parentWindow.getContentPane().getHeight()/2+1, (int)(percent*(parentWindow.getWidth()-1)), parentWindow.getContentPane().getHeight()/2-2);
			
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
}