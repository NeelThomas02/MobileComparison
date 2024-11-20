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

// Function to handle the search and frequency count
async function handleSearchAndCount(event) {
    // Trigger only on Enter key press (key code 13)
    if (event.keyCode !== 13) return; // Check if Enter key is pressed

    const searchInput = document.getElementById('searchInput').value.trim().toLowerCase();
    
    if (searchInput === "") {
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
            body: JSON.stringify({ searchTerm: searchInput })
        });
        const frequencyCount = await frequencyResponse.json();

        // Display frequency count
        document.getElementById('frequencyDisplay').style.display = 'block';
        document.getElementById('frequencyDisplay').textContent = `Search Frequency: ${frequencyCount}`;

        // Fetch spelling suggestions from backend
        const suggestionsResponse = await fetch(`/phones/spellcheck?searchTerm=${encodeURIComponent(searchInput)}`);
        const suggestions = await suggestionsResponse.json();

        // If suggestions are available, show them
        if (suggestions.length > 0) {
            document.getElementById('suggestionsContainer').style.display = 'block';
            document.getElementById('suggestionsContainer').innerHTML = `Suggested word: ${suggestions[0]}`;

            // Show phones for the suggested word
            const phoneResponse = await fetch(`/phones/search?model=${encodeURIComponent(suggestions[0])}&company=${encodeURIComponent(suggestions[0])}`);
            const phones = await phoneResponse.json();
            displayPhones(phones);

        } else {
            // If no suggestions (word exists in the vocabulary), just show the phones
            const phoneResponse = await fetch(`/phones/search?model=${encodeURIComponent(searchInput)}&company=${encodeURIComponent(searchInput)}`);
            const phones = await phoneResponse.json();
            displayPhones(phones);
        }

    } catch (error) {
        console.error('Error handling search and count:', error);
        alert("Failed to fetch search suggestions or frequency count");
    }
}

// Function to filter the phones based on the search term and company
function filterPhones(searchTerm) {
    const phones = Array.from(document.querySelectorAll('.phone-item'));  // Get all phone items in the DOM
    phones.forEach(phoneItem => {
        const phoneModel = phoneItem.querySelector('p').textContent.toLowerCase();
        
        // Show phone items that match the search term
        if (phoneModel.includes(searchTerm)) {
            phoneItem.style.display = 'block';
        } else {
            phoneItem.style.display = 'none';
        }
    });
}

// Function to setup company filters in the dropdown
function setupFilters(phones) {
    const companyFilter = document.getElementById('companyFilter');
    const companies = [...new Set(phones.map(phone => phone.company))];  // Get unique companies

    companies.forEach(company => {
        const option = document.createElement('option');
        option.value = company;
        option.textContent = company;
        companyFilter.appendChild(option);
    });

    // Filter phones by company when a company is selected
    companyFilter.addEventListener('change', () => {
        const selectedCompany = companyFilter.value.toLowerCase();
        const phones = Array.from(document.querySelectorAll('.phone-item'));
        phones.forEach(phoneItem => {
            const phoneCompany = phoneItem.dataset.company.toLowerCase();
            phoneItem.style.display = selectedCompany === '' || phoneCompany === selectedCompany ? 'block' : 'none';
        });
    });
}

// Call the function to fetch and display phones when the page loads
document.addEventListener('DOMContentLoaded', () => {
    getPhones();

    // Set up the event listener for the search input field (Enter key)
    const searchInputField = document.getElementById('searchInput');
    searchInputField.addEventListener('keydown', handleSearchAndCount);
});
