package taskManager.utility;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import kr.ac.uos.ai.arbi.model.GLFactory;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.parser.ParseException;
import taskManager.TaskManagerDataSource;
import uos.ai.jam.Goal;
import uos.ai.jam.Interpreter;
import uos.ai.jam.exception.AgentRuntimeException;
import uos.ai.jam.expression.Binding;
import uos.ai.jam.expression.Expression;
import uos.ai.jam.expression.Relation;
import uos.ai.jam.expression.Value;
import uos.ai.jam.expression.ValueLong;
import uos.ai.jam.plan.action.AchieveGoalAction;

public class GLMessageManager {
	public Interpreter interpreter;
	private Queue<String> messageQueue;

	
	
	public static void main(String[] ar) {
		
		new GLMessageManager();
		
	}

	public int toInteger(String input) {
		double doub = Double.parseDouble(input);
		int result = (int) doub;

		return result;
	}
	
	public  String turnbackPosition(String input) {
		String[] data = input.split(" ");
		return data[0] + " " + data[1] + " " + data[2] + " " + data[4] + " " + data[3];
	}
	
	public String contains(String input, String text) {
		if(input.contains(text)) {
			return "true";
		}
		
		return "false";
	}
	
	public void dequeueMessage() {

		if (messageQueue.isEmpty() == false) {
			String message = messageQueue.poll();
			GeneralizedList gl = null;
			try {
				gl = GLFactory.newGLFromGLString(message);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (gl.getName().equals("PostGoal")) {
				if (gl.getExpressionsSize() == 1)
					this.postGoal(gl.getExpression(0).toString(), "100");
				else {
					this.postGoal(gl.getExpression(0).toString(), gl.getExpression(1).toString());
				}
			} else if (gl.getName().equals("AssertFact")) {
				this.assertGL(gl.getExpression(0).toString());
			}
		}

	}

	public void removePlan(String planID) {
		interpreter.getPlanLibrary().removePlan(planID);
	}

	public GLMessageManager(Interpreter interpreter) {
		this.interpreter = interpreter;
		messageQueue = new LinkedList<String>();
	}

	public GLMessageManager() {
		String result = this.retrieveGLExpression("(arguments \"화자\" \"후식\" \"물\")", 2);
		//int result = retireveExpressionSize("(result)");
		System.out.println(result);
	}

	public String changeName(String input, String newName) {
		System.out.println("name Changed : " + input);

		String[] splitResult = input.split(" ");
		String result = splitResult[0].substring(1, splitResult[0].length());

		for (int i = 1; i < splitResult.length; i++) {
			input += " " + removeQuotationMarks(splitResult[i]);
		}

		return input;
	}

	public String retrieveElement(String input) {

		GeneralizedList list = null;
		String result = "";
		try {
			list = GLFactory.newGLFromGLString(input);

			for (int i = 0; i < list.getExpressionsSize(); i++) {
				result += removeQuotationMarks(list.getExpression(i).toString()) + ", ";
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public String removeQuotationMarks(String input) {
		if (input.startsWith("\"")) {
			input = input.substring(1, input.length() - 1);
		}
		return input;
	}

	public String retrieveTime(String input) {

		String[] resultList = input.split("T");

		resultList = resultList[1].split(":");

		String result = resultList[0].substring(1, resultList[0].length());
		result = result + "�떆";

		return result;

	}
	public String retrieveGLName(String glString) {
		String result = "";
		if (glString.startsWith("\"")) {
			glString = removeQuotationMarks(glString);
		}
		try {
			GeneralizedList gl = GLFactory.newGLFromGLString(glString);
			result = gl.getName();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return result;
	}
	
	
	public int retireveExpressionSize(String expression) {
		int result = 0;

		try {
			GeneralizedList gl = GLFactory.newGLFromGLString(expression);
			result = gl.getExpressionsSize();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public int getUtility(int value) {
		System.out.println("utility value : " + value);

		return value;
	}

	public String postGoal(String goal, String utility) {
		String result = null;
		GeneralizedList gl = null;
		try {
			gl = GLFactory.newGLFromGLString(goal);
			String name = gl.getName();
			List<Expression> expList = new ArrayList<Expression>();

			for (int i = 0; i < gl.getExpressionsSize(); i++) {
				String expressionValue = gl.getExpression(i).toString();
				expressionValue = this.removeQuotationMarks(expressionValue);
				if(expressionValue.startsWith("$")) {
				expressionValue = "null";
				}
				expList.add(new Value(expressionValue));
			}

			Relation r = interpreter.getWorldModel().newRelation(name, expList);
			int utilityValue = Integer.valueOf(removeQuotationMarks(utility));

			AchieveGoalAction generatedGoal = new AchieveGoalAction(name, r, new Value(utilityValue), null, null,
					interpreter);
			interpreter.getIntentionStructure().addUnique(generatedGoal, null, null);
			System.out.println("achieve goal posted : " + r.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (AgentRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public String unpostGoal(String goal) {

		String expression1 = "exp1";
		String expression2 = "exp2";
		String glName = "glName";

		String glText = "(" + glName + " " + expression1 + " " + expression2 + ")";

		try {
			GeneralizedList gl = GLFactory.newGLFromGLString(glText);

			gl.getExpression(1).toString();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String result = null;
		GeneralizedList gl = null;

		try {
			gl = GLFactory.newGLFromGLString(goal);
			String name = gl.getName();
			List<Expression> expList = new ArrayList<Expression>();

			for (int i = 0; i < gl.getExpressionsSize(); i++) {
				String expressionValue = gl.getExpression(i).toString();
				expList.add(new Value(expressionValue));
			}

			Relation r = interpreter.getWorldModel().newRelation(name, expList);

			AchieveGoalAction generatedGoal = new AchieveGoalAction(name, r, new Value(0), null, null, interpreter);

			interpreter.getIntentionStructure().drop(generatedGoal, null);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return result;
	}
	/*
	 * public String switchUtility(String goal1,String goal2){ String result = null;
	 * GeneralizedList gl = null;
	 * 
	 * try { gl = GLFactory.newGLFromGLString(goal1); String name = gl.getName();
	 * List<Expression> expList = new ArrayList<Expression>();
	 * 
	 * for(int i = 0; i < gl.getExpressionsSize();i++){ String expressionValue =
	 * gl.getExpression(i).toString(); expList.add(new Value(expressionValue)); }
	 * Relation r = interpreter.getWorldModel().newRelation(name, expList);
	 * AchieveGoalAction generatedGoal1 = new AchieveGoalAction(name, r, new
	 * ValueLong(0), null, null, interpreter);
	 * 
	 * gl = GLFactory.newGLFromGLString(goal2); name = gl.getName(); expList = new
	 * ArrayList<Expression>();
	 * 
	 * for(int i = 0; i < gl.getExpressionsSize();i++){ String expressionValue =
	 * gl.getExpression(i).toString(); expList.add(new Value(expressionValue)); } r
	 * = interpreter.getWorldModel().newRelation(name, expList); AchieveGoalAction
	 * generatedGoal2 = new AchieveGoalAction(name, r, new ValueLong(0), null, null,
	 * interpreter);
	 * 
	 * 
	 * Goal foundGoal1 =
	 * interpreter.getIntentionStructure().searchGoal(generatedGoal1, null); Goal
	 * foundGoal2 = interpreter.getIntentionStructure().searchGoal(generatedGoal2,
	 * null); Expression tempUtility = foundGoal1.getGoalAction().getUtility();
	 * 
	 * System.out.println("goal swapped");
	 * 
	 * System.out.println(foundGoal1.getGoalAction().getName() + " " +
	 * foundGoal1.getGoalAction().getUtility().toString());
	 * System.out.println(foundGoal2.getGoalAction().getName() + " " +
	 * foundGoal2.getGoalAction().getUtility().toString());
	 * 
	 * foundGoal1.getGoalAction().setUtility(foundGoal2.getGoalAction().getUtility()
	 * ); foundGoal2.getGoalAction().setUtility(tempUtility);
	 * 
	 * System.out.println(foundGoal1.getGoalAction().getName() + " " +
	 * foundGoal1.getGoalAction().getUtility().toString());
	 * System.out.println(foundGoal2.getGoalAction().getName() + " " +
	 * foundGoal2.getGoalAction().getUtility().toString());
	 * 
	 * } catch (ParseException e) { e.printStackTrace(); }
	 * 
	 * return result; }
	 * 
	 * 
	 * public String setUtility(String goal,String utility){ String result = null;
	 * GeneralizedList gl = null;
	 * 
	 * try { gl = GLFactory.newGLFromGLString(goal); String name = gl.getName();
	 * List<Expression> expList = new ArrayList<Expression>();
	 * 
	 * for(int i = 0; i < gl.getExpressionsSize();i++){ String expressionValue =
	 * gl.getExpression(i).toString(); expList.add(new Value(expressionValue)); }
	 * 
	 * Relation r = interpreter.getWorldModel().newRelation(name, expList); long
	 * utilityValue = Long.valueOf(this.removeQuotationMarks(utility));
	 * AchieveGoalAction generatedGoal = new AchieveGoalAction(name, r, new
	 * ValueLong(utilityValue), null, null, interpreter);
	 * 
	 * Goal foundGoal =
	 * interpreter.getIntentionStructure().searchGoal(generatedGoal, null);
	 * 
	 * foundGoal.getGoalAction().setUtility(new Value(utility));
	 * 
	 * System.out.println(foundGoal.getGoalAction().getName() + " " +
	 * foundGoal.getGoalAction().getUtility().toString()); } catch (ParseException
	 * e) { e.printStackTrace(); }
	 * 
	 * return result; }
	 * 
	 */

	public void startGoal(String arg) {
		try {

			GeneralizedList gl = GLFactory.newGLFromGLString(arg);
			String goal = "(ServicePerformed ";

			for (int i = 0; i < gl.getExpressionsSize(); i++) {
				goal += gl.getExpression(i).toString() + " ";
			}

			goal = goal.substring(0, goal.length() - 1);

			goal += ")";

			this.postGoal(goal, "100");

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	

	public String retrieveGLExpression(String input, int i) {
		String result = "";
		if (input.startsWith("\"")) {
			input = removeQuotationMarks(input);
		}
		try {
			GeneralizedList gl = GLFactory.newGLFromGLString(input);
			result = gl.getExpression(i).toString();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		result = this.removeQuotationMarks(result);
		return result;
	}

	public String retrieveNonGLExpression(String input, int i) {
		String result = "";
		result = input.split(" ")[i];

		return result;
	}

	public String retrieveGLGoal(String input) {
		String result = "";

		try {
			GeneralizedList gl = GLFactory.newGLFromGLString(input);
			result = gl.getName();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public void assertFact(String name, Object... args) {
		interpreter.getWorldModel().assertFact(name, args);
	}

	public void retractFact(String expression) {

		GeneralizedList gl = null;
		try {
			gl = GLFactory.newGLFromGLString(expression);
			String name = gl.getName();
			List<Expression> expList = new ArrayList<Expression>();

			for (int i = 0; i < gl.getExpressionsSize(); i++) {
				String expressionValue = gl.getExpression(i).toString();
				expList.add(new Value(expressionValue));
			}

			Relation r = interpreter.getWorldModel().newRelation(name, expList);
			interpreter.getWorldModel().retract(r, null);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	public void assertGL(String input) {
		String name = "";

		if (input.startsWith("(")) {

			try {
				GeneralizedList gl = GLFactory.newGLFromGLString(input);
				name = gl.getName();
				Object[] expressionList = new Object[gl.getExpressionsSize()];

				for (int i = 0; i < gl.getExpressionsSize(); i++) {
					if (gl.getExpression(i).isGeneralizedList()) {
						String glString = gl.getExpression(i).toString();
						expressionList[i] = GLFactory.unescape(glString);
					} else {
						kr.ac.uos.ai.arbi.model.Value value = gl.getExpression(i).asValue();
						if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.FLOAT) {
							expressionList[i] = value.floatValue();
						} else if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.INT) {
							expressionList[i] = value.intValue();
						} else if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.STRING) {
							String glString = value.stringValue();
							expressionList[i] = GLFactory.unescape(glString);
						} else {
							String glString = value.stringValue();
							expressionList[i] = GLFactory.unescape(glString);
						}
					}

				}

				assertFact(name, expressionList);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void assertGoal(String input) {
		GeneralizedList gl = null;

		try {
			gl = GLFactory.newGLFromGLString(input);
			String name = gl.getName();

			if (name.contains(":")) {

				String goalName = gl.getName().split(":")[1];
				String prefix = gl.getName().split(":")[0];

				name = prefix + ":Post" + goalName;
			}else {
				name = "Post" + name;
			}
			String[] expressionList = new String[gl.getExpressionsSize()];

			for (int i = 0; i < gl.getExpressionsSize(); i++) {
				expressionList[i] = gl.getExpression(i).toString();
				expressionList[i] = GLFactory.unescape(expressionList[i]);
				expressionList[i] = this.removeQuotationMarks(expressionList[i]);
			}

			assertFact(name, expressionList);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void updateFact(String string, String string2) {
		String name = "";

		if (string.startsWith("(")) {

			try {
				GeneralizedList gl = GLFactory.newGLFromGLString(string);
				name = gl.getName();
				gl = GLFactory.newGLFromGLString(string2);

				String[] expressionList = new String[gl.getExpressionsSize()];

				for (int i = 0; i < gl.getExpressionsSize(); i++) {
					expressionList[i] = gl.getExpression(i).toString();
				}

				this.retractFact(string);
				assertFact(name, expressionList);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public String escapeGL(String gl) {
		System.out.println("start escape : " + gl);
		String result = GLFactory.escape(gl);
		System.out.println(result);
		return result;
	}
	public String unescapeGL(String gl) {
		return GLFactory.unescape(gl);
	}
}
