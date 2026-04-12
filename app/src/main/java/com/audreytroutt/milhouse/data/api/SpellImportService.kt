package com.audreytroutt.milhouse.data.api

import com.audreytroutt.milhouse.data.model.Spell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger

class SpellImportService {

    suspend fun fetchAllSpells(
        characterId: Long,
        onProgress: (fetched: Int, total: Int) -> Unit
    ): List<Spell> = withContext(Dispatchers.IO) {
        val indexJson = JSONObject(get("https://www.dnd5eapi.co/api/spells"))
        val results = indexJson.getJSONArray("results")
        val total = results.length()
        val indices = (0 until total).map { results.getJSONObject(it).getString("index") }

        val fetched = AtomicInteger(0)
        val semaphore = Semaphore(10)

        coroutineScope {
            indices.map { index ->
                async {
                    semaphore.withPermit {
                        val spell = fetchSpell(index, characterId)
                        onProgress(fetched.incrementAndGet(), total)
                        spell
                    }
                }
            }.awaitAll().filterNotNull()
        }
    }

    private fun fetchSpell(index: String, characterId: Long): Spell? = try {
        parseSpell(JSONObject(get("https://www.dnd5eapi.co/api/spells/$index")), characterId)
    } catch (_: Exception) {
        null
    }

    private fun get(url: String): String {
        val conn = URL(url).openConnection() as HttpURLConnection
        return try {
            conn.connectTimeout = 15_000
            conn.readTimeout = 15_000
            conn.requestMethod = "GET"
            conn.setRequestProperty("Accept", "application/json")
            conn.inputStream.bufferedReader().readText()
        } finally {
            conn.disconnect()
        }
    }

    private fun parseSpell(json: JSONObject, characterId: Long): Spell {
        fun jsonArrayToString(key: String) =
            json.optJSONArray(key)?.let { arr ->
                (0 until arr.length()).joinToString("\n\n") { arr.getString(it) }
            } ?: ""

        val components = json.optJSONArray("components")?.let { arr ->
            (0 until arr.length()).map { arr.getString(it) }.joinToString(", ")
        } ?: ""

        val classes = json.optJSONArray("classes")?.let { arr ->
            (0 until arr.length()).map { arr.getJSONObject(it).getString("name") }.joinToString(", ")
        } ?: ""

        return Spell(
            characterId = characterId,
            name = json.getString("name"),
            level = json.getInt("level"),
            school = json.optJSONObject("school")?.optString("name", "") ?: "",
            castingTime = json.optString("casting_time", ""),
            range = json.optString("range", ""),
            duration = json.optString("duration", ""),
            components = components,
            materialComponents = json.optString("material", ""),
            description = jsonArrayToString("desc"),
            higherLevels = jsonArrayToString("higher_level"),
            classes = classes,
            isConcentration = json.optBoolean("concentration", false),
            isRitual = json.optBoolean("ritual", false)
        )
    }
}
