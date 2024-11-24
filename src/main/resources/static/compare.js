// Array to store selected phones for comparison
let selectedPhones = [];

// Function to fetch and display the list of phones
async function fetchPhonesForComparison() {
    try {
        const response = await fetch('/phones');
        const phones = await response.json();

        if (phones.length > 0) {
            displayPhoneSelectionList(phones);
        } else {
            alert('No phones available for comparison.');
        }
    } catch (error) {
        console.error('Error fetching phones:', error);
        alert('Failed to load phone data.');
    }
}

// Function to display the phone selection list
function displayPhoneSelectionList(phones) {
    const phoneListContainer = document.getElementById('phoneListContainer');
    phoneListContainer.innerHTML = ''; // Clear existing content

    phones.forEach((phone) => {
        const phoneItem = document.createElement('div');
        phoneItem.className = 'phone-item';

        phoneItem.innerHTML = `
            <img src="${phone.imageUrl}" alt="${phone.model}" />
            <p>${phone.model}</p>
            <p>$${phone.price}</p>
            <input 
                type="checkbox" 
                class="compare-checkbox" 
                value="${phone.id}" 
                onchange="handlePhoneSelection(${phone.id}, '${phone.model}')"
            /> Compare
        `;

        phoneListContainer.appendChild(phoneItem);
    });
}

// Function to handle phone selection for comparison
function handlePhoneSelection(phoneId, phoneModel) {
    const checkbox = document.querySelector(`input[value="${phoneId}"]`);

    if (checkbox.checked) {
        if (selectedPhones.length >= 2) {
            alert('You can only compare up to 2 phones.');
            checkbox.checked = false;
            return;
        }
        selectedPhones.push({ id: phoneId, model: phoneModel });
    } else {
        selectedPhones = selectedPhones.filter((phone) => phone.id !== phoneId);
    }

    console.log('Selected phones for comparison:', selectedPhones);
}

// Function to fetch and compare selected phones
async function comparePhones() {
    if (selectedPhones.length !== 2) {
        alert('Please select exactly 2 phones for comparison.');
        return;
    }

    try {
        // Fetch details for the selected phones
        const phoneDetails = await Promise.all(
            selectedPhones.map((phone) =>
                fetch(`/phones/${phone.id}`).then((res) => res.json())
            )
        );

        displayComparisonTable(phoneDetails);
    } catch (error) {
        console.error('Error comparing phones:', error);
        alert('Failed to fetch comparison details.');
    }
}

// Function to display the comparison table
function displayComparisonTable(phoneDetails) {
    const comparisonContainer = document.getElementById('comparisonContainer');
    const comparisonTable = document.getElementById('comparisonTable').querySelector('tbody');

    // Clear any previous comparison data
    comparisonTable.innerHTML = '';

    // Populate the comparison table
    const features = [
        'model',
        'price',
        'company',
        'screenSize',
        'battery',
        'camera',
        'storage',
        'processor',
    ];

    features.forEach((feature) => {
        const row = document.createElement('tr');

        row.innerHTML = `
            <td>${capitalizeFirstLetter(feature)}</td>
            <td>${phoneDetails[0][feature] || 'N/A'}</td>
            <td>${phoneDetails[1][feature] || 'N/A'}</td>
        `;

        comparisonTable.appendChild(row);
    });

    // Show the comparison container
    comparisonContainer.style.display = 'block';
}

// Utility function to capitalize the first letter of a string
function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

// Load phones on page load
document.addEventListener('DOMContentLoaded', fetchPhonesForComparison);
