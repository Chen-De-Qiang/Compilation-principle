
 package grammaticalanalysis;

 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.Stack;
 import java.util.HashSet;


/**LL(1)语法分析的入口函数
 * 实验目标：
 * 		完成以下描述算术表达式的LL(1)文法的分析表
		G[P]: 
        P→S|Q|; 
		S→V=E;
		E→TR
		R→ATR|$
		T→FY
		Y→MFY|$
		F→CZ
		Z→OCZ|$
		C→BI
		I→XBI|$
		B→(E)|i
		A→+|-
		M→*|/
		X→a|o           //a表示逻辑符号&&，o表示逻辑符号||
		O→t|d|g|l|u|e  //t表示>=，d表示<=，g表示>,l表示<,e表示==，u表示!=
		V→i
		Q→8JKH          //8表示if在符号表中序号
		H→fJKH|9K|$     //f 表示 else if符号的组合、9表示else在符号表中的序号
		J→(E)           //逻辑语句
		K→S|{U}|;       //if语句程序体
		U→PU|{U}U|$

 * 实验说明：
 * 		终结符号i为用户定义的简单变量，即标识符的定义。
 * 实验要求：
 * 		1）输入串应是词法分析的输出二元式序列，即某算术表达式“专题1”的输出结果，
 * 			输出为输入串是否为该文法定义的算术表达式的判断结果；
 * 		2）递归下降分析程序应能发现输入串出错；
 * 		3）设计两个测试用例（尽可能完备，正确和出错），并给出测试结果。
 * 
 * */
public class LL1PredictionAnalysisMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//FirstAndFollow faf = new FirstAndFollow();
		//faf.displayGrammar();
		//1、构造预测分析表
		LL1PredictionAnalysis pa = new LL1PredictionAnalysis("二元式文件.tys");
		//2、进行预测分析
		pa.analysisProcessing();
	}

}






 //
  class LL1PredictionAnalysis {
 	private BufferedReader br = null; //输入的二元式文件流
 	private static List<String> InputStream = new ArrayList<String>(); //从二元式文件中拆解的符号穿输入流
 	private int indexP = 0; //扫描指针，初始为0
 	private Map<String,String> LL1Table = new HashMap<String,String>();
 	private FirstAndFollow faf = new FirstAndFollow(); //文法G的FIRST集和FOLLOW集
 	
 	private Stack<Character> analysisStack = new Stack<Character>(); //分析栈
 	private int tab = 1; //制符表的数量，初始为1
 	//构造函数
 	public LL1PredictionAnalysis(String fileName) {
 		File fp = new File(fileName);
 		if(!fp.getName().endsWith(".tys")) {
 			System.out.println("文件格式不正确...");
 			return;
 		}
 		//构造文件扫描
 		try {
 			br = new BufferedReader(new InputStreamReader(new FileInputStream(fp.getName())));
 			String erYuanShi = "";
 			while((erYuanShi=br.readLine())!=null) {
 				//截取符号串
 				
 					InputStream.add(erYuanShi.substring(erYuanShi.indexOf(",") + 1, erYuanShi.lastIndexOf(")")));
 				
 				
 			}
 			InputStream.add("#");  //末尾添加#号
 			//输出一下序列
 			System.out.print("输入的源程序为：");
 			int l=InputStream.size();
 			for(int i = 0;i<l-1;++i) {
 				print_indexp(i);
 			}
 			System.out.println();
 		} catch (FileNotFoundException e) {
 			// TODO Auto-generated catch block
 			System.out.println(fileName+"文件不存在...");
 			e.printStackTrace();
 		} catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 		
 		//构造分析表
 		buildLLR1Table();
 		//显示分析表
 		displayLLR1Table();
 		
 		//分析栈中移入终结符号和文法的开始符号
 		analysisStack.push(FirstAndFollow.end);
 		analysisStack.push(FirstAndFollow.start);
 	}
 	
 	//构建LLR(1)文法的预测表
 	private void buildLLR1Table() {
 		//LLR1Table的形式：[E,i]：E->TR
 		
 		//遍历每一条产生式
 		Set<String> X = faf.getProductionFirstSet().keySet();
 		Iterator<String> it = X.iterator();
 		while(it.hasNext()) {
 			String pro = (String) it.next();
 			String temp = pro.substring(3); //截取产生式->右边的字符判断是不是$,从而选择FIRST集还是FOLLOW集
 			Set<Character> symbol;
 			if(temp.equals("$")) {
 				symbol = faf.getFollowSet().get(pro.charAt(0));
 			}
 			else{
 				symbol = faf.getProductionFirstSet().get(pro);
 			}
 			Iterator<Character> itSymbol = symbol.iterator();
 			while(itSymbol.hasNext()) {
 				Character sy = (Character) itSymbol.next();
 				LL1Table.put(pro.charAt(0)+","+sy, pro);
 			}
 		}
 	}
 	
 	//显示分析表
 	private void displayLLR1Table() {
 		System.out.println("\n\n\t\t\t\tLL(1)分析表\t\t\t");
 		for(int i = 0;i<faf.getVt().size();++i) {
 			System.out.print("\t"+faf.getVt().get(i));
 		}
 		System.out.println();
 		for(int i = 0;i<faf.getVn().size();++i) {
 			System.out.print(faf.getVn().get(i));
 			for(int j = 0;j<faf.getVt().size();++j) {
 				String str = faf.getVn().get(i)+","+faf.getVt().get(j);
 				String pro = LL1Table.get(str);
 				if(pro!=null) {
 					System.out.print("\t"+pro);
 				}
 				else {
 					System.out.print("\t");
 				}
 			}
 			System.out.println();
 		}
 	}
 	
 	//预测分析执行
 	public void analysisProcessing() {
 		System.out.println("\n\n预测分析过程表");
 		//System.out.println("分析栈\t\t\t\t余留输入串\t\t\t\t所用产生式");
 		while(!analysisStack.isEmpty() && indexP<InputStream.size()) {
 			String Input = get_indexP(indexP);
 		//	System.out.println(Input);
 			//if(Input.length()>1||Input.length()==1&&Character.isLetter(Input.charAt(0))) { 
 				//符号长度大于1时，表示是一个变量，将其转换成i
 				//或者是长度等于1的字母变量，也转换成i
 			//	Input = "i";
 			//}
 			//分析栈栈栈顶元素弹栈
 			displayProcessing("");
 			Character analysisSymbol = (Character) analysisStack.pop();
 			
 			
 			
 			if(faf.getVt().indexOf(analysisSymbol)!=-1&&!analysisSymbol.equals(FirstAndFollow.end)) { //非终结符号，识别出来，相销
 				if(analysisSymbol==Input.charAt(0))
 				{
 					++indexP;
 					continue;
 				}
 				else if(analysisSymbol=='$'&&Input.charAt(0)=='#')
 				{
 					++indexP;
 					continue;
 					
 				}else
 				{
 					System.out.println("分析失败！"+"算术表达式应该包含字符："+analysisSymbol);
 					return ;
 				}
 				
 			}
 			
 			//System.out.println("出栈元素："+analysisSymbol+"\n"+"输入元素："+Input);
 			
 			if(analysisSymbol==(FirstAndFollow.end)&&Input.equals("#")) {
 				++indexP;
 				continue;
 			}
 			if(analysisSymbol=='$'&&Input.equals("#")) {
 				++indexP;
 				
 				continue;
 			}
 			//System.out.println(analysisSymbol+Input);
 			//根据栈顶元素与输入元素的组合查找分析表，找到对应的产生式
 			
 			String temp = LL1Table.get(analysisSymbol+","+Input);
 			//System.out.println(analysisSymbol+","+Input);
 			if(temp==null) {
 				System.out.println("LL1分析表中 ["+analysisSymbol+","+Input+"] 无产生式");
 				if(analysisSymbol=='K')
 				{
 					System.out.println("缺少程序体");
 					System.out.println("分析失败");
 	 				return;
 				}
 				
 			}
 			else
 				
 			{
 				String production = temp.substring(3);
 	 			if(production.equals("$")) {
 	 				displayProcessing(temp);
 	 				continue;
 	 			}
 	 			char [] nArry = new StringBuffer(production).reverse().toString().toCharArray();
 	 			for(int i = 0;i<nArry.length;++i) {
 	 				analysisStack.push(nArry[i]);
 	 			}
 	 			displayProcessing(temp);
 			}
 			//产生式右部逆序入栈
 			
 		}
 		
 		if((analysisStack.isEmpty()||analysisStack.pop()=='#'))
 		{
 			System.out.println("分析成功");
 			System.out.println("源程序为：");
 			int l=InputStream.size();
 			for(int i = 0;i<l-1;++i) {
 				print_indexp(i); 
 			}
 			System.out.println();
 		}
 			
 		else
 			System.out.println("分析失败");
 	}
 	
 	//输出分析的中间过程
 	private void displayProcessing(String pro) {
 		System.out.print("分析栈："+analysisStack);
 		System.out.print("\t\t余留串："+InputStream.subList(+indexP, InputStream.size()));
 		System.out.println("\t\t产生式："+pro);
 	}
 	
 	public void print_indexp(int i) {
		String s_curr=InputStream.get(i);
		if(i>=1)
		{
			if((InputStream.get(i-1).equals(";")&&!InputStream.get(i).equals("}"))
					||(InputStream.get(i-1).equals("else")&&!InputStream.get(i).equals("if")))
			{
				System.out.print("\n");
				for(int j=0;j<tab;j++)
				{
					System.out.print("    ");
				}
				//System.out.print("\n");
			}
			else
				if(InputStream.get(i-1).equals("}")) {
					System.out.print("\n");
					if(InputStream.get(i).equals("}"))
					{
						for(int j=0;j<tab-1;j++)
						{
							System.out.print("    ");
						}
					}
					else 
					{
						
						for(int j=0;j<tab;j++)
						{
							System.out.print("    ");
						}
					}
				}
				else if(InputStream.get(i-1).equals("{"))
				{
					if(!InputStream.get(i).equals("{"))
					{
						System.out.print("\n");
						for(int j=0;j<tab;j++)
						{
							System.out.print("    ");
						}
					}
					else
					{
						System.out.print("\n");
					}
				}
			
		}
		
		if(s_curr.equals(";"))
			System.out.print(s_curr+"\n");
		else if(s_curr.equals("{"))
		{
			if(InputStream.get(i-1).equals(")"))
			{
				System.out.print("\n");
			}
			if(!InputStream.get(i-1).equals("else"))
			{
				for(int j=0;j<tab;j++)
				{
					System.out.print("    ");
				}
			}
		
			System.out.print(s_curr);
			tab++;
		}
		else if(s_curr.equals("}"))
		{
			tab--;
			
			if(!InputStream.get(i-1).equals("}"))
			{
				
				for(int j=0;j<tab;j++)
				{
					System.out.print("    ");
				}
			}
			System.out.print(s_curr);
			
		}
		else
		{
			System.out.print(s_curr+" ");
		}
	}
	public String get_indexP(int index) {
		String s = InputStream.get(index);
		
		if(s.equals("{"))
		{
			s="{";
		}	
		else if(s.equals("}"))
		{
			s="}";
		}	
		else if(s.equals("else"))
		{
			String s1 = InputStream.get(index+1);
			if (s1.equals("if"))
			{
				s="f";
				indexP++;
			}
			else
			{
				s="9";
			}
		}	
		else if(s.equals("if"))
		{
			if(index>=1)
			{
				String s1 = InputStream.get(index-1);
				if (s1.equals("else"))
				{
					s="f";
					
				}
				else
				{
					s="8";
				}
			}
			else
			s="8";
			
		}
		else if(s.equals("&&"))
			s="a";	
		else if(s.equals("||"))
			s="o";	
		else if(s.equals("<"))
			s="l";	
		else if(s.equals("=="))
			s="e";	
		else if(s.equals("!="))
			s="u";	
		else if(s.equals(">="))
			s="t";	
		else if(s.equals("<="))
			s="d";	
		else if(s.equals(">"))
			s="g";	
		else if(s.equals(";"))
			s=";";	
		else	if((s.length()==1)&&Character.isLetterOrDigit(s.charAt(0))){
			s="i";
		}else if(s.length()>1) {
			s="i";
			for(int i = 0;i < s.length();++i) {
				if(!Character.isLetterOrDigit(s.charAt(i)))
				{
					System.out.println("未知符号："+s);
					s="unknow";
				}
			
			}
		}
		return  s;
	}

 }
  
  /**文法
   * G[P]: 
          P→S|Q|; 
  		S→V=E;
  		E→TR
  		R→ATR|$
  		T→FY
  		Y→MFY|$
  		F→CZ
  		Z→OCZ|$
  		C→BI
  		I→XBI|$
  		B→(E)|i
  		A→+|-
  		M→*|/
  		X→a|o           //a表示逻辑符号&&，o表示逻辑符号||
  		O→t|d|g|l|u|e  //t表示>=，d表示<=，g表示>,l表示<,e表示==，u表示!=
  		V→i
  		Q→8JKH          //8表示if在符号表中序号
  		H→fJKH|9K|$     //f 表示 else if符号的组合、9表示else在符号表中的序号
  		J→(E)           //逻辑语句
  		K→S|{U}|;       //if语句程序体
  		U→PU|{U}U|$

   * */

  //构造文法的First集和Follow集
   class FirstAndFollow {
  	//终结符号集
  	private List<Character> Vt = new ArrayList<Character>();
  	//非终结符号集
  	private List<Character> Vn = new ArrayList<Character>();
  	
  	public static Character epsn = '$';//设置空
  	public static Character start = 'P'; //开始符号
  	public static Character end = '#';  //终止符号
  	//用map来代替文法
  	private Map<Character, String> grammar = new HashMap<Character, String>();
  	
  	//First集
  	private Map<Character,Set<Character>> First = new HashMap<Character,Set<Character>>();
  	//Follow集
  	private Map<Character,Set<Character>> Follow = new HashMap<Character,Set<Character>>();
  	//产生式的First集
  	private Map<String,Set<Character>> productionFirst = new HashMap<String,Set<Character>>();  	
  	public Map<Character,Set<Character>> getFollowSet(){
  		return Follow;
  	}
  	public Map<String,Set<Character>> getProductionFirstSet(){
  		return productionFirst;
  	}
  	public List<Character> getVt(){
  		return this.Vt;
  	}
  	public List<Character> getVn(){
  		return this.Vn;
  	}
  	public FirstAndFollow() {
  		//添加非终结符号
  		Vn.add('P');
  		
  		Vn.add('Q');
  		Vn.add('J');
  		Vn.add('K');
  		Vn.add('H');
  	
  		Vn.add('S');
  		Vn.add('V');
  		Vn.add('E');
  		Vn.add('R');
  		Vn.add('T');
  		Vn.add('Y');
  		Vn.add('F');
  		Vn.add('A');
  		Vn.add('M');
  		Vn.add('Z');
  		Vn.add('C');
  		Vn.add('I');
  		Vn.add('B');
  		Vn.add('X');
  		Vn.add('O');
  		Vn.add('U');

  		//添加终结符号
  		Vt.add('8');
  		Vt.add('9');
  		Vt.add('f');
  		Vt.add('=');
  		Vt.add('i');
  		Vt.add('+');
  		Vt.add('-');
  		Vt.add('*');
  		Vt.add('/');
  		Vt.add('(');
  		Vt.add(')');
  		Vt.add('{');
  		Vt.add('}');
  		Vt.add(';');
  		Vt.add('a');
  		Vt.add('o');
  		Vt.add('t');
  		Vt.add('d');
  		Vt.add('g');
  		Vt.add('l');
  		Vt.add('e');
  		Vt.add('u');
  		Vt.add(end);
  		Vt.add(epsn);
  	
  		//输入文法
  		grammar.put('P', "S|Q|;");
  		grammar.put('S', "V=E;");
  		grammar.put('E', "TR");
  		grammar.put('R', "ATR|"+epsn);
  		grammar.put('T', "FY");
  		grammar.put('Y', "MFY|"+epsn);
  		grammar.put('F', "CZ");
  		grammar.put('Z', "OCZ|"+epsn);
  		grammar.put('C', "BI");
  		grammar.put('I', "XBI|"+epsn);
  		grammar.put('B', "(E)|i");
  		grammar.put('A', "+|-");
  		grammar.put('M', "*|/");
  		grammar.put('V', "i");
  		grammar.put('Q', "8JKH");
  		grammar.put('H', "fJKH|9K|"+epsn);
  		grammar.put('J', "(E)");
  		grammar.put('K', "S|{U}|;");
  		grammar.put('U', "PU|{U}U|"+epsn);
  		grammar.put('X', "a|o");
  		grammar.put('O', "t|d|g|l|u|e");

  		//非终结符号的First集和Follow集初始化
  		for(int i = 0;i<Vn.size();++i) {
  			First.put(Vn.get(i), new HashSet<Character>());
  			Follow.put(Vn.get(i), new HashSet<Character>());
  		}
  		
  		//根据文法初始化productionFirst集
  		for(int i = 0;i<Vn.size();++i) {
  			String str = grammar.get(Vn.get(i));
  			String []nArryStr = str.split("\\|");
  			for(int j = 0;j<nArryStr.length;++j) {
  				productionFirst.put(Vn.get(i)+"->"+nArryStr[j], new HashSet<Character>());
  			}
  		}
  		
  		//构造first集
  		this.buildFirst();
  		//显示first集
  		//this.displayFirst();
  		displayFirst();
  		
  		//构造Follow集
  		this.buildFollow();
  		//显示Follow集
  		this.displayFollow();
  	}	
  	/**构造FIRST集
  	 * 反复利用如下规则，直至FIRST集不再增大
  	 * （1）若X属于Vt,则FIRST(X)={X};
  	 * （2）若X属于Vn,且有X->aN(a属于Vt),则令a属于FIRST(X);若有X->$,则$属于FIRST(X);
  	 * （3）若X->Y1Y2...Yk,
  	 *		 ⅰ 将FIRST(Y1)中的一切非$的终结符加进FIRST(X);
  			ⅱ若$属于FIRST(Y1),则将FIRST(Y2)中的一切非$的终结符加进FIRST(X);
  			ⅲ 若$属于FIRST(Y1),并且$属于FIRST(Y2),则将FIRST(Y3)中的一切非$终结符加进FIRST(X);一次类推
  			ⅲi若$都属于FIRST(Y1...YN),则将$加进FIRST(X)
  	 * */
  	private void buildFirst() {
  		boolean bigger = true;
  		while(bigger) {
  			bigger = false;
  			int setSize = 0;
  			for(int i = 0;i<Vn.size();++i) {
  				Character left = Vn.get(i); //产生式左部符号
  				String right = grammar.get(left); //产生式右部
  				String []rightnArry = right.split("\\|");  //分割产生式右部
  				setSize = First.get(left).size();
  				for(int k = 0;k<rightnArry.length;++k) {   //对右部的产生式一个一个处理
  					int rightIndex = 0;
  					Character cha = rightnArry[k].charAt(rightIndex);
  					if (Vt.indexOf(cha) != -1) { // 是终结符号
  						// 加入left的FIRST集
  						First.get(left).add(cha);
  						productionFirst.get(left+"->"+rightnArry[k]).add(cha);
  						if(First.get(left).size()>setSize)
  							bigger = true;
  					} else if (Vn.indexOf(cha) != -1) { // 是非终结符号
  						Set<Character> Y;
  						do {
  							if(rightIndex>=rightnArry[k].length()) {
  								//说明到最后Y的first集中都有$,此时应该将$加入first集
  								productionFirst.get(left+"->"+rightnArry[k]).add('$');
  								First.get(left).add('$');
  								break;
  							}
  							cha = rightnArry[k].charAt(rightIndex);
  							Y = First.get(cha);
  							//把Y的First集（除$）放入X的First集
  							Iterator<Character> it = Y.iterator();
  							while (it.hasNext()) {
  								Character tempc = (Character) it.next();
  								if (!tempc.equals('$')) {
  									productionFirst.get(left+"->"+rightnArry[k]).add(tempc);
  									First.get(left).add(tempc);
  								}
  							}
  							++rightIndex;
  						} while (Y.contains('$'));
  						
  						if(First.get(left).size()>setSize)
  							bigger = true;
  					}
  				}
  			}
  			
  		}
  	}
  	//显示FIRST集
  	public void displayFirst() {
  		System.out.println("文法的FIRST集如下：");
  		for(int i = 0;i < Vn.size();++i) {
  			System.out.print(Vn.get(i)+":");
  			Iterator<Character> it = First.get(Vn.get(i)).iterator();
  			while(it.hasNext()) {
  				System.out.print(it.next()+" ");
  			}
  			System.out.println();
  		}
  	}
  	/**构造FOLLOW集
  	 * ①令# ∈FOLLOW(S)        S为文法开始符号
  	 * ②对A→ αBβ,  且β ≠ ε
  	  	则将 FIRST(β) -{ε}加入FOLLOW(B)中
  	   ③反复, 直至每一个FOLLOW(A)不再增大 
  	           对A→ αB或A→ αBβ(且ε ∈ FIRST(β)) 则FOLLOW(A)中的全部元素加入FOLLOW(B)
  	 * */
  	private void buildFollow() {
  		//#属于FOLLOW(E)
  		Follow.get(start).add(end);
  		boolean bigger = true;
  		while(bigger) {
  			bigger=false;
  			int setSize = 0;
  			for(int i = 0;i < Vn.size();++i) {
  				Character left = Vn.get(i); //产生式左部符号
  				String right = grammar.get(left); //产生式右部
  				int rightIndex = 0;
  				
  				//对产生式的右部进行操作
  				while(rightIndex<right.length()) {
  					Character firstChar = right.charAt(rightIndex);
  					if(Vt.indexOf(firstChar)!=-1 || firstChar.equals('|')) { //终结符号
  						++rightIndex;
  						continue;
  					}
  					if(right.length()>rightIndex+1) { //还可以继续识别符号
  						Character secondChar = right.charAt(rightIndex+1);
  						if(secondChar.equals('|')) {  //达到产生的尾部了
  							//将left的Follow集加入到firstChar的Follow集
  							setSize=Follow.get(firstChar).size();
  							Follow.get(firstChar).addAll(Follow.get(left));
  							if(Follow.get(firstChar).size()>setSize)
  								bigger = true;
  							rightIndex+=2;
  							continue;
  						}
  						if(Vt.indexOf(secondChar)!=-1) { //终结符号，移入firstChar的Follow集
  							//System.out.println(firstChar);
  							setSize=Follow.get(firstChar).size();
  							Follow.get(firstChar).add(secondChar);
  							if(Follow.get(firstChar).size()>setSize)
  								bigger = true;
  						}else if(Vn.indexOf(secondChar)!=-1) { //非终结符号
  							//将second的FIRST集元素除$移入firstChar的Follow集
  							setSize=Follow.get(firstChar).size();
  							Iterator<Character> it = First.get(secondChar).iterator();
  							while (it.hasNext()) {
  								Character tempc = (Character) it.next();
  								if (!tempc.equals('$')) {
  									Follow.get(firstChar).add(tempc);
  								}
  							}
  							if(Follow.get(firstChar).size()>setSize)
  								bigger = true;
  							
  							//如果$属于secondChar的First集，将left的Follow集全部加入firstChar的Follow集
  							if(First.get(secondChar).contains(epsn)) {
  								setSize=Follow.get(firstChar).size();
  								Follow.get(firstChar).addAll(Follow.get(left));
  								if(Follow.get(firstChar).size()>setSize)
  									bigger = true;
  							}
  						}
  					}else {	//没有符号了
  						//将left的Follow集全部加入firstChar的Follow集
  						setSize=Follow.get(firstChar).size();
  						Follow.get(firstChar).addAll(Follow.get(left));
  						if(Follow.get(firstChar).size()>setSize)
  							bigger = true;
  					}
  					++rightIndex;
  				}
  			}
  		}
  	}
  	
  	// 显示Follow集
  	public void displayFollow() {
  		System.out.println("文法的FOLLOW集如下：");
  		for (int i = 0; i < Vn.size(); ++i) {
  			System.out.print(Vn.get(i) + ":");
  			Iterator<Character> it = Follow.get(Vn.get(i)).iterator();
  			while (it.hasNext()) {
  				System.out.print(it.next() + " ");
  			}
  			System.out.println();
  		}
  	}
  	//输出文法

  }
