package cn.jeff.app.d2de.data

import tornadofx.*

class MainData(dbfDirectory: String) {

	private val globalTextDbf = DbfWrapper("$dbfDirectory/Tglobal.dbf")
	private val unitsDbf = DbfWrapper("$dbfDirectory/Gunits.dbf")
	val unitList = observableList<GameUnit>()

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
				GameUnit().also {
					it.unitId = unitId
					it.unitName = unitName
				}
			)
		}
	}

}
