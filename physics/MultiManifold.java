package physics;

import java.util.ArrayList;

import external.*;

public class MultiManifold {
	
	Element primary;
	
	ArrayList<Element> others;
	
	public MultiManifold(Element primary, ArrayList<Element> others){
		this.primary = primary;
		this.others = others;
	}
	
	public void resolve(){
		
	}
	
}
