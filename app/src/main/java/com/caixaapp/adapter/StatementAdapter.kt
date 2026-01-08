package com.caixaapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.caixaapp.R
import com.caixaapp.databinding.ItemStatementBinding
import com.caixaapp.model.StatementItem
import com.caixaapp.model.TransactionType
import com.caixaapp.util.DateUtils
import java.text.NumberFormat
import java.util.Locale

class StatementAdapter(
    private var items: List<StatementItem>
) : RecyclerView.Adapter<StatementAdapter.StatementViewHolder>() {

    fun update(newItems: List<StatementItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatementViewHolder {
        val binding = ItemStatementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatementViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class StatementViewHolder(private val binding: ItemStatementBinding) : RecyclerView.ViewHolder(binding.root) {
        private val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        fun bind(item: StatementItem) {
            binding.itemDate.text = DateUtils.formatDate(item.data)
            binding.itemType.text = item.tipo.label
            binding.itemDescription.text = item.descricao
            binding.itemValue.text = formatter.format(item.valor)

            val colorRes = if (item.tipo == TransactionType.CREDITO) {
                R.color.green_credit
            } else {
                R.color.red_debit
            }
            binding.itemValue.setTextColor(ContextCompat.getColor(binding.root.context, colorRes))
            binding.itemType.setTextColor(ContextCompat.getColor(binding.root.context, colorRes))
        }
    }
}
