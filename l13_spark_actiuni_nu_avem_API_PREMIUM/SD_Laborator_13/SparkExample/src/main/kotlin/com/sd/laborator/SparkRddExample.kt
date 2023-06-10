package com.sd.laborator

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.api.java.function.Function
import org.apache.spark.api.java.function.Function2
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaStreamingContext
import scala.Tuple2

fun main(args: Array<String>) {
    // configurarea Spark
    val sparkConf = SparkConf().setMaster("local[4]").setAppName("Spark Example")
    // initializarea contextului Spark
    val streamingContext = JavaStreamingContext(sparkConf, Durations.seconds(1))
    val lines = streamingContext.socketTextStream("localhost", 9999)

    lines.mapToPair { it ->
        Tuple2(it, Json.parseToJsonElement(it).jsonObject["targetLow"].toString().replace("\"","").toDouble() / Json.parseToJsonElement(it).jsonObject["targetMean"].toString().replace("\"","").toDouble())
       }.filter { it._2 > 0.4 }
        .foreachRDD { rdd ->
            val items = rdd.collect()
            items.forEach {
                val json = Json.parseToJsonElement(it._1).jsonObject
                val profit = it._2 * 100
                println("Company: ${json["symbol"].toString()} | Profit: ${profit}%")
            }
        }

    streamingContext.start()
    streamingContext.awaitTermination()
}