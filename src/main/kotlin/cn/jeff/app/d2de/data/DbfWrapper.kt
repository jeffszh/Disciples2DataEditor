package cn.jeff.app.d2de.data

import cn.jeff.app.d2de.StaticVars
import com.linuxense.javadbf.DBFField
import com.linuxense.javadbf.DBFReader
import com.linuxense.javadbf.DBFWriter
import javafx.beans.property.SimpleBooleanProperty
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.Charset

class DbfWrapper(private val dbfFilename: String) {

	val fields: List<DBFField>
	val records: List<Array<Any?>>
	private val defaultCharset = Charset.forName(StaticVars.appConfig.defaultCharset)
	val changedProperty = SimpleBooleanProperty(false)

	init {
		FileInputStream(dbfFilename).use { fis ->
			DBFReader(fis, defaultCharset).use { reader ->
				fields = (0 until reader.fieldCount).map { i ->
					reader.getField(i)
				}
				records = (0 until reader.recordCount).map {
					reader.nextRecord() ?: arrayOfNulls(reader.fieldCount)
				}
			}
		}
	}

	fun saveDbf() {
		println("写入......")
		DBFWriter(FileOutputStream(dbfFilename), defaultCharset).use { writer ->
			writer.setFields(fields.toTypedArray())
			records.forEach { record ->
				if (record.all { it == null }) {
					// writer.addRecord(null)
				} else {
					writer.addRecord(record)
				}
//				writer.addRecord(record)
			}
		}
		println("写入完毕。")
	}

	operator fun get(recNo: Int, fieldName: String): Any? {
		val fieldIndex = fields.indexOfFirst {
			it.name == fieldName
		}
		return records[recNo][fieldIndex]
	}

	operator fun set(recNo: Int, fieldName: String, value: Any?) {
		val fieldIndex = fields.indexOfFirst {
			it.name == fieldName
		}
		records[recNo][fieldIndex] = value
	}

	val recordCount get() = records.count()
	val fieldCount get() = fields.count()

	fun find(fieldName: String, fieldValue: Any?): Int {
		val fieldIndex = fields.indexOfFirst {
			it.name == fieldName
		}
		return records.indexOfFirst {
			it[fieldIndex].toString() == fieldValue.toString()
		}
	}

	fun findData(fieldName: String, fieldValue: Any?, dataName: String): Any? {
		val recNo = find(fieldName, fieldValue)
		return if (recNo >= 0) {
			this[recNo, dataName]
		} else {
			null
		}
	}

}
