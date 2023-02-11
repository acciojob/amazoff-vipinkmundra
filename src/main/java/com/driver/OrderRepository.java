package com.driver;

import io.swagger.models.auth.In;
import org.springframework.stereotype.Repository;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;

//@Repository
public class OrderRepository {
    Map<String,Order> orderMap = new HashMap<>();
    Map<String, DeliveryPartner> deliveryPartnerMap = new HashMap<>();
    Map<String, HashSet<Order>> orderPartnerPair = new HashMap<>();
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
        if(orderMap.containsKey(orderId) && deliveryPartnerMap.containsKey(partnerId)){
            Order order = orderMap.get(orderId);
            if(!assignedOrder.contains(order)){
                if(orderPartnerPair.containsKey(partnerId)){
                    HashSet<Order> list = orderPartnerPair.get(partnerId);
                    list.add(order);
                    DeliveryPartner dp = deliveryPartnerMap.get(partnerId);
                    dp.setNumberOfOrders(list.size());
                    deliveryPartnerMap.put(partnerId,dp);
                    orderPartnerPair.put(partnerId,list);
                    assignedOrder.add(order);
                }else{
                    HashSet<Order> list = new HashSet<>();
                    list.add(order);
                    DeliveryPartner dp = deliveryPartnerMap.get(partnerId);
                    dp.setNumberOfOrders(list.size());
                    deliveryPartnerMap.put(partnerId,dp);
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
    public Integer getOrderCountByPartnerId(String partnerId){
        if(orderPartnerPair.containsKey(partnerId)){
            return orderPartnerPair.get(partnerId).size();
        }
        return 0;
    }
    public List<String> getOrdersByPartnerId(String partnerId){
        HashSet<Order> list = new HashSet<>();
        List<String> ans = new ArrayList<>();
        if(orderPartnerPair.containsKey(partnerId)){
            list = orderPartnerPair.get(partnerId);
            for(Order order : list){
                ans.add(order.getId());
            }
        }
        return ans;
    }
    public List<String> getAllOrders(){
        return new ArrayList<>(orderMap.keySet());

    }
    public Integer getCountOfUnassignedOrders(){
        if(orderMap.size() == 0){
            return 0;
        }
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
                HashSet<Order> newOrderList = new HashSet<>();
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
    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId){
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
        HashSet<Order> hs = orderPartnerPair.get(partnerId);
        List<Order> orders = new ArrayList<>(hs);
        int time = 0;
        for(Order order : orders){
            time = Math.max(time,order.getDeliveryTime());
        }
        int MM = time%60;
        int HH = (time-MM)/60;
        String string1 = String.valueOf(MM);
        String string2 = String.valueOf(HH);
        if(string1.length() == 1){
            string1 = "0"+string1;
        }
        if(string2.length() == 1){
            string2 = "0"+string2;
        }
        String orderTime = string2+":"+string1;
        return orderTime;
    }
}
