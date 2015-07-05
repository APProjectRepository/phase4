package dos.math.sharif.edu.query.result;

public class QueryResult
{
	public boolean succeeded;			// true for successful queries, false for failed ones
	public String message;
	public String temp;
	public int ID;
	
	public QueryResult(boolean succeeded, String message, String temp, int ID)
	{
		super();
		this.succeeded = succeeded;
		this.message = message;
		this.temp = temp;
		this.ID = ID;
	}
}
