# Extract all feature files, scenario names, and pattern tags from AsciiDoc files
# Output: scenarios-list.txt

$SpecsDir = "C:\Users\Farhan\git\sheep-dog-main\sheep-dog-specs\sheep-dog-features\src\test\resources\asciidoc\specs\Ubiquitous Language"
$OutputFile = "C:\Users\Farhan\git\sheep-dog-main\scripts\scenarios-list.txt"

# Clear output file if it exists
if (Test-Path $OutputFile) {
    Remove-Item $OutputFile
}

# Get all .asciidoc files
$files = Get-ChildItem -Path $SpecsDir -Filter "*.asciidoc" | Sort-Object Name

foreach ($file in $files) {
    $fileName = $file.BaseName
    $content = Get-Content $file.FullName

    # Output the file name
    Add-Content -Path $OutputFile -Value "File: $fileName"

    # Process each line to find Test-Case and their tags
    for ($i = 0; $i -lt $content.Length; $i++) {
        $line = $content[$i]

        # Check if this is a Test-Case line
        if ($line -match "^== Test-Case:") {
            $scenarioName = $line -replace "^== Test-Case: ", ""

            # Look for the tag in the next few lines (skip empty lines)
            $tag = "NoTag"
            for ($j = $i + 1; $j -lt $content.Length -and $j -lt $i + 5; $j++) {
                $nextLine = $content[$j].Trim()
                if ($nextLine -match "^@(.+)$") {
                    $tag = $matches[1]
                    break
                }
                # Stop if we hit another section or non-empty content that isn't a tag
                if ($nextLine -and -not ($nextLine -match "^@")) {
                    break
                }
            }

            Add-Content -Path $OutputFile -Value "  Scenario: $scenarioName"
            Add-Content -Path $OutputFile -Value "    Tag: $tag"
        }
    }

    # Add blank line between files
    Add-Content -Path $OutputFile -Value ""
}

Write-Host "Scenario list extracted to: $OutputFile"
Write-Host "Total files processed: $($files.Count)"
