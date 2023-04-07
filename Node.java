import java.util.ArrayList;
import java.util.List;

public class Node
{
	private List<Node> children;
	
	private NonTerminal nodeType;
	
	private Token tok;
	
	/**
	 * @param nodeType used to create a node representing a non-terminal
	 */
	public Node(NonTerminal nodeType)
	{
		this.nodeType = nodeType;
		this.children = new ArrayList<Node>();
		this.tok = null;
	}

	/**
	 * @param tok used to create a node representing a terminal
	 */
	public Node (Token tok)
	{
		this.tok = tok;
		this.children = null;
		this.nodeType = NonTerminal.NONE;
	}
	
	/**
	 * @param n node to which child will be added
	 * @throws ParseError
	 */
	public void addChild (Node n) throws ParseError
	{
		if (n == null)
			throw new ParseError ("null node argument");
		children.add(n);
	}

	/**
	 * @return value obtained when evaluating the expression
	 */
	public ReturnValue evaluate()
	{
		ReturnValue result = null;
		switch (nodeType)
		{
			case PROGRAM:
				for (int i = 0; i < children.size(); i++)
					System.out.println (children.get(i).evaluate());
				break;
			case STATEMENT:
				Node exprNode = children.get(0);
				result = exprNode.evaluate();
				break;
			case EXPRESSION:
				Node e1 = children.get(0);
				if (e1.nodeType != NonTerminal.NONE)
				{
					result = e1.evaluate();
					if (children.size() > 1)
					{
						Token op = children.get(1).tok;
						Node e2 = children.get(2);
						result = result.performOp(op, e2.evaluate());
					}
				}
				else
				{
					Node n = children.get(1);
					result = n.evaluate();
					if (e1.tok.getTokType() == TokenType.MINUS)
						result = n.evaluate().negate();
				}
				break;
			case TERM:
				Node ef = children.get(0);
				result = ef.evaluate();
				if (children.size() > 1)
				{
					Token op = children.get(1).tok;
					Node e2 = children.get(2);
					result = result.performOp(op, e2.evaluate());
				}
				break;
			case FACTOR:
				Node ee = children.get(0);
				result = ee.evaluate();
				if (children.size() > 1)
				{
					Token op = children.get(1).tok;
					Node e2 = children.get(2);
					result = result.performOp(op, e2.evaluate());
				}
				break;
			case EXPONENT:
				Node n = children.get(0);
				if (n.nodeType == NonTerminal.NONE)
					n = children.get(1);
				result = n.evaluate();
				break;
			case NEG_EXPR:
				Node nn = children.get(0);
				result = nn.evaluate();
				result = result.negate();
				break;
			case NUM:
				result = createReturnNode(children.get(0).tok);
				break;
		}
		return result;
	}

	/**
	 * @param tok whether token is an integer or a float
	 * @return ReturnValue associated with tok
	 */
	private ReturnValue createReturnNode(Token tok)
	{
		ReturnValue result = null;
		if (tok.getTokType() == TokenType.INTLIT)
		{
			Integer value = Integer.valueOf(tok.getLexeme());
			result = new ReturnValue(value, true);
		}
		else
		{
			Float value = Float.valueOf(tok.getLexeme());
			result = new ReturnValue(value, false);
		}
		return result;
	}

}
