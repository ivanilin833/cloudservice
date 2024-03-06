package com.netology.diplombackend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.netology.diplombackend.domain.dto.request.EditFileNameRequest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class DBIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:latest")
            .withExposedPorts(3306)
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(3306), new ExposedPort(3306)))));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.jpa.generate-ddl", () -> true);
    }

    @Test
    void testIntegration() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBodyLogin = "{\"login\": \"user1\", \"password\": \"user_one\"}";

        var signInResponse = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyLogin))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode responseJson = objectMapper.readTree(signInResponse);

        String authToken = responseJson.get("auth-token").asText();
        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());

        //test upload
        mockMvc.perform(MockMvcRequestBuilders.multipart("/file")
                        .file(file)
                        .param("filename", "test.txt")
                        .header("auth-token", "Bearer " + authToken)
                        .cookie(new Cookie("auth-token", authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        //test get
        var list = mockMvc.perform(MockMvcRequestBuilders.get("/list")
                        .header("auth-token", "Bearer " + authToken)
                        .cookie(new Cookie("auth-token", authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        responseJson = objectMapper.readTree(list.getResponse().getContentAsString());
        Assertions.assertEquals(file.getOriginalFilename(), responseJson.get(0).get("filename").asText());

        //test download
        var downloadFile = mockMvc.perform(MockMvcRequestBuilders.get("/file")
                        .param("filename", file.getOriginalFilename())
                        .header("auth-token", "Bearer " + authToken)
                        .cookie(new Cookie("auth-token", authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        Assertions.assertArrayEquals(file.getBytes(), downloadFile.getResponse().getContentAsByteArray());

        //test editFileName
        var newName = "newName.txt";
        mockMvc.perform(MockMvcRequestBuilders.put("/file")
                        .param("filename", file.getOriginalFilename())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new EditFileNameRequest(newName)))
                        .header("auth-token", "Bearer " + authToken)
                        .cookie(new Cookie("auth-token", authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        var newList = mockMvc.perform(MockMvcRequestBuilders.get("/list")
                        .header("auth-token", "Bearer " + authToken)
                        .cookie(new Cookie("auth-token", authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        responseJson = objectMapper.readTree(newList.getResponse().getContentAsString());
        Assertions.assertEquals(newName, responseJson.get(0).get("filename").asText());

        //test deleteFile
        mockMvc.perform(MockMvcRequestBuilders.delete("/file")
                        .param("filename", newName)
                        .header("auth-token", "Bearer " + authToken)
                        .cookie(new Cookie("auth-token", authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        var emptyList = mockMvc.perform(MockMvcRequestBuilders.get("/list")
                        .header("auth-token", "Bearer " + authToken)
                        .cookie(new Cookie("auth-token", authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        responseJson = objectMapper.readTree(emptyList.getResponse().getContentAsString());
        Assertions.assertTrue(responseJson.isEmpty());
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
