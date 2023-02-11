package com.driver;

public class Order {

    private String id;
    private int deliveryTime;
    public Order(){

    }

    public Order(String id, String deliveryTime) {
        this.id = id;
        // The deliveryTime has to converted from string to int and then stored in the attribute
        String hour = deliveryTime.substring(0,2);
        String min = deliveryTime.substring(3);
        int HH = Integer.parseInt(hour);
        int MM = Integer.parseInt(min);
        this.deliveryTime  = HH*60 + MM;
    }

    public String getId() {
        return id;
    }

    public int getDeliveryTime() {return deliveryTime;}

}
