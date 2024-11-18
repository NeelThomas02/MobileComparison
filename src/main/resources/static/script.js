// Fetching the phone data from the backend
async function getPhones() {
    try {
        const response = await fetch('/phones');
        const phones = await response.json();

        if (phones.length > 0) {
            displayPhones(phones); // Display all phones initially
            setupFilters(phones); // Setup filters after fetching phones
        } else {
            alert("No phones available");
        }
    } catch (error) {
        console.error('Error fetching phones:', error);
        alert("Failed to load phone data");
    }
}

// Displaying the phones in the UI
function displayPhones(phones) {
    const phoneListContainer = document.getElementById('phoneListContainer');
    phoneListContainer.innerHTML = ''; // Clear existing content

    phones.forEach(phone => {
        const phoneItem = document.createElement('div');
        phoneItem.className = 'phone-item';
        phoneItem.innerHTML = `
            <img src="${phone.imageUrl}" alt="${phone.model}" />
            <p>${phone.model}</p>
            <p>$${phone.price}</p>
        `;
        phoneListContainer.appendChild(phoneItem);
    });
}

// Function to handle the search and filtering
function setupFilters(phones) {
    const searchInput = document.getElementById('searchInput');
    const companyFilter = document.getElementById('companyFilter');

    // Handling search functionality
    searchInput.addEventListener('input', () => {
        const searchTerm = searchInput.value.toLowerCase();
        const filteredPhones = filterPhones(phones, searchTerm, companyFilter.value);
        displayPhones(filteredPhones);
    });

    // Handling company filter functionality
    companyFilter.addEventListener('change', () => {
        const searchTerm = searchInput.value.toLowerCase();
        const filteredPhones = filterPhones(phones, searchTerm, companyFilter.value);
        displayPhones(filteredPhones);
    });

    // Populate the company filter with unique companies from the phones data
    const companies = [...new Set(phones.map(phone => phone.company))]; // Reference the 'company' field
    companies.forEach(company => {
        const option = document.createElement('option');
        option.value = company;
        option.textContent = company;
        companyFilter.appendChild(option);
    });
}

// Function to filter the phones based on the search term and company
function filterPhones(phones, searchTerm, selectedCompany) {
    return phones.filter(phone => {
        const matchesSearch = phone.model.toLowerCase().includes(searchTerm);
        const matchesCompany = selectedCompany ? phone.company === selectedCompany : true; // Adjusted to filter by company
        return matchesSearch && matchesCompany;
    });
}

// Call the function to fetch and display phones when the page loads
document.addEventListener('DOMContentLoaded', () => {
    getPhones();
});
