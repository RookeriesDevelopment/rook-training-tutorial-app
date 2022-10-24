package com.rookmotion.rooktraining.ui.trainingtype.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rookmotion.app.sdk.persistence.entities.training.RMTrainingType
import com.rookmotion.rooktraining.databinding.ListTileTrainingTypeBinding

class TrainingTypeAdapter(
    private val data: List<RMTrainingType>,
    private val onClick: (RMTrainingType) -> Unit
) : RecyclerView.Adapter<TrainingTypeAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ListTileTrainingTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trainingType: RMTrainingType) {
            with(binding) {
                name.text = trainingType.trainingName
                root.setOnClickListener { onClick(trainingType) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListTileTrainingTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size
}