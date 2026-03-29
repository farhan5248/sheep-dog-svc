org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'POST'
        url ('/asciidoctor/runConvertAsciidoctorToUML') {
            queryParameters {
                parameter fileName: 'src/test/resources/asciidoc/stepdefs/blah application/Object page.asciidoc'
            }
        }
        body(file('bodies/Create cucumber files from asciidoc files.3.req.json'))
        headers {
            header('scenarioId', 'Create cucumber files from asciidoc files')
        }
    }
    response {
        status 200
        headers {
            contentType('application/json')
        }
        body(file('bodies/Create cucumber files from asciidoc files.3.rsp.json'))
    }
}
