param([string[]] $Service = @('*'), [switch] $Stop)

$ErrorActionPreference = 'Stop'
$project = Split-Path -Parent $PSScriptRoot
$legacyRun = Join-Path $project '.pocketlife'
$run = if ($env:POCKETLIFE_RUN_DIR) {
    [IO.Path]::GetFullPath($env:POCKETLIFE_RUN_DIR)
} else {
    $legacyRun
}
New-Item -ItemType Directory -Force -Path $run | Out-Null

if (-not $Stop -and -not $env:POCKETLIFE_MYSQL_PASSWORD) {
    $env:POCKETLIFE_MYSQL_PASSWORD = docker exec mysql8.0 sh -c 'printf "%s" "$MYSQL_ROOT_PASSWORD"'
    if ($LASTEXITCODE -ne 0 -or -not $env:POCKETLIFE_MYSQL_PASSWORD) {
        throw 'Set POCKETLIFE_MYSQL_PASSWORD or start the mysql8.0 container.'
    }
}
if (-not $Stop -and -not $env:POCKETLIFE_MYSQL_DECRYPT) {
    $env:POCKETLIFE_MYSQL_DECRYPT = 'false'
}
if (-not $Stop -and -not $env:SPRING_DATA_REDIS_PASSWORD -and (docker exec redis redis-cli ping) -eq 'PONG') {
    $env:SPRING_APPLICATION_JSON = '{"spring":{"data":{"redis":{"password":""}}}}'
}

$java = (Get-Command java.exe).Source
$apps = Get-ChildItem -LiteralPath $project -Recurse -File -Filter '*-SNAPSHOT.jar' |
    Where-Object { $_.Directory.Name -eq 'target' -and (Test-Path -LiteralPath ($_.FullName + '.original')) }
if (-not $apps) {
    throw 'Run mvn clean package -DskipTests from the project root first.'
}

$pidFile = Join-Path $run 'processes.txt'
$pidFiles = @($pidFile, (Join-Path $legacyRun 'processes.txt')) | Select-Object -Unique
$previous = Get-Content -LiteralPath $pidFiles -ErrorAction SilentlyContinue
foreach ($app in $apps) {
    $name = $app.BaseName -replace '-[0-9].*$', ''
    if (-not ($Service | Where-Object { $name -like $_ })) {
        continue
    }
    $running = $previous | Where-Object { $_ -match "^$name ([0-9]+)$" } | ForEach-Object {
        $processId = [int](($_ -split ' ')[-1])
        Get-CimInstance Win32_Process -Filter "ProcessId=$processId"
    } | Where-Object { $_.Name -eq 'java.exe' -and $_.CommandLine.Contains($app.FullName) }
    if ($Stop) {
        if ($running) {
            Stop-Process -Id $running.ProcessId
            Write-Output "$name stopped"
        }
        continue
    }
    if ($running) {
        Write-Output "$name already running"
        continue
    }

    $options = @('--spring.cloud.nacos.discovery.ip=127.0.0.1', '--server.address=127.0.0.1')
    if ($name -eq 'pocketlife-data-align') {
        $options += '--xxl.job.ip=host.docker.internal', '--xxl.job.log-path=xxl-job'
    }
    if ($name -eq 'pocketlife-oss-biz') {
        $options += '--spring.cloud.nacos.config.enabled=false', '--storage.type=minio'
        $env:STORAGE_MINIO_ACCESS_KEY = docker exec minio sh -c 'printf "%s" "$MINIO_ROOT_USER"'
        $env:STORAGE_MINIO_SECRET_KEY = docker exec minio sh -c 'printf "%s" "$MINIO_ROOT_PASSWORD"'
    }
    $start = @{
        FilePath = $java
        ArgumentList = @('-XX:ActiveProcessorCount=2', '-Xms48m', '-Xmx240m', '-Dfile.encoding=UTF-8',
            '-jar', ('"' + $app.FullName + '"')) + $options
        WorkingDirectory = $run
        WindowStyle = 'Hidden'
        RedirectStandardOutput = Join-Path $run "$name.out"
        RedirectStandardError = Join-Path $run "$name.err"
        PassThru = $true
    }
    $process = Start-Process @start
    "$name $($process.Id)" | Tee-Object -FilePath $pidFile -Append
}
