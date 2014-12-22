package planner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Planner {
	ArrayList<Operator> operators;
	ArrayList<Object> plan;

	public static void main(String argv[]) {
		(new Planner()).start();
	}

	Planner() {

	}

	public void start() {
		initOperators();
		ArrayList<Object> goalList = initGoalList();
		ArrayList<Object> initialState = initInitialState();

		HashMap<Object, Object> theBinding = new HashMap<Object, Object>();
		plan = new ArrayList<Object>();
		planning(goalList, initialState, theBinding);

		System.out.println("***** This is a plan! *****");
		for (int i = 0; i < plan.size(); i++) {
			Operator op = (Operator) plan.get(i);
			System.out.println((op.instantiate(theBinding)).name);
		}
	}

	private boolean planning(List<Object> theGoalList,
			List<Object> theCurrentState, HashMap<Object, Object> theBinding) {
		System.out.println("*** GOALS ***!!" + theGoalList);
		// ゴールまたはサブゴールを処理
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
				HashMap<Object, Object> orgBinding = new HashMap<Object, Object>();
				for (Iterator<Object> it = theBinding.keySet().iterator(); it
						.hasNext();) {
					String key = (String) it.next();
					String value = (String) theBinding.get(key);
					orgBinding.put(key, value);
				}
				List<Object> orgState = new ArrayList<Object>();
				for (int i = 0; i < theCurrentState.size(); i++) {
					orgState.add(theCurrentState.get(i));
				}

				int tmpPoint = planningAGoal(aGoal, theCurrentState,
						theBinding, cPoint);
				// System.out.println("tmpPoint: "+tmpPoint);
				if (tmpPoint != -1) {
					theGoalList.remove(0);
					System.out.println("チェック CurrentState");
					System.out.println(theCurrentState);
					if (planning(theGoalList, theCurrentState, theBinding)) {
						// System.out.println("Success !");
						return true;
					} else {// 失敗したとき
						cPoint = tmpPoint;
						// System.out.println("Fail::"+cPoint);
						theGoalList.add(0, aGoal);

						theBinding.clear();
						for (Iterator<Object> it = orgBinding.keySet()
								.iterator(); it.hasNext();) {
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
					for (Iterator<Object> it = orgBinding.keySet().iterator(); it
							.hasNext();) {
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

	private int planningAGoal(String theGoal, List<Object> theCurrentState,
			HashMap<Object, Object> theBinding, int cPoint) {
		System.out.println("**??" + theGoal);
		int size = theCurrentState.size();
		// 全部ユニファイできたらおｋ
		for (int i = 0; i < size; i++) {
			String aState = (String) theCurrentState.get(i);
			if ((new Unifier()).unify(theGoal, aState, theBinding)) {
				return 0;
			}
		}

		for (int i = 0; i < operators.size(); i++) {
			System.out.println(operators.get(i).getPriority());
		}
		// ここで優先度でオペレータをソート
		Collections.sort(operators, new PriComparator());

		for (int i = cPoint; i < operators.size(); i++) {
			Operator anOperator = rename((Operator) operators.get(i));
			// オペレータ使用の際優先度を更新
			operators.get(i).incrementPriority();
			// 現在のCurrent state, Binding, planをbackup
			HashMap<Object, Object> orgBinding = new HashMap<Object, Object>();
			for (Iterator<Object> it = theBinding.keySet().iterator(); it
					.hasNext();) {
				String key = (String) it.next();
				String value = (String) theBinding.get(key);
				orgBinding.put(key, value);
			}
			List<Object> orgState = new ArrayList<Object>();
			for (int j = 0; j < theCurrentState.size(); j++) {
				orgState.add(theCurrentState.get(j));
			}
			List<Object> orgPlan = new ArrayList<Object>();
			for (int j = 0; j < plan.size(); j++) {
				orgPlan.add(plan.get(j));
			}


			// オペレータのaddlistを見る
			List<Object> addList = (List<Object>) anOperator.getAddList();
				for (int j = 0; j < addList.size(); j++) {

					if ((new Unifier()).unify(theGoal, (String) addList.get(j),
							theBinding)) {
						Operator newOperator = anOperator
								.instantiate(theBinding);
						List<Object> newGoals = (List<Object>) newOperator
								.getIfList();
						System.out.println("新しいオペレーター");
						System.out.println(newOperator.name);
						if (planning(newGoals, theCurrentState, theBinding)) {
							System.out.println(newOperator.name);
							plan.add(newOperator);

							System.out.println("theBinding" + theBinding);
							System.out.println("theCurrentState"
									+ theCurrentState);
							// 現在の状態を遷移させる
							theCurrentState = newOperator.applyState(
									theCurrentState, theBinding);
							System.out.println("theCurrentState"
									+ theCurrentState);

							return i + 1;
						} else {
							System.out.println("失敗");
							// 失敗したら元に戻す．
							theBinding.clear();
							for (Iterator<Object> it = orgBinding.keySet()
									.iterator(); it.hasNext();) {
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

	private ArrayList<Object> initGoalList() {
		ArrayList<Object> goalList = new ArrayList<Object>();
		goalList.add("A on B");
		return goalList;
	}

	private ArrayList<Object> initInitialState() {
		ArrayList<Object> initialState = new ArrayList<Object>();
		initialState.add("C on A");
		initialState.add("clear B");
		initialState.add("clear C");

		initialState.add("ontable A");
		initialState.add("ontable B");
		initialState.add("handEmpty");

		return initialState;
	}

	private void initOperators() {
		operators = new ArrayList<Operator>();

		// OPERATOR 1
		// / NAME
		String name1 = new String("Place ?x on ?y");
		// / IF
		ArrayList<Object> ifList1 = new ArrayList<Object>();
		ifList1.add(new String("clear ?y"));
		ifList1.add(new String("holding ?x"));
		// / ADD-LIST
		ArrayList<Object> addList1 = new ArrayList<Object>();
		addList1.add(new String("?x on ?y"));
		addList1.add(new String("clear ?x"));
		addList1.add(new String("handEmpty"));
		// / DELETE-LIST
		ArrayList<Object> deleteList1 = new ArrayList<Object>();
		deleteList1.add(new String("clear ?y"));
		deleteList1.add(new String("holding ?x"));
		Operator operator1 = new Operator(name1, ifList1, addList1, deleteList1);
		operators.add(operator1);

		// OPERATOR 2
		// / NAME
		String name2 = new String("remove ?x from on top ?y");
		// / IF
		List<Object> ifList2 = new ArrayList<Object>();
		ifList2.add(new String("?x on ?y"));
		ifList2.add(new String("clear ?x"));
		ifList2.add(new String("handEmpty"));
		// / ADD-LIST
		ArrayList<Object> addList2 = new ArrayList<Object>();
		addList2.add(new String("clear ?y"));
		addList2.add(new String("holding ?x"));
		// / DELETE-LIST
		ArrayList<Object> deleteList2 = new ArrayList<Object>();
		deleteList2.add(new String("?x on ?y"));
		deleteList2.add(new String("clear ?x"));
		deleteList2.add(new String("handEmpty"));
		Operator operator2 = new Operator(name2, ifList2, addList2, deleteList2);
		operators.add(operator2);

		// OPERATOR 3
		// / NAME
		String name3 = new String("pick up ?x from the table");
		// / IF
		ArrayList<Object> ifList3 = new ArrayList<Object>();
		ifList3.add(new String("ontable ?x"));
		ifList3.add(new String("clear ?x"));
		ifList3.add(new String("handEmpty"));
		// / ADD-LIST
		ArrayList<Object> addList3 = new ArrayList<Object>();
		addList3.add(new String("holding ?x"));
		// / DELETE-LIST
		ArrayList<Object> deleteList3 = new ArrayList<Object>();
		deleteList3.add(new String("ontable ?x"));
		deleteList3.add(new String("clear ?x"));
		deleteList3.add(new String("handEmpty"));
		Operator operator3 = new Operator(name3, ifList3, addList3, deleteList3);
		operators.add(operator3);

		// OPERATOR 4
		// / NAME
		String name4 = new String("put ?x down on the table");
		// / IF
		ArrayList<Object> ifList4 = new ArrayList<Object>();
		ifList4.add(new String("holding ?x"));
		// / ADD-LIST
		ArrayList<Object> addList4 = new ArrayList<Object>();
		addList4.add(new String("ontable ?x"));
		addList4.add(new String("clear ?x"));
		addList4.add(new String("handEmpty"));
		// / DELETE-LIST
		ArrayList<Object> deleteList4 = new ArrayList<Object>();
		deleteList4.add(new String("holding ?x"));
		Operator operator4 = new Operator(name4, ifList4, addList4, deleteList4);
		operators.add(operator4);
	}

}

class Operator {
	String name;
	List<Object> ifList;
	List<Object> addList;
	List<Object> deleteList;
	private int Priority;

	Operator(String theName, List<Object> theIfList, List<Object> theAddList,
			List<Object> theDeleteList) {
		name = theName;
		ifList = theIfList;
		addList = theAddList;
		deleteList = theDeleteList;
		Priority = 0;
	}

	public List<Object> getAddList() {
		return addList;
	}

	public List<Object> getDeleteList() {
		return deleteList;
	}

	public List<Object> getIfList() {
		return ifList;
	}

	public void incrementPriority() {
		Priority++;
	}

	public int getPriority() {
		return Priority;
	}

	public void resetPriority() {
		Priority = 0;
	}

	public String toString() {
		String result = "NAME: " + name + "\n" + "IF :" + ifList + "\n"
				+ "ADD:" + addList + "\n" + "DELETE:" + deleteList;
		return result;
	}

	public List<Object> applyState(List<Object> theState,
			HashMap<Object, Object> theBinding) {

		for (int i = 0; i < addList.size(); i++) {
			if (addList.get(i).toString().contains("?")) {
				theState.add(instantiateString((String) addList.get(i),
						theBinding));
			} else {
				theState.add(addList.get(i));
			}
			System.out.println("add : " + addList.get(i));
		}
		for (int i = 0; i < deleteList.size(); i++) {
			if (deleteList.get(i).toString().contains("?")) {
				theState.remove(instantiateString((String) deleteList.get(i),
						theBinding));
			} else {
				theState.remove(deleteList.get(i));
			}
			System.out.println("delete : " + deleteList.get(i));
		}

		return theState;
	}

	public Operator getRenamedOperator(int uniqueNum) {
		List<Object> vars = new ArrayList<Object>();
		// IfListの変数を集める
		for (int i = 0; i < ifList.size(); i++) {
			String anIf = (String) ifList.get(i);
			vars = getVars(anIf, vars);
		}
		// addListの変数を集める
		for (int i = 0; i < addList.size(); i++) {
			String anAdd = (String) addList.get(i);
			vars = getVars(anAdd, vars);
		}
		// deleteListの変数を集める
		for (int i = 0; i < deleteList.size(); i++) {
			String aDelete = (String) deleteList.get(i);
			vars = getVars(aDelete, vars);
		}
		Hashtable renamedVarsTable = makeRenamedVarsTable(vars, uniqueNum);

		// 新しいIfListを作る
		List<Object> newIfList = new ArrayList<Object>();
		for (int i = 0; i < ifList.size(); i++) {
			String newAnIf = renameVars((String) ifList.get(i),
					renamedVarsTable);
			newIfList.add(newAnIf);
		}
		// 新しいaddListを作る
		List<Object> newAddList = new ArrayList<Object>();
		for (int i = 0; i < addList.size(); i++) {
			String newAnAdd = renameVars((String) addList.get(i),
					renamedVarsTable);
			newAddList.add(newAnAdd);
		}
		// 新しいdeleteListを作る
		List<Object> newDeleteList = new ArrayList<Object>();
		for (int i = 0; i < deleteList.size(); i++) {
			String newADelete = renameVars((String) deleteList.get(i),
					renamedVarsTable);
			newDeleteList.add(newADelete);
		}
		// 新しいnameを作る
		String newName = renameVars(name, renamedVarsTable);

		return new Operator(newName, newIfList, newAddList, newDeleteList);
	}

	private List<Object> getVars(String thePattern, List<Object> vars) {
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				vars.add(tmp);
			}
		}
		return vars;
	}

	private Hashtable makeRenamedVarsTable(List<Object> vars, int uniqueNum) {
		Hashtable result = new Hashtable();
		for (int i = 0; i < vars.size(); i++) {
			String newVar = (String) vars.get(i) + uniqueNum;
			result.put((String) vars.get(i), newVar);
		}
		return result;
	}

	private String renameVars(String thePattern, Hashtable renamedVarsTable) {
		String result = new String();
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				result = result + " " + (String) renamedVarsTable.get(tmp);
			} else {
				result = result + " " + tmp;
			}
		}
		return result.trim();
	}

	public Operator instantiate(HashMap<Object, Object> theBinding) {
		// name を具体化
		String newName = instantiateString(name, theBinding);
		// ifList を具体化
		List<Object> newIfList = new ArrayList<Object>();
		for (int i = 0; i < ifList.size(); i++) {
			String newIf = instantiateString((String) ifList.get(i), theBinding);
			newIfList.add(newIf);
		}
		// addList を具体化
		List<Object> newAddList = new ArrayList<Object>();
		for (int i = 0; i < addList.size(); i++) {
			String newAdd = instantiateString((String) addList.get(i),
					theBinding);
			newAddList.add(newAdd);
		}
		// deleteListを具体化
		List<Object> newDeleteList = new ArrayList<Object>();
		for (int i = 0; i < deleteList.size(); i++) {
			String newDelete = instantiateString((String) deleteList.get(i),
					theBinding);
			newDeleteList.add(newDelete);
		}
		return new Operator(newName, newIfList, newAddList, newDeleteList);
	}

	private String instantiateString(String thePattern,
			HashMap<Object, Object> theBinding) {
		String result = new String();
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				String newString = (String) theBinding.get(tmp);
				if (newString == null) {
					result = result + " " + tmp;
				} else {
					result = result + " " + newString;
				}
			} else {
				result = result + " " + tmp;
			}
		}
		return result.trim();
	}

	private boolean var(String str1) {
		// 先頭が ? なら変数
		return str1.startsWith("?");
	}
}

class Unifier {
	StringTokenizer st1;
	String buffer1[];
	StringTokenizer st2;
	String buffer2[];
	HashMap<Object, Object> vars;

	Unifier() {
		vars = new HashMap<Object, Object>();
	}

	public boolean unify(String string1, String string2,
			HashMap<Object, Object> theBindings) {
		HashMap<Object, Object> orgBindings = new HashMap<Object, Object>();
		for (Iterator<Object> it = theBindings.keySet().iterator(); it
				.hasNext();) {
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
			for (Iterator<Object> it = orgBindings.keySet().iterator(); it
					.hasNext();) {
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
			for (Iterator<Object> it = vars.keySet().iterator(); it.hasNext();) {
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
		for (Iterator<Object> it = vars.keySet().iterator(); it.hasNext();) {
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

// Comparator実装クラス
class PriComparator implements Comparator<Operator> {
	public static final int ASC = 1; // 昇順
	public static final int DESC = -1; // 降順
	private int sort = ASC; // デフォルトは昇順

	public PriComparator() {

	}

	/**
	 * @param sort
	 *            StringComparator.ASC | StringComparator.DESC。昇順や降順を指定します。
	 */
	public PriComparator(int sort) {
		this.sort = sort;
	}

	public int compare(Operator arg0, Operator arg1) {

		if (arg0.getPriority() == arg1.getPriority()) {
			return 0; // arg0 = arg1
		} else if (arg0.getPriority() > arg1.getPriority()) {
			return 1 * sort; // arg1 > arg2
		} else {
			return -1 * sort; // arg1 < arg2
		}

	}

}
