package plannerTest;

public class NOAHThread implements Runnable{
	
	//このスレッドが担当するゴール状態
	String goal;
	//以下二つはほかのスレッドと共有するので排他制御必須
	NOAHParameters nPara;
	NOAHPlan nPlan;
	
	NOAHThread(String goal,NOAHParameters nPara,NOAHPlan nPlan){
		this.goal = goal;
		this.nPara = nPara;
		this.nPlan = nPlan;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		goalExploder(goal);
	}
	
	//担当するゴール状態についてプランを展開する
	private void goalExploder(String goal){
		System.out.println(goal);
	}

}
