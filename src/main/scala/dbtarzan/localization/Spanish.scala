package dbtarzan.localization

import java.nio.file.Path
import dbtarzan.messages.{ QueryId, TWithDatabaseId, TWithTableId, TWithQueryId }

class Spanish extends Localization {
    def settings = "Opciones"
    def globalSettings = "Opciones globales"
    def editConnections = "Modificación de conexiones"  
    def help = "Ayuda"      
    def documentation = "Documentación"
    def new_ = "Nuevo"
    def remove = "Eliminar"
    def delete = "Cancelar"
    def duplicate = "Duplicar"
    def test = "Test"
    def cancel = "Anular"
    def save = "Guardar"
    def name = "Nombre"
    def user = "Usuario"
    def password = "Contraseña"
    def schema = "Esquema"
    def advanced = "Avanzadas"
    def catalog = "catálogo"        
    def delimiters = "Separadores"
    def maxRows = "Máximo de filas"
    def maxFieldSize = "Max tamaño campo"
    def queryTimeoutInSeconds = "tiempo de espera de consulta en segundos"
    def tables = "Tablas"    
    def databases = "Base de datos"
    def foreignKeys = "Llaves extranjeras"
    def connectionReset = "Restablecer la conexión"
    def orderBy = "Ordenar por"
    def where = "Where"
    def more = "Más..."
    def message = "Mensaje"    
    def details = "Detalles"
    def language = "Idioma"
    def encryptionKey = "Master password"
    def enter = "Registrar"
    def filter = "Filtro"
    def add = "Añadir"
    def update = "Actualizar"
    def moveUp = "Arriba"
    def moveDown = "Abajo'"
    def field = "Columna"
    def description = "Descripción"
    def direction = "Dirección"
    def choices = "Opciones"
    def editGlobalSettings = "Editar configuración global"
    def addConnection = "Nueva conexion"
    def closeThisTab = "Cerrar est pestañas"
    def closeTabsBeforeThis = "Cerrar las pestañas antes de esta"
    def closeTabsAfterThis = "Cerrar las pestañas después de esta"
    def closeAllTabs = "Cerrar todas las pestañas"
    def checkAll = "Marcar todo"
    def uncheckAll = "Desmarcar todo"
    def copyMessageToClipboard = "Copiar mensaje al portapapeles"
    def queryText = "Texto De La Consulta"
    def columnsDescription = "Descripción De Las Columnas"    
    def rowDetails = "Detalles de la fila"  
    def buildForeignKeysFile = "Crear un archivo con las claves externas"
    def areYouSureClose = "Seguro que quieres cerrar sin guardar?"
    def areYouSureSaveConnections = "Seguro que quieres guardar las conexiones?"
    def saveConnections = "Guardar las conexiones"
    def areYouSureSaveGlobalSettings = "Seguro que quieres guardar las opciones globales?"    
    def saveGlobalSettings = "Guardar las opciones globales"
    def selectionCopied = "Selección copiada"
    def saveOrder = "Guardar orden"
    def sqlCopied = "SQL copiado"
    def copySelectionToClipboard = "Copiar selección al portapapeles"
    def copyContentToClipboard = "Copiar contenido al portapapeles"
    def onlyCells = "Solo las celdas"
    def cellsWithHeaders = "Celdas con titulos"
    def chooseOrderByColumns = "Elige las columnas para ordenar"
    def selectDriverFile = "Seleccione el archivo JAR del controlador"
    def jdbcUrlStrings = "URL para las conexiones Jdbc"
    def jarFiles = "Archivo JAR"
    def changeEncryptionKey = "Cambiar la master password. Posibles longitudes:"
    def originalEncryptionKey = "Originale"
    def newEncryptionKey1 = "Nueva"
    def newEncryptionKey2 = "Nueva, una vez mas"
    def tableFrom = "Tablas de"
    def tableTo = "Tabella a"
    def columnsFrom = "Columnas de"
    def columnsTo = "Columnas a"
    def openAdditionalForeignKeys = "Claves externas adicionales"
    def writingFile(fileName : Path) = "Estoy escribiendo el archivo "+fileName
    def fileWritten(fileName : Path) = "Archivo "+fileName+" escrito"
    def connectedTo(databaseName: String) = "Conectado a "+databaseName
    def loadedTables(amount : Int, databaseName : String) = "cargadas "+amount+" tablas de base de datos "+databaseName
    def openingDatabase(databaseName : String) = "Estoy abriendo la base de datos "+databaseName
    def loadingForeignKeys(fileName : String) = "Estoy cargando las claves externas desde el archivo de base de datos "+fileName    
    def savingForeignKeys(fileName : String)  = "Estoy guardando las claves externas en el archivo de base de datos "+fileName   
    def noRowsFromForeignKey(keyName : String, keyToTable : String) = "Ninguna fila seleccionada con la clave "+keyName+". Tabla "+keyToTable+" abierta sin filtro."
    def unorderedQueryResults = "Resultados no ordenadas?"
    def globalChangesAfterRestart = "Reinicie la aplicación para activar estos cambios"
    def connectionResetted(databaseName : String) = "Conexión de base de datos "+databaseName+" restablecida"
    def connectionRefused = "Conexión denegada"
    def connectionSuccessful = "Conexión exitosa"
    def connectionToDatabaseSuccesful(databaseName: String) = "Conexión de base de datos "+databaseName+" ha tenido éxito"
    def databaseAlreadyOpen(databaseName : String) =  "La base de datos "+databaseName+" ya está abierta"
    def editingConnectionFile(fileName: Path) = "Estoy editando el archivo de configuración de las conexiones " + fileName
    def errorConnectingToDatabase(databaseName : String) = "La conexión a la base de datos "+databaseName+" y 'fallado debido a"
    def errorQueryingDatabase(databaseName : String) = "La apertura de la base de datos "+databaseName+" y 'fallado debido a"
    def errorRequestingTheRows(queryId : QueryId) = "La solicitud de las filas de "+queryId+" y 'fallado debido a"    
    def errorCopyingSelection = "La copia de la selección y 'fallado debido a "
    def errorCopyingSQL = "La copia de SQL y 'fallado debido a "
    def errorReadingKeys(databaseName : String) = "Se produjo el siguiente error en la lectura del archivo de clave externa de la base de datos "+databaseName+". Elimine el archivo si está dañado o es de una versión anterior de la aplicación."
    def errorWritingKeys(databaseName : String) = "Se produjo el siguiente error en la escritura del archivo de clave externa de la base de datos  "+databaseName+". Compruebe que el archivo no está abierto en otra aplicación"
    def errorDisplayingConnections = "La visualización de conexiones ha fallado debido a"
    def errorSavingConnections = "El guardado de conexiones ha fallado debido a"
    def errorSavingGlobalSettings = "El guardado de las opciones globales, y 'fallado debido a"
    def errorWrongEncryptionKey = "Contraseña maestra incorrecta"
    def errorWrongEncryptionKeySize = "La longitud de la contraseña maestra es incorrecta. Posibles longitudes"
    def errorEncryptionKeysDifferent = "Las contraseñas maestras son diferentes"
    def errorDatabaseMessage(msg : TWithDatabaseId) = "Mensaje de la base de datos "+msg+" no reconocido"
    def errorTableMessage(msg : TWithTableId) = "Mensaje de la tabla "+msg+" non riconosciuto"
    def errorTableMessage(msg : TWithQueryId) = "Mensaje de la tabla "+msg+" non riconosciuto"
    def errorNoTables(databaseName : String, schemasText : String) = "Ninguna tabla leído de la base de datos "+databaseName+". Esquema incorrecto? Esquemas disponibles: "+schemasText
    def errorDisplayingRows = "La visualización de las filas de la tabla falló debido a"
    def errorAFKVerification= "claves externas adicionales incorrecto."
    def errorAFKEmptyNames = "Nombres vacios"
    def errorAFKNameNewRow = "Nombres inválidos"
    def errorAFKNoColumns(noColumns: List[String]) = "Faltan columnas en "+noColumns.mkString(", ")
    def errorAFKSameColumns(sameColumns: List[String]) = "Mismas columnas de y a en "+sameColumns.mkString(", ")
    def errorAFKDifferentColumnsNumber(differentColumnsNumber: List[String]) = "De y acon diferente número de columnas en "+differentColumnsNumber.mkString(", ")
    def errorAFKDuplicateNames(nameDuplicates: List[String]) = "Nombres duplicados: "+nameDuplicates.mkString(", ")
    def errorAFKDuplicateRelations(relationDuplicates: List[String]) = "Reacciones duplicadas: "+relationDuplicates.mkString(", ")
    def errorAFKAlreadyExisting(names : List[String]) = "Las claves externas adicionales "+names.mkString(", ")+" ya existen como claves externas"
    def errorRegisteringDriver(databaseName: String) = "El registro a la base de datos "+databaseName+" y 'fallado debido a"
}