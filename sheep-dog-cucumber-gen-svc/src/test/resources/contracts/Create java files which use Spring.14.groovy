org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'GET'
        url ('/cucumber/getConvertUMLToCucumberSpringObjectNames') {
            queryParameters {
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
        body(file('bodies/Create java files which use Spring.14.rsp.json'))
    }
}
