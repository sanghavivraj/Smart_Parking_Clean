package com.example.Smart_Parking.Controller;

import com.example.Smart_Parking.DTO.PaymentDTO;
import com.example.Smart_Parking.Model.*;
import com.example.Smart_Parking.Repository.PaymentRepository;
import com.example.Smart_Parking.Repository.ReserveRepository;
import com.example.Smart_Parking.Repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/Payment")
public class PaymentController {

    private final PaymentRepository paymentRepo;
    private final UserRepository userRepo;
    private final ReserveRepository reserveRepo;

    @Value("${stripe.secret}")
    private String stripeSecretKey;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    public PaymentController(PaymentRepository paymentRepo,
                             UserRepository userRepo,
                             ReserveRepository reserveRepo) {
        this.paymentRepo = paymentRepo;
        this.userRepo = userRepo;
        this.reserveRepo = reserveRepo;
    }

    // ------------------------------------------------------
    // SHOW PAYMENT PAGE
    // ------------------------------------------------------
    @GetMapping
    public String showPaymentForm(@RequestParam("userId") Long userId,
                                  @RequestParam("reserveId") Long reserveId,
                                  Model model) {

        Optional<Reserve> reserveOpt = reserveRepo.findById(reserveId);
        if (reserveOpt.isEmpty()) {
            model.addAttribute("error", "Invalid Reservation.");
            return "error";
        }

        Reserve reserve = reserveOpt.get();
        double amount = reserve.getDurationHours() * 100.0;

        PaymentDTO dto = new PaymentDTO();
        dto.setUserId(userId);
        dto.setReserveId(reserveId);
        dto.setAmount(amount);

        model.addAttribute("payment", dto);
        return "Payment";
    }

    // ------------------------------------------------------
    // MAKE PAYMENT (STRIPE OR OFFLINE)
    // ------------------------------------------------------
    @PostMapping("/make")
    public String makePayment(@ModelAttribute PaymentDTO dto,
                              HttpSession session,
                              Model model) {

        System.out.println("PAYMENT SUBMIT -> userId=" + dto.getUserId()
                + " reserveId=" + dto.getReserveId()
                + " amount=" + dto.getAmount()
                + " method=" + dto.getMethod()
                + " upi=" + dto.getUpiTransactionId());

        System.out.println("DEBUG SUCCESS_URL = " + successUrl);
        System.out.println("DEBUG CANCEL_URL = " + cancelUrl);

        // Session store for Stripe success callback
        session.setAttribute("reserveId", dto.getReserveId());
        session.setAttribute("userId", dto.getUserId());

        // Validate method
        if (dto.getMethod() == null || dto.getMethod().trim().isEmpty()) {
            model.addAttribute("error", "Please select a payment method.");
            model.addAttribute("payment", dto);
            return "Payment";
        }

        String methodStr = dto.getMethod().trim().toUpperCase(Locale.ROOT);

        // OFFLINE PAYMENT
        if ("UPI".equals(methodStr) || "CASH".equals(methodStr)) {
            return saveOfflinePayment(dto, PaymentMethod.CASH);
        }

        // CARD PAYMENT (STRIPE)
        if ("CARD".equals(methodStr)) {
            try {
                if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
                    model.addAttribute("error", "Stripe secret key not configured.");
                    return "Payment";
                }

                Stripe.apiKey = stripeSecretKey;

                // -------------------------------
                // SANITIZE & VALIDATE URLS
                // -------------------------------
                successUrl = successUrl == null ? "" : successUrl.trim();
                cancelUrl = cancelUrl == null ? "" : cancelUrl.trim();

                try {
                    new URL(successUrl);
                    new URL(cancelUrl);
                } catch (Exception ex) {
                    System.out.println("INVALID URL >> success=" + successUrl + " cancel=" + cancelUrl);
                    model.addAttribute("error", "Invalid Stripe redirect URLs.");
                    return "Payment";
                }
                // -------------------------------

                SessionCreateParams params =
                        SessionCreateParams.builder()
                                .setMode(SessionCreateParams.Mode.PAYMENT)
                                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                                .setCancelUrl(cancelUrl)
                                .addLineItem(
                                        SessionCreateParams.LineItem.builder()
                                                .setQuantity(1L)
                                                .setPriceData(
                                                        SessionCreateParams.LineItem.PriceData.builder()
                                                                .setCurrency("inr")
                                                                .setUnitAmount((long) (dto.getAmount() * 100))
                                                                .setProductData(
                                                                        SessionCreateParams.LineItem.PriceData.ProductData
                                                                                .builder()
                                                                                .setName("Smart Parking Fee")
                                                                                .build()
                                                                )
                                                                .build()
                                                )
                                                .build()
                                )
                                .build();

                Session stripeSession = Session.create(params);

                return "redirect:" + stripeSession.getUrl();

            } catch (StripeException e) {
                e.printStackTrace();
                model.addAttribute("error", "Stripe error: " + e.getMessage());
                return "Payment";
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "Payment error: " + e.getMessage());
                return "Payment";
            }
        }

        model.addAttribute("error", "Unsupported payment method: " + dto.getMethod());
        return "Payment";
    }

    // ------------------------------------------------------
    // STRIPE SUCCESS CALLBACK
    // ------------------------------------------------------
    @GetMapping("/success")
    public String stripeSuccess(@RequestParam("session_id") String sessionId, HttpSession httpSession) {
        try {
            Stripe.apiKey = stripeSecretKey;

            Session stripeSession = Session.retrieve(sessionId);

            Long reserveId = (Long) httpSession.getAttribute("reserveId");
            Long userId = (Long) httpSession.getAttribute("userId");

            Optional<User> userOpt = userRepo.findById(userId);
            Optional<Reserve> reserveOpt = reserveRepo.findById(reserveId);

            if (userOpt.isEmpty() || reserveOpt.isEmpty()) {
                System.out.println("Stripe success but missing user/reserve");
                return "redirect:/Payment/failed";
            }

            Payment payment = new Payment();
            payment.setUser(userOpt.get());
            payment.setReservation(reserveOpt.get());
            payment.setAmount((double) stripeSession.getAmountTotal() / 100);
            payment.setMethod(PaymentMethod.CARD);
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaymentDate(LocalDateTime.now());

            Payment saved = paymentRepo.save(payment);

            return "redirect:/Payment/success/" + saved.getPayment_id();

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/Payment/failed";
        }
    }

    // ------------------------------------------------------
    // SAVE OFFLINE PAYMENT
    // ------------------------------------------------------
    private String saveOfflinePayment(PaymentDTO dto, PaymentMethod method) {
        Optional<User> userOpt = userRepo.findById(dto.getUserId());
        Optional<Reserve> reserveOpt = reserveRepo.findById(dto.getReserveId());

        if (userOpt.isEmpty() || reserveOpt.isEmpty()) {
            return "redirect:/reserve";
        }

        Payment payment = new Payment();
        payment.setUser(userOpt.get());
        payment.setReservation(reserveOpt.get());
        payment.setAmount(dto.getAmount());
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());

        Payment saved = paymentRepo.save(payment);
        return "redirect:/Payment/success/" + saved.getPayment_id();
    }
}
