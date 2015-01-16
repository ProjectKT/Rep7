package plannerTest;

import java.util.ArrayList;
import java.util.Iterator;

public class JointS {
	private Object forward;
	private ArrayList<Object> backs = new ArrayList<Object>();
	
	public void changeforward(Object forward){
		this.forward = forward;
	}
	
	public void addback(Object back){
		backs.add(back);
	}

	public void removeback(Object back){
		backs.remove(back);
	}
	
	
	public Object getforward(){
		return forward;
	}
	
	public ArrayList<Object> getback(){
		return backs;
	}
}
