
package pl.wsb.fitnesstracker.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.wsb.fitnesstracker.user.dto.UserCreateDto;
import pl.wsb.fitnesstracker.user.dto.UserUpdateDto;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserCreateDto testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserCreateDto("John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com");
    }

    @Test
    void shouldCreateAndDeleteUser() throws Exception {
        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andReturn().getResponse().getContentAsString();

        Long userId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldSearchByEmail() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("email", "example"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldSearchByMinAge() throws Exception {
        mockMvc.perform(get("/api/users/age")
                        .param("minAge", "20"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldHandleUserNotFound() throws Exception {
        mockMvc.perform(get("/v1/users/{id}", 9999))
                .andExpect(status().isNotFound());
    }
}
