
public class ReturnValue
{

	private Number num;
	
	private boolean isInt;

	public ReturnValue(Number num, boolean isInt)
	{
		this.num = num;
		this.isInt = isInt;
	}

	/**
	 * @return the n
	 */
	public Number getNumber()
	{
		return num;
	}

	/**
	 * @return the isInt
	 */
	public boolean isInt()
	{
		return isInt;
	}

	public ReturnValue negate()
	{
		ReturnValue result = null;
		if (isInt)
		{
			Integer n = (Integer)num;
			int value = n.intValue();
			Integer n1 = new Integer(-value);
			result = new ReturnValue(n1,isInt);
		}
		else
		{
			Float f = (Float)num;
			float value = f.floatValue();
			Float f1 = new Float(-value);
			result = new ReturnValue(f,isInt);
		}
		return result;	
	}
	
	public ReturnValue performOp (Token opTok, ReturnValue nd)
	{
		ReturnValue result = null;
		if (isInt)
		{
			int left = num.intValue();
			if (nd.isInt)
			{
				int value = 0;
				int right = nd.num.intValue();
				if (opTok.getTokType() == TokenType.PLUS)
					value = left + right;
				else if (opTok.getTokType() == TokenType.MINUS)
					value = left - right;
				else if (opTok.getTokType() == TokenType.TIMES)
					value = left * right;
				else if (opTok.getTokType() == TokenType.DIVIDE)
					value = left / right;
				else
					value = (int)Math.pow(left, right);
				result = new ReturnValue(value, true);				
			}
			else
			{
				float value = 0;
				float right = nd.num.floatValue();
				if (opTok.getTokType() == TokenType.PLUS)
					value = left + right;
				else if (opTok.getTokType() == TokenType.MINUS)
					value = left - right;
				else if (opTok.getTokType() == TokenType.TIMES)
					value = left * right;
				else if (opTok.getTokType() == TokenType.DIVIDE)
					value = left / right;
				else
					value = (float)Math.pow(left, right);
				result = new ReturnValue (value, false);				
			}
		}
		else
		{
			float value = 0;
			float left = num.floatValue();
			float right = nd.num.floatValue();
			if (opTok.getTokType() == TokenType.PLUS)
				value = left + right;
			else if (opTok.getTokType() == TokenType.MINUS)
				value = left - right;
			else if (opTok.getTokType() == TokenType.TIMES)
				value = left * right;
			else if (opTok.getTokType() == TokenType.DIVIDE)
				value = left / right;
			else
				value = (float)Math.pow(left, right);
			result = new ReturnValue (value, false);				
		}		
		return result;
	}

	@Override
	public String toString()
	{
		String s;
		if (isInt)
			s = num.intValue() + "";
		else
			s = num.floatValue() + "";
		return s;
	}
	
}
