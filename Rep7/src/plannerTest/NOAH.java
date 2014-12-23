package plannerTest;

import java.util.ArrayList;
import java.util.List;

import planner.Operator;

public class NOAH {
	
	NOAHParameters nPara;
	NOAHPlan nPlan;
	
	public static void main(String args[]){
		(new NOAH()).planning();
	}
	
	//初期状態、目標状態の指定がない場合のコンストラクタ
	NOAH(){
		nPara = new NOAHParameters(initOperators(),initGoalState(),initCurrentState());
		nPlan = new NOAHPlan();
	}
	
	//初期状態、目標状態が指定されている場合のコンストラクタ
	NOAH(ArrayList<String> goalState, ArrayList<String> initialState){
		nPara = new NOAHParameters(initOperators(),goalState,initialState);
		nPlan = new NOAHPlan();
	}
	
	/**
	 * オペレーターを初期化
	 */
	private ArrayList<Operator> initOperators() {
		ArrayList<Operator> operators = new ArrayList<Operator>();

		// OPERATOR 1
		// / NAME
		String name1 = new String("Place ?x on ?y");
		// / IF
		ArrayList<String> ifList1 = new ArrayList<String>();
		ifList1.add(new String("clear ?y"));
		ifList1.add(new String("holding ?x"));
		// / ADD-LIST
		ArrayList<String> addList1 = new ArrayList<String>();
		addList1.add(new String("?x on ?y"));
		addList1.add(new String("clear ?x"));
		addList1.add(new String("handEmpty"));
		// / DELETE-LIST
		ArrayList<String> deleteList1 = new ArrayList<String>();
		deleteList1.add(new String("clear ?y"));
		deleteList1.add(new String("holding ?x"));
		Operator operator1 = new Operator(name1, ifList1, addList1, deleteList1);
		operators.add(operator1);

		// OPERATOR 2
		// / NAME
		String name2 = new String("remove ?x from on top ?y");
		// / IF
		List<String> ifList2 = new ArrayList<String>();
		ifList2.add(new String("?x on ?y"));
		ifList2.add(new String("clear ?x"));
		ifList2.add(new String("handEmpty"));
		// / ADD-LIST
		ArrayList<String> addList2 = new ArrayList<String>();
		addList2.add(new String("clear ?y"));
		addList2.add(new String("holding ?x"));
		// / DELETE-LIST
		ArrayList<String> deleteList2 = new ArrayList<String>();
		deleteList2.add(new String("?x on ?y"));
		deleteList2.add(new String("clear ?x"));
		deleteList2.add(new String("handEmpty"));
		Operator operator2 = new Operator(name2, ifList2, addList2, deleteList2);
		operators.add(operator2);

		// OPERATOR 3
		// / NAME
		String name3 = new String("pick up ?x from the table");
		// / IF
		ArrayList<String> ifList3 = new ArrayList<String>();
		ifList3.add(new String("ontable ?x"));
		ifList3.add(new String("clear ?x"));
		ifList3.add(new String("handEmpty"));
		// / ADD-LIST
		ArrayList<String> addList3 = new ArrayList<String>();
		addList3.add(new String("holding ?x"));
		// / DELETE-LIST
		ArrayList<String> deleteList3 = new ArrayList<String>();
		deleteList3.add(new String("ontable ?x"));
		deleteList3.add(new String("clear ?x"));
		deleteList3.add(new String("handEmpty"));
		Operator operator3 = new Operator(name3, ifList3, addList3, deleteList3);
		operators.add(operator3);

		// OPERATOR 4
		// / NAME
		String name4 = new String("put ?x down on the table");
		// / IF
		ArrayList<String> ifList4 = new ArrayList<String>();
		ifList4.add(new String("holding ?x"));
		// / ADD-LIST
		ArrayList<String> addList4 = new ArrayList<String>();
		addList4.add(new String("ontable ?x"));
		addList4.add(new String("clear ?x"));
		addList4.add(new String("handEmpty"));
		// / DELETE-LIST
		ArrayList<String> deleteList4 = new ArrayList<String>();
		deleteList4.add(new String("holding ?x"));
		Operator operator4 = new Operator(name4, ifList4, addList4, deleteList4);
		operators.add(operator4);
		
		return operators;
	}
	
	/**
	 * 目標状態を初期化
	 * @return
	 */
	private ArrayList<String> initGoalState() {
		ArrayList<String> goalList = new ArrayList<String>();
		goalList.add("B on C");
		goalList.add("A on B");
		return goalList;
	}
	
	/**
	 * 初期状態を初期化
	 * @return
	 */
	private ArrayList<String> initCurrentState() {
		ArrayList<String> initialState = new ArrayList<String>();
		initialState.add("clear A");
		initialState.add("clear B");
		initialState.add("clear C");

		initialState.add("ontable A");
		initialState.add("ontable B");
		initialState.add("ontable C");
		initialState.add("handEmpty");
		
		return initialState;
	}
	
	/**
	 * 現在状態をセット
	 * @param State
	 */
	public void setCurrentState(ArrayList<String> State){
		nPara.setCurrentState(State);
	}
	
	/**
	 * 目標状態をセット
	 * @param GoalList
	 */
	public void setGoalState(ArrayList<String> GoalList){
		nPara.setGoalState(GoalList);
	}
	
	public ArrayList<String> getCurrentState(){
		return nPara.getCurrentState();
	}
	
	public ArrayList<String> getGoalState(){
		return nPara.getGoalState();
	}
	
	/**
	 * 現在状態から目標状態への道筋をNOAH(Nets Of Action Hierarchies)で求める
	 */
	public void planning(){
		ArrayList<String> goalState = nPara.getGoalState();
		
		//与えられたゴール状態の数だけの対応するThreadを作る
		NOAHThread[] nt = new NOAHThread[goalState.size()];
		Thread[] t = new Thread[goalState.size()];
		
		int count = 0;
		for(String str: goalState){
			nPlan.addPlan(str);
			nt[count] = new NOAHThread(str,nPara,nPlan);
			t[count] = new Thread(nt[count]);
			count++;
		}
		
		for(int i = 0; i<count; i++){
			t[i].start();
		}
	}
}
