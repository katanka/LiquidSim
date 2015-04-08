package external;

import java.awt.*;
import java.util.*;


import physics.*;

public class Element{
	
	private Vec2 position;
	
	private Vec2 velocity;
	
	private Vec2 force;
	
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
	}
	
	public void draw(Graphics2D g){
		g.setColor(getType().getColor());

		circles.get(0).draw(getPosition(), g);

		
	}
	
	public void update(){
		
		step();
	}
	
	public boolean colliding(Element e){
		
		return Circle.intersecting(getPosition(), circles.get(0), e.getPosition(), e.circles.get(0));		
		
	}
	
	public void step(){
		setVelocity(Vec2.add(getVelocity(), Vec2.multiply(force,getInvMass())));
		setPosition( Vec2.add(getPosition(), getVelocity()) );
		force.set(0,0);
	}
	
}