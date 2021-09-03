package cn.jeff.app.d2de

class EditAttackWnd(attackId: String) : EditRecordWnd("编辑攻击") {

	private val mainWnd: MainWnd by inject()

	override val dataRecord = mainWnd.mainData!!.createAttackRecord(attackId)

	init {
		continueInit()
	}

}
