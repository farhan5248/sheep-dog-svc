org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'POST'
        url ('/asciidoctor/runConvertAsciidoctorToUML') {
            queryParameters {
                parameter fileName: 'src/test/resources/asciidoc/specs/app/Process.asciidoc'
            }
        }
        body(file('bodies/Create java files which use Guice.20.req.json'))
        headers {
            header('scenarioId', 'Create java files which use Guice')
        }
    }
    response {
        status 200
        headers {
            contentType('application/json')
        }
        body(file('bodies/Create java files which use Guice.20.rsp.json'))
    }
}
