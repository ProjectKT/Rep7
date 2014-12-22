package gui;

import gui.objects.Box;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;

public class SampleGUI extends JFrame{
	
	PhysicsPanel physicsPanel = null;

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
		
		JMenu mnNewMenu_1 = new JMenu("State");
		menuBar.add(mnNewMenu_1);
		
		JRadioButtonMenuItem rdbtnmntmNewRadioItem_2 = new JRadioButtonMenuItem("Graphics");
		rdbtnmntmNewRadioItem_2.setSelected(true);
		mnNewMenu_1.add(rdbtnmntmNewRadioItem_2);
		
		JRadioButtonMenuItem rdbtnmntmNewRadioItem_3 = new JRadioButtonMenuItem("Text");
		mnNewMenu_1.add(rdbtnmntmNewRadioItem_3);
		
		ButtonGroup group2 = new ButtonGroup();
		group2.add(rdbtnmntmNewRadioItem_2);
		group2.add(rdbtnmntmNewRadioItem_3);

		JMenu mnNewMenu_2 = new JMenu("Test");
		menuBar.add(mnNewMenu_2);
		
		JMenuItem mntmNewItem = new JMenuItem("Add component");
		mntmNewItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Box(100, 100).attachTo(physicsPanel);
			}
		});
		mnNewMenu_2.add(mntmNewItem);
		
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
		
		physicsPanel = new PhysicsPanel();
		panel.add(physicsPanel, BorderLayout.CENTER);
		
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
