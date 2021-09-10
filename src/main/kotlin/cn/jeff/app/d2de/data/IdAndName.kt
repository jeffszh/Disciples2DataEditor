package cn.jeff.app.d2de.data

open class IdAndName(val id: String, protected val name: String) {
	override fun toString() = "$id - $name"
}
