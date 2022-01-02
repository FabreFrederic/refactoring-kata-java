package com.sipios.refactoring.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ShoppingControllerIntTest {
    private MockMvc mockMvc;

    @BeforeEach
    void initMocks() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ShoppingController()).build();
    }

    @Test
    void should_get_price() throws Exception {
        MvcResult result = mockMvc.perform(post("/shopping")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"items\":[{\"type\":\"TSHIRT\",\"quantity\":2}],\"type\":\"STANDARD_CUSTOMER\"}")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("60.0");
    }
}
