package gui;

import java.util.List;

public interface PlannerController {

	/**
	 * 箱を作って置く
	 * @param name 箱の名前
	 * @param on 下敷きになるオブジェクトの名前, null か空白なら table 上
	 */
	public void putBox(String name, String on);
	
	/**
	 * target をピックアップする
	 * @param target
	 */
	public void pickup(String target) throws InterruptedException;
	
	/**
	 * to の上に今持っている箱を置く
	 * @param to
	 */
	public void place(String to) throws InterruptedException;
	
	/**
	 * 現在の状態を出力する
	 * @return
	 */
	public List<String> getStates();
}
