package com.caixaapp.view

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.caixaapp.controller.TransactionController
import com.caixaapp.databinding.ActivityChartBinding
import com.caixaapp.model.Person
import com.caixaapp.repository.RoomTransactionRepository
import com.caixaapp.util.DatabaseProvider
import com.caixaapp.util.JsonUtils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch

class ChartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChartBinding
    private lateinit var people: List<Person>
    private lateinit var controller: TransactionController
    private val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = DatabaseProvider.getDatabase(this).transactionDao()
        controller = TransactionController(RoomTransactionRepository(dao))

        people = JsonUtils.loadPeople(this)
        setupSpinner()
        setupChart()
    }

    private fun setupSpinner() {
        val labels = people.map { it.nome }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        binding.chartFilterSpinner.adapter = adapter
        binding.chartFilterSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                loadChart()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
        loadChart()
    }

    private fun setupChart() {
        binding.barChart.description.isEnabled = false
        binding.barChart.axisRight.isEnabled = false
        binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.barChart.xAxis.granularity = 1f
    }

    private fun loadChart() {
        val rateio = JsonUtils.loadRateio(this)
        val personId = people[binding.chartFilterSpinner.selectedItemPosition].id

        lifecycleScope.launch {
            val result = controller.getMonthlySummary(personId, rateio)
            val labels = result.summaries.map { it.monthLabel }

            val creditEntries = result.summaries.mapIndexed { index, summary ->
                BarEntry(index.toFloat(), summary.totalCredito.toFloat())
            }
            val debitEntries = result.summaries.mapIndexed { index, summary ->
                BarEntry(index.toFloat(), summary.totalDebito.toFloat())
            }

            val creditSet = BarDataSet(creditEntries, "Crédito")
            creditSet.color = getColor(com.caixaapp.R.color.green_credit)
            val debitSet = BarDataSet(debitEntries, "Débito")
            debitSet.color = getColor(com.caixaapp.R.color.red_debit)

            val barData = BarData(creditSet, debitSet)
            val groupSpace = 0.2f
            val barSpace = 0.05f
            val barWidth = 0.35f
            barData.barWidth = barWidth

            runOnUiThread {
                binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                binding.barChart.data = barData
                binding.barChart.xAxis.axisMinimum = 0f
                binding.barChart.xAxis.axisMaximum = labels.size.toFloat()
                binding.barChart.groupBars(0f, groupSpace, barSpace)
                binding.barChart.invalidate()

                binding.chartSummary.text = """Resumo 6 meses
Total de créditos: ${formatter.format(result.totalCredito)}
Total de débitos: ${formatter.format(result.totalDebito)}
Resultado líquido: ${formatter.format(result.saldo)}
""".trimIndent()
            }
        }
    }
}
