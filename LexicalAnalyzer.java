

import java.util.List;

/**
 * @author dgayler
 *
 */
public class LexicalAnalyzer
{

	private List<String> code;
	
	private int rowNum;

	private int index;
	
	private String current;
	
	private boolean sentEOF;
	
	private boolean sentLastEOLN;
	
	/************************************************
	 * if \n is a character in the string then these 
	 * boolean flags are not needed.
	 */
		
	/**
	 * @param code - list of lines of code
	 * @throws LexicalError if there is an error in 
	 * analyzing the code
	 */
	public LexicalAnalyzer(List<String> code) throws LexicalError
	{
		this.code = code;
		this.rowNum = 1;
		this.current = code.get(0);
		this.index = 0;
		this.sentEOF = false;
		this.sentLastEOLN = false;
		}

	/**
	 * skips white space leaving current character at next
	 * non-white space character
	 * @throws LexicalError
	 */
	private void skipWhiteSpace() throws LexicalError
	{
		while (index < current.length() && Character.isWhitespace(current.charAt(index)))
			index++;
	}

	/**
	 * @return next token 
	 * @throws LexicalError if error occurs generating next token
	 */
	public Token getToken() throws LexicalError
	{
		Token tok = null;
		if (sentEOF)
			throw new LexicalError ("attempt to read past end of program");
		skipWhiteSpace();
		if (index == current.length())
		{
			if (sentLastEOLN)
			{
				tok = new Token (TokenType.EOS, "", rowNum, index+1);
				sentEOF = true;
			}
			else
			{
				tok = new Token (TokenType.EOLN, "", rowNum, index+1);
				if (rowNum == code.size())
					sentLastEOLN = true;
				else
				{
					current = code.get(rowNum);
					rowNum++;
					index = 0;
				}
			}
		}
		else
		{
			switch (current.charAt(index))
			{
				case '+':
					tok = new Token(TokenType.PLUS, "+", rowNum, index+1);
					index++;
					break;
				case '-':
					tok = new Token(TokenType.MINUS, "-", rowNum, index+1);
					index++;
					break;
				case '*':
					tok = new Token(TokenType.TIMES, "*", rowNum, index+1);
					index++;
					break;
				case '/':
					tok = new Token(TokenType.DIVIDE, "/", rowNum, index+1);
					index++;
					break;
				case '(':
					tok = new Token(TokenType.LPAREN, "(", rowNum, index+1);
					index++;
					break;
				case ')':
					tok = new Token(TokenType.RPAREN, "+", rowNum, index+1);
					index++;
					break;
				case '^':
					tok = new Token(TokenType.EXPONENT, "^", rowNum, index+1);
					index++;
					break;
				default:
					if (Character.isDigit(current.charAt(index)))
					{
						int begin = index;
						String first = getDigitSequence();
						if (index < current.length() && current.charAt(index) == '.')
						{
							index++;
							String second = getDigitSequence();
							tok = new Token(TokenType.FLOATLIT, first + '.' + second, rowNum, begin);
						}
						else
							tok = new Token(TokenType.INTLIT, first, rowNum, begin);
					}
					else if (current.charAt(index) == '.')
					{
						int start = index;
						index++;
						String s = getDigitSequence();
						tok = new Token(TokenType.FLOATLIT, s, rowNum, start);
					}
					else
						throw new LexicalError(rowNum, index+1, current.charAt(index));
			}
		}
		return tok;
	}	

	/**
	 * @return longest string containing strictly digits starting at current character
	 * @throws LexicalError
	 */
	private String getDigitSequence() throws LexicalError
	{
		int start = index;
		while (index < current.length() && Character.isDigit(current.charAt(index)))
			index++;
		return current.substring(start, index);
	}

}
