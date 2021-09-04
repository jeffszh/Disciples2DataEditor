package cn.jeff.app.d2de

import cn.jeff.app.d2de.data.MainData
import cn.jeff.app.d2de.data.UnitIdAndName
import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane
import tornadofx.*
import java.io.File

class MainWnd : View("圣战群英传2数据编辑器") {

	override val root: BorderPane
	private val j: MainWndJ
	var mainData: MainData? = null
		private set

	init {
		primaryStage.isResizable = true

		val loader = FXMLLoader()
		root = loader.load(
			javaClass.getResourceAsStream(
				"MainWnd.fxml"
			)
		)
		j = loader.getController()
		j.k = this

		j.tfUnitNameFilter.textProperty().addListener { _, _, new ->
			mainData?.also { mainData ->
				if (new.isBlank()) {
					j.lvUnitName.items = mainData.unitList
				} else {
					j.lvUnitName.items = mainData.unitList.filtered {
						it.toString().contains(new, ignoreCase = true)
					}
				}
			}
		}

		j.lvUnitName.selectionModel.selectedItemProperty().addListener { _, _, new ->
			reloadMainTableView(new)
		}
	}

	fun btnClick(actionEvent: ActionEvent) {
		when (actionEvent.source) {
			j.btnOpen -> {
				chooseDirectory(
					"请选择DBF文件目录",
					File(StaticVars.appConfig.defaultDirectory)
				)?.also {
					StaticVars.appConfig.defaultDirectory = it.absolutePath
					StaticVars.saveAppConfig()
					loadData()
				}
			}
		}
	}

	private fun loadData() {
		mainData = MainData(StaticVars.appConfig.defaultDirectory).also {
			j.lvUnitName.items = it.unitList
		}
	}

	private fun reloadMainTableView(unitIdAndName: UnitIdAndName?) {
		j.mainTableView.columns.clear()
		j.mainTableView.items = null
		unitIdAndName ?: return
		val mainData = mainData
		mainData ?: return
		val unitRecord = mainData.createUnitRecord(unitIdAndName.unitId)
		unitRecord.setCustomAction("ATTACK_ID") {
			EditAttackWnd(it).openWindow()
		}
		unitRecord.setCustomAction("ATTACK2_ID") {
			EditAttackWnd(it).openWindow()
		}
		unitRecord.setCustomAction("RACE_ID") {
			EditRaceWnd(it).openWindow()
		}
		unitRecord.setCustomAction("DYN_UPG1") {
			EditDynUpgradeWnd(it).openWindow()
		}
		unitRecord.setCustomAction("DYN_UPG2") {
			EditDynUpgradeWnd(it).openWindow()
		}
		unitRecord.attachToTableView(j.mainTableView)
		j.btnSave.enableWhen(unitRecord.changedProperty)
		j.btnSave.action {
			unitRecord.saveDbf()
			unitRecord.changedProperty.value = false
		}
	}

}
