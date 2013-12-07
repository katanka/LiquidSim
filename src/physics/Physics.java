package physics;



public class Physics {

	
	private static final double G = 0.01; //gravitational constant
	private static final double K = 5; //spring constant
	public static final double waterDist = 2; //spring constant
	public static final double rockDist = 100; //spring constant
	public static final double F_MAX = 100; //spring constant
	
	
	
	public static Vec2 getGravityForce(Manifold m){
		double distance = m.distance();
		
		if(distance == 0){
			return new Vec2(0,0);
		}
		
		double strength = G * m.A.getMass() * m.B.getMass() / distance / distance;
			
		return Vec2.multiply(m.normal(), strength);
	}
	
	public static void applyGravity(Manifold m){
		
		Vec2 F_ab = getGravityForce(m);
			
		m.A.addForce(F_ab);
			
		Vec2 F_ba = Vec2.multiply(F_ab, -1);
			
		m.B.addForce(F_ba);
		
		//normalForce(m, F_ab);
	}
	
	public static void applySpring(Manifold m){
		
		double dist = m.distance();
		
		double springDist = m.A.r + m.B.r;
		
		if(dist > springDist /*|| springDist == 2*waterDist*/){
			return;
		}
		
		
		double x = springDist - dist;
		
		double strength = Math.max(K * x * -1, F_MAX*-1);
		
		if(Vec2.dotProduct( m.relativeVelocity(), m.normal() ) > 0 /*&& !(m.A.getType() == ElementType.ROCK && m.B.getType() == ElementType.ROCK)*/){
			//System.err.println("separating");
			strength *= 0.1;
		}
		
		if(m.A.getType() == ElementType.ROCK && m.B.getType() == ElementType.ROCK){
			strength *= 6000;
		}
		
		
		Vec2 F_ab = Vec2.multiply(m.normal(), strength);
		Vec2 F_ba = Vec2.multiply(F_ab, -1);
		
			
		m.A.addForce(F_ab);
		m.B.addForce(F_ba);
		
		
	}
	
	
	//Deprecated, still nice code though
	public static Vec2 getImpulse(Manifold m){
		// Calculate relative velocity
		  Vec2 rv = m.relativeVelocity();
		  
		  
		  // Calculate relative velocity in terms of the normal direction
		  double velAlongNormal = Vec2.dotProduct( rv, m.normal() );
		 
		  // Do not resolve if velocities are separating
		  if(velAlongNormal > 0)
		    return new Vec2(0,0);
		  
		  // Calculate restitution
		  double e = m.restitution();
		 
		  // Calculate impulse scalar
		  double j = -(1 + e) * velAlongNormal;
		  j /= m.A.getInvMass() + m.B.getInvMass();
		 
		  // Apply impulse
		  return Vec2.multiply(m.normal(), j);
	}
	
}
