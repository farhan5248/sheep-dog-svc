org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'DELETE'
        url ('/asciidoctor/clearConvertAsciidoctorToUMLObjects') {
            queryParameters {
            }
        }
        headers {
            header('scenarioId', 'Create cucumber files from asciidoc files')
        }
    }
    response {
        status 200
    }
}
