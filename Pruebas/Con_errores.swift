// =====================================================================
// PROGRAMA DE PRUEBA EXHAUSTIVO: CÓDIGO CON ERRORES (> 200 LÍNEAS)
// Archivo: con_errores.swift
// Descripción: Contiene múltiples fallos intencionados para comprobar
// la robustez del reporte de errores léxicos, sintácticos y semánticos.
// =====================================================================

// --- ERROR LÉXICO 1: Carácter no reconocido por el lenguaje (@) ---
var g2@: Double = 10.0
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

// Declaraciones de variables adicionales Double con errores simulados
var alphaError1: Double = 20.1
var alphaError2: Double = 21.2
var betaError1: Double = 22.3
var betaError2: Double = 23.4
var gammaError1: Double = 24.5
var gammaError2: Double = 25.6
var deltaError1: Double = 26.7
var deltaError2: Double = 27.8
var epsilonError1: Double = 28.9
var epsilonError2: Double = 29.0
var etaError1: Double = 32.3
var etaError2: Double = 33.4
var thetaError1: Double = 34.5
var thetaError2: Double = 35.6

// --- ERROR SEMÁNTICO 1: Asignación de tipos incompatibles (String a Int) ---
var a: Int = "ErrorSemanticoAsignacionTexto"
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
var w: Int = 75
var xVal: Int = 80
var yVal: Int = 85
var zVal: Int = 90

// Variables adicionales Int
var numError1: Int = 100
var numError2: Int = 110
var numError3: Int = 120
var numError4: Int = 130
var numError5: Int = 140
var numError6: Int = 150
var numError7: Int = 160
var numError8: Int = 170
var numError9: Int = 180
var numError10: Int = 190
var numError11: Int = 200
var numError12: Int = 210
var numError13: Int = 220
var numError14: Int = 230
var numError15: Int = 240
var numError16: Int = 250

// --- ERROR SINTÁCTICO 1: Falta de llave de cierre en la estructura condicional ---
if false {
    print("Bloque con error sintáctico por falta de llave")
    var variableTemporalError: Int = 100
}

// --- ERROR SEMÁNTICO 2: Uso de variable no declarada en la expresión aritmética ---
var x: Double = ((g2 + h2) * variableQueJamasFueDeclarada) / ((a2 - b2) + (c2 * 2.0))
var y: Double = ((d2 * e2) - f2) / (i2 + j2)
var z: Double = (k2 + l2) * (m2 - n2) / (o2 + p2)
var w: Double = (q2 * r2) + (s2 - t2)
var extraCalcError: Double = (u2 + v2) * (w2 - x2) / (y2 + z2)
var extraCalcError2: Double = (alphaError1 + betaError1) * (gammaError1 - deltaError1) / (epsilonError1 + 1.0)
var extraCalcError3: Double = (etaError1) * (thetaError1 - 1.0) / 2.0

// --- ERROR SINTÁCTICO 2: Operador aritmético duplicado consecutivo sin operando ---
var resultado: Int = (a + * b) / (d + 1)
var resultado2: Int = (e * f - g) / (h + 2)
var resultado3: Int = (i + j * k) - (l / m)
var resultado4: Int = (n - o * p) + (q * r)
var resultadoExtraError: Int = (s + t * u) / (v + 1)
var resultadoExtraError2: Int = (numError1 + numError2) * (numError3 - numError4) / (numError5 + 1)
var resultadoExtraError3: Int = (numError6 + numError7) * (numError8 - numError9) / (numError10 + 1)

// --- ERROR LÉXICO 2: Identificador mal formado que inicia con número ---
var 999identificadorInvalido: Int = 0
var limite: Int = 10

print("Inicio de pruebas con errores múltiples")

// --- ERROR SINTÁCTICO 3: Condición de bucle while vacía y mal estructurada ---
while {
    print("Bucle while con error sintáctico severo")
    contador = contador + 1
}

var resultadoFinal: Int = limite * 2
print("Cálculo parcial realizado con errores previos.")

// --- ERROR SEMÁNTICO 3: Uso ilegal de sentencia break fuera de un ciclo de bucle ---
break

// Sección de relleno con código adicional para alcanzar longitud y validar análisis
var operacionFallidaExtra1: Int = (a + b) * (c + d)
var operacionFallidaExtra2: Int = (e + f) * (g + h)
var operacionFallidaExtra3: Int = (i + j) * (k + l)
var operacionFallidaExtra4: Int = (m + n) * (o + p)
var operacionFallidaExtra5: Int = (q + r) * (s + t)
var operacionFallidaExtra6: Int = (u + v) * (w + xVal)
var operacionFallidaExtra7: Int = (numError1 + numError2) * (numError3 + numError4)

var operacionDobleFallida1: Double = (g2 * h2) / (a2 + b2)
var operacionDobleFallida2: Double = (c2 * d2) / (e2 + f2)
var operacionDobleFallida3: Double = (i2 * j2) / (k2 + l2)
var operacionDobleFallida4: Double = (m2 * n2) / (o2 + p2)
var operacionDobleFallida5: Double = (alphaError1 * betaError1) / (gammaError1 + deltaError1)

// --- BLOQUE DE 50 LÍNEAS ADICIONALES CON ERRORES Y SIMULACIONES ---
var errorExtraTipo1: Int = "FallaAsignacion1"
var errorExtraTipo2: Int = "FallaAsignacion2"
var errorExtraTipo3: Int = "FallaAsignacion3"
var errorExtraTipo4: Int = "FallaAsignacion4"
var errorExtraTipo5: Int = "FallaAsignacion5"
var errorExtraTipo6: Int = "FallaAsignacion6"
var errorExtraTipo7: Int = "FallaAsignacion7"
var errorExtraTipo8: Int = "FallaAsignacion8"
var errorExtraTipo9: Int = "FallaAsignacion9"
var errorExtraTipo10: Int = "FallaAsignacion10"

var errorLexicoExtra1# : Int = 100
var errorLexicoExtra2$ : Int = 200
var errorLexicoExtra3% : Int = 300
var errorLexicoExtra4& : Int = 400
var errorLexicoExtra5/ : Int = 500

var varInexistenteCalculo1 = variableFicticiaA + 5
var varInexistenteCalculo2 = variableFicticiaB * 2
var varInexistenteCalculo3 = variableFicticiaC - 1
var varInexistenteCalculo4 = variableFicticiaD / 4
var varInexistenteCalculo5 = variableFicticiaE + 10

// Errores de sintaxis simulados por declaraciones mal formadas
var sintaxisRota1 Int = 123
var sintaxisRota2 Double = 456.78
var sintaxisRota3 Int = 789
var sintaxisRota4 Double = 101.11
var sintaxisRota5 Int = 202.22

if true {
    // Falta llave de cierre intencional en subbloque de error sintáctico
    var subErrorVar: Int = 999
}

var cierreError1: Int = a + * 5
var cierreError2: Int = b - * 10
var cierreError3: Double = c2 + * 2.0
var cierreError4: Double = d2 - * 3.5
var cierreError5: Int = e * / 2

// Cierre final del documento con errores controlados adicionales
var finErrorToken1: Int = "TerminacionError1"
var finErrorToken2: Int = "TerminacionError2"
var finErrorToken3: Int = "TerminacionError3"
var finErrorToken4: Int = "TerminacionError4"
var finErrorToken5: Int = "TerminacionError5"

print("Fin del documento de pruebas con errores estructurados y controlados.")