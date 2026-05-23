$results = @()
$pass = 0
$fail = 0

function Test-Case($id, $name, $script) {
    Write-Host ("{0}: {1}" -f $id, $name) -ForegroundColor Yellow -NoNewline
    try {
        $result = & $script
        if ($result.Passed) {
            Write-Host " [PASS]" -ForegroundColor Green
            $global:pass++
        } else {
            Write-Host (" [FAIL] {0}" -f $result.Message) -ForegroundColor Red
            $global:fail++
        }
        $result | Add-Member -NotePropertyName CaseId -NotePropertyValue $id -Force
        $result | Add-Member -NotePropertyName CaseName -NotePropertyValue $name -Force
        $global:results += $result
    } catch {
        Write-Host (" [ERROR] {0}" -f $_.Exception.Message) -ForegroundColor Red
        $global:fail++
        $global:results += @{ CaseId=$id; CaseName=$name; Passed=$false; Message=$_.Exception.Message }
    }
}

$base = "http://localhost:8080"
$ct = "application/json"

Write-Host ""
Write-Host "=== 1. Register Tests ===" -ForegroundColor Cyan

Test-Case "REG-001" "Register RECRUITER user" {
    $b = '{"username":"apitest_rec","password":"123456","realName":"API Recruiter","role":"RECRUITER"}'
    $r = Invoke-WebRequest -Uri "$base/api/v1/auth/register" -Method Post -Body $b -ContentType $ct -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 200); Message=("code="+$j.code) }
}

Test-Case "REG-002" "Register APPROVER user" {
    $b = '{"username":"apitest_app","password":"123456","realName":"API Approver","role":"APPROVER"}'
    $r = Invoke-WebRequest -Uri "$base/api/v1/auth/register" -Method Post -Body $b -ContentType $ct -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 200); Message=("code="+$j.code) }
}

Test-Case "REG-003" "Register VISITOR user" {
    $b = '{"username":"apitest_vis","password":"123456","realName":"API Visitor","role":"VISITOR"}'
    $r = Invoke-WebRequest -Uri "$base/api/v1/auth/register" -Method Post -Body $b -ContentType $ct -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 200); Message=("code="+$j.code) }
}

Test-Case "REG-004" "Duplicate username rejected" {
    $b = '{"username":"apitest_rec","password":"654321","realName":"Dup"}'
    $r = Invoke-WebRequest -Uri "$base/api/v1/auth/register" -Method Post -Body $b -ContentType $ct -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 500); Message=("code="+$j.code) }
}

Write-Host ""
Write-Host "=== 2. Login Tests ===" -ForegroundColor Cyan

Test-Case "LOGIN-001" "Login with correct credentials" {
    $b = '{"username":"apitest_rec","password":"123456"}'
    $r = Invoke-WebRequest -Uri "$base/api/v1/auth/login" -Method Post -Body $b -ContentType $ct -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    $ok = ($j.code -eq 200) -and ($null -ne $j.data.role) -and ($j.data.role -eq "RECRUITER")
    return @{ Passed=$ok; Message=("code="+$j.code+",role="+$j.data.role+",token="+$j.data.token) }
}

Test-Case "LOGIN-002" "Login with wrong password rejected" {
    $b = '{"username":"apitest_rec","password":"wrongpwd"}'
    $r = Invoke-WebRequest -Uri "$base/api/v1/auth/login" -Method Post -Body $b -ContentType $ct -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 400); Message=("code="+$j.code) }
}

Test-Case "LOGIN-003" "Login nonexistent user rejected" {
    $b = '{"username":"notexistuser","password":"123456"}'
    $r = Invoke-WebRequest -Uri "$base/api/v1/auth/login" -Method Post -Body $b -ContentType $ct -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 400); Message=("code="+$j.code) }
}

Test-Case "LOGIN-004" "Admin user login" {
    $b = '{"username":"admin","password":"123456"}'
    $r = Invoke-WebRequest -Uri "$base/api/v1/auth/login" -Method Post -Body $b -ContentType $ct -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    $ok = ($j.code -eq 200) -and ($j.data.role -eq "RECRUITER")
    return @{ Passed=$ok; Message=("code="+$j.code+",role="+$j.data.role) }
}

Write-Host ""
Write-Host "=== 3. Role Permission Tests ===" -ForegroundColor Cyan

Test-Case "ROLE-001" "VISITOR can view position list" {
    $r = Invoke-WebRequest -Uri "$base/webapi/positions" -Method Get -Headers @{"X-User-Role"="VISITOR"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 200); Message=("code="+$j.code) }
}

Test-Case "ROLE-002" "VISITOR cannot create position (403)" {
    $b = '{"title":"VisitorTest","description":"Should fail"}'
    $r = Invoke-WebRequest -Uri "$base/webapi/positions" -Method Post -Body $b -ContentType $ct -Headers @{"X-User-Role"="VISITOR"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 403); Message=("code="+$j.code) }
}

Test-Case "ROLE-003" "VISITOR cannot delete position (403)" {
    $r = Invoke-WebRequest -Uri "$base/webapi/positions/1" -Method Delete -Headers @{"X-User-Role"="VISITOR"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 403); Message=("code="+$j.code) }
}

Test-Case "ROLE-004" "RECRUITER can create position" {
    $b = '{"title":"Java Backend Engineer","description":"Spring Boot development","status":"DRAFT"}'
    $r = Invoke-WebRequest -Uri "$base/webapi/positions" -Method Post -Body $b -ContentType $ct -Headers @{"X-User-Role"="RECRUITER"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 200); Message=("code="+$j.code) }
}

Test-Case "ROLE-005" "RECRUITER cannot approve (403)" {
    $b = '{"action":"APPROVE","comment":"test"}'
    $r = Invoke-WebRequest -Uri "$base/webapi/positions/1/approve?pass=true" -Method Post -Body $b -ContentType $ct -Headers @{"X-User-Role"="RECRUITER"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 403); Message=("code="+$j.code) }
}

Test-Case "ROLE-006" "APPROVER cannot create position (403)" {
    $b = '{"title":"ApproverTest","description":"Should fail"}'
    $r = Invoke-WebRequest -Uri "$base/webapi/positions" -Method Post -Body $b -ContentType $ct -Headers @{"X-User-Role"="APPROVER"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 403); Message=("code="+$j.code) }
}

Test-Case "ROLE-007" "APPROVER can view positions" {
    $r = Invoke-WebRequest -Uri "$base/webapi/positions" -Method Get -Headers @{"X-User-Role"="APPROVER"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 200); Message=("code="+$j.code) }
}

Test-Case "ROLE-008" "No role header defaults VISITOR (403)" {
    $b = '{"title":"NoHeaderTest","description":"test"}'
    $r = Invoke-WebRequest -Uri "$base/webapi/positions" -Method Post -Body $b -ContentType $ct -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 403); Message=("code="+$j.code) }
}

Write-Host ""
Write-Host "=== 4. Position CRUD Tests ===" -ForegroundColor Cyan

Test-Case "CRUD-001" "Create position (Frontend Engineer)" {
    $b = '{"title":"Frontend Engineer","description":"Vue/React development","status":"DRAFT"}'
    $r = Invoke-WebRequest -Uri "$base/webapi/positions" -Method Post -Body $b -ContentType $ct -Headers @{"X-User-Role"="RECRUITER"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 200); Message=("code="+$j.code) }
}

Test-Case "CRUD-002" "Create position (QA Engineer)" {
    $b = '{"title":"QA Engineer","description":"Functional testing","status":"DRAFT"}'
    $r = Invoke-WebRequest -Uri "$base/webapi/positions" -Method Post -Body $b -ContentType $ct -Headers @{"X-User-Role"="RECRUITER"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 200); Message=("code="+$j.code) }
}

Test-Case "CRUD-003" "Query all positions" {
    $r = Invoke-WebRequest -Uri "$base/webapi/positions" -Method Get -Headers @{"X-User-Role"="RECRUITER"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    $cnt = if ($j.data -is [array]) { $j.data.Count } else { if ($null -ne $j.data) { 1 } else { 0 } }
    return @{ Passed=($j.code -eq 200 -and $cnt -gt 0); Message=("code="+$j.code+",count="+$cnt) }
}

Test-Case "CRUD-004" "Search by keyword" {
    $r = Invoke-WebRequest -Uri "$base/webapi/positions?keyword=Java" -Method Get -Headers @{"X-User-Role"="RECRUITER"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 200); Message=("code="+$j.code) }
}

Test-Case "CRUD-005" "Update position (DRAFT editable)" {
    $b = '{"title":"Java Senior Engineer","description":"Architecture design","status":"DRAFT"}'
    $r = Invoke-WebRequest -Uri "$base/webapi/positions/1" -Method Put -Body $b -ContentType $ct -Headers @{"X-User-Role"="RECRUITER"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 200); Message=("code="+$j.code) }
}

Test-Case "CRUD-006" "Empty title rejected (400)" {
    $b = '{"title":"","description":"Empty title"}'
    $r = Invoke-WebRequest -Uri "$base/webapi/positions" -Method Post -Body $b -ContentType $ct -Headers @{"X-User-Role"="RECRUITER"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 400); Message=("code="+$j.code) }
}

Write-Host ""
Write-Host "=== 5. Approval Flow Tests ===" -ForegroundColor Cyan

Test-Case "APR-001" "Submit for approval (DRAFT->PENDING)" {
    $r = Invoke-WebRequest -Uri "$base/webapi/positions/1/submit" -Method Post -Headers @{"X-User-Role"="RECRUITER"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 200); Message=("code="+$j.code) }
}

Test-Case "APR-002" "APPROVER approve (PENDING->PUBLISHED)" {
    $b = '{"action":"APPROVE","comment":"Approved"}'
    $r = Invoke-WebRequest -Uri "$base/webapi/positions/1/approve?pass=true" -Method Post -Body $b -ContentType $ct -Headers @{"X-User-Role"="APPROVER"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 200); Message=("code="+$j.code) }
}

Test-Case "APR-003" "Approve non-PENDING rejected (400)" {
    $b = '{"action":"APPROVE","comment":"Should fail"}'
    $r = Invoke-WebRequest -Uri "$base/webapi/positions/1/approve?pass=true" -Method Post -Body $b -ContentType $ct -Headers @{"X-User-Role"="APPROVER"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 400); Message=("code="+$j.code) }
}

Test-Case "APR-004" "Submit second position for approval" {
    $r = Invoke-WebRequest -Uri "$base/webapi/positions/2/submit" -Method Post -Headers @{"X-User-Role"="RECRUITER"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 200); Message=("code="+$j.code) }
}

Test-Case "APR-005" "APPROVER reject (PENDING->DRAFT)" {
    $b = '{"action":"REJECT","comment":"Need more tests"}'
    $r = Invoke-WebRequest -Uri "$base/webapi/positions/2/approve?pass=false" -Method Post -Body $b -ContentType $ct -Headers @{"X-User-Role"="APPROVER"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 200); Message=("code="+$j.code) }
}

Test-Case "APR-006" "Delete DRAFT position" {
    $r = Invoke-WebRequest -Uri "$base/webapi/positions/2" -Method Delete -Headers @{"X-User-Role"="RECRUITER"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 200); Message=("code="+$j.code) }
}

Test-Case "APR-007" "Delete PUBLISHED position rejected (400)" {
    $r = Invoke-WebRequest -Uri "$base/webapi/positions/1" -Method Delete -Headers @{"X-User-Role"="RECRUITER"} -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    return @{ Passed=($j.code -eq 400); Message=("code="+$j.code) }
}

# ==================== Summary ====================
Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Test Results Summary" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ("Total: {0}" -f $results.Count) -ForegroundColor White
Write-Host ("Passed: {0}" -f $pass) -ForegroundColor Green
Write-Host ("Failed: {0}" -f $fail) -ForegroundColor Red

if ($results.Count -gt 0) {
    $rate = [math]::Round($pass / $results.Count * 100, 2)
    $color = if ($rate -ge 90) { "Green" } else { "Yellow" }
    Write-Host ("Rate: {0}%" -f $rate) -ForegroundColor $color
}

Write-Host ""
Write-Host "Details:" -ForegroundColor Cyan
foreach ($r in $results) {
    $mark = if ($r.Passed) { "PASS" } else { "FAIL" }
    Write-Host ("  {0} {1} {2}" -f $mark, $r.CaseId, $r.CaseName)
}

$results | ConvertTo-Json -Depth 3 | Out-File -FilePath "d:\Hr_System1\test-results.json" -Encoding UTF8
Write-Host ""
Write-Host "JSON report saved: d:\Hr_System1\test-results.json" -ForegroundColor Green

if ($fail -gt 0) { exit 1 } else { exit 0 }
