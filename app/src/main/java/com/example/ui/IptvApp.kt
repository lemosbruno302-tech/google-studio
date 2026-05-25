package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.nativeCanvas
import com.example.ui.theme.Typography
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.IptvClient
import com.example.data.IptvPlan
import com.example.data.IptvTransaction
import com.example.data.IptvUser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

// Predefined servers metadata with design colors
data class ServerConfig(
    val id: String,
    val name: String,
    val description: String,
    val color: Color,
    val imageIcon: String
)

val PREDEFINED_SERVERS = listOf(
    ServerConfig("unitv", "UniTV", "Servidor Premium UniTV", Color(0xFF673AB7), "U"),
    ServerConfig("starplay", "StarPlay", "Acesso Rápido StarPlay", Color(0xFFFFB300), "S"),
    ServerConfig("blinder", "Blinder", "Transmissão Blinder", Color(0xFF009688), "B"),
    ServerConfig("manual", "Servidor Manual", "Adicionado Manunamente", Color(0xFF607D8B), "M")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IptvApp(viewModel: IptvViewModel) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val selectedThemePalette by viewModel.selectedThemePalette.collectAsStateWithLifecycle()

    // Wrap with the custom selected theme palette
    val appColorScheme = if (isDarkTheme) {
        when (selectedThemePalette) {
            "AMBER" -> darkColorScheme(
                primary = Color(0xFFFFB74D), // Soft Amber Gold
                onPrimary = Color(0xFF4E2600),
                primaryContainer = Color(0xFF9E4B00),
                onPrimaryContainer = Color(0xFFFFF3E0),
                secondary = Color(0xFFFFCC80),
                onSecondary = Color(0xFF3E2723),
                background = Color(0xFF161412),
                surface = Color(0xFF23201C),
                onBackground = Color(0xFFEFEBE9),
                onSurface = Color(0xFFFFFFFF),
                error = Color(0xFFEF9A9A)
            )
            "CYBER" -> darkColorScheme(
                primary = Color(0xFF00E5FF), // Neon Cyan
                onPrimary = Color(0xFF00363A),
                primaryContainer = Color(0xFF006064),
                onPrimaryContainer = Color(0xFFE0F7FA),
                secondary = Color(0xFFFF4081), // Neon Pink/Magenta
                onSecondary = Color(0xFF4A0033),
                background = Color(0xFF0D0E15),
                surface = Color(0xFF161824),
                onBackground = Color(0xFFECEFF1),
                onSurface = Color(0xFFFFFFFF),
                error = Color(0xFFFF5252)
            )
            "OCEAN" -> darkColorScheme(
                primary = Color(0xFF4DD0E1), // Ocean turquoise
                onPrimary = Color(0xFF00363A),
                primaryContainer = Color(0xFF004D40),
                onPrimaryContainer = Color(0xFFE0F2F1),
                secondary = Color(0xFF80DEEA),
                onSecondary = Color(0xFF002D38),
                background = Color(0xFF0B141E),
                surface = Color(0xFF132233),
                onBackground = Color(0xFFE0F2F1),
                onSurface = Color(0xFFFFFFFF),
                error = Color(0xFFEF9A9A)
            )
            "SLATE" -> darkColorScheme(
                primary = Color(0xFFECEFF1), // Soft slate silver
                onPrimary = Color(0xFF263238),
                primaryContainer = Color(0xFF37474F),
                onPrimaryContainer = Color(0xFFECEFF1),
                secondary = Color(0xFFB0BEC5),
                onSecondary = Color(0xFF212730),
                background = Color(0xFF1E2125),
                surface = Color(0xFF2A2E35),
                onBackground = Color(0xFFECEFF1),
                onSurface = Color(0xFFFFFFFF),
                error = Color(0xFFEF9A9A)
            )
            else -> darkColorScheme( // EMERALD (Current green default)
                primary = Color(0xFFA5D6A7),
                onPrimary = Color(0xFF1B5E20),
                primaryContainer = Color(0xFF2E7D32),
                onPrimaryContainer = Color(0xFFE8F5E9),
                secondary = Color(0xFF81D4FA),
                onSecondary = Color(0xFF01579B),
                background = Color(0xFF121212),
                surface = Color(0xFF1E1E1E),
                onBackground = Color(0xFFE0E0E0),
                onSurface = Color(0xFFFFFFFF),
                error = Color(0xFFEF9A9A)
            )
        }
    } else {
        when (selectedThemePalette) {
            "AMBER" -> lightColorScheme(
                primary = Color(0xFFE65100),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFFFCC80),
                onPrimaryContainer = Color(0xFF3E2723),
                secondary = Color(0xFFF57C00),
                onSecondary = Color(0xFFFFFFFF),
                background = Color(0xFFFFFDFB),
                surface = Color(0xFFFFFFFF),
                onBackground = Color(0xFF271C19),
                onSurface = Color(0xFF271C19),
                error = Color(0xFFD32F2F)
            )
            "CYBER" -> lightColorScheme(
                primary = Color(0xFF00838F),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFB2EBF2),
                onPrimaryContainer = Color(0xFF002D33),
                secondary = Color(0xFFC2185B),
                onSecondary = Color(0xFFFFFFFF),
                background = Color(0xFFFAF9FC),
                surface = Color(0xFFFFFFFF),
                onBackground = Color(0xFF1A191C),
                onSurface = Color(0xFF1A191C),
                error = Color(0xFFC2185B)
            )
            "OCEAN" -> lightColorScheme(
                primary = Color(0xFF006064),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFB2EBF2),
                onPrimaryContainer = Color(0xFF001F21),
                secondary = Color(0xFF00838F),
                onSecondary = Color(0xFFFFFFFF),
                background = Color(0xFFF0F4F8),
                surface = Color(0xFFFFFFFF),
                onBackground = Color(0xFF1A2229),
                onSurface = Color(0xFF1A2229),
                error = Color(0xFFD32F2F)
            )
            "SLATE" -> lightColorScheme(
                primary = Color(0xFF37474F),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFCFD8DC),
                onPrimaryContainer = Color(0xFF1A237E),
                secondary = Color(0xFF455A64),
                onSecondary = Color(0xFFFFFFFF),
                background = Color(0xFFF4F6F7),
                surface = Color(0xFFFFFFFF),
                onBackground = Color(0xFF21272B),
                onSurface = Color(0xFF21272B),
                error = Color(0xFFD32F2F)
            )
            else -> lightColorScheme( // EMERALD (Current green default)
                primary = Color(0xFF2E7D32),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFC8E6C9),
                onPrimaryContainer = Color(0xFF1B5E20),
                secondary = Color(0xFF0288D1),
                onSecondary = Color(0xFFFFFFFF),
                background = Color(0xFFFAFAFA),
                surface = Color(0xFFFFFFFF),
                onBackground = Color(0xFF212121),
                onSurface = Color(0xFF212121),
                error = Color(0xFFD32F2F)
            )
        }
    }

    MaterialTheme(
        colorScheme = appColorScheme,
        typography = Typography
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (currentUser == null) {
                // Return Authentication screen
                LoginScreen(viewModel = viewModel)
            } else {
                // Return main application Scaffold dashboard layout
                MainDashboard(viewModel = viewModel)
            }
        }
    }
}

// --- SUB-VIEWS ---

@Composable
fun LoginScreen(viewModel: IptvViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = "IPTV Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Next ",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Gerenciador",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Text(
                    text = "Acesso Restrito ao Painel",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )

                // User Inputs
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Nome de usuário") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User Icon") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Senha administrativa") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                if (loginError != null) {
                    Text(
                        text = loginError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (username.isNotEmpty() && password.isNotEmpty()) {
                            viewModel.login(username, password)
                        } else {
                            Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Login, contentDescription = "Acessar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Entrar no Painel", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Fast test user profiles listing
                Text(
                    text = "Perfis de Teste Rápido (Selecione):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElevatedButton(
                        onClick = {
                            username = "bruno_editor"
                            password = "1234"
                            viewModel.login(username, password)
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Editor", fontSize = 10.sp)
                    }
                    ElevatedButton(
                        onClick = {
                            username = "visita_viewer"
                            password = "1234"
                            viewModel.login(username, password)
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Viewer", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MainDashboard(viewModel: IptvViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var showAddClientDialog by remember { mutableStateOf(false) }
    var showAddPlanDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            HeaderSection(viewModel = viewModel)
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { viewModel.currentTab.value = 0 },
                    icon = { Icon(Icons.Default.People, contentDescription = "Clientes") },
                    label = { Text("Clientes", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { viewModel.currentTab.value = 1 },
                    icon = { Icon(Icons.Default.CardMembership, contentDescription = "Planos") },
                    label = { Text("Planos", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { viewModel.currentTab.value = 2 },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = "Finanças") },
                    label = { Text("Finanças", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { viewModel.currentTab.value = 3 },
                    icon = { Icon(Icons.Default.Description, contentDescription = "Observações") },
                    label = { Text("Notas", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = currentTab == 4,
                    onClick = { viewModel.currentTab.value = 4 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Backup/Painel") },
                    label = { Text("Backup", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
        },
        floatingActionButton = {
            if (currentTab == 0 && viewModel.hasWritePermission()) {
                FloatingActionButton(
                    onClick = { showAddClientDialog = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.testTag("add_client_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Cliente")
                }
            } else if (currentTab == 1 && viewModel.hasWritePermission()) {
                FloatingActionButton(
                    onClick = { showAddPlanDialog = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.testTag("add_plan_fab")
                ) {
                    Icon(Icons.Default.AddCard, contentDescription = "Adicionar Plano")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> ClientesTabContent(viewModel = viewModel)
                1 -> PlanosTabContent(viewModel = viewModel)
                2 -> FinancasTabContent(viewModel = viewModel)
                3 -> ObservacoesTabContent(viewModel = viewModel)
                4 -> BackupTabContent(viewModel = viewModel)
            }
        }
    }

    if (showAddClientDialog) {
        AddClientDialog(viewModel = viewModel, onDismiss = { showAddClientDialog = false })
    }

    if (showAddPlanDialog) {
        AddPlanDialog(viewModel = viewModel, onDismiss = { showAddPlanDialog = false })
    }
}

@Composable
fun HeaderSection(viewModel: IptvViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    var showProfileMenu by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.5.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Brand Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = "IPTV Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Next ",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Gerenciador",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                // Right Info with Theme trigger & User badge trigger
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.isDarkTheme.value = !isDarkTheme },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Mudar Tema",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    // Minimalist compact profile badge
                    IconButton(
                        onClick = { showProfileMenu = !showProfileMenu },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = when (currentUser?.role) {
                                "ADMIN" -> Icons.Default.Shield
                                "EDITOR" -> Icons.Default.Edit
                                else -> Icons.Default.Visibility
                            },
                            contentDescription = "Perfil",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = currentUser?.username ?: "Anon",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable { showProfileMenu = !showProfileMenu }
                            .padding(end = 2.dp)
                    )
                }
            }

            AnimatedVisibility(visible = showProfileMenu) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Nível de Permissão: ${currentUser?.role ?: "VISUALIZAR"}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = when (currentUser?.role) {
                                "ADMIN" -> "• Acesso total: Criar, editar, renovar, excluir, criar planos e gerenciar banco de dados."
                                "EDITOR" -> "• Acesso intermediário: Cadastrar, alterar e renovar clientes. Não exclui dados e não acessa backups."
                                else -> "• Acesso leitura: Permissão exclusiva para monitoramento visual de conexões e notas."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = {
                                viewModel.logout()
                                showProfileMenu = false
                            }) {
                                Icon(Icons.Default.Logout, contentDescription = "Sair", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sair do Perfil", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- TAB - CLIENTES ---

@Composable
fun ClientesTabContent(viewModel: IptvViewModel) {
    val clients by viewModel.filteredClients.collectAsStateWithLifecycle()
    val rawClients by viewModel.clients.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedServer by viewModel.selectedServerFilter.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()

    val currentTime = System.currentTimeMillis()
    val fiveDaysMs = 5L * 24 * 60 * 60 * 1000

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // PREDEFINED SERVERS HORIZONTAL CARDS
        item {
            Text(
                text = "Plataformas Contratadas (Filtrar)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Preloaded specific filter card: "TODOS"
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedServer == null) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        modifier = Modifier
                            .width(100.dp)
                            .height(80.dp)
                            .border(
                                width = if (selectedServer == null) 2.dp else 1.dp,
                                color = if (selectedServer == null) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.selectedServerFilter.value = null },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                "TODOS",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = if (selectedServer == null) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                            Text(
                                "${rawClients.size} clis",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                // Servers
                items(PREDEFINED_SERVERS) { conf ->
                    val serverCount = rawClients.count { it.provider.equals(conf.name, ignoreCase = true) }
                    val isCurrent = selectedServer?.equals(conf.name, ignoreCase = true) == true

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) conf.color.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .width(115.dp)
                            .height(80.dp)
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) conf.color else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.selectedServerFilter.value = conf.name },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(conf.color),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    conf.imageIcon,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                conf.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "$serverCount ativos",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }

        // STATS TAB ROW (TODOS | VENCENDO | VENCIDOS)
        item {
            Text(
                text = "Situação de Assinaturas (Status)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            val totalCount = rawClients.size
            val expiringCount = rawClients.count {
                val remaining = it.dueDate - currentTime
                remaining in 0..fiveDaysMs && it.active
            }
            val expiredCount = rawClients.count { it.dueDate < currentTime && it.active }

            TabRow(
                selectedTabIndex = when (selectedStatus) {
                    "VENCENDO" -> 1
                    "VENCIDO" -> 2
                    else -> 0
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Tab(
                    selected = selectedStatus == "TODOS",
                    onClick = { viewModel.selectedStatusFilter.value = "TODOS" },
                    text = { Text("Total ($totalCount)", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedStatus == "VENCENDO",
                    onClick = { viewModel.selectedStatusFilter.value = "VENCENDO" },
                    text = {
                        Text(
                            "Vencendo ($expiringCount)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (expiringCount > 0) Color(0xFFFF9800) else LocalContentColor.current
                        )
                    }
                )
                Tab(
                    selected = selectedStatus == "VENCIDO",
                    onClick = { viewModel.selectedStatusFilter.value = "VENCIDO" },
                    text = {
                        Text(
                            "Vencidos ($expiredCount)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (expiredCount > 0) Color(0xFFD32F2F) else LocalContentColor.current
                        )
                    }
                )
            }
        }

        // SEARCH BAR
        item {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                label = { Text("Visualizar por cliente...") },
                placeholder = { Text("Digite nome, telefone ou nota") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_field"),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // COLLAPSIBLE LAYOUT CONFIGURATOR
        item {
            var expandedConfigs by remember { mutableStateOf(false) }
            val currentPalette by viewModel.selectedThemePalette.collectAsStateWithLifecycle()
            val currentLayoutMode by viewModel.clientLayoutMode.collectAsStateWithLifecycle()

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.12f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedConfigs = !expandedConfigs },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Personalizar Aparência",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Opções de Layout & Aparência",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Icon(
                            imageVector = if (expandedConfigs) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expandir detalhes",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    AnimatedVisibility(visible = expandedConfigs) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))

                            // Estilo de Exibição
                            Text(
                                "Modo de Visualização dos Clientes:",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Opção Lista
                                InputChip(
                                    selected = currentLayoutMode == "LISTA",
                                    onClick = { viewModel.clientLayoutMode.value = "LISTA" },
                                    label = { Text("📱 Lista Detalhada", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    modifier = Modifier.weight(1f)
                                )
                                // Opção Grid
                                InputChip(
                                    selected = currentLayoutMode == "GRID",
                                    onClick = { viewModel.clientLayoutMode.value = "GRID" },
                                    label = { Text("📊 Grid Compacto", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Paleta de Cores do Painel
                            Text(
                                "Paleta de Cores do Sistema:",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            val palettes = listOf(
                                Triple("EMERALD", "🟢 Esmeralda", Color(0xFF2E7D32)),
                                Triple("AMBER", "🟠 Solar", Color(0xFFE65100)),
                                Triple("CYBER", "🔵 Neon Tech", Color(0xFF00E5FF)),
                                Triple("OCEAN", "🐳 Marinho", Color(0xFF006064)),
                                Triple("SLATE", "⚪ Slate Modern", Color(0xFF37474F))
                            )

                            // Wrap them inside a row flow of chips
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(palettes) { (id, label, accentColor) ->
                                    val isSelected = currentPalette == id
                                    SuggestionChip(
                                        onClick = { viewModel.selectedThemePalette.value = id },
                                        label = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(accentColor)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                            }
                                        },
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isSelected) 1.5.dp else 0.8.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // CLIENT LISTING
        if (clients.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TvOff,
                        contentDescription = "No clients",
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Nenhum cliente cadastrado neste status.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Toque no botão '+' abaixo para adicionar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            val currentLayoutMode = viewModel.clientLayoutMode.value
            if (currentLayoutMode == "GRID") {
                val chunkedClients = clients.chunked(2)
                items(chunkedClients) { rowClients ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowClients.forEach { client ->
                            Box(modifier = Modifier.weight(1f)) {
                                ClientGridCard(client = client, viewModel = viewModel)
                            }
                        }
                        if (rowClients.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            } else {
                items(clients) { client ->
                    ClientCard(client = client, viewModel = viewModel)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientGridCard(client: IptvClient, viewModel: IptvViewModel) {
    val context = LocalContext.current
    val currentTime = System.currentTimeMillis()
    val remainingMs = client.dueDate - currentTime
    val remainingDays = (remainingMs / (24 * 60 * 60 * 1000)).toInt()
    
    var showRenewDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf(false) }

    val statusColor = when {
        remainingMs < 0 -> Color(0xFFD32F2F) // Vencido (Red)
        remainingMs <= 5L * 24 * 60 * 60 * 1000 -> Color(0xFFFF9800) // Vencendo (Orange)
        else -> Color(0xFF2E7D32) // Ativo (Green)
    }

    val dateStr = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(client.dueDate))

    val borderStroke = when {
        remainingMs < 0 -> androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFD32F2F).copy(alpha = 0.5f))
        remainingMs <= 5L * 24 * 60 * 60 * 1000 -> androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFFF9800).copy(alpha = 0.5f))
        else -> androidx.compose.foundation.BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clickable { showDetailDialog = true },
        shape = RoundedCornerShape(12.dp),
        border = borderStroke,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // Header: Indicator + Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Status indicator colored dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                
                // Server short tag / label
                val serverConf = PREDEFINED_SERVERS.find { it.name.equals(client.provider, ignoreCase = true) }
                val badgeColor = serverConf?.color ?: MaterialTheme.colorScheme.primary
                Text(
                    text = client.provider,
                    fontSize = 11.sp,
                    color = badgeColor,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.12f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Client Name
            Text(
                text = client.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Info rows: Vence em, Preço
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (remainingMs < 0) "Vencido" else "Vence $dateStr",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = if (remainingMs < 0) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "R$ ${String.format(Locale.getDefault(), "%.2f", client.price)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Quick display of existence of credentials
            if (client.username.isNotEmpty() || client.password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VpnKey,
                        contentDescription = "Possui login",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.size(9.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Login Cadastrado",
                        fontSize = 8.sp,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }

            // Quick actions buttons footer
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Quick Renew Button
                IconButton(
                    onClick = { showRenewDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Renovar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            "Renovar",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Quick WhatsApp Button
                IconButton(
                    onClick = {
                        try {
                            val cleanPhone = client.phone.replace(Regex("[^0-9]"), "")
                            val formattedPhone = if (cleanPhone.length == 10 || cleanPhone.length == 11) {
                                "55$cleanPhone"
                            } else {
                                cleanPhone
                            }
                            val url = "https://api.whatsapp.com/send?phone=$formattedPhone"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Erro ao abrir WhatsApp", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .background(Color(0xFF25D366).copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Message,
                            contentDescription = "WhatsApp",
                            tint = Color(0xFF25D366),
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            "Mensagem",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E7E34)
                        )
                    }
                }
            }
        }
    }

    if (showRenewDialog) {
        RenewClientDialog(
            client = client,
            viewModel = viewModel,
            onDismiss = { showRenewDialog = false }
        )
    }

    if (showDetailDialog) {
        // A compact detail dialog showing notes and credentials
        AlertDialog(
            onDismissRequest = { showDetailDialog = false },
            confirmButton = {
                TextButton(onClick = { showDetailDialog = false }) {
                    Text("Fechar")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(client.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Vencimento:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        val fullDateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(client.dueDate))
                        Text(fullDateStr, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = statusColor)
                    }

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Servidor:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(client.provider, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Preço:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("R$ ${String.format(Locale.getDefault(), "%.2f", client.price)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }

                    if (client.username.isNotEmpty() || client.password.isNotEmpty()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        Text("Dados de Acesso:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        
                        // Username
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("Usuário", client.username)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Usuário copiado!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(6.dp)
                        ) {
                            Text("USUÁRIO", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(client.username, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", modifier = Modifier.size(10.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        // Password
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("Senha", client.password)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Senha copiada!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(6.dp)
                        ) {
                            Text("SENHA", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(client.password, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", modifier = Modifier.size(10.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    if (client.notes.isNotEmpty()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        Text("Notas / Observações:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            client.notes,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                .padding(8.dp)
                        )
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientCard(client: IptvClient, viewModel: IptvViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val currentTime = System.currentTimeMillis()
    val remainingMs = client.dueDate - currentTime
    val remainingDays = (remainingMs / (24 * 60 * 60 * 1000)).toInt()

    var showRenewDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var isEditingNotes by remember { mutableStateOf(false) }
    var notesTextState by remember(client.notes) { mutableStateOf(client.notes) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showMessageOptions by remember { mutableStateOf(false) }
    var showIptvUrlsDialog by remember { mutableStateOf(false) }

    // Deduce indicators
    val statusColor = when {
        remainingMs < 0 -> Color(0xFFD32F2F) // Vencido
        remainingMs <= 5L * 24 * 60 * 60 * 1000 -> Color(0xFFFF9800) // Vencendo
        else -> Color(0xFF2E7D32) // Ativo
    }

    val statusText = when {
        remainingMs < 0 -> "Vencido"
        remainingDays == 0 -> "Vence Hoje!"
        remainingDays == 1 -> "Vence Amanhã!"
        else -> "Vence em $remainingDays dias"
    }

    val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(client.dueDate))

    val borderStroke = when {
        remainingMs < 0 -> androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD32F2F).copy(alpha = 0.4f))
        remainingMs <= 5L * 24 * 60 * 60 * 1000 -> androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFF9800).copy(alpha = 0.4f))
        else -> androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(if (expanded) 6.dp else 2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        border = borderStroke,
        colors = CardDefaults.cardColors(
            containerColor = if (expanded) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.04f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = client.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (client.username.isNotEmpty() || client.password.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = "Possui credenciais",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Server Badge decoration
                        val servColor = PREDEFINED_SERVERS.find {
                            it.name.equals(client.provider, ignoreCase = true)
                        }?.color ?: Color(0xFF607D8B)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(servColor.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                client.provider.uppercase(Locale.getDefault()),
                                color = servColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Phone
                        Text(
                            text = client.phone,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Expiry/Vencimento Status Badge
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = statusText,
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Alterar Vencimento",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Venc: $dateStr",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // EXPANDED REGULAR DETAILS
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 4.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Plan details description
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Plano Contratado:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            "${client.planName} (R$ %.2f)".format(client.price),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // CREDENTIALS ACCESS DISPLAY
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VpnKey,
                                        contentDescription = "Dados de Acesso",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Dados de Acesso / Login",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                
                                if (client.username.isNotEmpty() || client.password.isNotEmpty()) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = {
                                                showIptvUrlsDialog = true
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Tv,
                                                contentDescription = "Gerador de Playlists",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                                val clip = android.content.ClipData.newPlainText(
                                                    "Credenciais",
                                                    "Usuário: ${client.username}\nSenha: ${client.password}"
                                                )
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "Usuário e Senha copiados!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copiar Tudo",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Username Sub-block
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            if (client.username.isNotEmpty()) {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                                val clip = android.content.ClipData.newPlainText("Usuário", client.username)
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "Usuário copiado!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        "USUÁRIO",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontSize = 8.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = client.username.ifEmpty { "Não Definido" },
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = if (client.username.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (client.username.isNotEmpty()) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copiar Usuário",
                                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }

                                // Password Sub-block
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            if (client.password.isNotEmpty()) {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                                val clip = android.content.ClipData.newPlainText("Senha", client.password)
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "Senha copiada!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        "SENHA",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontSize = 8.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = client.password.ifEmpty { "Não Definida" },
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = if (client.password.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (client.password.isNotEmpty()) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copiar Senha",
                                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // NOTES RESOLUTION AND LINK CLICKS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Anotações / Notas de Configuração:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        if (viewModel.hasWritePermission()) {
                            IconButton(
                                onClick = { isEditingNotes = !isEditingNotes },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (isEditingNotes) Icons.Filled.Close else Icons.Filled.EditNote,
                                    contentDescription = "Editar Nota",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (isEditingNotes) {
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            OutlinedTextField(
                                value = notesTextState,
                                onValueChange = { notesTextState = it },
                                label = { Text("Texto livre com links") },
                                placeholder = { Text("Insira observações, links do servidor, teste m3u...") },
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                textStyle = MaterialTheme.typography.bodySmall,
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { 
                                        notesTextState = client.notes
                                        isEditingNotes = false 
                                    }
                                ) {
                                    Text("Cancelar", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.updateClient(client.copy(notes = notesTextState.trim()))
                                        isEditingNotes = false
                                        Toast.makeText(context, "Nota atualizada e salva!", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Salvar Notas", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else if (client.notes.trim().isNotEmpty()) {
                        // Parse URLs and make clickable links
                        val notesText = client.notes
                        val urls = extractUrls(notesText)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = notesText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                if (urls.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        "Links Detectados (Toque para Abrir):",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    urls.forEach { url ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    val cleanUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                                        "https://$url"
                                                    } else url
                                                    try {
                                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cleanUrl)).apply {
                                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        }
                                                        context.startActivity(intent)
                                                    } catch (e: Exception) {
                                                        Toast.makeText(context, "Navegador não encontrado", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Launch,
                                                contentDescription = "Abrir Link",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = url,
                                                color = MaterialTheme.colorScheme.primary,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                textDecoration = TextDecoration.Underline,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            "Nenhuma nota ou link inserido para este cliente.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // CALENDAR EXPIRATION DIRECT SETTING
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Vencimento da Conta (Calendário):",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                "Nova data: $dateStr",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { showDatePicker = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Modificar vencimento no calendário", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Alterar Data", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ACTIONS ROW (NOTIFICAR, RENOVAR, EDITAR, EXCLUIR)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Show popup options of WhatsApp reminder template messages
                        Button(
                            onClick = {
                                showMessageOptions = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), // Whatsapp Green
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = "Notificar WhatsApp", modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Opções WhatsApp", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        if (showMessageOptions) {
                            MessageOptionsDialog(
                                client = client,
                                viewModel = viewModel,
                                onDismiss = { showMessageOptions = false }
                            )
                        }

                        // Renew subscription
                        if (viewModel.hasWritePermission()) {
                            ElevatedButton(
                                onClick = { showRenewDialog = true },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.Autorenew, contentDescription = "Renovar", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Renovar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            FilledTonalButton(
                                onClick = { showEditDialog = true },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Editar", fontSize = 11.sp)
                            }
                        }

                        // Delete Client (Only Admin can delete)
                        if (viewModel.hasAdminPermission()) {
                            IconButton(onClick = {
                                viewModel.deleteClient(client)
                                Toast.makeText(context, "Cliente removido!", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Excluir",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showRenewDialog) {
        RenewClientDialog(
            client = client,
            viewModel = viewModel,
            onDismiss = { showRenewDialog = false }
        )
    }

    if (showEditDialog) {
        EditClientDialog(
            client = client,
            viewModel = viewModel,
            onDismiss = { showEditDialog = false }
        )
    }

    if (showIptvUrlsDialog) {
        IptvConnectionDetailsDialog(
            client = client,
            onDismiss = { showIptvUrlsDialog = false }
        )
    }

    if (showDatePicker) {
        val initialDate = if (client.dueDate > 0L) client.dueDate else System.currentTimeMillis()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate = datePickerState.selectedDateMillis
                        if (selectedDate != null) {
                            viewModel.updateClient(client.copy(dueDate = selectedDate))
                            Toast.makeText(context, "Vencimento da conta atualizado!", Toast.LENGTH_SHORT).show()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// Regex matching helper for URLs
fun extractUrls(text: String): List<String> {
    val list = mutableListOf<String>()
    val pattern = "(https?://)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)"
    val regex = pattern.toRegex()
    val matches = regex.findAll(text)
    for (m in matches) {
        list.add(m.value)
    }
    return list
}

// --- TAB - PLANOS ---

@Composable
fun PlanosTabContent(viewModel: IptvViewModel) {
    val plans by viewModel.plans.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Grade de Planos IPTV",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Eles auxiliam no preenchimento rápido ao cadastrar novos clientes.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (plans.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhum plano cadastrado ainda.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(plans) { plan ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    plan.name,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Validade: ${plan.durationDays} dias",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "R$ %.2f".format(plan.price),
                                    fontWeight = FontWeight.Black,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (viewModel.hasAdminPermission()) {
                                    Spacer(modifier = Modifier.width(12.dp))
                                    IconButton(onClick = { viewModel.deletePlan(plan) }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Excluir Plano",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

// --- TAB - FINANÇAS E GRÁFICOS ---

@Composable
fun FinancasTabContent(viewModel: IptvViewModel) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val revenue by viewModel.dashboardRevenue.collectAsStateWithLifecycle()
    val rawClients by viewModel.clients.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                "Desempenho Financeiro Detalhado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Revenue Glowing Banner Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Faturamento Histórico Acumulado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "R$ %.2f".format(revenue),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Adquiridos de ${rawClients.size} clientes ativos totais",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // CANVAS NATIVE GRAPHS
        item {
            Text(
                "Faturamento por Servidor (Distribuição %)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Donut Piechart representation
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val serverEarnings = mutableMapOf<String, Double>()
                    transactions.forEach {
                        serverEarnings[it.server] = (serverEarnings[it.server] ?: 0.0) + it.amount
                    }

                    if (serverEarnings.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp), contentAlignment = Alignment.Center
                        ) {
                            Text("Aguardando primeiras transações de recibo...", style = MaterialTheme.typography.bodySmall)
                        }
                    } else {
                        val totalTxs = serverEarnings.values.sum()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            // Donut Drawing Canvas
                            Canvas(modifier = Modifier.size(130.dp)) {
                                var startAngle = 0f
                                serverEarnings.entries.forEach { entry ->
                                    val sweep = ((entry.value / totalTxs) * 360f).toFloat()

                                    val col = PREDEFINED_SERVERS.find {
                                        it.name.equals(entry.key, ignoreCase = true)
                                    }?.color ?: Color(0xFF607D8B)

                                    drawArc(
                                        color = col,
                                        startAngle = startAngle,
                                        sweepAngle = sweep,
                                        useCenter = false,
                                        style = Stroke(width = 30f, cap = StrokeCap.Round)
                                    )
                                    startAngle += sweep
                                }
                            }

                            // Dynamic Legend list
                            Column {
                                serverEarnings.entries.take(4).forEach { entry ->
                                    val col = PREDEFINED_SERVERS.find {
                                        it.name.equals(entry.key, ignoreCase = true)
                                    }?.color ?: Color(0xFF607D8B)

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 3.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(col)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "${entry.key}: R$ %.2f".format(entry.value),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // MONTHLY PERFORMANCE BAR CHART
        item {
            Text(
                "Desempenho de Renovação Mensal",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    val monthlyTotals = listOf(450f, 620f, 890f, 1250f, 1580f, revenue.toFloat())
                    val monthLabels = listOf("Dez", "Jan", "Fev", "Mar", "Abr", "Maio")

                    Text(
                        "Tendência Ascendente de Ganhos Actumais",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Real native bar charts drawn on Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        val maxVal = monthlyTotals.maxOrNull() ?: 1000f
                        val widthGap = size.width / (monthlyTotals.size)
                        val paintText = android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 24f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }

                        // Grid Lines
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                            strokeWidth = 2f
                        )
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            start = androidx.compose.ui.geometry.Offset(0f, size.height / 2),
                            end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2),
                            strokeWidth = 2f
                        )

                        monthlyTotals.forEachIndexed { index, value ->
                            val percent = value / maxVal
                            val barHeight = (size.height - 40f) * percent
                            val barWidth = 35f
                            val xOffset = index * widthGap + (widthGap / 2) - (barWidth / 2)
                            val yOffset = size.height - barHeight - 30f

                            // Draw beautiful rounded bar columns
                            drawRoundRect(
                                color = if (index == monthlyTotals.size - 1) Color(0xFF2E7D32) else Color(0xFF81D4FA),
                                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                                topLeft = androidx.compose.ui.geometry.Offset(xOffset, yOffset),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                            )

                            // Labels at bottom
                            drawContext.canvas.nativeCanvas.drawText(
                                monthLabels[index],
                                xOffset + (barWidth / 2),
                                size.height - 5f,
                                paintText
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // RECENT TRANSACTION STATEMENT
        item {
            Text(
                "Histórico de Caixa Recente (Recibos)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (transactions.isEmpty()) {
            item {
                Text(
                    "Nenhuma transação financeira processada.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(transactions.take(15)) { tx ->
                val dateFmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(tx.date))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (tx.type == "RECIBO") Icons.Default.VerticalAlignBottom else Icons.Default.Launch,
                                contentDescription = "Sinal",
                                tint = if (tx.type == "RECIBO") Color(0xFF2E7D32) else Color(0xFF0288D1),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    tx.clientName,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Servidor: ${tx.server} • $dateFmt",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                        Text(
                            "+ R$ %.2f".format(tx.amount),
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// --- TAB - BACKUP E CONFIGURAÇÃO ---

@Composable
fun BackupTabContent(viewModel: IptvViewModel) {
    val context = LocalContext.current
    val cloudSyncState by viewModel.cloudSyncState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val pdfExportPath by viewModel.pdfExportPath.collectAsStateWithLifecycle()

    val template by viewModel.reminderTemplate.collectAsStateWithLifecycle()
    val suspensionTemplate by viewModel.suspensionTemplate.collectAsStateWithLifecycle()
    val renewalTemplate by viewModel.renewalTemplate.collectAsStateWithLifecycle()
    val pix by viewModel.pixKey.collectAsStateWithLifecycle()

    var manualJsonText by remember { mutableStateOf("") }
    var showImportArea by remember { mutableStateOf(false) }

    // Launcher to save backup as JSON file directly into device's local memory/directories
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val jsonStr = viewModel.exportBackupJson(context)
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(jsonStr)
                    }
                }
                Toast.makeText(context, "Backup salvo com sucesso no armazenamento do dispositivo!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Erro ao exportar arquivo: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Launcher to select and read backup JSON file from device's local memory/directories
    val getContentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        val stringBuilder = StringBuilder()
                        var line: String? = reader.readLine()
                        while (line != null) {
                            stringBuilder.append(line)
                            line = reader.readLine()
                        }
                        manualJsonText = stringBuilder.toString()
                        showImportArea = true
                        Toast.makeText(context, "Backup carregado do dispositivo! Selecione uma opção abaixo.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erro ao abrir arquivo do dispositivo: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Administração & Segurança",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Ferramentas de backup, exportação de arquivos PDF e mensagens automáticas.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        // PDF REPORT CARD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Exportar Dados Comerciais",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Gera um documento PDF formatado completo com a listagem de clientes ativos, vencimentos e dados do caixa atual.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.generatePdfReport(context) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF Icon")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Exportar Relatório PDF", fontWeight = FontWeight.Bold)
                    }

                    if (pdfExportPath != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    "Arquivo PDF Salvo!",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    pdfExportPath ?: "",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                TextButton(onClick = { viewModel.clearPdfPath() }) {
                                    Text("Entendido", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }
        }

        // CLOUD SYNC & LOCAL JSON BACKUP CARD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Backup e Restauração (Armazenamento Local)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Salve os dados dos clientes diretamente na memória do seu aparelho (.json) ou importe um arquivo de backup para restaurar ou adicionar novos clientes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Cloud Progress simulation (kept for cloud backup simulation)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = cloudSyncState,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        if (isSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            IconButton(onClick = { viewModel.backupToCloud(context) }) {
                                Icon(Icons.Default.CloudUpload, contentDescription = "Enviar", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("💾 ARMAZENAMENTO DO DISPOSITIVO (ARQUIVOS)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                createDocumentLauncher.launch("iptv_backup_$dateStr.json")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Salvar Arquivo", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Salvar Arquivo", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                getContentLauncher.launch("*/*")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = "Ler Arquivo", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ler Arquivo", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("✍️ CÓPIA MANUAL (TEXTO/ÁREA DE TRANSFERÊNCIA)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val jsonStr = viewModel.exportBackupJson(context)
                                manualJsonText = jsonStr
                                showImportArea = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Code, contentDescription = "Ver JSON", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Visualizar JSON", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { showImportArea = !showImportArea },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Digitar", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Digitar JSON", fontSize = 11.sp)
                        }
                    }

                    if (showImportArea) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Dados do Backup JSON carregados. Escolha uma das opções abaixo:",
                            modifier = Modifier.padding(bottom = 6.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = manualJsonText,
                            onValueChange = { manualJsonText = it },
                            placeholder = { Text("Insira o texto JSON...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .padding(vertical = 4.dp),
                            textStyle = MaterialTheme.typography.bodySmall,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // OPTION 1: Add ONLY clients (anyone can do this with write permission, appends/adds)
                            Button(
                                onClick = {
                                    if (manualJsonText.isNotEmpty()) {
                                        val ok = viewModel.importOnlyClients(context, manualJsonText)
                                        if (ok) {
                                            showImportArea = false
                                            manualJsonText = ""
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)), // Beautiful feedback green
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = "Adicionar Clientes Icon")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Apenas Adicionar Clientes do Backup", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            // OPTION 2: High power DB rewrite (original admin-only restore)
                            OutlinedButton(
                                onClick = {
                                    if (manualJsonText.isNotEmpty()) {
                                        val ok = viewModel.importBackupJson(context, manualJsonText)
                                        if (ok) {
                                            showImportArea = false
                                            manualJsonText = ""
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Restore, contentDescription = "Restaurar Banco Completo", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ação Administrativa: Restaurar Banco Completo", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // TEXT REMINDERS CONFIGURATION
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Modelos de Mensagens WhatsApp",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Personalize os textos automáticos enviados via WhatsApp para cobranças e avisos.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = pix,
                        onValueChange = { viewModel.pixKey.value = it },
                        label = { Text("Chave Pix de Recebimento") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("1. LEMBRETE / COBRANÇA ATIVA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = template,
                        onValueChange = { viewModel.reminderTemplate.value = it },
                        label = { Text("Texto de Lembrete inicial") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("2. BLOQUEIO / SINAL SUSPENSO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = suspensionTemplate,
                        onValueChange = { viewModel.suspensionTemplate.value = it },
                        label = { Text("Texto de Suspensão") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("3. CONFIRMAÇÃO DE RENOVAÇÃO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = renewalTemplate,
                        onValueChange = { viewModel.renewalTemplate.value = it },
                        label = { Text("Texto de Confirmação") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tags suportadas:\n• {NAME} (Nome) • {PLAN} (Plano) • {SERVER} (Servidor) • {DATE} (Vencimento) • {PRICE} (Valores) • {PIX_KEY} (Sua chave Pix)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// --- DIALOGS (FORMS) ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClientDialog(viewModel: IptvViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val plans by viewModel.plans.collectAsStateWithLifecycle()
    val customServers by viewModel.customServers.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var clientUsername by remember { mutableStateOf("") }
    var clientPassword by remember { mutableStateOf("") }

    // M3U imports state
    var m3uLinkState by remember { mutableStateOf("") }
    var isM3uExpanded by remember { mutableStateOf(false) }

    // Dropdown States
    var serverExpanded by remember { mutableStateOf(false) }
    var selectedServer by remember { mutableStateOf("UniTV") }

    var planExpanded by remember { mutableStateOf(false) }
    var selectedPlan by remember { mutableStateOf<IptvPlan?>(null) }

    // If there are details
    var customPriceText by remember { mutableStateOf("") }
    var customDaysText by remember { mutableStateOf("") }

    var customServerNameText by remember { mutableStateOf("") }
    var isAddingCustomServer by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cadastrar Novo Cliente IPTV", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        confirmButton = {
            Button(
                onClick = {
                    if (name.trim().isEmpty() || phone.trim().isEmpty()) {
                        Toast.makeText(context, "Nome e telefone são campos requeridos!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val finalPlanName = selectedPlan?.name ?: "Personalizado"
                    val finalPrice = customPriceText.toDoubleOrNull() ?: selectedPlan?.price ?: 30.00
                    val finalDays = customDaysText.toIntOrNull() ?: selectedPlan?.durationDays ?: 30

                    viewModel.addClient(
                        name = name.trim(),
                        phone = phone.trim(),
                        provider = selectedServer,
                        planId = selectedPlan?.id ?: 0,
                        planName = finalPlanName,
                        price = finalPrice,
                        durationDays = finalDays,
                        notes = notes.trim(),
                        username = clientUsername.trim(),
                        password = clientPassword.trim()
                    )

                    Toast.makeText(context, "Cliente adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                modifier = Modifier.testTag("confirm_add_client")
            ) {
                Text("Cadastrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isM3uExpanded = !isM3uExpanded },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Link,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Importar via Link M3U",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Icon(
                                    imageVector = if (isM3uExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isM3uExpanded) "Fechar" else "Abrir",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            if (isM3uExpanded) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "Cole o link M3U / Xtream abaixo para autocompletar os campos do cliente:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                OutlinedTextField(
                                    value = m3uLinkState,
                                    onValueChange = { m3uLinkState = it },
                                    label = { Text("Link M3U / Playlist") },
                                    placeholder = { Text("http://servidor:port/get.php?username=...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        val parsed = parseM3uUrl(m3uLinkState)
                                        if (parsed != null) {
                                            if (parsed.username.isNotEmpty()) {
                                                name = parsed.username.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                                                clientUsername = parsed.username
                                            }
                                            if (parsed.password.isNotEmpty()) {
                                                clientPassword = parsed.password
                                            }
                                            if (parsed.server.isNotEmpty()) {
                                                viewModel.addCustomServer(parsed.server)
                                                selectedServer = parsed.server
                                            }
                                            notes = buildString {
                                                appendLine("🔌 Informações de Acesso Importadas:")
                                                appendLine("🖥️ Servidor: ${parsed.server}")
                                                if (parsed.username.isNotEmpty()) appendLine("👤 Usuário: ${parsed.username}")
                                                if (parsed.password.isNotEmpty()) appendLine("🔑 Senha: ${parsed.password}")
                                                appendLine("🔗 Playlist M3U:")
                                                append(m3uLinkState.trim())
                                            }
                                            Toast.makeText(context, "Link M3U analisado! Dados preenchidos.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Verifique o link. Formato não identificado.", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Analisar e Preencher", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome Completo") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_name_field"),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Telefone / WhatsApp") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = clientUsername,
                            onValueChange = { clientUsername = it },
                            label = { Text("Usuário / Login") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = clientPassword,
                            onValueChange = { clientPassword = it },
                            label = { Text("Senha") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                // Servers selection
                item {
                    Text("Escolha o Servidor IPTV:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))

                    ExposedDropdownMenuBox(
                        expanded = serverExpanded,
                        onExpandedChange = { serverExpanded = !serverExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedServer,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serverExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = serverExpanded,
                            onDismissRequest = { serverExpanded = false }
                        ) {
                            customServers.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s) },
                                    onClick = {
                                        selectedServer = s
                                        serverExpanded = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("+ Outro Servidor (Manual)") },
                                onClick = {
                                    isAddingCustomServer = true
                                    serverExpanded = false
                                }
                            )
                        }
                    }
                }

                if (isAddingCustomServer) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = customServerNameText,
                                onValueChange = { customServerNameText = it },
                                label = { Text("Nome do Novo Servidor") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    if (customServerNameText.trim().isNotEmpty()) {
                                        viewModel.addCustomServer(customServerNameText.trim())
                                        selectedServer = customServerNameText.trim()
                                        isAddingCustomServer = false
                                        customServerNameText = ""
                                    }
                                },
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Text("Mais")
                            }
                        }
                    }
                }

                // Plans selection
                item {
                    Text("Escolha o Plano Comercial:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))

                    ExposedDropdownMenuBox(
                        expanded = planExpanded,
                        onExpandedChange = { planExpanded = !planExpanded }
                    ) {
                        val currentText = selectedPlan?.let { "${it.name} (R$ ${it.price})" } ?: "Personalizado..."
                        OutlinedTextField(
                            value = currentText,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = planExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = planExpanded,
                            onDismissRequest = { planExpanded = false }
                        ) {
                            plans.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text("${p.name} - R$ ${p.price}") },
                                    onClick = {
                                        selectedPlan = p
                                        customPriceText = p.price.toString()
                                        customDaysText = p.durationDays.toString()
                                        planExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customPriceText,
                            onValueChange = { customPriceText = it },
                            label = { Text("Preço cobrado (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = customDaysText,
                            onValueChange = { customDaysText = it },
                            label = { Text("Validade (Dias)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Anotações e Links") },
                        placeholder = { Text("Cole links de m3u, teste ou observações...") },
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                }
            }
        }
    )
}

@Suppress("UNUSED_EXPRESSION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClientDialog(client: IptvClient, viewModel: IptvViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val customServers by viewModel.customServers.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf(client.name) }
    var phone by remember { mutableStateOf(client.phone) }
    var notes by remember { mutableStateOf(client.notes) }
    var selectedServer by remember { mutableStateOf(client.provider) }
    var priceStr by remember { mutableStateOf(client.price.toString()) }
    var serverExpanded by remember { mutableStateOf(false) }
    var clientUsername by remember { mutableStateOf(client.username) }
    var clientPassword by remember { mutableStateOf(client.password) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modificar Cliente", fontWeight = FontWeight.Bold) },
        confirmButton = {
            Button(
                onClick = {
                    if (name.trim().isEmpty() || phone.trim().isEmpty()) {
                        return@Button
                    }
                    val updated = client.copy(
                        name = name.trim(),
                        phone = phone.trim(),
                        provider = selectedServer,
                        price = priceStr.toDoubleOrNull() ?: client.price,
                        notes = notes.trim(),
                        username = clientUsername.trim(),
                        password = clientPassword.trim()
                    )
                    viewModel.updateClient(updated)
                    Toast.makeText(context, "Cliente atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            ) {
                Text("Atualizar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do cliente") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("WhatsApp") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = clientUsername,
                        onValueChange = { clientUsername = it },
                        label = { Text("Usuário / Login") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = clientPassword,
                        onValueChange = { clientPassword = it },
                        label = { Text("Senha") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Preço acordado (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Server choices dropdown
                ExposedDropdownMenuBox(
                    expanded = serverExpanded,
                    onExpandedChange = { serverExpanded = !serverExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedServer,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serverExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = serverExpanded,
                        onDismissRequest = { serverExpanded = false }
                    ) {
                        customServers.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    selectedServer = s
                                    serverExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observação e Links Úteis") },
                    modifier = Modifier.fillMaxWidth().height(80.dp)
                )
            }
        }
    )
}

@Composable
fun RenewClientDialog(client: IptvClient, viewModel: IptvViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var renewDaysText by remember { mutableStateOf("30") }
    var priceText by remember { mutableStateOf(client.price.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Renovar Sinal - Receber de ${client.name}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val days = renewDaysText.toIntOrNull() ?: 30
                    val amount = priceText.toDoubleOrNull() ?: client.price
                    viewModel.renewClient(client, days, amount)
                    Toast.makeText(context, "Sinal renovado e pagamento computado no caixa!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            ) {
                Text("Confirmar Pagamento")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "A validade será acrescida a partir do vencimento atual ou da data de hoje caso já esteja vencido.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                OutlinedTextField(
                    value = renewDaysText,
                    onValueChange = { renewDaysText = it },
                    label = { Text("Prorrogar por (Dias)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Valor Recebido R$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
fun AddPlanDialog(viewModel: IptvViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var daysText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar Novo Plano Comercial", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceText.toDoubleOrNull() ?: 0.0
                    val d = daysText.toIntOrNull() ?: 30
                    if (name.trim().isNotEmpty() && p > 0.0) {
                        viewModel.addPlan(name.trim(), p, d)
                        Toast.makeText(context, "Plano cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    } else {
                        Toast.makeText(context, "Preencha corretamente os detalhes do plano!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.testTag("confirm_add_plan")
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do Plano (ex: Mensal Completo)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Valor Mensura (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = daysText,
                    onValueChange = { daysText = it },
                    label = { Text("Duração (Dias como 30, 365...)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
fun MessageOptionsDialog(
    client: IptvClient,
    viewModel: IptvViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val pixKey by viewModel.pixKey.collectAsStateWithLifecycle()
    val reminderTpl by viewModel.reminderTemplate.collectAsStateWithLifecycle()
    val suspensionTpl by viewModel.suspensionTemplate.collectAsStateWithLifecycle()
    val renewalTpl by viewModel.renewalTemplate.collectAsStateWithLifecycle()

    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(client.dueDate))
    val priceFormatted = "%.2f".format(client.price)

    // Helper formatting function to replace tags in user templates
    val formatMsg = { tpl: String ->
        tpl.replace("{NAME}", client.name, ignoreCase = true)
           .replace("{SERVER}", client.provider, ignoreCase = true)
           .replace("{PLAN}", client.planName, ignoreCase = true)
           .replace("{DATE}", formattedDate, ignoreCase = true)
           .replace("{PRICE}", priceFormatted, ignoreCase = true)
           .replace("{PIX_KEY}", pixKey, ignoreCase = true)
    }

    val templates = listOf(
        Triple(
            "Cobrança Básica (Lembrete Leve)",
            "Olá Tudo bem? Passando para lembrar que seu teste/assinatura IPTV vence dia $formattedDate. Ficamos muito felizes de tê-lo conosco! Atte.",
            "Lembrete inicial leve para enviar antes do prazo."
        ),
        Triple(
            "Lembrete Detalhado com Pix (Cobrança Ativa)",
            formatMsg(reminderTpl),
            "Sua mensagem customizada para faturas e lembretes comPix ativo."
        ),
        Triple(
            "Suficiência / Aviso de Sinal Suspenso",
            formatMsg(suspensionTpl),
            "Mensagem customizada enviada quando o sinal do cliente é bloqueado."
        ),
        Triple(
            "Confirmação de Renovação com Sucesso",
            formatMsg(renewalTpl),
            "Mensagem amigável de sucesso enviada após receber o Pix do cliente."
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "WhatsApp Options",
                    tint = Color(0xFF25D366),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Notificações WhatsApp", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 450.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(templates) { (title, content, description) ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = description,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            // Preview content
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = content,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    try {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, content)
                                            type = "text/plain"
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        val shareIntent = Intent.createChooser(sendIntent, "Enviar via:").apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(shareIntent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Erro ao compartilhar", Toast.LENGTH_SHORT).show()
                                    }
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(32.dp).align(Alignment.End),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share, 
                                    contentDescription = "Enviar", 
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Compartilhar", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun IptvConnectionDetailsDialog(
    client: IptvClient,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var serverUrl by remember { mutableStateOf("http://suaurl.vip:8080") }

    val m3uUrl = "$serverUrl/get.php?username=${client.username}&password=${client.password}&type=m3u_plus&output=ts"
    val epgUrl = "$serverUrl/xmltv.php?username=${client.username}&password=${client.password}"
    
    val xtreamCardText = """
📺 *DADOS DE SEU ACESSO IPTV* 📺

🌐 *Servidor:* $serverUrl
🔑 *Usuário:* ${client.username}
🔒 *Senha:* ${client.password}

🔌 *Instruções de Login:*
Abra o aplicativo IPTV de sua preferência (ex: XCIPTV, SmartUp, IPTV Smarters) e escolha a opção *Xtream Codes API* para inserir estes dados.
    """.trimIndent()

    val copyToClipboard = { label: String, text: String ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copiado!", Toast.LENGTH_SHORT).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tv,
                    contentDescription = "Configurações IPTV",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gerador de Conexões IPTV", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Fechar", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Configuração do Servidor:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = serverUrl,
                    onValueChange = { serverUrl = it },
                    label = { Text("URL / DNS do Servidor") },
                    placeholder = { Text("Ex: http://dnsdoservidor.xyz:8080") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // --- 1. M3U Card ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Formato M3U Plus (Playlist)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            IconButton(
                                onClick = { copyToClipboard("Link M3U", m3uUrl) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copiar M3U", modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = m3uUrl,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontSize = 9.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                // --- 2. Xtream Codes Card ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Credenciais Xtream API", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            IconButton(
                                onClick = { copyToClipboard("Card de Acesso", xtreamCardText) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copiar Card", modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "URL: $serverUrl\nUser: ${client.username}\nPass: ${client.password}",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                // --- 3. EPG Document Card ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Guia de Programação (EPG)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            IconButton(
                                onClick = { copyToClipboard("Link EPG", epgUrl) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copiar EPG", modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = epgUrl,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontSize = 9.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        try {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, xtreamCardText)
                                type = "text/plain"
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Enviar Card de Acesso:").apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(shareIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Erro ao compartilhar", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Enviar", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviar Card de Acesso", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObservacoesTabContent(viewModel: IptvViewModel) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    
    var selectedNoteForEdit by remember { mutableStateOf<com.example.data.IptvNote?>(null) }
    var showAddNoteDialog by remember { mutableStateOf(false) }

    // Filter notes by title or content
    val filteredNotes = notes.filter {
        searchQuery.isEmpty() || 
        it.title.contains(searchQuery, ignoreCase = true) || 
        it.content.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bloco de Notas Geral",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Anotações gerais, templates de mensagens e dados sem vínculo com nenhum cliente específico.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            if (viewModel.hasWritePermission()) {
                FilledTonalButton(
                    onClick = { showAddNoteDialog = true },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nova Nota", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nova", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Filtrar notas por título ou anotação...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpar")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        if (filteredNotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Sem resultados",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (searchQuery.isEmpty()) "Nenhuma anotação geral cadastrada." else "Nenhum resultado para a busca.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredNotes) { note ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = note.title,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    val dateNice = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(note.dateModified))
                                    Text(
                                        text = "Modificado: $dateNice",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                                
                                Row {
                                    if (viewModel.hasWritePermission()) {
                                        IconButton(
                                            onClick = { selectedNoteForEdit = note },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Editar Nota",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    if (viewModel.hasAdminPermission()) {
                                        IconButton(
                                            onClick = {
                                                viewModel.deleteNote(note)
                                                Toast.makeText(context, "Nota excluída com sucesso!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Deletar",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = note.content,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            val urls = extractUrls(note.content)
                            if (urls.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Link,
                                        contentDescription = "Links detectados",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Links detectados na nota:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(modifier = Modifier.padding(start = 20.dp, top = 4.dp)) {
                                    urls.forEach { url ->
                                        val cleanUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                            "https://$url"
                                        } else url
                                        Row(
                                            modifier = Modifier
                                                .clickable {
                                                    try {
                                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cleanUrl)).apply {
                                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        }
                                                        context.startActivity(intent)
                                                    } catch (e: Exception) {
                                                        Toast.makeText(context, "Navegador não encontrado", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Launch,
                                                contentDescription = "Abrir Link",
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = url,
                                                color = MaterialTheme.colorScheme.secondary,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                textDecoration = TextDecoration.Underline,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog: Nova Nota
    if (showAddNoteDialog) {
        var noteTitle by remember { mutableStateOf("") }
        var noteContent by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            title = {
                Text(
                    text = "Criar Nova Nota Geral",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("Título da Nota") },
                        placeholder = { Text("ex: Servidores Modelo, Avisos, etc...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Conteúdo / Links") },
                        placeholder = { Text("Cole links de listas gerais, tutoriais de clientes...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        textStyle = MaterialTheme.typography.bodySmall,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteTitle.trim().isEmpty() || noteContent.trim().isEmpty()) {
                            Toast.makeText(context, "Título e conteúdo são necessários!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.addNote(noteTitle.trim(), noteContent.trim())
                        showAddNoteDialog = false
                        Toast.makeText(context, "Nota salva com sucesso!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Salvar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddNoteDialog = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    // Dialog: Editar Nota
    if (selectedNoteForEdit != null) {
        val editingNote = selectedNoteForEdit!!
        var noteTitle by remember { mutableStateOf(editingNote.title) }
        var noteContent by remember { mutableStateOf(editingNote.content) }

        AlertDialog(
            onDismissRequest = { selectedNoteForEdit = null },
            title = {
                Text(
                    text = "Editar Nota",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("Título da Nota") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Conteúdo / Links") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        textStyle = MaterialTheme.typography.bodySmall,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteTitle.trim().isEmpty() || noteContent.trim().isEmpty()) {
                            Toast.makeText(context, "Título e conteúdo são necessários!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.updateNote(editingNote.copy(title = noteTitle.trim(), content = noteContent.trim(), dateModified = System.currentTimeMillis()))
                        selectedNoteForEdit = null
                        Toast.makeText(context, "Nota editada com sucesso!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Atualizar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedNoteForEdit = null }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }
}

fun parseM3uUrl(url: String): ParsedM3uComponents? {
    val trimmed = url.trim()
    if (trimmed.isEmpty()) return null
    try {
        val decodedUrl = java.net.URLDecoder.decode(trimmed, "UTF-8")
        
        val hostRegex = Regex("""https?://([^:/\s]+)(:\d+)?""")
        val hostMatch = hostRegex.find(decodedUrl)
         val host = hostMatch?.groupValues?.get(1) ?: "Servidor"
        val port = hostMatch?.groupValues?.get(2) ?: ""
        val serverName = "$host$port"

        val usernameRegex = Regex("""[?&](username|user)=([^&\s]+)""")
        val usernameMatch = usernameRegex.find(decodedUrl)
        val username = usernameMatch?.groupValues?.get(2) ?: ""

        val passwordRegex = Regex("""[?&](password|pass)=([^&\s]+)""")
        val passwordMatch = passwordRegex.find(decodedUrl)
         val password = passwordMatch?.groupValues?.get(2) ?: ""

        return ParsedM3uComponents(
            server = serverName,
            username = username,
            password = password
        )
    } catch (e: Exception) {
        try {
            if (trimmed.contains("?") && trimmed.contains("=")) {
                val queryPart = trimmed.substringAfter("?")
                val hostPart = trimmed.substringBefore("?").substringAfter("://")
                val serverName = hostPart.substringBefore("/")
                
                var username = ""
                var password = ""
                queryPart.split("&").forEach { param ->
                    val parts = param.split("=")
                    if (parts.size == 2) {
                        val key = parts[0].lowercase()
                        val valStr = parts[1]
                        if (key == "username" || key == "user") username = valStr
                        if (key == "password" || key == "pass") password = valStr
                    }
                }
                if (username.isNotEmpty()) {
                    return ParsedM3uComponents(server = serverName, username = username, password = password)
                }
            }
        } catch (e2: Exception) {
            // ignored
        }
        return null
    }
}

data class ParsedM3uComponents(
    val server: String,
    val username: String,
    val password: String
)
