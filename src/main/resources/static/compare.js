window.onload = function() {
    // Retrieve selected phones from localStorage
    const selectedPhones = JSON.parse(localStorage.getItem('selectedPhones'));

    if (selectedPhones && selectedPhones.length === 2) {
        const comparisonTable = document.getElementById('comparisonTable');

        // Retrieve phone data (including new attributes)
        const phones = {
            Apple: [
                { name: "iPhone 14", price: "$799", image: "https://upload.wikimedia.org/wikipedia/commons/5/5b/IPhone_14_Pro_Space_Black.svg", display: "6.1 inch", battery: "20 hours", processor: "A15 Bionic", ram: "4GB", storage: "128GB", camera: "12MP", os: "iOS 16" },
                { name: "iPhone 14 Pro", price: "$999", image: "https://upload.wikimedia.org/wikipedia/commons/5/5b/IPhone_14_Pro_Space_Black.svg", display: "6.1 inch", battery: "23 hours", processor: "A16 Bionic", ram: "6GB", storage: "128GB", camera: "48MP", os: "iOS 16" },
                { name: "iPhone SE (2023)", price: "$429", image: "https://upload.wikimedia.org/wikipedia/commons/2/2e/IPhone_SE_3rd_Gen.svg", display: "4.7 inch", battery: "15 hours", processor: "A15 Bionic", ram: "4GB", storage: "64GB", camera: "12MP", os: "iOS 16" },
            ],
            Samsung: [
                { name: "Galaxy S23", price: "$849", image: "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5f/Samsung_Galaxy_S23.svg/1200px-Samsung_Galaxy_S23.svg.png", display: "6.1 inch", battery: "22 hours", processor: "Snapdragon 8 Gen 2", ram: "8GB", storage: "128GB", camera: "50MP", os: "Android 13" },
                { name: "Galaxy Z Flip 5", price: "$999", image: "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2d/Samsung_Galaxy_Z_Flip5.svg/1200px-Samsung_Galaxy_Z_Flip5.svg.png", display: "6.7 inch", battery: "22 hours", processor: "Snapdragon 8 Gen 2", ram: "8GB", storage: "256GB", camera: "12MP", os: "Android 13" },
                { name: "Galaxy A54", price: "$449", image: "https://upload.wikimedia.org/wikipedia/commons/8/8a/Samsung_Galaxy_A54.svg", display: "6.4 inch", battery: "20 hours", processor: "Exynos 1380", ram: "6GB", storage: "128GB", camera: "50MP", os: "Android 13" },
            ],
            Google: [
                { name: "Pixel 8", price: "$699", image: "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2b/Google_Pixel_8.svg/1200px-Google_Pixel_8.svg.png", display: "6.2 inch", battery: "20 hours", processor: "Google Tensor G3", ram: "8GB", storage: "128GB", camera: "50MP", os: "Android 14" },
                { name: "Pixel 8 Pro", price: "$999", image: "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7d/Google_Pixel_8_Pro.svg/1200px-Google_Pixel_8_Pro.svg.png", display: "6.7 inch", battery: "22 hours", processor: "Google Tensor G3", ram: "12GB", storage: "128GB", camera: "50MP", os: "Android 14" },
                { name: "Pixel Fold", price: "$1799", image: "https://upload.wikimedia.org/wikipedia/commons/thumb/3/31/Google_Pixel_Fold.svg/1200px-Google_Pixel_Fold.svg.png", display: "7.6 inch", battery: "24 hours", processor: "Google Tensor G2", ram: "12GB", storage: "256GB", camera: "48MP", os: "Android 14" },
            ]
        };

        // Find the selected phones' data
        let phone1, phone2;
        for (const company in phones) {
            phones[company].forEach(phone => {
                if (phone.name === selectedPhones[0]) {
                    phone1 = phone;
                }
                if (phone.name === selectedPhones[1]) {
                    phone2 = phone;
                }
            });
        }

        // Insert the comparison data into the table
        if (phone1 && phone2) {
            comparisonTable.innerHTML = `
                <tr>
                    <td>Model</td>
                    <td> ${phone1.name}</td>
                    <td> ${phone2.name}</td>
                </tr>
                <tr>
                    <td>Price</td>
                    <td>${phone1.price}</td>
                    <td>${phone2.price}</td>
                </tr>
                <tr>
                    <td>Display</td>
                    <td>${phone1.display}</td>
                    <td>${phone2.display}</td>
                </tr>
                <tr>
                    <td>Battery Life</td>
                    <td>${phone1.battery}</td>
                    <td>${phone2.battery}</td>
                </tr>
                <tr>
                    <td>Processor</td>
                    <td>${phone1.processor}</td>
                    <td>${phone2.processor}</td>
                </tr>
                <tr>
                    <td>RAM</td>
                    <td>${phone1.ram}</td>
                    <td>${phone2.ram}</td>
                </tr>
                <tr>
                    <td>Storage</td>
                    <td>${phone1.storage}</td>
                    <td>${phone2.storage}</td>
                </tr>
                <tr>
                    <td>Camera</td>
                    <td>${phone1.camera}</td>
                    <td>${phone2.camera}</td>
                </tr>
                <tr>
                    <td>OS</td>
                    <td>${phone1.os}</td>
                    <td>${phone2.os}</td>
                </tr>
            `;
        }
    } else {
        window.location.href = 'index.html';
    }
};
