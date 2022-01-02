package com.sipios.refactoring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@RestController
@RequestMapping("/shopping")
public class ShoppingController {

    @PostMapping
    public String getPrice(@RequestBody Body body) {
        Date today = new Date();
        return calculatePrice(body, today);
    }

    public String calculatePrice(Body body, Date today) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        calendar.setTime(today);

        double price = 0;
        double discount;

        // Compute discount for customer
        if (body.getType().equals("STANDARD_CUSTOMER")) {
            discount = 1;
        } else if (body.getType().equals("PREMIUM_CUSTOMER")) {
            discount = 0.9;
        } else if (body.getType().equals("PLATINUM_CUSTOMER")) {
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
            if (body.getType().equals("STANDARD_CUSTOMER")) {
                if (price > 200) {
                    throw new Exception("Price (" + price + ") is too high for standard customer");
                }
            } else if (body.getType().equals("PREMIUM_CUSTOMER")) {
                if (price > 800) {
                    throw new Exception("Price (" + price + ") is too high for premium customer");
                }
            } else if (body.getType().equals("PLATINUM_CUSTOMER")) {
                if (price > 2000) {
                    throw new Exception("Price (" + price + ") is too high for platinum customer");
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

class Body {

    private Item[] items;
    private String type;

    public Body(Item[] items, String shopperType) {
        this.items = items;
        this.type = shopperType;
    }

    public Body() {
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

class Item {

    private String type;
    private int quantity;

    public Item() {
    }

    public Item(String type, int quantity) {
        this.type = type;
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
