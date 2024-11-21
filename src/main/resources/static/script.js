// Declare global variables
let searchTerm = '';
let displayedPhones = [];

// Fetching the phone data from the backend
async function getPhones() {
    try {
        const response = await fetch('/phones');
        const phones = await response.json();

        if (phones.length > 0) {
            displayedPhones = phones;
            displayPhones(phones);
            setupFilters(phones);
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
    phoneListContainer.innerHTML = '';

    if (phones.length > 0) {
        phones.forEach(phone => {
            const phoneItem = document.createElement('div');
            phoneItem.className = 'phone-item';
            phoneItem.dataset.company = phone.company;
            phoneItem.innerHTML = `
                <img src="${phone.imageUrl}" alt="${phone.model}" />
                <p>${phone.model}</p>
                <p>$${phone.price.toFixed(2)}</p>
                <p>Company: ${phone.company}</p>
            `;
            phoneListContainer.appendChild(phoneItem);
        });
    } else {
        phoneListContainer.innerHTML = '<p>No phones found matching your search.</p>';
    }
}

// Function to handle the search and frequency count
async function handleSearchAndCount(event) {
    if (event.key !== 'Enter') return;

    searchTerm = document.getElementById('searchInput').value.trim().toLowerCase();

    if (searchTerm === "") {
        getPhones();
        return;
    }

    try {
        // Fetch phones based on search term
        const phoneResponse = await fetch(`/phones/search?model=${encodeURIComponent(searchTerm)}`);
        const phones = await phoneResponse.json();

        // Fetch frequency count
        const frequencyResponse = await fetch('/phones/searchFrequency', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ searchTerm })
        });
        const frequencyCount = await frequencyResponse.json();

        // Display frequency count
        document.getElementById('frequencyDisplay').style.display = 'block';
        document.getElementById('frequencyDisplay').textContent = `Search Frequency: ${frequencyCount}`;

        if (phones.length > 0) {
            displayedPhones = phones;
            displayPhones(phones);
            document.getElementById('suggestionsContainer').style.display = 'none';
        } else {
            // Fetch spelling suggestions if no phones found
            const suggestionsResponse = await fetch(`/phones/spellcheck?searchTerm=${encodeURIComponent(searchTerm)}`);
            const suggestions = await suggestionsResponse.json();

            if (suggestions.length > 0) {
                document.getElementById('suggestionsContainer').style.display = 'block';
                document.getElementById('suggestionsContainer').innerHTML = `Suggested word: ${suggestions[0]}`;

                // Fetch phones for the suggested word
                const suggestedPhoneResponse = await fetch(`/phones/search?model=${encodeURIComponent(suggestions[0])}`);
                const suggestedPhones = await suggestedPhoneResponse.json();
                displayedPhones = suggestedPhones;
                displayPhones(suggestedPhones);
            } else {
                displayedPhones = [];
                displayPhones([]);
                document.getElementById('suggestionsContainer').style.display = 'none';
            }
        }
    } catch (error) {
        console.error('Error handling search and count:', error);
        alert("Failed to fetch search results or frequency count");
    }
}

// Function to filter the phones based on the selected company
function filterPhones() {
    const companyFilter = document.getElementById('companyFilter');
    const selectedCompany = companyFilter.value.trim().toLowerCase();

    const filteredPhones = displayedPhones.filter(phone =>
        (selectedCompany === '' || phone.company.toLowerCase() === selectedCompany) &&
        phone.model.toLowerCase().includes(searchTerm)
    );

    displayPhones(filteredPhones);
}

// Function to setup company filters in the dropdown
function setupFilters(phones) {
    const companyFilter = document.getElementById('companyFilter');
    const companies = [...new Set(phones.map(phone => phone.company))];

    companyFilter.innerHTML = '<option value="">Select Company</option>';

    companies.forEach(company => {
        const option = document.createElement('option');
        option.value = company;
        option.textContent = company;
        companyFilter.appendChild(option);
    });

    companyFilter.addEventListener('change', filterPhones);
}

// Function to apply sorting based on selected sort option
function applySort() {
    const sortFilter = document.getElementById('sortFilter');
    const sortOption = sortFilter.value;

    if (displayedPhones.length === 0) return;

    try {
        let sortedPhones = [...displayedPhones];

        switch(sortOption) {
            case 'priceLowHigh':
                sortedPhones.sort((a, b) => a.price - b.price);
                break;
            case 'priceHighLow':
                sortedPhones.sort((a, b) => b.price - a.price);
                break;
            case 'modelAZ':
                sortedPhones.sort((a, b) => a.model.localeCompare(b.model));
                break;
            case 'modelZA':
                sortedPhones.sort((a, b) => b.model.localeCompare(a.model));
                break;
        }

        displayPhones(sortedPhones);
    } catch (error) {
        console.error('Error applying sort:', error);
        alert("Failed to sort phones");
    }
}

// Call the function to fetch and display phones when the page loads
document.addEventListener('DOMContentLoaded', () => {
    getPhones();

    const searchInputField = document.getElementById('searchInput');
    searchInputField.addEventListener('keyup', handleSearchAndCount);

    const sortFilter = document.getElementById('sortFilter');
    sortFilter.addEventListener('change', applySort);
});