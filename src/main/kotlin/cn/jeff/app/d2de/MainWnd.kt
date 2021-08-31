package cn.jeff.app.d2de

import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane
import tornadofx.*

class MainWnd : View("圣战群英传2数据编辑器") {

	override val root: BorderPane
	private val j: MainWndJ

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
	}

	fun btnClick(actionEvent: ActionEvent) {
		when (actionEvent.source) {
			j.btnOpen -> {
				information("很好！")
			}
		}
	}

}
