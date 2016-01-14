package loctionMining;

class Locations implements Comparable<Locations>{
	String location;
	int counter;
	public Locations(){
	}
	public Locations(String l){
		this.location = l;
		this.counter = 1;
	}
	public Locations(String l,int c){
		this.location = l;
		this.counter = c;
	}
	@Override
	public int compareTo(Locations o) {
		return (int) (this.counter - o.counter);
	}
}
