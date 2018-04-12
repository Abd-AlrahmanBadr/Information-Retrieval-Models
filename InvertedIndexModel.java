import java.util.*;

public class InvertedIndexModel {

	private int NumberofDocuments;
	private ArrayList<String> Documents;
	
	private Hashtable<String, Set<Integer>> PostingList;
	
	public InvertedIndexModel(ArrayList<String> Docs) {
		PostingList = new Hashtable<String, Set<Integer>>();
		Documents = Docs;
		NumberofDocuments = Docs.size();
	}
	
	public int getNumberofDocuments() {
		return NumberofDocuments;
	}

	public void BuildModel() {
		Set<Integer> tmp;
		String Token;
		for(int i = 0;i < NumberofDocuments;i++) {
			ArrayList<String> Splitted = new ArrayList<String>(Arrays.asList(Documents.get(i).split(" ")));
			int SplittedSize = Splitted.size();
			for(int j = 0;j < SplittedSize;j++) {
				Token = Splitted.get(j);
				if(PostingList.get(Token) == null) PostingList.put(Token, new TreeSet<Integer>());
				tmp = PostingList.get(Token);
				tmp.add(i);
				PostingList.put(Token, tmp);
			}
		}
	}
	
	public void ProcessQuery(String Query){
		Query = PrepQuery(Query);
		System.out.println("Prepared Query : " + Query);
		
		Stack<Integer> OpenPraceIndex = new Stack<Integer>();
		int i = 0;
		String tmp;
		while(i < Query.length()){			
			if(Query.charAt(i) == '(') OpenPraceIndex.push(i);
			else if(Query.charAt(i) == ')'){
				tmp = Calc(Query.substring(OpenPraceIndex.peek() + 2, i - 1));
				Query = Query.replace(Query.substring(OpenPraceIndex.peek(), i + 1), tmp);
				i = OpenPraceIndex.peek();
				OpenPraceIndex.pop();
			}
			i++;
		}
		String Result = Calc(Query);
		
		ArrayList<Integer> MatchedDocs = new ArrayList<Integer>();
		String[] Splitted = Result.split("_");
		int SplittedSize = Splitted.length;
		for(i = 0;i < SplittedSize;i++) 
			MatchedDocs.add(Integer.parseInt(Splitted[i]) + 1);
		
		System.out.print("Matched Documents : ");
		for(i = 0;i < MatchedDocs.size();i++) {
			System.out.print("Document #" + MatchedDocs.get(i));
			if(i < MatchedDocs.size() - 2) System.out.print(", ");
			else if(i < MatchedDocs.size() - 1) System.out.print(" and ");
		}
		if(MatchedDocs.size() == 0) System.out.print("No Match");
		System.out.println(".");
	}
	
	private String PrepQuery(String Query){
		StringBuilder NewQuery = new StringBuilder(Query);
		for(int i = 0;i < NewQuery.length();i++){
			if(NewQuery.charAt(i) == '(' && NewQuery.charAt(i + 1) != ' '){
				if(i - 1 >= 0 && 	NewQuery.charAt(i - 1) != ' ') NewQuery.insert(i, ' ');
				NewQuery.insert(i + 1, ' ');
			}
			else if(NewQuery.charAt(i) == ')' && NewQuery.charAt(i - 1) != ' '){
				NewQuery.insert(i, ' ');
				if(i + 2 < NewQuery.length() && NewQuery.charAt(i + 2) != ' ') NewQuery.insert(i + 2, ' ');
			}
		}
		return NewQuery.toString();
	}
	
	private String Calc(String Query){
		Scanner sc = new Scanner(Query);
		String tmp1;
		String NewQuery = new String("");
		while(sc.hasNext()){
			tmp1 = sc.next();
			if(tmp1.toUpperCase().equals("NOT")){
				tmp1 = sc.next();
				tmp1 = NOT(Token2PostingList(tmp1));
			}
			NewQuery += (" " + tmp1);
		}
		sc.close();
		
		sc = new Scanner(NewQuery);
		Stack<String> st = new Stack<String>();
		String tmp2;
		while(sc.hasNext()){
			tmp1 = sc.next();
			if(tmp1.toUpperCase().equals("OR")){
				tmp1 = st.pop();
				tmp2 = sc.next();
				tmp1 = OR(Token2PostingList(tmp1), Token2PostingList(tmp2));
			}else if(tmp1.toUpperCase().equals("AND")) continue;
			st.push(tmp1);
		}
		sc.close();
		
		if(st.size() == 1){
			ArrayList<Integer> tmp3 = Token2PostingList(st.pop());
			String tmp4 = new String("");
			int tmp3Size = tmp3.size();
			for(int i = 0;i < tmp3Size;i++) tmp4 += (tmp3.get(i) + "_");
			return tmp4;
		}
		
		tmp1 = st.pop();
		while(!st.isEmpty()) tmp1 = AND(Token2PostingList(tmp1), Token2PostingList(st.pop()));
		
		return tmp1;
	}
	
	private ArrayList<Integer> Token2PostingList(String Token){
		if(PostingList.get(Token) != null)
			return new ArrayList<Integer>(PostingList.get(Token));
		
		ArrayList<Integer> PostingList = new ArrayList<Integer>();
		String[] Splitted = Token.split("_");
		int SplittedSize = Splitted.length;
		for(int i = 0;i < SplittedSize;i++) 
			PostingList.add(Integer.parseInt(Splitted[i]));
		return PostingList;
	}
	
	private String NOT(ArrayList<Integer> TokenPostingList){
		String Result = new String("");
		int ListSize = TokenPostingList.size();
		for(int i = 0,j = 0;i < NumberofDocuments;i++) {
			if(j >= ListSize || TokenPostingList.get(j) != i) Result += (i + "_");
			else j++;
		}
		return Result;
	}

	private String OR(ArrayList<Integer> TokenPostingList1, ArrayList<Integer> TokenPostingList2){
		Set<Integer> tmp = new TreeSet<Integer>();
		int List1Size = TokenPostingList1.size(), List2Size = TokenPostingList2.size();
		for(int i = 0;i < List1Size;i++) tmp.add(TokenPostingList1.get(i));
		for(int i = 0;i < List2Size;i++) tmp.add(TokenPostingList2.get(i));
		String Result = new String("");
		for(int x : tmp)
			Result += (x + "_");
		return Result;
	}
	
	private String AND(ArrayList<Integer> TokenPostingList1, ArrayList<Integer> TokenPostingList2){
		String Result = new String("");
		int i = 0, j = 0, List1Size = TokenPostingList1.size(), List2Size = TokenPostingList2.size();
		while(i < List1Size && j < List2Size) {
			if(TokenPostingList1.get(i) == TokenPostingList2.get(j)) {
				Result += (TokenPostingList1.get(i) + "_");
				i++;
				j++;
			}else {
				if(TokenPostingList1.get(i) > TokenPostingList2.get(j)) j++;
				else i++;
			}
		}
		return Result;
	}
	
	public String toString() {
		String R = new String("");
		R += "Number of Documents : " + NumberofDocuments + "\n\n";
		R += "Documents of the Model : " + "\n";
		for(int i = 0;i < NumberofDocuments;i++)
			R += "\tDocument #" + (i + 1) + ": " + Documents.get(i) + "\n";
		R += "\n";
		R += "Number of Tokens : " + PostingList.size() + "\n\n";
		R += "Tokens : " + PostingList.keySet() + "\n\n";
		R += "--------------------Inverted Index Model--------------------\n"; 
		Set<String> Tokens = new TreeSet<String>(PostingList.keySet());
		R += "Token\t\tDoc. Freq.\tPostingList\n";
		R += "------------------------------------------------------------\n";
		for(String Token : Tokens)
			R += Token + "\t\t" + PostingList.get(Token).size() + "\t\t" + PostingList.get(Token) + "\n";
		return R;
	}
	
}