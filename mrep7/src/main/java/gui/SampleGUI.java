package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

public class SampleGUI extends JFrame implements ActionListener{

	// 
	JPanel panel = new JPanel();
	CardLayout layout = new CardLayout();
	// 
	JPanel graphics = new JPanel();
	CardLayout gra_layout = new CardLayout();
	// 物体を表示するパネル
	PlannerPanel plannerPanel = new PlannerPanel();
	
	ArrayList<String> startList = new ArrayList<String>();
	ArrayList<String> goalList = new ArrayList<String>();

	// コンストラクタ
	public SampleGUI() {
		initialize();
		
		loadData();
		//setupSuffixArray();
		
		//set();
		setVisible(true);
		
		// TODO Planner の出力を元に PlannerPanel のコマンドを呼ぶ操作パネル、レイアウトの作成
		// 1. 初期状態、目標状態を入れるための入力コンポーネントを用意する
		// 2. 入力された内容を元に Planner を動かし、プランをもらう
		// 3. 受け取ったプランを元に、 PlannerPanel の操作メソッドを呼び、操作する
		
	}
	
	// 初期化
	private void initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(10,10,1500,1000);
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
		
		//グラフィックで表示する
		JPanel card1 = new JPanel();
		card1.setLayout(new GridLayout());
		//JPanel start1 = new JPanel();//初期状態のエリア
		//JPanel goal1 = new JPanel();//目標状態のエリア
		//JTabbedPane tab1 = new JTabbedPane();
		//tab1.add("start",start1);
		//tab1.add("goal",goal1);
		JTabbedPane tab = new JTabbedPane();
		//初期状態と目標状態を決めるページ
		JPanel page1 = new JPanel();
		JPanel start = new JPanel();
		PlannerPanel start1 = new PlannerPanel();//初期状態の制作パネル
		start1.enableScrollScreen(false);
		Pattern pat = Pattern.compile("clear (.*)");
		//Matcher mat = pat.matcher();
		start.setLayout(new BorderLayout());
		start.add(BorderLayout.NORTH, new JLabel("初期状態"));
		start.add("Center", start1);
		//start1.setBackground(Color.RED);
		JPanel goal = new JPanel();
		PlannerPanel goal1 = new PlannerPanel();//目標状態の制作パネル
		goal1.enableScrollScreen(false);
		goal.setLayout(new BorderLayout());
		goal.add("North", new JLabel("目標状態"));
		goal.add("Center", goal1);
		//goal1.setBackground(Color.GREEN);
		JPanel ok = new JPanel();//実行ボタンを配置するパネル
		JButton okButton = new JButton("OK");
		ok.add(okButton);
		page1.setLayout(new GridLayout(3,1));
		page1.add(start1);
		page1.add(goal1);
		page1.add(ok);
		tab.add("select",page1);
		//実行結果を表示するページ
		JPanel page2 = new JPanel();
		graphics.setLayout(gra_layout);
		JPanel gra_start = new JPanel();//初期状態のエリア
		gra_start.add(new JLabel("start"));
		//gra_start.setBackground(Color.RED);
		graphics.add(gra_start);
		for(int i = 1; i < 13; i++){
			JPanel gra_process = new JPanel();//過程のエリア
			String s = Integer.toString(i);
			if(i % 10 == 1){
				gra_process.add(new JLabel(s+"st process"));
			}else if(i % 10 == 2){
				gra_process.add(new JLabel(s+"nd process"));
			}else if(i % 10 == 3){
				gra_process.add(new JLabel(s+"rd process"));
				//gra_process.setBackground(Color.BLUE);
			}else{		ArrayList<String> startList = new ArrayList<String>();
				gra_process.add(new JLabel(s+"th process"));
			}
			graphics.add(gra_process);
		}
		JPanel gra_goal = new JPanel();//目標状態のエリア
		gra_goal.add(new JLabel("goal"));
		//gra_goal.setBackground(Color.GREEN);
		graphics.add(gra_goal);
		JButton startButton = new JButton("start");//初期状態を表示するボタン
		startButton.addActionListener(this);
		startButton.setActionCommand("start");
		JButton prevButton = new JButton("prev");//前の状態に戻るボタン
		prevButton.addActionListener(this);
		prevButton.setActionCommand("prev");
		JButton nextButton = new JButton("next");//次の状態に進むボタン
		nextButton.addActionListener(this);
		nextButton.setActionCommand("next");
		JButton goalButton = new JButton("goal");//目標状態を表示するボタン
		goalButton.addActionListener(this);
		goalButton.setActionCommand("goal");
		JPanel btnPanel = new JPanel();
		btnPanel.add(startButton);
		btnPanel.add(prevButton);
		btnPanel.add(nextButton);
		btnPanel.add(goalButton);
		page2.setLayout(new BorderLayout());
		page2.add("Center", graphics);
		page2.add("South", btnPanel);
		tab.add("answer", page2);
		card1.add(tab);
		
		//card1.add(tab1);
		
		
		//テキストで表示する
		String startText="aaa\naaa\naaa";
		String goalText="";
		
		JPanel card2 = new JPanel();
		card2.setLayout(new GridLayout(1,4));
		JTextArea start2 = new JTextArea(startText);//初期状態のエリア
		JTextArea goal2 = new JTextArea(goalText);//目標状態のエリア
		JTextArea answer2 = new JTextArea("ccc");//解答のエリア
		JTabbedPane tab2 = new JTabbedPane();
		JTabbedPane tab3 = new JTabbedPane();
		JTabbedPane tab4 = new JTabbedPane();
		tab2.add("start",start2);
		tab3.add("goal",goal2);
		tab4.add("answer",answer2);
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();
		JPanel panel4 = new JPanel();
		JButton button = new JButton("OK");
		panel1.add(tab2);
		panel1.add(tab3);
		panel1.setLayout(new GridLayout());
		panel2.add(panel3);
		panel2.add(panel4);
		panel2.setLayout(new GridLayout());
		panel3.add(button);
		panel4.add(tab4);
		panel4.setLayout(new GridLayout());
		
		card2.add(panel1);
		card2.add(panel2);
		
		String strs[] = startText.split("\n");
		for (int i = 0; i < strs.length; i++) {
		    startList.add(strs[i]);
		}
		
		
		//
		panel.setLayout(layout);
		panel.add(card1);
		panel.add(card2);
		
		getContentPane().add(panel,BorderLayout.CENTER);
		
		BevelBorder border4 = new BevelBorder(BevelBorder.RAISED);
		BevelBorder border8 = new BevelBorder(BevelBorder.RAISED);
		BevelBorder border9 = new BevelBorder(BevelBorder.RAISED);
		// ---
		
		JMenu mnNewMenu_2 = new JMenu("Test");
		menuBar.add(mnNewMenu_2);
		
		JMenuItem mntmNewItem = new JMenuItem("Add component");
		mntmNewItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				new Box(100, 100).attachTo(physicsPanel);
			}
		});
		mnNewMenu_2.add(mntmNewItem);
		
		//panel.add(physicsPanel, BorderLayout.CENTER);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
    	String cmd = e.getActionCommand();
    	if(cmd == "graphics"){
    		layout.first(panel);
    	}else if(cmd.equals("start")){
    		gra_layout.first(graphics);
    	}else if(cmd.equals("goal")){
    		gra_layout.last(graphics);
    	}else if(cmd.equals("next")){
    		gra_layout.next(graphics);
    	}else if(cmd.equals("prev")){
    		gra_layout.previous(graphics);
    	}else if(cmd.equals("OK")){
    		
    	}
    	else{
    		layout.last(panel);
    	}
        
    }
	
	private void loadData(){
		
	}
	
	public static void main(String[] args) {
		//
		
		SampleGUI gui = new SampleGUI();
		gui.setVisible(true);
	}
	
}
