package plannerTest;

import java.util.ArrayList;

//NOAHでのプラン、複数のスレッドで共有する
public class NOAHPlan {
	private ArrayList<String> plan;
	
	NOAHPlan(){
		plan = new ArrayList<String>();
	}
	
	void addPlan(String planElement){
		plan.add(planElement);
	}
	
	void removePlan(String planElement){
		plan.remove(planElement);
	}
}
