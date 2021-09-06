package cn.jeff.app.d2de;

import cn.jeff.app.d2de.data.DataRecord;
import cn.jeff.app.d2de.data.UnitIdAndName;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class MainFragmentJ {

	public MainFragment k;
	public Button btnOpen;
	public Button btnSave;
	public TextField tfUnitNameFilter;
	public ListView<UnitIdAndName> lvUnitName;
	public TableView<DataRecord.DataRecordItem> mainTableView;

	public void btnClick(ActionEvent actionEvent) {
		k.btnClick(actionEvent);
	}

}
