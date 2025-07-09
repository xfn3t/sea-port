package ru.sea.port;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import ru.sea.port.dto.UserDto; // Используем правильный DTO для создания
import ru.sea.port.dto.LoginRequest;
import ru.sea.port.dto.LoginResponse;
import ru.sea.port.dto.RoleDto;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Активируем порядок тестов
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS) // Пересоздаём контекст перед классом
class AdminUserCrudTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private static String adminToken;
    private static Long createdUserId;

    @BeforeAll
    static void initToken(@Autowired MockMvc mockMvc,
                          @Autowired ObjectMapper mapper) throws Exception {
        LoginRequest login = new LoginRequest("ivan@local.test", "password123");
        String resp = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andReturn()
            .getResponse()
            .getContentAsString();

        adminToken = mapper.readValue(resp, LoginResponse.class).getToken();
        Assertions.assertNotNull(adminToken, "JWT должен быть выдан");
    }

    @Test
    @Order(1)
    @DisplayName("ADMIN создаёт пользователя через /api/admin/users")
    void adminCreatesUser() throws Exception {
        // Используем специальный DTO для создания пользователя
        UserDto newUser = new UserDto();
        newUser.setFirstName("Test");
        newUser.setPatronomic("T");
        newUser.setLastName("User");
        newUser.setEmail("test.user@example.com");
        newUser.setPassword("testPass"); // Обязательно передаём пароль!
        newUser.setRole(new RoleDto(2L, "TERMINAL_OPERATOR")); // ID роли TERMINAL_OPERATOR

        String result = mockMvc.perform(post("/api/admin/users")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test.user@example.com"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        UserDto created = mapper.readValue(result, UserDto.class);
        createdUserId = created.getId();
        Assertions.assertNotNull(createdUserId, "Должен вернуться id созданного пользователя");
    }

    @Test
    @Order(2)
    @DisplayName("ADMIN видит нового пользователя в списке")
    void adminSeesUserInList() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.email=='test.user@example.com')]").exists());
    }

    @Test
    @Order(3)
    @DisplayName("ADMIN обновляет данные пользователя")
    void adminUpdatesUser() throws Exception {
        // Для обновления используем UserDto
        UserDto update = new UserDto();
        update.setId(createdUserId); // Важно установить ID
        update.setFirstName("Updated");
        update.setPatronomic("Tu");
        update.setLastName("User");
        update.setEmail("test.user@example.com");
        update.setRole(new RoleDto(3L, "TALLYMAN")); // Обновляем роль

        mockMvc.perform(put("/api/admin/users/{id}", createdUserId) // Исправленный URL с ID
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(update)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Updated"))
            .andExpect(jsonPath("$.role.role").value("TALLYMAN"));
    }

    @Test
    @Order(4)
    @DisplayName("ADMIN удаляет пользователя")
    void adminDeletesUser() throws Exception {
        mockMvc.perform(delete("/api/admin/users/{id}", createdUserId) // Исправленный URL с ID
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());

        // Проверяем, что пользователь действительно удалён
        mockMvc.perform(get("/api/admin/users/{id}", createdUserId)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isNotFound());
    }
}