package com.bikalp.rentalservice.controller;

import com.bikalp.rentalservice.dto.LoginRequest;
import com.bikalp.rentalservice.dto.RegisterRequest;
import com.bikalp.rentalservice.dto.ResetPasswordRequest;
import com.bikalp.rentalservice.entity.User;
import com.bikalp.rentalservice.enums.UserRole;
import com.bikalp.rentalservice.service.AuthService;
import com.bikalp.rentalservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout,
                              @RequestParam(value = "expired", required = false) String expired,
                              Model model, HttpSession session) {
        if (error != null) {
            model.addAttribute("error", "Invalid email/username or password");
        }
        if (logout != null) {
            model.addAttribute("success", "You have been logged out successfully");
        }
        if (expired != null) {
            model.addAttribute("error", "Your session has expired. Please login again");
        }
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        log.info("Received login request for email or username: {}", loginRequest.getEmailOrUsername());
        
        if (bindingResult.hasErrors()) {
            log.warn("Login request validation failed: {}", bindingResult.getAllErrors());
            model.addAttribute("error", "Please fill in all required fields");
            return "auth/login";
        }

        try {
            authService.login(loginRequest);
            return "redirect:/";
        } catch (AuthenticationException e) {
            log.warn("Authentication failed: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                             BindingResult result) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        user.setRole(UserRole.CUSTOMER);
        userService.saveUser(user);
        return "redirect:/auth/login?registered=true";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email,
                                RedirectAttributes redirectAttributes) {
        try {
            authService.sendPasswordResetEmail(email);
            redirectAttributes.addFlashAttribute("success", "Password reset link has been sent to your email.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        model.addAttribute("resetPasswordRequest", new ResetPasswordRequest());
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                              @Valid @ModelAttribute("resetPasswordRequest") ResetPasswordRequest resetPasswordRequest,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.resetPasswordRequest", result);
            redirectAttributes.addFlashAttribute("resetPasswordRequest", resetPasswordRequest);
            return "redirect:/auth/reset-password?token=" + token;
        }

        try {
            authService.resetPassword(token, resetPasswordRequest);
            redirectAttributes.addFlashAttribute("success", "Password has been reset successfully.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/reset-password?token=" + token;
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Logging out user");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/auth/login?logout=true";
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException {
        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String redirectUrl;
            
            if (user.getRole() == UserRole.SUPER_ADMIN) {
                redirectUrl = "/superadmin/dashboard";
            } else if (user.getRole() == UserRole.ADMIN) {
                redirectUrl = "/admin/dashboard";
            } else {
                redirectUrl = "/user/dashboard";
            }
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect("/login?error");
        }
    }
} 