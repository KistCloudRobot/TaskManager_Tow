package taskManager.utility;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import kr.ac.uos.ai.arbi.model.parser.ParseException;
import uos.ai.jam.Interpreter;
import uos.ai.jam.expression.Expression;
import uos.ai.jam.expression.FunctionCall;
import uos.ai.jam.expression.ObjectGetField;
import uos.ai.jam.expression.ObjectInvokeMethod;
import uos.ai.jam.expression.Relation;
import uos.ai.jam.expression.Value;
import uos.ai.jam.expression.condition.Condition;
import uos.ai.jam.expression.condition.ExpressionCondition;
import uos.ai.jam.expression.condition.FactCondition;
import uos.ai.jam.expression.condition.RetrieveCondition;
import uos.ai.jam.plan.Plan;
import uos.ai.jam.plan.PlanContext;
import uos.ai.jam.plan.action.AchieveGoalAction;
import uos.ai.jam.plan.action.Action;
import uos.ai.jam.plan.action.AssertAction;
import uos.ai.jam.plan.action.AssignAction;
import uos.ai.jam.plan.action.GoalAction;
import uos.ai.jam.plan.action.MaintainGoalAction;
import uos.ai.jam.plan.action.ObjectInvokeAction;
import uos.ai.jam.plan.action.PerformGoalAction;
import uos.ai.jam.plan.action.PostAction;
import uos.ai.jam.plan.action.RetractAction;
import uos.ai.jam.plan.action.RetrieveAction;
import uos.ai.jam.plan.action.UnpostAction;
import uos.ai.jam.plan.action.UpdateAction;
import uos.ai.jam.plan.constructor.PlanAtomicConstruct;
import uos.ai.jam.util.ClassLoaderEX;

public class JAMOwlParser {
	private String sourceURL = "http://www.arbi.com/ontologies/test";
	private String owlFile = "./owl/isro_service.owl";
	public String NS = sourceURL + "#";
	private Property rdfType;
	private Property operator;
	private Property righthandExpression;
	private Property lefthandExpression;
	private Property consistOfStatement;
	
	private Interpreter interpreter;
	private ClassLoaderEX _loader;
	public static void main(String[] ar){
		new JAMOwlParser();
	}
	
	
	public JAMOwlParser(){
		rdfType = ResourceFactory.createProperty("rdf:type");
		
		_loader = new ClassLoaderEX();
		parseOwlFile(owlFile);
		
		rdfType = ResourceFactory.createProperty("rdf:type");
		operator = ResourceFactory.createProperty("");
		righthandExpression = ResourceFactory.createProperty("");
		lefthandExpression = ResourceFactory.createProperty("");
		consistOfStatement = ResourceFactory.createProperty("http://www.robot-arbi.kr/ontologies/isro_service.owl#consistOfStatement");
	}
	
	public Relation retrieveRelation(Resource relationResource) throws ParseException{
		Relation relation = null;
		List<Expression> el = new LinkedList<Expression>();
		
		Property contextProperty = ResourceFactory.createProperty("RelationID");
		Resource relationID = relationResource.getProperty(contextProperty).getResource();
		
		Property relationArguments = ResourceFactory.createProperty("RelationArgument");
		StmtIterator iter = relationArguments.listProperties(consistOfStatement);
		
		while(iter.hasNext()){
			Statement stmt = iter.nextStatement();
			Resource node = (Resource)stmt.getObject();
			Expression exp = retrieveExpression(node);
			el.add(exp);
		}
		
		relation = interpreter.getWorldModel().newRelation(relationID.toString(), el);
		
		return relation;
	}
	
	public Expression retrieveExpression(Resource expResource) throws ParseException{
		String type = expResource.getProperty(rdfType).toString();
		List<Expression> expressionList = new LinkedList<Expression>();
		Expression expression = null;
		
		if(type.equals("ConditionalExpression")){
			Resource righthandResource = expResource.getProperty(righthandExpression).getResource();
			Resource lefthandResource = expResource.getProperty(lefthandExpression).getResource();
			String operatorToken = expResource.getProperty(operator).getString();
			expressionList.add(retrieveExpression(righthandResource));
			expressionList.add(retrieveExpression(lefthandResource));
			
			expression = new FunctionCall(interpreter,operatorToken,expressionList);
		}else if(type.equals("AdditiveExpression")){
			Resource righthandResource = expResource.getProperty(righthandExpression).getResource();
			Resource lefthandResource = expResource.getProperty(lefthandExpression).getResource();
			String operatorToken = expResource.getProperty(operator).getString();
			expressionList.add(retrieveExpression(righthandResource));
			expressionList.add(retrieveExpression(lefthandResource));
			
			expression = new FunctionCall(interpreter,operatorToken,expressionList);
		}else if(type.equals("MultiplicativeExpression")){
			Resource righthandResource = expResource.getProperty(righthandExpression).getResource();
			Resource lefthandResource = expResource.getProperty(lefthandExpression).getResource();
			String operatorToken = expResource.getProperty(operator).getString();
			expressionList.add(retrieveExpression(righthandResource));
			expressionList.add(retrieveExpression(lefthandResource));
			
			expression = new FunctionCall(interpreter,operatorToken,expressionList);
		}else if(type.equals("RelationalExpression")){

			Resource righthandResource = expResource.getProperty(righthandExpression).getResource();
			Resource lefthandResource = expResource.getProperty(lefthandExpression).getResource();
			String operatorToken = expResource.getProperty(operator).getString();
			expressionList.add(retrieveExpression(righthandResource));
			expressionList.add(retrieveExpression(lefthandResource));
			
			expression = new FunctionCall(interpreter,operatorToken,expressionList);
		}else if(type.equals("UnaryExpression")){
			Property primaryProperty = ResourceFactory.createProperty("");
			Resource primaryResource = expResource.getProperty(primaryProperty).getResource();
			Expression primaryExpression = retrieveExpression(primaryResource);
			expressionList.add(primaryExpression);
			expression = new FunctionCall(interpreter,"!",expressionList);
		}else if(type.equals("ClassOperation")){
			
			try {
				StringTokenizer tokenizer = new StringTokenizer(expResource.toString(), ".");
				Class<?> clazz = _loader.loadClass(tokenizer);
				Expression classExpression = new Value(clazz);
				if (!tokenizer.hasMoreTokens()) {
					throw new ParseException("require: field/method name");
				}
				while(tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					if (tokenizer.hasMoreTokens()) {
						classExpression = new ObjectGetField(classExpression, token);
					} else {
						
						if (expressionList != null) {
							expression = new ObjectInvokeMethod(classExpression, token, expressionList);
						} else {
							expression = new ObjectGetField(classExpression, token);
						}
					}
				}
				return classExpression;
			} catch(ClassNotFoundException e) {
				throw new ParseException(e.getLocalizedMessage());
			}
		}
		
		
		
		
		return expression;
	}
	
	public PlanContext getContext(Resource input) throws ParseException{
		PlanContext context = new PlanContext();
		Property contextProperty = ResourceFactory.createProperty("http://www.robot-arbi.kr/ontologies/isro_service.owl#PlanContext");
		Resource preconditionBlock = input.getProperty(contextProperty).getResource();
		StmtIterator iter = preconditionBlock.listProperties(consistOfStatement);
		List<Condition> conditionList = new LinkedList<Condition>();
		
		while(iter.hasNext()){
			Statement stmt = iter.nextStatement();
			RDFNode node = stmt.getObject();
			Resource resourceNode = (Resource)node;
			Condition condition;
			String conditionType = resourceNode.getProperty(rdfType).getResource().toString();
			if(conditionType.equals("ExpressionCondition")){
				Expression retrievedExpression = retrieveExpression(resourceNode);
				condition = new ExpressionCondition(retrievedExpression);
				conditionList.add(condition);
			}else{
				Relation relation = retrieveRelation(resourceNode);
				if(conditionType.equals("http://www.robot-arbi.kr/ontologies/isro_service.owl#Fact")){
					condition = new FactCondition(relation,interpreter);
				}else{
					condition = new RetrieveCondition(relation,interpreter);
				}
			}
			
		}
		
		
		context.addConditions(conditionList);
		return context;
	}
	
	public PlanContext getPrecondition(Resource input) throws ParseException{
		PlanContext context = new PlanContext();
		Property contextProperty = ResourceFactory.createProperty("http://www.robot-arbi.kr/ontologies/isro_service.owl#PlanPrecondition");
		Resource preconditionBlock = input.getProperty(contextProperty).getResource();
		StmtIterator iter = preconditionBlock.listProperties(consistOfStatement);
		List<Condition> conditionList = new LinkedList<Condition>();
		
		while(iter.hasNext()){
			Statement stmt = iter.nextStatement();
			RDFNode node = stmt.getObject();
			Resource resourceNode = (Resource)node;
			Condition condition;
			String conditionType = resourceNode.getProperty(rdfType).getResource().toString();
			if(conditionType.equals("ExpressionCondition")){
				Expression retrievedExpression = retrieveExpression(resourceNode);
				condition = new ExpressionCondition(retrievedExpression);
				conditionList.add(condition);
			}else{
				Relation relation = retrieveRelation(resourceNode);
				if(conditionType.equals("http://www.robot-arbi.kr/ontologies/isro_service.owl#Fact")){
					condition = new FactCondition(relation,interpreter);
				}else{
					condition = new RetrieveCondition(relation,interpreter);
				}
			}
			
		}
		
		
		context.addConditions(conditionList);
		return context;
	}
	
	public String getPlanName(Resource input){
		PlanContext context = new PlanContext();
		Property contextProperty = ResourceFactory.createProperty("http://www.robot-arbi.kr/ontologies/isro_service.owl#planName");
		input.getProperty(contextProperty);
		
		return null;
	}
	
	public Expression setUtility(Resource input){
		PlanContext context = new PlanContext();
		Property contextProperty = ResourceFactory.createProperty("http://www.robot-arbi.kr/ontologies/isro_service.owl#utility");
		input.getProperty(contextProperty);
		
		return null;
	}
	
	public PlanAtomicConstruct getFailure(Resource input){
		PlanContext context = new PlanContext();
		Property contextProperty = ResourceFactory.createProperty("http://www.robot-arbi.kr/ontologies/isro_service.owl#PlanFailure ");
		input.getProperty(contextProperty);
		
		return null;
	}
	
	public PlanAtomicConstruct getEffect(Resource input){
		PlanContext context = new PlanContext();
		Property contextProperty = ResourceFactory.createProperty("http://www.robot-arbi.kr/ontologies/isro_service.owl#hasPlanEffect");
		input.getProperty(contextProperty);
		
		return null;
	}
	
	public String getAttributes(Resource input){
		PlanContext context = new PlanContext();
		Property contextProperty = ResourceFactory.createProperty("http://www.robot-arbi.kr/ontologies/isro_service.owl#attributes");
		input.getProperty(contextProperty);
		
		return null;
	}
	
	public void getConstructorBlock(Resource input){
		
		
		
	}
	
	public Action getGoalAction(Resource input){
		PlanContext context = new PlanContext();
		Property contextProperty = ResourceFactory.createProperty("http://www.robot-arbi.kr/ontologies/isro_service.owl#GoalAction");
		input.getProperty(contextProperty);
		
		
		return null;
	}
	
	public Action retrieveAction(Resource input) throws ParseException{
		Action action = null;
		String actionType = input.getProperty(rdfType).getResource().toString();
		if(actionType.equals("AssignAction")){
			Resource righthandResource = input.getProperty(righthandExpression).getResource();
			Resource lefthandResource = input.getProperty(lefthandExpression).getResource();
			
			Expression righthandExpression = retrieveExpression(righthandResource);
			Expression lefthandExpression = retrieveExpression(lefthandResource);
			
			
			action = new AssignAction(lefthandExpression,righthandExpression);
		}else if(actionType.equals("ClassOperation")){
			Resource lefthandResource = input.getProperty(lefthandExpression).getResource();
			Expression lefthandExpression = retrieveExpression(lefthandResource);
			action = new ObjectInvokeAction(lefthandExpression);
		}else if(actionType.equals("Post")){
			Resource relation = input.getProperty(consistOfStatement).getResource();
			GoalAction goalAction = (GoalAction)retrieveAction(relation);
			
			new PostAction(goalAction,interpreter);
		}else if(actionType.equals("Unpost")){
			Resource relation = input.getProperty(consistOfStatement).getResource();
			GoalAction goalAction = (GoalAction)retrieveAction(relation);
			
			new UnpostAction(goalAction,interpreter);
		}else if(actionType.equals("Assert")){
			Resource relation = input.getProperty(consistOfStatement).getResource();
			Relation relationValue = retrieveRelation(relation);
			action = new AssertAction(relationValue,interpreter);
		}else if(actionType.equals("Retrieve")){
			Resource relation = input.getProperty(consistOfStatement).getResource();
			Relation relationValue = retrieveRelation(relation);
			action = new RetrieveAction(relationValue,interpreter);
		}else if(actionType.equals("Update")){
			Resource oldrelation = input.getProperty(consistOfStatement).getResource();
			Relation oldValue = retrieveRelation(oldrelation);
			
			Resource newrelation = input.getProperty(consistOfStatement).getResource();
			Relation newValue = retrieveRelation(newrelation);
			
			action = new UpdateAction(oldValue,newValue,interpreter);
		}else if(actionType.equals("Retract")){
			Resource relation = input.getProperty(consistOfStatement).getResource();
			Relation relationValue = retrieveRelation(relation);
			action = new RetractAction(relationValue,interpreter);
		}else if(actionType.equals("GoalAction")){
			Resource relation = input.getProperty(consistOfStatement).getResource();
			Relation relationValue = retrieveRelation(relation);
			
			Resource utility = input.getProperty(consistOfStatement).getResource();
			Expression utilityValue = retrieveExpression(relation);
			String goalType = input.getProperty(rdfType).getResource().toString();
			
			GoalAction goalAction = null;
			if(goalType.equals("Perform")){
				goalAction = new PerformGoalAction(relationValue.getName(),relationValue,null,null,null,interpreter);
			}else if(goalType.equals("Achieve")){
				goalAction = new AchieveGoalAction(relationValue.getName(),relationValue,null,null,null,interpreter);
			}else if(goalType.equals("Maintain")){
				goalAction = new MaintainGoalAction(relationValue.getName(),relationValue,null,interpreter);
			}
			
			goalAction.setUtility(utilityValue);
			
			action = goalAction;
		}
		
		
		
		return action;
		
	}
	
	public void parseOwlFile(String fileLocation){
		Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		model.read(fileLocation);
		Plan plan = new Plan();
		
		
		Resource vcard = model.getResource("http://www.robot-arbi.kr/ontologies/isro_service.owl#MoveToDestination");
		StmtIterator iter = model.listStatements();
		Property prop = ResourceFactory.createProperty("rdf:type");
		
		
		System.out.println(vcard.getProperty(prop).getString());
		
		
		
		/*
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			Resource subject = stmt.getSubject(); // get the subject
			Property predicate = stmt.getPredicate(); // get the predicate
			RDFNode object = stmt.getObject(); // get the object
			
			System.out.println("statement start");
			System.out.println("s : " + subject.toString());
			System.out.println("p : " + predicate.toString() + " ");
			if (object instanceof Resource) {
				System.out.println("o : " + object.toString());
			} else {
				// object is a literal
				System.out.println("o : \"" + object.toString() + "\"");
			}

			System.out.println(" .");
		}
	*/
		// Resource name = vcard.getPropertyResourceValue("");
	}
}
