package dbtarzan.types

import java.nio.file.Path

case class ConfigPath(globalConfigPath: Path, connectionsConfigPath: Path, keyFilesDirPath: Path, compositeConfigPath: Path , logFilePath: Path)