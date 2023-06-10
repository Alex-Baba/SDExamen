package com.sd.laborator

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.api.java.function.Function
import org.apache.spark.api.java.function.Function2
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaStreamingContext
import scala.Tuple2
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.apache.spark.api.java.StorageLevels.MEMORY_ONLY
import java.io.File


//https://spark.apache.org/docs/latest/rdd-programming-guide.html
//https://spark.apache.org/docs/latest/streaming-programming-guide.html

fun main(args: Array<String>) {

    // configurarea Spark
    val sparkConf = SparkConf().setMaster("local[4]").setAppName("Spark Example")

    //incarcam emotii in memorie
    val sparkContext = JavaSparkContext(sparkConf)

    val negativeWords = sparkContext.textFile("negative-words.txt")
    val positiveWords = sparkContext.textFile("positive-words.txt")

    negativeWords.persist(MEMORY_ONLY)
    positiveWords.persist(MEMORY_ONLY)

    var negativeWordsList = negativeWords.collect()
    var pozitiveWordsList = positiveWords.collect()

    sparkContext.close()

    // initializarea contextului Spark

    val streamingContext = JavaStreamingContext(sparkConf, Durations.seconds(1))
    val lines = streamingContext.socketTextStream("localhost", 9999)

    lines.map{it.split(" ") }.flatMap{it.iterator()}.filter { it -> negativeWordsList.contains(it)  }.count().union(
        lines.map{it.split(" ") }.flatMap{it.iterator()}.filter { it -> pozitiveWordsList.contains(it)  }.count()
    ).reduce{ negativeCount, positiveCount ->  if(negativeCount > positiveCount) 0 else 1 }
        .foreachRDD {
            rdd ->
        val items = rdd.collect()
        items.forEach {
            if( it.toInt() == 0) {
                println("Textul este negativ")
            }else if ( it.toInt() == 1){
                println("Textul este pozitiv")
            }
        }
    }

    streamingContext.start()
    streamingContext.awaitTermination()
}



