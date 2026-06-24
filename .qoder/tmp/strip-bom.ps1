$ErrorActionPreference = 'Stop'
Set-Location -Path "E:\Project\loktar"

$utf8NoBom = New-Object System.Text.UTF8Encoding $false

$files = Get-ChildItem -Path "src\main\java" -Recurse -Filter *.java
$count = 0
foreach ($file in $files) {
    $bytes = [System.IO.File]::ReadAllBytes($file.FullName)
    if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
        $newBytes = New-Object byte[] ($bytes.Length - 3)
        [Array]::Copy($bytes, 3, $newBytes, 0, $bytes.Length - 3)
        [System.IO.File]::WriteAllBytes($file.FullName, $newBytes)
        $count++
        Write-Host "Stripped BOM:" $file.FullName
    }
}
Write-Host "Total stripped:" $count
