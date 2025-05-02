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
@RequestMapping("/landlord")
@RequiredArgsConstructor
public class LandlordController {

    private final UserService userService;

    @GetMapping("/landlords")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String listLandlords(Model model) {
        model.addAttribute("landlords", userService.getUsersByRole(UserRole.LANDLORD));
        return "landlord/list";
    }

    @GetMapping("/landlords/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String showCreateLandlordForm(Model model) {
        model.addAttribute("landlord", new User());
        return "landlord/form";
    }

    @PostMapping("/landlords/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String createLandlord(@Valid @ModelAttribute("landlord") User landlord, 
                               BindingResult result) {
        if (result.hasErrors()) {
            return "landlord/form";
        }
        landlord.setRole(UserRole.LANDLORD);
        userService.saveUser(landlord);
        return "redirect:/landlord/landlords";
    }

    @GetMapping("/landlords/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String showEditLandlordForm(@PathVariable Long id, Model model) {
        model.addAttribute("landlord", userService.getUserById(id));
        return "landlord/form";
    }

    @PostMapping("/landlords/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String updateLandlord(@PathVariable Long id, 
                               @Valid @ModelAttribute("landlord") User landlord,
                               BindingResult result) {
        if (result.hasErrors()) {
            return "landlord/form";
        }
        landlord.setRole(UserRole.LANDLORD);
        userService.updateUser(id, landlord);
        return "redirect:/landlord/landlords";
    }

    @PostMapping("/landlords/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String deleteLandlord(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/landlord/landlords";
    }
} 