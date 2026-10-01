package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.ViajeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ViajeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViaje(viaje: ViajeEntity): Long

    @Query("SELECT * FROM viajes_registrados WHERE id_usuario = :idUsuario ORDER BY id_viaje DESC")
    fun getViajesByUsuario(idUsuario: Long): Flow<List<ViajeEntity>>

    @Query("SELECT * FROM viajes_registrados ORDER BY id_viaje DESC")
    fun getAllViajesFlow(): Flow<List<ViajeEntity>>

    @Query("SELECT COUNT(*) FROM viajes_registrados WHERE id_usuario = :idUsuario")
    suspend fun countViajesByUsuario(idUsuario: Long): Int
}
