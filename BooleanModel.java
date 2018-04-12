import java.util.*;

public class BooleanModel {
	
	private int NumberofDocuments;
	private List<String> Documents;
	private int NumberofTokens;
	private ArrayList<String> AllTokens;
	private List<HashSet<String>> TokensPerDocument;
	private List<List<Boolean>> BooleanModelMatrix;
	
	public BooleanModel(ArrayList<String> Docs) {
		NumberofDocuments = Docs.size();
		Documents = Docs;
	}

	public int getNumberofDocuments() {
		return NumberofDocuments;
	}
	
	public void BuildModel() {
		TokensPerDocument = new ArrayList<HashSet<String>>();
		Set<String> tmp = new HashSet<String>();
		for(int i = 0;i < NumberofDocuments;i++) {
			ArrayList<String> Splitted = new ArrayList<String>(Arrays.asList(Documents.get(i).split(" ")));
			int SplittedSize = Splitted.size();
			TokensPerDocument.add(new HashSet<String>());
			for(int j = 0;j < SplittedSize;j++) {
				TokensPerDocument.get(i).add(Splitted.get(j));
				tmp.add(Splitted.get(j));
			}
		}
		AllTokens = new ArrayList<String>(tmp);
		NumberofTokens = AllTokens.size();
		BooleanModelMatrix = new ArrayList<List<Boolean>>();
		for(int i = 0;i < NumberofDocuments;i++) {
			BooleanModelMatrix.add(new ArrayList<Boolean>());
			for(String Token : AllTokens) {
				if(TokensPerDocument.get(i).contains(Token)) BooleanModelMatrix.get(i).add(true);
				else BooleanModelMatrix.get(i).add(false);
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
		System.out.println("Binary Vector : " + Result);
		ArrayList<Integer> MatchedDocs =  new ArrayList<Integer>();
		for(i = 0;i < Result.length();i++)
			if(Result.charAt(i) == '1')
				MatchedDocs.add(i + 1);
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
		while(sc.hasNext()) {
			tmp1 = sc.next();
			if(tmp1.toUpperCase().equals("NOT")) {
				tmp1 = sc.next();
				tmp1 = NOT(Token2BooleanString(tmp1));
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
				tmp1 = OR(Token2BooleanString(tmp1), Token2BooleanString(tmp2));
			}else if(tmp1.toUpperCase().equals("AND")) continue;
			st.push(tmp1);
		}
		sc.close();
		
		if(st.size() == 1){
			return Token2BooleanString(st.pop());
		}
		
		tmp1 = st.pop();
		while(!st.isEmpty()) tmp1 = AND(Token2BooleanString(tmp1), Token2BooleanString(st.pop()));

		return tmp1;
	}
	
	private String Token2BooleanString(String Token) {
		String NewToken;
		try{
			Integer.parseInt(Token);
			NewToken = Token;
		}catch(NumberFormatException e) { 
	        NewToken = new String("");
	        for(int i = 0, TokenIndex = AllTokens.indexOf(Token);i < NumberofDocuments;i++)
				NewToken += BooleanModelMatrix.get(i).get(TokenIndex) ? 1 : 0;
	    } catch(NullPointerException e) {
	    	NewToken = new String("");
	        for(int i = 0, TokenIndex = AllTokens.indexOf(Token);i < NumberofDocuments;i++)
				NewToken += BooleanModelMatrix.get(i).get(TokenIndex) ? 1 : 0;
	    }
		return NewToken;
	}
	
	private String NOT(String Token) {
		String Result = new String("");
		for(int i = 0;i < NumberofDocuments;i++)
			Result += Token.charAt(i) == '1' ? 0 : 1;
		return Result;
	}
	
	private String OR(String Token1, String Token2) {
		String Result = new String("");
		for(int i = 0;i < NumberofDocuments;i++)
			Result += Token1.charAt(i) == '1' || Token2.charAt(i) == '1' ? 1 : 0;
		return Result;
	}
	
	private String AND(String Token1, String Token2) {
		String Result = new String("");
		for(int i = 0;i < NumberofDocuments;i++)
			Result += Token1.charAt(i) == '1' && Token2.charAt(i) == '1' ? 1 : 0;
		return Result;
	}

	public String toString() {
		String R = new String("");
		R += "Number of Documents : " + NumberofDocuments + "\n\n";
		R += "Documents of the Model : " + "\n";
		for(int i = 0;i < NumberofDocuments;i++)
			R += "\tDocument #" + (i + 1) + ": " + Documents.get(i) + "\n";
		R += "\n";
		R += "Number of Tokens : " + NumberofTokens + "\n\n";
		R += "Tokens : " + AllTokens + "\n\n";
		R += "------------------------BooleanModel------------------------\n"; 
		R += "\t";
		for(int i = 0;i < NumberofDocuments;i++) 
			R += "\t\tDocument #" + (i + 1);
		R += "\n------------------------------------------------------------\n";
		for(int i = 0;i < NumberofTokens;i++) {
			R += AllTokens.get(i);
			for(int j = 0;j < NumberofDocuments;j++)
				R += "\t\t\t" + BooleanModelMatrix.get(j).get(i);
			R += "\n";
		}
		return R;
	}

}
