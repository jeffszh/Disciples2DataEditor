package cn.jeff.app.d2de

import cn.jeff.app.d2de.data.DataRecord
import cn.jeff.app.d2de.data.IdAndName
import cn.jeff.app.d2de.data.MainData
import javafx.collections.ObservableList
import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane
import tornadofx.*

class MainFragment(
	private val indexList: ObservableList<IdAndName>,
	private val createRecordFunc: MainData.(dataId: String) -> DataRecord,
	private val extraSettingsFunc: DataRecord.() -> Unit
) : Fragment() {

	override val root: BorderPane
	private val j: MainFragmentJ
	private val mainWnd: MainWnd by inject()

	init {
		val loader = FXMLLoader()
		root = loader.load(
			javaClass.getResourceAsStream(
				"MainFragment.fxml"
			)
		)
		j = loader.getController()

		j.tfFilter.textProperty().addListener { _, _, new ->
			if (new.isBlank()) {
				j.lvIndex.items = indexList
			} else {
				j.lvIndex.items = indexList.filtered {
					it.toString().contains(new, ignoreCase = true)
				}
			}
		}

		j.lvIndex.selectionModel.selectedItemProperty().addListener { _, _, new ->
			reloadMainTableView(new)
		}

		mainWnd.mainDataProperty.onChange {
			onMainDataChanged()
		}

		// 创建时主动更新一次
		onMainDataChanged()
	}

	private fun onMainDataChanged() {
		j.lvIndex.items = indexList
	}

	private fun reloadMainTableView(idAndName: IdAndName?) {
		j.mainTableView.columns.clear()
		j.mainTableView.items = null
		idAndName ?: return
		val mainData = mainWnd.mainData
		// mainData ?: return
		val unitRecord = mainData.createRecordFunc(idAndName.id)
		unitRecord.extraSettingsFunc()
		unitRecord.attachToTableView(j.mainTableView)
		j.btnSave.enableWhen(unitRecord.changedProperty)
		j.btnSave.action {
			unitRecord.saveDbf()
			unitRecord.changedProperty.value = false
		}
	}

}
