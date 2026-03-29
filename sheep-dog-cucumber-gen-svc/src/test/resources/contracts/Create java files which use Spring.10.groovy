org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'DELETE'
        url ('/asciidoctor/clearConvertAsciidoctorToUMLObjects') {
            queryParameters {
            }
        }
        headers {
            header('scenarioId', 'Create java files which use Spring')
        }
    }
    response {
        status 200
    }
}
