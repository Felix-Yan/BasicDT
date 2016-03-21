package imp;

import java.util.ArrayList;
import java.util.List;

/**
 * This builds a learned decision tree.
 * @author yanlong
 *
 */
public class DTLearning {
	Helper trainingSet;
	Helper testingSet;
	List<String> categoryNames;
	List<String> attNames;
	Node root;//root node of the decision tree

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
			return new LeafNode(null,null,name,1);
		}

		if(attributes.isEmpty()){
			//return the majority class node
			return findMostProbableNode(instances);
		}
		else{
		}
		return null;//to compile
	}

	private double computePurity(List<Helper.Instance> instances ){
		return 0; //to compile
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
		return new LeafNode(null,null,name,probability);
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
		if(args.length<2) return;
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
		/**
		 * @param left Left child Node.
		 * @param right Right child Node.
		 * @param attribute The attribute evaluated in this node.
		 */
		public Node(Node left, Node right, String attribute) {
			this.left = left;
			this.right = right;
			this.attribute = attribute;
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
		public LeafNode(Node left, Node right, String attribute, double p) {
			super(left, right, attribute);
			probability = p;
		}
	}

}
