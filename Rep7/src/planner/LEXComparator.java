package planner;
import java.util.Comparator;

public class LEXComparator implements Comparator<Operator> {

    //比較メソッド（データクラスを比較して-1, 0, 1を返すように記述する）
    public int compare(Operator a, Operator b) {
    	int count = 0;
    	count = a.times.size();
    	if(b.times.size()<a.times.size()){
    		count = b.times.size();
    	}
    	
        for(int i=0; i<count;i++){

        if (a.times.get(i)> b.times.get(i)) {
            return 1;

        } else if (a.times.get(i) == b.times.get(i)) {
            return 0;

        } else {
            return -1;

        }
    }
        return 0;
    }
}
