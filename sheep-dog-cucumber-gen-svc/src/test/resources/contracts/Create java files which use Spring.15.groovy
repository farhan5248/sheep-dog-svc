org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'POST'
        url ('/cucumber/runConvertUMLToCucumberSpring') {
            queryParameters {
                parameter fileName: 'src-gen/test/resources/cucumber/specs/app/Process.feature'
            }
        }
        headers {
            header('scenarioId', 'Create java files which use Spring')
        }
    }
    response {
        status 200
        headers {
            contentType('application/json')
        }
        body(file('bodies/Create java files which use Spring.15.rsp.json'))
    }
}
