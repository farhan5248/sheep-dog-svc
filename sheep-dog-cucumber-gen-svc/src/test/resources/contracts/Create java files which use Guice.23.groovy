org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'GET'
        url ('/cucumber/getConvertUMLToCucumberGuiceObjectNames') {
            queryParameters {
            }
        }
        headers {
            header('scenarioId', 'Create java files which use Guice')
        }
    }
    response {
        status 200
        headers {
            contentType('application/json')
        }
        body(file('bodies/Create java files which use Guice.23.rsp.json'))
    }
}
