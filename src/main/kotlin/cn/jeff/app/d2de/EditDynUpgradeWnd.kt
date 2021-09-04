package cn.jeff.app.d2de

import tornadofx.*

class EditDynUpgradeWnd(dynUpgradeId: String) : EditRecordWnd(
	"編輯升級設定",
	find<MainWnd>().mainData!!.createDynUpgradeRecord(dynUpgradeId)
)
