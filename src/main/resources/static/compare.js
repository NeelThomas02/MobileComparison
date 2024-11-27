document.getElementById('compareButton').addEventListener('click', function() {
    const phone1Id = document.getElementById('phone1Select').value;
    const phone2Id = document.getElementById('phone2Select').value;

    // Check if both phones are selected
    if (!phone1Id || !phone2Id) {
        alert('Please select two phones to compare.');
        return;
    }

    // Fetch comparison data from the backend
    fetch(`http://localhost:8080/phones/compare?phone1=${phone1Id}&phone2=${phone2Id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Error fetching comparison: ${response.statusText}`);
            }
            return response.json();  // Parse JSON only if response is OK
        })
        .then(data => {
            // Pass the data to display in a table
            displayComparison(data);
        })
        .catch(error => {
            console.error(error);
            alert('An error occurred while fetching comparison data.');
        });
});

document.addEventListener('DOMContentLoaded', () => {
    // Fetch data from the backend
    fetch('http://localhost:8080/phones')
        .then(response => {
            if (!response.ok) {
                throw new Error('API error: ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            populateSelect('phone1Select', data);
            populateSelect('phone2Select', data);
        })
        .catch(error => {
            console.error('Error fetching phones:', error);
        });
});

// Function to populate the dropdown
function populateSelect(selectId, phones) {
    const selectElement = document.getElementById(selectId);

    // Check if select element exists
    if (!selectElement) {
        console.error(`Dropdown with ID '${selectId}' not found.`);
        return;
    }

    // Clear existing options
    selectElement.innerHTML = '<option value="">Select a phone</option>';

    // Populate dropdown with phone models
    phones.forEach(phone => {
        const option = document.createElement('option');
        option.value = phone.id; // Set phone ID as the value
        option.textContent = phone.model; // Use phone model as display text
        selectElement.appendChild(option);
    });
}




async function fetchPhones() {
    try {
        const response = await fetch('/phones'); // Adjust this endpoint based on your API
        const phones = await response.json();
        populateDropdowns(phones);
    } catch (error) {
        console.error('Error fetching phones:', error);
    }
}

function populateDropdowns(phones) {
    const phone1Select = document.getElementById('phone1Select');
    const phone2Select = document.getElementById('phone2Select');

    phones.forEach(phone => {
        const option1 = new Option(phone.model, phone.id);
        const option2 = new Option(phone.model, phone.id);
        phone1Select.add(option1);
        phone2Select.add(option2);
    });
}

async function comparePhones() {
    const phone1Id = document.getElementById('phone1Select').value;
    const phone2Id = document.getElementById('phone2Select').value;

    if (phone1Id === phone2Id) {
        alert('Please select two different phones to compare.');
        return;
    }

    try {
        const response = await fetch(`/phones/compare/detailed?id1=${phone1Id}&id2=${phone2Id}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const comparedPhones = await response.json();
        displayComparison(comparedPhones);
    } catch (error) {
        console.error('Error comparing phones:', error);
        alert(`An error occurred while comparing phones: ${error.message}`);
    }
}

function displayComparison(comparisonData) {
    const tbody = document.querySelector('#comparisonTable tbody');
    tbody.innerHTML = '';  // Clear the previous comparison

    // Loop through the comparison data and create rows for the table
    for (const feature in comparisonData) {
        if (comparisonData.hasOwnProperty(feature)) {
            const row = document.createElement('tr');
            
            // Feature Column
            const featureCell = document.createElement('td');
            featureCell.textContent = feature;
            row.appendChild(featureCell);

            // Phone 1 Column
            const phone1Cell = document.createElement('td');
            phone1Cell.textContent = comparisonData[feature].phone1 || 'N/A';
            row.appendChild(phone1Cell);

            // Phone 2 Column
            const phone2Cell = document.createElement('td');
            phone2Cell.textContent = comparisonData[feature].phone2 || 'N/A';
            row.appendChild(phone2Cell);

            // Append row to table
            tbody.appendChild(row);
        }
    }
}

