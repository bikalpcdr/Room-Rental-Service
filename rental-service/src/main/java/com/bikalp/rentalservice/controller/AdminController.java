package com.bikalp.rentalservice.controller;

import com.bikalp.rentalservice.entity.User;
import com.bikalp.rentalservice.enums.UserRole;
import com.bikalp.rentalservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String listAdmins(Model model) {
        model.addAttribute("admins", userService.getUsersByRole(UserRole.ADMIN));
        return "admin/list";
    }

    @GetMapping("/admins/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String showCreateAdminForm(Model model) {
        model.addAttribute("admin", new User());
        return "admin/form";
    }

    @PostMapping("/admins/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String createAdmin(@Valid @ModelAttribute("admin") User admin, 
                            BindingResult result) {
        if (result.hasErrors()) {
            return "admin/form";
        }
        admin.setRole(UserRole.ADMIN);
        userService.saveUser(admin);
        return "redirect:/admin/admins";
    }

    @GetMapping("/admins/{id}/edit")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String showEditAdminForm(@PathVariable Long id, Model model) {
        model.addAttribute("admin", userService.getUserById(id));
        return "admin/form";
    }

    @PostMapping("/admins/{id}/edit")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String updateAdmin(@PathVariable Long id, 
                            @Valid @ModelAttribute("admin") User admin,
                            BindingResult result) {
        if (result.hasErrors()) {
            return "admin/form";
        }
        admin.setRole(UserRole.ADMIN);
        userService.updateUser(id, admin);
        return "redirect:/admin/admins";
    }

    @PostMapping("/admins/{id}/delete")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String deleteAdmin(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/admins";
    }
} 