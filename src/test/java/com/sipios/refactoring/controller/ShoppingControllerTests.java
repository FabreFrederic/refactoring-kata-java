package com.sipios.refactoring.controller;

import com.sipios.refactoring.domain.Body;
import com.sipios.refactoring.domain.Item;
import com.sipios.refactoring.service.ShoppingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShoppingControllerTests {

    @InjectMocks
    private ShoppingController shoppingController;

    @Mock
    private ShoppingService shoppingService;

    @Test
    void should_not_throw() {
        when(shoppingService.calculatePrice(
            any(Body.class), any(Date.class))).thenReturn("100.0");

        assertThat(shoppingController.getPrice(
            new Body(new Item[]{}, "STANDARD_CUSTOMER"))).isEqualTo("100.0");
    }
}
