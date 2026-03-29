org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'POST'
        url ('/asciidoctor/runConvertAsciidoctorToUML') {
            queryParameters {
                parameter fileName: 'src/test/resources/asciidoc/stepdefs/blah application/Object page.asciidoc'
            }
        }
        body(file('bodies/Create java files which use Guice.21.req.json'))
        headers {
            header('scenarioId', 'Create java files which use Guice')
        }
    }
    response {
        status 200
        headers {
            contentType('application/json')
        }
        body(file('bodies/Create java files which use Guice.21.rsp.json'))
    }
}
