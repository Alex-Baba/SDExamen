package com.sd.laborator

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaInputDStream
import org.apache.spark.streaming.api.java.JavaPairDStream
import org.apache.spark.streaming.api.java.JavaStreamingContext
import org.apache.spark.streaming.kafka010.ConsumerStrategies
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies
import scala.Tuple2
import kotlin.math.sqrt

var devX: Double = 0.0
var devY: Double = 0.0

fun averageDeviation(devX: Double, devY: Double): Double {
    return (devX + devY) / 2.0
}
/*
 !!! ATENTIE...nu te speria ca e mult rosu si da eroare...nah...dai cu CTRL+F in log-uri si cauti 'Deviatie' si gasesti output-ul
 */

fun main(args: Array<String>) {
    // configurarea Kafka
    val kafkaParams = mutableMapOf<String, Any>(
        "bootstrap.servers" to "localhost:9092",
        "key.deserializer" to StringDeserializer::class.java,
        "value.deserializer" to StringDeserializer::class.java,
        "group.id" to "use_a_separate_group_id_for_each_stream",
        "auto.offset.reset" to "earliest",
        "enable.auto.commit" to false
    )
    val topics: Collection<String> = listOf("topic_random")
    val sparkConf = SparkConf().setMaster("local[4]").setAppName("KafkaIntegration")
    //val sparkContext = JavaSparkContext(sparkConf)
    // initializarea contextului de streaming
    val streamingContext = JavaStreamingContext(sparkConf, Durations.seconds(1))
    val stream: JavaInputDStream<ConsumerRecord<String, String>> = KafkaUtils.createDirectStream(
        streamingContext,
        LocationStrategies.PreferConsistent(),
        ConsumerStrategies.Subscribe<String, String>(topics, kafkaParams)
    )

    val streamPartitioned = stream.mapToPair{record: ConsumerRecord<String, String> -> Tuple2(record.value().drop(1).dropLast(1).split(", ")[0].toInt(),record.value().drop(1).dropLast(1).split(", ")[1].toInt()) }

    //asta nu stiu daca merge bine ca abia am scris-o, cum drq s-o si testez...mergem pe increde si tinete bine
    streamPartitioned.count().print()
    streamPartitioned
            //unde e cu _1 este pentru X din pos mouse
        .map { (it._1).toLong() }
        .reduce {acc, it -> acc + it} //suma lor
        .union(streamPartitioned
            .map { (it._1 * it._1).toLong() }
            .reduce {acc, it -> acc + it}) // suma patratelor
        .map { it -> it.toDouble()}
        .reduce{ s1, s2 -> sqrt(((100 * s2 - s1 * s1)/(100 * (100 - 1)).toDouble())) } //formula random de pe net pentru deviatie
        .union(streamPartitioned
            .map { (it._2).toLong() } //acu e pt Y ca e cu _2
            .reduce {acc, it -> acc + it}
            .union(streamPartitioned
                .map { (it._2 * it._2).toLong() }
                .reduce {acc, it -> acc + it})
            .map { it -> it.toDouble()}
            .reduce{ s1, s2 -> sqrt(((100 * s2 - s1 * s1)/(100 * (100 - 1)).toDouble())) })
        .reduce{devX, devY -> (devX + devY) / 2.0} //media deviatiei pe X si Y
        .foreachRDD{ rdd ->
            val dev = rdd.collect()[0]
            println("Deviatie pentru random este ${dev}")
        }

    //tura asta pentru mouse-ul pe bune

    val topics2: Collection<String> = listOf("topic_mouse")
    //val sparkContext = JavaSparkContext(sparkConf)
    // initializarea contextului de streaming
    val stream2: JavaInputDStream<ConsumerRecord<String, String>> = KafkaUtils.createDirectStream(
        streamingContext,
        LocationStrategies.PreferConsistent(),
        ConsumerStrategies.Subscribe<String, String>(topics2, kafkaParams)
    )

    val streamPartitioned2 = stream2.mapToPair{record: ConsumerRecord<String, String> -> Tuple2(record.value().drop(1).dropLast(1).split(", ")[0].toInt(),record.value().drop(1).dropLast(1).split(", ")[1].toInt()) }
    streamPartitioned2.count().print()
    streamPartitioned2
        //unde e cu _1 este pentru X din pos mouse
        .map { (it._1).toLong() }
        .reduce {acc, it -> acc + it} //suma lor
        .union(streamPartitioned2
            .map { (it._1 * it._1).toLong() }
            .reduce {acc, it -> acc + it}) // suma patratelor
        .map { it -> it.toDouble()}
        .reduce{ s1, s2 -> sqrt(((100 * s2 - s1 * s1)/(100 * (100 - 1)).toDouble())) } //formula random de pe net pentru deviatie
        .union(streamPartitioned2
            .map { (it._2).toLong() } //acu e pt Y ca e cu _2
            .reduce {acc, it -> acc + it}
            .union(streamPartitioned2
                .map { (it._2 * it._2).toLong() }
                .reduce {acc, it -> acc + it})
            .map { it -> it.toDouble()}
            .reduce{ s1, s2 -> sqrt(((100 * s2 - s1 * s1)/(100 * (100 - 1)).toDouble())) })
        .reduce{devX, devY -> (devX + devY) / 2.0} //media deviatiei pe X si Y
        .foreachRDD{ rdd ->
            val dev = rdd.collect()[0]
            println("Deviatie pentru mouse este ${dev}")
        }

    streamingContext.start()
    streamingContext.awaitTermination()//OrTimeout(1200)
}

fun calcDeviation(s1: Int, s2: Int): Double {
    val s0 = 100
    val rez = sqrt(((s0 * s2 - s1 * s1).toDouble()/(s0 * (s0 - 1)).toDouble()))
    println(rez)
    return rez
}
