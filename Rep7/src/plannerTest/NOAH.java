package plannerTest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import planner.Operator;

public class NOAH {

	NOAHParameters nPara;
	NOAHPlan nPlan;

	ArrayList<Node> plan = new ArrayList<Node>();
	int nodecount = 1;
	// ノードの結合情報
	ArrayList<JointS> ss = new ArrayList<JointS>();
	ArrayList<JointJ> js = new ArrayList<JointJ>();

	ArrayList<String> under = new ArrayList<String>();

	public static void main(String args[]) {
		(new NOAH()).initialplanning();
	}

	// 初期状態、目標状態の指定がない場合のコンストラクタ
	NOAH() {
		nPara = new NOAHParameters(initOperators(), initGoalState(),
				initCurrentState());
		nPlan = new NOAHPlan();
	}

	// 初期状態、目標状態が指定されている場合のコンストラクタ
	NOAH(ArrayList<String> goalState, ArrayList<String> initialState) {
		nPara = new NOAHParameters(initOperators(), goalState, initialState);
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
	 * 
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
	 * 
	 * @return
	 */
	private ArrayList<String> initCurrentState() {
		ArrayList<String> initialState = new ArrayList<String>();
		initialState.add("clear B");
		initialState.add("clear C");

		initialState.add("C on A");
		return initialState;
	}

	/**
	 * 現在状態をセット
	 * 
	 * @param State
	 */
	public void setCurrentState(ArrayList<String> State) {
		nPara.setCurrentState(State);
	}

	/**
	 * 目標状態をセット
	 * 
	 * @param GoalList
	 */
	public void setGoalState(ArrayList<String> GoalList) {
		nPara.setGoalState(GoalList);
	}

	public ArrayList<String> getCurrentState() {
		return nPara.getCurrentState();
	}

	public ArrayList<String> getGoalState() {
		return nPara.getGoalState();
	}

	/**
	 * 現在状態から目標状態への道筋をNOAH(Nets Of Action Hierarchies)で求める
	 * 
	 */
	public void initialplanning() {
		ArrayList<String> goalState = nPara.getGoalState();

		// スタート、ゴールの登録
		JointS s = new JointS();
		s.changeforward("Start");
		JointJ j = new JointJ();
		Node goal = new Node("Goal", 0, j, null);
		j.changeback(goal);
		// ノードの登録
		for (String str : goalState) {
			Node newNode = new Node(str, nodecount++, s, j);
			plan.add(newNode);
			s.addback(newNode);
			j.addforward(newNode);
		}
		ss.add(s);
		js.add(j);
		planning();

	}

	/**
	 * プランを展開する
	 */
	public void expandplan() {
		ArrayList<Node> newplan = new ArrayList<Node>();
		for (Node node : plan) {
			if (node.getNodeName().contains("on")) {
				JointS s = new JointS();
				s.changeforward(node.getForward());
				ss.add(s);
				JointJ j = new JointJ();
				js.add(j);

				ss.get(0).removeback(node);
				ss.get(0).addback(s);

				js.get(0).removeforward(node);

				Pattern p = Pattern.compile("(.*) on (.*)");
				Matcher m = p.matcher(node.getNodeName());
				if (m.find()) {
					under.add(m.group(2));
					Node newNode1 = new Node("Clear " + m.group(1),
							nodecount++, s, j);
					s.addback(newNode1);
					j.addforward(newNode1);
					Node newNode2 = new Node("Clear " + m.group(2),
							nodecount++, s, j);
					s.addback(newNode2);
					j.addforward(newNode2);
					Node newNode3 = new Node("Place " + m.group(1) + " on "
							+ m.group(2), nodecount++, j, node.getBack());
					j.changeback(newNode3);
					js.get(0).addforward(newNode3);

					newplan.add(newNode1);
					newplan.add(newNode2);
					newplan.add(newNode3);
				}

			}
		}
		plan.clear();
		plan = newplan;
	}

	Node headNode;
	
	/**
	 * 干渉を探し順序付けを行う
	 */
	public void checkInterference() {
		System.out.println(under);
		// 今回注目する干渉
		JointJ j = js.get(0);
		ArrayList<Node> list = j.getforward();
		ArrayList<Node> subPlan = new ArrayList<Node>();
		Pattern p = Pattern.compile("Place (.*) on (.*)");
		System.out.println("syu");
		while (subPlan.size() != list.size()) {
			System.out.println("subPlan"+subPlan.size()+"list"+list.size());
			for (Node node : list) {
				Matcher m = p.matcher(node.getNodeName());
				if (m.find()) {
					if (!under.contains(m.group(1))) {
						under.remove(m.group(2));
						
						if(!subPlan.contains(node)){
						subPlan.add(0, node);
						}
					}
				}
			}

		}
		
		headNode = subPlan.get(0);
		
		for(int i = 0; i < subPlan.size() - 1; i++){
				subPlan.get(i).changeback(subPlan.get(i+1).getForward());
				((JointJ)subPlan.get(i+1).getForward()).addforward(subPlan.get(i));
		}

			subPlan.get(subPlan.size() - 1).changeback(j.getback());
			j.getback().changeforward(subPlan.get(subPlan.size() - 1));
			js.remove(0);
	}

	/**
	 * 冗長を探しグラフの変形を行う
	 */
	public void checkLengthy() {

		ArrayList<String> clearList = new ArrayList<String>();
		Node node = headNode;
		
		
		System.out.println("check do \n");
		do{
			System.out.println("node : " + node);
			
			System.out.println("clearList : "+clearList);
			
			Node clearNode = ((JointJ) node.getForward()).getforward().get(1);
			System.out.println("clearNode? :" +clearNode);
			if(clearList.contains(clearNode.getNodeName())){
				System.out.println("in  clearNode");
				((JointJ)clearNode.getBack()).removeforward(clearNode);
				//((JointJ)( (JointJ) node.getForward() ).getforward().get(1).getBack()).removeforward(( (JointJ) node.getForward() ).getforward().get(1));
				Object x = clearNode.getForward();
				if (x instanceof JointS) {
					System.out.println("success JointS\n" + x);
					JointS jointS = (JointS) x;
					
					jointS.removeback(clearNode);
				} else {
					
					System.out.println("not JointS \n"+x);
					// JointS じゃない
				}
			}
			clearList.add(( (JointJ) node.getForward() ).getforward().get(0).getNodeName());
			System.out.println(node.getBack().getClass().toString());
			if(node.getBack().getClass().toString().equals("class plannerTest.Node")){
				System.out.println("goalNode :"+node.getBack());
				System.out.println(node.getBack().toString());
				break;
			}else{
			node = ((JointJ) node.getBack()).getback();
			}
			
			System.out.println("");
		}while(true);
		
		System.out.println("finish checklengthy");
	}

	/**
	 * 干渉の残数ｊと冗長の総数sを無くす
	 */
	public void planning() {
		expandplan();
		
		System.out.println("initialState \n");
		printState();

		checkInterference();
		printState();
		checkLengthy();
		
		System.out.println("\nresult \n");
		printState();
	}

	public void printState() {
		for (JointS joint : ss) {
			
			System.out.println("center :"+ joint);
			
			System.out.println("forward : "+joint.getforward());

			Iterator it = joint.getback();
			while (it.hasNext()) {
				System.out.println("back : " + it.next());
			}
			System.out.println("");
		}

		for (JointJ joint2 : js) {
			
			System.out.println("joint2 " + joint2);
			
			Iterator it = joint2.getforward().iterator();
			while (it.hasNext()) {
				System.out.println("forward : "+it.next());
			}

			System.out.println("back : " +joint2.getback());

			System.out.println("");
		}

		for (Node node : plan) {
			System.out
					.println(node.getNodeName() + "  " + node.getNodeNumber());
			System.out.println("me"+node+"\nforward "+node.getForward()+"\nback "+node.getBack());
		}
	}

	/**
	 * 現在状態から目標状態への道筋をNOAH(Nets Of Action Hierarchies)で求める
	 * multithreadを使おうとしたがよくわからなくなったので放棄
	 */
	public void planningMultiThread() {
		ArrayList<String> goalState = nPara.getGoalState();

		// 与えられたゴール状態の数だけの対応するThreadを作る
		NOAHThread[] nt = new NOAHThread[goalState.size()];
		Thread[] t = new Thread[goalState.size()];

		int count = 0;
		for (String str : goalState) {
			nPlan.addPlan(str);
			nt[count] = new NOAHThread(str, nPara, nPlan);
			t[count] = new Thread(nt[count]);
			count++;
		}

		for (int i = 0; i < count; i++) {
			t[i].start();
		}

		for (int i = 0; i < count; i++) {
			try {
				t[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
