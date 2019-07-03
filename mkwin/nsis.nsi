;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"

;--------------------------------
;General

  ;Name and file
  Name "DBTarzan (${VERSION})"
  OutFile "DBTarzan-Install-${VERSION}.exe"

  ;Default installation folder
  InstallDir "$PROGRAMFILES\DBTarzan"

  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\DBTarzan" ""

  ;Request application privileges for Windows Vista
  RequestExecutionLevel none

;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING
  !define MUI_ICON "monkey-face-cartoon.ico"

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_LICENSE "..\LICENSE"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_PAGE_FINISH

  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  !insertmacro MUI_UNPAGE_FINISH

;--------------------------------
;Languages

  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "DBTarzan Base" SecBase

  SetOutPath "$INSTDIR"

  ;ADD YOUR OWN FILES HERE...
  File "dbtarzan_${VERSION}.exe"
  File /r "jre11"
  CreateDirectory "$SMPROGRAMS\DBTarzan"
  CreateDirectory "$APPDATA\DBTarzan"
  CreateShortCut "$SMPROGRAMS\DBTarzan\DBTarzan.lnk" "$INSTDIR\dbtarzan_${VERSION}.exe" "--configPath=$APPDATA\DBTarzan"
  CreateShortCut "$SMPROGRAMS\DBTarzan\Uninstall.lnk" "$INSTDIR\uninstall.exe"

  ;Store installation folder
  WriteRegStr HKCU "Software\DBTarzan" "" $INSTDIR

  ;Create uninstaller
  WriteUninstaller "$INSTDIR\uninstall.exe"
SectionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecBase ${LANG_ENGLISH} "The DBTarzan application."

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecBase} $(DESC_SecBase)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;ADD YOUR OWN FILES HERE...

  Delete "$INSTDIR\*.jar"
  Delete "$INSTDIR\*.exe"
  RMDIR  /r "$INSTDIR\icons"
  RMDIR  /r "$INSTDIR\dbtarzan"

  Delete "$SMPROGRAMS\DBTarzan\DBTarzan.lnk"
  Delete "$SMPROGRAMS\DBTarzan\Uninstall.lnk"
  RMDIR "$SMPROGRAMS\DBTarzan"

  RMDir "$INSTDIR"

  DeleteRegKey /ifempty HKCU "Software\DBTarzan"

SectionEnd