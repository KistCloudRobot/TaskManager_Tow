package taskManager.logger.argument;

import java.util.ArrayList;

public class GoalArgument {
	private String name;
	private ArrayList<String> expressionList;
	private String utility;
	
	
	
	public GoalArgument(){
		name = "";
		expressionList = new ArrayList<String>();
		utility = "";
	}
	
	public void addExpression(String expression){
		expressionList.add(expression);
	}
	public ArrayList<String> getExpresisonList(){
		return expressionList;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}

	public String getUtility() {
		return utility;
	}

	public void setUtility(String utility) {
		this.utility = utility;
	}
	
	
	public String toString(){
		return name;
	}
}
