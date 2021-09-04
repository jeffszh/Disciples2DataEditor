package cn.jeff.app.d2de

import cn.jeff.app.d2de.data.DataRecord
import javafx.scene.layout.Priority
import tornadofx.*

open class EditRecordWnd(title: String, private val dataRecord: DataRecord) : View(title) {

	override val root = vbox {
		style = "-fx-font-family: 'Courier New'; -fx-font-size: 16;"
		prefWidth = 780.0
		prefHeight = 580.0
		spacing = 10.0
		paddingAll = 10.0

		button("保存修改") {
			enableWhen {
				dataRecord.changedProperty
			}
			action {
				dataRecord.saveDbf()
				dataRecord.changedProperty.value = false
			}
		}
		tableview<DataRecord.DataRecordItem> {
			vgrow = Priority.ALWAYS
			dataRecord.attachToTableView(this)
		}
	}

	override fun onUndock() {
		super.onUndock()
		println("undock!")
	}

	override fun onDelete() {
		super.onDelete()
		println("delete!")
	}

}
