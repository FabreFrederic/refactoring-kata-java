package com.sipios.refactoring.controller;

import com.sipios.refactoring.service.ShoppingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ShoppingControllerIntTest {
    @MockBean
    private ShoppingService shoppingService;

    private MockMvc mockMvc;

    @BeforeEach
    void initMocks() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ShoppingController(shoppingService)).build();
    }

    @Test
    void should_get_ok_status() throws Exception {
        mockMvc.perform(post("/shopping")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"items\":[{\"type\":\"TSHIRT\",\"quantity\":2}],\"type\":\"STANDARD_CUSTOMER\"}")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
