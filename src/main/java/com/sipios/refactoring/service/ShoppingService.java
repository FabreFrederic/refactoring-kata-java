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
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        calendar.setTime(today);

        double price = 0;
        double discount;

        // Compute discount for customer
        if (body.getType().equals(ShopperType.STANDARD_CUSTOMER.toString())) {
            discount = 1;
        } else if (body.getType().equals(ShopperType.PREMIUM_CUSTOMER.toString())) {
            discount = 0.9;
        } else if (body.getType().equals(ShopperType.PLATINUM_CUSTOMER.toString())) {
            discount = 0.5;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // Compute total amount depending on the types and quantity of product and
        // if we are in winter or summer discounts periods
        if (
            !(
                calendar.get(Calendar.DAY_OF_MONTH) < 15 &&
                    calendar.get(Calendar.DAY_OF_MONTH) > 5 &&
                    calendar.get(Calendar.MONTH) == 5
            ) &&
                !(
                    calendar.get(Calendar.DAY_OF_MONTH) < 15 &&
                        calendar.get(Calendar.DAY_OF_MONTH) > 5 &&
                        calendar.get(Calendar.MONTH) == 0
                )
        ) {
            if (body.getItems() == null) {
                return "0";
            }

            for (int i = 0; i < body.getItems().length; i++) {
                Item item = body.getItems()[i];

                if (item.getType().equals("TSHIRT")) {
                    price += 30 * item.getQuantity() * discount;
                } else if (item.getType().equals("DRESS")) {
                    price += 50 * item.getQuantity() * discount;
                } else if (item.getType().equals("JACKET")) {
                    price += 100 * item.getQuantity() * discount;
                }
            }
        } else {
            if (body.getItems() == null) {
                return "0";
            }

            for (int i = 0; i < body.getItems().length; i++) {
                Item item = body.getItems()[i];

                if (item.getType().equals("TSHIRT")) {
                    price += 30 * item.getQuantity() * discount;
                } else if (item.getType().equals("DRESS")) {
                    price += 50 * item.getQuantity() * 0.8 * discount;
                } else if (item.getType().equals("JACKET")) {
                    price += 100 * item.getQuantity() * 0.9 * discount;
                }
            }
        }

        try {
            if (body.getType().equals(ShopperType.STANDARD_CUSTOMER.toString())) {
                if (price > 200) {
                    throw new Exception("Price (" + price + ") is too high for " + ShopperType.STANDARD_CUSTOMER.getLabel());
                }
            } else if (body.getType().equals(ShopperType.PREMIUM_CUSTOMER.toString())) {
                if (price > 800) {
                    throw new Exception("Price (" + price + ") is too high for " + ShopperType.PREMIUM_CUSTOMER.getLabel());
                }
            } else if (body.getType().equals(ShopperType.PLATINUM_CUSTOMER.toString())) {
                if (price > 2000) {
                    throw new Exception("Price (" + price + ") is too high for " + ShopperType.PLATINUM_CUSTOMER.getLabel());
                }
            } else {
                if (price > 200) {
                    throw new Exception("Price (" + price + ") is too high for standard customer");
                }
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return String.valueOf(price);
    }
}
