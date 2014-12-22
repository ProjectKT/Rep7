package plannerTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import planner.Operator;
import planner.Planner;
import plannerTest.Unifier;

//ベースはPlannerと同じ
public class PlannerOriginal {
	ArrayList<Operator> operators;
	ArrayList<Operator> plan;
	ArrayList<String> goalList;
	ArrayList<String> nowState;
	int timer;  //タイムタグを追加するたびに加算
	HashMap<Object,Integer> timeTag = new HashMap<Object,Integer>();  //タイムタグ

	public static void main(String argv[]) {
		(new PlannerOriginal()).start();
	}
	
	//初期、目標状態のないときのコンストラクタ
	PlannerOriginal() {
		timer = 0;
		goalList = initGoalList();
		nowState = initNowState();
		initOperators();
	}

	//初期、目標状態のあるときのコンストラクタ
	PlannerOriginal(ArrayList<String> goalList, ArrayList<String> startState) {
		timer = 0;
		this.goalList = goalList;
		this.nowState = startState;
		initOperators();
	}
	
	/**
	 * 目標状態を初期化
	 * @return
	 */
	private ArrayList<String> initGoalList() {
		ArrayList<String> goalList = new ArrayList<String>();
		goalList.add("B on C");
		goalList.add("A on B");
		return goalList;
	}
	
	/**
	 * 初期状態を初期化
	 * @return
	 */
	private ArrayList<String> initNowState() {
		ArrayList<String> initialState = new ArrayList<String>();
		initialState.add("clear A");
		initialState.add("clear B");
		initialState.add("clear C");

		initialState.add("ontable A");
		initialState.add("ontable B");
		initialState.add("ontable C");
		initialState.add("handEmpty");
		
		for(Object obj: initialState){
			System.out.println((String)obj);
			timeTag.put(obj, 0);
		}
		System.out.println(timeTag);
		timer++;
		
		return initialState;
	}
	
	/**
	 * オペレーターを初期化
	 */
	private void initOperators() {
		operators = new ArrayList<Operator>();

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
	}
	
	/**
	 * 現在状態をセット
	 * @param State
	 */
	public void setState(ArrayList<String> State){
		nowState.clear();
		for(String str: State){
			nowState.add(str);
		}
	}
	
	/**
	 * 目標状態をセット
	 * @param GoalList
	 */
	public void setGoal(ArrayList<String> GoalList){
		goalList.clear();
		for(String str : GoalList){
			goalList.add(str);
		}
	}
	
	public ArrayList<String> getNowState(){
		return nowState;
	}
	
	public ArrayList<String> getGoalList(){
		return goalList;
	}

	public ArrayList<Operator> getPlan(){
		return plan;
	}
	
	public void start(){
		HashMap<String,String> theBinding = new HashMap<String,String>();
		plan = new ArrayList<Operator>();
		planning(goalList, nowState, theBinding);

		System.out.println("***** This is a plan! *****");
		for (int i = 0; i < plan.size(); i++) {
			Operator op = (Operator) plan.get(i);
			System.out.println((op.instantiate(theBinding)).name);
		}
	}
	
	//現在の階層のゴールリストを前から解いていくメソッド
	private boolean planning(List<String> theGoalList, List<String> theCurrentState, HashMap<String,String> theBinding){
		System.out.println("*** GOALS ***" + theGoalList);
		if (theGoalList.size() == 1) {
			String aGoal = (String) theGoalList.get(0);
			if (planningAGoal(aGoal, theCurrentState, theBinding, 0) != -1) {
				return true;
			} else {
				return false;
			}
		} else {
			//先頭の条件について探索
			String aGoal = (String) theGoalList.get(0);
			int cPoint = 0;
			//四種のオペレータについて試す？
			while (cPoint < operators.size()) {
				// System.out.println("cPoint:"+cPoint);
				// Store original binding
				HashMap<String,String> orgBinding = new HashMap<String,String>();
				for (Iterator<String> it = theBinding.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					String value = (String) theBinding.get(key);
					orgBinding.put(key, value);
				}
				List<String> orgState = new ArrayList<String>();
				for (int i = 0; i < theCurrentState.size(); i++) {
					orgState.add(theCurrentState.get(i));
				}
				//ここまで失敗時に戻すためのデータを保存
				
				//先頭のゴールについて試す
				int tmpPoint = planningAGoal(aGoal, theCurrentState, theBinding, cPoint);
				// System.out.println("tmpPoint: "+tmpPoint);
				//失敗だとtmpPointが-1らしい
				if (tmpPoint != -1) {
					theGoalList.remove(0);
					System.out.println("チェック CurrentState");
					System.out.println(theCurrentState);
					System.out.println("チェック timeTag");
					System.out.println(timeTag);
					//ゴールリストの探索を次に進める
					if (planning(theGoalList, theCurrentState, theBinding)) {
						// System.out.println("Success !");
						return true;
					} else {//失敗したとき
						cPoint = tmpPoint;
						// System.out.println("Fail::"+cPoint);
						theGoalList.add(0, aGoal);

						theBinding.clear();
						for (Iterator<String> it = orgBinding.keySet().iterator(); it.hasNext();) {
							String key = (String) it.next();
							String value = (String) orgBinding.get(key);
							theBinding.put(key, value);
						}
						theCurrentState.clear();
						for (int i = 0; i < orgState.size(); i++) {
							theCurrentState.add(orgState.get(i));
							
						}
					}
				} else {
					//失敗なら状態を戻す処理
					theBinding.clear();
					for (Iterator<String> it = orgBinding.keySet().iterator(); it.hasNext();) {
						String key = (String) it.next();
						String value = (String) orgBinding.get(key);
						theBinding.put(key, value);
					}
					theCurrentState.clear();
					for (int i = 0; i < orgState.size(); i++) {
						theCurrentState.add(orgState.get(i));
					}
					return false;
				}
			}
			return false;
		}
	}
	
	//ゴールリストから取り出されたゴールの条件の一つについて戦略を選ぶメソッド
	private int planningAGoal(String theGoal, List<String> theCurrentState, HashMap<String,String> theBinding, int cPoint) {
		System.out.println("**" + theGoal);
		int size = theCurrentState.size();
		//もしワーキングメモリ内に欲しい条件がすでにあるなら
		for (int i = 0; i < size; i++) {
			String aState = (String) theCurrentState.get(i);
			if ((new Unifier()).unify(theGoal, aState, theBinding)) {
				System.out.println("unifier = 0");
				return 0;
			}
		}
		
		//ここでオペレーターを入れ替えることで競合解消
		
		
		for (int i = cPoint; i < operators.size(); i++) {
			Operator anOperator = rename((Operator) operators.get(i));

			
			//現在のBindingのバックアップ
			HashMap<String,String> orgBinding = new HashMap<String,String>();
			for (Iterator<String> it = theBinding.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				String value = (String) theBinding.get(key);
				orgBinding.put(key, value);
			}
			
			//現在のCurrent stateをバックアップ
			List<String> orgState = new ArrayList<String>();
			for (int j = 0; j < theCurrentState.size(); j++) {
				orgState.add(theCurrentState.get(j));
			}
			
			//現在のplanをバックアップ
			List<Operator> orgPlan = new ArrayList<Operator>();
			for (int j = 0; j < plan.size(); j++) {
				orgPlan.add(plan.get(j));
			}

			
			List<String> addList = (List<String>) anOperator.getAddList();
			for (int j = 0; j < addList.size(); j++) {
				if ((new Unifier()).unify(theGoal, (String) addList.get(j), theBinding)) {
					//オペレーターの変数を具体化
					Operator newOperator = anOperator.instantiate(theBinding);
					List<String> newGoals = (List<String>) newOperator.getIfList();
					System.out.println("新しいオペレーター");
					System.out.println(newOperator.name);
					if (planning(newGoals, theCurrentState, theBinding)) {
						System.out.println(newOperator.name);
						plan.add(newOperator);
						
						
						//現在の状態を遷移させる
						theCurrentState = newOperator
								.applyState(theCurrentState);
						return i + 1;
					} else {
						// 失敗したら元に戻す．
						theBinding.clear();
						for (Iterator<String> it = orgBinding.keySet().iterator(); it.hasNext();) {
							String key = (String) it.next();
							String value = (String) orgBinding.get(key);
							theBinding.put(key, value);
						}
						theCurrentState.clear();
						for (int k = 0; k < orgState.size(); k++) {
							theCurrentState.add(orgState.get(k));
						}
						plan.clear();
						for (int k = 0; k < orgPlan.size(); k++) {
							plan.add(orgPlan.get(k));
						}
					}
				}
			}
		}
		return -1;
	}
	
	int uniqueNum = 0;
	
	private Operator rename(Operator theOperator) {
		Operator newOperator = theOperator.getRenamedOperator(uniqueNum);
		//System.out.println("!!!"+newOperator);
		uniqueNum = uniqueNum + 1;
		return newOperator;
	}
}
