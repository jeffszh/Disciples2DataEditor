package cn.jeff.app.d2de.data

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class GameUnit {
	private val unitIdProp = SimpleStringProperty("")
	var unitId: String by unitIdProp
	private val unitNameProp = SimpleStringProperty("")
	var unitName: String by unitNameProp
//	val attack1Prop = SimpleStringProperty("")
//	var attack1 by attack1Prop
//	val attack2Prop = SimpleStringProperty("")
//	var attack2 by attack2Prop

	override fun toString() = "$unitId - $unitName"

}
