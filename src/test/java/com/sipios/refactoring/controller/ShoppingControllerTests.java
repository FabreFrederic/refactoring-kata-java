package com.sipios.refactoring.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ShoppingControllerTests {

    @InjectMocks
    private ShoppingController controller;

    @Test
    void should_not_throw() {
        Assertions.assertDoesNotThrow(
            () -> controller.getPrice(new Body(new Item[]{}, "STANDARD_CUSTOMER"))
        );
    }

    @Test
    void should_throw_exception_when_unknown_customer_type() {
        assertThatThrownBy(() -> controller.getPrice(new Body(new Item[]{}, "UNKNOWN_CUSTOMER")))
            .isExactlyInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("400 BAD_REQUEST");
    }

    @Test
    void should_throw_exception_when_price_too_high_for_standard_customer() {
        assertThatThrownBy(() -> controller.getPrice(
            new Body(new Item[]{new Item("TSHIRT", 10)}, "STANDARD_CUSTOMER")))
            .isExactlyInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("400 BAD_REQUEST \"Price (300.0) is too high for standard customer\"");
    }

    @Test
    void should_throw_exception_when_price_too_high_for_premium_customer() {
        assertThatThrownBy(() -> controller.getPrice(
            new Body(new Item[]{new Item("TSHIRT", 1000)}, "PREMIUM_CUSTOMER")))
            .isExactlyInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("400 BAD_REQUEST \"Price (27000.0) is too high for premium customer");
    }

    @Test
    void should_throw_exception_when_price_too_high_for_platinum_customer() {
        assertThatThrownBy(() -> controller.getPrice(
            new Body(new Item[]{new Item("TSHIRT", 1000)}, "PLATINUM_CUSTOMER")))
            .isExactlyInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("400 BAD_REQUEST \"Price (15000.0) is too high for platinum customer\"");
    }

    @ParameterizedTest
    @MethodSource("getTestDataWithExpectedPrice")
    void should_calculate_price(Date today, Body body, String expectedPrice) {
        String price = controller.calculatePrice(body, today);

        assertThat(price).isEqualTo(expectedPrice);
    }

    @Test
    void should_calculate_price_without_item() {
        Body bodyWithoutItem = new Body(new Item[]{}, "STANDARD_CUSTOMER");

        String priceOutOfDiscountsPeriods =
            controller.calculatePrice(bodyWithoutItem, getOutOfDiscountsPeriodsDate());
        String priceInDiscountsPeriods =
            controller.calculatePrice(bodyWithoutItem, getInDiscountsPeriodsDate());

        assertThat(priceOutOfDiscountsPeriods).isEqualTo("0.0");
        assertThat(priceInDiscountsPeriods).isEqualTo("0.0");
    }

    @Test
    void should_calculate_price_with_null_item() {
        Body bodyWithNullItem = new Body(null, "STANDARD_CUSTOMER");

        String priceOutOfDiscountsPeriods =
            controller.calculatePrice(bodyWithNullItem, getOutOfDiscountsPeriodsDate());
        String priceInDiscountsPeriods =
            controller.calculatePrice(bodyWithNullItem, getInDiscountsPeriodsDate());

        assertThat(priceOutOfDiscountsPeriods).isEqualTo("0");
        assertThat(priceInDiscountsPeriods).isEqualTo("0");
    }

    private static Stream<Arguments> getTestDataWithExpectedPrice() {
        return Stream.of(
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 2)}, "STANDARD_CUSTOMER"), "60.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 3)}, "STANDARD_CUSTOMER"), "90.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 2)}, "PREMIUM_CUSTOMER"), "54.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 3)}, "PREMIUM_CUSTOMER"), "81.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 2)}, "PLATINUM_CUSTOMER"), "30.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 3)}, "PLATINUM_CUSTOMER"), "45.0"),

            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 2)}, "STANDARD_CUSTOMER"), "60.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 3)}, "STANDARD_CUSTOMER"), "90.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 2)}, "PREMIUM_CUSTOMER"), "54.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 3)}, "PREMIUM_CUSTOMER"), "81.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 2)}, "PLATINUM_CUSTOMER"), "30.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 3)}, "PLATINUM_CUSTOMER"), "45.0"),

            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 2)}, "STANDARD_CUSTOMER"), "80.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 3)}, "STANDARD_CUSTOMER"), "120.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 2)}, "PREMIUM_CUSTOMER"), "72.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 3)}, "PREMIUM_CUSTOMER"), "108.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 2)}, "PLATINUM_CUSTOMER"), "40.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 3)}, "PLATINUM_CUSTOMER"), "60.0"),

            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 2)}, "STANDARD_CUSTOMER"), "100.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 3)}, "STANDARD_CUSTOMER"), "150.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 2)}, "PREMIUM_CUSTOMER"), "90.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 3)}, "PREMIUM_CUSTOMER"), "135.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 2)}, "PLATINUM_CUSTOMER"), "50.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 3)}, "PLATINUM_CUSTOMER"), "75.0"),

            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 1)}, "STANDARD_CUSTOMER"), "90.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 2)}, "STANDARD_CUSTOMER"), "180.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 2)}, "PREMIUM_CUSTOMER"), "162.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 3)}, "PREMIUM_CUSTOMER"), "243.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 2)}, "PLATINUM_CUSTOMER"), "90.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 3)}, "PLATINUM_CUSTOMER"), "135.0"),

            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 1)}, "STANDARD_CUSTOMER"), "100.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 2)}, "STANDARD_CUSTOMER"), "200.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 2)}, "PREMIUM_CUSTOMER"), "180.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 3)}, "PREMIUM_CUSTOMER"), "270.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 2)}, "PLATINUM_CUSTOMER"), "100.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 3)}, "PLATINUM_CUSTOMER"), "150.0")
        );
    }

    private static Date getOutOfDiscountsPeriodsDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2022, Calendar.JANUARY, 9);
        return cal.getTime();
    }

    private static Date getInDiscountsPeriodsDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2023, Calendar.MAY, 21);
        return cal.getTime();
    }
}
