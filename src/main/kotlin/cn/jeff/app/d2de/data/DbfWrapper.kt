package cn.jeff.app.d2de.data

import cn.jeff.app.d2de.StaticVars
import com.linuxense.javadbf.DBFField
import com.linuxense.javadbf.DBFReader
import com.linuxense.javadbf.DBFWriter
import javafx.collections.ObservableList
import tornadofx.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.Charset

class DbfWrapper(private val dbfFilename: String) {

	val fields: List<DBFField>
	val records: ObservableList<Array<Any?>>
	private val defaultCharset = Charset.forName(StaticVars.appConfig.defaultCharset)

	init {
		FileInputStream(dbfFilename).use { fis ->
			DBFReader(fis, defaultCharset).use { reader ->
				fields = (0 until reader.fieldCount).map { i ->
					reader.getField(i)
				}
				records = (0 until reader.recordCount).map {
					reader.nextRecord() ?: arrayOfNulls(reader.fieldCount)
				}.observable()
			}
		}
	}

	private fun saveDbf() {
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

}
