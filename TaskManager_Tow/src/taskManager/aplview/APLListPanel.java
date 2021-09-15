package taskManager.aplview;

import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import uos.ai.jam.APLChangeListener;
import uos.ai.jam.plan.APL;
import uos.ai.jam.plan.APLElement;

public class APLListPanel extends JPanel implements APLChangeListener{
	private DefaultTableModel aplList;
	private JScrollPane aplPane;
	private JTable aplTable;
	
	
	public APLListPanel(){
		aplList = new DefaultTableModel();
		aplTable = new JTable();
		aplPane = new JScrollPane(aplTable);
		
		this.setLayout(null);
		aplTable.setModel(aplList);
		aplList.addColumn("APL");
		this.add(aplPane);
		aplPane.setBounds(0,0,480,440);

	}




	@Override
	public void aplChanged(APL apl) {
		/*
		while(aplList.getRowCount() != 0){
			aplList.removeRow(0);
		}
		
		for(int i = 0; i < apl.getSize();i++){
			Object[] msg = new Object[1];
			msg[0] = apl.get(i).getFromGoal().getName();
			aplList.addRow(msg);
		}
		*/	
	}
	
}
