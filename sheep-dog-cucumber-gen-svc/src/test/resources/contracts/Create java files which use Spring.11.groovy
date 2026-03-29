org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'POST'
        url ('/asciidoctor/runConvertAsciidoctorToUML') {
            queryParameters {
                parameter fileName: 'src/test/resources/asciidoc/specs/app/Process.asciidoc'
            }
        }
        body(file('bodies/Create java files which use Spring.11.req.json'))
        headers {
            header('scenarioId', 'Create java files which use Spring')
        }
    }
    response {
        status 200
        headers {
            contentType('application/json')
        }
        body(file('bodies/Create java files which use Spring.11.rsp.json'))
    }
}
