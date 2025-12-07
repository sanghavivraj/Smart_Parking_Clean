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

    // ---------------------------- SHOW PAYMENT PAGE ----------------------------
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


    // ---------------------------- MAKE PAYMENT ----------------------------
    @PostMapping("/make")
    public String makePayment(@ModelAttribute PaymentDTO dto,
                              HttpSession session,
                              Model model) {

        try {
            // Convert String → Enum safely
            PaymentMethod method = PaymentMethod.valueOf(dto.getMethod().trim().toUpperCase());
            dto.setMethod(String.valueOf(method));

            // Save IDs in session for Stripe success
            session.setAttribute("reserveId", dto.getReserveId());
            session.setAttribute("userId", dto.getUserId());

            // -------------------------
            // CASE 1: STRIPE PAYMENT
            // -------------------------
            if (method == PaymentMethod.CARD) {

                SessionCreateParams params = SessionCreateParams.builder()
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
            }

            // -------------------------
            // CASE 2: UPI PAYMENT
            // -------------------------
            if (method == PaymentMethod.UPI) {
                return saveOfflinePayment(dto, PaymentMethod.UPI);
            }

            // -------------------------
            // CASE 3: CASH PAYMENT
            // -------------------------
            return saveOfflinePayment(dto, PaymentMethod.CASH);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Payment error: " + e.getMessage());
            model.addAttribute("payment", dto);
            return "Payment";
        }
    }

    private String saveOfflinePayment(PaymentDTO dto, PaymentMethod paymentMethod) {
        Optional<User> user = userRepo.findById(dto.getUserId());
        Optional<Reserve> reserve = reserveRepo.findById(dto.getReserveId());

        Payment payment = new Payment();
        payment.setUser(user.get());
        payment.setReservation(reserve.get());
        payment.setAmount(dto.getAmount());
        payment.setMethod(paymentMethod);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setUpiTransactionId(dto.getUpiTransactionId());

        Payment saved = paymentRepo.save(payment);

        return "redirect:/Payment/success/" + saved.getPayment_id();
    }


    // ---------------------------- STRIPE SUCCESS HANDLER ----------------------------
    @GetMapping("/success")
    public String stripeSuccess(@RequestParam("session_id") String sessionId, HttpSession session) throws StripeException {

        Stripe.apiKey = stripeSecretKey; // Important
        Session stripeSession = Session.retrieve(sessionId);

        Long userId = (Long) session.getAttribute("userId");
        Long reserveId = (Long) session.getAttribute("reserveId");

        Optional<User> userOpt = userRepo.findById(userId);
        Optional<Reserve> reserveOpt = reserveRepo.findById(reserveId);

        Payment payment = new Payment();
        payment.setUser(userOpt.get());
        payment.setReservation(reserveOpt.get());
        payment.setAmount((double) stripeSession.getAmountTotal() / 100);
        payment.setMethod(PaymentMethod.CARD);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());

        Payment saved = paymentRepo.save(payment);

        return "redirect:/Payment/success/" + saved.getPayment_id();
    }
}
