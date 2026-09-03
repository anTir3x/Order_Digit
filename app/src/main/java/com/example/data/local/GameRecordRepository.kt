package com.example.data.local

import com.example.data.model.GameRecord
import kotlinx.coroutines.flow.Flow

class GameRecordRepository(private val dao: GameRecordDao) {
    val allRecords: Flow<List<GameRecord>> = dao.getAllRecords()

    suspend fun insertRecord(record: GameRecord): Long {
        return dao.insertRecord(record)
    }

    suspend fun clearHistory() {
        dao.clearAllRecords()
    }
}
