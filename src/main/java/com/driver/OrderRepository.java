package com.driver;

import io.swagger.models.auth.In;
import org.springframework.stereotype.Repository;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;

@Repository
public class OrderRepository {
    Map<String,Order> orderMap = new HashMap<>();
    Map<String, DeliveryPartner> deliveryPartnerMap = new HashMap<>();
    Map<String, List<Order>> orderPartnerPair = new HashMap<>();
    HashSet<Order> assignedOrder = new HashSet<>();
    public void addOrder(Order order){
        String id = order.getId();
        orderMap.put(id,order);
    }
    public void addPartner(String partnerId){
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        deliveryPartnerMap.put(partnerId,deliveryPartner);
    }
    public void addOrderPartnerPair(String orderId,String partnerId){
        if(orderMap.containsKey(orderId)){
            Order order = orderMap.get(orderId);
            if(!assignedOrder.contains(order)){
                if(orderPartnerPair.containsKey(partnerId)){
                    List<Order> list = orderPartnerPair.get(partnerId);
                    list.add(orderMap.get(orderId));
                    orderPartnerPair.put(partnerId,list);
                    assignedOrder.add(order);
                }else{
                    List<Order> list = new ArrayList<>();
                    list.add(orderMap.get(orderId));
                    orderPartnerPair.put(partnerId,list);
                    assignedOrder.add(order);
                }
            }
        }
    }
    public Order getOrderById(String orderId){
        if(!orderMap.containsKey(orderId)){
            return null;
        }
        return orderMap.get(orderId);
    }
    public DeliveryPartner getPartnerById(String partnerId){
        if(deliveryPartnerMap.containsKey(partnerId)){
            return deliveryPartnerMap.get(partnerId);
        }
        return null;
    }
    public int getOrderCountByPartnerId(String partnerId){
        if(orderPartnerPair.containsKey(partnerId)){
            return orderPartnerPair.size();
        }
        return 0;
    }
    public List<Order> getOrdersByPartnerId(String partnerId){
        List<Order> list = new ArrayList<>();
        if(orderPartnerPair.containsKey(partnerId)){
            list = orderPartnerPair.get(partnerId);
        }
        return list;
    }
    public List<Order> getAllOrders(){
        List<Order> orders = new ArrayList<>();
        for(String id : orderMap.keySet()){
            orders.add(orderMap.get(id));
        }
        return orders;
    }
    public int getCountOfUnassignedOrders(){
        return orderMap.size() - assignedOrder.size();
    }
    public void deletePartnerById(String partnerId){
        if(deliveryPartnerMap.containsKey(partnerId)){
            if(orderPartnerPair.containsKey(partnerId)){
                for(Order order : orderPartnerPair.get(partnerId)){
                    assignedOrder.remove(order);
                }
            }
        }
        orderPartnerPair.remove(partnerId);
        deliveryPartnerMap.remove(partnerId);
    }
    public void deleteOrderById(String orderId){
        if(orderMap.containsKey(orderId)){
            Order order1 = orderMap.get(orderId);
            orderMap.remove(orderId);
            assignedOrder.remove(order1);
            for(String id : orderPartnerPair.keySet()){
                List<Order> newOrderList = new ArrayList<>();
                for(Order order : orderPartnerPair.get(id)){
                    if(order.equals(order1)){
                        continue;
                    }
                    newOrderList.add(order);
                }
                orderPartnerPair.put(id,newOrderList);
            }
        }
    }
    public int getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId){
        int count = 0;
        String hour = time.substring(0,2);
        String min = time.substring(3);
        int HH = Integer.parseInt(hour);
        int MM = Integer.parseInt(min);
        int givenTime = HH*60 + MM;
        for(Order order : orderPartnerPair.get(partnerId)){
            if(order.getDeliveryTime() > givenTime){
                count++;
            }
        }
        return count;
    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        List<Order> orders = orderPartnerPair.get(partnerId);
        Order order = orders.get(orders.size()-1);
        int time = order.getDeliveryTime();
        int MM = time%60;
        int HH = (time-MM)/60;
        String orderTime = String.valueOf(HH)+":"+String.valueOf(MM);
        return orderTime;
    }




}