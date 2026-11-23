package com.racha.datos

object Semilla {
    const val META_DIARIA = 50

    /** Pasos que marcan solo "Caminar 20 min" (unos 20 minutos a paso normal). */
    const val META_PASOS = 2_000

    /** Meta reducida para demostrarlo en clase caminando unos segundos. */
    const val META_PASOS_DEMO = 30

    const val ID_CAMINAR = "caminar"

    val actividades = listOf(
        Actividad("agua", "Beber agua", 5, Icono.GOTA),
        Actividad("estirar", "Estirar", 10, Icono.FIGURA),
        Actividad("caminar", "Caminar 20 min", 15, Icono.PIE),
        Actividad("leer", "Leer", 10, Icono.LIBRO),
        Actividad("meditar", "Meditar", 15, Icono.MEDITAR),
        Actividad("dormir", "Dormir 7 horas", 20, Icono.LUNA),
        Actividad("entrenar", "Entrenar", 25, Icono.PESA),
    )

    val logros = listOf(
        Logro(
            "primer_paso", "Primer paso", "Ya empezaste",
            "Cumple una actividad", Condicion.PrimeraActividad, Icono.PIE,
        ),
        Logro(
            "constante", "Constante", "Tres días seguidos",
            "Racha de 3 días", Condicion.Racha(3), Icono.LLAMA,
        ),
        Logro(
            "semana", "Semana entera", "Siete días sin fallar",
            "Racha de 7 días", Condicion.Racha(7), Icono.LLAMA,
        ),
        Logro(
            "imparable", "Imparable", "Un mes completo",
            "Racha de 30 días", Condicion.Racha(30), Icono.LLAMA,
        ),
        Logro(
            "subiendo", "Subiendo", "Ya vas en serio",
            "Llega al nivel 5", Condicion.Nivel(5), Icono.ESTRELLA,
        ),
        Logro(
            "veterano", "Veterano", "Nivel de experto",
            "Llega al nivel 10", Condicion.Nivel(10), Icono.ESTRELLA,
        ),
        Logro(
            "dia_completo", "Día completo", "No faltó ninguna",
            "Todas en un día", Condicion.DiaCompleto, Icono.TROFEO,
        ),
        Logro(
            "madrugador", "Madrugador", "Antes que el sol",
            "Una antes de las 7", Condicion.AntesDe(7), Icono.SOL,
        ),
    )
}
