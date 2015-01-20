package gui;

import gui.PlannerController.StatesChangeListener;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import planner.NOAH;

public class SampleGUI extends JFrame implements ActionListener{

	// 
	JPanel panel = new JPanel();
	CardLayout layout = new CardLayout();
	// Label
	JLabel stepLabel;
	// 物体を表示するパネル
	PlannerPanel plannerPanel;
	// 初期状態
	PlannerPanel startPanel;
	// 終了状態
	PlannerPanel goalPanel;
	// Noah
	NOAH noah;
	// PlannerStepExecutor
	final PlannerStepExecutor plannerStepExecutor = new PlannerStepExecutor();
	
	ArrayList<String> startList = new ArrayList<String>();
	ArrayList<String> goalList = new ArrayList<String>();
	ArrayList<String> objects = new ArrayList<String>();
	ArrayList<String> ansList = new ArrayList<String>();

	JTabbedPane tab1 = new JTabbedPane();
	JTabbedPane tab2 = new JTabbedPane();
	JTextArea txtStart = new JTextArea("");//初期状態のエリア
	JTextArea txtGoal = new JTextArea("");//目標状態のエリア
	JTextArea txtAnswer = new JTextArea("");//解答のエリア
	
	// コンストラクタ
	public SampleGUI() {
		initialize();
		
		loadData();
		
		setVisible(true);
		
		// 初期状態・終了状態を設定
		initPlanner();
		initPlannerPanel(startList, startPanel);
		initPlannerPanel(goalList, goalPanel);
		
		// TODO Planner の出力を元に PlannerPanel のコマンドを呼ぶ操作パネル、レイアウトの作成
		// 1. 初期状態、目標状態を入れるための入力コンポーネントを用意する
		// 2. 入力された内容を元に Planner を動かし、プランをもらう
		// 3. 受け取ったプランを元に、 PlannerPanel の操作メソッドを呼び、操作する
		
	}
	
	// 初期化
	private void initialize() {		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(10,10,550,600);
		setTitle("SampleGUI");
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JMenuBar menuBar = new JMenuBar();
		getContentPane().add(menuBar, BorderLayout.NORTH);
		
		JMenu mnFile = new JMenu("File");
		mnFile.setBackground(new Color(192, 192, 192));
		mnFile.setForeground(Color.RED);
		menuBar.add(mnFile);
		
		JMenu mnNewMenu_1 = new JMenu("State");
		menuBar.add(mnNewMenu_1);
		
		JRadioButtonMenuItem rdbtnmntmNewRadioItem_2 = new JRadioButtonMenuItem("Graphics");
		rdbtnmntmNewRadioItem_2.setSelected(true);
		mnNewMenu_1.add(rdbtnmntmNewRadioItem_2);
		rdbtnmntmNewRadioItem_2.addActionListener(this);
		rdbtnmntmNewRadioItem_2.setActionCommand("graphics");
		
		JRadioButtonMenuItem rdbtnmntmNewRadioItem_3 = new JRadioButtonMenuItem("Text");
		mnNewMenu_1.add(rdbtnmntmNewRadioItem_3);
		rdbtnmntmNewRadioItem_3.addActionListener(this);
		rdbtnmntmNewRadioItem_3.setActionCommand("text");
		
		ButtonGroup group2 = new ButtonGroup();
		group2.add(rdbtnmntmNewRadioItem_2);
		group2.add(rdbtnmntmNewRadioItem_3);
		
		// --- Card1: グラフィックのタブ
		JPanel card1 = new JPanel();
		card1.setLayout(new BorderLayout());
		
		// - Page 1
		JPanel page1_panels = new JPanel(new GridLayout(2,1));
		
		//初期状態と目標状態を決めるページ
		JPanel page1 = new JPanel(new BorderLayout());
		page1.add(BorderLayout.CENTER, page1_panels);

		startPanel = new PlannerPanel();
		startPanel.enableScrollScreen(false);
		startPanel.setStatesChangeListener(new StatesChangeListener() {
			@Override
			public void onChangeStates(List<String> states) {
				startList.clear();
				startList.addAll(states);
			}
		});
		
		JPanel start = new JPanel();
		start.setLayout(new BorderLayout());
		start.add(BorderLayout.NORTH, new JLabel("初期状態"));
		start.add(BorderLayout.CENTER, startPanel);
		page1_panels.add(start);
		
		goalPanel = new PlannerPanel();
		goalPanel.enableScrollScreen(false);
		goalPanel.setStatesChangeListener(new StatesChangeListener() {
			@Override
			public void onChangeStates(List<String> states) {
				goalList.clear();
				goalList.addAll(states);
			}
		});
		
		JPanel goal = new JPanel();
		goal.setLayout(new BorderLayout());
		goal.add(BorderLayout.NORTH, new JLabel("目標状態"));
		goal.add(BorderLayout.CENTER, goalPanel);
		page1_panels.add(goal);
		
		JPanel ctrl = new JPanel();
		page1.add(BorderLayout.SOUTH, ctrl);
		
		JButton okButton = new JButton("plan");
		okButton.addActionListener(this);
		okButton.setActionCommand("plan");
		ctrl.add(okButton);
		
		tab1.add("select", page1);
		
		// - Page 2: 実行結果を表示するページ
		JPanel page2 = new JPanel(new BorderLayout());

		JPanel page2_panel = new JPanel(new BorderLayout());
		stepLabel = new JLabel("初期状態");
		page2_panel.add(BorderLayout.NORTH, stepLabel);
		plannerPanel = new PlannerPanel();
		page2_panel.add(BorderLayout.CENTER, plannerPanel);
		page2.add(BorderLayout.CENTER, page2_panel);

		JButton playButton = new JButton("play");//初期状態を表示するボタン
		playButton.addActionListener(plannerStepExecutor);
		playButton.setActionCommand("play");
		JButton pauseButton = new JButton("pause");//初期状態を表示するボタン
		pauseButton.addActionListener(plannerStepExecutor);
		pauseButton.setActionCommand("pause");
		JButton startButton = new JButton("start");//初期状態を表示するボタン
		startButton.addActionListener(plannerStepExecutor);
		startButton.setActionCommand("start");
		JButton prevButton = new JButton("prev");//前の状態に戻るボタン
		prevButton.addActionListener(plannerStepExecutor);
		prevButton.setActionCommand("prev");
		JButton nextButton = new JButton("next");//次の状態に進むボタン
		nextButton.addActionListener(plannerStepExecutor);
		nextButton.setActionCommand("next");
		JButton goalButton = new JButton("goal");//目標状態を表示するボタン
		goalButton.addActionListener(plannerStepExecutor);
		goalButton.setActionCommand("goal");
		JPanel btnPanel = new JPanel();
		btnPanel.add(playButton);
		btnPanel.add(pauseButton);
		btnPanel.add(startButton);
		btnPanel.add(prevButton);
		btnPanel.add(nextButton);
		btnPanel.add(goalButton);
		
		page2.add(BorderLayout.SOUTH, btnPanel);
		tab1.add("answer", page2);
		
		card1.add(tab1);
		
		// --- Card2: テキストで表示する
		JPanel card2 = new JPanel();
		card2.setLayout(new GridLayout());
		
		EtchedBorder border = new EtchedBorder();
		

		
		//

		JPanel sPanel = new JPanel();//初期状態等の変更ページ
		sPanel.setLayout(new GridLayout());
		JPanel aPanel = new JPanel();//解答ページ
		aPanel.setLayout(new BorderLayout());
		
		tab2.add("select",sPanel);
		tab2.add("answer",aPanel);
		
		JPanel startPanel = new JPanel();//初期状態エリア用のパネル
		JScrollPane scroll1 = new JScrollPane(txtStart);
		startPanel.setLayout(new BorderLayout());
		startPanel.add(new JLabel("start"),BorderLayout.NORTH);
		startPanel.add(scroll1,BorderLayout.CENTER);
		startPanel.setBorder(border);
		
		JPanel goalPanel = new JPanel();//目標状態エリア用のパネル
		JScrollPane scroll2 = new JScrollPane(txtGoal);
		goalPanel.setLayout(new BorderLayout());
		goalPanel.add(new JLabel("goal"),BorderLayout.NORTH);
		goalPanel.add(scroll2,BorderLayout.CENTER);
		goalPanel.setBorder(border);
		
		JPanel buttonPanel = new JPanel();//ボタン用のパネル
		buttonPanel.setLayout(new FlowLayout());
		JButton OK = new JButton("ok");
		OK.addActionListener(this);
		OK.setActionCommand("OK");
		JButton RESET = new JButton("reset");
		RESET.addActionListener(this);
		RESET.setActionCommand("RESET");
		JButton GtoS = new JButton("GtoS");
		GtoS.addActionListener(this);
		GtoS.setActionCommand("GtoS");
		
		buttonPanel.add(OK);
		buttonPanel.add(RESET);
		buttonPanel.add(GtoS);
		buttonPanel.setBorder(border);

		JPanel answerPanel = new JPanel();//目標状態エリア用のパネル
		JScrollPane scroll3 = new JScrollPane(txtAnswer);
		answerPanel.setLayout(new BorderLayout());
		answerPanel.add(scroll3,BorderLayout.CENTER);
		answerPanel.setBorder(border);
		
		sPanel.add(startPanel);
		sPanel.add(goalPanel);
		sPanel.add(buttonPanel);
		aPanel.add(answerPanel);
		
		card2.add(tab2);
		
		//
		panel.setLayout(layout);
		panel.add(card1);
		panel.add(card2);
		
		getContentPane().add(panel,BorderLayout.CENTER);
		
		BevelBorder border4 = new BevelBorder(BevelBorder.RAISED);
		BevelBorder border8 = new BevelBorder(BevelBorder.RAISED);
		BevelBorder border9 = new BevelBorder(BevelBorder.RAISED);
		// ---
		
	}
	
	private void initPlanner() {
		//NOAHを準備
		noah = new NOAH();
		for (String start : noah.getCurrentState()) {
			startList.add(start);
		}
		for (String goal : noah.getGoalState()) {
			goalList.add(goal);
		}
		
		System.out.println("startList"+startList);
		System.out.println("goalList"+goalList);
		objects = noah.getObjects();
	}
	
	/**
	 * PlannerPanel の状態を初期化する
	 * @param states 初期化する状態
	 * @param panel パネル
	 */
	private synchronized void initPlannerPanel(ArrayList<String> states, PlannerPanel panel) {
		final ArrayList<String> onTableStates = new ArrayList<String>();
		
		for(String object : objects){
			onTableStates.add("clear " +object);
		}
		
		noah.setCurrentState(onTableStates);
		noah.setGoalState(states);
		noah.planning();
		final ArrayList<String> plan = noah.getResult();
		
		Pattern p1 = Pattern.compile("pick up (.*) from the table");
		Pattern p2 = Pattern.compile("Place (.*) on (.*)");
		ArrayList<String> exist = new ArrayList<String>();
		
		try { panel.stop(); } catch (InterruptedException e) { e.printStackTrace(); }
		for (String operator : plan) {
			Matcher m1 = p1.matcher(operator);
			Matcher m2 = p2.matcher(operator);

			if (m1.find()) {
				exist.add(m1.group(1));
			}

			if (m2.find()) {
				if(!exist.contains(m2.group(2))){
					panel.putBox(m2.group(2), null);
				}
				panel.putBox(m2.group(1),m2.group(2));
			}
		}
		try { panel.start(); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	private void textsToStates() {
		startList.clear();
		String strs1[] = txtStart.getText().split("\n");
		for (int i = 0; i < strs1.length; i++) {
		    startList.add(strs1[i]);
		}
		goalList.clear();
		String strs2[] = txtGoal.getText().split("\n");
		for (int i = 0; i < strs2.length; i++) {
		    goalList.add(strs2[i]);
		}
	}
	
	private void plan() {
		noah.setCurrentState(startList);
		noah.setGoalState(goalList);
		noah.planning();
		ansList = noah.getResult();
	}
	
	private String nth(int i) {
		switch (i % 10) {
		case 1: return String.valueOf(i)+"st";
		case 2: return String.valueOf(i)+"nd";
		case 3: return String.valueOf(i)+"rd";
		default: return String.valueOf(i)+"th";
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
    	String cmd = e.getActionCommand();
    	if(cmd == "graphics"){
    		layout.first(panel);
    	}else if(cmd.equals("plan")){
    		plan();
    		plannerStepExecutor.initialize();
    		tab1.setSelectedIndex(1);
    	}else if(cmd.equals("OK")){
    		textsToStates();
    		plan();
    		for(int i=0;i < ansList.size();i++)
    		txtAnswer.append(ansList.get(i)+"\n");
    		tab2.setSelectedIndex(1);
    		
    	}else if(cmd.equals("RESET")){
    		txtStart.setText("");
    		txtGoal.setText("");
    		txtAnswer.setText("");
    		
    	}else if(cmd.equals("GtoS")){
    		txtStart.setText(txtGoal.getText());
    		txtGoal.setText("");
    	}else if(cmd.equals("text")){
    		layout.last(panel);
    	}

        
    }
	
	private void loadData(){
		
	}
	
	private class PlannerStepExecutor implements ActionListener, Runnable {
		final Pattern p1 = Pattern.compile("pick up (.*) from the table");
		final Pattern p2 = Pattern.compile("remove (.*) from (.*)");
		final Pattern p3 = Pattern.compile("Place (.*) on (.*)");
		
		int ptr = 0;
		boolean loop = false;
		Thread th;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if(cmd.equals("play")) {
				start();
			}else if(cmd.equals("pause")){
	    		try { stop(); } catch (Exception e0) { e0.printStackTrace(); }
	    	}else if(cmd.equals("start")){
	    		initPlannerPanel(startList, plannerPanel);
	    		ptr = 0;
	    	}else if(cmd.equals("goal")){
	    		initPlannerPanel(goalList, plannerPanel);
	    		ptr = ansList.size()-1;
	    	}else if(cmd.equals("next")){
	    		if (ptr < ansList.size()) {
	    			String op = ansList.get(ptr);
	    			try { execute(op); } catch (InterruptedException e0) { e0.printStackTrace(); }
	    			ptr++;
	    		}
	    	}else if(cmd.equals("prev")){
	    		if (0 < ptr) {
	    			ptr--;
	    			String op = ansList.get(ptr);
	    			try { execute(op); } catch (InterruptedException e0) { e0.printStackTrace(); }
	    		}
	    	}
		}
		
		public void initialize() {
			try { stop(); } catch (InterruptedException e) { }
			ptr = 0;
			loop = false;
			initPlannerPanel(startList, plannerPanel);
		}
		
		private void execute(String op) throws InterruptedException {
			Matcher m;
			
			// ラベル更新
			stepLabel.setText(nth(ptr)+" process");
			
			// pickup (.*) from the table
			m = p1.matcher(op);
			if (m.find()) {
				String name = m.group(1);
				plannerPanel.pickup(name);
				return;
			}
			
			// remove (.*) from (.*)
			m = p2.matcher(op);
			if (m.find()) {
				String name = m.group(1);
				plannerPanel.pickup(name);
				return;
			}
			
			// Place (.*) on (.*)
			m = p3.matcher(op);
			if (m.find()) {
				String name = m.group(2);
				plannerPanel.place(name);
				return;
			}
		}
		
		private void start() {
			if (th == null) {
				loop = true;
				th = new Thread(this);
				th.start();
			}
		}
		private void stop() throws InterruptedException {
			if (th != null) {
				loop = false;
				th.join();
				th = null;
			}
		}
		
		@Override
		public void run() {
			String op = null;
			try {
				while (loop) {
					synchronized (ansList) {
						op = (0 <= ptr && ptr < ansList.size()) ? ansList.get(ptr) : null;
					}
					if (op == null) {
						break;
					}
					execute(op);
					
					ptr++;
				}
			} catch (InterruptedException e) {
				System.out.println("An error occured while executing the operation: "+op);
				e.printStackTrace();
			}
		}
	};
	
	public static void main(String[] args) {
		//
		
		SampleGUI gui = new SampleGUI();
		gui.setVisible(true);
	}
	
}
