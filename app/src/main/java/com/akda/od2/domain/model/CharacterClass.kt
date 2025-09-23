package com.akda.od2.domain.model

data class Specialization(
    val name: String,
    val description: String
)

sealed class CharacterClass(
    val name: String,
    val baseHitPoints: Int,
    val baseAttackBonus: Int,
    val baseSavingThrow: Int,
    val baseAbilities: List<String>,
    val specializations: List<Specialization>
) {
    object Warrior : CharacterClass(
        name = "Guerreiro",
        baseHitPoints = 10,
        baseAttackBonus = 1,
        baseSavingThrow = 5,
        baseAbilities = listOf(
            "Pode usar todas as armas e armaduras",
            "Aparar: Sacrifica arma/escudo para negar dano",
            "Maestria em Arma: Bônus de dano com armas escolhidas",
            "Ataque Extra no 6º nível"
        ),
        specializations = listOf(
            Specialization("Bárbaro", "Combatente selvagem com fúria e resistência aumentada"),
            Specialization("Paladino", "Guerreiro sagrado com poderes divinos e código de honra")
        )
    )

    object Clerig : CharacterClass(
        name = "Clérigo",
        baseHitPoints = 8,
        baseAttackBonus = 1,
        baseSavingThrow = 5,
        baseAbilities = listOf(
            "Pode usar armas impactantes e todas as armaduras",
            "Conjura Magias Divinas",
            "Afastar Mortos-Vivos",
            "Cura Milagrosa: Pode trocar magias por cura"
        ),
        specializations = listOf(
            Specialization("Druida", "Mestre da natureza, pode se transformar e usar magias naturais"),
            Specialization("Acadêmico", "Estudioso dos deuses e dos mistérios divinos")
        )
    )

    object Thief : CharacterClass(
        name = "Ladrão",
        baseHitPoints = 6,
        baseAttackBonus = 1,
        baseSavingThrow = 5,
        baseAbilities = listOf(
            "Pode usar apenas armaduras leves e armas pequenas ou médias",
            "Ataque Furtivo: Multiplica o dano ao atacar desprevenido",
            "Ouvir Ruídos",
            "Talentos de Ladrão: Arrombar, Desarmar Armadilhas, Escalar, Furtividade, Punga"
        ),
        specializations = listOf(
            Specialization("Ranger", "Caçador e rastreador em ambientes selvagens"),
            Specialization("Bardo", "Versátil, com habilidades musicais e mágicas")
        )
    )

    object Mage : CharacterClass(
        name = "Mago",
        baseHitPoints = 4,
        baseAttackBonus = 0,
        baseSavingThrow = 5,
        baseAbilities = listOf(
            "Não pode usar armaduras e apenas armas pequenas",
            "Conjura Magias Arcanas a partir de um Grimório",
            "Ler Magias",
            "Detectar Magias"
        ),
        specializations = listOf(
            Specialization("Ilusionista", "Manipula ilusões e engana os sentidos"),
            Specialization("Necromante", "Domina os mortos e a energia negativa")
        )
    )

    companion object {
        fun values() = arrayOf(Warrior, Clerig, Thief, Mage)
    }
}