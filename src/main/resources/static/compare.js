document.addEventListener('DOMContentLoaded', () => {
    fetchPhones();
    document.getElementById('compareButton').addEventListener('click', comparePhones);
});

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

function displayComparison(comparison) {
    const comparisonResultContainer = document.getElementById('comparisonResultContainer');
    comparisonResultContainer.innerHTML = `
        <h2>Comparison Result</h2>
        <p>${comparison.comparisonResult}</p>
        <h3>${comparison.phone1.model}</h3>
        <img src="${comparison.phone1.imageUrl}" alt="${comparison.phone1.model}" width="100">
        <p>Price: $${comparison.phone1.price}</p>
        
        <h3>${comparison.phone2.model}</h3>
        <img src="${comparison.phone2.imageUrl}" alt="${comparison.phone2.model}" width="100">
        <p>Price: $${comparison.phone2.price}</p>
    `;
}