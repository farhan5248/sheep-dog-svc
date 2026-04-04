# Remove all tags from AsciiDoc files
# This removes lines starting with @ that appear after Test-Case declarations

$SpecsDir = "C:\Users\Farhan\git\sheep-dog-main\sheep-dog-specs\sheep-dog-features\src\test\resources\asciidoc\specs\Ubiquitous Language"

# Get all .asciidoc files
$files = Get-ChildItem -Path $SpecsDir -Filter "*.asciidoc" | Sort-Object Name

$totalTagsRemoved = 0
$filesModified = 0

foreach ($file in $files) {
    Write-Host "Processing: $($file.Name)"

    $content = Get-Content $file.FullName
    $newContent = @()
    $tagsRemovedInFile = 0
    $previousLineWasTestCase = $false

    for ($i = 0; $i -lt $content.Length; $i++) {
        $line = $content[$i]

        # Check if this is a Test-Case line
        if ($line -match "^== Test-Case:") {
            $newContent += $line
            $previousLineWasTestCase = $true
            continue
        }

        # Skip tag lines that come after Test-Case (and empty lines before tags)
        if ($line -match "^@\w+\s*$") {
            Write-Host "  Removing tag: $line"
            $tagsRemovedInFile++
            $totalTagsRemoved++
            continue
        }

        # If we hit non-tag content, reset the flag
        if ($line.Trim() -ne "" -and -not ($line -match "^@")) {
            $previousLineWasTestCase = $false
        }

        $newContent += $line
    }

    if ($tagsRemovedInFile -gt 0) {
        # Write the modified content back to the file
        Set-Content -Path $file.FullName -Value $newContent
        $filesModified++
        Write-Host "  Tags removed: $tagsRemovedInFile" -ForegroundColor Green
    } else {
        Write-Host "  No tags found" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "=========================================="
Write-Host "Summary:"
Write-Host "  Files processed: $($files.Count)"
Write-Host "  Files modified: $filesModified"
Write-Host "  Total tags removed: $totalTagsRemoved"
Write-Host "=========================================="
Write-Host ""
Write-Host "IMPORTANT: Commit these changes before proceeding!"
Write-Host "This creates a rollback point for the tag removal."
