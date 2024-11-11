package com.map.tatuongvietanh.minesweeper4.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.map.tatuongvietanh.minesweeper4.R
import com.map.tatuongvietanh.minesweeper4.model.Tile

class CellView(context: Context, attrs: AttributeSet? = null) : CardView(context, attrs) {

    private var tile: Tile? = null
    var onCellClickListener: ((Int, Int) -> Unit)? = null

    init {
        inflate(context, R.layout.view_cell, this)
        setOnClickListener {
            tile?.let { cell ->
                if (!cell.isRevealed) {
                    onCellClickListener?.invoke(cell.row, cell.column)
                }
            }
        }
        setOnLongClickListener {
            tile?.let { cell ->
                if (!cell.isRevealed) {
                    cell.isFlagged = !cell.isFlagged
                    updateView()
                }
            }
            true
        }
    }

    fun setTile(tile: Tile) {
        this.tile = tile
        updateView()
    }

    private fun updateView() {
        tile?.let { cell ->
            val tileText = findViewById<TextView>(R.id.tileText).apply {
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 0)
            }
            if (cell.isRevealed) {
                // Revealed state with distinct color for easy viewing
                setCardBackgroundColor(Color.parseColor("#404040")) // Darker gray for revealed cells
                tileText.visibility = View.VISIBLE
                tileText.text = when {
                    cell.hasMine -> "💣"
                    cell.adjacentMines > 0 -> cell.adjacentMines.toString()
                    else -> "❌"
                }
                tileText.setTextColor(getColorForNumber(cell.adjacentMines))
            } else {
                // Unrevealed or flagged state
                setCardBackgroundColor(Color.parseColor("#2A2A2A")) // Dark gray for unrevealed cells
                tileText.visibility = if (cell.isFlagged) View.VISIBLE else View.GONE
                tileText.text = if (cell.isFlagged) "🚩" else ""
                tileText.setTextColor(Color.parseColor("#FFEB3B")) // Yellow color for flags
            }
        }
    }

    private fun getColorForNumber(mines: Int): Int {
        return when (mines) {
            1 -> Color.parseColor("#1976D2") // Blue for "1"
            2 -> Color.parseColor("#388E3C") // Green for "2"
            3 -> Color.parseColor("#D32F2F") // Red for "3"
            4 -> Color.parseColor("#7B1FA2") // Purple for "4"
            5 -> Color.parseColor("#F57C00") // Orange for "5"
            else -> Color.parseColor("#D3D3D3") // Default light gray for other numbers
        }
    }
}