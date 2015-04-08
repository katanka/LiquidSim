package physics;

import java.awt.Color;



public enum ElementType {
	WATER(Color.BLUE, 0.5, 1), //elastic
	ROCK(Color.GRAY, 0, 5); //inelastic
	
	private Color color;
	private double restitution;
	private double density;
	
	private ElementType(Color c, double r, double d){
		this.color = c;
		this.restitution = r;
		this.density = d;
	}
	
	public Color getColor(){
		return color;
	}
	
	public double getRestitution(){
		return restitution;
	}
	
	public double getDensity(){
		return density;
	}
}
