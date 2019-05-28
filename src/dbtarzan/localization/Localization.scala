package dbtarzan.localization

import java.nio.file.Path
import dbtarzan.messages.{ QueryId, TWithDatabaseId, TWithTableId, TWithQueryId }

trait Localization {
    def settings : String
    def globalSettings: String
    def editConnections: String
    def help : String
    def documentation: String
    def new_ : String 
    def remove : String
    def delete : String
    def duplicate : String
    def cancel : String
    def save : String
    def name : String
    def user : String
    def password : String
    def schema : String
    def advanced: String
    def catalog : String
    def delimiters : String
    def maxRows : String
    def tables: String
    def databases: String
    def foreignKeys: String
    def connectionReset: String
    def orderBy: String
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
    def editGlobalSettings: String
    def addConnection: String
    def closeTabsBeforeThis: String
    def closeTabsAfterThis: String
    def closeAllTabs: String
    def checkAll: String
    def uncheckAll: String
    def copyMessageToClipboard : String
    def queryText : String
    def columnsDescription : String    
    def rowDetails: String
    def buildForeignKeysFile: String
    def areYouSureClose: String
    def areYouSureSaveConnections: String
    def saveConnections: String
    def areYouSureSaveGlobalSettings: String
    def saveGlobalSettings: String
    def selectionCopied: String    
    def sqlCopied: String
    def saveOrder: String
    def copySelectionToClipboard : String
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
    def writingFile(fileName : Path) : String
    def fileWritten(fileName : Path) : String
    def connectedTo(databaseName : String) : String
    def loadedTables(amount : Int, databaseName : String)  : String
    def openingDatabase(databaseName : String) : String
    def loadingForeignKeys(fileName : String) : String
    def noRowsFromForeignKey(keyName : String, keyToTable : String) : String
    def unorderedQueryResults : String
    def globalChangesAfterRestart : String
    def connectionResetted(databaseName : String) : String
    def databaseAlreadyOpen(databaseName : String) : String
    def editingConnectionFile(fileName: Path) : String
    def errorConnectingToDatabase(databaseName : String) : String 
    def errorQueryingDatabase(databaseName : String) : String
    def errorRequestingTheRows(queryId : QueryId) : String
    def errorCopyingSelection : String
    def errorCopyingSQL : String
    def errorReadingKeys(databaseName : String) : String
    def errorDisplayingConnections : String
    def errorSavingConnections: String
    def errorSavingGlobalSettings: String
    def errorWrongEncryptionKey : String
    def errorEncryptionKeysDifferent : String
    def errorWrongEncryptionKeySize : String
    def errorDatabaseMessage(msg : TWithDatabaseId) : String
    def errorTableMessage(msg : TWithTableId) : String
    def errorTableMessage(msg : TWithQueryId) : String
    def errorNoTables(databaseName : String, schemasText : String) : String
    def errorDisplayingRows: String
}
