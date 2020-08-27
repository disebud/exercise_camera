package com.disebud.exercise_camera

import java.text.SimpleDateFormat
import java.util.*

fun main(){

    val date = Date()
    println(date)

    val sdf = SimpleDateFormat("yyyyMM")

    println(sdf.format(date))
    println(SimpleDateFormat("MM").format(Date()))

}