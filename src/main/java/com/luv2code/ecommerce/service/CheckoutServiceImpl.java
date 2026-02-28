package com.luv2code.ecommerce.service;

import com.luv2code.ecommerce.dao.CustomerRespository;
import com.luv2code.ecommerce.dto.PaymentInfo;
import com.luv2code.ecommerce.dto.Purchase;
import com.luv2code.ecommerce.dto.PurchaseResponse;
import com.luv2code.ecommerce.entity.Customer;
import com.luv2code.ecommerce.entity.Order;
import com.luv2code.ecommerce.entity.OrderItem;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private CustomerRespository customerRespository;

    public CheckoutServiceImpl(CustomerRespository customerRespository,
                               @Value("${stripe.key.secret}") String secretKey) {
        this.customerRespository = customerRespository;
        Stripe.apiKey = secretKey;
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
        orderItems.forEach(item -> order.add(item));

        //populate order with billing Address and shipping Address
        order.setBillingAddress(purchase.getBillingAddress());
        order.setShippingAddress(purchase.getShippingAddress());

        //populate customer with order
        Customer customer = purchase.getCustomer();

        // Check if this is an existing customer
        String theEmail = customer.getEmail();
        Customer customerFromDB = customerRespository.findByEmail(theEmail);
        if (customerFromDB != null) {
            // we found them. let's assign them accordingly
            customer = customerFromDB;
        }

        customer.add(order);

        customerRespository.save(customer);
        return new PurchaseResponse(orderTrackingNumber);
    }

    @Override
    public PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException {
        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");
        Map<String,Object> params = new HashMap<>();
        params.put("amount",paymentInfo.getAmount());
        params.put("currency",paymentInfo.getCurrency());
        params.put("payment_method_types",paymentMethodTypes);
        params.put("description","Luv2Shop purchase");
        params.put("receipt_email", paymentInfo.getReceiptEmail());
        return PaymentIntent.create(params);
    }


    private String generateOrderTrackingNumber() {
        // generate a random UUID number(UUID version-4) Universal unitque identifier
        return UUID.randomUUID().toString();
    }
}
