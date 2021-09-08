package cn.jeff.app.d2de

import tornadofx.*

class EditModiWnd(modiId: String) : EditRecordWnd(
	"编辑增减益效果",
	find<MainWnd>().mainData.createModiRecord(modiId)
)
