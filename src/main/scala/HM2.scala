//import org.apache.commons.io.IOUtils
import org.apache.hadoop.io.IOUtils
import org.apache.hadoop.conf._
import org.apache.hadoop.fs._
import org.apache.hadoop.fs.{FileSystem, Path}


import java.io.InputStream
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.nio.charset.StandardCharsets
import scala.collection.immutable.Stream

object HM2 extends App {

  val conf = new Configuration()
  val fileSystem = FileSystem.get(new URI("hdfs://10.28.81.36:9000"), conf)

  //val path = new Path("/stage/date=2020-12-01/part-0000.csv")

  def saveFile(filepath: String): Unit = {
    val file = new File(filepath)
    val out = fileSystem.create(new Path(file.getName))
    val in = new BufferedInputStream(new FileInputStream(file))
    var b = new Array[Byte](1024)
    var numBytes = in.read(b)
    while (numBytes > 0) {
      out.write(b, 0, numBytes)
      numBytes = in.read(b)
    }
    in.close()
    out.close()
  }

  def removeFile(filename: String): Boolean = {
    val path = new Path(filename)
    fileSystem.delete(path, true)
  }

  def getFile(filename: String): InputStream = {
    val path = new Path(filename)
    fileSystem.open(path)
  }

  val inputStream = getFile("/stage/date=2020-12-03/part-0000.csv")
  val statusFilesList = fileSystem.listStatus(new Path("/stage/"))
  //println(x.mkString)

  val hdfs = FileSystem.get(new URI("hdfs://10.28.81.36:9000/"), new Configuration())
  val path = new Path("/stage/date=2020-12-03/part-0000.csv")
  val stream = hdfs.open(path)
  def readLines = Stream.cons(stream.readLine, Stream.continually( stream.readLine))

  //This example checks line for null and prints every existing line consequentally
  //readLines.takeWhile(_ != null).foreach(line => println(line))


  private val sourcePaths = statusFilesList
    .filter(s => s.isDirectory)
    .map { status =>
      status.getPath
      //println(sourcePath)
    }

  private val targetFolderName = "hdfs://10.28.81.36:9000/ods"
  private val isCreated: Boolean = fileSystem.mkdirs(new Path(targetFolderName))
  if(isCreated) println("Path successfully created")
  else println("Path not created")



  //  sourcePaths.foreach{ sPath =>
//    val iter = fileSystem.listFiles(sPath, false)
//    val stream = fileSystem.create(new Path(targetFolderName + "/part-0000.csv"))
//    while (iter.hasNext) {
//      val fileStatus = iter.next()
//      val in: FSDataInputStream = fileSystem.open(new Path("/stage/date=2020-12-03/part-0000.csv"))
//
//      //val in = new BufferedInputStream(new FileInputStream(new File()))
//      var b = new Array[Byte](1024)
//      var numBytes = in.read(b)
//      //println(b)
//      //IOUtils.copyBytes(in,stream,conf,false)
//      in.close()
//      //println(sPath.getName)
//      //println(fileStatus.getPath)
//    }
//    stream.close()
//  }



//  То есть , если у нас есть папка / stage / date = 2020 - 11 - 11 с файлами
  // то должна получиться папка / ods / date = 2020 - 11 - 11 с одним файлом
}
