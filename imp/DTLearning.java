package imp;

import java.util.ArrayList;
import java.util.List;

/**
 * This builds a learned decision tree.
 * @author yanlong
 *
 */
public class DTLearning {
	private Helper trainingSet;
	private Helper testingSet;
	private List<String> categoryNames;
	private List<String> attNames;
	private Node root;//root node of the decision tree
	public static final int DUMMY_INDEX = 999;//only to satisfy the node constructor

	/**
	 * This constructs a learned decision tree from helpers.
	 * @param train. Helper keeping all the training set instances.
	 * @param test. Helper keeping all the testing set instances.
	 */
	public DTLearning(Helper train, Helper test) {
		trainingSet = train;
		testingSet = test;
		categoryNames = trainingSet.getCategoryNames();
		attNames = trainingSet.getAttNames();
		root = BuildTree(trainingSet.getAllInstances(), attNames);
	}

	/**
	 * This tests the prediction accuracy of the decision tree against a test set
	 */
	private void testAccuracy(){
		List<Helper.Instance> testInstances = testingSet.getAllInstances();

	}

	/**
	 * This tests the correctness of a single instance prediction
	 * @return
	 */
	private boolean testInstance(Helper.Instance instance){
		Node current = root;
		while(current.attIndex != DUMMY_INDEX){
			if(instance.getAtt(current.attIndex)){
				current = root.left;
			}else{
				current = root.right;
			}
		}
		if(current.attribute.equals(categoryNames.get(instance.getCategory()))){
			return true;//true if the predicted class name is the same as the instance class
		}
		return false;//false otherwise
	}

	/**
	 * This builds the decision tree recursively.
	 * @param insatnces
	 * @param attibutes
	 */
	private Node BuildTree(List<Helper.Instance> instances, List<String> attributes){
		if(instances.isEmpty()){
			//return the overall most probable class node
			return findMostProbableNode(trainingSet.getAllInstances());
		}

		if(isPure(instances)){
			int category = instances.get(0).getCategory();
			String name = categoryNames.get(category);
			return new LeafNode(null,null,name,DUMMY_INDEX,1);
		}

		if(attributes.isEmpty()){
			//return the majority class node
			return findMostProbableNode(instances);
		}
		else{
			int bestAttribute = findBestAttribute(instances, attributes);
			List<Helper.Instance> bestInstsTrue = findTrueList(instances, bestAttribute);
			//now instances represents bestInstsFalse.
			instances.removeAll(bestInstsTrue);
			//remove the current best attributes by setting it to null. Size does not change.
			attributes.set(bestAttribute, null);
			Node left = BuildTree(bestInstsTrue, attributes);
			Node right = BuildTree(instances, attributes);
			return new Node(left, right, attNames.get(bestAttribute), bestAttribute);
		}
	}

	/**
	 * This finds out the best attribute, which has the smallest impurity.
	 * @param instances
	 * @return
	 */
	private int findBestAttribute(List<Helper.Instance> instances, List<String> attributes){
		double minImpurity = Double.MAX_VALUE;
		int bestAttribute = 0;
		for(int i=0; i<attributes.size(); i++){
			if(attributes.get(i) == null){
				continue;//The attribute has been removed in previous nodes. Compute next attribute.
			}
			double impurity = computeImpurity(instances, i);
			if(impurity < minImpurity){
				minImpurity = impurity;
				bestAttribute = i;
			}
		}
		return bestAttribute;
	}

	/**
	 * This computes the impurity of given instances by using formula A/(A+B) * B/(A+B), where A and B are two sets of
	 * the instances with opposite value of the attribute.
	 * @param instances
	 * @param index The index of attribute in attNames.
	 * @return
	 */
	private double computeImpurity(List<Helper.Instance> instances, int index ){
		List<Helper.Instance> trueList = findTrueList(instances, index);
		int A = trueList.size();
		int B = instances.size() - trueList.size();
		double impurity = A*1.0/(A+B)*B/(A+B);
		return impurity;
	}

	/**
	 * This finds out the list of instances that has the true value of the given attribute
	 * @param instances
	 * @param index
	 * @return
	 */
	private List<Helper.Instance> findTrueList(List<Helper.Instance> instances, int index){
		List<Helper.Instance> trueList = new ArrayList<Helper.Instance>();
		for(Helper.Instance i: instances){
			if(i.getAtt(index)){
				trueList.add(i);
			}
		}
		return trueList;
	}

	/**
	 * This finds out the most probable class in the form of a leaf node, from the given instances.
	 * @return
	 */
	private LeafNode findMostProbableNode(List<Helper.Instance> instances){
		int numCategories = trainingSet.getNumCategories();
		int[] counts = new int[numCategories];
		for(Helper.Instance i: instances){
			int category = i.getCategory();
			counts[category]+=1;
		}
		int maxIndex = findMaxIndex(counts);
		double probability = counts[maxIndex]*1.0/instances.size();
		String name = categoryNames.get(maxIndex);
		return new LeafNode(null,null,name,DUMMY_INDEX, probability);
	}

	/**
	 * This finds out the index of the max element in the given counts array
	 * @param counts. The array of category counts.
	 * @return The index of the max
	 */
	private int findMaxIndex(int[] counts){
		int maxIndex = 0;
		int max = counts[0];
		for(int i=1; i<counts.length; i++){
			if(counts[i]>max){
				max = counts[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	/**
	 * This checks if the given list of instances is pure.
	 * @return
	 */
	private boolean isPure(List<Helper.Instance> instances){
		int previous = 10;//initialize previous category with a dummy value
		for(Helper.Instance i: instances){
			if(previous == 10){//assign the first category
				previous = i.getCategory();
			}else{
				if(previous != i.getCategory()){
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * The main method.
	 * @param args
	 */
	public static void main(String[] args){
		if(args.length<2){
			System.out.println("You need to give two file names for training and testing");
			return;
		}
		Helper training = new Helper(args[0]);
		Helper testing = new Helper(args[1]);
		new DTLearning(training, testing);
	}

	/**
	 * The decision node used in the decision tree.
	 * @author yanlong
	 *
	 */
	private class Node{
		public final Node left;
		public final Node right;
		public final String attribute;
		public final int attIndex;
		/**
		 * @param left Left child Node.
		 * @param right Right child Node.
		 * @param attribute The attribute evaluated in this node.
		 */
		public Node(Node left, Node right, String attribute, int index) {
			this.left = left;
			this.right = right;
			this.attribute = attribute;
			this.attIndex = index;
		}
	}

	/**
	 * The leaf node contains the name of the class and the probability of it.
	 * @author yanlong
	 *
	 */
	private class LeafNode extends Node{
		public final double probability;
		/**
		 * @param left
		 * @param right
		 * @param attribute
		 * @param p The probability of the most probable class.
		 */
		public LeafNode(Node left, Node right, String attribute, int index, double p) {
			super(left, right, attribute, index);
			probability = p;
		}
	}

}
