package physics;

import external.*;

public class Manifold {
	public Element A;
	public Element B;
	
	
	public double penetrationDepth;
	
	public Manifold(Element A, Element B){
		this.A = A;
		this.B = B;
		
		if(A.colliding(B)){
		
			Circle Ac = null;
			Circle Bc = null;
			
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
		
		}
	}
	
	public double distance(){
		double dx = A.getX() - B.getX();
		double dy = A.getY() - B.getY();
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	public Vec2 normal(){

		Vec2 difference = Vec2.subtract(B.getPosition(), A.getPosition());
		
		return difference.unitVector();
	}
	
	public Vec2 relativeVelocity(){
		return Vec2.subtract(B.getVelocity(), A.getVelocity());
	}
	
	public double restitution(){
		return Math.min( A.getType().getRestitution(), B.getType().getRestitution());
	}
}
