package io.payflow.controller;


import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.base.rest.PayPalRESTException;
import io.payflow.exception.PayPalServiceException;
import io.payflow.model.Order;
import io.payflow.service.OrderService;
import io.payflow.service.PayPalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PayPalService payPalService;
    private final OrderService orderService;

    @PostMapping("/create/{orderId}")
    public String createPayment(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderOrThrow(orderId);
            Payment payment = payPalService.createPayment(order);
            return payment.getLinks().stream()
                    .filter(link -> link.getRel().equals("approval_url"))
                    .findFirst()
                    .map(link -> link.getHref())
                    .orElse("Falha ao criar pagamento.");
        } catch (PayPalServiceException e) {
            return "Erro ao processar pagamento: " + e.getMessage();
        } catch (Exception e) {
            return "Erro desconhecido: " + e.getMessage();
        }
    }

    @GetMapping("/complete")
    public String completePayment(@RequestParam("paymentId") String paymentId,
                                  @RequestParam("PayerID") String payerId) {
        try {
            Payment executedPayment = payPalService.executePayment(paymentId, payerId);
            return executedPayment.getState();
        } catch (PayPalServiceException e) {
            return "Erro ao concluir o pagamento: " + e.getMessage();
        } catch (Exception e) {
            return "Erro desconhecido: " + e.getMessage();
        }
    }
}