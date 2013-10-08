; Script for Inno Setup QuickStart Pack software (version 5.3.10-u).
; Usage: ant installer

#define AppName "Compendium"
#define AppVerNumber "1.7.1"

#define AppBuild "Build 1370 "
#define AppURL "http://compendium.open.ac.uk/"
#define InstallerDir "installer"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{B255B748-6FDE-4AF2-9AA9-CBBF19CF4AAE}
AppName={#AppName}
AppVersion={#AppVerNumber} {#AppBuild}
AppVerName={#AppName} {#AppVerNumber} {#AppBuild}
AppPublisher=Verizon and The Open University UK
AppPublisherURL={#AppURL}
AppSupportURL={#AppURL}
AppUpdatesURL={#AppURL}

DefaultDirName={pf32}\{#AppName}

DefaultGroupName={#AppName}
LicenseFile={#InstallerDir}\installer-license.txt
OutputBaseFilename={#AppName} {#AppVerNumber} {#AppBuild}{#Configuration}
Compression=lzma
SolidCompression=yes
WindowVisible=yes
AppCopyright=Copyright (C) 1998-2010 Verizon & The Open University UK
WizardImageFile={#InstallerDir}\installer-logo.bmp

;AlwaysRestart=yes
ChangesEnvironment=yes
SetupLogging=yes

[Registry]
Root: HKLM; Subkey: "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"; ValueType: string; ValueName: "CompendiumSysPath"; ValueData: "{app}"
Root: HKLM; Subkey: "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"; ValueType: string; ValueName: "CompendiumUserPath"; ValueData: "{app}"

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"

[Dirs]
Name: "{app}\Templates"

Name: "{app}\Backups\"
Name: "{app}\Exports\"
Name: "{app}\Linked Files\"
Name: "{app}\Temp\"

Name: "{app}\System\resources\Databases\"
Name: "{app}\System\resources\Logs\"
Name: "{app}\System\resources\Meetings\"

[Files]
Source: "dist\Compendium.bat"; 				DestDir: "{app}\"; Flags: ignoreversion
Source: "dist\Compendium.dtd"; 				DestDir: "{app}\"; Flags: ignoreversion
Source: "dist\open_word_with_template.vbs"; 		DestDir: "{app}\"; Flags: ignoreversion
Source: "dist\Skins\*"; 				DestDir: "{app}\Skins\"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "dist\System\lib\*"; 				DestDir: "{app}\System\lib\"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "dist\System\resources\Audio\*"; 		DestDir: "{app}\System\resources\Audio\"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "dist\System\resources\Help\*"; 		DestDir: "{app}\System\resources\Help\"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "dist\System\resources\LinkGroups\*"; 		DestDir: "{app}\System\resources\LinkGroups\"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "dist\System\resources\Images\*"; 		DestDir: "{app}\System\resources\Images\"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "dist\System\resources\OutlineStyles\*"; 	DestDir: "{app}\System\resources\OutlineStyles\"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "dist\System\resources\ReferenceNodeIcons\*"; 	DestDir: "{app}\System\resources\ReferenceNodeIcons\"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "dist\System\resources\Stencils\*"; 		DestDir: "{app}\System\resources\Stencils\"; Flags: ignoreversion recursesubdirs createallsubdirs

Source: "dist\System\resources\*.properties"; 		DestDir: "{app}\System\resources\"; Flags: ignoreversion
Source: "dist\System\resources\toolbars.xml"; 		DestDir: "{app}\System\resources\"; Flags: ignoreversion

Source: "C:\Users\Public\Documents\Microsoft\Templates\Compendium-outline.dot"; DestDir: "C:\Users\Public\Documents\Microsoft\Templates"; DestName: "Compendium-outline-previous.dot"; Flags: ignoreversion external skipifsourcedoesntexist
Source: "{#InstallerDir}\Compendium-outline.dot"; DestDir: "C:\Users\Public\Documents\Microsoft\Templates"; Flags: ignoreversion

Source: "{userappdata}\Microsoft\Templates\Compendium-outline.dot"; DestDir: "{userappdata}\Microsoft\Templates"; DestName: "Compendium-outline-previous.dot"; Flags: ignoreversion external skipifsourcedoesntexist
Source: "{#InstallerDir}\Compendium-outline.dot"; DestDir: "{userappdata}\Microsoft\Templates"; Flags: ignoreversion

Source: "C:\Users\Public\Documents\Microsoft\Templates\Compendium-outline.dotm"; DestDir: "C:\Users\Public\Documents\Microsoft\Templates"; DestName: "Compendium-outline-previous.dotm"; Flags: ignoreversion external skipifsourcedoesntexist
Source: "{#InstallerDir}\Compendium-outline.dotm"; DestDir: "C:\Users\Public\Documents\Microsoft\Templates"; Flags: ignoreversion

Source: "{userappdata}\Microsoft\Templates\Compendium-outline.dotm"; DestDir: "{userappdata}\Microsoft\Templates"; DestName: "Compendium-outline-previous.dotm"; Flags: ignoreversion external skipifsourcedoesntexist
Source: "{#InstallerDir}\Compendium-outline.dotm"; DestDir: "{userappdata}\Microsoft\Templates"; Flags: ignoreversion

[Icons]
Name: "{group}\Compendium"; Filename: "{app}\Compendium.bat"; WorkingDir: "{app}"; IconFilename: "{app}\System\resources\Images\compendium.ico"
Name: "{group}\Uninstall Compendium"; Filename: "{uninstallexe}"
Name: "{commondesktop}\Compendium"; Filename: "{app}\Compendium.bat"; Tasks: desktopicon; WorkingDir: "{app}"; IconFilename: "{app}\System\resources\Images\compendium.ico"


[Code]
procedure DoPostInstall();

	var LogDir : string;
	var LogFile : string;
	var UserName : string;
	var DateTime : string;
	var InstallVersion : string;

begin

  LogDir := 'C:\Program Files\Compendium\';
  LogFile := 'CompendiumInstall.log';
  InstallVersion := ' Compendium 1.7.1 Build 1370 '
  UserName := GetUserNameString();
  DateTime := GetDateTimeString('mm/dd/yyyy hh:nn:ss am/pm', #0, #0);
  
  if (DirExists(LogDir)) then 
  begin
    SaveStringToFile(LogDir+LogFile, DateTime + InstallVersion + UserName + #13#10, True);
  end;
  
  if (DirExists('\\sce\workgroup\CEH&S1\ENVIRONMENTAL\SHARE\QM\Compendium_Admin\')) then
  	  begin
  	      SaveStringToFile('\\sce\workgroup\CEH&S1\ENVIRONMENTAL\SHARE\QM\Compendium_Admin\'+LogFile, DateTime + InstallVersion + UserName + #13#10, True);
  	  end
  else
  	  begin
  	  		SaveStringToFile(LogDir+LogFile, 'Error - sce directory for logging does not exist' + #13#10, True);
  	  end;
end;

procedure CurStepChanged(CurStep: TSetupStep);
begin
  if CurStep = ssPostInstall then 
  begin
    DoPostInstall();
  end;
end;

