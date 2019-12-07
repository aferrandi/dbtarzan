package dbtarzan.localization

import java.nio.file.Path
import dbtarzan.messages.{ QueryId, TWithDatabaseId, TWithTableId, TWithQueryId }

class English extends Localization {
    def settings = "Settings"
    def globalSettings = "Global Settings"
    def editConnections = "Edit Connections"    
    def help = "Help"      
    def documentation = "Documentation"      
    def new_ = "New"
    def remove = "Remove"
    def delete = "Delete"
    def duplicate = "Duplicate"
    def cancel = "Cancel"
    def save = "Save"
    def name = "Name"
    def user = "User"
    def password = "Password"
    def schema = "Schema"
    def advanced = "Advanced"
    def catalog = "Catalog"    
    def delimiters = "Delimiters"
    def maxRows = "Max Rows"
    def queryTimeoutInSeconds = "Query timeout in seconds"
    def tables = "Tables"
    def databases = "Databases"
    def foreignKeys = "Foreign keys"
    def connectionReset = "Connection Reset"
    def orderBy = "Order by"
    def where = "Where"
    def more= "More..."
    def message = "Message"
    def details = "Details"
    def language = "Language"
    def encryptionKey = "Master password"
    def enter = "Enter"
    def filter = "Filter"
    def add = "Add" 
    def update = "Update"
    def moveUp = "Move up"
    def moveDown = "Move Down"
    def field = "Field"
    def description = "Description"
    def direction = "Direction"
    def editGlobalSettings = "Edit Global Settings"   
    def addConnection = "Add Connection"
    def closeTabsBeforeThis = "Close tabs before this"
    def closeTabsAfterThis = "Close tabs after this"
    def closeAllTabs = "Close all tabs"
    def checkAll = "Check All"
    def uncheckAll = "Uncheck All"
    def queryText = "Query text"
    def columnsDescription = "Columns Description"
    def copyMessageToClipboard = "Copy Message To Clipboard"
    def rowDetails = "Row Details"
    def buildForeignKeysFile = "Build foreign keys file"
    def areYouSureClose = "Are you sure you want to close without saving?"
    def areYouSureSaveConnections = "Are you sure you want to save the connections?"
    def saveConnections = "Save connections"
    def areYouSureSaveGlobalSettings = "Are you sure you want to save the global settings?"
    def saveGlobalSettings = "Save global settings"
    def saveOrder = "Save Order"
    def selectionCopied = "Selection copied"
    def sqlCopied = "SQL copied"
    def copySelectionToClipboard = "Copy selection to clipboard"
    def onlyCells = "Only cells"
    def cellsWithHeaders = "Cells with headers"
    def chooseOrderByColumns = "Choose OrderBy Columns"
    def selectDriverFile = "Select the driver jar file"
    def jdbcUrlStrings = "Jdbc connections url strings"
    def jarFiles = "JAR files"
    def changeEncryptionKey = "Change master password. Possible lengths: "
    def originalEncryptionKey = "Original"
    def newEncryptionKey1 = "New"
    def newEncryptionKey2 = "New again"
    def tableFrom = "Table from"
    def tableTo = "Table to"
    def columnsFrom = "Columns from"
    def columnsTo = "Columns to"
    def openAdditionalForeignKeys = "Additional foreign keys"
    def writingFile(fileName : Path) = "Writing file "+fileName
    def fileWritten(fileName : Path) = "File "+fileName+" written"
    def connectedTo(databaseName : String) = "Connected to "+databaseName
    def loadedTables(amount : Int, databaseName : String) = "Loaded "+amount+" tables from the database "+databaseName
    def openingDatabase(databaseName : String) = "Opening database "+databaseName
    def loadingForeignKeys(fileName : String) = "Loading foreign keys from the database file "+fileName   
    def savingForeignKeys(fileName : String)  = "Saving foreign keys to the database file "+fileName   
    def noRowsFromForeignKey(keyName : String, keyToTable : String) = "No rows selected with key "+keyName+". Open table "+keyToTable+" without filter."
    def unorderedQueryResults = "Unordered query results?"
    def globalChangesAfterRestart = "Please restart the application to activate these changes"
    def connectionResetted(databaseName : String) = "Connection to the database "+databaseName+" resetted"
    def databaseAlreadyOpen(databaseName : String) =  "Database "+databaseName+" already open"
    def editingConnectionFile(fileName: Path) = "Editing connections configuration file " + fileName
    def errorConnectingToDatabase(databaseName : String) = "Cronnecting to the database "+databaseName+" got"
    def errorQueryingDatabase(databaseName : String) = "Querying the database "+databaseName+" got"
    def errorRequestingTheRows(queryId : QueryId) = "Requesting the rows for the tab "+queryId+" got"    
    def errorCopyingSelection = "Copying selection to the clipboard got "
    def errorCopyingSQL = "Copying SQL to the clipboard got "
    def errorReadingKeys(databaseName : String) = "Reading the keys file for database "+databaseName+" got the following error. Delete the file if it is corrupted or of an old version of the application."
    def errorWritingKeys(databaseName : String) = "Writing the keys file for database "+databaseName+" got the following error. Check that the file is not open in another application"
    def errorDisplayingConnections = "Displaying connections got"
    def errorSavingConnections = "Saving the connections got"
    def errorSavingGlobalSettings = "Saving the global settings got"
    def errorWrongEncryptionKey = "Wrong master password"
    def errorWrongEncryptionKeySize = "Wrong master password length. Possible lengths"
    def errorEncryptionKeysDifferent = "Master passwords are different"
    def errorDatabaseMessage(msg : TWithDatabaseId) = "Database message "+msg+" not recognized"
    def errorTableMessage(msg : TWithTableId) = "Table message "+msg+" not recognized"
    def errorTableMessage(msg : TWithQueryId) = "Table message "+msg+" not recognized"    
    def errorNoTables(databaseName : String, schemasText : String) = "No tables read from database "+databaseName+". Wrong schema? Available schemas: "+schemasText
    def errorDisplayingRows = "Displaying the table rows got"
    def errorAFKVerification= "Wrong additional foreign keys."
    def errorAFKEmptyNames = "Empty names"
    def errorAFKNameNewRow = "Not valid names"
    def errorAFKNoColumns(noColumns: List[String]) = "Missing columns in "+noColumns.mkString(", ")
    def errorAFKSameColumns(sameColumns: List[String]) = "Same from and to columns in "+sameColumns.mkString(", ")
    def errorAFKDifferentColumnsNumber(differentColumnsNumber: List[String]) = "From and to with different columns number in "+differentColumnsNumber.mkString(", ")
    def errorAFKDuplicateNames(nameDuplicates: List[String]) = " Duplicate names: "+nameDuplicates.mkString(", ")
    def errorAFKDuplicateRelations(relationDuplicates: List[String]) = "Duplicate relations: "+relationDuplicates.mkString(", ")
    def errorAFKAlreadyExisting(names : List[String]) = "The additional foreign keys "+names.mkString(", ")+" already exist as foreign keys"
}
