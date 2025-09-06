<#
.SYNOPSIS
  Настройка переменных окружения PowerShell и Windows для DeepSeek.

.ВЫХОД
  Устанавливаются переменные окружения DeepSeek Key и LLM конфигурация.
.ИСПОЛЬЗОВАНИЕ:
  .\setup-deepseek-env.ps1
#>

Write-Host "=== Настройка окружения для DeepSeek ===" -ForegroundColor Cyan

$apiKey = Read-Host -Prompt "sk-91798c1d888547db9186768b50e9f3c6"

$env:DEEPSEEK_API_KEY = $apiKey
setx DEEPSEEK_API_KEY "$apiKey" | Out-Null

$provider = "deepseek"
$baseUrl = "https://api.deepseek.com"
$model = "deepseek-chat"

$env:LLM_PROVIDER = $provider
$env:OPENAI_BASE_URL = $baseUrl
$env:LLM_MODEL = $model

setx LLM_PROVIDER "$provider" | Out-Null
setx OPENAI_BASE_URL "$baseUrl" | Out-Null
setx LLM_MODEL "$model" | Out-Null

Write-Host "`nУстановлены переменные окружения:" -ForegroundColor Green
Write-Host " • DEEPSEEK_API_KEY"
Write-Host " • LLM_PROVIDER = $provider"
Write-Host " • OPENAI_BASE_URL = $baseUrl"
Write-Host " • LLM_MODEL = $model"

Write-Host "`nПерезапустите PowerShell или IDE, чтобы переменные вступили в силу." -ForegroundColor Yellow
Write-Host "`nDeepSeek окружение настроено!" -ForegroundColor Cyan
