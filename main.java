import java.util.*;

public class main {

	public static void main(String[] args) {
		
		ArrayList<String> x = new ArrayList<String>();
		x.add("english tutorial and fast track");
		x.add("learning latent semantic indexing");
		x.add("book on semantic indexing");
		x.add("advance in structure and semantic indexing");
		x.add("analysis of latent structures");
		
		BooleanModel BM = new BooleanModel(x);
		BM.BuildModel();
		System.out.println(BM.toString());
		BM.ProcessQuery("advance and structure AND NOT analysis");
		
		System.out.println();
		System.out.println();
		
		InvertedIndexModel IIM = new InvertedIndexModel(x);
		IIM.BuildModel();
		System.out.println(IIM.toString());
		IIM.ProcessQuery("advance and structure AND NOT analysis");
		
	}

}
