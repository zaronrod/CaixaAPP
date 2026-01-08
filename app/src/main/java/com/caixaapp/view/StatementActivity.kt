package com.caixaapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.caixaapp.adapter.StatementAdapter
import com.caixaapp.controller.TransactionController
import com.caixaapp.databinding.ActivityStatementBinding
import com.caixaapp.model.Person
import com.caixaapp.repository.RoomTransactionRepository
import com.caixaapp.util.DatabaseProvider
import com.caixaapp.util.JsonUtils
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch

class StatementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatementBinding
    private lateinit var people: List<Person>
    private lateinit var adapter: StatementAdapter
    private lateinit var controller: TransactionController
    private val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = DatabaseProvider.getDatabase(this).transactionDao()
        controller = TransactionController(RoomTransactionRepository(dao))

        people = JsonUtils.loadPeople(this)
        setupSpinner()
        setupRecycler()

        binding.goToChartButton.setOnClickListener {
            startActivity(Intent(this, ChartActivity::class.java))
        }
    }

    private fun setupSpinner() {
        val labels = people.map { it.nome }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        binding.filterSpinner.adapter = adapter
        binding.filterSpinner.setSelection(0)
        binding.filterSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                loadStatement()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
        loadStatement()
    }

    private fun setupRecycler() {
        adapter = StatementAdapter(emptyList())
        binding.statementRecycler.layoutManager = LinearLayoutManager(this)
        binding.statementRecycler.adapter = adapter
    }

    private fun loadStatement() {
        val rateio = JsonUtils.loadRateio(this)
        val personId = people[binding.filterSpinner.selectedItemPosition].id

        lifecycleScope.launch {
            val result = controller.getStatement(personId, rateio)
            runOnUiThread {
                adapter.update(result.items)
                binding.statementSummary.text = "Saldo total: ${formatter.format(result.saldo)}"
            }
        }
    }
}
