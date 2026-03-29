org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'DELETE'
        url ('/asciidoctor/clearConvertAsciidoctorToUMLObjects') {
            queryParameters {
            }
        }
        headers {
            header('scenarioId', 'Create java files which use Guice')
        }
    }
    response {
        status 200
    }
}
