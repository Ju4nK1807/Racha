package com.racha.logica

import com.racha.datos.Semilla
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class LogicaTest {

    private val hoy = LocalDate.of(2026, 10, 19)

    @Test
    fun umbralesDeNivel() {
        assertEquals(0, umbralNivel(1))
        assertEquals(150, umbralNivel(2))
        assertEquals(300, umbralNivel(3))
        assertEquals(500, umbralNivel(4))
        assertEquals(1, nivelPara(0))
        assertEquals(2, nivelPara(150))
        assertEquals(3, nivelPara(320))
        assertEquals(4, nivelPara(500))
    }

    @Test
    fun rachaSigueSeMantieneOReinicia() {
        assertEquals(1, rachaAlRegistrar(null, 0, hoy))
        assertEquals(4, rachaAlRegistrar(hoy, 4, hoy))
        assertEquals(5, rachaAlRegistrar(hoy.minusDays(1), 4, hoy))
        assertEquals(1, rachaAlRegistrar(hoy.minusDays(2), 4, hoy))
        assertEquals(0, rachaVigente(hoy.minusDays(2), 4, hoy))
    }

    @Test
    fun bonoDesdeElDiaTresConTope() {
        assertEquals(0, bonoRacha(15, 2))
        assertEquals(4, bonoRacha(15, 3))
        assertEquals(7, bonoRacha(15, 5))
        assertEquals(7, bonoRacha(15, 12))
    }

    @Test
    fun logrosSeEvaluanUnaSolaVez() {
        val contexto = ContextoLogros(
            totalRegistros = 1, racha = 3, nivel = 1,
            cumplidasHoy = 1, totalActividades = 7, horaRegistro = 6,
        )
        val nuevos = logrosNuevos(Semilla.logros, emptySet(), contexto).map { it.id }
        assertEquals(listOf("primer_paso", "constante", "madrugador"), nuevos)
        assertTrue(logrosNuevos(Semilla.logros, nuevos.toSet(), contexto).isEmpty())
    }
}
