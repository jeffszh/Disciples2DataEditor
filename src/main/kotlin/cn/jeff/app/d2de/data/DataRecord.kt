package cn.jeff.app.d2de.data

import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.ObservableList
import javafx.scene.control.TableCell
import javafx.scene.control.TableView
import tornadofx.*

class DataRecord(private val dbfWrapper: DbfWrapper, private val recNo: Int) {

	private val fieldNames: List<String>
	private val fieldValues: ObservableList<String>
	private val extraInfos: Array<String?>
	private val customActions: Array<(() -> Unit)?>
	private val dataRecordItem: List<DataRecordItem>
	val changedProperty = SimpleBooleanProperty(false)

	init {
		val fieldCount = dbfWrapper.fieldCount
		fieldNames = (0 until fieldCount).map { i ->
			dbfWrapper.fields[i].name
		}
		fieldValues = (0 until fieldCount).map { i ->
			dbfWrapper.records[recNo][i].toString()
		}.observable()
		extraInfos = arrayOfNulls(fieldCount)
		customActions = arrayOfNulls(fieldCount)

		dataRecordItem = (0 until fieldCount).map { i ->
			DataRecordItem(i)
		}
	}

	fun setExtraInfos(fieldName: String, value: String) {
		val fieldIndex = fieldNames.indexOf(fieldName)
		extraInfos[fieldIndex] = value
	}

	fun setCustomAction(fieldName: String, action: () -> Unit) {
		val fieldIndex = fieldNames.indexOf(fieldName)
		customActions[fieldIndex] = action
	}

	fun attachToTableView(tableView: TableView<DataRecordItem>) {
		with(tableView) {
			readonlyColumn("名称", DataRecordItem::fieldName)
			column("数值", DataRecordItem::fieldValue).makeEditable()
			readonlyColumn("详情", DataRecordItem::extraInfo)
			readonlyColumn("操作", DataRecordItem::customAction).setCellFactory {
				ButtonCell()
			}
			items = dataRecordItem.observable()
		}
	}

	inner class DataRecordItem(private val fieldIndex: Int) {
		val fieldName get() = fieldNames[fieldIndex]
		var fieldValue: String
			get() = fieldValues[fieldIndex]
			set(value) {
				fieldValues[fieldIndex] = value
				changedProperty.value = true
			}
		val extraInfo get() = extraInfos[fieldIndex]
		val customAction get() = customActions[fieldIndex]
	}

	private class ButtonCell : TableCell<DataRecordItem, (() -> Unit)?>() {
		private var currentIndex = -1

		override fun updateIndex(i: Int) {
			super.updateIndex(i)
			currentIndex = i
		}

		override fun updateItem(item: (() -> Unit)?, empty: Boolean) {
			super.updateItem(item, empty)
			graphic = if (empty || item == null) {
				null
			} else {
				button("打开详情") {
					action {
						item()
					}
				}
			}
		}
	}

}
