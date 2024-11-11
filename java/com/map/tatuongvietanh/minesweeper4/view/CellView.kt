package com.map.tatuongvietanh.minesweeper4.view

// CellView.kt
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import com.map.tatuongvietanh.minesweeper4.model.Tile

class CellView(context: Context, attrs: AttributeSet? = null) : androidx.appcompat.widget.AppCompatButton(context, attrs) {

    private var tile: Tile? = null
    var onCellClickListener: ((Int, Int) -> Unit)? = null

    fun setTile(tile: Tile) {
        this.tile = tile
        updateView()
    }

    private fun updateView() {
        tile?.let { cell ->
            if (cell.isRevealed) {
                isEnabled = false
                setBackgroundColor(Color.LTGRAY)
                text = when {
                    cell.hasMine -> "💣"
                    cell.adjacentMines > 0 -> cell.adjacentMines.toString()
                    else -> ""
                }
            } else {
                isEnabled = true
                setBackgroundColor(Color.DKGRAY)
                text = if (cell.isFlagged) "🚩" else ""
            }
        }
    }

    init {
        setOnClickListener {
            tile?.let { cell ->
                // Check if flagging or revealing
                if (!cell.isRevealed) {
                    if (cell.isFlagged) {
                        cell.isFlagged = false
                    } else {
                        onCellClickListener?.invoke(cell.row, cell.column)
                    }
                    updateView()
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
}
