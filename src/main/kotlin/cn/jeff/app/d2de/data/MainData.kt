package cn.jeff.app.d2de.data

import cn.jeff.app.d2de.EditAttackWnd
import tornadofx.*
import java.lang.Exception

class MainData(dbfDirectory: String) {

	private val globalTextDbf = DbfWrapper("$dbfDirectory/Tglobal.dbf")
	private val unitsDbf = DbfWrapper("$dbfDirectory/Gunits.dbf")
	private val attackDbf = DbfWrapper("$dbfDirectory/Gattacks.dbf")
	private val raceDbf = DbfWrapper("$dbfDirectory/Grace.dbf")
	private val dynUpgradeDbf = DbfWrapper("$dbfDirectory/GDynUpgr.dbf")
	private val artifactsDbf = DbfWrapper("$dbfDirectory/GItem.DBF")
	private val modiDbf = DbfWrapper("$dbfDirectory/GmodifL.dbf")
	private val spellDbf = DbfWrapper("$dbfDirectory/Gspells.dbf")
	private val spellResearchDbf = DbfWrapper("$dbfDirectory/GSpellR.dbf")
	private val lordDbf = DbfWrapper("$dbfDirectory/Glord.dbf")
	private val buildingDbf = DbfWrapper("$dbfDirectory/GBuild.dbf")
	val unitList = createIndexList(unitsDbf, "UNIT_ID")
	val artifactsList = createIndexList(artifactsDbf, "ITEM_ID")
	val spellList = createIndexList(spellDbf, "SPELL_ID")
	val spellResearchList = createIndexList(
		spellResearchDbf, "LORD_ID | SPELL_ID",
		""
	)

	private fun createIndexList(
		dbf: DbfWrapper, idFieldName: String, nameFieldName: String = "NAME_TXT"
	) = (0 until dbf.recordCount).map { i ->
		// val id = dbf[i, idFieldName].toString()
		val idFieldList = idFieldName.split("|").map { it.trim() }
		val id = idFieldList.map {
			dbf[i, it]
		}.joinToString(" | ")
		if (nameFieldName.isBlank()) {
			IdAndName(id, "")
		} else {
			val nameTxtId = dbf[i, nameFieldName].toString()
			val name = globalTextDbf.findData("TXT_ID", nameTxtId, "TEXT")
				?.toString() ?: "没找到"
			IdAndName(id, name)
		}
	}.observable()

	private fun createDataRecord(
		dbf: DbfWrapper, key: String, keyValue: String,
		vararg lookupFields: String
	): DataRecord {
		val recNo = dbf.find(key, keyValue)
		val record = DataRecord(dbf, recNo)
		for (lookupField in lookupFields) {
			record.setExtraInfos(lookupField) { value ->
				try {
					if (value.isNotBlank()
						&& !value.equals("g000000000", true)
						&& value.length == 10
					) {
						val txtId = when (value.substring(4, 6)) {
							"uu" -> {
								unitsDbf.findData("UNIT_ID", value, "NAME_TXT")
									?: value
							}
							"rr" -> {
								raceDbf.findData("RACE_ID", value, "NAME_TXT")
									?: value
							}
							"aa" -> {
								attackDbf.findData("ATT_ID", value, "NAME_TXT")
									?: value
							}
							"ss" -> {
								spellDbf.findData("SPELL_ID", value, "NAME_TXT")
									?: value
							}
							"LR" -> {
								lordDbf.findData("LORD_ID", value, "NAME_TXT")
									?: value
							}
							"bb", "BB" -> {
								buildingDbf.findData("BUILD_ID", value, "NAME_TXT")
									?: value
							}
							else -> {
								value
							}
						}
						globalTextDbf.findData(
							"TXT_ID", txtId.toString(), "TEXT"
						)?.toString()
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

	private val attackClassList = listOf(
		"-",
		"一般",
		"吸血",
		"麻痺",
		"保留", "保留",
		"治療",
		"逼退",
		"增強攻擊力",
		"石化",
		"降低攻擊力",
		"降低敏捷",
		"放毒",
		"凍傷",
		"復活",
		"吸血治療",
		"恢復",
		"召喚",
		"降級",
		"再次行動",
		"模仿變形",
		"自我變形",
		"變形",
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
			"NAME_TXT", "DESC_TXT", "ABIL_TXT", "PREV_ID",
			"RACE_ID", "ATTACK_ID", "ATTACK2_ID", "BASE_UNIT", "UPGRADE_B"
		)

	fun createAttackRecord(attackId: String) =
		createDataRecord(
			attackDbf, "ATT_ID", attackId,
			"NAME_TXT", "DESC_TXT"
		).setEnumFields(
			"SOURCE" to attackSourceList,
			"REACH" to attackReachList,
			"CLASS" to attackClassList,
		).apply {
			setCustomAction("ALT_ATTACK") {
				EditAttackWnd(it).openWindow()
			}
		}

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

	fun createArtifactsRecord(itemId: String) =
		createDataRecord(
			artifactsDbf, "ITEM_ID", itemId,
			"NAME_TXT", "DESC_TXT", "SPELL_ID", "ATTACK_ID", "UNIT_ID"
		)

	fun createModiRecord(modiId: String) =
		createDataRecord(
			modiDbf, "BELONGS_TO", modiId,
			"DESC"
		)

	fun createSpellRecord(spellId: String) =
		createDataRecord(
			spellDbf, "SPELL_ID", spellId,
			"NAME_TXT", "DESC_TXT", "UNIT_ID", "MODIF_TXT"
		)

	fun createSpellResearchRecord(spellResearchId: String) =
		createDataRecord(
			spellResearchDbf, "LORD_ID | SPELL_ID", spellResearchId,
			"LORD_ID", "SPELL_ID"
		)

}
