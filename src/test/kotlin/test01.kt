import javafx.beans.property.SimpleObjectProperty
import tornadofx.*

fun main() {
	val s1 = SimpleObjectProperty(3 to "three")
	val sb = s1.stringBinding { "--- $it ---" }
	println(sb)
	sb.onChange {
		println(it)
	}
	s1.value = 5 to "five"
	s1.value = 6 to "six"
}
