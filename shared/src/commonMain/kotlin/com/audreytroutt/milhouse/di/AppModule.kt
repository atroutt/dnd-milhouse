package com.audreytroutt.milhouse.di

import com.audreytroutt.milhouse.data.api.SpellImportService
import com.audreytroutt.milhouse.data.db.DatabaseDriverFactory
import com.audreytroutt.milhouse.data.repository.AbilityRepository
import com.audreytroutt.milhouse.data.repository.ActionRepository
import com.audreytroutt.milhouse.data.repository.CharacterRepository
import com.audreytroutt.milhouse.data.repository.NoteRepository
import com.audreytroutt.milhouse.data.repository.SpellRepository
import com.audreytroutt.milhouse.db.MilhouseDatabase
import com.audreytroutt.milhouse.viewmodel.AbilityViewModel
import com.audreytroutt.milhouse.viewmodel.ActionViewModel
import com.audreytroutt.milhouse.viewmodel.CharacterViewModel
import com.audreytroutt.milhouse.viewmodel.NoteViewModel
import com.audreytroutt.milhouse.viewmodel.SpellViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val appModule = module {
    single<MilhouseDatabase> { MilhouseDatabase(get<DatabaseDriverFactory>().createDriver()) }

    single { CharacterRepository(get()) }
    single { SpellRepository(get()) }
    single { AbilityRepository(get()) }
    single { ActionRepository(get()) }
    single { NoteRepository(get()) }

    single { SpellImportService() }

    viewModel {
        CharacterViewModel(get(), get(), get(), get(), get())
    }
    viewModel { (characterId: Long) -> SpellViewModel(get(), characterId) }
    viewModel { (characterId: Long) -> AbilityViewModel(get(), characterId) }
    viewModel { (characterId: Long) -> ActionViewModel(get(), characterId) }
    viewModel { (characterId: Long) -> NoteViewModel(get(), characterId) }
}
