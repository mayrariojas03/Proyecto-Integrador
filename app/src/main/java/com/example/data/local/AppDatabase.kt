package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.UsuarioDao
import com.example.data.local.dao.ViajeDao
import com.example.data.local.entities.ConfiguracionUsuarioEntity
import com.example.data.local.entities.ConductorEntity
import com.example.data.local.entities.PasajeroEntity
import com.example.data.local.entities.PerfilUsuarioEntity
import com.example.data.local.entities.UsuarioEntity
import com.example.data.local.entities.ViajeEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UsuarioEntity::class,
        PerfilUsuarioEntity::class,
        ConfiguracionUsuarioEntity::class,
        PasajeroEntity::class,
        ConductorEntity::class,
        ViajeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun viajeDao(): ViajeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_chiclayo.db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Pre-populate with initial official records from MySQL script
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialDatabase(database)
                    }
                }
            }

            private suspend fun populateInitialDatabase(database: AppDatabase) {
                val dao = database.usuarioDao()
                // 1. Ana Torres (PASAJERO)
                val id1 = dao.insertUsuario(
                    UsuarioEntity(
                        nombres = "Ana",
                        apellidos = "Torres",
                        tipo_documento = "DNI",
                        numero_documento = "70000001",
                        telefono = "987000001",
                        correo = "ana@smartchiclayo.pe",
                        tipo_usuario = "PASAJERO",
                        estado = "ACTIVO",
                        telefono_verificado = true,
                        correo_verificado = true,
                        fecha_registro = "2024-10-01 10:00:00"
                    )
                )
                dao.insertPerfil(
                    PerfilUsuarioEntity(
                        id_usuario = id1,
                        fecha_nacimiento = "2000-05-10",
                        direccion = "Chiclayo Centro",
                        ciudad = "Chiclayo",
                        departamento = "Lambayeque"
                    )
                )
                dao.insertConfiguracion(
                    ConfiguracionUsuarioEntity(id_usuario = id1, tema = "OSCURO")
                )
                dao.insertPasajero(
                    PasajeroEntity(id_usuario = id1, promedio_calificacion = 5.0, total_viajes = 0)
                )

                // 2. Carlos Mendoza (CONDUCTOR)
                val id2 = dao.insertUsuario(
                    UsuarioEntity(
                        nombres = "Carlos",
                        apellidos = "Mendoza",
                        tipo_documento = "DNI",
                        numero_documento = "70000002",
                        telefono = "987000002",
                        correo = "carlos@smartchiclayo.pe",
                        tipo_usuario = "CONDUCTOR",
                        estado = "ACTIVO",
                        telefono_verificado = true,
                        correo_verificado = true,
                        fecha_registro = "2024-10-01 10:00:00"
                    )
                )
                dao.insertPerfil(
                    PerfilUsuarioEntity(
                        id_usuario = id2,
                        fecha_nacimiento = "1995-08-15",
                        direccion = "La Victoria",
                        ciudad = "Chiclayo",
                        departamento = "Lambayeque"
                    )
                )
                dao.insertConfiguracion(
                    ConfiguracionUsuarioEntity(id_usuario = id2, tema = "OSCURO")
                )
                dao.insertConductor(
                    ConductorEntity(
                        id_usuario = id2,
                        numero_licencia = "LIC-000001",
                        categoria_licencia = "A-IIa",
                        vencimiento_licencia = "2030-12-31",
                        estado_validacion = "VERIFICADO",
                        estado_servicio = "DISPONIBLE",
                        promedio_calificacion = 5.0,
                        total_viajes = 0
                    )
                )

                // 3. Luis Ramirez (GESTOR)
                val id3 = dao.insertUsuario(
                    UsuarioEntity(
                        nombres = "Luis",
                        apellidos = "Ramirez",
                        tipo_documento = "DNI",
                        numero_documento = "70000003",
                        telefono = "987000003",
                        correo = "luis@smartchiclayo.pe",
                        tipo_usuario = "GESTOR",
                        estado = "ACTIVO",
                        telefono_verificado = true,
                        correo_verificado = true,
                        fecha_registro = "2024-10-01 10:00:00"
                    )
                )
                dao.insertPerfil(
                    PerfilUsuarioEntity(
                        id_usuario = id3,
                        fecha_nacimiento = "1988-12-20",
                        direccion = "José Leonardo Ortiz",
                        ciudad = "Chiclayo",
                        departamento = "Lambayeque"
                    )
                )
                dao.insertConfiguracion(
                    ConfiguracionUsuarioEntity(id_usuario = id3, tema = "OSCURO")
                )
            }
        }
    }
}
