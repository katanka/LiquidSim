package physics;



public class Physics {

	
	private static final double G = 0.01; //gravitational constant
	private static final double K = 5; //spring constant
	public static final double waterDist = 2; //spring constant
	public static final double rockDist = 100; //spring constant
	public static final double F_MAX = 100; //spring constant
	
	private static final double percent = 0.5; // usually 20% to 80%
	private static final double slop = 0.1; // usually 0.01 to 0.1
	
	private static final double friction = 0.99;
	
	public static Vec2 getGravityForce(Manifold m){
		/*if(m.A.getType() == m.B.getType() && willCollide(m) && m.relativeVelocity().length() < 10){
			return new Vec2(0,0);
		}*/
		
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
		
		/*if(m.A.getType() == ElementType.ROCK){
			springDist += rockDist;
		}else{
			springDist += waterDist;
		}
		
		if(m.B.getType() == ElementType.ROCK){
			springDist += rockDist;
		}else{
			springDist += waterDist;
		}
		*/
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
		
		//System.out.println(strength);
		
		
		Vec2 F_ab = Vec2.multiply(m.normal(), strength);
		Vec2 F_ba = Vec2.multiply(F_ab, -1);
		
		/*if(m.A.getType() == ElementType.ROCK && m.B.getType() == ElementType.ROCK){
			m.A.addForce(F_ab);
			m.B.addForce(F_ba);
		}else if(m.A.getType() == ElementType.WATER && m.B.getType() == ElementType.WATER){
			m.A.addForce(F_ab);
			m.B.addForce(F_ba);
		}else if(m.A.getType() == ElementType.ROCK && m.B.getType() == ElementType.WATER){

			m.B.addForce(F_ba);
		}else if(m.A.getType() == ElementType.WATER && m.B.getType() == ElementType.ROCK){
			m.A.addForce(F_ab);
		}*/
			
		m.A.addForce(F_ab);
		m.B.addForce(F_ba);
		
		
		
		//resolvePosition(m);
	}
	
	public static void normalForce(Manifold m, Vec2 force){
		
		
		
		if(willCollide(m)){
			m.B.setForce(new Vec2(0,0));
			m.A.setForce(new Vec2(0,0));
		}
	}
	
	public static void friction(Manifold m, Vec2 impulse){
		if(!m.A.colliding(m.B)){
			return;
		}
		
		Vec2 tangent = null;
		
		double value = Vec2.dotProduct(m.A.getVelocity(), m.normal());
		//if normal is up, velocity is right
		if(value < 0){
			tangent = new Vec2(m.normal().getY(), m.normal().getX() * -1);
		}else if(value > 0){
			tangent = new Vec2(m.normal().getY()*-1, m.normal().getX());
		}else{
			tangent = new Vec2(0,0);
		}
		
		//tangent = Vec2.multiply(tangent, impulse.length());
		
		double friction = Vec2.dotProduct(m.relativeVelocity(), tangent) * -1;
		
		friction /= m.A.getInvMass() + m.B.getInvMass();
		
		Vec2 frictionImpulse = new Vec2(0,0);
		
		frictionImpulse = Vec2.multiply(tangent,friction);
		
		m.A.addForce(frictionImpulse);
		m.B.addForce(frictionImpulse.invert());
	}
	
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
	
	public static void resolveCollision( Manifold m )
	{
	  
		
		
	
	  
	  Vec2 impulse = getImpulse(m);
	  
	  //m.A.setVelocity( Vec2.subtract(m.A.getVelocity(), Vec2.multiply(impulse, m.A.getInvMass()) ) );
	  
	  //m.B.setVelocity( Vec2.add(m.B.getVelocity(), Vec2.multiply(impulse, m.B.getInvMass()) ) );
	  
	  m.A.addForce(Vec2.multiply(impulse, -1));
	  m.B.addForce(impulse);
	  friction(m, impulse);
	  
	  resolvePosition(m);
	  
	  //m.A.setForce(new Vec2(0,0));
	  //m.A.setForce(new Vec2(0,0));
	  
	  //if(Vec2.dotProduct( m.relativeVelocity(), m.normal() ) < 0){
	  //	 m.A.addLatentForce(Vec2.multiply(getGravityForce(m), -1));
		//  m.B.addLatentForce(getGravityForce(m));
	  //}
	}
	
	public static void resolvePosition( Manifold m ){
		
		double scalar = Math.max(m.penetrationDepth - slop, 0)/(m.A.getInvMass() + m.B.getInvMass()) * percent;
		  Vec2 correction = Vec2.multiply(m.normal(), scalar);
		  
		  m.A.setPosition( Vec2.subtract(m.A.getPosition(), Vec2.multiply(correction, m.A.getInvMass())) );
		  m.B.setPosition( Vec2.add(m.B.getPosition(), Vec2.multiply(correction, m.B.getInvMass())) );
	}
	
	public static boolean willCollide(Manifold m){
		Vec2 Apos = new Vec2(m.A.getPosition());
		Vec2 Avel = new Vec2(m.A.getVelocity());
		Vec2 Aforce = new Vec2(m.A.getForce());
		
		Vec2 Bpos = new Vec2(m.B.getPosition());
		Vec2 Bvel = new Vec2(m.B.getVelocity());
		Vec2 Bforce = new Vec2(m.B.getForce());
		
		m.A.step();
		m.B.step();
		
		
		boolean colliding = m.A.colliding(m.B);
		
		m.A.setPosition(Apos);
		m.A.setVelocity(Avel);
		m.A.setForce(Aforce);
		
		m.B.setPosition(Bpos);
		m.B.setVelocity(Bvel);
		m.B.setForce(Bforce);
		
		return colliding;
	}
	
}
