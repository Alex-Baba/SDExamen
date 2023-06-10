package com.sd.laborator

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaStreamingContext

fun main(args: Array<String>) {
    // configurarea Spark
    val sparkConf = SparkConf().setMaster("local[4]").setAppName("Spark Example")
    // initializarea contextului Spark
    val streamingContext = JavaStreamingContext(sparkConf, Durations.seconds(1))
    val lines = streamingContext.socketTextStream("tcp_server", 9999)
    
    lines
        .filter { it -> Json.parseToJsonElement(it).jsonObject["source"].toString().replace("\"", "") == "Yahoo" }
        .filter { it -> Json.parseToJsonElement(it).jsonObject["summary"].toString().length > 50 }
        .foreachRDD { rdd ->
            val list = rdd.collect()
            list.forEach {
                val json = Json.parseToJsonElement(it).jsonObject
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd")
                //val date = java.util.Date(1532358895 * 1000)

                println(
                    "STIRE: ${json["headline"]} | data: ${
                        sdf.format(
                            java.util.Date(
                                json["datetime"].toString().toLong() * 1000
                            )
                        )
                    } | url: ${json["url"].toString()}"
                )
            }
        }

    streamingContext.start()             // Start the computation
    streamingContext.awaitTermination()  // Wait for the computation to terminate
}
