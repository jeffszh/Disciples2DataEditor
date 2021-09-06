package cn.jeff.app

import cn.jeff.app.d2de.MainFragment
import cn.jeff.app.d2de.StaticVars
import tornadofx.*

class Disciples2DataEditor : App(MainFragment::class) {
	init {
		StaticVars.loadAppConfig()
	}
}
