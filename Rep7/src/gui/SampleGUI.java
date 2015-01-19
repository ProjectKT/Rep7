package gui;

import javax.swing.JFrame;

import java.awt.BorderLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JScrollPane;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.Toolkit;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;

import javax.swing.JTextArea;
import javax.swing.JLabel;

import plannerTest.NOAH;

import java.awt.event.*;
import java.util.ArrayList;

public class SampleGUI extends JFrame implements ActionListener{
	ArrayList<String> startList = new ArrayList<String>();
	ArrayList<String> goalList = new ArrayList<String>();
	ArrayList<String> ansList = new ArrayList<String>();

	JTextArea txtStart = new JTextArea("");//初期状態のエリア
	JTextArea txtGoal = new JTextArea("");//目標状態のエリア
	JTextArea txtAnswer = new JTextArea("");//解答のエリア
	
	// コンストラクタ
	public SampleGUI() {
		initialize();
		
		loadData();
		//setupSuffixArray();
		
		//set();
		setVisible(true);
	}
	JPanel panel = new JPanel();
	CardLayout layout = new CardLayout();
	
	JPanel graphics = new JPanel();
	CardLayout gra_layout = new CardLayout();
	
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
		rdbtnmntmNewRadioItem_2.setActionCommand("graphic");
		
		
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
		JPanel start1 = new JPanel();//初期状態の制作パネル
		start1.add(new JLabel("初期状態"));
		//start1.setBackground(Color.RED);
		JPanel goal1 = new JPanel();//目標状態の制作パネル
		goal1.add(new JLabel("目標状態"));
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
			if(i == 1){
				gra_process.add(new JLabel(s+"st process"));
			}else if(i == 2){
				gra_process.add(new JLabel(s+"nd process"));
			}else if(i == 3){
				gra_process.add(new JLabel(s+"rd process"));
				//gra_process.setBackground(Color.BLUE);
			}else{
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
		JPanel card2 = new JPanel();
		card2.setLayout(new GridLayout());
		
		EtchedBorder border = new EtchedBorder();
		

		
		//
		JTabbedPane tab2 = new JTabbedPane();
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
	}
	
    public void actionPerformed(ActionEvent e) {
    	String cmd = e.getActionCommand();
    	if(cmd == "graphic"){
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
    		
    		String strs1[] = txtStart.getText().split("\n");
    		for (int i = 0; i < strs1.length; i++) {
    		    startList.add(strs1[i]);
    		}
    		String strs2[] = txtGoal.getText().split("\n");
    		for (int i = 0; i < strs2.length; i++) {
    		    goalList.add(strs2[i]);
    		}
    		NOAH noah = new NOAH(goalList,startList);
    		noah.planning();
    		ansList = noah.getResult();
    		for(int i=0;i < ansList.size();i++)
    		txtAnswer.append(ansList.get(i)+"\n");
    		
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
	
	public static void main(String[] args) {
		//
		
		SampleGUI gui = new SampleGUI();
		gui.setVisible(true);
	}
}
