package com.example.phone_comparison_backend.controller;

import com.example.phone_comparison_backend.model.Phone;
import com.example.phone_comparison_backend.model.SearchTerm;
import com.example.phone_comparison_backend.service.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/phones")
public class PhoneController {

    @Autowired
    private PhoneService phoneService;

    // Endpoint to get all phones
    @GetMapping
    public List<Phone> getAllPhones() {
        return phoneService.getAllPhones();
    }

    // Endpoint to search phones by model or company
    @GetMapping("/search")
    public List<Phone> searchPhones(@RequestParam(required = false) String model,
                                    @RequestParam(required = false) String company) {
        return phoneService.searchPhones(model, company);
    }

    // Endpoint to get search term statistics
    @GetMapping("/search-stats")
    public List<SearchTerm> getSearchStatistics() {
        return phoneService.getSearchStatistics();
    }
}
