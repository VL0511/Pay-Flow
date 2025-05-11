package io.payflow.service;

import com.paypal.api.payments.*;
import io.payflow.exception.PayPalServiceException;
import io.payflow.model.Order;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import io.payflow.model.OrderItem;
import io.payflow.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayPalService {

    private static final String CURRENCY = "BRL";
    private static final String INTENT = "sale";
    private static final String CANCEL_URL = "http://localhost:8080/v1/payments/cancel";
    private static final String RETURN_URL = "http://localhost:8080/v1/payments/complete";
    private final OrderRepository orderRepository;

    private final APIContext apiContext;

    public Payment createPayment(Order order) {
        try {
            List<Item> paypalItems = convertOrderItemsToPayPalItems(order.getItems());
            double total = calculateTotalAmount(order.getItems());

            Payment payment = buildPayment(paypalItems, total, order.getId());
            Payment createdPayment = payment.create(apiContext);

            String approvalLink = createdPayment.getLinks().stream()
                    .filter(link -> "approval_url".equals(link.getRel()))
                    .findFirst()
                    .map(Links::getHref)
                    .orElse(null);

            if (approvalLink != null) updateOrderLinkPayment(order, approvalLink);

            return createdPayment;

        }catch (PayPalRESTException e) {
            throw new PayPalServiceException("Error creating PayPal payment", e);
        }
    }

    private Order updateOrderLinkPayment(Order order, String paymentLink) {
        order.setPaypalLink(paymentLink);
        return orderRepository.save(order);
    }

    public Payment getPayment(String paymentId) {
        try {
            return Payment.get(apiContext, paymentId);
        } catch (PayPalRESTException e) {
            throw new PayPalServiceException("Error retrieving PayPal payment with ID " + paymentId, e);
        }
    }

    public Payment executePayment(String paymentId, String payerId) {
        try {
            Payment payment = getPayment(paymentId);
            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);
            return payment.execute(apiContext, paymentExecution);
        } catch (PayPalRESTException e) {
            throw new PayPalServiceException("Error executing PayPal payment with ID " + paymentId, e);
        }
    }

    private List<Item> convertOrderItemsToPayPalItems(List<OrderItem> orderItems) {
        return orderItems.stream().map(item -> new Item()
                .setName(item.getProduct().getName())
                .setCurrency(CURRENCY)
                .setPrice(String.format("%.2f", item.getPrice()))
                .setQuantity(String.valueOf(item.getQuantity()))
        ).collect(Collectors.toList());
    }

    private double calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    private Payment buildPayment(List<Item> items, double total, Long orderId) {
        Amount amount = new Amount()
                .setCurrency(CURRENCY)
                .setTotal(String.format("%.2f", total));

        ItemList itemList = new ItemList();
        itemList.setItems(items);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription("Pagamento do pedido #" + orderId);
        transaction.setItemList(itemList);

        return new Payment()
                .setIntent(INTENT)
                .setPayer(new Payer().setPaymentMethod("paypal"))
                .setTransactions(List.of(transaction))
                .setRedirectUrls(new RedirectUrls()
                        .setCancelUrl(CANCEL_URL)
                        .setReturnUrl(RETURN_URL));
    }
}
