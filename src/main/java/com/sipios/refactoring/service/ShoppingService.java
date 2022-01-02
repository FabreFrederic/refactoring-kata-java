package com.sipios.refactoring.service;

import com.sipios.refactoring.domain.Body;
import com.sipios.refactoring.domain.Item;
import com.sipios.refactoring.domain.ShopperType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Service
public class ShoppingService {

    public String calculatePrice(Body body, Date today) {
        double price = 0;
        double discount = computeDiscountForCustomer(body.getShopperType());

        if (body.getItems() == null) {
            return "0";
        }
        for (int i = 0; i < body.getItems().length; i++) {
            Item item = body.getItems()[i];

            switch (item.getType()) {
                case "TSHIRT":
                    price += 30 * item.getQuantity() * discount;
                    break;
                case "DRESS":
                    price += 50 * item.getQuantity() * computeDiscountForPeriod(today, item) * discount;
                    break;
                case "JACKET":
                    price += 100 * item.getQuantity() * computeDiscountForPeriod(today, item) * discount;
                    break;
                default:
                    price = 0.0;
            }
        }
        checkPrice(body, price);

        return String.valueOf(price);
    }

    private boolean isDiscountsPeriods(Date today) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        calendar.setTime(today);

        return (
            calendar.get(Calendar.DAY_OF_MONTH) < 15 &&
                calendar.get(Calendar.DAY_OF_MONTH) > 5 &&
                calendar.get(Calendar.MONTH) == Calendar.JUNE
        ) ||
            (
                calendar.get(Calendar.DAY_OF_MONTH) < 15 &&
                    calendar.get(Calendar.DAY_OF_MONTH) > 5 &&
                    calendar.get(Calendar.MONTH) == Calendar.JANUARY
            );
    }

    private double computeDiscountForPeriod(Date date, Item item) {
        double periodDiscount = 1;
        if (isDiscountsPeriods(date)) {
            if (item.getType().equals("DRESS")) {
                periodDiscount = 0.8;
            } else if (item.getType().equals("JACKET")) {
                periodDiscount = 0.9;
            }
        }
        return periodDiscount;
    }

    private double computeDiscountForCustomer(String shopperType) {
        double discount;
        if (ShopperType.STANDARD_CUSTOMER.toString().equals(shopperType)) {
            discount = 1;
        } else if (ShopperType.PREMIUM_CUSTOMER.toString().equals(shopperType)) {
            discount = 0.9;
        } else if (ShopperType.PLATINUM_CUSTOMER.toString().equals(shopperType)) {
            discount = 0.5;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return discount;
    }

    private void checkPrice(Body body, double price) {
        if (body.getShopperType().equals(ShopperType.STANDARD_CUSTOMER.toString())) {
            if (price > 200) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Price (" + price + ") is too high for " + ShopperType.STANDARD_CUSTOMER.getLabel());
            }
        } else if (body.getShopperType().equals(ShopperType.PREMIUM_CUSTOMER.toString())) {
            if (price > 800) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Price (" + price + ") is too high for " + ShopperType.PREMIUM_CUSTOMER.getLabel());
            }
        } else if (body.getShopperType().equals(ShopperType.PLATINUM_CUSTOMER.toString())) {
            if (price > 2000) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Price (" + price + ") is too high for " + ShopperType.PLATINUM_CUSTOMER.getLabel());
            }
        } else {
            if (price > 200) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Price (" + price + ") is too high for standard customer");
            }
        }
    }
}
