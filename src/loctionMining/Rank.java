package loctionMining;


public class Rank implements Comparable<Rank>{
	 double s;
	 int identity;
	public Rank(int identity, double score){
		this.s = score;
		this.identity = identity;
	}
	@Override
	public int compareTo(Rank o) {
		return (int) (o.s*10000 - this.s*10000 );
	}
}