package taskManager.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import kr.ac.uos.ai.arbi.model.GLFactory;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.parser.ParseException;
import uos.ai.jam.Interpreter;
import uos.ai.jam.JAM;
import uos.ai.jam.WorldModelTable.WorldModelRelation;
import uos.ai.jam.expression.Binding;
import uos.ai.jam.expression.Expression;
import uos.ai.jam.expression.Relation;
import uos.ai.jam.expression.Symbol;
import uos.ai.jam.expression.Value;
import uos.ai.jam.expression.Variable;
import uos.ai.jam.parser.JAMParser;

public class JAMUtilityManager {
	private Interpreter interpreter;
	private int actionID;
	private int goalID;
	private final ReadWriteLock								_lock;
	
	public JAMUtilityManager(Interpreter i) {
		this.interpreter = i;
		actionID = 0;
		goalID = 0;
		_lock = new ReentrantReadWriteLock();
	}
	
	public int getUtility(String input){
		
		int utilityValue = 100;
		
		
		return utilityValue;
	}
	
	public String getActionID(String agentName) {
		_lock.writeLock().lock();
		try {
			actionID += 1;
			System.out.println("actionID retrieved");
			return agentName + "_" + actionID;
		} finally {
			_lock.writeLock().unlock();
		}
	}
	
	public String getGoalID(String agent) {
		goalID += 1;
		return agent + goalID; 
	}
	
	public float getPolicy(String policyName,String servicePackage) {
		List<Expression> expressionList = new ArrayList<Expression>();
		Value spName = new Value(servicePackage);
		Variable policyValue = new Variable(new Symbol("policyValue"));
		expressionList.add(spName);
		expressionList.add(policyValue);
		Relation rel = interpreter.getWorldModel().newRelation(policyName, expressionList);
		
		Binding b = new Binding();
		b.unbindVariables(rel);
		boolean result = interpreter.getWorldModelManager().getWorldModel().match(rel, b);

		return Float.parseFloat(b.getValue(policyValue).toString());
	}
	
	public void sleepAwhile(int miliSecond) {
		System.out.println("start wait");
		try {
			Thread.sleep(miliSecond);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("wait finished");
	}
	public String dateCalculation(int argDate, int argHour){
		Calendar calendar = Calendar.getInstance();
		
		calendar.add(Calendar.DAY_OF_MONTH, argDate);
		calendar.add(Calendar.HOUR_OF_DAY, argHour);
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		int sec = calendar.get(Calendar.SECOND);
		
		String result = year + "-" + month + "-" + day + "T" + hour + ":" + min + ":" + sec;

		System.out.println(result);
		return result;
		
	}
	public int retrieveTimeInterval(String startTime, String endTime){
		int result = 0;
		String startTimeSplited = startTime.split("T")[1];
		String endTimeSplited = endTime.split("T")[1];
		
		String[] startTimeList = startTime.split(":");
		String[] endTimeList = endTime.split(":");
		
		int startTimeMin = Integer.parseInt(startTimeList[0]) * 60 + Integer.parseInt(startTimeList[1]);
		int endTimeMin = Integer.parseInt(endTimeList[0]) * 60 + Integer.parseInt(endTimeList[1]);
		result = (endTimeMin - startTimeMin);
		
		return result;
	}

	public String addTime(String time, String timeToAdd){
		String[] resultList = time.split("T");
		int addTime = Integer.parseInt(timeToAdd);
		
		int year = Integer.parseInt(resultList[0].split("-")[0]);
		int month = Integer.parseInt(resultList[0].split("-")[1]);
		int date = Integer.parseInt(resultList[0].split("-")[2]);
		
		int hrs = Integer.parseInt(resultList[1].split(":")[0]);
		int min = Integer.parseInt(resultList[1].split(":")[1]);
		int sec = Integer.parseInt(resultList[1].split(":")[2]);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, date, hrs, min, sec);
		calendar.add(Calendar.HOUR_OF_DAY, addTime);
		
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		date = calendar.get(Calendar.DAY_OF_MONTH);
		hrs = calendar.get(Calendar.HOUR_OF_DAY);
		min = calendar.get(Calendar.MINUTE);
		sec = calendar.get(Calendar.SECOND);

		String result = year + "-" + month + "-" + date + "T" + hrs + ":" + min + ":" + sec;
		
		
		return result;
	}
	
	public String toVerbalTime(String time){
		
		String[] resultList = time.split("T");

		int hrs = Integer.parseInt(resultList[1].split(":")[0]);
		int min = Integer.parseInt(resultList[1].split(":")[1]);
		int sec = Integer.parseInt(resultList[1].split(":")[2]);
		

		String result =  hrs + " �떆 " + min + "遺�";
		
		return result;
	}
	
	public String currentTime() {
		Calendar calendar = Calendar.getInstance();
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);

		String dateSentence = new String();

		dateSentence += year;
		dateSentence += "�뀈 ";
		dateSentence += month;
		dateSentence += "�썡 ";
		dateSentence += day;
		dateSentence += "�씪 ";
		dateSentence += String.valueOf(hour);
		dateSentence += "�떆 ";
		dateSentence += String.valueOf(min);
		dateSentence += "遺꾩엯�땲�떎. ";

		return dateSentence;
	}

	public void loadPlan(String path) {
		File plan = new File(path);
		if (!plan.exists()) {
			System.out.println("Plan File is not found");
			return;
		}
		if (!plan.isFile()) {
			System.out.println("This Path is not a File");
			return;
		}
		
		JAMParser.parseFile(interpreter, new File(path));
	}

	public void parsePlan(String plan) {
		JAMParser.parseString(interpreter, plan);
	}

	public String readFile(String input) {
		BufferedReader br = null;
		FileReader fr = null;
		StringBuilder builder = new StringBuilder();
		try {
			fr = new FileReader(input);

			br = new BufferedReader(fr);

			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				builder.append(currentLine);
				builder.append("\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return builder.toString();
	}

	public void loadPlanPackage(String folder) {
		File directory = new File(folder);
		if (!directory.exists()) {
			System.out.println("Plan Package is not found");
			return;
		}
		if (directory.isFile()) {
			System.out.println("This Path is not a Package");
			return;
		}
		ArrayList<File> files = new ArrayList<File>();
		

		findPath(files, directory);
		
		
		for (File file : files) {
			JAMParser.parseFile(interpreter, file);
		}
	}

	private void findPath(ArrayList<File> files, File directory) {
		for (File childFile : directory.listFiles()) {
			if (childFile.isDirectory()) {
				findPath(files, childFile);
			}else{
				files.add(childFile);
			}
		}

	}

	public String toString() {
		return "Helper";
	}

}
