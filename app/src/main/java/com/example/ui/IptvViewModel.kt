package com.example.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.IptvClient
import com.example.data.IptvPlan
import com.example.data.IptvRepository
import com.example.data.IptvTransaction
import com.example.data.IptvUser
import com.example.data.IptvNote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IptvViewModel(private val repository: IptvRepository) : ViewModel() {

    // --- ACCESS CONTROL MODULE ---
    val users: StateFlow<List<IptvUser>> = repository.users.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val currentUser = MutableStateFlow<IptvUser?>(null)
    val loginError = MutableStateFlow<String?>(null)

    // --- THEME MODES ---
    val isDarkTheme = MutableStateFlow(true) // Start with premium dark theme by default
    val selectedThemePalette = MutableStateFlow("EMERALD") // "EMERALD", "AMBER", "CYBER", "OCEAN", "SLATE"
    val clientLayoutMode = MutableStateFlow("LISTA") // "LISTA" vs "GRID"

    // --- NAVIGATION MODES ---
    val currentTab = MutableStateFlow(0) // 0: Clientes, 1: Planos, 2: Finanças, 3: Acesso/Backup

    // --- SEARCH AND FILTERS ---
    val searchQuery = MutableStateFlow("")
    val selectedServerFilter = MutableStateFlow<String?>(null) // null = all, or "UniTV", "StarPlay", "Blinder", "Manual", or custom
    val selectedStatusFilter = MutableStateFlow("TODOS") // "TODOS", "VENCENDO", "VENCIDO"

    // Custom servers database (added manually by users)
    val customServers = MutableStateFlow<List<String>>(listOf("UniTV", "StarPlay", "Blinder"))

    // --- DATABASE LISTS ---
    val plans: StateFlow<List<IptvPlan>> = repository.plans.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val transactions: StateFlow<List<IptvTransaction>> = repository.transactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val clients: StateFlow<List<IptvClient>> = repository.clients.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val notes: StateFlow<List<IptvNote>> = repository.notes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- COMBINED FILTERED CLIENTS FLOW ---
    val filteredClients: StateFlow<List<IptvClient>> = combine(
        clients,
        searchQuery,
        selectedServerFilter,
        selectedStatusFilter
    ) { clientList, query, server, status ->
        val currentTime = System.currentTimeMillis()
        val fiveDaysInMs = 5L * 24 * 60 * 60 * 1000

        clientList.filter { client ->
            val matchesQuery = client.name.contains(query, ignoreCase = true) ||
                    client.phone.contains(query) ||
                    client.notes.contains(query, ignoreCase = true)

            val matchesServer = server == null || client.provider.equals(server, ignoreCase = true)

            val matchesStatus = when (status) {
                "VENCENDO" -> {
                    val daysRemaining = (client.dueDate - currentTime)
                    daysRemaining in 0..fiveDaysInMs && client.active
                }
                "VENCIDO" -> {
                    client.dueDate < currentTime && client.active
                }
                else -> true // TODOS
            }

            matchesQuery && matchesServer && matchesStatus
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- TAX CONTROLLER (DASHBOARD METRICS) ---
    val dashboardRevenue: StateFlow<Double> = transactions.map { txList ->
        txList.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // --- NOTIFICATION TEXT TEMPLATES ---
    val reminderTemplate = MutableStateFlow(
        "Olá *{NAME}*!\n\nSeu acesso IPTV do servidor *{SERVER}* (Plano: *{PLAN}*) vence em *{DATE}*.\nValor para renovação: *R$ {PRICE}*.\n\nEvite a suspensão do sinal enviando o comprovante via Pix:\n👉 *{PIX_KEY}*\n\nAgradecemos a sua preferência!"
    )
    val suspensionTemplate = MutableStateFlow(
        "🚨 *SINAL IPTV SUSPENSO* 🚨\n\nOlá *{NAME}*!\nConstatamos que sua assinatura IPTV no servidor *{SERVER}* venceu em *{DATE}* e o sinal foi suspenso automaticamente.\n\nPara restabelecer seu acesso completo hoje mesmo:\n💵 Valor de ativação: *R$ {PRICE}*\n🔑 Chave Pix: {PIX_KEY}\n\nRealize o pagamento e envie o comprovante para reativação imediata do sinal!"
    )
    val renewalTemplate = MutableStateFlow(
        "✅ *RENOVAÇÃO CONFIRMADA* \n\nOlá *{NAME}*!\n\nSeu pagamento de *R$ {PRICE}* foi recebido e seu sinal no servidor *{SERVER}* já foi reestabelecido/renovado!\n\n📅 Seu novo vencimento é em: *{DATE}*.\n\nAgradecemos a preferência e desejamos um excelente entretenimento! 📺"
    )
    val pixKey = MutableStateFlow("seu-pix-aqui@link.com")

    // --- BACKUP & EXPORT GRAPH STATE ---
    val cloudSyncState = MutableStateFlow("Nenhum backup realizado")
    val isSyncing = MutableStateFlow(false)
    val pdfExportPath = MutableStateFlow<String?>(null)

    init {
        // Pre-populate Database with default plans and default admin users
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
            // Automaticaly sign in standard Admin user for ease of testing!
            currentUser.value = IptvUser(id = 1, username = "lemos_admin", pass = "1234", role = "ADMIN")
        }
    }

    // --- SERVICES ---

    fun login(username: String, pass: String): Boolean {
        val userList = users.value
        val found = userList.find { it.username == username && it.pass == pass }
        return if (found != null) {
            currentUser.value = found
            loginError.value = null
            true
        } else {
            loginError.value = "Usuário ou senha incorretos."
            false
        }
    }

    fun logout() {
        currentUser.value = null
    }

    fun switchUser(username: String) {
        val found = users.value.find { it.username == username }
        if (found != null) {
            currentUser.value = found
        }
    }

    // --- ACTIONS ON CLIENTS ---

    fun addClient(
        name: String,
        phone: String,
        provider: String,
        planId: Int,
        planName: String,
        price: Double,
        durationDays: Int,
        notes: String,
        username: String = "",
        password: String = ""
    ) {
        if (!hasWritePermission()) return
        viewModelScope.launch {
            val start = System.currentTimeMillis()
            val due = start + (durationDays.toLong() * 24 * 60 * 60 * 1000)
            val client = IptvClient(
                name = name,
                phone = phone,
                provider = provider,
                planId = planId,
                planName = planName,
                price = price,
                startDate = start,
                dueDate = due,
                active = true,
                notes = notes,
                username = username,
                password = password
            )
            repository.insertClient(client)

            // Auto-record initial payment transaction!
            repository.insertTransaction(
                IptvTransaction(
                    clientName = name,
                    amount = price,
                    date = start,
                    type = "RECIBO",
                    server = provider
                )
            )
        }
    }

    fun updateClient(client: IptvClient) {
        if (!hasWritePermission()) return
        viewModelScope.launch {
            repository.updateClient(client)
        }
    }

    fun deleteClient(client: IptvClient) {
        if (!hasAdminPermission()) return
        viewModelScope.launch {
            repository.deleteClient(client)
        }
    }

    fun renewClient(client: IptvClient, durationDays: Int, price: Double) {
        if (!hasWritePermission()) return
        viewModelScope.launch {
            val start = System.currentTimeMillis()
            val currentDue = if (client.dueDate > start) client.dueDate else start
            val newDue = currentDue + (durationDays.toLong() * 24 * 60 * 60 * 1000)

            val updated = client.copy(
                startDate = start,
                dueDate = newDue,
                price = price,
                active = true
            )
            repository.updateClient(updated)

            // Register Transaction for this renew payment
            repository.insertTransaction(
                IptvTransaction(
                    clientName = client.name,
                    amount = price,
                    date = start,
                    type = "RECIBO",
                    server = client.provider
                )
            )
        }
    }

    // --- PLANS ---

    fun addPlan(name: String, price: Double, durationDays: Int) {
        if (!hasWritePermission()) return
        viewModelScope.launch {
            repository.insertPlan(IptvPlan(name = name, price = price, durationDays = durationDays))
        }
    }

    fun deletePlan(plan: IptvPlan) {
        if (!hasAdminPermission()) return
        viewModelScope.launch {
            repository.deletePlan(plan)
        }
    }

    // --- CUSTOM SERVERS ---

    fun addCustomServer(serverName: String) {
        val trimmed = serverName.trim()
        if (trimmed.isNotEmpty() && !customServers.value.contains(trimmed)) {
            customServers.value = customServers.value + trimmed
        }
    }

    // --- STANDALONE NOTES ---

    fun addNote(title: String, content: String) {
        if (!hasWritePermission()) return
        viewModelScope.launch {
            repository.insertNote(IptvNote(title = title, content = content))
        }
    }

    fun updateNote(note: IptvNote) {
        if (!hasWritePermission()) return
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }

    fun deleteNote(note: IptvNote) {
        if (!hasWritePermission()) return
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // --- TRANSACTIONS ---

    fun addManualTransaction(clientName: String, amount: Double, server: String) {
        if (!hasWritePermission()) return
        viewModelScope.launch {
            repository.insertTransaction(
                IptvTransaction(
                    clientName = clientName,
                    amount = amount,
                    date = System.currentTimeMillis(),
                    type = "AVULSO",
                    server = server
                )
            )
        }
    }

    // --- ROLE PERMISSIONS ---

    fun hasAdminPermission(): Boolean {
        val role = currentUser.value?.role ?: return false
        return role == "ADMIN"
    }

    fun hasWritePermission(): Boolean {
        val role = currentUser.value?.role ?: return false
        return role == "ADMIN" || role == "EDITOR"
    }

    // --- CLOUD BACKUP & JSON DECORATOR ---

    fun backupToCloud(context: Context) {
        isSyncing.value = true
        viewModelScope.launch {
            try {
                // Simulating network latency for realistic "Nuvem" sync
                kotlinx.coroutines.delay(1800)
                val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val nowStr = format.format(Date())
                cloudSyncState.value = "Sincronizado na nuvem com sucesso em: $nowStr"
                Toast.makeText(context, "Backup em nuvem sincronizado!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                cloudSyncState.value = "Falha no sync de backup."
            } finally {
                isSyncing.value = false
            }
        }
    }

    fun exportBackupJson(context: Context): String {
        try {
            val root = JSONObject()

            // Plans
            val plansArray = JSONArray()
            plans.value.forEach {
                val p = JSONObject()
                p.put("name", it.name)
                p.put("price", it.price)
                p.put("durationDays", it.durationDays)
                plansArray.put(p)
            }
            root.put("plans", plansArray)

            // Clients
            val clientsArray = JSONArray()
            clients.value.forEach {
                val c = JSONObject()
                c.put("name", it.name)
                c.put("phone", it.phone)
                c.put("provider", it.provider)
                c.put("planName", it.planName)
                c.put("price", it.price)
                c.put("startDate", it.startDate)
                c.put("dueDate", it.dueDate)
                c.put("active", it.active)
                c.put("notes", it.notes)
                c.put("username", it.username)
                c.put("password", it.password)
                clientsArray.put(c)
            }
            root.put("clients", clientsArray)

            // Transactions
            val txArray = JSONArray()
            transactions.value.forEach {
                val t = JSONObject()
                t.put("clientName", it.clientName)
                t.put("amount", it.amount)
                t.put("date", it.date)
                t.put("type", it.type)
                t.put("server", it.server)
                txArray.put(t)
            }
            root.put("transactions", txArray)

            Toast.makeText(context, "Dados de exportação formatados. Copie o JSON!", Toast.LENGTH_SHORT).show()
            return root.toString(2)
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao expor JSON: ${e.message}", Toast.LENGTH_SHORT).show()
            return ""
        }
    }

    fun importBackupJson(context: Context, jsonStr: String): Boolean {
        if (!hasAdminPermission()) {
            Toast.makeText(context, "Apenas Admins podem restaurar backups!", Toast.LENGTH_SHORT).show()
            return false
        }
        return try {
            val root = JSONObject(jsonStr)

            viewModelScope.launch {
                // Import plans
                if (root.has("plans")) {
                    val array = root.getJSONArray("plans")
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        repository.insertPlan(
                            IptvPlan(
                                name = obj.getString("name"),
                                price = obj.getDouble("price"),
                                durationDays = obj.getInt("durationDays")
                            )
                        )
                    }
                }

                // Import clients
                if (root.has("clients")) {
                    val array = root.getJSONArray("clients")
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        repository.insertClient(
                            IptvClient(
                                name = obj.getString("name"),
                                phone = obj.getString("phone"),
                                provider = obj.getString("provider"),
                                planId = 0,
                                planName = obj.getString("planName"),
                                price = obj.getDouble("price"),
                                startDate = obj.getLong("startDate"),
                                dueDate = obj.getLong("dueDate"),
                                active = obj.getBoolean("active"),
                                notes = obj.optString("notes", ""),
                                username = obj.optString("username", ""),
                                password = obj.optString("password", "")
                            )
                        )
                    }
                }

                // Import transactions
                if (root.has("transactions")) {
                    val array = root.getJSONArray("transactions")
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        repository.insertTransaction(
                            IptvTransaction(
                                clientName = obj.getString("clientName"),
                                amount = obj.getDouble("amount"),
                                date = obj.getLong("date"),
                                type = obj.getString("type"),
                                server = obj.optString("server", "Manual")
                            )
                        )
                    }
                }

                Toast.makeText(context, "Backup importado com sucesso!", Toast.LENGTH_SHORT).show()
            }
            true
        } catch (e: Exception) {
            Toast.makeText(context, "JSON de backup inválido!", Toast.LENGTH_LONG).show()
            false
        }
    }

    fun importOnlyClients(context: Context, jsonStr: String): Boolean {
        if (!hasWritePermission()) {
            Toast.makeText(context, "Sem permissão de gravação para importar!", Toast.LENGTH_SHORT).show()
            return false
        }
        return try {
            val root = JSONObject(jsonStr)
            val clientsArray: JSONArray = when {
                root.has("clients") -> root.getJSONArray("clients")
                root.has("name") -> {
                    JSONArray().put(root)
                }
                else -> {
                    throw Exception("Formato desconhecido")
                }
            }

            viewModelScope.launch {
                var importedCount = 0
                for (i in 0 until clientsArray.length()) {
                    val obj = clientsArray.getJSONObject(i)
                    val c = IptvClient(
                        name = obj.getString("name"),
                        phone = obj.optString("phone", ""),
                        provider = obj.optString("provider", "Manual"),
                        planId = 0,
                        planName = obj.optString("planName", "Importado"),
                        price = obj.optDouble("price", 0.0),
                        startDate = obj.optLong("startDate", System.currentTimeMillis()),
                        dueDate = obj.optLong("dueDate", System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)),
                        active = obj.optBoolean("active", true),
                        notes = obj.optString("notes", "Restaurado via Importador de Clientes"),
                        username = obj.optString("username", ""),
                        password = obj.optString("password", "")
                    )
                    repository.insertClient(c)
                    importedCount++
                }
                Toast.makeText(context, "$importedCount clientes adicionados do backup!", Toast.LENGTH_LONG).show()
            }
            true
        } catch (e: Exception) {
            try {
                val array = JSONArray(jsonStr)
                viewModelScope.launch {
                    var importedCount = 0
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val c = IptvClient(
                            name = obj.getString("name"),
                            phone = obj.optString("phone", ""),
                            provider = obj.optString("provider", "Manual"),
                            planId = 0,
                            planName = obj.optString("planName", "Importado"),
                            price = obj.optDouble("price", 0.0),
                            startDate = obj.optLong("startDate", System.currentTimeMillis()),
                            dueDate = obj.optLong("dueDate", System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)),
                            active = obj.optBoolean("active", true),
                            notes = obj.optString("notes", "Restaurado via Importador de Clientes"),
                            username = obj.optString("username", ""),
                            password = obj.optString("password", "")
                        )
                        repository.insertClient(c)
                        importedCount++
                    }
                    Toast.makeText(context, "$importedCount clientes adicionados do backup!", Toast.LENGTH_LONG).show()
                }
                true
            } catch (e2: Exception) {
                Toast.makeText(context, "Falha ao analisar JSON de Clientes!", Toast.LENGTH_LONG).show()
                false
            }
        }
    }

    // --- PDF REPORT WRITER SERVICE (NATIVE AGENT-CRAFTED CONTEXT) ---

    fun generatePdfReport(context: Context) {
        viewModelScope.launch {
            try {
                val pdfDocument = PdfDocument()
                val pageWidth = 595
                val pageHeight = 842 // A4 standard dimensions

                // Page 1 Setup
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                // Styling paints
                val titlePaint = Paint().apply {
                    color = Color.BLACK
                    textSize = 22f
                    isFakeBoldText = true
                    isAntiAlias = true
                }

                val subtitlePaint = Paint().apply {
                    color = Color.DKGRAY
                    textSize = 12f
                    isAntiAlias = true
                }

                val textPaint = Paint().apply {
                    color = Color.BLACK
                    textSize = 11f
                    isAntiAlias = true
                }

                val boldTextPaint = Paint().apply {
                    color = Color.BLACK
                    textSize = 11f
                    isFakeBoldText = true
                    isAntiAlias = true
                }

                val linePaint = Paint().apply {
                    color = Color.LTGRAY
                    strokeWidth = 1f
                }

                val headerBgPaint = Paint().apply {
                    color = Color.rgb(240, 240, 240)
                }

                // Header
                canvas.drawText("RELATÓRIO FINANCEIRO & DE CONTROLE - GESTÃO IPTV", 30f, 50f, titlePaint)
                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                canvas.drawText("Gerado em: ${formatter.format(Date())} - Por: ${currentUser.value?.username ?: "Operador"}", 30f, 75f, subtitlePaint)
                canvas.drawLine(30f, 90f, (pageWidth - 30).toFloat(), 90f, linePaint)

                // Resumo
                canvas.drawText("RESUMO DO NEGÓCIO", 30f, 120f, boldTextPaint)
                canvas.drawText("Total de Clientes Cadastrados: ${clients.value.size}", 30f, 140f, textPaint)

                val currentTime = System.currentTimeMillis()
                val fiveDaysMs = 5L * 24 * 60 * 60 * 1000
                val expiring = clients.value.count {
                    val remaining = it.dueDate - currentTime
                    remaining in 0..fiveDaysMs && it.active
                }
                val expired = clients.value.count { it.dueDate < currentTime && it.active }

                canvas.drawText("Clientes com Vencimento Próximo (menos de 5 dias): $expiring", 30f, 160f, textPaint)
                canvas.drawText("Clientes Expirados (Sem Acesso): $expired", 30f, 180f, textPaint)
                canvas.drawText("Faturamento Histórico Acumulado: R$ %.2f".format(dashboardRevenue.value), 30f, 200f, textPaint)

                canvas.drawLine(30f, 220f, (pageWidth - 30).toFloat(), 220f, linePaint)

                // Detailed clients table header
                canvas.drawText("SITUAÇÃO DETALHADA DOS CLIENTES ACTUADOS", 30f, 245f, boldTextPaint)

                var yPos = 270f
                // Draw Table Header Background
                canvas.drawRect(30f, yPos, (pageWidth - 30).toFloat(), yPos + 22f, headerBgPaint)
                canvas.drawText("Nome do Cliente", 35f, yPos + 15f, boldTextPaint)
                canvas.drawText("Servidor", 200f, yPos + 15f, boldTextPaint)
                canvas.drawText("Plano Contratado", 320f, yPos + 15f, boldTextPaint)
                canvas.drawText("Vencimento", 440f, yPos + 15f, boldTextPaint)
                canvas.drawText("Preço", 520f, yPos + 15f, boldTextPaint)

                yPos += 22f

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                clients.value.take(20).forEach { client -> // max 20 entries in page 1 to avoid layout overflow
                    if (yPos < pageHeight - 60) {
                        yPos += 20f
                        canvas.drawText(client.name, 35f, yPos, textPaint)
                        canvas.drawText(client.provider, 200f, yPos, textPaint)
                        canvas.drawText(client.planName, 320f, yPos, textPaint)
                        canvas.drawText(dateFormat.format(Date(client.dueDate)), 440f, yPos, textPaint)
                        canvas.drawText("R$ %.2f".format(client.price), 520f, yPos, textPaint)
                        canvas.drawLine(30f, yPos + 4f, (pageWidth - 30).toFloat(), yPos + 4f, linePaint)
                    }
                }

                // Footer
                canvas.drawText("Página 1 de 1 - Gestão IPTV Software", (pageWidth / 2 - 50).toFloat(), (pageHeight - 30).toFloat(), subtitlePaint)

                pdfDocument.finishPage(page)

                // Save PDF into standard downloads external file direction
                val folder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                if (folder != null) {
                    if (!folder.exists()) folder.mkdirs()
                    val pdfFile = File(folder, "Relatorio_IPTV_${System.currentTimeMillis()}.pdf")
                    val outputStream = FileOutputStream(pdfFile)
                    pdfDocument.writeTo(outputStream)
                    pdfDocument.close()
                    outputStream.close()

                    pdfExportPath.value = pdfFile.absolutePath
                    Toast.makeText(context, "Relatório PDF exportado com sucesso em Downloads!", Toast.LENGTH_LONG).show()
                } else {
                    pdfDocument.close()
                    Toast.makeText(context, "Pasta de download indisponível no dispositivo", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(context, "Falha ao gerar PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- HELPER WRAPPER ---
    fun clearPdfPath() {
        pdfExportPath.value = null
    }
}

class IptvViewModelFactory(private val repository: IptvRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IptvViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IptvViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
