package kr.dcos.common.utils.evaluator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;


/**
 * Table에서 row를 조회하기 위해서 작성됨
 * Row의 컬럼명과 값을 변수로 받고 문장을 변수로 받아서 수식을 해석한다
 * Table뿐만이 아니고 다른 용도로도 사용가능하다
 * 
 * 1. 수식은 1개의 라인으로 구성한다(프로그램 소스처럼 2개이상의 라인은 지원하지 않는다.)
 * 2. field명은 대소문자를 구분하지 않는다 ocYn=ocyn=OCYN
 * 3. 문자열은 ' ' 로 표현하고 ' 안에 ' 가 포함되지 않는것으로 한다
 *  
 * 지원하는 연산자 : == ,  != , ne, eq, and , &&, or, ||,  +, -, /, *, %
 * 
 * @author 김도영
 *
 */
public class Evaluator {
	private class Token {
		public boolean isField = false;
		public TOKEN_TYPE type;
		public String name;
		public String value;
		private int icp = 0; // Incoming procedence 스택에 들어올 때의 우선순위
	    private  int isp = 0; // In-Stack procedence 스택안에서의 우선순위
		public Token(TOKEN_TYPE type, String name, String value){
			this(type,name, value, 0,0);
		}
		public Token(TOKEN_TYPE type, String name, String value, int icp, int isp){
			this.type = type; this.name = name; this.value = value; this.icp = icp; this.isp=isp; this.isField = false;
		}
		@Override
		public String toString() {
			return "Token name=[" + name + "], type=[" + type + "], value=[" + value + "], icp=[" + icp + "], isp=["+isp+"]";
		}
	}
	private enum TOKEN_TYPE { NUMBER_VALUE, STRING_VALUE, BOOLEAN_VALUE,
		OP_PLUS, OP_MINUS, OP_DIVIDE, OP_MULTIPLE, OP_MOD, OP_BIGGER, OP_LESSTHAN, OP_BIGGER_EQUAL, OP_LESS_EQUAL,
		OP_EQUAL, OP_NOT_EQUAL,  OP_AND, OP_OR, OP_NOT, SP_LPAREN, SP_RPAREN 
	}
	private boolean debug = false; //debug용


	/**
	 * 변수를 담고 있는 map
	 */
	private Map<String, Object> variableMap;;
	/**
	 * 해석할 문장
	 */
	private String statement;
	
	/**
	 * statement를 파싱한 TOKEN을 담고 있는 스택
	 */
	private Stack<Token> tokenStack = null;

	/**
	 * 파싱한 수식의 postfix형태로 담은 stack
	 */
	private Stack<Token> postfixStack = null;
	
	/**
	 * 토큰 테이블 : 사용가능한 토큰을 가지고 있다
	 */
	private Map<String, Token> tokenTable;
	
	/**
	 * statement에 대한 hashcode로 한번 파싱한 것을 다시 파싱하지 않기 위한 용도로 사용됨
	 */
	private int parsedCode = 0;

	/**
	 * 생성자
	 * @param map
	 * @throws EvaluatorException 
	 */
	public Evaluator() throws EvaluatorException {
		this(null, null);
	}
	/**
	 * 생성자
	 * @param map
	 * @throws EvaluatorException 
	 */
	public Evaluator(Map<String, Object> map) throws EvaluatorException {
		this(map, null);
	}
	/**
	 * 생성자 
	 * @param variableMap 
	 * @param statement
	 * @throws EvaluatorException 
	 */
	public Evaluator(Map<String, Object> variableMap, String statement) throws EvaluatorException {
		if (variableMap == null) {
			variableMap = new HashMap<String, Object>();
		}
		this.variableMap = variableMap;
		initializeTokenTable();
		setStatement(statement);
		parseStatement();
	}
	private void addToken(String word) throws EvaluatorException {
		if(word == null) return;
		String s = word.trim();
		if(s.length() < 1) return;
		if(tokenTable.containsKey(s)){
			tokenStack.add( tokenTable.get(s));
		}else if(variableMap.containsKey(word.toLowerCase())){
			Object object = null;
			if( variableMap.get(word.toLowerCase()) != null){
				object = variableMap.get(word.toLowerCase());
				Token fieldToken;  
				if( object instanceof String){
					fieldToken = new Token(TOKEN_TYPE.STRING_VALUE, word, object.toString());
				}else if(object instanceof Double){
					fieldToken = new Token(TOKEN_TYPE.NUMBER_VALUE, word, object.toString());
				}else {
					fieldToken = new Token(TOKEN_TYPE.STRING_VALUE, word, object.toString());
				}
				fieldToken.isField = true;
				tokenStack.add( fieldToken );
			}else{
				fail(word + " 는 존재하지 않은 필드명입니다");
			}
		}else if(isNumber(word)) {
			tokenStack.add( new Token(TOKEN_TYPE.NUMBER_VALUE, word, word) );
		}else if(isString(word)){ //양쪽 ' '이면 문자열로 판단하고 양 ' 를 제거한 후 값에 넣는다
			tokenStack.add( new Token(TOKEN_TYPE.STRING_VALUE, word, word.substring(1,word.length()-1)) );
		}else {
			fail(word + " 는 알려지지 않은 변수 또는 값입니다");
		}
	}
	

	/**
	 * 나누기 연산을 수행한다. <br>
	 * 수행결과는 소수점 2 자리로 만들어서 리턴한다
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 * @throws EvaluatorException
	 */
	private Token divide(Token t1, Token t2) throws EvaluatorException {
		if(t1.type == TOKEN_TYPE.NUMBER_VALUE && t2.type == TOKEN_TYPE.NUMBER_VALUE  ) { // n + n 
			return new Token(TOKEN_TYPE.NUMBER_VALUE, t1.name+"+"+t2.name, String.format("%.2f",Double.valueOf(t2.value) / Double.valueOf(t1.value)));	
		}else{
			fail(" 숫자와 문자 또는 문자와 문자 사이에 마이너스 연산을 할 수 없습니다");
		}
		return null;
	}

	private Token eval() throws EvaluatorException {
		
		//statement 파싱, postfix가 만들어진다
		parseStatement();

		if(debug){
			System.out.println("tokenStack-----------------------------------------");
			printStack(tokenStack);
			System.out.println("postfixStack-----------------------------------------");
			printStack(postfixStack);
		}
		Stack<Token> stack = new Stack<Token>();
		//postfix에서 field값을 구해서 다시 채운다
		for (Token token : postfixStack) {
			if(token.isField) {
				Object o = variableMap.get(token.name.toLowerCase());
				if(o == null){
					token.value = "";
				}else{
					token.value = o.toString();
				}
			}
			stack.push(token);
		}
		
        Stack<Token> tmpStack = new Stack<Token>();
        while(stack.isEmpty()==false)
        {
        	Token token = stack.pop();
            switch (token.type)
            {
                case OP_PLUS:
                    if (tmpStack.size() < 2) {fail("더하기 연산 실패, 수식을 확인해 주십시오");} 
                    tmpStack.push(plus(tmpStack.pop(), tmpStack.pop()));
                    break;
                case OP_MINUS:
                    if (tmpStack.size() < 2) {fail("빼기 연산 실패, 수식을 확인해 주십시오");}
                    tmpStack.push(minus(tmpStack.pop(), tmpStack.pop()));
                    break;
                case OP_MULTIPLE:
                	if (tmpStack.size() < 2) {fail("곱하기 연산 실패, 수식을 확인해 주십시오");}
                    tmpStack.push(multiple(tmpStack.pop(), tmpStack.pop()));
                    break; 
                case OP_DIVIDE:
                	if (tmpStack.size() < 2) {fail("곱하기 연산 실패, 수식을 확인해 주십시오");}
                    tmpStack.push(divide(tmpStack.pop(), tmpStack.pop()));
                    break; 
                case OP_MOD:
					if (tmpStack.size() < 2) {fail("곱하기 연산 실패, 수식을 확인해 주십시오");}
					tmpStack.push(mod(tmpStack.pop(), tmpStack.pop()));
					break;
                case OP_BIGGER : // >
                    if (tmpStack.size() < 2)  {fail("비교 연산 실패, 수식을 확인해 주십시오");}
                    tmpStack.push(logicalBigger(tmpStack.pop(), tmpStack.pop()));                	
                	break;
                case OP_LESSTHAN : //<
                	if (tmpStack.size() < 2)  {fail("비교 연산 실패, 수식을 확인해 주십시오");}
                    tmpStack.push(logicalLessThan(tmpStack.pop(), tmpStack.pop()));                	
                	break;
                case OP_BIGGER_EQUAL : //>=
                	if (tmpStack.size() < 2)  {fail("비교 연산 실패, 수식을 확인해 주십시오");}
                    tmpStack.push(logicalBiggerEqual(tmpStack.pop(), tmpStack.pop()));                	
                	break;
                case OP_LESS_EQUAL : //<=
                	if (tmpStack.size() < 2)  {fail("비교 연산 실패, 수식을 확인해 주십시오");}
                    tmpStack.push(logicalLessEqual(tmpStack.pop(), tmpStack.pop()));                	
                	break;
                case OP_NOT_EQUAL : //<=
                	if (tmpStack.size() < 2)  {fail("비교 연산 실패, 수식을 확인해 주십시오");}
                    tmpStack.push(logicalNotEqual(tmpStack.pop(), tmpStack.pop()));                	
                	break;
                case OP_EQUAL : //<=
                	if (tmpStack.size() < 2)  {fail("비교 연산 실패, 수식을 확인해 주십시오");}
                    tmpStack.push(logicalEqual(tmpStack.pop(), tmpStack.pop()));                	
                	break;
                case OP_AND :
                	if (tmpStack.size() < 2)  {fail("AND 논리 연산 실패, 수식을 확인해 주십시오");}
                	tmpStack.push(logicalAnd(tmpStack.pop(), tmpStack.pop()));
                	break;
                case OP_OR :
                	if (tmpStack.size() < 2)  {fail("OR 논리 연산 실패, 수식을 확인해 주십시오");}
                	tmpStack.push(logicalOr(tmpStack.pop(), tmpStack.pop()));
                	break;
                case OP_NOT :
                	if (tmpStack.size() < 1)  {fail("NOT 논리 연산 실패, 수식을 확인해 주십시오");}
                	tmpStack.push(logicalNot(tmpStack.pop()));
                	break;
                case STRING_VALUE:
                case NUMBER_VALUE:
                case BOOLEAN_VALUE:
               	 	tmpStack.push(token);
                    break;
				default:
					break;  
            }
        }
        if (tmpStack.size() < 1){
        	fail("수식해석에 실패했습니다. 입력 수식을 다시 확인해주십시오");
        }
        return tmpStack.pop();		 
		
	}

	/**
	 * exception을 던진다
	 * @param msg
	 * @throws EvaluatorException
	 */
	private void fail(String msg) throws EvaluatorException{
		throw new EvaluatorException(msg);
	}

	private Stack<Token> getPostfixStack(Stack<Token> infix) {
		
		if (infix == null) return null;

		Stack<Token> postfix = new Stack<Token>();
		Stack<Token> tmp = new Stack<Token>();
		
		Iterator<Token> it = infix.iterator();
	
         while(it.hasNext()){
        	 Token token =  it.next();
             switch (token.type)
             {
                 case SP_LPAREN:
                     tmp.push(token);
                     break;
                 case NUMBER_VALUE:  case STRING_VALUE: case BOOLEAN_VALUE :
                     postfix.push(token);
                     break;
                 case OP_DIVIDE: case OP_MULTIPLE:  case OP_MINUS: case OP_PLUS: case OP_MOD:
                 case OP_BIGGER: case OP_BIGGER_EQUAL: case OP_LESSTHAN: case OP_LESS_EQUAL:
                 case OP_EQUAL:  case OP_NOT_EQUAL:
                 case OP_AND: case OP_OR: case OP_NOT : 	 
                     if (tmp.isEmpty())
                     {
                         tmp.push(token);
                     }
                     else
                     {
                         if (token.icp > tmp.peek().isp)
                         {
                             tmp.push(token);
                         }
                         else
                         {
                             while (!tmp.isEmpty())
                             {
                                 Token t1 = tmp.pop();
                                 if (token.icp  <= t1.isp)
                                 {
                                	 postfix.push(t1);
                                     tmp.push(token);
                                     break;
                                 }
                                 else
                                 {
                                     tmp.push(t1);
                                     tmp.push(token);
                                     break;
                                 }
                             }
                         }
                     }
                     break;
                 case SP_RPAREN:
                     while (!tmp.isEmpty())
                     {
                         Token t = tmp.pop();
                         if (t.type == TOKEN_TYPE.SP_LPAREN)
                         {
                             break;//무시
                         }
                         else
                         {
                        	 postfix.push(t);
                         }
                     }
                     break;
                 default:
                	 postfix.push(token);
                     break;
             }
         }
         while (!tmp.isEmpty())
         {
        	 postfix.push(tmp.pop());
         }

        Stack<Token> stack = new Stack<Token>();
 		for (int i = postfix.size() - 1; i >= 0; i--) {
			Token token = postfix.get(i);
			stack.push(token);
		}
        return stack;
	}


	private void initializeTokenTable() {
		
		if(tokenTable == null) tokenTable = new HashMap<String, Token>();
		tokenTable.put("+", new Token(TOKEN_TYPE.OP_PLUS, "+", "+", 12, 12));
		tokenTable.put("-", new Token(TOKEN_TYPE.OP_MINUS, "-", "-", 12, 12));
		tokenTable.put("/", new Token(TOKEN_TYPE.OP_DIVIDE, "/", "/", 13, 13));
		tokenTable.put("*", new Token(TOKEN_TYPE.OP_MULTIPLE, "*", "*", 13, 13));
		tokenTable.put("%", new Token(TOKEN_TYPE.OP_MOD, "%", "%", 13, 13));
		
		tokenTable.put(">",  new Token(TOKEN_TYPE.OP_BIGGER, "biggerthan", ">", 11, 11));
		tokenTable.put("<",  new Token(TOKEN_TYPE.OP_LESSTHAN, "lessthan", "<", 11, 11));
		tokenTable.put(">=", new Token(TOKEN_TYPE.OP_BIGGER_EQUAL, "biggerequal", ">=", 11, 11));
		tokenTable.put("<=", new Token(TOKEN_TYPE.OP_LESS_EQUAL, "lessequal", "<=", 11, 11));
		tokenTable.put("!=", new Token(TOKEN_TYPE.OP_NOT_EQUAL, "notequal", "!=", 11, 11));
		tokenTable.put("ne", new Token(TOKEN_TYPE.OP_NOT_EQUAL, "notequal", "ne", 11, 11));
		tokenTable.put("==", new Token(TOKEN_TYPE.OP_EQUAL, "equal", "==", 11, 11));
		tokenTable.put("eq", new Token(TOKEN_TYPE.OP_EQUAL, "equal", "eq", 11, 11));

		tokenTable.put("&&", new Token(TOKEN_TYPE.OP_AND, "and", "&&", 9, 9));
		tokenTable.put("and", new Token(TOKEN_TYPE.OP_AND, "and", "and", 9, 9));
		tokenTable.put("||", new Token(TOKEN_TYPE.OP_OR, "or", "||", 9, 9));
		tokenTable.put("or", new Token(TOKEN_TYPE.OP_OR, "or", "or", 9, 9));
		tokenTable.put("!", new Token(TOKEN_TYPE.OP_NOT, "not", "!", 14, 14));
		tokenTable.put("not", new Token(TOKEN_TYPE.OP_NOT, "not", "not", 14, 14));

		tokenTable.put("(", new Token(TOKEN_TYPE.SP_LPAREN, "leftParen", "(", 20, 0));
		tokenTable.put(")", new Token(TOKEN_TYPE.SP_RPAREN, "rightParen", ")", 19, 0));

		tokenTable.put("true", new Token(TOKEN_TYPE.BOOLEAN_VALUE, "true", "true", 0, 0));
		tokenTable.put("false", new Token(TOKEN_TYPE.BOOLEAN_VALUE, "false", "false", 0, 0));
		
	}


	private boolean isNumber(String word) {
		for (int i = 0; i < word.length(); i++) {
			if( (word.charAt(i) <= '9' && word.charAt(i) >='0' ) || (word.charAt(i) == '.') ) {
				;
			}else{
				return false;
			}
		}
		return true;
	}


	private boolean isString(String word) {
		if(word == null) return false;
		if(word.startsWith("'") && word.endsWith("'")) return true;
		return false;
	}


	private boolean isValid() {
		if(variableMap == null || statement == null || statement.length() < 1){
			return false;
		}
		return true;
	}


	/**
	 * 논리연사자 and 
	 */
	private Token logicalAnd(Token t1, Token t2) throws EvaluatorException {
		if(t1.type == TOKEN_TYPE.BOOLEAN_VALUE && t2.type == TOKEN_TYPE.BOOLEAN_VALUE){
			if(t1.value.equals("true") &&  t2.value.equals("true")) {
				return new Token(TOKEN_TYPE.BOOLEAN_VALUE, t1.name+"and"+t2.name, "true");
			}
			return new Token(TOKEN_TYPE.BOOLEAN_VALUE, t1.name+"and"+t2.name, "false");
		}
		fail("논리연산자 and 실패");
		return null;
	}


	//연산자 : >
	private Token logicalBigger(Token t1, Token t2) throws EvaluatorException {
		if(t1.type == TOKEN_TYPE.NUMBER_VALUE && t2.type == TOKEN_TYPE.NUMBER_VALUE  ) { // n + n 
			return new Token(TOKEN_TYPE.BOOLEAN_VALUE, t1.name+">"+t2.name, (Double.valueOf(t2.value) >  Double.valueOf(t1.value) ? "true" : "false"));	
		}else if(t1.type==TOKEN_TYPE.STRING_VALUE && t2.type == TOKEN_TYPE.STRING_VALUE ){ // s + s
			return new Token(TOKEN_TYPE.BOOLEAN_VALUE, t1.name+">"+t2.name, (t1.value.compareTo(t2.value) < 0 ? "true" : "false"));	
		}else{
			fail(" 숫자와 숫자, 문자와 문자만  비교할 수 있습니다. (" + t1.name + " > " + t2.name + ")");
		}
		return null;	
	}


	private Token logicalBiggerEqual(Token t1, Token t2) throws EvaluatorException {

		if(t1.type == TOKEN_TYPE.NUMBER_VALUE && t2.type == TOKEN_TYPE.NUMBER_VALUE  ) { // n + n 
			return new Token(TOKEN_TYPE.BOOLEAN_VALUE, t1.name+">"+t2.name, (Double.valueOf(t2.value) >=  Double.valueOf(t1.value) ? "true" : "false"));	
		}else if(t1.type==TOKEN_TYPE.STRING_VALUE && t2.type == TOKEN_TYPE.STRING_VALUE ){ // s + s
			return new Token(TOKEN_TYPE.BOOLEAN_VALUE, t1.name+">"+t2.name, (t1.value.compareTo(t2.value) <= 0 ? "true" : "false"));	
		}else{
			fail(" 숫자와 숫자, 문자와 문자만  비교할 수 있습니다. (" + t1.name + " >= " + t2.name + ")");
		}
		return null;
	}


	//연산자 : ==(eq)
	private Token logicalEqual(Token t1, Token t2) throws EvaluatorException {
		if(t1.type == TOKEN_TYPE.NUMBER_VALUE && t2.type == TOKEN_TYPE.NUMBER_VALUE  ) { // n == n 
			return new Token(TOKEN_TYPE.BOOLEAN_VALUE, t1.name+"=="+t2.name, (Double.parseDouble(t2.value) ==  Double.parseDouble(t1.value) ? "true" : "false"));
		}else if(t1.type == TOKEN_TYPE.STRING_VALUE && t2.type == TOKEN_TYPE.STRING_VALUE  ) { // s == s
			return new Token(TOKEN_TYPE.BOOLEAN_VALUE, t1.name+"=="+t2.name, t2.value.equals(t1.value) ? "true" : "false");
		}else{
				fail("equal 연산자는 숫자와 숫자, 문자와 문자만 비교연산을 지원합니다.");
		}
		return null;
	}


	// 연산자 : <= 
	private Token logicalLessEqual(Token t1, Token t2) throws EvaluatorException {
		if(t1.type == TOKEN_TYPE.NUMBER_VALUE && t2.type == TOKEN_TYPE.NUMBER_VALUE  ) { // n , n 
			return new Token(TOKEN_TYPE.BOOLEAN_VALUE, t1.name+"<="+t2.name, (Double.valueOf(t2.value) <=  Double.valueOf(t1.value) ? "true" : "false"));	
		}else if(t1.type==TOKEN_TYPE.STRING_VALUE && t2.type == TOKEN_TYPE.STRING_VALUE ){ // s , s
			return new Token(TOKEN_TYPE.BOOLEAN_VALUE, t1.name+"<="+t2.name, ((t1.value.compareTo(t2.value) <= 0) ? "true" : "false"));
		}else{
			fail(" 숫자와 숫자, 문자와 문자만  비교할 수 있습니다. (" + t1.name + " <= " + t2.name + ")");
		}
		return null;
	}


	/**
	 * lessthan : < 연산자
	 * @param t1
	 * @param t2
	 * @return
	 * @throws EvaluatorException
	 */
	private Token logicalLessThan(Token t1, Token t2) throws EvaluatorException {
		if(t1.type == TOKEN_TYPE.NUMBER_VALUE && t2.type == TOKEN_TYPE.NUMBER_VALUE  ) { // n , n 
			return new Token(TOKEN_TYPE.BOOLEAN_VALUE, t1.name+"<="+t2.name, (Double.valueOf(t2.value) <  Double.valueOf(t1.value) ? "true" : "false"));	
		}else if(t1.type==TOKEN_TYPE.STRING_VALUE && t2.type == TOKEN_TYPE.STRING_VALUE ){ // s , s
			return new Token(TOKEN_TYPE.BOOLEAN_VALUE, t1.name+"<="+t2.name, ((t1.value.compareTo(t2.value) > 0) ? "true" : "false"));
		}else{
			fail(" 숫자와 숫자, 문자와 문자만  비교할 수 있습니다. (" + t1.name + " < " + t2.name + ")");
		}
		return null;
	}


	private Token logicalNot(Token t1) throws EvaluatorException {
		if(t1.type == TOKEN_TYPE.BOOLEAN_VALUE){
			if(t1.value.equals("false")) {
				return new Token(TOKEN_TYPE.BOOLEAN_VALUE, "not"+t1.name, "true");
			}
			return new Token(TOKEN_TYPE.BOOLEAN_VALUE, "not"+t1.name, "false");
		}
		fail("논리연산자 and 실패");
		return null;
	}


	private Token logicalNotEqual(Token t1, Token t2) throws EvaluatorException {
		Token o = this.logicalEqual(t1, t2);
		if(o != null && o.value != null){
			if(Boolean.parseBoolean(o.value)){
				o.value = "false";
			}else{
				o.value = "true";
			}
		}
		return o;
	}


	private Token logicalOr(Token t1, Token t2) throws EvaluatorException {
		if(t1.type == TOKEN_TYPE.BOOLEAN_VALUE && t2.type == TOKEN_TYPE.BOOLEAN_VALUE){
			if(t1.value.equals("true") ||  t2.value.equals("true")) {
				return new Token(TOKEN_TYPE.BOOLEAN_VALUE, t1.name+"and"+t2.name, "true");
			}
			return new Token(TOKEN_TYPE.BOOLEAN_VALUE, t1.name+"and"+t2.name, "false");
		}
		fail("논리연산자 and 실패");
		return null;
	}


	/**
	 * 연산자 : 빼기 연산자
	 */
	private Token minus(Token t1, Token t2) throws EvaluatorException  {
		if(t1.type == TOKEN_TYPE.NUMBER_VALUE && t2.type == TOKEN_TYPE.NUMBER_VALUE  ) { // n + n 
			return new Token(TOKEN_TYPE.NUMBER_VALUE, t1.name+"+"+t2.name, String.format("%.2f",Double.valueOf(t2.value) - Double.valueOf(t1.value)));	
		}else{
				fail(" 숫자와 숫자만 빼기 연산자를 지원합니다. ");
		}
		return null;
	}

	/**
	 * mod (나머지) 연산자
	 */
	private Token mod(Token t1, Token t2) throws EvaluatorException {
		if(t1.type == TOKEN_TYPE.NUMBER_VALUE && t2.type == TOKEN_TYPE.NUMBER_VALUE  ) { // n + n 
			return new Token(TOKEN_TYPE.NUMBER_VALUE, t1.name+"+"+t2.name, String.format("%.2f",Double.valueOf(t2.value) % Double.valueOf(t1.value)));	
		} else {
			fail(" 숫자와 숫자만 나머지 연산자를 지원합니다.");
		}
		return null;
	}
	/**
	 * 곱하기 연산자 
	 */
	private Token multiple(Token t1, Token t2) throws EvaluatorException {
		if(t1.type == TOKEN_TYPE.NUMBER_VALUE && t2.type == TOKEN_TYPE.NUMBER_VALUE  ) { // n + n 
			return new Token(TOKEN_TYPE.NUMBER_VALUE, t1.name+"+"+t2.name, String.format("%.02f",Double.valueOf(t2.value) * Double.valueOf(t1.value)));	
		}else{
			fail(" 숫자와 숫자만 곱하기 연산자를 지원합니다.");
		}
		return null;
	}
	/**
	 * statement를 파싱하여 각각의 token으로 만들어서  tokenStack에 넣는다.
	 * @throws EvaluatorException 
	 */
	private void parseStatement() throws EvaluatorException {
		//파싱이 가능한지 체크한다
		if(isValid() == false) return;
		//이미 현재 tokenStack에 statement를 파싱한게 들어가 있다면 다시 파싱하지 않는다.
		if(parsedCode == statement.hashCode()) return; 
		if(tokenStack == null) {
			tokenStack = new Stack<Token>();
		}
		tokenStack.clear();
		int index = 0;
		char ch ;
		String word = "";
		while (index < statement.length()) {
			ch = statement.charAt(index);
			if(ch == ' ' || ch == '\t') {
				addToken(word); word = ""; index++; continue;
			}else if(ch == '\''){
				addToken(word); 
				word = ""+ch;
				index++;
				while(index < statement.length()){
					ch = statement.charAt(index);
					if(ch == '\'') { word+=ch; break;}
					word += ch;
					index++;
				}
				if(word == "\'") fail("막는 싱글따옴표가 빠짐");
				addToken(word); word = ""; index++; continue;
			}else if(ch == '='){
				addToken(word);
				if(index+1 >= statement.length() ) fail("잘못된 수식입니다");
				word = "=";  
				if(statement.charAt(index+1) == '='){
					word += statement.charAt(index+1);
				}
				addToken(word);	word = ""; index+=2; continue;
			}else if(tokenTable.containsKey(ch+"")){
				addToken(word);
				if((index+1 < statement.length()) && tokenTable.containsKey("" + ch +  statement.charAt(index+1)+"")){ //char 2개로 된 op
					word = ""+ ch +  statement.charAt(index+1) +"";
					addToken(word);	word = ""; index+=2; continue;
				}else{ //char 1개로 된 op
					word = ch+"";
					addToken(word);	word = ""; index+=1; continue;
				}
								
			}
			word += ch;
			
			index++;
		}
		addToken(word);	
		parsedCode = statement.hashCode();
		postfixStack = getPostfixStack(tokenStack);
	}

	/**
	 * 더하기 연산자
	 */
	private Token plus(Token t1, Token t2) {
		if(t1.type == TOKEN_TYPE.NUMBER_VALUE && t2.type == TOKEN_TYPE.NUMBER_VALUE  ) { // n + n
			return new Token(TOKEN_TYPE.NUMBER_VALUE, t1.name+"+"+t2.name, String.format("%.2f",(Double.valueOf(t1.value) + Double.valueOf(t2.value))));
		}else if(t1.type==TOKEN_TYPE.STRING_VALUE || t2.type == TOKEN_TYPE.STRING_VALUE)  { // s, s
			return new Token(TOKEN_TYPE.STRING_VALUE, t1.name+"+"+t2.name, t2.value.toString() + t1.value.toString());
		}
		return null;
	}
	private void printStack(Stack<Token> stack) {
		for (Token token : stack) {
			System.out.println(token.toString());
		}
	}
	public boolean evalBoolean() throws EvaluatorException {
		if(isValid() == false) {
			fail("변수를 가지고 있는 Map 또는 해석할 문장이 존재하지 않습니다");
		}
		Token o = eval();
		if(o == null || o.value == null) fail("수식을 해석하지 못했습니다 [" + this.statement + "]");
		if(o.value.equalsIgnoreCase("true")) {
			return true;
		}
		return false;
	}
	public boolean evalBoolean(Map<String, Object> variableMap) throws EvaluatorException {
		setVariableMap(variableMap);
		return evalBoolean();
	}
	public boolean evalBoolean(String statement) throws EvaluatorException {
		setStatement(statement);
		return evalBoolean();
	}
	public Double evalNumber() throws EvaluatorException {
		if(isValid() == false) {
			fail("변수를 가지고 있는 Map 또는 해석할 문장이 존재하지 않습니다");
		}
		Token o = eval();
		if (o == null || o.value == null || o.type != TOKEN_TYPE.NUMBER_VALUE){
			fail("수식을 해석하지 못했습니다 [" + this.statement + "]");
		}
		return Double.parseDouble(o.value);
		
	}
	public Double evalNumber(Map<String, Object> variableMap) throws EvaluatorException {
		setVariableMap(variableMap);
		return evalNumber();
	}


	public Double evalNumber(String statement) throws EvaluatorException {
		setStatement(statement);
		return evalNumber();
	}
	
	public String evalString() throws EvaluatorException{
		if(isValid() == false) {
			fail("변수를 가지고 있는 Map 또는 해석할 문장이 존재하지 않습니다");
		}
		Token o = eval();
		if(o == null || o.value == null) fail("수식을 해석하지 못했습니다 [" + this.statement + "]");
		return o.value.toString();
	}

	public String evalString(Map<String, Object> variableMap) throws EvaluatorException {
		setVariableMap(variableMap);
		return evalString();
	}


	public String evalString(String statement) throws EvaluatorException {
		setStatement(statement);
		return evalString();
	}

	public String getStatement() {
		return statement;
	}


	public Map<String, Object> getVariableMap() {
		return variableMap;
	}
	
	public boolean isDebug() {
		return debug;
	}


	public void setDebug(boolean debug) {
		this.debug = debug;
	}


	public void setStatement(String statement) throws EvaluatorException {
		if(statement == null) return;
		
		if(this.statement == null){
			this.statement = statement.trim();
			parseStatement();
		}else if(this.statement.equals(statement) == false){
			this.statement = statement.trim();
			parseStatement();
		}
	}


	public void setVariableMap(Map<String, Object> variableMap) {
		this.variableMap = variableMap;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String key;
		Object value;
		for (Entry<String, Object> entry : variableMap.entrySet()) {
			key = entry.getKey();
			value = String.valueOf(entry.getValue());
			sb.append(String.format("%s=%s,", key,value));
		}
		String s = sb.toString();
		if( s.endsWith(",") ){
			s = s.substring(s.length()-1);
		}

		return "Evaluator [\nstatement=" + statement + "\nvariableMap={" + s + "}\n]";
	}
	
}
