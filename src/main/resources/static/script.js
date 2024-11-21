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
            hideSearchInfo();
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

function isAlphanumeric(str) {
    return /^[a-zA-Z0-9\s+-]+$/.test(str);
}

// Function to handle the search and frequency count
async function handleSearchAndCount(event) {
    if (event.key === 'Enter' || event.target.value === ''){

    searchTerm = document.getElementById('searchInput').value.trim().toLowerCase();

    if (searchTerm === "") {
        getPhones();
        hideSearchInfo(); // Hide search frequency and suggestions
        return;
    }

            if (!isAlphanumeric(searchTerm)) {
                alert("Please enter only alphanumeric characters.");
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
}

function hideSearchInfo() {
    document.getElementById('frequencyDisplay').style.display = 'none';
    document.getElementById('suggestionsContainer').style.display = 'none';
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
    const companyFilter = document.getElementById('companyFilter');
    const selectedCompany = companyFilter.value.trim().toLowerCase();

    if (displayedPhones.length === 0) return; // If no phones are displayed, do nothing

    try {
        // First, filter by company if one is selected
        let phonesToSort = selectedCompany
            ? displayedPhones.filter(phone => phone.company.toLowerCase() === selectedCompany)
            : displayedPhones;

        // Then apply sorting
        if (sortOption === 'priceLowHigh') {
            phonesToSort.sort((a, b) => a.price - b.price);
        } else if (sortOption === 'priceHighLow') {
            phonesToSort.sort((a, b) => b.price - a.price);
        } else if (sortOption === 'modelAZ') {
            phonesToSort.sort((a, b) => a.model.localeCompare(b.model));
        } else if (sortOption === 'modelZA') {
            phonesToSort.sort((a, b) => b.model.localeCompare(a.model));
        }

        // Re-display the phones after sorting
        displayPhones(phonesToSort);

    } catch (error) {
        console.error('Error applying sort:', error);
        alert("Failed to sort phones");
    }
}

// Modify the filterPhones function to also apply current sort
function filterPhones() {
    const companyFilter = document.getElementById('companyFilter');
    const selectedCompany = companyFilter.value.trim().toLowerCase();

    const filteredPhones = displayedPhones.filter(phone =>
        (selectedCompany === '' || phone.company.toLowerCase() === selectedCompany) &&
        phone.model.toLowerCase().includes(searchTerm)
    );

    // Apply current sort to filtered phones
    const sortFilter = document.getElementById('sortFilter');
    const currentSort = sortFilter.value;

    if (currentSort) {
        applySort(); // This will now use the filtered phones
    } else {
        displayPhones(filteredPhones);
    }
}

function resetAllFilters() {
    // Reset search input
    document.getElementById('searchInput').value = '';

    // Reset company filter dropdown to default
    document.getElementById('companyFilter').selectedIndex = 0;

    // Reset sort filter dropdown to default
    document.getElementById('sortFilter').selectedIndex = 0;

    // Reset search term and displayed phones
    searchTerm = '';

    // Reload all phones
    getPhones();

    // Hide frequency and suggestions displays
    document.getElementById('frequencyDisplay').style.display = 'none';
    document.getElementById('suggestionsContainer').style.display = 'none';
}

// Update event listeners
document.addEventListener('DOMContentLoaded', () => {
    getPhones();

    const searchInputField = document.getElementById('searchInput');
    searchInputField.addEventListener('keydown', handleSearchAndCount);
    searchInputField.addEventListener('input', function(event) {
        if (event.target.value === '') {
            getPhones();
        } else if (!isValidInput(event.target.value)) {
            alert("Please enter only alphanumeric characters, spaces, plus (+), or minus (-).");
            event.target.value = event.target.value.replace(/[^a-zA-Z0-9\s+-]/g, '');
        }
    });

    const companyFilter = document.getElementById('companyFilter');
    companyFilter.addEventListener('change', filterPhones);

    const sortFilter = document.getElementById('sortFilter');
    sortFilter.addEventListener('change', applySort);
});