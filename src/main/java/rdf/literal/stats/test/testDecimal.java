package rdf.literal.stats.test;

public class testDecimal {
	
	public static void main(String[] args) {
		
		
		int value = 301551;
		int [] ss = {1,2,3,4,5,6,7,8,9,10,100,1000,10000,100000,1000000};
		for (int i = 0; i < ss.length; i++) {
			if (ss[i] >= value || ss[i+1] > value){
				System.out.println(ss[i]);
				break;
			}
		}
		
		
	}

}
