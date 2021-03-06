package cn.jeff.app.d2de.data

import com.linuxense.javadbf.DBFDataType
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.scene.control.TableCell
import javafx.scene.control.TableView
import tornadofx.*
import java.io.IOException

class DataRecord(private val dbfWrapper: DbfWrapper, private val recNo: Int) {

	private val fieldNames: List<String>
	private val fieldValues: FieldValueAccessor
	private val extraInfos: Array<((String) -> String?)?>
	private val customActions: Array<((String) -> Unit)?>
	private val dataRecordItem: List<DataRecordItem>
	val changedProperty get() = dbfWrapper.changedProperty
	private var tableView: TableView<DataRecordItem>? = null

	init {
		val fieldCount = dbfWrapper.fieldCount
		fieldNames = (0 until fieldCount).map { i ->
			dbfWrapper.fields[i].name
		}
		fieldValues = FieldValueAccessor(
			getter = { fieldInd ->
				dbfWrapper.records[recNo][fieldInd].toString()
			},
			setter = { fieldInd, textValue ->
				val anyValue = when (val txtVal = textValue.trim()) {
					"", null.toString() -> {
						null
					}
					else -> {
						when (val fieldType = dbfWrapper.fields[fieldInd].type) {
							DBFDataType.CHARACTER, DBFDataType.VARCHAR -> {
								txtVal
							}
							DBFDataType.NUMERIC,
							DBFDataType.FLOATING_POINT,
							DBFDataType.DOUBLE,
							DBFDataType.CURRENCY -> {
								txtVal.toDouble()
							}
							DBFDataType.LONG -> {
								txtVal.toInt()
							}
							DBFDataType.LOGICAL -> {
								txtVal.toBoolean()
							}
							else -> {
								throw IOException("不支持修改的数据类型：$fieldType")
							}
						}
					}
				}

				dbfWrapper.records[recNo][fieldInd] = anyValue
			}
		)
		extraInfos = arrayOfNulls(fieldCount)
		customActions = arrayOfNulls(fieldCount)

		dataRecordItem = (0 until fieldCount).map { i ->
			DataRecordItem(i)
		}
	}

	private class FieldValueAccessor(
		private val getter: (fieldInd: Int) -> String,
		private val setter: (fieldInd: Int, fieldValue: String) -> Unit
	) {
		operator fun get(fieldInd: Int) = getter(fieldInd)
		operator fun set(fieldInd: Int, fieldValue: String) = setter(fieldInd, fieldValue)
	}

	fun setExtraInfos(fieldName: String, extraInfoProvider: (String) -> String?) {
		val fieldIndex = fieldNames.indexOf(fieldName)
		extraInfos[fieldIndex] = extraInfoProvider
	}

	fun setCustomAction(fieldName: String, action: (fieldTextValue: String) -> Unit) {
		val fieldIndex = fieldNames.indexOf(fieldName)
		val fieldValue = fieldValues[fieldIndex]
		if (fieldValue.isNotBlank() && !fieldValue.equals("g000000000", true)) {
			customActions[fieldIndex] = action
		}
	}

	fun saveDbf() {
		dbfWrapper.saveDbf()
	}

	fun attachToTableView(tableView: TableView<DataRecordItem>) {
		this.tableView = tableView
		with(tableView) {
			readonlyColumn("名称", DataRecordItem::fieldName)
			column("数值", DataRecordItem::fieldValue).makeEditable()
			column<DataRecordItem, String>("詳情") {
				ReadOnlyObjectWrapper(it.value.extraInfo?.invoke(it.value.fieldValue))
			}.apply {
				prefWidth = 500.0
				maxWidth = 1500.0
				enableTextWrap()
			}
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
				tableView?.refresh()
			}
		val extraInfo get() = extraInfos[fieldIndex]
		val customAction get() = customActions[fieldIndex]
	}

	private inner class ButtonCell : TableCell<DataRecordItem, ((String) -> Unit)?>() {
		private var currentIndex = -1

		override fun updateIndex(i: Int) {
			super.updateIndex(i)
			currentIndex = i
		}

		override fun updateItem(item: ((String) -> Unit)?, empty: Boolean) {
			super.updateItem(item, empty)
			graphic = if (empty || item == null) {
				null
			} else {
				button("打开详情") {
					action {
						item(fieldValues[currentIndex])
					}
				}
			}
		}
	}

}
