package cn.jeff.app.d2de.data

import tornadofx.*

class MainData(dbfDirectory: String) {

	private val globalTextDbf = DbfWrapper("$dbfDirectory/Tglobal.dbf")
	private val unitsDbf = DbfWrapper("$dbfDirectory/Gunits.dbf")
	private val attackDbf = DbfWrapper("$dbfDirectory/Gattacks.dbf")
	val unitList = observableList<UnitIdAndName>()

	init {
		for (i in 0 until unitsDbf.recordCount) {
			val unitId = unitsDbf[i, "UNIT_ID"].toString()
			val nameTxtId = unitsDbf[i, "NAME_TXT"].toString()
			val nameIndex = globalTextDbf.find("TXT_ID", nameTxtId)
			val unitName = if (nameIndex < 0)
				"没找到！"
			else
				globalTextDbf[nameIndex, "TEXT"].toString()
			unitList.add(
				UnitIdAndName(unitId, unitName)
			)
		}
	}

	private fun createDataRecord(
		dbf: DbfWrapper, key: String, keyValue: String,
		lookupFields: List<String>
	): DataRecord {
		val recNo = dbf.find(key, keyValue)
		val record = DataRecord(dbf, recNo)
		for (lookupField in lookupFields) {
			val txtNo = globalTextDbf.find("TXT_ID", dbf[recNo, lookupField])
			record.setExtraInfos(lookupField, globalTextDbf[txtNo, "TEXT"].toString())
		}
		return record
	}

	fun createUnitRecord(unitId: String): DataRecord =
		createDataRecord(
			unitsDbf, "UNIT_ID", unitId, listOf(
				"NAME_TXT", "DESC_TXT", "ABIL_TXT"
			)
		)

	fun createAttackRecord(attackId: String): DataRecord =
		createDataRecord(
			attackDbf, "ATT_ID", attackId, listOf(
				"NAME_TXT", "DESC_TXT"
			)
		)

}
