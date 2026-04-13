package com.audreytroutt.milhouse.data.model

data class ClassFeature(val name: String, val description: String)

val CLASS_FEATURES: Map<String, List<ClassFeature>> = mapOf(
    "Barbarian" to listOf(
        ClassFeature("Rage", "In battle, you fight with primal ferocity. On your turn, you can enter a rage as a bonus action."),
        ClassFeature("Unarmored Defense", "While you are not wearing any armor, your Armor Class equals 10 + your Dexterity modifier + your Constitution modifier."),
        ClassFeature("Reckless Attack", "When you make your first attack on your turn, you can decide to attack recklessly, giving you advantage on melee weapon attack rolls using Strength during this turn."),
        ClassFeature("Danger Sense", "You have advantage on Dexterity saving throws against effects that you can see, such as traps and spells."),
        ClassFeature("Primal Path", "At 3rd level, you choose a path that shapes the nature of your rage, such as the Path of the Berserker."),
    ),
    "Bard" to listOf(
        ClassFeature("Spellcasting", "You have learned to untangle and reshape the fabric of reality in harmony with your wishes and music."),
        ClassFeature("Bardic Inspiration", "You can inspire others through stirring words or music. You use a bonus action to give one creature within 60 feet an Inspiration die."),
        ClassFeature("Jack of All Trades", "You can add half your proficiency bonus, rounded down, to any ability check you make that doesn't already include your proficiency bonus."),
        ClassFeature("Song of Rest", "You can use soothing music or oration to help revitalize your wounded allies during a short rest."),
        ClassFeature("Expertise", "Choose two of your skill proficiencies. Your proficiency bonus is doubled for any ability check you make that uses either of the chosen proficiencies."),
    ),
    "Cleric" to listOf(
        ClassFeature("Spellcasting", "As a conduit for divine power, you can cast cleric spells."),
        ClassFeature("Divine Domain", "Choose one domain related to your deity, such as Life, which grants you domain spells and other features."),
        ClassFeature("Channel Divinity", "You gain the ability to channel divine energy directly from your deity, using that energy to fuel magical effects."),
        ClassFeature("Destroy Undead", "When an undead fails its saving throw against your Turn Undead feature, the creature is instantly destroyed if its challenge rating is at or below a certain threshold."),
    ),
    "Druid" to listOf(
        ClassFeature("Druidic", "You know Druidic, the secret language of druids. You can speak the language and use it to leave hidden messages."),
        ClassFeature("Spellcasting", "Drawing on the divine essence of nature itself, you can cast spells to shape that essence to your will."),
        ClassFeature("Wild Shape", "You can use your action to magically assume the shape of a beast that you have seen before."),
        ClassFeature("Druid Circle", "At 2nd level, you choose to identify with a circle of druids, such as the Circle of the Land."),
    ),
    "Fighter" to listOf(
        ClassFeature("Fighting Style", "You adopt a particular style of fighting as your specialty, such as Archery, Defense, or Dueling."),
        ClassFeature("Second Wind", "You have a limited well of stamina that you can draw on to protect yourself from harm. You can use a bonus action to regain hit points."),
        ClassFeature("Action Surge", "You can push yourself beyond your normal limits for a moment. On your turn, you can take one additional action on top of your regular action."),
        ClassFeature("Martial Archetype", "At 3rd level, you choose an archetype that you strive to emulate in your combat styles and techniques."),
    ),
    "Monk" to listOf(
        ClassFeature("Unarmored Defense", "Your AC equals 10 + your Dexterity modifier + your Wisdom modifier while you are wearing no armor and not wielding a shield."),
        ClassFeature("Martial Arts", "Your practice of martial arts gives you mastery of combat styles that use unarmed strikes and monk weapons."),
        ClassFeature("Ki", "Your training allows you to harness the mystic energy of ki to exceed your body's physical capabilities."),
        ClassFeature("Stunning Strike", "You can interfere with the flow of ki in an opponent's body to stun them with a melee weapon attack."),
    ),
    "Paladin" to listOf(
        ClassFeature("Divine Sense", "The presence of strong evil registers on your senses like a noxious odor, and powerful good rings like heavenly music in your ears."),
        ClassFeature("Lay on Hands", "Your blessed touch can heal wounds. You have a pool of healing power that replenishes when you take a long rest."),
        ClassFeature("Divine Smite", "When you hit a creature with a melee weapon attack, you can expend one spell slot to deal radiant damage to the target."),
        ClassFeature("Sacred Oath", "At 3rd level, you swear the oath that binds you as a paladin forever."),
    ),
    "Ranger" to listOf(
        ClassFeature("Favored Enemy", "You have significant experience studying, tracking, hunting, and even talking to a certain type of enemy."),
        ClassFeature("Natural Explorer", "You are particularly familiar with one type of natural environment and are adept at traveling and surviving in such regions."),
        ClassFeature("Primeval Awareness", "You can use your action and expend one ranger spell slot to focus your awareness on the region around you to detect certain creature types."),
    ),
    "Rogue" to listOf(
        ClassFeature("Expertise", "Your proficiency bonus is doubled for certain skill proficiencies or thieves' tools."),
        ClassFeature("Sneak Attack", "You know how to strike subtly and exploit a foe's distraction. Once per turn, you can deal extra damage to one creature you hit with an attack."),
        ClassFeature("Thieves' Cant", "You know a secret mix of dialect, jargon, and code that allows you to hide messages in seemingly normal conversation."),
        ClassFeature("Cunning Action", "Your quick thinking and agility allow you to move and act quickly. You can take a bonus action on each of your turns in combat to Dash, Disengage, or Hide."),
    ),
    "Sorcerer" to listOf(
        ClassFeature("Spellcasting", "An event in your past, or in the life of a parent or ancestor, left a metaphysical mark on you, conferring the ability to cast spells."),
        ClassFeature("Sorcerous Origin", "Choose a sorcerous origin, which describes the source of your innate magical power."),
        ClassFeature("Font of Magic", "You tap into a deep wellspring of magic within yourself. This wellspring is represented by sorcery points."),
        ClassFeature("Metamagic", "You gain the ability to twist your spells to suit your needs, such as doubling a spell's range or duration."),
    ),
    "Warlock" to listOf(
        ClassFeature("Otherworldly Patron", "At 1st level, you have struck a bargain with an otherworldly being of your choice, such as the Fiend."),
        ClassFeature("Pact Magic", "Your arcane research and the magic bestowed on you by your patron have given you facility with spells."),
        ClassFeature("Eldritch Invocations", "In your study of occult lore, you have unearthed eldritch incarnations, fragments of forbidden knowledge that imbue you with an abiding magical ability."),
        ClassFeature("Pact Boon", "Your patron bestows a gift upon you for your loyal service, taking the form of a Pact of the Chain, Blade, or Tome."),
    ),
    "Wizard" to listOf(
        ClassFeature("Spellcasting", "As a student of arcane magic, you have a spellbook containing spells that show the first glimmerings of your true power."),
        ClassFeature("Arcane Recovery", "You have learned to regain some of your magical energy by studying your spellbook. You can recover expended spell slots during a short rest."),
        ClassFeature("Arcane Tradition", "When you reach 2nd level, you choose an arcane tradition, shaping your practice of magic through one of eight schools."),
    ),
)

fun classFeatures(className: String): List<ClassFeature> =
    CLASS_FEATURES.entries.find { it.key.equals(className, ignoreCase = true) }?.value ?: emptyList()
