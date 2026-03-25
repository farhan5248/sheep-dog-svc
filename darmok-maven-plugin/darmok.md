# Darmok

## Install

```
cd C:\Users\Farhan\git\sheep-dog-main\sheep-dog-cloud\darmok-maven-plugin
mvn install
```

## Goals

### gen-from-existing

Processes scenarios from a pre-populated `scenarios-list.txt`. Reads the next scenario, runs the red-green-refactor cycle, removes it from the file, and repeats until the file is empty.

```
cd C:\Users\Farhan\git\sheep-dog-main\sheep-dog-local\sheep-dog-grammar
mvn org.farhan:darmok-maven-plugin:gen-from-existing
```

### gen-from-comparison

Dynamically generates scenarios by calling the `/rgr-gen-from-comparison` skill, which compares the main branch to a RebuildXX branch. The skill writes one scenario to `scenarios-list.txt`, which is then processed through the red-green-refactor cycle. Repeats until the skill leaves the file empty.

```
cd C:\Users\Farhan\git\sheep-dog-main\sheep-dog-local\sheep-dog-grammar
mvn org.farhan:darmok-maven-plugin:gen-from-comparison
```

## Properties

| Property | Default | Description |
|---|---|---|
| specsDir | ../../sheep-dog-qa/sheep-dog-specs | Path to specs project (relative to baseDir) |
| asciidocDir | ../../sheep-dog-qa/sheep-dog-specs/src/test/resources/asciidoc/specs | Path to asciidoc files |
| scenariosFile | scenarios-list.txt | Scenarios list file in baseDir |
| host | dev.sheepdogdev.io | Service host |
| port | 80 | Service port |
| timeout | 300000 | Service health check timeout (ms) |
| modelRed | sonnet | Claude CLI model for red phase |
| modelGreen | sonnet | Claude CLI model for green phase |
| modelRefactor | sonnet | Claude CLI model for refactor phase |
| modelComparison | sonnet | Claude CLI model for comparison skill (gen-from-comparison only) |
| coAuthor | Claude Sonnet 4.5 <noreply@anthropic.com> | Git co-author for commits |
| maxRetries | 3 | Max retries for Claude CLI errors |
| retryWaitSeconds | 30 | Wait time between retries |
| pipeline | forward | RGR refactor pipeline |

Override properties with -D flags:

```
mvn org.farhan:darmok-maven-plugin:gen-from-existing -DmodelRed=opus -DmaxRetries=5
```
