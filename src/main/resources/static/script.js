// Declare a global search term variable for use across different functions
let searchTerm = '';
let displayedPhones = []; // Global array to hold the currently displayed phones

// Fetching the phone data from the backend
async function getPhones() {
    try {
        const response = await fetch('/phones');
        const phones = await response.json();

        if (phones.length > 0) {
            displayedPhones = phones;  // Save all phones as the displayed list
            displayPhones(phones);     // Display all phones initially
            setupFilters(phones);      // Setup filters after fetching phones
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

    if (phones.length > 0) {
        phones.forEach(phone => {
            const phoneItem = document.createElement('div');
            phoneItem.className = 'phone-item';
            phoneItem.dataset.company = phone.company;  // Add the company to the phone item
            phoneItem.innerHTML = `
                <img src="${phone.imageUrl}" alt="${phone.model}" />
                <p>${phone.model}</p>
                <p>$${phone.price}</p>
            `;
            phoneListContainer.appendChild(phoneItem);
        });
    } else {
        phoneListContainer.innerHTML = `<p>No phones found matching your search.</p>`;
    }
}

// Function to handle the search and frequency count
async function handleSearchAndCount(event) {
    // Trigger only on Enter key press (key code 13)
    if (event.keyCode !== 13) return; // Check if Enter key is pressed

    searchTerm = document.getElementById('searchInput').value.trim().toLowerCase();

    if (searchTerm === "") {
        // Show all phones if input is empty
        getPhones();
        return;
    }

    try {
        // Fetch frequency count for search term from backend
        const frequencyResponse = await fetch('/phones/searchFrequency', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ searchTerm })
        });
        const frequencyCount = await frequencyResponse.json();

        // Display frequency count
        document.getElementById('frequencyDisplay').style.display = 'block';
        document.getElementById('frequencyDisplay').textContent = `Search Frequency: ${frequencyCount}`;

        // Fetch spelling suggestions from backend
        const suggestionsResponse = await fetch(`/phones/spellcheck?searchTerm=${encodeURIComponent(searchTerm)}`);
        const suggestions = await suggestionsResponse.json();

        if (suggestions.length > 0) {
            document.getElementById('suggestionsContainer').style.display = 'block';
            document.getElementById('suggestionsContainer').innerHTML = `Suggested word: ${suggestions[0]}`;

            // Show phones for the suggested word
            const phoneResponse = await fetch(`/phones/search?model=${encodeURIComponent(suggestions[0])}`);
            const phones = await phoneResponse.json();
            displayedPhones = phones; // Save the filtered phones
            displayPhones(phones); // Show phones for the suggested word
        } else {
            // If no suggestions (word exists in the vocabulary), show phones matching the search term
            const phoneResponse = await fetch(`/phones/search?model=${encodeURIComponent(searchTerm)}`);
            const phones = await phoneResponse.json();
            displayedPhones = phones; // Save the filtered phones
            displayPhones(phones); // Display phones for the search term
        }

    } catch (error) {
        console.error('Error handling search and count:', error);
        alert("Failed to fetch search suggestions or frequency count");
    }
}

// Function to filter the phones based on the selected company
function filterPhones() {
    const companyFilter = document.getElementById('companyFilter');
    const selectedCompany = companyFilter.value.trim().toLowerCase();  // Get selected company from dropdown

    // Get all phone items in the DOM
    const phones = Array.from(document.querySelectorAll('.phone-item'));

    // Filter phones based on company and search term
    phones.forEach(phoneItem => {
        const phoneModel = phoneItem.querySelector('p').textContent.toLowerCase();
        const phoneCompany = phoneItem.dataset.company.toLowerCase();

        if ((selectedCompany === '' || phoneCompany === selectedCompany) && phoneModel.includes(searchTerm)) {
            phoneItem.style.display = 'block'; // Show matching phones
        } else {
            phoneItem.style.display = 'none';  // Hide non-matching phones
        }
    });
}

// Function to setup company filters in the dropdown
function setupFilters(phones) {
    const companyFilter = document.getElementById('companyFilter');
    const companies = [...new Set(phones.map(phone => phone.company))];  // Get unique companies

    companyFilter.innerHTML = '<option value="">Select Company</option>';  // Reset dropdown

    companies.forEach(company => {
        const option = document.createElement('option');
        option.value = company;
        option.textContent = company;
        companyFilter.appendChild(option);
    });

    // Event listener for dropdown change (filter phones by company)
    companyFilter.addEventListener('change', filterPhones);  // Call filterPhones when company is selected
}

// Function to apply sorting based on selected sort option
function applySort() {
    const sortFilter = document.getElementById('sortFilter');
    const sortOption = sortFilter.value;

    if (displayedPhones.length === 0) return; // If no phones are displayed, do nothing

    try {
        if (sortOption === 'priceLowHigh') {
            displayedPhones.sort((a, b) => a.price - b.price); // Sort by price low to high
        } else if (sortOption === 'priceHighLow') {
            displayedPhones.sort((a, b) => b.price - a.price); // Sort by price high to low
        } else if (sortOption === 'modelAZ') {
            displayedPhones.sort((a, b) => a.model.localeCompare(b.model)); // Sort by model A-Z
        } else if (sortOption === 'modelZA') {
            displayedPhones.sort((a, b) => b.model.localeCompare(a.model)); // Sort by model Z-A
        }

        // Re-display the phones after sorting
        displayPhones(displayedPhones);

    } catch (error) {
        console.error('Error applying sort:', error);
        alert("Failed to sort phones");
    }
}

// Call the function to fetch and display phones when the page loads
document.addEventListener('DOMContentLoaded', () => {
    getPhones();

    // Set up the event listener for the search input field (Enter key)
    const searchInputField = document.getElementById('searchInput');
    searchInputField.addEventListener('keydown', handleSearchAndCount);

    // Set up event listener for sort filter change
    const sortFilter = document.getElementById('sortFilter');
    sortFilter.addEventListener('change', applySort); // Trigger sorting when a sort option is selected
});
