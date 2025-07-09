package ru.sea.port;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import ru.sea.port.dto.RegisterRequest;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private org.springframework.test.web.servlet.MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("Регистрация и логин нового пользователя в Postgres")
    void registerAndLogin() throws Exception {
        // // 1) Регистрируем пользователя (CommandLineRunner уже создал роли)
        // RegisterRequest req = new RegisterRequest(
        //     "Ivan", "Ivanovich", "Ivanov",
        //     "ivan@local.test", "password123",
        //     1L  // предполагаем, что роль ADMIN в БД имеет id=1
        // );

        // mockMvc.perform(post("/api/auth/register")
        //         .contentType(MediaType.APPLICATION_JSON)
        //         .content(mapper.writeValueAsString(req)))
        //     .andExpect(status().isOk())
        //     .andExpect(jsonPath("$.email").value("ivan@local.test"))
        //     .andExpect(jsonPath("$.role.role").value("ADMIN"));

        // 2) Логинимся тем же email/password
        String loginJson = """
            {
              "email": "ivan@local.test",
              "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(loginJson))
    .andDo(print())                      
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.token").isNotEmpty())
    .andExpect(jsonPath("$.role.role").value("ADMIN"));
    }
}
