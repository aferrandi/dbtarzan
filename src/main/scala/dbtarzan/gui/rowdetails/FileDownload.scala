package dbtarzan.gui.rowdetails

import dbtarzan.db.{Field, FieldType}
import dbtarzan.types.Binaries.Binary

import java.io.{File, FileOutputStream, FileWriter}
import scala.util.Using

object FileDownload {
  def downloadData(file: File, field: Field, value: String | Binary): Unit = {
      field.fieldType match {
        case FieldType.STRING => writeTextFile(file, value.asInstanceOf[String])
        case FieldType.BINARY => writeBinaryFile(file, value.asInstanceOf[Binary])
        case _ => throw new RuntimeException(s"Field type not recognized ${field.fieldType} in field ${field.name}")
      }
  }

  private def writeTextFile(file: File, value: String): Unit = {
    Using(new FileWriter(file)) { writer =>
      writer.write(value)
    }
  }

  private def writeBinaryFile(file: File, value: Binary): Unit = {
    Using(new FileOutputStream(file)) { writer =>
      val bytes = value.asBytes
      writer.write(bytes)
    }
  }
}
