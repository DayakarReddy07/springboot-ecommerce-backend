package com.luv2code.ecommerce.service;

import com.luv2code.ecommerce.dao.CustomerRespository;
import com.luv2code.ecommerce.dto.Purchase;
import com.luv2code.ecommerce.dto.PurchaseResponse;
import com.luv2code.ecommerce.entity.Customer;
import com.luv2code.ecommerce.entity.Order;
import com.luv2code.ecommerce.entity.OrderItem;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class CheckoutServiceImpl implements CheckoutService{

    private CustomerRespository customerRespository;

    public CheckoutServiceImpl(CustomerRespository customerRespository){
        this.customerRespository = customerRespository;
    }

    @Override
    public PurchaseResponse placeOrder(Purchase purchase) {

        // retrieve the order info from the dto
        Order order = purchase.getOrder();

        //generate Order tracking number
        String orderTrackingNumber = generateOrderTrackingNumber();
        order.setOrderTrackingNumber(orderTrackingNumber);

        //populate order with orderItems
        Set<OrderItem> orderItems = purchase.getOrderItems();
        orderItems.forEach(item->order.add(item));

        //populate order with billing Address and shipping Address
        order.setBillingAddress(purchase.getBillingAddress());
        order.setShippingAddress(purchase.getShippingAddress());

        //populate customer with order
        Customer customer = purchase.getCustomer();
        customer.add(order);

        customerRespository.save(customer);
        return new PurchaseResponse(orderTrackingNumber);
    }

    private String generateOrderTrackingNumber(){
        // generate a random UUID number(UUID version-4) Universal unitque identifier
        return UUID.randomUUID().toString();
    }
}
