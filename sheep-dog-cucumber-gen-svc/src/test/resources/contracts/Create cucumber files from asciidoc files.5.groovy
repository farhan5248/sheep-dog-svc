org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'GET'
        url ('/cucumber/getConvertUMLToCucumberObjectNames') {
            queryParameters {
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
        body(file('bodies/Create cucumber files from asciidoc files.5.rsp.json'))
    }
}
