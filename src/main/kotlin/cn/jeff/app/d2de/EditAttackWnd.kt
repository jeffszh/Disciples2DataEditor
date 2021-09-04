package cn.jeff.app.d2de

import tornadofx.*

class EditAttackWnd(attackId: String) :
	EditRecordWnd(
		"编辑攻击",
		find(MainWnd::class).mainData!!.createAttackRecord(attackId)
	)
