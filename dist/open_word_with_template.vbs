tempname="Compendium-outline.dotm"

Set WSHShell = CreateObject("WScript.Shell")

strRegistryKey = "HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\App Paths\Winword.exe\Path"

wordpath = WSHShell.RegRead( strRegistryKey )
rem userpf = WSHShell.ExpandEnvironmentStrings( "%USERPROFILE%" )
userpf = WSHShell.ExpandEnvironmentStrings( "%Public%" )

wordpath = chr(34) & wordpath & "winword.exe" & chr(34)
temppath = userpf & "\Documents\Microsoft\Templates\" & tempname
cmdline = wordpath & " /f """ & temppath & """ /minsert_and_format_outline" 

WSHShell.run cmdline,3

set WSHShell=nothing
