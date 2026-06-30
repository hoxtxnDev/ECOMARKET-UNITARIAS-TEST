param(
    [string]$BaseUrl = "http://localhost:8087"
)

$productos = Get-Content "$PSScriptRoot\productos.json" -Encoding UTF8 | ConvertFrom-Json
$total = $productos.Count
$ok = 0
$err = 0

Write-Host "Importando $total productos a $BaseUrl/api/catalogo ..." -ForegroundColor Cyan

foreach ($p in $productos) {
    $body = $p | ConvertTo-Json -Compress
    try {
        $r = Invoke-RestMethod -Uri "$BaseUrl/api/catalogo" -Method Post -Body $body -ContentType "application/json"
        $ok++
        Write-Host "  [$ok/$total] OK $($p.sku) - $($p.nombre)" -ForegroundColor Green
    } catch {
        $err++
        Write-Host "  [ERROR] $($p.sku) - $($p.nombre): $_" -ForegroundColor Red
    }
}

Write-Host "`nImportación completada. OK: $ok  |  ERROR: $err" -ForegroundColor Cyan
