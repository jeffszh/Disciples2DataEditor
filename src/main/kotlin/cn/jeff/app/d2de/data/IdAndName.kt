package cn.jeff.app.d2de.data

class IdAndName(val id: String, private val name: String) {
	override fun toString() =
		if (name == "") id else "$id - $name"
}
