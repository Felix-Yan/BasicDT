package imp;

import java.util.List;

/**
 * This builds a learned decision tree.
 * @author yanlong
 *
 */
public class DTLearning {
	Helper trainingSet;
	Helper testingSet;
	
	/**
	 * This constructs a learned decision tree from helpers.
	 * @param train. Helper keeping all the training set instances.
	 * @param test. Helper keeping all the testing set instances.
	 */
	public DTLearning(Helper train, Helper test) {
		trainingSet = train;
		testingSet = test;
	}
	
	/**
	 * This builds the decision tree.
	 * @param insatnces
	 * @param attibutes
	 */
	public void BuildTree(List<Helper.Instance> insatnces, List<String> attibutes){
		
	}
	
	/**
	 * This checks if the given list of instances is pure.
	 * @return
	 */
	public boolean isPure(List<Helper.Instance> instances){
		return true;
	}

	/**
	 * The main method.
	 * @param args
	 */
	public static void main(String[] args){
		if(args.length<2) return;
		Helper training = new Helper(args[0]);
		Helper testing = new Helper(args[1]);
		new DTLearning(training, testing);
	}

}
