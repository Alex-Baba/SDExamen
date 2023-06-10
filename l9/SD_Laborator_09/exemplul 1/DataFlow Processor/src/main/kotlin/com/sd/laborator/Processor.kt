package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.integration.annotation.Transformer
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

@EnableBinding(Processor::class)
@SpringBootApplication
open class SpringDataFlowTimeProcessorApplication {
    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    fun transform(msg: String?): Any? {
        if(msg == null || msg == "")
            return ""
        try {
            println("Message:$msg")
            val splitMessage = msg.split("~")
            var command = splitMessage[0]
            var prevOutput = if (splitMessage.size > 1) splitMessage[1] else ""
            val toExecute = command.split("|", limit=2)[0].removeSuffix(" ")
            val restOfCommand = if(command.split(" | ").size > 1) command.split("|", limit=2)[1].removePrefix(" ") else ""
            val proc = ProcessBuilder(listOf("/bin/sh", "-c", "echo \"$prevOutput\" | $toExecute"))
                .directory(File("/home/student"))
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()
            val output = proc.inputStream.bufferedReader().readText()
            println(output)
            println(restOfCommand)
            println("$restOfCommand~$output-This is the message to send.")
            return "$restOfCommand~$output"
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}

fun main(args: Array<String>) {
    runApplication<SpringDataFlowTimeProcessorApplication>(*args)
}