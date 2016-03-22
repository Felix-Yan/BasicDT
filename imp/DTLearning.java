package imp;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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

	/**
	 * This constructs a learned decision tree from helpers.
	 * @param train. Helper keeping all the training set instances.
	 * @param test. Helper keeping all the testing set instances.
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public DTLearning(Helper train, Helper test) throws FileNotFoundException, UnsupportedEncodingException {
		trainingSet = train;
		testingSet = test;
		categoryNames = trainingSet.getCategoryNames();
		attNames = trainingSet.getAttNames();
		root = BuildTree(trainingSet.getAllInstances(), attNames);
		printTree();
		//testAccuracy();
	}

	/**
	 * This prints out the decision tree in a text format
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	private void printTree() throws FileNotFoundException, UnsupportedEncodingException{
		String filename = trainingSet.directory+"tree.txt";
		PrintWriter treeWriter = new PrintWriter(filename, "UTF-8");
		String indentation = "";
		printTreeRec(root, treeWriter, indentation);
		treeWriter.close();
	}

	/**
	 * This prints out the decision tree nodes recusively.
	 */
	private void printTreeRec(Node node, PrintWriter treeWriter, String indent){
		if(node.left != null){
			String line = indent + node.name + " = " + "True:";
			treeWriter.println(line);
			printTreeRec(node.left, treeWriter, indent+"\t");
		}
		if(node.right != null){
			String line = indent + node.name + " = " + "False:";
			treeWriter.println(line);
			printTreeRec(node.right, treeWriter, indent+"\t");
		}else{
			String line = indent + "Class "+node.name+", prob = "+((LeafNode)node).probability;
			treeWriter.println(line);
		}
	}

	/**
	 * This tests the prediction accuracy of the decision tree against a test set
	 */
	private void testAccuracy(){
		System.out.println("begin to test");//debug
		List<Helper.Instance> testsetInstances = testingSet.getAllInstances();
		int count = 0;
		for(Helper.Instance i: testsetInstances){
			if(testInstance(i)){
				count++;
			}
		}
		double accuracy = count*1.0/testsetInstances.size();
		System.out.printf("The accuracy is %.2f \n"+accuracy);

	}

	/**
	 * This tests the correctness of a single instance prediction
	 * @return
	 */
	private boolean testInstance(Helper.Instance instance){
		Node current = root;
		while(current.left != null){
			if(instance.getAtt(current.attIndex)){
				current = root.left;
			}else{
				current = root.right;
			}
		}
		if(current.name.equals(categoryNames.get(instance.getCategory()))){
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
		List<Helper.Instance> newInstances = newInstances(instances);
		List<String> newAttributes = newAttributes(attributes);
		if(newInstances.isEmpty()){
			//return the overall most probable class node
			return findMostProbableNode(trainingSet.getAllInstances());
		}

		if(isPure(newInstances)){
			int category = newInstances.get(0).getCategory();
			String name = categoryNames.get(category);
			return new LeafNode(null,null,name,category,1);
		}

		if(newAttributes.isEmpty()){
			//return the majority class node
			return findMostProbableNode(newInstances);
		}
		else{
			int bestAttribute = findBestAttribute(newInstances, newAttributes);
			List<Helper.Instance> bestInstsTrue = findTrueList(newInstances, bestAttribute);
			//now instances represents bestInstsFalse.
			newInstances.removeAll(bestInstsTrue);
			//remove the current best attributes by setting it to null. Size does not change.
			newAttributes.set(bestAttribute, null);
			Node left = BuildTree(bestInstsTrue, newAttributes);
			Node right = BuildTree(newInstances, newAttributes);
			return new Node(left, right, attNames.get(bestAttribute), bestAttribute);
		}
	}

	/**
	 * This copies the list of given instances to a new list with a new reference
	 * @param instances
	 * @return
	 */
	private List<Helper.Instance> newInstances(List<Helper.Instance> instances){
		List<Helper.Instance> newInstances = new ArrayList<Helper.Instance>();
		for(Helper.Instance i: instances){
			newInstances.add(i);
		}
		return newInstances;
	}

	/**
	 * This copies the list of attributes to a new list with a new reference
	 * @param attributes
	 * @return
	 */
	private List<String> newAttributes(List<String> attributes){
		List<String> newAttributes = new ArrayList<String>();
		for(String s: attributes){
			newAttributes.add(s);
		}
		return newAttributes;
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
		return new LeafNode(null,null,name,maxIndex, probability);
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
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
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
		public final String name;
		public final int attIndex;
		/**
		 * @param left Left child Node.
		 * @param right Right child Node.
		 * @param attribute The attribute evaluated in this node.
		 * @param index The position of the attribute name
		 */
		public Node(Node left, Node right, String name, int index) {
			this.left = left;
			this.right = right;
			this.name = name;
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
		public LeafNode(Node left, Node right, String name, int index, double p) {
			super(left, right, name, index);
			probability = p;
		}
	}

}
