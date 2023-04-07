import java.util.List;

public class Parser
{

	private LexicalAnalyzer lex;
	
	private Token tok;
	
	/**
	 * @param code - list of lines of code
	 * @throws LexicalError if error when scanning the code
	 */
	public Parser(List<String> code) throws LexicalError
	{
		lex = new LexicalAnalyzer (code);
		tok = lex.getToken();
	}
		
	/**
	 * @return ParseTree representing the program
	 * @throws ParseError
	 * @throws LexicalError
	 */
	public ParseTree parse() throws ParseError, LexicalError
	{
		Node progNode = new Node(NonTerminal.PROGRAM);
		do
		{
			Node s = getStatement();
			progNode.addChild(s);
		}
		while (tok.getTokType() != TokenType.EOS);
		return new ParseTree(progNode);
	}

	/**
	 * @return Node representing a statement
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getStatement() throws ParseError, LexicalError
	{
		Node stmtNode = new Node(NonTerminal.STATEMENT);
		Node exprNode = getExpression();
		stmtNode.addChild(exprNode);
		Node tokNode = new Node(tok);
		stmtNode.addChild(tokNode);
		match(TokenType.EOLN);
		return stmtNode;
	}

	/**
	 * @return Node representing an expression
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getExpression() throws ParseError, LexicalError
	{
		Node termNode = getTerm();
		return getExpressionPrime(termNode);
	}

	/**
	 * @param e Node representing the first expression in production
	 * @return Node representing the entire expression
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getExpressionPrime(Node e) throws ParseError, LexicalError
	{
		Node exprNode = null;
		if (tok.getTokType() == TokenType.PLUS)
		{
			Node tokNode = new Node(tok);			
			match (TokenType.PLUS);
			Node termNode = getTerm();
			Node addNode = createBinaryExpression (e, tokNode, termNode);
			exprNode = getExpressionPrime(addNode);
		}
		else if (tok.getTokType() == TokenType.MINUS)
		{
			Node tokNode = new Node(tok);
			match (TokenType.MINUS);
			Node termNode = getTerm();
			Node subNode = createBinaryExpression (e, tokNode, termNode);
			exprNode = getExpressionPrime(subNode);
		}
		else
			exprNode = e;
		return exprNode;
	}

	/**
	 * @param e first expression
	 * @param tokNode node representing the operation
	 * @param termNode second terms
	 * @return Node representing the result of the operation on the 2 operands
	 * @throws ParseError
	 */
	private Node createBinaryExpression(Node e, Node tokNode, Node termNode) throws ParseError
	{
		Node exprNode = new Node(NonTerminal.EXPRESSION);
		exprNode.addChild(e);
		exprNode.addChild(tokNode);
		exprNode.addChild(termNode);
		return exprNode;
	}

	/**
	 * @return Node representing a term
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getTerm() throws ParseError, LexicalError
	{
		Node facNode = getFactor();
		return getTermPrime(facNode);
	}

	/**
	 * @param t represents the input term
	 * @return Node representing full term  
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getTermPrime(Node t) throws ParseError, LexicalError
	{
		Node tm = null;
		if (tok.getTokType() == TokenType.TIMES)
		{
			Node tokNode = new Node(tok);
			match (TokenType.TIMES);
			Node fNode = getFactor();
			Node mulTerm = createBinaryTerm(t, tokNode, fNode);
			tm = getTermPrime(mulTerm);
		}
		else if (tok.getTokType() == TokenType.DIVIDE)
		{
			Node tokNode = new Node(tok);
			match (TokenType.DIVIDE);
			Node fNode = getFactor();
			Node divTerm = createBinaryTerm(t, tokNode, fNode);
			tm = getTermPrime(divTerm);
		}
		else
			tm = t;
		return tm;
	}

	/**
	 * @param t node representing first term
	 * @param tokNode node representing operation
	 * @param fNode node representing second operand
	 * @return node representing result of operation
	 * @throws ParseError
	 */
	private Node createBinaryTerm(Node t, Node tokNode, Node fNode) throws ParseError
	{
		Node termNode = new Node (NonTerminal.TERM);
		termNode.addChild(t);
		termNode.addChild(tokNode);
		termNode.addChild(fNode);
		return termNode;
	}

	/**
	 * @return node representing factor
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getFactor() throws ParseError, LexicalError
	{
		Node expNode = getExp();
		return getFactorPrime(expNode);
	}

	/**
	 * @param f node representing first operand
	 * @return node representing entire factor
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getFactorPrime(Node f) throws ParseError, LexicalError
	{
		Node fNode = null;
		if (tok.getTokType() == TokenType.EXPONENT)
		{
			Node tokNode = new Node(tok);
			match(TokenType.EXPONENT);
			Node exp = getExp();
			fNode = createBinaryFactor(f,tokNode, exp);
		}
		else
			fNode = f;
		return fNode;
	}

	/**
	 * @param f node representing first operand
	 * @param tokNode node representing operator
	 * @param exp node representing second operand
	 * @return node representing result of operation
	 * @throws ParseError
	 */
	private Node createBinaryFactor(Node f, Node tokNode, Node exp) throws ParseError
	{
		Node facNode = new Node (NonTerminal.FACTOR);
		facNode.addChild(facNode);
		facNode.addChild(tokNode);
		facNode.addChild(exp);
		return facNode;
	}

	/**
	 * @return node representing exp 
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getExp() throws ParseError, LexicalError
	{
		Node eNode = null;
		if (tok.getTokType() == TokenType.LPAREN)
		{
			Node tokNode = new Node(tok);
			match (TokenType.LPAREN);
			Node expNode = getExpression();
			Node tokNode1 = new Node(tok);
			match(TokenType.RPAREN);	
			eNode = createExprParen (tokNode, expNode, tokNode1);
		}
		else if (tok.getTokType() == TokenType.MINUS)
		{
			match(TokenType.MINUS);
			Node exp = getExpression();
			Node exp1 = createNegExp(exp);
			Node exp2 = createUnaryExpression(exp1);
			eNode = createExprExponent(exp2);
		}
		else
			eNode = getNum();
		return eNode;
	}

	/**
	 * @param tokNode node representing left paren
	 * @param expNode node representing expression
	 * @param tokNode1 node representing right parent
	 * @return node representing expression enclosed in parentheses
	 * @throws ParseError
	 */
	private Node createExprParen(Node tokNode, Node expNode, Node tokNode1) throws ParseError
	{
		Node n = new Node (NonTerminal.EXPRESSION);
		n.addChild(tokNode);
		n.addChild(expNode);
		n.addChild(tokNode1);
		return n;
	}

	/**
	 * @param e Node representing expression
	 * @return node representing e as an exponent
	 * @throws ParseError
	 */
	private Node createExprExponent(Node e) throws ParseError
	{
		Node expNode = new Node(NonTerminal.EXPONENT);
		expNode.addChild(e);
		return expNode;
	}

	/**
	 * @param e node representing expression
	 * @return node representing unary expression
	 * @throws ParseError
	 */
	private Node createUnaryExpression(Node e) throws ParseError
	{
		Node expNode = new Node(NonTerminal.EXPRESSION);
		expNode.addChild(e);
		return expNode;
	}

	/**
	 * @param e node representing expression
	 * @return node representing negation of e
	 * @throws ParseError
	 */
	private Node createNegExp(Node e) throws ParseError
	{
		Node n = new Node (NonTerminal.NEG_EXPR);
		n.addChild(e);
		return n;
	}

	/**
	 * @return node representing a number
	 * @throws ParseError
	 * @throws LexicalError
	 */
	private Node getNum() throws ParseError, LexicalError
	{
		Node n = new Node (NonTerminal.NUM);
		Node tokNode = new Node (tok);
		if (tok.getTokType() == TokenType.INTLIT)
			match (TokenType.INTLIT);
		else
			match (TokenType.FLOATLIT);
		n.addChild(tokNode);
		return n;
	}

	/**
	 * @param type expected token type
	 * @throws ParseError if expected token type is different than the current token type
	 * @throws LexicalError
	 */
	private void match(TokenType type) throws ParseError, LexicalError
	{
		if (tok.getTokType() != type)
			throw new ParseError ("expected " + type + " at row " + tok.getLineNum() + " and column " + tok.getColNum());
		tok = lex.getToken();
	}
}
