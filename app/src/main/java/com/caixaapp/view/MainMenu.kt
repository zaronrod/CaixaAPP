package com.caixaapp.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.caixaapp.controller.TransactionController
import com.caixaapp.databinding.MainMenuBinding
import com.caixaapp.repository.RoomTransactionRepository
import com.caixaapp.util.DatabaseProvider
import com.caixaapp.util.JsonExportService // Import the new service
import kotlinx.coroutines.launch

class MainMenu : AppCompatActivity() {

    private lateinit var binding: MainMenuBinding
    private lateinit var controller: TransactionController
    private lateinit var jsonExportService: JsonExportService // Declare the service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- CORRECT MVC INITIALIZATION ---
        val dao = DatabaseProvider.getDatabase(this).transactionDao()
        controller = TransactionController(RoomTransactionRepository(dao))
        jsonExportService = JsonExportService() // Initialize the service

        // --- UI Event Handlers ---
        setupButtonClickListeners()
    }

    private fun setupButtonClickListeners() {
        // Navigation buttons
        binding.buttonGoToTransaction.setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
        }

        binding.buttonGoToStatement.setOnClickListener {
            startActivity(Intent(this, StatementActivity::class.java))
        }

        binding.buttonGoToChart.setOnClickListener {
            startActivity(Intent(this, ChartActivity::class.java))
        }

        // Action buttons
        binding.buttonExport.setOnClickListener {
            handleExportRequest()
        }

        binding.buttonSync.setOnClickListener {
            showFutureFeatureDialog()
        }

        binding.buttonExit.setOnClickListener {
            finishAffinity() // Closes all activities in the app
        }
    }

    /**
     * Handles the user's request to export data.
     * The business logic is NOT in the Activity.
     */
    private fun handleExportRequest() {
        lifecycleScope.launch {
            // 1. Get data from the Controller
            val transactions = controller.getAllTransactions()

            // 2. Get the JSON string from the Service
            val jsonString = jsonExportService.export(transactions)

            // 3. The View's only job is to handle the UI interaction (the share sheet)
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, jsonString)
                type = "application/json"
            }

            val shareIntent = Intent.createChooser(sendIntent, "Exportar Lançamentos Para...")
            startActivity(shareIntent)
        }
    }

    /**
     * Shows a simple information dialog. This is a UI concern and can stay here.
     */
    private fun showFutureFeatureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Funcionalidade Futura")
            .setMessage("Sincronização com API (pós-aula API).")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
