package cn.jeff.app.d2de.data

class UnitIdAndName(val unitId: String, private val unitName: String) {
	override fun toString() = "$unitId - $unitName"
}
