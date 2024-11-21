package com.example.phone_comparison_backend.service;

import com.example.phone_comparison_backend.model.Phone;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneSorterService {

    // Quick Sort for price
    public List<Phone> sortByPrice(List<Phone> phones, boolean ascending) {
        quickSort(phones, 0, phones.size() - 1, ascending, "price");
        return phones;
    }

    // Quick Sort for model name
    public List<Phone> sortByModel(List<Phone> phones, boolean ascending) {
        quickSort(phones, 0, phones.size() - 1, ascending, "model");
        return phones;
    }

    // Quick Sort implementation
    private void quickSort(List<Phone> phones, int low, int high, boolean ascending, String sortBy) {
        if (low < high) {
            int pivotIndex = partition(phones, low, high, ascending, sortBy);
            quickSort(phones, low, pivotIndex - 1, ascending, sortBy);  // Left part
            quickSort(phones, pivotIndex + 1, high, ascending, sortBy); // Right part
        }
    }

    // Partition function for Quick Sort
    private int partition(List<Phone> phones, int low, int high, boolean ascending, String sortBy) {
        Phone pivot = phones.get(high); // Last element as pivot
        int i = low - 1;

        for (int j = low; j < high; j++) {
            boolean condition = false;

            // Sort by price
            if ("price".equalsIgnoreCase(sortBy)) {
                condition = ascending ? phones.get(j).getPrice() < pivot.getPrice() : phones.get(j).getPrice() > pivot.getPrice();
            }

            // Sort by model
            if ("model".equalsIgnoreCase(sortBy)) {
                condition = ascending ? phones.get(j).getModel().compareTo(pivot.getModel()) < 0 : phones.get(j).getModel().compareTo(pivot.getModel()) > 0;
            }

            // Swap if condition is met
            if (condition) {
                i++;
                swap(phones, i, j);
            }
        }

        // Swap pivot element to correct position
        swap(phones, i + 1, high);

        return i + 1;
    }

    // Utility method to swap two elements in the list
    private void swap(List<Phone> phones, int i, int j) {
        Phone temp = phones.get(i);
        phones.set(i, phones.get(j));
        phones.set(j, temp);
    }
}
