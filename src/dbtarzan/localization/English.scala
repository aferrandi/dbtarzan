package dbtarzan.localization

import java.nio.file.Path
import dbtarzan.messages.QueryId

class English extends Localization {
    def settings = "Settings"
    def globalSettings = "Global Settings"
    def editConnections = "Edit Connections"    
    def help = "Help"      
    def documentation = "Documentation"      
    def new_ = "New"
    def remove = "Remove"
    def duplicate = "Duplicate"
    def cancel = "Cancel"
    def save = "Save"
    def name = "Name"
    def user = "User"
    def password = "Password"
    def schema = "Schema"
    def delimiters = "Delimiters"
    def maxRows = "Max Rows"
    def tables = "Tables"
    def databases = "Databases"
    def foreignKeys = "Foreign keys"
    def connectionReset = "Connection Reset"
    def more= "More..."
    def message = "Message"
    def details = "Details"
    def language = "Language"
    def editGlobalSettings = "Edit Global Settings"    
    def copySQLToClipboard = "Copy SQL To Clipboard"
    def closeTabsBeforeThis = "Close tabs before this"
    def closeTabsAfterThis = "Close tabs after this"
    def closeAllTabs = "Close all tabs"
    def checkAll = "Check All"
    def uncheckAll = "Uncheck All"
    def copyMessageToClipboard = "Copy Message To Clipboard"
    def rowDetails = "Row Details"
    def buildForeignKeysFile = "Build foreign keys file"
    def areYouSureClose = "Are you sure you want to close without saving?"
    def areYouSureSaveConnections = "Are you sure you want to save the connections?"
    def saveConnections = "Save connections"
    def areYouSureSaveGlobalSettings = "Are you sure you want to save the global settings?"
    def saveGlobalSettings = "Save global settings"
    def selectionCopied = "Selection copied"
    def sqlCopied = "SQL copied"
    def writingFile(fileName : Path) = "Writing file "+fileName
    def fileWritten(fileName : Path) = "File "+fileName+" written"
    def connectedTo(databaseName : String) = "Connected to "+databaseName
    def loadedTables(amount : Int, databaseName : String) = "Loaded "+amount+" tables from the database "+databaseName
    def openingDatabase(databaseName : String) = "Opening database "+databaseName
    def loadingForeignKeys(fileName : String) = "Loading foreign keys from the database file "+fileName    
    def errorConnectingToDatabase(databaseName : String) = "Cronnecting to the database "+databaseName+" got"
    def errorRequestingTheRows(queryId : QueryId) = "Requesting the rows for the tab "+queryId+" got"    
    def errorCopyingSelection = "Copying selection to the clipboard got "
}