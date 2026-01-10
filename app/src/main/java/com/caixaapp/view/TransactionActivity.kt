package com.caixaapp.view

import android.app.DatePickerDialog
// import android.content.Intent // No longer needed
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.caixaapp.controller.TransactionController
import com.caixaapp.databinding.ActivityTransactionBinding
import com.caixaapp.model.Person
import com.caixaapp.model.Transaction
import com.caixaapp.model.TransactionType
import com.caixaapp.repository.RoomTransactionRepository
import com.caixaapp.util.DatabaseProvider
import com.caixaapp.util.DateUtils
import com.caixaapp.util.JsonUtils
import java.time.LocalDate
import java.util.Calendar
import kotlinx.coroutines.launch

// REMOVED: private val ActivityTransactionBinding.goToStatementButton: Any

class TransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionBinding
    private lateinit var people: List<Person>
    private lateinit var controller: TransactionController
    private var selectedDate: LocalDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge() // Habilita o modo ponta a ponta

        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set OnClickListener for the back to menu button
        binding.backToMenuButton.setOnClickListener {
            // Finishes this activity and returns to the previous one in the stack (MainMenu)
            finish()
        }

        val dao = DatabaseProvider.getDatabase(this).transactionDao()
        controller = TransactionController(RoomTransactionRepository(dao))

        people = JsonUtils.loadPeople(this)
        setupPessoaSpinner()
        setupDatePicker()
        setupTypeSelector()

        binding.saveButton.setOnClickListener { saveTransaction() }

        // REMOVED: The listener for goToStatementButton as it no longer exists
        // binding.goToStatementButton.setOnClickListener {
        //     startActivity(Intent(this, StatementActivity::class.java))
        // }
    }

    private fun setupPessoaSpinner() {
        val labels = people.map { it.nome }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        binding.pessoaSpinner.adapter = adapter
    }

    private fun setupDatePicker() {
        binding.dateInput.setText(DateUtils.formatDate(selectedDate))
        binding.dateInput.setOnClickListener { openDatePicker() }
        // The line below might cause an error if your layout doesn't have an end icon.
        // If it does, you can keep it. Otherwise, remove it.
        // binding.dateInputLayout.setEndIconOnClickListener { openDatePicker() }
    }

    private fun setupTypeSelector() {
        binding.typeCredit.isChecked = true
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                binding.dateInput.setText(DateUtils.formatDate(selectedDate))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveTransaction() {
        // Safety check for people list
        if (people.isEmpty() || binding.pessoaSpinner.selectedItemPosition < 0) {
            Toast.makeText(this, "Erro ao carregar os dados de pessoas.", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedIndex = binding.pessoaSpinner.selectedItemPosition
        val personId = people[selectedIndex].id
        val type = if (binding.typeCredit.isChecked) TransactionType.CREDITO else TransactionType.DEBITO
        val valueText = binding.valueInput.text?.toString().orEmpty()
        val description = binding.descriptionInput.text?.toString().orEmpty()

        if (valueText.isBlank() || description.isBlank()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        val value = valueText.replace(",", ".").toDoubleOrNull()
        if (value == null) {
            Toast.makeText(this, "Informe um valor válido", Toast.LENGTH_SHORT).show()
            return
        }

        val transaction = Transaction(
            valor = value,
            descricao = description,
            data = selectedDate,
            tipo = type,
            pessoaId = personId
        )

        lifecycleScope.launch {
            controller.add(transaction)
            runOnUiThread {
                Toast.makeText(this@TransactionActivity, "Lançamento salvo", Toast.LENGTH_SHORT).show()
                binding.valueInput.text?.clear()
                binding.descriptionInput.text?.clear()
            }
        }
    }
}

// REMOVED: private fun Any.setOnClickListener(function: () -> Unit) {}
