package org.farhan.runners.surefire.webmvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.farhan.mbt.asciidoctor.ConvertAsciidoctorToUML;
import org.farhan.mbt.controller.AsciiDoctorController;
import org.farhan.dsl.grammar.IResourceRepository;
import org.farhan.mbt.exception.TransformationException;
import org.farhan.mbt.service.AsciiDoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ContextConfiguration(classes = MockitoTestConfig.class)
@ActiveProfiles("surefire")
@WebMvcTest(AsciiDoctorController.class)
public class TransformationControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private IResourceRepository repository;

        @MockitoBean
        private AsciiDoctorService service;

        @Test
        public void testClearConvertAsciidoctorToUMLObjectsTransformationException() throws Exception {
                String message = "Clearing objects";
                doThrow(new org.farhan.mbt.exception.TransformationException(message))
                                .when(service).clearObjects(any(ConvertAsciidoctorToUML.class));

                mockMvc.perform(delete("/asciidoctor/clearConvertAsciidoctorToUMLObjects"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message", is("Error transforming object: " + message)));
        }

        @Test
        public void testRunConvertAsciidoctorToUMLPublishingException() throws Exception {
                String fileName = "src/test/resources/asciidoc/stepdefs/blah application/Object page.asciidoc";
                String contents = "dummy content";
                String message = fileName;
                doThrow(new org.farhan.mbt.exception.PublishingException(message)).when(service)
                                .convertSourceObject(any(ConvertAsciidoctorToUML.class), any());

                mockMvc.perform(post("/asciidoctor/runConvertAsciidoctorToUML")
                                .param("tags", "")
                                .param("fileName", fileName)
                                .content(contents)
                                .contentType("application/json"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message", is("Error publishing object: " + message)));
        }

        @Test
        public void testRunConvertAsciidoctorToUMLTransformationException() throws Exception {
                String fileName = "src/test/resources/asciidoc/stepdefs/blah application/Object page.asciidoc";
                String contents = "dummy content";
                String message = fileName;
                doThrow(new org.farhan.mbt.exception.TransformationException(message)).when(service)
                                .convertSourceObject(any(ConvertAsciidoctorToUML.class), any());

                mockMvc.perform(post("/asciidoctor/runConvertAsciidoctorToUML")
                                .param("tags", "")
                                .param("fileName", fileName)
                                .content(contents)
                                .contentType("application/json"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message", is("Error transforming object: " + message)));
        }

        @Test
        public void testGetConvertUMLToAsciiDoctorObjectNamesTransformationException() throws Exception {

                String tags = "tag1";
                String message = "Getting object names for tags: " + tags;
                org.mockito.Mockito
                                .when(service.getObjectNames(org.mockito.ArgumentMatchers.any(),
                                                org.mockito.ArgumentMatchers.eq(tags)))
                                .thenThrow(new TransformationException(message));

                mockMvc.perform(get("/asciidoctor/getConvertUMLToAsciidoctorObjectNames")
                                .param("tags", tags))
                                .andExpect(status().isInternalServerError())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message", is("Error transforming object: " + message)));
        }

        @Test
        public void testRunConvertUMLToAsciiDoctorTransformationException() throws Exception {
                String fileName = "src-gen/test/java/org/farhan/objects/blah/ObjectPage.java";
                String tags = "tag1";
                String contents = "";
                String message = fileName;
                org.mockito.Mockito
                                .when(service.convertObject(org.mockito.ArgumentMatchers.any(),
                                                org.mockito.ArgumentMatchers.anyString(),
                                                org.mockito.ArgumentMatchers.any()))
                                .thenThrow(new TransformationException(message));

                mockMvc.perform(post("/asciidoctor/runConvertUMLToAsciidoctor")
                                .param("tags", tags)
                                .param("fileName", fileName)
                                .content(contents)
                                .contentType("application/json"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message", is("Error transforming object: " + message)));
        }
}
