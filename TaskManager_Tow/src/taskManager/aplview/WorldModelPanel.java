package taskManager.aplview;

import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import taskManager.logger.argument.RelationArgument;
import uos.ai.jam.WorldModelChangeListener;
import uos.ai.jam.expression.Relation;

public class WorldModelPanel extends JPanel implements WorldModelChangeListener{
	private DefaultTableModel worldModelList;
	
	private JScrollPane worldModelPane;
	
	private JTable worldModelTable;
	
	public WorldModelPanel(){
		worldModelList = new DefaultTableModel();
		worldModelTable = new JTable();
		worldModelPane = new JScrollPane(worldModelTable);
		
		worldModelList.addColumn("worldModel");
		worldModelTable.setModel(worldModelList);
		this.setLayout(null);
		worldModelPane.setBounds(0,0,470,470);
		this.add(worldModelPane);
	}
	
	
	@Override
	public void worldModelChanged(Relation[] retracted, Relation asserted) {
		
		if (asserted != null) {
			if (!asserted.getName().equals("CURRENT_TIME") && !asserted.getName().equals("APL")) {
				System.out.println("asserted ");
				Object[] msg = new Object[1];
				msg[0] = asserted.toString();
				worldModelList.addRow(msg);
			
			}
		}
		if (retracted != null) {
			for (int i = 0; i < retracted.length; i++) {
				if (!retracted[i].getName().equals("CURRENT_TIME")){
					removeRow(retracted[i]);
									}
			}
		}
		
	}


	private void removeRow(Relation relation) {
		for(int i = 0; i < worldModelList.getRowCount();i++){
			String data = (String) worldModelList.getValueAt(i, 0);
			if(data.equals(relation.toString())){
				worldModelList.removeRow(i);
			}
		}
		
	}

}
