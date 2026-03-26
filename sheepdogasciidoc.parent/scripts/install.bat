cd ..
call gradle clean
call gradle publishToMavenLocal --refresh-dependencies
call gradle xtextasciidocplugin.vscode:installExtension 
cd scripts 