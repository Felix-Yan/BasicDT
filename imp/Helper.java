package imp;

import java.util.*;
import java.io.*;

/**
 * A helper class to read data file into instances.
 * @author yanlong
 *
 */
public class Helper {
	private int numCategories;
	private int numAtts;
	private List<String> categoryNames;
	private List<String> attNames;
	private List<Instance> allInstances;
	//This is where the data file should be
	public final String directory = "src/ass1-data/part2/";

	/**
	 * This consturcts a Helper from the given name of data file.
	 * @param fname is the name string of the data file to be read.
	 */
	public Helper(String fname) {
		readDataFile(fname);
	}


	private void readDataFile(String fname){
		/* format of names file:
		 * names of categories, separated by spaces
		 * names of attributes
		 * category followed by true's and false's for each instance
		 */
		fname = directory + fname;
		System.out.println("Reading data from file "+fname);
		try {
			Scanner din = new Scanner(new File(fname));

			categoryNames = new ArrayList<String>();
			for (Scanner s = new Scanner(din.nextLine()); s.hasNext();) categoryNames.add(s.next());
			numCategories=categoryNames.size();
			System.out.println(numCategories +" categories");

			attNames = new ArrayList<String>();
			for (Scanner s = new Scanner(din.nextLine()); s.hasNext();) attNames.add(s.next());
			numAtts = attNames.size();
			System.out.println(numAtts +" attributes");

			allInstances = readInstances(din);
			din.close();
		}
		catch (IOException e) {
			throw new RuntimeException("Data File caused IO exception");
		}
	}


	private List<Instance> readInstances(Scanner din){
		/* instance = classname and space separated attribute values */
		List<Instance> instances = new ArrayList<Instance>();
		String ln;
		while (din.hasNext()){
			Scanner line = new Scanner(din.nextLine());
			instances.add(new Instance(categoryNames.indexOf(line.next()),line));
		}
		System.out.println("Read " + instances.size()+" instances");
		return instances;
	}



	/**
	 * @return the categoryNames
	 */
	public List<String> getCategoryNames() {
		return categoryNames;
	}


	/**
	 * @return the attNames
	 */
	public List<String> getAttNames() {
		return attNames;
	}


	/**
	 * @return the allInstances
	 */
	public List<Instance> getAllInstances() {
		return allInstances;
	}


	/**
	 * @return the numCategories
	 */
	public int getNumCategories() {
		return numCategories;
	}



	/**
	 * This stores the category and attributes of an instance.
	 * @author yanlong
	 *
	 */
	public class Instance {
		/**
		 * Index of the category in categoryName List.
		 * live should be 0, die should be 1.
		 */
		private int category;
		private List<Boolean> vals;

		public Instance(int cat, Scanner s){
			category = cat;
			vals = new ArrayList<Boolean>();
			while (s.hasNextBoolean()) vals.add(s.nextBoolean());
		}

		public boolean getAtt(int index){
			return vals.get(index);
		}

		public int getCategory(){
			return category;
		}

		public String toString(){
			StringBuilder ans = new StringBuilder(categoryNames.get(category));
			ans.append(" ");
			for (Boolean val : vals)
				ans.append(val?"true  ":"false ");
			return ans.toString();
		}

	}

}
