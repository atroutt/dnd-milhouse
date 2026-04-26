package com.audreytroutt.milhouse.data.api

import com.audreytroutt.milhouse.data.model.Spell
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val lenientJson = Json { ignoreUnknownKeys = true }

class SpellImportService {

    private val client = HttpClient {
        install(ContentNegotiation) { json(lenientJson) }
    }

    suspend fun fetchAllSpells(
        characterId: Long,
        onProgress: (fetched: Int, total: Int) -> Unit
    ): List<Spell> = coroutineScope {
        val index = client.get("https://www.dnd5eapi.co/api/spells").body<SpellIndex>()
        val total = index.results.size
        val indices = index.results.map { it.index }

        val mutex = Mutex()
        var fetched = 0
        val semaphore = Semaphore(10)

        indices.map { spellIndex ->
            async {
                semaphore.withPermit {
                    val spell = fetchSpell(spellIndex, characterId)
                    mutex.withLock { onProgress(++fetched, total) }
                    spell
                }
            }
        }.mapNotNull { it.await() }
    }

    private suspend fun fetchSpell(index: String, characterId: Long): Spell? = try {
        val raw = client.get("https://www.dnd5eapi.co/api/spells/$index").body<RawSpell>()
        raw.toSpell(characterId)
    } catch (_: Exception) {
        null
    }
}

@Serializable
private data class SpellIndex(val results: List<SpellRef>)

@Serializable
private data class SpellRef(val index: String)

@Serializable
private data class RawSpell(
    val name: String,
    val level: Int,
    val school: SchoolRef? = null,
    @SerialName("casting_time") val castingTime: String = "",
    val range: String = "",
    val duration: String = "",
    val components: List<String> = emptyList(),
    val material: String = "",
    val desc: List<String> = emptyList(),
    @SerialName("higher_level") val higherLevel: List<String> = emptyList(),
    val classes: List<ClassRef> = emptyList(),
    val concentration: Boolean = false,
    val ritual: Boolean = false
) {
    fun toSpell(characterId: Long) = Spell(
        characterId = characterId,
        name = name,
        level = level,
        school = school?.name ?: "",
        castingTime = castingTime,
        range = range,
        duration = duration,
        components = components.joinToString(", "),
        materialComponents = material,
        description = desc.joinToString("\n\n"),
        higherLevels = higherLevel.joinToString("\n\n"),
        classes = classes.joinToString(", ") { it.name },
        isConcentration = concentration,
        isRitual = ritual
    )
}

@Serializable
private data class SchoolRef(val name: String)

@Serializable
private data class ClassRef(val name: String)
