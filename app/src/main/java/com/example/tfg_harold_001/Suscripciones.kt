package com.example.tfg_harold_001

data class Suscripciones(
    var Nombre: String ?= "DEFAULT NAME",
    var Plan: String ?= "DEFAULT PLAN",
    var Precio: Double = 33.00,
    var Tipo: String ?= "DEFAULT TIPO",
    var Estado: String  ?= "DEFAULT Activo",
    var IDnoti: Int = 55) {
}