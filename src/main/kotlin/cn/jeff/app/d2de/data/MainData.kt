package cn.jeff.app.d2de.data

import tornadofx.*
import java.lang.Exception

class MainData(dbfDirectory: String) {

	private val globalTextDbf = DbfWrapper("$dbfDirectory/Tglobal.dbf")
	private val unitsDbf = DbfWrapper("$dbfDirectory/Gunits.dbf")
	private val attackDbf = DbfWrapper("$dbfDirectory/Gattacks.dbf")
	private val raceDbf = DbfWrapper("$dbfDirectory/Grace.dbf")
	private val dynUpgradeDbf = DbfWrapper("$dbfDirectory/GDynUpgr.dbf")
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
		vararg lookupFields: String
	): DataRecord {
		val recNo = dbf.find(key, keyValue)
		val record = DataRecord(dbf, recNo)
		for (lookupField in lookupFields) {
			record.setExtraInfos(lookupField) { value ->
				try {
					if (value.isNotBlank() && value != "g000000000" && value.length == 10) {
						val txtId = if (value.substring(4, 6) == "uu") {
							val unitRecNo = unitsDbf.find("UNIT_ID", value)
							if (unitRecNo >= 0) unitsDbf[unitRecNo, "NAME_TXT"] else value
						} else {
							value
						}
						val txtNo = globalTextDbf.find("TXT_ID", txtId)
						// println("$txtId ----------------> $txtNo")
						globalTextDbf[txtNo, "TEXT"].toString()
					} else {
						null
					}
				} catch (e: Exception) {
					e.printStackTrace()
					"異常：${e.message}"
				}
			}
		}
		return record
	}

	private val attackSourceList = listOf(
		"武器", "意念", "生命", "死系", "火系", "水系", "地系", "氣系"
	)

	private val attackReachList = listOf(
		"-", "全體", "遠程", "近戰"
	)

	private fun DataRecord.setEnumFields(vararg enumDef: Pair<String, List<String>>): DataRecord {
		enumDef.forEach { (fieldName, enumTextList) ->
			setExtraInfos(fieldName) { value ->
				val intValue = value.toDoubleOrNull()?.toInt()
				intValue?.let {
					if (intValue in enumTextList.indices) {
						enumTextList[intValue]
					} else {
						null
					}
				}
			}
		}
		return this
	}

	fun createUnitRecord(unitId: String) =
		createDataRecord(
			unitsDbf, "UNIT_ID", unitId,
			"NAME_TXT", "DESC_TXT", "ABIL_TXT", "PREV_ID"
		)

	fun createAttackRecord(attackId: String) =
		createDataRecord(
			attackDbf, "ATT_ID", attackId,
			"NAME_TXT", "DESC_TXT"
		).setEnumFields(
			"SOURCE" to attackSourceList,
			"REACH" to attackReachList,
		)

	fun createRaceRecord(raceId: String) =
		createDataRecord(
			raceDbf, "RACE_ID", raceId,
			"NAME_TXT",
			"GUARDIAN", "NOBLE",
			"LEADER_1", "LEADER_2", "LEADER_3", "LEADER_4",
			"SOLDIER_1", "SOLDIER_2", "SOLDIER_3", "SOLDIER_4", "SOLDIER_5",
		)

	fun createDynUpgradeRecord(dynUpgradeId: String) =
		createDataRecord(dynUpgradeDbf, "UPGRADE_ID", dynUpgradeId)

}
