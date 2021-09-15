package taskManager.logger.argument;

import java.util.ArrayList;


public class RelationArgument {
	private String name;
	private String[] expressionList;
	
	
	public RelationArgument(int i){
		name = "";
		expressionList = new String[i];
	}
	

	public String[] getExpresisonList(){
		return expressionList;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	
	public String toString(){
		String result = name ;
		for(String exp : expressionList){
			result += exp + " ";
		}
		
		return result;
	}
	
}
