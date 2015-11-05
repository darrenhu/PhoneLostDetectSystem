package loctionMining;

import java.util.ArrayList;

public class resultList implements Comparable<resultList> {
	ArrayList<Double> results = new ArrayList<Double>();

	public resultList() {
		for (int i = 0; i < 16; i++) {
			this.results.add(1.0);
		}
	}

	public resultList(ArrayList<Double> x) {
		for (int i = 0; i < 16; i++) {
			this.results.add(x.get(i));
		}
	}

	@Override
	public int compareTo(resultList o) {
		if ((int) (this.results.get(1) * 10000 - o.results.get(1) * 10000) == 0) {
			return (int) (o.results.get(0) * 10000 - this.results.get(0) * 10000);
		} else {
			return (int) (this.results.get(1) * 10000 - o.results.get(1) * 10000);
		}

	}
	// @Override
	// public String toString() {
	// return "results TPR " + results.get(0) + ", "+results.get(1)" FPR " +
	// results.get(2) + ", "+results.get(3)+" TP " + results.get(4) +
	// ", "+results.get(5)+" TP " + results.get(4) + ", "+results.get(5);
	// }
}
