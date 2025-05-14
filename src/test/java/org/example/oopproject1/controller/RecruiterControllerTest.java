package org.example.oopproject1.controller;

import org.example.oopproject1.model.Recruiter;
import org.example.oopproject1.service.RecruiterService;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecruiterController.class)
class RecruiterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecruiterService recruiterService;

    @Test
    void getAllRecruiters_returnsOk() throws Exception {
        Recruiter r = new Recruiter(); r.setId("1"); r.setEmail("e@c");
        BDDMockito.given(recruiterService.getAllRecruiters()).willReturn(Arrays.asList(r));

        mockMvc.perform(get("/api/recruiters").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("e@c"));
    }
}