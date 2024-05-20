package dbtarzan.localization

import java.nio.file.Path
import dbtarzan.messages.{ QueryId, TWithDatabaseId, TWithTableId, TWithQueryId }

trait Localization {
  def settings : String
  def globalSettings: String
  def editConnections: String
  def editComposites: String
  def help : String
  def documentation: String
  def new_ : String
  def remove : String
  def delete : String
  def duplicate : String
  def cancel : String
  def test: String
  def save : String
  def name : String
  def user : String
  def password : String
  def passwords: String
  def schema : String
  def advanced: String
  def catalog : String
  def delimiters : String
  def maxRows : String
  def queryTimeoutInSeconds : String
  def maxFieldSize: String
  def maxInClauseCount: String
  def tables: String
  def database: String
  def databases: String
  def composites: String
  def foreignKeys: String
  def reconnect: String
  def orderBy: String
  def ascending: String
  def descending: String
  def where: String
  def more: String
  def message: String
  def details: String
  def language: String
  def encryptionKey : String
  def enter: String
  def filter : String
  def add: String
  def update : String
  def moveUp: String
  def moveDown: String
  def field: String
  def description: String
  def direction : String
  def choices: String
  def editGlobalSettings: String
  def addConnection: String
  def expand: String
  def open: String
  def download: String
  def closeThisTab: String
  def closeTabsBeforeThis: String
  def closeTabsAfterThis: String
  def closeAllTabs: String
  def checkAll: String
  def uncheckAll: String
  def copyMessageToClipboard : String
  def queryText : String
  def filterFields: String
  def columnsDescription : String
  def rowDetails: String
  def refresh: String
  def buildForeignKeysFile: String
  def areYouSureClose: String
  def areYouSureSaveConnections: String
  def areYouSureSaveComposite: String
  def saveConnections: String
  def saveComposites: String
  def areYouSureSaveGlobalSettings: String
  def saveGlobalSettings: String
  def selectionCopied: String
  def sqlCopied: String
  def saveOrder: String
  def copySelectionToClipboard : String
  def copyContentToClipboard : String
  def onlyCells: String
  def chooseOrderByColumns: String
  def cellsWithHeaders: String
  def selectDriverFile: String
  def jdbcUrlStrings : String
  def jarFiles : String
  def changeEncryptionKey : String
  def originalEncryptionKey : String
  def newEncryptionKey1 : String
  def newEncryptionKey2 : String
  def tableFrom : String
  def tableTo : String
  def columnsFrom : String
  def columnsTo : String
  def openVirtualForeignKeys: String
  def showAlsoIndividualDatabases: String
  def writingFile(fileName : Path) : String
  def fileWritten(fileName : Path) : String
  def connectedTo(databaseName : String) : String
  def loadedTables(amount : Int, databaseName : String)  : String
  def openingDatabase(databaseName : String) : String
  def loadingForeignKeys(fileName : String) : String
  def savingForeignKeys(fileName : String) : String
  def noRowsFromForeignKey(keyName : String, keyToTable : String) : String
  def unorderedQueryResults : String
  def globalChangesAfterRestart : String
  def connectionResetted(databaseName : String) : String
  def databaseAlreadyOpen(databaseName : String) : String
  def connectionRefused: String
  def connectionSuccessful: String
  def connectionToDatabaseSuccesful(databaseName: String): String
  def editingConnectionFile(fileName: Path) : String
  def editingCompositeFile(compositeConfigPath: Path): String
  def indexes: String
  def rowsNumber: String
  def errorConnectingToDatabase(databaseName : String) : String
  def errorQueryingDatabase(databaseName : String) : String
  def errorRequestingTheRows(queryId : QueryId) : String
  def errorCopyingSelection : String
  def errorCopyingSQL : String
  def errorReadingKeys(databaseName : String) : String
  def errorWritingKeys(databaseName : String) : String
  def errorDisplayingConnections : String
  def errorDisplayingComposites : String
  def errorSavingConnections: String
  def errorSavingComposites: String
  def errorSavingGlobalSettings: String
  def errorWrongEncryptionKey : String
  def errorEncryptionKeysDifferent : String
  def errorWrongEncryptionKeySize : String
  def errorDatabaseMessage(msg : TWithDatabaseId) : String
  def errorTableMessage(msg : TWithTableId) : String
  def errorTableMessage(msg : TWithQueryId) : String
  def errorNoTables(databaseName : String, schemasText : String) : String
  def errorDisplayingRows: String
  def errorVFKerification: String
  def errorAFKEmptyNames: String
  def errorAFKNameNewRow: String
  def errorAFKNoColumns(noColumns: List[String]) : String
  def errorAFKSameColumns(sameColumns: List[String]) : String
  def errorAFKDifferentColumnsNumber(differentColumnsNumber: List[String]) : String
  def errorAFKDuplicateNames(nameDuplicates: List[String]) : String
  def errorAFKDuplicateRelations(relationDuplicates: List[String]) : String
  def errorAFKAlreadyExisting(names : List[String]) : String
  def errorRegisteringDriver(name: String): String
  def warningNoPrimaryKeyInTable(tableName: String): String
}
