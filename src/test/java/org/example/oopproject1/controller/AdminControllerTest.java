package org.example.oopproject1.controller;

import org.example.oopproject1.model.User;
import org.example.oopproject1.security.JwtAuthenticationFilter;
import org.example.oopproject1.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Slice test for AdminController.
 * Excludes both Spring Security auto-config and our custom JwtAuthenticationFilter,
 * so we can hit /api/admin/users without any security beans failing to wire.
 */
@WebMvcTest(
        controllers = AdminController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // this prevents Spring from trying to instantiate your real filter (and blowing up on JwtUtils)
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // mock your service
    @MockBean
    private UserService userService;

    @Test
    @DisplayName("GET /api/admin/users → 200 OK with JSON array containing our user")
    void getAllUsers_returnsOk() throws Exception {
        // arrange
        User u = new User();
        u.setId("1");
        u.setUsername("u");
        u.setEmail("e");
        u.setRoles(Collections.singletonList("USER"));  // your controller maps roles into the DTO

        BDDMockito.given(userService.findAll()).willReturn(List.of(u));

        // act + assert
        mockMvc.perform(get("/api/admin/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username").value("u"));
    }
}
