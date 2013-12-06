package physics;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Circle {
	
	
	private Vec2 position;
	
	private double r;
	
	public Circle(double x, double y, double r){
		this(new Vec2(x, y), r);
		
	}
	
	public Circle(Vec2 p, double r){
		setPosition(p);
		setR(r);
	}
	
	public boolean intersecting(Circle c){
		
		double dx = getX() - c.getX();
		double dy = getY() - c.getY();
		
		double dist = Math.sqrt(dx*dx + dy*dy);
		
		return dist < (r + c.getR());
	}
	
	public static boolean intersecting(Vec2 p1, Circle a, Vec2 p2, Circle b){
		double dx = p1.getX() + a.getX() - (p2.getX() + b.getX());
		double dy = p1.getY() + a.getY() - (p2.getY() + b.getY());
		
		double dist = Math.sqrt(dx*dx + dy*dy);
		
		return dist < (a.r + b.getR());
	}
	
	public void setX(double x) {
		position.setX(x);
	}
	
	public void setY(double y) {
		position.setY(y);
	}
	
	
	public void setPosition(Vec2 p) {
		this.position = p;
	}
	
	public double getX() {
		return position.getX();
	}
	
	public double getY() {
		return position.getY();
	}
		
	public Vec2 getPosition(){
		return position;
	}
	
	public void setR(double rad){
		this.r = Math.abs(rad);
	}
	public double getR() {
		return r;
	}

	public void draw(Vec2 parent, Graphics2D g){
		Ellipse2D.Double shape = new Ellipse2D.Double((parent.getX() + getX()-getR()), (parent.getY() + getY()-getR()), (2*getR()), (2*getR()));
		g.fill(shape);
	}
}
