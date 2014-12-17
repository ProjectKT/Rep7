package gui;

import javax.swing.JFrame;

import java.awt.BorderLayout;

import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import javax.swing.JMenu;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.Toolkit;

import javax.swing.JRadioButtonMenuItem;

public class SampleGUI extends JFrame{

	// コンストラクタ
	public SampleGUI() {
		initialize();
		
		loadData();
		//setupSuffixArray();
		
		//set();
		setVisible(true);
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
		
		JMenu mnNewMenu = new JMenu("Query");
		menuBar.add(mnNewMenu);
		
		JRadioButtonMenuItem rdbtnmntmNewRadioItem = new JRadioButtonMenuItem("Voise");
		mnNewMenu.add(rdbtnmntmNewRadioItem);
		
		JRadioButtonMenuItem rdbtnmntmNewRadioItem_1 = new JRadioButtonMenuItem("Text");
		mnNewMenu.add(rdbtnmntmNewRadioItem_1);
		rdbtnmntmNewRadioItem_1.setSelected(true);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnmntmNewRadioItem);
		group.add(rdbtnmntmNewRadioItem_1);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setPreferredSize(new Dimension(200, 100));
		panel.add(tabbedPane, BorderLayout.WEST);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("New tab", null, panel_1, null);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("New tab", null, panel_2, null);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("New tab", null, panel_3, null);
		
		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		panel.add(tabbedPane_1, BorderLayout.CENTER);
		
		JPanel panel_4 = new JPanel();
		tabbedPane_1.addTab("New tab", null, panel_4, null);
		
		JTabbedPane tabbedPane_2 = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_2.setPreferredSize(new Dimension(200, 100));
		panel.add(tabbedPane_2, BorderLayout.EAST);
		
		JPanel panel_5 = new JPanel();
		tabbedPane_2.addTab("New tab", null, panel_5, null);
		
		JTabbedPane tabbedPane_3 = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_3.setPreferredSize(new Dimension(150, 200));
		panel.add(tabbedPane_3, BorderLayout.SOUTH);
		
		JPanel panel_6 = new JPanel();
		tabbedPane_3.addTab("New tab", null, panel_6, null);
		
	}
	
	private void loadData(){
		
	}
	
	public static void main(String[] args) {
		//
		
		SampleGUI gui = new SampleGUI();
		gui.setVisible(true);
	}
	
}
