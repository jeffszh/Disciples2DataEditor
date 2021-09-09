package cn.jeff.app.d2de

import cn.jeff.app.d2de.data.DataRecord
import cn.jeff.app.d2de.data.IdAndName
import cn.jeff.app.d2de.data.MainData
import javafx.collections.ObservableList
import javafx.fxml.FXMLLoader
import javafx.scene.control.RadioButton
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
	private val tgText get() = (j.tgLookupType.selectedToggle as RadioButton).text
	var tfText: String
		get() = j.tfFilter.text
		set(value) {
			j.tfFilter.text = value
		}

	init {
		val loader = FXMLLoader()
		root = loader.load(
			javaClass.getResourceAsStream(
				"MainFragment.fxml"
			)
		)
		j = loader.getController()

		j.tgLookupType.selectedToggleProperty().onChange {
			if (tgText == "定位") {
				j.lvIndex.items = indexList
				j.lvIndex.scrollTo(j.lvIndex.selectedItem)
			} else {
				onLookupConditionChanged()
			}
		}
		j.tfFilter.textProperty().onChange {
			onLookupConditionChanged()
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

	private fun onLookupConditionChanged() {
		when (tgText) {
			"过滤" -> {
				if (tfText.isBlank()) {
					j.lvIndex.items = indexList
				} else {
					j.lvIndex.items = indexList.filtered {
						it.toString().contains(tfText, ignoreCase = true)
					}
				}
			}
			"定位" -> {
				j.lvIndex.items = indexList
				if (tfText.isNotBlank()) {
					val ind = j.lvIndex.items.indexOfFirst {
						it.toString().contains(tfText, ignoreCase = true)
					}
					if (ind >= 0) {
						j.lvIndex.selectionModel.select(ind)
						j.lvIndex.scrollTo(ind)
					}
				}
			}
		}
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
