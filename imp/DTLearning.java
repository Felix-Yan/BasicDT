package imp;

/**
 * This builds a learned decision tree.
 * @author yanlong
 *
 */
public class DTLearning {
	
	/**
	 * The main method.
	 * @param args
	 */
	public static void main(String[] args){
		if(args.length<1) return;
		new Helper(args[0]);
	}

}
