package cn.jeff.app

import cn.jeff.app.d2de.MainWnd
import cn.jeff.app.d2de.StaticVars
import tornadofx.*

class Disciples2DataEditor : App(MainWnd::class) {
	init {
		StaticVars.loadAppConfig()
	}
}
