package org.hathitrust.htrc.tools.ef.metadata.combineseqfiles

import com.gilt.gfc.time.Timer
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import org.hathitrust.htrc.tools.ef.metadata.combineseqfiles.Helper.logger

import scala.io.Codec
import scala.language.reflectiveCalls

object Main {
  val appName: String = "combine-seq-files"

  def main(args: Array[String]): Unit = {
    val conf = new Conf(args.to(Seq))
    val inputPath = conf.inputPath().toString
    val updatePath = conf.updatePath().toString
    val outputPath = conf.outputPath().toString

    conf.outputPath().mkdirs()

    // set up logging destination
    conf.sparkLog.toOption match {
      case Some(logFile) => System.setProperty("spark.logFile", logFile)
      case None =>
    }
    System.setProperty("logLevel", conf.logLevel().toUpperCase)

    // set up Spark context
    val sparkConf = new SparkConf()
    sparkConf.setAppName(appName)
    sparkConf.setIfMissing("spark.master", "local[*]")

    val spark = SparkSession.builder()
      .config(sparkConf)
      .getOrCreate()

    implicit val sc: SparkContext = spark.sparkContext
    implicit val codec: Codec = Codec.UTF8

    val numPartitions = conf.numPartitions.getOrElse(sc.defaultMinPartitions)

    logger.info("Starting...")

    // record start time
    val t0 = System.nanoTime()

    val inputRDD = sc.sequenceFile[String, String](inputPath, minPartitions = numPartitions)
    val updateRDD = sc.sequenceFile[String, String](updatePath, minPartitions = numPartitions)

    val resultRDD = inputRDD.subtractByKey(updateRDD).union(updateRDD)

    resultRDD
      .saveAsSequenceFile(outputPath + "/output", Some(classOf[org.apache.hadoop.io.compress.BZip2Codec]))
//
//    resultRDD.foreach { case (id, json) =>
//      val cleanId = id.replaceAllLiterally(":", "+").replaceAllLiterally("/", "=")
//      FileUtils.writeStringToFile(new File(outputPath, s"$cleanId.json"), Json.prettyPrint(json), StandardCharsets.UTF_8)
//    }

    // record elapsed time and report it
    val t1 = System.nanoTime()
    val elapsed = t1 - t0

    logger.info(f"All done in ${Timer.pretty(elapsed)}")
  }

}
