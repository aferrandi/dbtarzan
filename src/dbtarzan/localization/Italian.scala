package dbtarzan.localization

import java.nio.file.Path
import dbtarzan.messages.QueryId

class Italian extends Localization {
    def settings = "Opzioni"
    def globalSettings = "Opzioni Globali"
    def editConnections = "Modifica Connessioni"  
    def help = "Aiuto"      
    def documentation = "Documentazione"
    def new_ = "Nuovo"
    def remove = "Cancella"
    def duplicate = "Duplica"
    def cancel = "Annulla"
    def save = "Salva"
    def name = "Nome"
    def user = "Utente"
    def password = "Password"
    def schema = "Schema"
    def delimiters = "Separatori"
    def maxRows = "Max Righe"    
    def tables = "Tabelle"    
    def databases = "Database"
    def foreignKeys = "Chiavi esterne"
    def connectionReset = "Resetta connessione"
    def more = "Altro..."
    def message = "Messaggio"    
    def details = "Dettagli"
    def language = "Lingua"
    def editGlobalSettings = "Modifica Opzioni Globali"    
    def copySQLToClipboard = "Copia SQL nel Clipboard"
    def closeTabsBeforeThis = "Chiudi le tab prima di questa"
    def closeTabsAfterThis = "Chiudi le tab dopo questa"
    def closeAllTabs = "Chiudi tutte le tab"
    def checkAll = "Spunta Tutto"
    def uncheckAll = "Rimuovi tutte le spunte"
    def copyMessageToClipboard = "Copia Messaggio Nel Clipboard"
    def rowDetails = "Dettagli della riga"  
    def buildForeignKeysFile = "crea file con le chiavi esterne"
    def areYouSureClose = "Sicuro di chiudere senza salvare?"
    def areYouSureSaveConnections = "Sicuro di voler salvare le connessioni?"
    def saveConnections = "Salva le connessioni"
    def areYouSureSaveGlobalSettings = "Sicuro di voler salvare le opzioni globali?"    
    def saveGlobalSettings = "Salvare le opzioni globali"
    def selectionCopied = "Selezione copiata"
    def sqlCopied = "SQL copiato"
    def copySelectionToClipboard = "Copia selezione nel clipboard"
    def onlyCells = "Solo le celle"
    def cellsWithHeaders = "Celle con titoli"
    def writingFile(fileName : Path) = "Sto scrivendo il file "+fileName
    def fileWritten(fileName : Path) = "File "+fileName+" scritto"
    def connectedTo(databaseName: String) = "Connesso a "+databaseName
    def loadedTables(amount : Int, databaseName : String) = "Caricate "+amount+" tabelle dal database "+databaseName
    def openingDatabase(databaseName : String) = "Sto aprendo il database "+databaseName
    def loadingForeignKeys(fileName : String) = "Sto caricando le chiavi esterne dal file dei database "+fileName    
    def errorConnectingToDatabase(databaseName : String) = "La connessione al database "+databaseName+" e' fallita a causa di"
    def errorRequestingTheRows(queryId : QueryId) = "La richiesta delle right di "+queryId+" e' fallita a a causa di"    
    def errorCopyingSelection = "La copia della selezione e' fallita a causa di "
}