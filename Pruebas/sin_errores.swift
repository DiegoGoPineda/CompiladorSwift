// =====================================================================
// PROGRAMA DE PRUEBA EXHAUSTIVO: CÓDIGO LIMPIO (> 200 LÍNEAS)
// Archivo: sin_errores.swift
// Descripción: Programa completo y válido para probar el análisis léxico,
// sintáctico, semántico y las optimizaciones de código muerto/inalcanzable.
// =====================================================================

// Bloque 1: Declaración masiva de variables de tipo Double (Grupo Alfa)
var g2: Double = 10.0
var h2: Double = 5.0
var a2: Double = 8.0
var b2: Double = 3.0
var c2: Double = 4.0
var d2: Double = 12.5
var e2: Double = 7.2
var f2: Double = 9.8
var i2: Double = 1.1
var j2: Double = 2.2
var k2: Double = 3.3
var l2: Double = 4.4
var m2: Double = 5.5
var n2: Double = 6.6
var o2: Double = 7.7
var p2: Double = 8.8
var q2: Double = 9.9
var r2: Double = 11.1
var s2: Double = 12.2
var t2: Double = 13.3
var u2: Double = 14.4
var v2: Double = 15.5
var w2: Double = 16.6
var x2: Double = 17.7
var y2: Double = 18.8
var z2: Double = 19.9

// Bloque 2: Declaración masiva de variables adicionales Double (Grupo Beta)
var alpha1: Double = 20.1
var alpha2: Double = 21.2
var beta1: Double = 22.3
var beta2: Double = 23.4
var gamma1: Double = 24.5
var gamma2: Double = 25.6
var delta1: Double = 26.7
var delta2: Double = 27.8
var epsilon1: Double = 28.9
var epsilon2: Double = 29.0
var zeta1: Double = 30.1
var zeta2: Double = 31.2
var eta1: Double = 32.3
var eta2: Double = 33.4
var theta1: Double = 34.5
var theta2: Double = 35.6

// Bloque 3: Declaración masiva de variables de tipo Int (Grupo Numérico 1)
var a: Int = 12
var b: Int = 4
var c: Int = 2
var d: Int = 3
var e: Int = 5
var f: Int = 6
var g: Int = 7
var h: Int = 8
var i: Int = 9
var j: Int = 10
var k: Int = 15
var l: Int = 20
var m: Int = 25
var n: Int = 30
var o: Int = 35
var p: Int = 40
var q: Int = 45
var r: Int = 50
var s: Int = 55
var t: Int = 60
var u: Int = 65
var v: Int = 70
var wVar: Int = 75
var xVal: Int = 80
var yVal: Int = 85
var zVal: Int = 90

// Bloque 4: Declaración masiva de variables adicionales Int (Grupo Numérico 2)
var num1: Int = 100
var num2: Int = 110
var num3: Int = 120
var num4: Int = 130
var num5: Int = 140
var num6: Int = 150
var num7: Int = 160
var num8: Int = 170
var num9: Int = 180
var num10: Int = 190
var num11: Int = 200
var num12: Int = 210
var num13: Int = 220
var num14: Int = 230
var num15: Int = 240
var num16: Int = 250

// Bloque 5: Prueba de código muerto para el optimizador
if false {
    print("Este bloque es código muerto absoluto y debe ser eliminado por el optimizador.")
    var variableFantasma1: Double = 999.9
    var variableFantasma2: Int = 8888
    var variableFantasma3: Double = 111.1
    var variableFantasma4: Int = 2222
}

// Bloque 6: Expresiones aritméticas complejas iniciales
var x: Double = ((g2 + h2) * 3.0) / ((a2 - b2) + (c2 * 2.0))
var y: Double = ((d2 * e2) - f2) / (i2 + j2)
var z: Double = (k2 + l2) * (m2 - n2) / (o2 + p2)
var w: Double = (q2 * r2) + (s2 - t2)
var wExtra: Double = (u2 + v2) * (w2 - x2) / (y2 + z2)
var extraCalc2: Double = (alpha1 + beta1) * (gamma1 - delta1) / (epsilon1 + 1.0)
var extraCalc3: Double = (zeta1 + eta1) * (theta1 - 1.0) / 2.0

var resultado: Int = (a + b * c) / (d + 1)
var resultado2: Int = (e * f - g) / (h + 2)
var resultado3: Int = (i + j * k) - (l / m)
var resultado4: Int = (n - o * p) + (q * r)
var resultadoExtra: Int = (s + t * u) / (v + 1)
var resultadoExtra2: Int = (num1 + num2) * (num3 - num4) / (num5 + 1)
var resultadoExtra3: Int = (num6 + num7) * (num8 - num9) / (num10 + 1)

// Bloque 7: Bucle While falso (Código Muerto)
var contador: Int = 0
var limite: Int = 10

print("Inicio de la ejecución del programa principal limpio")

while false {
    print("Iteración fantasma del bucle while muerto número 1")
    contador = contador + 1
}

while false {
    print("Iteración fantasma del bucle while muerto número 2")
    limite = limite - 1
}

var resultadoFinal: Int = limite * 2
print("El cálculo del resultado intermedio ha finalizado con éxito.")

// Bloque 8: Bucle condicional con control de flujo (break) y código inalcanzable
var index: Int = 0

while index < 5 {
    print("Evaluando índice actual en bucle de control")

    if index == 2 {
        print("Se alcanzó el punto de corte óptimo, saliendo del bucle.")
        break

        // Código inalcanzable después del break detectado por optimización
        var codigoInutilSecundario: Int = 54321
        print("Este mensaje jamás debería aparecer en la consola de salida")
    }

    index = index + 1
}

print("El bucle principal finalizó de manera limpia y correcta.")

// Bloque 9: Operaciones aritméticas adicionales de relleno funcional
var operacionExtra1: Int = (a + b) * (c + d)
var operacionExtra2: Int = (e + f) * (g + h)
var operacionExtra3: Int = (i + j) * (k + l)
var operacionExtra4: Int = (m + n) * (o + p)
var operacionExtra5: Int = (q + r) * (s + t)
var operacionExtra6: Int = (u + v) * (wVar + xVal)
var operacionExtra7: Int = (num1 + num2) * (num3 + num4)

var operacionDoble1: Double = (g2 * h2) / (a2 + b2)
var operacionDoble2: Double = (c2 * d2) / (e2 + f2)
var operacionDoble3: Double = (i2 * j2) / (k2 + l2)
var operacionDoble4: Double = (m2 * n2) / (o2 + p2)
var operacionDoble5: Double = (alpha1 * beta1) / (gamma1 + delta1)

// Bloque 10: Extensiones adicionales de seguridad de líneas (Líneas extra limpias)
var valExtension1: Int = 1001
var valExtension2: Int = 1002
var valExtension3: Int = 1003
var valExtension4: Int = 1004
var valExtension5: Int = 1005
var valExtension6: Int = 1006
var valExtension7: Int = 1007
var valExtension8: Int = 1008
var valExtension9: Int = 1009
var valExtension10: Int = 1010
var valExtension11: Double = 50.1
var valExtension12: Double = 50.2
var valExtension13: Double = 50.3
var valExtension14: Double = 50.4
var valExtension15: Double = 50.5
var valExtension16: Double = 50.6
var valExtension17: Double = 50.7
var valExtension18: Double = 50.8
var valExtension19: Double = 50.9
var valExtension20: Double = 51.0

print("Todas las operaciones aritméticas masivas se completaron sin incidencias.")
print("Finalización exitosa del script de pruebas limpio y verificado.")