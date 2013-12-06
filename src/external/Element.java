package external;

import java.awt.*;
import java.util.*;


import physics.*;

public class Element{
	
	private Vec2 position;
	
	private Vec2 velocity;
	
	private Vec2 force;
	
	private Vec2 latentForce;
	
	private ElementType type;
	
	private double invmass;
	
	protected ArrayList<Element> elements;
	
	protected ArrayList<Circle> circles;
	
	public double r;
	
	private UUID id;
	
	public Element(Vec2 p, Vec2 v, ElementType t, ArrayList<Element> elements, ArrayList<Circle> circles){
		setup(p,v,t,elements,circles);
	}
	
	public Element(Vec2 p, Vec2 v, ElementType t, ArrayList<Element> elements, double r){
		Circle c = new Circle(0,0, r);
		ArrayList<Circle> list = new ArrayList<Circle>();
		list.add(c);
		this.r = r;
		setup(p,v,t,elements,list);
	}
	
	private void setup(Vec2 p, Vec2 v, ElementType t, ArrayList<Element> elements, ArrayList<Circle> circles){
		this.position = p;
		
		this.velocity = v;
		
		this.force = new Vec2(0,0);
		this.latentForce = new Vec2(0,0);
		
		this.type = t;
		
		this.elements = elements;
		this.circles = circles;
		
		calculateMass();
		generateID();
	}
	
	private void generateID(){
		id = UUID.randomUUID();
	}
	
	protected UUID getID(){
		return id;
	}
	
	public void setX(double x) {
		position.setX(x);
	}
	
	public void setY(double y) {
		position.setY(y);
	}
	
	public void setVelocity(Vec2 v) {
		this.velocity = v;
	}
	
	public void setPosition(Vec2 p) {
		this.position = p;
	}
	public void setForce(Vec2 f){
		this.force = f;
	}
	public void addForce(Vec2 f){
		this.force = Vec2.add(force, f);
	}
	
	public void setLatentForce(Vec2 f){
		this.latentForce = f;
	}
	public void addLatentForce(Vec2 f){
		this.latentForce = Vec2.add(force, f);
	}
	
	public void setMass(double m){
		if(m != 0)
			this.invmass = 1/Math.abs(m);
		else
			this.invmass = 0;
	}
	
	public double getX() {
		return position.getX();
	}
	
	public double getY() {
		return position.getY();
	}
	
	public Vec2 getVelocity(){
		return velocity;
	}
	
	public Vec2 getPosition(){
		return position;
	}
	public Vec2 getForce(){
		return force;
	}
	
	public ElementType getType(){
		return type;
	}
	
	public ArrayList<Circle> getBounds(){
		return circles;
	}
	
	public double getMass(){
		return 1/invmass;
	}
	
	public double getInvMass(){
		return invmass;
	}
	
	private void calculateMass(){
		setMass(calculateArea() * getType().getDensity());
	}
	
	private double calculateArea(){
		return Math.PI * r * r;
		
		/*double total = 0;
		
		
		for(Circle c : circles){
			total += Math.PI * c.getR() * c.getR();
		}
		return total;*/
	}
	
	public void draw(Graphics2D g){
		g.setColor(getType().getColor());
		//for(Circle c : circles){
			circles.get(0).draw(getPosition(), g);
		//}
		
	}
	
	public void update(){
		
		step();
		
		/*ArrayList<Element> collidingWith = new ArrayList<Element>();
		
		for(Element e : elements){
			if(e != this){
				if(colliding(e)){
					collidingWith.add(e);
					Manifold m = new Manifold(this, e);
					Physics.resolveCollision(m);
					
				}
			}
		}*/
		
		/*if(collidingWith.size() == 1){
			Manifold m = new Manifold(this, collidingWith.get(0));
			Physics.resolveCollision(m);
		}else{
			
		}*/
		
		//setVelocity(Vec2.multiply(velocity, 0.9));
		
		
		
	}
	
	public boolean colliding(Element e){
		
		return Circle.intersecting(getPosition(), circles.get(0), e.getPosition(), e.circles.get(0));		
		
		/*for(Circle c : circles){
					for(Circle d : e.circles){
						if(Circle.intersecting(getPosition(), c, e.getPosition(), d)){
							return true;
						}
					}
				}
		
		return false;*/
	}
	
	public void step(){
		setVelocity(Vec2.add(getVelocity(), Vec2.multiply(force,getInvMass())));
		setPosition( Vec2.add(getPosition(), getVelocity()) );
		force.set(0,0);
		//latentForce.set(0, 0);
	}
	
	/*public boolean inContact(Element e){
		Circle Ac = null;
		Circle Bc = null;
		double dist;
		
		for(Circle c : A.getBounds()){
			for(Circle d : B.getBounds()){
				if(Circle.intersecting(A.getPosition(), c, B.getPosition(), d)){
					Ac = c;
					Bc = d;
				}
			}
		}
		
		double dx = A.getX() + Ac.getX() - (B.getX() + Bc.getX());
		double dy = A.getY() + Ac.getY() - (B.getY() + Bc.getY());
		
		double dist = Math.sqrt(dx*dx + dy*dy);
		
		penetrationDepth = Ac.getR() + Bc.getR() - dist;
	}*/
}