package gui;

import javax.swing.JFrame;

import java.awt.BorderLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;

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

import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;

import javax.swing.JTextArea;
import javax.swing.JLabel;

import java.awt.event.*;

public class SampleGUI extends JFrame implements ActionListener{

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
		
		JPanel gra_start = new JPanel();//初期状態のエリア
		gra_start.add(new JLabel("start"));
		//gra_start.setBackground(Color.RED);
		JPanel gra_goal = new JPanel();//目標状態のエリア
		gra_goal.add(new JLabel("goal"));
		//gra_goal.setBackground(Color.GREEN);
		graphics.setLayout(gra_layout);
		graphics.add(gra_start);
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
		card1.setLayout(new BorderLayout());
		card1.add("Center", graphics);
		card1.add("South", btnPanel);
		
		//card1.add(tab1);
		
		
		//テキストで表示する
		JPanel card2 = new JPanel();
		card2.setLayout(new GridLayout(1,4));
		JTextArea start2 = new JTextArea("aaa");//初期状態のエリア
		JTextArea goal2 = new JTextArea("bbb");//目標状態のエリア
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
    	}else{
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
