package plannerTest;

import java.util.ArrayList;
import java.util.Iterator;

public class JointJ {
	private ArrayList<Node> forwards = new ArrayList<Node>();
	private Node back;
	
	public void addforward(Node forward){
		forwards.add(forward);
	}
	
	public void changeback(Node back){
		this.back = back;
	}
	
	public void removeforward(Object forward){
		forwards.remove(forward);
	}
	
	public ArrayList<Node> getforward(){
		return forwards;
	}
	
	public Node getback(){
		return back;
	}
}
