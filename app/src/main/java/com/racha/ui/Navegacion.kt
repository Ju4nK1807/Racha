package com.racha.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.runtime.LaunchedEffect
import com.racha.PasosUi
import com.racha.datos.Semilla
import com.racha.ui.pantallas.Pasos
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.racha.EstadoUi
import com.racha.VistaModelo
import com.racha.ui.pantallas.Actividades
import com.racha.ui.pantallas.Celebracion
import com.racha.ui.pantallas.Confirmacion
import com.racha.ui.pantallas.Desarrollo
import com.racha.ui.pantallas.Hoy
import com.racha.ui.pantallas.Logros
import com.racha.ui.pantallas.Progreso

private object Rutas {
    const val INICIO = "inicio"
    const val ACTIVIDADES = "actividades"
    const val CONFIRMACION = "confirmacion"
    const val CELEBRACION = "celebracion"
    const val DESARROLLO = "desarrollo"
}

private const val PAGINAS = 4
private const val PAGINA_HOY = 1
private const val PAGINA_PASOS = 2

@Composable
fun Navegacion(vistaModelo: VistaModelo = viewModel()) {
    val estado by vistaModelo.estado.collectAsStateWithLifecycle()
    val resultado by vistaModelo.resultado.collectAsStateWithLifecycle()
    val porCelebrar by vistaModelo.porCelebrar.collectAsStateWithLifecycle()
    val pasos by vistaModelo.pasos.collectAsStateWithLifecycle()
    val navegador = rememberSwipeDismissableNavController()
    val hapticos = rememberHapticos()

    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        vistaModelo.permisoPasosActualizado()
    }
    val alPedirPermiso = { pedirPermiso.launch(Manifest.permission.ACTIVITY_RECOGNITION) }
    LaunchedEffect(Unit) {
        if (vistaModelo.faltaPermisoPasos()) alPedirPermiso()
    }
    // Al llegar a la meta de pasos, "Caminar" se registra sola: se confirma igual que a mano.
    LaunchedEffect(Unit) {
        vistaModelo.autoRegistro.collect {
            hapticos.registro()
            runCatching { navegador.navigate(Rutas.CONFIRMACION) { popUpTo(Rutas.INICIO) } }
        }
    }

    // Hasta que DataStore responde, solo negro: evita mostrar ceros por un instante.
    if (!estado.cargado) {
        Box(Modifier.fillMaxSize().background(ColoresRacha.fondo))
        return
    }

    SwipeDismissableNavHost(navController = navegador, startDestination = Rutas.INICIO) {
        composable(Rutas.INICIO) {
            Inicio(
                estado = estado,
                pasos = pasos,
                alPedirPermiso = alPedirPermiso,
                alRegistrar = { navegador.navigate(Rutas.ACTIVIDADES) },
                alAbrirDesarrollo = { navegador.navigate(Rutas.DESARROLLO) },
            )
        }
        composable(Rutas.ACTIVIDADES) {
            Pantalla(conVineta = true) {
                Actividades(
                    estado = estado,
                    alCumplir = { id ->
                        hapticos.registro()
                        vistaModelo.registrar(id) {
                            navegador.navigate(Rutas.CONFIRMACION) { popUpTo(Rutas.INICIO) }
                        }
                    },
                )
            }
        }
        composable(Rutas.CONFIRMACION) {
            Pantalla {
                Confirmacion(
                    resultado = resultado,
                    hapticos = hapticos,
                    alTerminar = {
                        if (porCelebrar.isNotEmpty()) {
                            navegador.navigate(Rutas.CELEBRACION) { popUpTo(Rutas.INICIO) }
                        } else {
                            volverAInicio(navegador)
                        }
                    },
                )
            }
        }
        composable(Rutas.CELEBRACION) {
            Pantalla {
                Celebracion(
                    logros = porCelebrar,
                    hapticos = hapticos,
                    alTerminar = {
                        vistaModelo.celebracionVista()
                        volverAInicio(navegador)
                    },
                )
            }
        }
        composable(Rutas.DESARROLLO) {
            Pantalla(conVineta = true) {
                Desarrollo(
                    demoPasos = pasos.demo,
                    alSumarPasos = { vistaModelo.sumarPasos { volverAInicio(navegador) } },
                    alAlternarDemo = vistaModelo::alternarDemoPasos,
                    alSumarPuntos = {
                        vistaModelo.sumarPuntos { hayLogros ->
                            if (hayLogros) {
                                navegador.navigate(Rutas.CELEBRACION) { popUpTo(Rutas.INICIO) }
                            } else {
                                volverAInicio(navegador)
                            }
                        }
                    },
                    alSimularDia = { vistaModelo.simularDiaAnterior { volverAInicio(navegador) } },
                    alReiniciar = { vistaModelo.reiniciar { volverAInicio(navegador) } },
                )
            }
        }
    }
}

/** Progreso, Hoy, Pasos y Logros como páginas horizontales; abre en Hoy. */
@Composable
private fun Inicio(
    estado: EstadoUi,
    pasos: PasosUi,
    alPedirPermiso: () -> Unit,
    alRegistrar: () -> Unit,
    alAbrirDesarrollo: () -> Unit,
) {
    val paginas = rememberPagerState(initialPage = PAGINA_HOY) { PAGINAS }
    Scaffold(timeText = { TimeText() }) {
        Box(Modifier.fillMaxSize()) {
            HorizontalPager(state = paginas, modifier = Modifier.fillMaxSize()) { pagina ->
                when (pagina) {
                    0 -> Progreso(estado)
                    PAGINA_HOY -> Hoy(estado, alRegistrar, alAbrirDesarrollo)
                    PAGINA_PASOS -> Pasos(
                        pasos = pasos,
                        caminarCumplida = estado.actividades.any { it.actividad.id == Semilla.ID_CAMINAR && it.cumplida },
                        alPedirPermiso = alPedirPermiso,
                    )
                    else -> Logros(estado)
                }
            }
            IndicadorPaginas(
                actual = paginas.currentPage,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp),
            )
        }
    }
}

@Composable
private fun IndicadorPaginas(actual: Int, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(PAGINAS) { indice ->
            Box(
                Modifier
                    .size(5.dp)
                    .background(
                        if (indice == actual) ColoresRacha.texto else ColoresRacha.atenuado,
                        CircleShape,
                    ),
            )
        }
    }
}

@Composable
private fun Pantalla(conVineta: Boolean = false, contenido: @Composable () -> Unit) {
    Scaffold(
        timeText = { TimeText() },
        vignette = if (conVineta) {
            { Vignette(vignettePosition = VignettePosition.TopAndBottom) }
        } else {
            null
        },
    ) {
        contenido()
    }
}

private fun volverAInicio(navegador: NavHostController) {
    navegador.popBackStack(Rutas.INICIO, inclusive = false)
}
