package cn.jeff.app.d2de;

import cn.jeff.app.d2de.data.DataRecord;
import cn.jeff.app.d2de.data.IdAndName;
import javafx.scene.control.*;

public class MainFragmentJ {

	public Button btnSave;
	public TextField tfFilter;
	public ListView<IdAndName> lvIndex;
	public TableView<DataRecord.DataRecordItem> mainTableView;
	public ToggleGroup tgLookupType;

}
