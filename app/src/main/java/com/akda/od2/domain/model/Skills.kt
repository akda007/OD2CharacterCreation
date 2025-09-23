package com.akda.od2.domain.model


sealed class Skill(val name: String, val description: String) {
    object Aprendizado : Skill(
        "Aprendizado",
        "Recebe um bônus de 10% sobre toda a experiência (XP) recebida."
    )
    object Adaptabilidade : Skill(
        "Adaptabilidade",
        "Recebe um bônus de +1 em uma única Jogada de Proteção (JP) à sua escolha."
    )
    object PercepcaoNatural : Skill(
        "Percepção Natural",
        "Detecta portas secretas com chance de 1 em 1d6 ao passar perto, ou 1-2 em 1d6 se procurando ativamente."
    )
    object Gracioso : Skill(
        "Gracioso",
        "Recebe +1 em qualquer teste de Jogada de Proteção de Destreza (JPD)."
    )
    object ArmaRacialArcos : Skill(
        "Arma Racial (Arcos)",
        "Bônus de +1 no dano com arcos."
    )
    object ImunidadeSonoParalisiaGhoul : Skill(
        "Imunidades",
        "Imune a magias de sono e à paralisia causada por Ghouls."
    )
    object Mineradores : Skill(
        "Mineradores",
        "Detecta anomalias em pedra (armadilhas, fossos) com chance de 1 em 1d6, ou 1-2 em 1d6 se procurando ativamente."
    )
    object Vigoroso : Skill(
        "Vigoroso",
        "Recebe +1 em qualquer teste de Jogada de Proteção de Constituição (JPC)."
    )
    object InimigosNaturais : Skill(
        "Inimigos Naturais",
        "Ataques contra orcs, ogros e hobgoblins são considerados fáceis."
    )
    object Furtivos : Skill(
        "Furtivos",
        "Consegue se esconder com uma chance de 1-2 em 1d6. Ladrões adicionam +1 ao seu talento Furtividade."
    )
    object Destemidos : Skill(
        "Destemidos",
        "Recebe +1 em qualquer teste de Jogada de Proteção de Sabedoria (JPS)."
    )
    object BonsDeMira : Skill(
        "Bons de Mira",
        "Ataques à distância com armas de arremesso são considerados fáceis."
    )
    object Pequenos : Skill(
        "Pequenos",
        "Criaturas grandes ou maiores têm dificuldade para acertá-los (o ataque delas é difícil)."
    )
    // Habilidades que são mais restrições do que perícias, mas podem ser modeladas se necessário.
    object RestricaoArmasGrandes : Skill(
        "Restrição de Armas Grandes",
        "Anões não podem usar armas grandes, a menos que sejam de fabricação racial anã."
    )
    object RestricaoEquipamentoHalfling : Skill(
        "Restrição de Equipamento (Halfling)",
        "Só usam armaduras de couro e armas pequenas ou médias (estas com duas mãos)."
    )
}