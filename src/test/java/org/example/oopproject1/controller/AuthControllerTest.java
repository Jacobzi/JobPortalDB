package org.example.oopproject1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.oopproject1.dto.LoginRequest;
import org.example.oopproject1.security.JwtUtils;
import org.example.oopproject1.service.RecruiterService;
import org.example.oopproject1.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;             // ← add this
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RecruiterService recruiterService;

    @MockBean
    private JwtUtils jwtUtils;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void authenticateUser_returnsJwt() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("u");
        req.setPassword("p");
        given(jwtUtils.generateJwtToken(any())).willReturn("token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));
    }
}
