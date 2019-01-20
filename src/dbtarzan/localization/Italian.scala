package dbtarzan.localization

import java.nio.file.Path
import dbtarzan.messages.{ QueryId, TWithDatabaseId, TWithTableId, TWithQueryId }

class Italian extends Localization {
    def settings = "Opzioni"
    def globalSettings = "Opzioni Globali"
    def editConnections = "Modifica Connessioni"  
    def help = "Aiuto"      
    def documentation = "Documentazione"
    def new_ = "Nuovo"
    def remove = "Cancella"
    def delete = "Cancella"
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
    def orderBy = "Order by"
    def where = "Where"
    def more = "Altro..."
    def message = "Messaggio"    
    def details = "Dettagli"
    def language = "Lingua"
    def filter = "Filtro"
    def add = "Aggiungi"
    def update = "Aggiorna"
    def moveUp = "Su"
    def moveDown = "Giu'"
    def field = "Colonna"
    def direction = "Direzione"    
    def editGlobalSettings = "Modifica Opzioni Globali"   
    def addConnection = "Nuova connessione" 
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
    def saveOrder = "Salva ordine"
    def sqlCopied = "SQL copiato"
    def copySelectionToClipboard = "Copia selezione nel clipboard"
    def onlyCells = "Solo le celle"
    def cellsWithHeaders = "Celle con titoli"
    def chooseOrderByColumns = "Segli le Colonne Da Ordinare"
    def selectDriverFile = "Seleziona il jar file del driver"
    def jdbcUrlStrings = "Url per le connessioni Jdbc"
    def jarFiles = "File JAR"
    def writingFile(fileName : Path) = "Sto scrivendo il file "+fileName
    def fileWritten(fileName : Path) = "File "+fileName+" scritto"
    def connectedTo(databaseName: String) = "Connesso a "+databaseName
    def loadedTables(amount : Int, databaseName : String) = "Caricate "+amount+" tabelle dal database "+databaseName
    def openingDatabase(databaseName : String) = "Sto aprendo il database "+databaseName
    def loadingForeignKeys(fileName : String) = "Sto caricando le chiavi esterne dal file dei database "+fileName    
    def noRowsFromForeignKey(keyName : String, keyToTable : String) = "Nessuna riga selezionata con la chiave "+keyName+". Tabella "+keyToTable+" aperta senza filtro."
    def unorderedQueryResults = "Risultati non ordinati?"
    def connectionResetted(databaseName : String) = "Connessione al database "+databaseName+" resettata"
    def databaseAlreadyOpen(databaseName : String) =  "Il database "+databaseName+" e' gia' aperto"
    def errorConnectingToDatabase(databaseName : String) = "La connessione al database "+databaseName+" e' fallita a causa di"
    def errorQueryingDatabase(databaseName : String) = "L'apertura del database "+databaseName+" e' fallita a causa di"
    def errorRequestingTheRows(queryId : QueryId) = "La richiesta delle right di "+queryId+" e' fallita a a causa di"    
    def errorCopyingSelection = "La copia della selezione e' fallita a causa di "
    def errorCopyingSQL = "La copia dell'SQL' e' fallita a causa di "
    def errorReadingKeys(databaseName : String) = "Nella lettiura del file delle chiavi esterne del database "+databaseName+" si e' avuto il seguente errore. Cancella il file se e' corrotto o di una vecchia versione dell'applicazione."
    def errorDisplayingConnections = "La lista delle connessioni e' fallita a causa di"
    def errorSavingConnections = "Il salvataggio delle connessioni e' fallito a causa di"
    def errorSavingGlobalSettings = "Il salvataggio delle opzioni globali e' fallito a causa di"
    def errorDatabaseMessage(msg : TWithDatabaseId) = "Messaggio dal database "+msg+" non riconosciuto"
    def errorTableMessage(msg : TWithTableId) = "Messaggio dalla tabella "+msg+" non riconosciuto"
    def errorTableMessage(msg : TWithQueryId) = "Messaggio dalla tabella "+msg+" non riconosciuto"
    def errorNoTables(databaseName : String, schemasText : String) = "Nessuna tabella letta dal database "+databaseName+". Schema errato? Schemi disponibili: "+schemasText
}