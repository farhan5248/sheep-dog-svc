org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'POST'
        url ('/cucumber/runConvertUMLToCucumber') {
            queryParameters {
                parameter fileName: 'src-gen/test/java/org/farhan/objects/blah/ObjectPage.java'
            }
        }
        headers {
            header('scenarioId', 'Create cucumber files from asciidoc files')
        }
    }
    response {
        status 200
        headers {
            contentType('application/json')
        }
        body(file('bodies/Create cucumber files from asciidoc files.8.rsp.json'))
    }
}
