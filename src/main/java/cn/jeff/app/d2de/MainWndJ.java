package cn.jeff.app.d2de;

import cn.jeff.app.d2de.data.GameUnit;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MainWndJ {

	public MainWnd k;
	public Button btnOpen;
	public TextField tfUnitNameFilter;
	public ListView<GameUnit> lvUnitName;

	public void btnClick(ActionEvent actionEvent) {
		k.btnClick(actionEvent);
	}

}
