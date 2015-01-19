package gui;

public interface PlannerController {
	final String HOME = "home";
	final String BOX_A = "box_a";
	final String BOX_B = "box_b";
	final String BOX_C = "box_c";
	
	/**
	 * target をピックアップする
	 * @param target
	 */
	public void pickup(String target);
	
	/**
	 * to の上に今持っている箱を置く
	 * @param to
	 */
	public void place(String to);
}
