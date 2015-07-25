package query.result;

import java.util.List;

import model.TableRow;

public class SelectQueryResult extends QueryResult
{
	public List<TableRow> rows;
	
	public SelectQueryResult(boolean succeeded, String message, List<TableRow> rows)
	{
		super(succeeded, message, "", 0);
		this.rows = rows;
	}
}
