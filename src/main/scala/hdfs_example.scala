import com.typesafe.config.ConfigFactory
import org.apache.hadoop.conf._
import org.apache.hadoop.fs._
import org.apache.hadoop.hdfs.DistributedFileSystem
import org.apache.hadoop.io.IOUtils
import org.apache.hadoop.fs.{FileSystem, Path => HPath}
import java.net.URI

object hdfs_example extends App {
  // Читаем конфигурационный файл
  val config           = ConfigFactory.load()
  val from             = config.getString("from")
  val to               = config.getString("to")
  val readySuffix      = config.getString("readySuffix")
  val inProgressSuffix = config.getString("inProgressSuffix")
  val targetFileName   = config.getString("targetFileName")
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


  // Определяем список директорий-источников и идем в цикле по этому списку
  val sourceDirs = fileSystem
    .listStatus(new HPath(from))
    .filter(_.isDirectory)
    .map(_.getPath)

  sourceDirs.foreach { dir =>
    // Отфильтрованный список файлов-источников для каждой исходной директории
    val sourceFiles = fileSystem.listStatus(dir)
      .filter(fs => fs.isFile && fs.getPath.getName.endsWith(readySuffix))
      .map(_.getPath).toList
    val destination = to + dir.getName

    // Копируем структуру директорий
    fileSystem.mkdirs(new HPath(destination))

    val targetFile = new HPath(destination + "/" + targetFileName)

    // В каждой директории создаем целевой файл
    val outputStream: FSDataOutputStream = fileSystem.create(targetFile)
    //val outputStream: FSDataOutputStream = fileSystem.append(targetFile)

    try {
      // Для каждой исходной директории перебираем файлы-источники
      sourceFiles.foreach(sourceFile => {
        val inputStream: FSDataInputStream = fileSystem.open(sourceFile)
        try {
          println(s"trying to add ${sourceFile.getName} to ${targetFile.getName}")
          // Стриминговая запись 
          IOUtils.copyBytes(inputStream, outputStream, 4096, false)
        } finally {
          inputStream.close()
        }
      })
    } finally {
      outputStream.close()
    }
  }
}
