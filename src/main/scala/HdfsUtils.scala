//Example from Vadim

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path => HPath}
import org.apache.hadoop.io.IOUtils

object HdfsUtils extends App {

  private val conf = new Configuration()
  private val hdfsCoreSitePath = new HPath("core-site.xml")
  private val hdfsHDFSSitePath = new HPath("hdfs-site.xml")

  conf.addResource(hdfsCoreSitePath)
  conf.addResource(hdfsHDFSSitePath)

  private val fileSystem = FileSystem.get(conf)

  val sourceDirs = fileSystem
    .listStatus(new HPath("/stage"))
    .filter(_.isDirectory)
    .map(_.getPath)

  sourceDirs.foreach{ dir =>
    val sourceFiles =  fileSystem.listStatus(dir)
      .filter(fs => fs.isFile && fs.getPath.getName.endsWith(".csv"))
      .map(_.getPath).toList
    val destination = "/ods/" + dir.getName

    fileSystem.mkdirs(new HPath(destination))

    val targetFile = new HPath(destination + "/targetFile.csv")

    val outStream = fileSystem.create(targetFile)

    try {
      sourceFiles.foreach(sourceFile => {
        val inStream = fileSystem.open(sourceFile)
        try {
          println(s"trying to add ${sourceFile.getName} to ${targetFile.getName}")
          IOUtils.copyBytes(inStream, outStream, conf, false)
        } finally {
          inStream.close()
        }
      })
    } finally {
      outStream.close()
    }
  }
}
