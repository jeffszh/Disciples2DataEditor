package cn.jeff.app.d2de

import tornadofx.*

class EditRaceWnd(raceId: String) : EditRecordWnd(
	"編輯種族",
	find<MainFragment>().mainData!!.createRaceRecord(raceId)
)
