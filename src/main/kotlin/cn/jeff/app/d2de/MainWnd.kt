package cn.jeff.app.d2de

import cn.jeff.app.d2de.data.MainData
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane
import tornadofx.*
import java.io.File

class MainWnd : View("圣战群英传2数据编辑器") {

	override val root: BorderPane
	private val j: MainWndJ
	val mainDataProperty = SimpleObjectProperty<MainData>()
	var mainData: MainData by mainDataProperty
		private set
//	private var unitFragment: MainFragment by singleAssign()

	init {
		primaryStage.isResizable = true

		val loader = FXMLLoader()
		root = loader.load(
			javaClass.getResourceAsStream(
				"MainWnd.fxml"
			)
		)
		j = loader.getController()
		j.k = this
	}

	fun onBtnOpenClick() {
		chooseDirectory(
			"请选择DBF文件目录",
			File(StaticVars.appConfig.defaultDirectory)
		)?.also {
			StaticVars.appConfig.defaultDirectory = it.absolutePath
			StaticVars.saveAppConfig()
			mainData = MainData(StaticVars.appConfig.defaultDirectory)
			if (j.mainTabPane.tabs.isEmpty()) {
				createTabs()
			}
		}
	}

	private fun createTabs() {
		val unitFragment = MainFragment(mainData.unitList, MainData::createUnitRecord) {
			setCustomAction("ATTACK_ID") {
				EditAttackWnd(it).openWindow()
			}
			setCustomAction("ATTACK2_ID") {
				EditAttackWnd(it).openWindow()
			}
			setCustomAction("RACE_ID") {
				EditRaceWnd(it).openWindow()
			}
			setCustomAction("DYN_UPG1") {
				EditDynUpgradeWnd(it).openWindow()
			}
			setCustomAction("DYN_UPG2") {
				EditDynUpgradeWnd(it).openWindow()
			}
		}
		val unitTab = j.mainTabPane.tab(unitFragment)
		unitTab.text = "兵种（unit）"

		val spellFragment = MainFragment(mainData.spellList, MainData::createSpellRecord) {
			setCustomAction("MODIF_ID") {
				EditModiWnd(it).openWindow()
			}
			setCustomAction("UNIT_ID") {
				j.mainTabPane.selectionModel.select(unitTab)
				unitFragment.tfText = it
			}
		}
		val spellTab = j.mainTabPane.tab(spellFragment)
		spellTab.text = "魔法（spell）"

		j.mainTabPane.tab(
			MainFragment(mainData.artifactsList, MainData::createArtifactsRecord) {
				setCustomAction("ATTACK_ID") {
					EditAttackWnd(it).openWindow()
				}
				setCustomAction("MOD_EQUIP") {
					EditModiWnd(it).openWindow()
				}
				setCustomAction("MOD_POTION") {
					EditModiWnd(it).openWindow()
				}
				setCustomAction("UNIT_ID") {
					j.mainTabPane.selectionModel.select(unitTab)
					unitFragment.tfText = it
				}
				setCustomAction("SPELL_ID") {
					j.mainTabPane.selectionModel.select(spellTab)
					spellFragment.tfText = it
				}
			}
		).text = "物品（item）"
	}

}
