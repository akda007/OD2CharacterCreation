package com.akda.od2.domain.model


enum class Alignment { ORDEM, NEUTRO, QUALQUER }

sealed class Race(
    val name: String,
    val baseMovement: Int,
    val infravision: Int,
    val alignmentTendency: Alignment,
    val skillList: List<Skill>
) {
    abstract fun calculateXPBonus(xpGain: Int): Int

    object Human : Race(
        name = "Humano",
        baseMovement = 9,
        infravision = 0,
        alignmentTendency = Alignment.QUALQUER,
        skillList = listOf(
            Skill.Aprendizado,
            Skill.Adaptabilidade
        )
    ) {
        override fun calculateXPBonus(xpGain: Int): Int = (xpGain * 1.1).toInt()
    }

    object Elf : Race(
        name = "Elfo",
        baseMovement = 9,
        infravision = 18,
        alignmentTendency = Alignment.NEUTRO,
        skillList = listOf(
            Skill.PercepcaoNatural,
            Skill.Gracioso,
            Skill.ArmaRacialArcos,
            Skill.ImunidadeSonoParalisiaGhoul
        )
    ) {
        override fun calculateXPBonus(xpGain: Int): Int = xpGain
    }

    object Dwarf : Race(
        name = "Anão",
        baseMovement = 6,
        infravision = 18,
        alignmentTendency = Alignment.ORDEM,
        skillList = listOf(
            Skill.Mineradores,
            Skill.Vigoroso,
            Skill.InimigosNaturais,
            Skill.RestricaoArmasGrandes
        )
    ) {
        override fun calculateXPBonus(xpGain: Int): Int = xpGain
    }

    object Halfling : Race(
        name = "Halfling",
        baseMovement = 6,
        infravision = 0,
        alignmentTendency = Alignment.NEUTRO,
        skillList = listOf(
            Skill.Furtivos,
            Skill.Destemidos,
            Skill.BonsDeMira,
            Skill.Pequenos,
            Skill.RestricaoEquipamentoHalfling
        )
    ) {
        override fun calculateXPBonus(xpGain: Int): Int = xpGain
    }

    companion object {
        fun values() = arrayOf(Human, Elf, Dwarf, Halfling)
    }
}