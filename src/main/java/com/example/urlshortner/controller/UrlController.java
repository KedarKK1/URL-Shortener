package com.example.urlshortner.controller;

// import java.net.URI;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.urlshortner.dto.CreateUrlRequest;
import com.example.urlshortner.dto.UpdateUrlRequest;
import com.example.urlshortner.dto.UrlResponse;
import com.example.urlshortner.exception.AliasAlreadyExistsException;
import com.example.urlshortner.service.UrlService;

import jakarta.validation.Valid;

@Controller
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("urls", urlService.listUrls());
        model.addAttribute("analytics", urlService.getAnalytics());
        model.addAttribute("createUrlRequest", new CreateUrlRequest(null, null, null));
        return "dashboard";
    }

    @GetMapping("/urls/new")
    public String createForm(Model model) {
        model.addAttribute("createUrlRequest", new CreateUrlRequest(null, null, null));
        return "create-url";
    }

    @PostMapping("/urls")
    public String createUrl(@Valid @ModelAttribute("createUrlRequest") CreateUrlRequest request,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("createUrlRequest", request);
            return "create-url";
        }

        try {
            urlService.createUrl(request);
        } catch (IllegalArgumentException | AliasAlreadyExistsException ex) {
            bindingResult.reject("submission.invalid", ex.getMessage());
            model.addAttribute("createUrlRequest", request);
            return "create-url";
        }
        return "redirect:/";
    }

    @GetMapping("/urls/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        UrlResponse response = urlService.getUrl(id);
        model.addAttribute("updateUrlRequest", new UpdateUrlRequest(response.targetUrl(), response.alias(), response.expiresAt()));
        model.addAttribute("urlId", id);
        return "edit-url";
    }

    @PostMapping("/urls/{id}/edit")
    public String updateUrl(@PathVariable UUID id, @Valid @ModelAttribute("updateUrlRequest") UpdateUrlRequest request,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("urlId", id);
            model.addAttribute("updateUrlRequest", request);
            return "edit-url";
        }

        try {
            urlService.updateUrl(id, request);
        } catch (IllegalArgumentException | AliasAlreadyExistsException ex) {
            bindingResult.reject("submission.invalid", ex.getMessage());
            model.addAttribute("urlId", id);
            model.addAttribute("updateUrlRequest", request);
            return "edit-url";
        }
        return "redirect:/";
    }

    @PostMapping("/urls/{id}/delete")
    public String deleteUrl(@PathVariable UUID id) {
        urlService.deleteUrl(id);
        return "redirect:/";
    }

    @GetMapping("/analytics")
    public String analytics(Model model) {
        model.addAttribute("analytics", urlService.getAnalytics());
        model.addAttribute("urls", urlService.listUrls());
        return "analytics";
    }

    
}
