

/**
 * @author dgayler
 *
 */
public class Token
{
	private TokenType tokType;
	
	private String lexeme;
	
	private int lineNum;
	
	private int colNum;

	/**
	 * @param tokType
	 * @param lexeme - string in program code
	 * @param lineNum - must be >= 0
	 * @param colNum - must be >= 0
	 * @throws LexicalError if lineNum or colNum is not valid
	 */
	public Token(TokenType tokType, String lexeme, int lineNum, int colNum) throws LexicalError
	{
		this.tokType = tokType;
		this.lexeme = lexeme;
		if (lineNum < 0)
			throw new LexicalError ("invalid line number");
		this.lineNum = lineNum;
		if (colNum < 0)
			throw new LexicalError ("invalid column number");
		this.colNum = colNum;
	}

	/**
	 * @return the lineNum
	 */
	public int getLineNum()
	{
		return lineNum;
	}

	/**
	 * @return the colNum
	 */
	public int getColNum()
	{
		return colNum;
	}

	public TokenType getTokType()
	{
		return tokType;
	}

	public String getLexeme()
	{
		return lexeme;
	}
	
	
}
