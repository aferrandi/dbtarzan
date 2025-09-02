package dbtarzan.gui.config.connections;

import org.scalatest.flatspec.AnyFlatSpec
        
class JarSelectorTest extends AnyFlatSpec {
  "normalizing a Windows path" should "replace all backslashes to slashes" in {
    assert(JarSelector.normalizeWindowsPath("C:\\database\\jars\\postgres_jdbc.jar") === "C:/database/jars/postgres_jdbc.jar")
  }
}