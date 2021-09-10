package cn.jeff.app.d2de.data

class CompoundIdAndName(vararg ids: String, name: String) : IdAndName(
	ids.joinToString(" | "), name
) {
	override fun toString() =
		if (name == "") id else super.toString()
}
