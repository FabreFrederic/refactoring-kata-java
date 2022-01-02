package com.sipios.refactoring.service;

import com.sipios.refactoring.domain.Body;
import com.sipios.refactoring.domain.Item;
import com.sipios.refactoring.domain.ShopperType;
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
class ShoppingServiceTest {
    @InjectMocks
    private ShoppingService shoppingService;

    @Test
    void should_throw_exception_when_unknown_customer_type() {
        assertThatThrownBy(() -> shoppingService.calculatePrice(new Body(new Item[]{}, "UNKNOWN_CUSTOMER"), new Date()))
            .isExactlyInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("400 BAD_REQUEST");
    }

    @Test
    void should_throw_exception_when_price_too_high_for_standard_customer() {
        assertThatThrownBy(() -> shoppingService.calculatePrice(
            new Body(new Item[]{new Item("TSHIRT", 10)}, ShopperType.STANDARD_CUSTOMER.toString()), new Date()))
            .isExactlyInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("400 BAD_REQUEST \"Price (300.0) is too high for standard customer\"");
    }

    @Test
    void should_throw_exception_when_price_too_high_for_premium_customer() {
        assertThatThrownBy(() -> shoppingService.calculatePrice(
            new Body(new Item[]{new Item("TSHIRT", 1000)}, ShopperType.PREMIUM_CUSTOMER.toString()), new Date()))
            .isExactlyInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("400 BAD_REQUEST \"Price (27000.0) is too high for premium customer");
    }

    @Test
    void should_throw_exception_when_price_too_high_for_platinum_customer() {
        assertThatThrownBy(() -> shoppingService.calculatePrice(
            new Body(new Item[]{new Item("TSHIRT", 1000)}, ShopperType.PLATINUM_CUSTOMER.toString()), new Date()))
            .isExactlyInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("400 BAD_REQUEST \"Price (15000.0) is too high for platinum customer\"");
    }

    @ParameterizedTest
    @MethodSource("getTestDataWithExpectedPrice")
    void should_calculate_price(Date today, Body body, String expectedPrice) {
        String price = shoppingService.calculatePrice(body, today);

        assertThat(price).isEqualTo(expectedPrice);
    }

    @Test
    void should_calculate_price_without_item() {
        Body bodyWithoutItem = new Body(new Item[]{}, "STANDARD_CUSTOMER");

        String priceOutOfDiscountsPeriods =
            shoppingService.calculatePrice(bodyWithoutItem, getOutOfDiscountsPeriodsDate());
        String priceInDiscountsPeriods =
            shoppingService.calculatePrice(bodyWithoutItem, getInDiscountsPeriodsDate());

        assertThat(priceOutOfDiscountsPeriods).isEqualTo("0.0");
        assertThat(priceInDiscountsPeriods).isEqualTo("0.0");
    }

    @Test
    void should_calculate_price_with_null_item() {
        Body bodyWithNullItem = new Body(null, "STANDARD_CUSTOMER");

        String priceOutOfDiscountsPeriods =
            shoppingService.calculatePrice(bodyWithNullItem, getOutOfDiscountsPeriodsDate());
        String priceInDiscountsPeriods =
            shoppingService.calculatePrice(bodyWithNullItem, getInDiscountsPeriodsDate());

        assertThat(priceOutOfDiscountsPeriods).isEqualTo("0");
        assertThat(priceInDiscountsPeriods).isEqualTo("0");
    }

    private static Stream<Arguments> getTestDataWithExpectedPrice() {
        return Stream.of(
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 2)}, ShopperType.STANDARD_CUSTOMER.toString()), "60.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 3)}, ShopperType.STANDARD_CUSTOMER.toString()), "90.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 2)}, ShopperType.PREMIUM_CUSTOMER.toString()), "54.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 3)}, ShopperType.PREMIUM_CUSTOMER.toString()), "81.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 2)}, ShopperType.PLATINUM_CUSTOMER.toString()), "30.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 3)}, ShopperType.PLATINUM_CUSTOMER.toString()), "45.0"),

            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 2)}, ShopperType.STANDARD_CUSTOMER.toString()), "60.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 3)}, ShopperType.STANDARD_CUSTOMER.toString()), "90.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 2)}, ShopperType.PREMIUM_CUSTOMER.toString()), "54.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 3)}, ShopperType.PREMIUM_CUSTOMER.toString()), "81.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 2)}, ShopperType.PLATINUM_CUSTOMER.toString()), "30.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("TSHIRT", 3)}, ShopperType.PLATINUM_CUSTOMER.toString()), "45.0"),

            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 2)}, ShopperType.STANDARD_CUSTOMER.toString()), "80.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 3)}, ShopperType.STANDARD_CUSTOMER.toString()), "120.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 2)}, ShopperType.PREMIUM_CUSTOMER.toString()), "72.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 3)}, ShopperType.PREMIUM_CUSTOMER.toString()), "108.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 2)}, ShopperType.PLATINUM_CUSTOMER.toString()), "40.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 3)}, ShopperType.PLATINUM_CUSTOMER.toString()), "60.0"),

            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 2)}, ShopperType.STANDARD_CUSTOMER.toString()), "100.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 3)}, ShopperType.STANDARD_CUSTOMER.toString()), "150.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 2)}, ShopperType.PREMIUM_CUSTOMER.toString()), "90.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 3)}, ShopperType.PREMIUM_CUSTOMER.toString()), "135.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 2)}, ShopperType.PLATINUM_CUSTOMER.toString()), "50.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("DRESS", 3)}, ShopperType.PLATINUM_CUSTOMER.toString()), "75.0"),

            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 1)}, ShopperType.STANDARD_CUSTOMER.toString()), "90.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 2)}, ShopperType.STANDARD_CUSTOMER.toString()), "180.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 2)}, ShopperType.PREMIUM_CUSTOMER.toString()), "162.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 3)}, ShopperType.PREMIUM_CUSTOMER.toString()), "243.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 2)}, ShopperType.PLATINUM_CUSTOMER.toString()), "90.0"),
            Arguments.of(getOutOfDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 3)}, ShopperType.PLATINUM_CUSTOMER.toString()), "135.0"),

            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 1)}, ShopperType.STANDARD_CUSTOMER.toString()), "100.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 2)}, ShopperType.STANDARD_CUSTOMER.toString()), "200.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 2)}, ShopperType.PREMIUM_CUSTOMER.toString()), "180.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 3)}, ShopperType.PREMIUM_CUSTOMER.toString()), "270.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 2)}, ShopperType.PLATINUM_CUSTOMER.toString()), "100.0"),
            Arguments.of(getInDiscountsPeriodsDate(), new Body(new Item[]{new Item("JACKET", 3)}, ShopperType.PLATINUM_CUSTOMER.toString()), "150.0")
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
