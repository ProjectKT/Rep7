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
	
	/**
	 * 状態の変化を監視するリスナーを登録する
	 * @param l
	 */
	public void setStatesChangeListener(StatesChangeListener l);
	
	/**
	 * 状態の変化を監視するリスナー
	 */
	public interface StatesChangeListener {
		/**
		 * 状態が変化したときに呼ばれる
		 * @param states 状態
		 */
		public void onChangeStates(List<String> states);
	}
}
