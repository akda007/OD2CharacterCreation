package com.akda.od2.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.akda.od2.R
import com.akda.od2.domain.application.CreatePlayer
import com.akda.od2.domain.model.*
import com.akda.od2.presentation.viewmodel.CharacterCreationUiState
import com.akda.od2.presentation.viewmodel.CharacterCreationViewModel
import com.akda.od2.presentation.viewmodel.CharacterCreationViewModelFactory

val medievalFont = FontFamily(
    Font(R.font.cinzel_regular, FontWeight.Normal)
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(fontFamily = medievalFont, fontWeight = FontWeight.Normal, fontSize = 34.sp),
    headlineMedium = TextStyle(fontFamily = medievalFont, fontWeight = FontWeight.Normal, fontSize = 28.sp),
    headlineSmall = TextStyle(fontFamily = medievalFont, fontWeight = FontWeight.Normal, fontSize = 24.sp),
    titleLarge = TextStyle(fontFamily = medievalFont, fontWeight = FontWeight.Normal, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = medievalFont, fontWeight = FontWeight.Normal, fontSize = 18.sp),
    bodyLarge = TextStyle(fontFamily = medievalFont, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    labelLarge = TextStyle(fontFamily = medievalFont, fontWeight = FontWeight.Normal, fontSize = 14.sp)
)

private val DarkColorPalette = darkColorScheme(
    primary = Color(0xFFD4AF37), // Ouro antigo
    onPrimary = Color(0xFF1C1C1C),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF242424),
    onSurface = Color(0xFFE0E0E0)
)

@Composable
fun OD2CharacterCreatorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorPalette,
        typography = AppTypography,
        content = content
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        setContent {
            OD2CharacterCreatorTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val viewModel: CharacterCreationViewModel = viewModel(factory = CharacterCreationViewModelFactory())
                    AppNavigation(navController, viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, viewModel: CharacterCreationViewModel) {
    NavHost(navController = navController, startDestination = "main_menu") {
        composable("main_menu") { MainMenuScreen(navController, viewModel) }
        composable("character_creation") { CharacterCreationFlow(navController, viewModel) }
        composable("character_list") { CharacterListScreen(navController, viewModel) }
    }
}

@Composable
fun MainMenuScreen(navController: NavController, viewModel: CharacterCreationViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Old Dragon II", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Text("Criador de Personagens", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(64.dp))

        ThemedButton(
            onClick = {
                viewModel.resetCreation()
                navController.navigate("character_creation")
            },
            text = "Criar Novo Personagem"
        )
        Spacer(modifier = Modifier.height(16.dp))
        ThemedButton(
            onClick = { navController.navigate("character_list") },
            text = "Listar Personagens",
            enabled = uiState.savedCharacters.isNotEmpty()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCreationFlow(navController: NavController, viewModel: CharacterCreationViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var currentStep by remember { mutableIntStateOf(1) }

    LaunchedEffect(uiState.characterCreated) {
        if (uiState.characterCreated) {
            navController.popBackStack("main_menu", inclusive = false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criação - Passo $currentStep de 5", fontFamily = medievalFont) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentStep) {
                1 -> StepNameAndRollMethod(uiState, viewModel) { currentStep++ }
                2 -> StepAttributes(uiState, viewModel) { currentStep++ }
                3 -> StepRace(viewModel) { currentStep++ }
                4 -> StepClass(viewModel) { currentStep++ }
                5 -> StepFinalReport(uiState, viewModel)
            }
        }
    }
}

@Composable
fun CharacterListScreen(navController: NavController, viewModel: CharacterCreationViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.savedCharacters.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nenhum personagem criado ainda.", style = MaterialTheme.typography.titleLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.savedCharacters) { personagem ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CutCornerShape(8.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(personagem.name, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                        Text("${personagem.race.name} | ${personagem.characterClass.name}")
                    }
                }
            }
        }
    }
}


@Composable
fun StepNameAndRollMethod(uiState: CharacterCreationUiState, viewModel: CharacterCreationViewModel, onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        OutlinedTextField(
            value = uiState.characterName,
            onValueChange = { viewModel.onNameChange(it) },
            label = { Text("Nome do Personagem", fontFamily = medievalFont) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontFamily = medievalFont, fontSize = 12.sp)
        )

        Text("Método de Rolagem:", style = MaterialTheme.typography.titleLarge)
        RollMethod.entries.forEach { method ->
            Row(
                Modifier.fillMaxWidth().clickable { viewModel.onRollMethodSelected(method) }.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = uiState.selectedRollMethod == method,
                    onClick = { viewModel.onRollMethodSelected(method) }
                )
                Spacer(Modifier.width(8.dp))
                Text(method.name.replaceFirstChar { it.titlecase() }, fontFamily = medievalFont)
            }
        }
        Spacer(Modifier.weight(1f))
        ThemedButton(
            onClick = onNext,
            text = "Próximo",
            enabled = uiState.characterName.isNotBlank()
        )
    }
}

@Composable
fun StepAttributes(uiState: CharacterCreationUiState, viewModel: CharacterCreationViewModel, onNext: () -> Unit) {
    var isRolling by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (uiState.rollResult == null) {
            ThemedButton(
                onClick = { if (!isRolling) isRolling = true },
                text = "Rolar Atributos"
            )
        }

        if (isRolling) {
            DiceRollAnimation {
                viewModel.rollAttributes()
                isRolling = false
            }
        } else {
            when (val result = uiState.rollResult) {
                is RollResult.Fixed -> {
                    Text("Atributos (Clássico):", style = MaterialTheme.typography.titleLarge)
                    result.attributes.forEach { (type, value) -> Text("${type.name}: $value") }
                }
                is RollResult.Distributable -> {
                    AttributeAssignmentContent(
                        rolledValues = result.valores,
                        onConfirm = { assignedAttributes ->
                            viewModel.onAttributesAssigned(assignedAttributes)
                        }
                    )
                }
                null -> {
                    Text("Clique no botão acima para rolar seus atributos.", fontFamily = medievalFont)
                }
            }
        }

        Spacer(Modifier.weight(1f))
        ThemedButton(onClick = onNext, text = "Próximo", enabled = uiState.finalAttributes != null)
    }
}

@Composable
fun StepRace(viewModel: CharacterCreationViewModel, onNext: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
    ) {
        items(Race.values()) { race ->
            ThemedButton(onClick = { viewModel.onRaceSelected(race); onNext() }, text = race.name)
        }
    }
}

@Composable
fun StepClass(viewModel: CharacterCreationViewModel, onNext: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
    ) {
        items(CharacterClass.values()) { characterClass ->
            ThemedButton(onClick = { viewModel.onClassSelected(characterClass); onNext() }, text = characterClass.name)
        }
    }
}

@Composable
fun StepFinalReport(uiState: CharacterCreationUiState, viewModel: CharacterCreationViewModel) {
    val personagem = remember(uiState) {
        if (uiState.selectedRace != null && uiState.selectedClass != null && uiState.finalAttributes != null) {
            CreatePlayer()(
                name = uiState.characterName,
                race = uiState.selectedRace,
                characterClass = uiState.selectedClass,
                attributes = uiState.finalAttributes
            )
        } else null
    }

    if (personagem == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Erro: Faltam dados para gerar o relatório.", fontFamily = medievalFont)
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Relatório Final", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                Card(modifier = Modifier.fillMaxWidth(), shape = CutCornerShape(8.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(personagem.name, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                        Text("${personagem.race.name} | ${personagem.characterClass.name}", style = MaterialTheme.typography.titleMedium)
                        HorizontalDivider()
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            Text("PV: ${personagem.currentHitPoints}/${personagem.maxHitPoints}", fontFamily = medievalFont)
                            Text("CA: ${personagem.armorClass}", fontFamily = medievalFont)
                            Text("MOV: ${personagem.movement}m", fontFamily = medievalFont)
                        }
                        HorizontalDivider()
                        Text("Atributos:", style = MaterialTheme.typography.titleMedium)
                        personagem.attributes.values.forEach { attr ->
                            Text("  ${attr.type.name.padEnd(4)}: ${attr.value.toString().padEnd(2)} (Mod: ${attr.modificator.toString().padStart(3,' ')})", fontFamily = medievalFont)
                        }
                    }
                }
                ThemedButton(onClick = { viewModel.finalizeCharacter() }, text = "Confirmar e Salvar")
            }
        }
    }
}

// --- Componentes Reutilizáveis e de UI ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttributeAssignmentContent(rolledValues: List<Int>, onConfirm: (Map<AttributeType, Attribute>) -> Unit) {
    var assignments by remember { mutableStateOf<Map<AttributeType, Int?>>(AttributeType.entries.associateWith { null }) }
    var availableValues by remember { mutableStateOf(rolledValues) }
    val allAssigned = assignments.values.all { it != null }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Atribua os valores:", style = MaterialTheme.typography.titleLarge)
        AttributeType.entries.forEach { attrType ->
            var expanded by remember { mutableStateOf(false) }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${attrType.name}:", modifier = Modifier.width(60.dp), fontFamily = medievalFont)
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    val selectedText = assignments[attrType]?.toString() ?: "Selecione"
                    OutlinedTextField(
                        value = selectedText,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(),
                        textStyle = TextStyle(fontFamily = medievalFont)
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        availableValues.forEach { value ->
                            DropdownMenuItem(
                                text = { Text(value.toString(), fontFamily = medievalFont) },
                                onClick = {
                                    val oldAssignedValue = assignments[attrType]
                                    if (oldAssignedValue != null) {
                                        availableValues = (availableValues + oldAssignedValue).sortedDescending()
                                    }
                                    assignments = assignments + (attrType to value)
                                    availableValues = availableValues - value
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
        if (allAssigned) {
            Spacer(Modifier.height(16.dp))
            ThemedButton(
                onClick = {
                    val finalMap = assignments.mapValues { (key, value) -> Attribute(key, value!!) }
                    onConfirm(finalMap)
                },
                text = "Confirmar Atributos"
            )
        }
    }
}

@Composable
fun ThemedButton(onClick: () -> Unit, text: String, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CutCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = Color.DarkGray.copy(alpha = 0.5f),
            disabledContentColor = Color.Gray
        ),
        modifier = Modifier.fillMaxWidth(0.8f).height(50.dp).border(1.dp, Color.DarkGray, CutCornerShape(8.dp))
    ) {
        Text(text, fontSize = 12.sp, fontFamily = medievalFont)
    }
}

@Composable
fun DiceRollAnimation(onAnimationEnd: () -> Unit) {
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = 360f * 3, // 3 rotações
            animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing)
        )
        onAnimationEnd()
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(32.dp)) {
        // Use uma imagem de dado genérica. Adicione um d20.png em res/drawable se tiver um.
        Image(
            painter = painterResource(id = R.drawable.dado_20),
            contentDescription = "Dado rolando",
            modifier = Modifier.size(100.dp).rotate(rotation.value)
        )
    }
}

