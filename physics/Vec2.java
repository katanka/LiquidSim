package physics;

public class Vec2 {
	
	private double x;
	private double y;
	
	public Vec2(Vec2 v){
		this(v.getX(), v.getY());
	}
	
	public Vec2( double x, double y ){
		setX(x);
		setY(y);
	}
	
	public void set(double x, double y){
		setX(x);
		setY(y);
	}
	
	public void set(Vec2 v){
		setX(v.x);
		setY(v.y);
	}
	
	public void setX(double x) {
		this.x = (double)x;
	}
	
	public void setY(double y) {
		this.y = (double)y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double length(){
		return Math.sqrt(x*x + y*y);
	}
	
	public double theta(){
		return (double)Math.atan2(y, x);
	}
	
	public Vec2 unitVector(){
		return Vec2.divide(this, length());
	}
	
	public static Vec2 divide(Vec2 v, double a){
		return new Vec2(v.getX()/a, v.getY()/a);
	}
	
	public static Vec2 multiply(Vec2 v, double a){
		return new Vec2(v.getX()*a, v.getY()*a);
	}
	
	public static Vec2 subtract(Vec2 a, Vec2 b){
		return new Vec2(a.getX()-b.getX(), a.getY() - b.getY());
	}
	
	public static Vec2 add(Vec2 a, Vec2 b){
		return new Vec2(a.getX()+b.getX(), a.getY() + b.getY());
	}
	
	public static double dotProduct(Vec2 a, Vec2 b){
		return a.getX()*b.getX() + a.getY() * b.getY();
	}
	
	public Vec2 invert(){
		return new Vec2(x*-1, y*-1);
	}
	
	public String toString(){
		return getX() + "i + " + getY()+ "j";
	}
}
