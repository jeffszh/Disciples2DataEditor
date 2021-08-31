package cn.jeff.app.d2de

import cn.jeff.app.d2de.data.MainData
import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane
import tornadofx.*
import java.io.File

class MainWnd : View("圣战群英传2数据编辑器") {

	override val root: BorderPane
	private val j: MainWndJ
	private var mainData: MainData? = null

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

}
