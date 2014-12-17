package planner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class Planner {
	ArrayList<Operator> operators;
	Random rand;
	ArrayList<Operator> plan;

	public static void main(String argv[]) {
		(new Planner()).start();
	}

	Planner() {
		rand = new Random();
	}

	public void start() {
		initOperators();
		ArrayList<String> goalList = initGoalList();
		ArrayList<String> initialState = initInitialState();

		HashMap<String,String> theBinding = new HashMap<String,String>();
		plan = new ArrayList<Operator>();
		planning(goalList, initialState, theBinding);

		System.out.println("***** This is a plan! *****");
		for (int i = 0; i < plan.size(); i++) {
			Operator op = (Operator) plan.get(i);
			System.out.println((op.instantiate(theBinding)).name);
		}
	}

	private boolean planning(List<String> theGoalList, List<String> theCurrentState, HashMap<String,String> theBinding) {
		System.out.println("*** GOALS ***" + theGoalList);
		if (theGoalList.size() == 1) {
			String aGoal = (String) theGoalList.get(0);
			if (planningAGoal(aGoal, theCurrentState, theBinding, 0) != -1) {
				return true;
			} else {
				return false;
			}
		} else {
			String aGoal = (String) theGoalList.get(0);
			int cPoint = 0;
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

				int tmpPoint = planningAGoal(aGoal, theCurrentState, theBinding, cPoint);
				// System.out.println("tmpPoint: "+tmpPoint);
				if (tmpPoint != -1) {
					theGoalList.remove(0);
					System.out.println(theCurrentState);
					if (planning(theGoalList, theCurrentState, theBinding)) {
						// System.out.println("Success !");
						return true;
					} else {
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

	private int planningAGoal(String theGoal, List<String> theCurrentState, HashMap<String,String> theBinding, int cPoint) {
		System.out.println("**" + theGoal);
		int size = theCurrentState.size();
		for (int i = 0; i < size; i++) {
			String aState = (String) theCurrentState.get(i);
			if ((new Unifier()).unify(theGoal, aState, theBinding)) {
				return 0;
			}
		}

		int randInt = Math.abs(rand.nextInt()) % operators.size();
		Operator op = (Operator) operators.get(randInt);
		operators.remove(randInt);
		operators.add(op);

		for (int i = cPoint; i < operators.size(); i++) {
			Operator anOperator = rename((Operator) operators.get(i));
			// 現在のCurrent state, Binding, planをbackup
			HashMap<String,String> orgBinding = new HashMap<String,String>();
			for (Iterator<String> it = theBinding.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				String value = (String) theBinding.get(key);
				orgBinding.put(key, value);
			}
			List<String> orgState = new ArrayList<String>();
			for (int j = 0; j < theCurrentState.size(); j++) {
				orgState.add(theCurrentState.get(j));
			}
			List<Operator> orgPlan = new ArrayList<Operator>();
			for (int j = 0; j < plan.size(); j++) {
				orgPlan.add(plan.get(j));
			}

			List<String> addList = (List<String>) anOperator.getAddList();
			for (int j = 0; j < addList.size(); j++) {
				if ((new Unifier()).unify(theGoal, (String) addList.get(j), theBinding)) {
					Operator newOperator = anOperator.instantiate(theBinding);
					List<String> newGoals = (List<String>) newOperator.getIfList();
					System.out.println(newOperator.name);
					if (planning(newGoals, theCurrentState, theBinding)) {
						System.out.println(newOperator.name);
						plan.add(newOperator);
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
		uniqueNum = uniqueNum + 1;
		return newOperator;
	}

	private ArrayList<String> initGoalList() {
		ArrayList<String> goalList = new ArrayList<String>();
		goalList.add("B on C");
		goalList.add("A on B");
		return goalList;
	}

	private ArrayList<String> initInitialState() {
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
}

class Unifier {
	StringTokenizer st1;
	String buffer1[];
	StringTokenizer st2;
	String buffer2[];
	HashMap<String,String> vars;

	Unifier() {
		// vars = new Hashtable();
	}

	public boolean unify(String string1, String string2, HashMap<String,String> theBindings) {
		HashMap<String,String> orgBindings = new HashMap<String,String>();
		for (Iterator<String> it = theBindings.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			String value = (String) theBindings.get(key);
			orgBindings.put(key, value);
		}
		this.vars = theBindings;
		if (unify(string1, string2)) {
			return true;
		} else {
			// 失敗したら元に戻す．
			theBindings.clear();
			for (Iterator<String> it = orgBindings.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				String value = (String) orgBindings.get(key);
				theBindings.put(key, value);
			}
			return false;
		}
	}

	public boolean unify(String string1, String string2) {
		// 同じなら成功
		if (string1.equals(string2))
			return true;

		// 各々トークンに分ける
		st1 = new StringTokenizer(string1);
		st2 = new StringTokenizer(string2);

		// 数が異なったら失敗
		if (st1.countTokens() != st2.countTokens())
			return false;

		// 定数同士
		int length = st1.countTokens();
		buffer1 = new String[length];
		buffer2 = new String[length];
		for (int i = 0; i < length; i++) {
			buffer1[i] = st1.nextToken();
			buffer2[i] = st2.nextToken();
		}

		// 初期値としてバインディングが与えられていたら
		if (this.vars.size() != 0) {
			for (Iterator<String> it = vars.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				String value = (String) vars.get(key);
				replaceBuffer(key, value);
			}
		}

		for (int i = 0; i < length; i++) {
			if (!tokenMatching(buffer1[i], buffer2[i])) {
				return false;
			}
		}

		return true;
	}

	boolean tokenMatching(String token1, String token2) {
		if (token1.equals(token2))
			return true;
		if (var(token1) && !var(token2))
			return varMatching(token1, token2);
		if (!var(token1) && var(token2))
			return varMatching(token2, token1);
		if (var(token1) && var(token2))
			return varMatching(token1, token2);
		return false;
	}

	boolean varMatching(String vartoken, String token) {
		if (vars.containsKey(vartoken)) {
			if (token.equals(vars.get(vartoken))) {
				return true;
			} else {
				return false;
			}
		} else {
			replaceBuffer(vartoken, token);
			if (vars.containsValue(vartoken)) {
				replaceBindings(vartoken, token);
			}
			vars.put(vartoken, token);
		}
		return true;
	}

	void replaceBuffer(String preString, String postString) {
		for (int i = 0; i < buffer1.length; i++) {
			if (preString.equals(buffer1[i])) {
				buffer1[i] = postString;
			}
			if (preString.equals(buffer2[i])) {
				buffer2[i] = postString;
			}
		}
	}

	void replaceBindings(String preString, String postString) {
		for (Iterator<String> it = vars.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			if (preString.equals(vars.get(key))) {
				vars.put(key, postString);
			}
		}
	}

	boolean var(String str1) {
		// 先頭が ? なら変数
		return str1.startsWith("?");
	}

}
