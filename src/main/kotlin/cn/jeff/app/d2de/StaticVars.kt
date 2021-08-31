package cn.jeff.app.d2de

import com.google.gson.GsonBuilder
import java.io.FileReader
import java.io.FileWriter

object StaticVars {

	private const val CONFIG_FILE = "AppConf.json"
	private val gson = GsonBuilder().setPrettyPrinting().create()

	var appConfig = AppConfig()

	fun loadAppConfig() {
		try {
			FileReader(CONFIG_FILE).use { reader ->
				appConfig = gson.fromJson(reader, AppConfig::class.java)
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	fun saveAppConfig() {
		FileWriter(CONFIG_FILE).use { writer ->
			gson.toJson(appConfig, writer)
		}
	}

}
