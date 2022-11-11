import com.typesafe.config.ConfigFactory
import org.apache.hadoop.conf._
import org.apache.hadoop.fs._
import org.apache.hadoop.hdfs.DistributedFileSystem
import org.apache.hadoop.io.IOUtils
import org.apache.hadoop.fs.{FileSystem, Path => HPath}
import java.net.URI
import java.io.{FileOutputStream, DataOutputStream, PrintWriter}
import java.io.File

object HM2_new extends App {
  // Читаем конфигурационный файл
  val config           = ConfigFactory.load()
  val from             = config.getString("from")
  val to               = config.getString("to")
  val readySuffix      = config.getString("readySuffix")
  val inProgressSuffix = config.getString("inProgressSuffix")
  val test               = config.getString("test")
  println(s"from $from  to $to  readySuffix $readySuffix  inProgressSuffix $inProgressSuffix")

  // Создаём файловую систему
  val conf             = new Configuration()
  private val fileSystem = FileSystem.get(new URI("hdfs://10.28.81.36:9000"), conf)
  val hdfsCoreSitePath = new Path("core-site.xml")
  val hdfsHDFSSitePath = new Path("hdfs-site.xml")
  conf.addResource(hdfsCoreSitePath)
  conf.addResource(hdfsHDFSSitePath)
  conf.set("fs.hdfs.impl", classOf[DistributedFileSystem].getName)
  conf.set("fs.file.impl", classOf[LocalFileSystem].getName)
  conf.setBoolean("dfs.support.append", true)
  conf.setBoolean("dfs.client.block.write.replace-datanode-on-failure.enable", false)

  val fileIsCreated = fileSystem.createNewFile(new Path(from))
  if (fileIsCreated== false) println("File already exists")

  val inputStream  = fileSystem.open(new Path(from))
  val outputStream = fileSystem.append(new Path(to))
  //IOUtils.copyBytes(inputStream, outputStream, 4096, true)
  val dataOutputStream = new DataOutputStream(new FileOutputStream(new File("data.txt")))
  //dataOutputStream.writeBytes("test")
  val output = fileSystem.create(new Path(test))
  val writer = new PrintWriter(output)
  dataOutputStream.writeChars("1111114444444")
  writer.write("this is a testfawqwgewwwwwwwwwwwwwwwwwwwwwwww ese")
  //writer.close()

}
