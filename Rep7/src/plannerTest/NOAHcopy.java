package plannerTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import planner.Operator;

public class NOAHcopy {

	NOAHParameters nPara;
	NOAHPlan nPlan;

	ArrayList<Node> plan = new ArrayList<Node>();
	int nodecount = 1;
	// 繝弱�繝峨�邨仙粋諠�ｱ
	ArrayList<JointS> ss = new ArrayList<JointS>();
	ArrayList<JointJ> js = new ArrayList<JointJ>();

	ArrayList<String> under = new ArrayList<String>();
	ArrayList<String> over = new ArrayList<String>();

	public static void main(String args[]) {
		(new NOAHcopy()).initialplanning();
	}

	// 蛻晄悄迥ｶ諷九∫岼讓咏憾諷九�謖�ｮ壹′縺ｪ縺�ｴ蜷医�繧ｳ繝ｳ繧ｹ繝医Λ繧ｯ繧ｿ
	NOAHcopy() {
		nPara = new NOAHParameters(initOperators(), initGoalState(),
				initCurrentState());
		nPlan = new NOAHPlan();
	}

	// 蛻晄悄迥ｶ諷九∫岼讓咏憾諷九′謖�ｮ壹＆繧後※縺�ｋ蝣ｴ蜷医�繧ｳ繝ｳ繧ｹ繝医Λ繧ｯ繧ｿ
	NOAHcopy(ArrayList<String> goalState, ArrayList<String> initialState) {
		nPara = new NOAHParameters(initOperators(), goalState, initialState);
		nPlan = new NOAHPlan();
	}

	/**
	 * 繧ｪ繝壹Ξ繝ｼ繧ｿ繝ｼ繧貞�譛溷喧
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
	 * 逶ｮ讓咏憾諷九ｒ蛻晄悄蛹
	 * 
	 * @return
	 */
	private ArrayList<String> initGoalState() {
		ArrayList<String> goalList = new ArrayList<String>();
		/*
		 * goalList.add("E on A"); goalList.add("C on E");
		 * goalList.add("B on D");
		 */

		goalList.add("4 on 1");
		goalList.add("6 on 5");
		goalList.add("5 on 2");
		goalList.add("2 on 3");

		return goalList;
	}

	/**
	 * 蛻晄悄迥ｶ諷九ｒ蛻晄悄蛹
	 * 
	 * @return
	 */
	private ArrayList<String> initCurrentState() {
		ArrayList<String> initialState = new ArrayList<String>();

		/*
		 * initialState.add("clear A"); initialState.add("clear D");
		 * 
		 * initialState.add("A on B"); initialState.add("B on C");
		 * initialState.add("D on E");
		 */

		initialState.add("clear 1");
		initialState.add("clear 4");

		initialState.add("1 on 2");
		initialState.add("2 on 3");
		initialState.add("4 on 5");
		initialState.add("5 on 6");

		return initialState;
	}

	/**
	 * 迴ｾ蝨ｨ迥ｶ諷九ｒ繧ｻ繝�ヨ
	 * 
	 * @param State
	 */
	public void setCurrentState(ArrayList<String> State) {
		nPara.setCurrentState(State);
	}

	/**
	 * 逶ｮ讓咏憾諷九ｒ繧ｻ繝�ヨ
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
	 * 迴ｾ蝨ｨ迥ｶ諷九°繧臥岼讓咏憾諷九∈縺ｮ驕鍋ｭ九ｒNOAH(Nets Of Action Hierarchies)縺ｧ豎ゅａ繧
	 * 
	 */
	public void initialplanning() {
		ArrayList<String> goalState = nPara.getGoalState();

		// 繧ｹ繧ｿ繝ｼ繝医√ざ繝ｼ繝ｫ縺ｮ逋ｻ骭ｲ
		JointS s = new JointS();
		s.changeForward("Start");
		JointJ j = new JointJ();
		Node goal = new Node("Goal", 0, j, null);
		j.changeBack(goal);
		// 繝弱�繝峨�逋ｻ骭ｲ
		for (String str : goalState) {
			Node newNode = new Node(str, nodecount++, s, j);
			plan.add(newNode);
			s.addBack(newNode);
			j.addForward(newNode);
		}
		ss.add(s);
		js.add(j);
		planning();

	}

	/**
	 * 繝励Λ繝ｳ繧貞ｱ暮幕縺吶ｋ
	 */
	public void expandplan() {
		ArrayList<Node> newplan = new ArrayList<Node>();
		for (Node node : plan) {
			if (node.getNodeName().contains("on")) {
				JointS s = new JointS();
				s.changeForward(node.getForward());
				ss.add(s);
				JointJ j = new JointJ();
				js.add(j);

				ss.get(0).removeBack(node);
				ss.get(0).addBack(s);

				js.get(0).removeForward(node);

				Pattern p = Pattern.compile("(.*) on (.*)");
				Matcher m = p.matcher(node.getNodeName());
				if (m.find()) {
					over.add(m.group(1));
					under.add(m.group(2));
					Node newNode1 = new Node("clear " + m.group(1),
							nodecount++, s, j);
					s.addBack(newNode1);
					j.addForward(newNode1);
					Node newNode2 = new Node("clear " + m.group(2),
							nodecount++, s, j);
					s.addBack(newNode2);
					j.addForward(newNode2);
					Node newNode3 = new Node("Place " + m.group(1) + " on "
							+ m.group(2), nodecount++, j, node.getBack());
					j.changeBack(newNode3);
					js.get(0).addForward(newNode3);

					newplan.add(newNode1);
					newplan.add(newNode2);
					newplan.add(newNode3);
				}

			}
		}
		plan.clear();
		plan = newplan;
	}

	ArrayList<Node> headNodes = new ArrayList<Node>();

	/**
	 * 蟷ｲ貂峨ｒ謗｢縺鈴�ｺ丈ｻ倥￠繧定｡後≧
	 */
	public void checkInterference() {
		System.out.println(under);
		// 莉雁屓豕ｨ逶ｮ縺吶ｋ蟷ｲ貂
		JointJ j = js.get(0);
		ArrayList<Node> list = j.getForward();
		ArrayList<Node> subPlan = new ArrayList<Node>();
		Pattern p = Pattern.compile("Place (.*) on (.*)");
		System.out.println("syu");
		while (subPlan.size() != list.size()) {
			System.out.println("subPlan" + subPlan.size() + "list"
					+ list.size());
			for (Node node : list) {
				Matcher m = p.matcher(node.getNodeName());
				if (m.find()) {
					if (!under.contains(m.group(1))) {
						under.remove(m.group(2));

						if (!subPlan.contains(node)) {
							subPlan.add(0, node);
						}
					}
				}
			}

		}

		headNodes.addAll(subPlan);

		for (int i = 0; i < subPlan.size(); i++) {
			// subPlan.get(i).changeBack(subPlan.get(i + 1).getForward());
			// ((JointJ) subPlan.get(i + 1).getForward()).addForward(subPlan
			// .get(i));
			Matcher m2 = p.matcher(subPlan.get(i).getNodeName());
			if (m2.find()) {
				for (int i2 = i + 1; i2 < subPlan.size(); i2++) {
					Matcher m3 = p.matcher(subPlan.get(i2).getNodeName());
					if (m3.find()) {
						System.out.println(m2.group(1) + " " + m3.group(2));
						if (m2.group(1).equals(m3.group(2))) {
							System.out.println(subPlan.get(i).getNodeName()
									+ "  " + subPlan.get(i2).getNodeName());
							subPlan.get(i).changeBack(
									subPlan.get(i2).getForward());
							((JointJ) subPlan.get(i2).getForward())
									.addForward(subPlan.get(i));
							j.removeForward(subPlan.get(i));
							headNodes.remove(subPlan.get(i2));
							break;
						}
					}

				}

			}

		}

		// subPlan.get(subPlan.size() - 1).changeBack(j.getBack());
		// j.getBack().changeForward(subPlan.get(subPlan.size() - 1));
		// js.remove(0);
	}

	/**
	 * 莠悟捉逶ｮ莉･髯阪�鬆�ｺ丈ｻ倥￠
	 */
	public void checkIF() {

		headNodes.clear();
		Pattern p = Pattern.compile("Place (.*) on (.*)");
		Pattern p2 = Pattern.compile("remove (.*) from on top (.*)");

		ArrayList<JointJ> deleteList = new ArrayList<JointJ>();
		for (int i = 1; i < js.size(); i++) {

			boolean stackFlag = false;
			JointJ j = js.get(i);
			ArrayList<Node> forward = j.getForward();
			ArrayList<Node> subPlan = new ArrayList<Node>();

			for (Node node : forward) {
				Matcher m = p.matcher(node.getNodeName());
				// Matcher m2 = p.matcher(node.getNodeName());
				if (m.find()) {
					subPlan.add(node);
					stackFlag = true;
				} else {
					subPlan.add(0, node);
				}
			}

			if (stackFlag) {
				System.out.println("subPlan" + subPlan + "\n\n");
				Object joint = subPlan.get(subPlan.size() - 1);
				while (joint instanceof Node) {

					Node next = (Node) joint;
					joint = next.getForward();

				}

				if (joint instanceof JointJ) {
					System.out.println("succes");
					JointJ target = (JointJ) joint;

					for (int k = 0; k < (subPlan.size() - 1); k++) {
						System.out.println(subPlan.get(k));
						Node jointNode = subPlan.get(k);

						jointNode.changeBack(target);
						target.addForward(jointNode);
					}

					Node stackNode = subPlan.get(subPlan.size() - 1);
					Node jointStack = ((JointJ) stackNode.getBack()).getBack();
					stackNode.changeBack(jointStack);
					jointStack.changeForward(stackNode);

					deleteList.add(j);
				}
			}

		}

		for (JointJ j : deleteList) {
			js.remove(j);
		}

		JointS s = ss.get(0);
		ArrayList<JointS> slist = new ArrayList<JointS>();
		for (Object obj : s.getBack()) {
			if (obj instanceof JointS) {
				slist.add((JointS) obj);
			}
		}

		for (JointS s2 : slist) {
			for (Object obj : s2.getBack()) {
				s.addBack(obj);
				((Node) obj).changeForward(s);
			}
			s.removeBack(s2);
			ss.remove(s2);

		}
	}

	/**
	 * 蜀鈴聞繧呈爾縺励げ繝ｩ繝輔�螟牙ｽ｢繧定｡後≧
	 */
	public void checkLengthy() {

		for (Node node : headNodes) {
			ArrayList<String> clearList = new ArrayList<String>();

			System.out.println("\ncheck " + node.getNodeName() + " do \n");

			do {
				System.out.println("node : " + node);

				System.out.println("clearList : " + clearList);

				Node clearNode = ((JointJ) node.getForward()).getForward().get(
						1);
				Node extraNode = ((JointJ) node.getForward()).getForward().get(
						0);
				System.out.println("clearNode? :" + clearNode);
				if (clearList.contains(clearNode.getNodeName())) {
					System.out.println("in  clearNode");
					((JointJ) clearNode.getBack()).removeForward(clearNode);
					// ((JointJ)( (JointJ) node.getForward()
					// ).getforward().get(1).getBack()).removeforward(( (JointJ)
					// node.getForward() ).getforward().get(1));
					Object x = clearNode.getForward();
					if (x instanceof JointS) {
						System.out.println("success JointS\n" + x);
						JointS jointS = (JointS) x;

						jointS.removeBack(clearNode);
						plan.remove(clearNode);

						((JointS) jointS.getForward()).addBack(extraNode);
						((JointS) jointS.getForward()).removeBack(jointS);

						extraNode.changeForward(((JointS) jointS.getForward()));

						ss.remove(jointS);
					} else {

						System.out.println("not JointS \n" + x);
						// JointS 縺倥ｃ縺ｪ縺
					}
				}
				clearList.add(((JointJ) node.getForward()).getForward().get(0)
						.getNodeName());
				clearList.add(((JointJ) node.getForward()).getForward().get(1)
						.getNodeName());
				System.out.println(node.getBack().getClass().toString());
				if (((JointJ) node.getBack()).equals(js.get(0))) {
					System.out.println("goalNode :" + node.getBack());
					System.out.println(node.getBack().toString());
					break;
				} else {
					node = ((JointJ) node.getBack()).getBack();
				}

				System.out.println("");
			} while (true);
		}
		System.out.println("finish checklengthy");

	}

	public void checkLen() {

		ArrayList<Node> clearList = new ArrayList<Node>();
		Pattern p = Pattern.compile("clear (.*)");
		for (int i = 1; i < js.size(); i++) {
			JointJ j = js.get(i);

			HashMap<String, Integer> data = new HashMap<String, Integer>();
			HashMap<String, Node> nodeData = new HashMap<String, Node>();
			for (Node node : j.getForward()) {
				int count = 1;

				Matcher m = p.matcher(node.getNodeName());
				while (!m.find()) {
					count++;
					node = ((Node) node.getForward());

					m = p.matcher(node.getNodeName());
				}

				if (data.containsKey(node.getNodeName())) {
					if (data.get(node.getNodeName()) > count) {
						clearList.add(node);
					} else {
						clearList.add(nodeData.get(node.getNodeName()));
						data.remove(node.getNodeName());
						data.put(node.getNodeName(), count);
						nodeData.remove(node.getNodeName());
						nodeData.put(node.getNodeName(), node);
					}
				} else {
					data.put(node.getNodeName(), count);
					nodeData.put(node.getNodeName(), node);
				}
			}
		}

		for (Node node : clearList) {
			JointS s = ss.get(0);
			s.removeBack(node);
			while (true) {
				if (node.getBack() instanceof Node) {
					plan.remove(node);
					node = (Node) node.getBack();
				} else {
					plan.remove(node);
					((JointJ) node.getBack()).removeForward(node);
					break;
				}
			}

		}
	}

	/**
	 * 縲隧ｳ邏ｰ蛹
	 */
	public void refinement() {

		JointS startNode = ss.get(0);
		ArrayList<String> currentState = nPara.getCurrentState();
		ArrayList<Node> deleteList = new ArrayList<Node>();
		ArrayList<Node> addList = new ArrayList<Node>();
		for (Object obj : startNode.getBack()) {

			if (obj instanceof Node) {
				if (!currentState.contains(((Node) obj).getNodeName())) {
					System.out.println("迴ｾ迥ｶ諷九↓隧ｲ蠖薙☆繧九ｂ縺ｮ辟｡縺 :"
							+ ((Node) obj).getNodeName());
					Pattern p1 = Pattern.compile("clear (.*)");
					Matcher m1 = p1.matcher(((Node) obj).getNodeName());

					if (m1.find()) {

						for (String str : currentState) {
							Pattern p2 = Pattern.compile("(.*) on (.*)");
							Matcher m2 = p2.matcher(str);
							if (m2.find()) {
								if (m2.group(2).equals(m1.group(1))) {
									Node newNode1 = new Node("clear "
											+ m2.group(1), nodecount++,
											((Node) obj).getForward(), null);
									Node newNode2 = new Node("remove "
											+ m2.group(1) + " from on top "
											+ m2.group(2), nodecount++, null,
											((Node) obj).getBack());

									newNode1.changeBack(newNode2);
									newNode2.changeForward(newNode1);

									plan.remove(obj);
									// startNode.removeBack(obj);
									deleteList.add((Node) obj);

									Object joint = ((Node) obj).getBack();

									if (joint instanceof JointJ) {
										((JointJ) ((Node) obj).getBack())
												.removeForward(obj);

										// startNode.addBack(newNode1);
										addList.add(newNode1);
										((JointJ) ((Node) obj).getBack())
												.addForward(newNode2);
									} else {
										// startNode.addBack(newNode1);
										addList.add(newNode1);
										((Node) ((Node) obj).getBack())
												.changeForward(newNode2);

									}

									plan.add(newNode1);
									plan.add(newNode2);

								}
							}
						}
					}
				}
			}
		}

		for (Node delete : deleteList) {
			startNode.removeBack(delete);
		}

		for (Node add : addList) {
			startNode.addBack(add);
		}
	}

	/**
	 * 蟷ｲ貂峨�谿区焚�翫→蜀鈴聞縺ｮ邱乗焚s繧堤┌縺上☆
	 */
	public void planning() {
		expandplan();

		System.out.println("initialState \n");
		printState();

		checkInterference();
		System.out.println("鬆�ｺ丈ｻ倥￠");
		printState();
		checkLengthy();
		System.out.println("蜀鈴聞蜑企勁");
		printState();
		refinement();

		System.out.println("\nAct1 result \n");
		printState();

		// 莉･荳倶ｺ悟捉逶ｮ莉･髯
		// while () {
		checkIF();
		// }
		System.out.println("\n checkIF");
		printState();
		ArrayList<Node> lastPlan = new ArrayList<Node>();
		lastPlan.addAll(plan);
		while (true) {
			checkLen();
			refinement();
			if (lastPlan.equals(plan)) {
				break;
			} else {
				lastPlan.clear();
				lastPlan.addAll(plan);
			}
			System.out.println("\n loop");
			printState();
		}
	}

	public void printState() {
		for (JointS joint : ss) {

			System.out.println("center :" + joint);

			System.out.println("forward : " + joint.getForward());

			Iterator it = joint.getBack().iterator();
			while (it.hasNext()) {

				Object obj = it.next();
				System.out.println("back : " + obj);

			}
			System.out.println("");
		}

		for (JointJ joint2 : js) {

			System.out.println("joint2 " + joint2);

			Iterator it = joint2.getForward().iterator();
			while (it.hasNext()) {

				Object obj = it.next();

				System.out.println("forward : " + obj);

			}

			System.out.println("back : " + joint2.getBack());

			System.out.println("");
		}

		for (Node node : plan) {
			System.out
					.println(node.getNodeName() + "  " + node.getNodeNumber());
			System.out.println("forward " + node.getForward() + "\nback "
					+ node.getBack() + "\n");
		}
	}

	/**
	 * 迴ｾ蝨ｨ迥ｶ諷九°繧臥岼讓咏憾諷九∈縺ｮ驕鍋ｭ九ｒNOAH(Nets Of Action Hierarchies)縺ｧ豎ゅａ繧
	 * multithread繧剃ｽｿ縺翫≧縺ｨ縺励◆縺後ｈ縺上ｏ縺九ｉ縺ｪ縺上↑縺｣縺溘�縺ｧ謾ｾ譽
	 */
	public void planningMultiThread() {
		ArrayList<String> goalState = nPara.getGoalState();

		// 荳弱∴繧峨ｌ縺溘ざ繝ｼ繝ｫ迥ｶ諷九�謨ｰ縺縺代�蟇ｾ蠢懊☆繧亀hread繧剃ｽ懊ｋ
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