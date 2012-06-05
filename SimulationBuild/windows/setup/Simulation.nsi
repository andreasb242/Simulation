; Simulation Setupfile: Siehe http://nsis.sourceforge.net
;
; Andreas Butti, 05.06.2012

;--------------------------------

; The source path
!define src ".."

;Include Modern UI
!include "MUI2.nsh"

!include "FileAssociation.nsh"

; The name of the installer
Name "(AB)² Simulation"
Caption "(AB)² Simulation"

!define MUI_ICON "SetupIcon.ico"

; The file to write
OutFile "Setup_Simulation.exe"

; The default installation directory
InstallDir $PROGRAMFILES\AB2Simulation

; Registry key to check for directory (so if you install again, it will 
; overwrite the old one automatically)
InstallDirRegKey HKLM "Software\AB2Simulation" "Install_Dir"

; Request application privileges for Windows Vista
RequestExecutionLevel admin

!define MUI_FINISHPAGE_RUN "$INSTDIR\Simulation.exe"
!define MUI_FINISHPAGE_RUN_TEXT "Simulation starten"


; Header image
!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_BITMAP "Setup_Header.bmp"

;--------------------------------
;Pages

!insertmacro MUI_PAGE_LICENSE "gpl-2.0.txt"
;!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------
;Languages
 
!insertmacro MUI_LANGUAGE "German"

;--------------------------------

; The stuff to install
Section "(AB)² Simulation (required)"
	SectionIn RO

	; Set output path to the installation directory.
	SetOutPath $INSTDIR

	; Put file there
	File "${src}\Simulation.exe"
	File "${src}\Simulation.jar"

	SetOutPath $INSTDIR\lib
	File "${src}\lib\*"

	SetOutPath $INSTDIR\plugin\import
	File "${src}\plugin\import\*"

	SetOutPath $INSTDIR\plugin\simulation
	File "${src}\plugin\simulation\*"

	SetOutPath $INSTDIR\config
	File "${src}\config\*"

	SetOutPath $INSTDIR\examples
	File "${src}\examples\*"

	${registerExtension} "$INSTDIR\Simulation.exe" ".simz" "(AB)² Simulation"

	; Startmenü
	; execution path
	SetOutPath $INSTDIR

	CreateDirectory "$SMPROGRAMS\(AB)² Simulation"
	CreateShortCut "$SMPROGRAMS\(AB)² Simulation\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
	CreateShortCut "$SMPROGRAMS\(AB)² Simulation\(AB)² Simulation.lnk" "$INSTDIR\Simulation.exe" "" "$INSTDIR\Simulation.exe" 0
	CreateShortcut "$SMPROGRAMS\(AB)² Simulation\Beispiele.lnk" "$INSTDIR\examples"


	; Write the installation path into the registry
	WriteRegStr HKLM SOFTWARE\AB2Simulation "Install_Dir" "$INSTDIR"

	; Write the uninstall keys for Windows
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\AB2Simulation" "DisplayName" "(AB)² Simulation"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\AB2Simulation" "UninstallString" '"$INSTDIR\uninstall.exe"'
	WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\AB2Simulation" "NoModify" 1
	WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\AB2Simulation" "NoRepair" 1
	WriteUninstaller "uninstall.exe"

SectionEnd

;--------------------------------

; Uninstaller

Section "Uninstall"

	; Remove registry keys
	DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\AB2Simulation"
	DeleteRegKey HKLM SOFTWARE\AB2Simulation

	; Remove files and uninstaller
	Delete "$INSTDIR\config\*.*"
	RMDir "$INSTDIR\config"
	Delete "$INSTDIR\lib\*.*"
	RMDir "$INSTDIR\lib"
	Delete "$INSTDIR\plugin\import\*.*"
	RMDir "$INSTDIR\plugin\import"
	Delete "$INSTDIR\plugin\simulation\*.*"
	RMDir "$INSTDIR\plugin\simulation"
	Delete "$INSTDIR\plugin\*.*"
	RMDir "$INSTDIR\plugin"
	Delete "$INSTDIR\examples\*.*"
	RMDir "$INSTDIR\examples"
	Delete "$INSTDIR\*.*"
	RMDir "$INSTDIR"

	; Remove shortcuts, if any
	Delete "$SMPROGRAMS\(AB)² Simulation\*.*"

	; Remove directories used
	RMDir "$SMPROGRAMS\(AB)² Simulation"

	${unregisterExtension} ".simz" "(AB)² Simulation"

SectionEnd
