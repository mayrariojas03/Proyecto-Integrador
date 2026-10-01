package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.ConfiguracionUsuarioEntity
import com.example.data.local.entities.ConductorEntity
import com.example.data.local.entities.PasajeroEntity
import com.example.data.local.entities.PerfilUsuarioEntity
import com.example.data.local.entities.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUsuario(usuario: UsuarioEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerfil(perfil: PerfilUsuarioEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfiguracion(configuracion: ConfiguracionUsuarioEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPasajero(pasajero: PasajeroEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConductor(conductor: ConductorEntity): Long

    @Update
    suspend fun updateUsuario(usuario: UsuarioEntity)

    @Update
    suspend fun updatePerfil(perfil: PerfilUsuarioEntity)

    @Update
    suspend fun updateConfiguracion(configuracion: ConfiguracionUsuarioEntity)

    @Update
    suspend fun updatePasajero(pasajero: PasajeroEntity)

    @Query("SELECT * FROM usuarios WHERE id_usuario = :id LIMIT 1")
    suspend fun getUsuarioById(id: Long): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE numero_documento = :dni LIMIT 1")
    suspend fun getUsuarioByDocumento(dni: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE telefono = :telefono OR telefono = :altTelefono LIMIT 1")
    suspend fun getUsuarioByTelefono(telefono: String, altTelefono: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE LOWER(correo) = LOWER(:correo) LIMIT 1")
    suspend fun getUsuarioByCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios ORDER BY id_usuario DESC")
    fun getAllUsuariosFlow(): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM perfiles_usuario WHERE id_usuario = :idUsuario LIMIT 1")
    suspend fun getPerfilByUsuarioId(idUsuario: Long): PerfilUsuarioEntity?

    @Query("SELECT * FROM configuraciones_usuario WHERE id_usuario = :idUsuario LIMIT 1")
    suspend fun getConfiguracionByUsuarioId(idUsuario: Long): ConfiguracionUsuarioEntity?

    @Query("SELECT * FROM pasajeros WHERE id_usuario = :idUsuario LIMIT 1")
    suspend fun getPasajeroByUsuarioId(idUsuario: Long): PasajeroEntity?

    @Query("SELECT COUNT(*) FROM usuarios WHERE numero_documento = :dni")
    suspend fun countByDocumento(dni: String): Int

    @Query("SELECT COUNT(*) FROM usuarios WHERE telefono = :telefono OR telefono = :altTelefono")
    suspend fun countByTelefono(telefono: String, altTelefono: String): Int

    @Query("SELECT COUNT(*) FROM usuarios WHERE LOWER(correo) = LOWER(:correo)")
    suspend fun countByCorreo(correo: String): Int
}
