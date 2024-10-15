const DEV_SERVER = 'https://dev.oody.site';
const PROD_SERVER = 'https://prod.oody.site';
const ODSAY = '/odsay';
const GOOGLE = '/google';

const devOdsayStatus = true;

async function fetchApiCallCounts() {
    try {
        const COUNT_URL = '/admin/api-call/count';

        const [odsayDevCount, googleDevCount, odsayProdCount, googleProdCount] = await Promise.all([
            fetchApiCallCount(DEV_SERVER, COUNT_URL + ODSAY),
            fetchApiCallCount(DEV_SERVER, COUNT_URL + GOOGLE),
            fetchApiCallCount(PROD_SERVER, COUNT_URL + ODSAY),
            fetchApiCallCount(PROD_SERVER, COUNT_URL + GOOGLE)
        ]);
        const odsayTotalCount = (typeof odsayDevCount !== 'number' || typeof odsayProdCount !== 'number' ? odsayDevCount : odsayDevCount + odsayProdCount);
        const googleTotalCount = (typeof googleDevCount !== 'number' || typeof googleProdCount !== 'number' ? googleDevCount : googleDevCount + googleProdCount);

        document.querySelector('#countTable').innerHTML = `
            <tr>
                <td>Dev</td>
                <td>${odsayDevCount}</td>
                <td>${googleDevCount}</td>
            </tr>
            <tr>
                <td>Prod</td>
                <td>${odsayProdCount}</td>
                <td>${googleProdCount}</td>
            </tr>
            <tr>
                <td>Total</td>
                <td>${odsayTotalCount}</td>
                <td>${googleTotalCount}</td>
            </tr>
        `;
    } catch (error) {
        console.error('Error fetching API call counts:', error);
    }
}

async function fetchApiCallCount(url, endpoint) {
    try {
        // const response = await axios.get(url + endpoint);
        const response = null;
        return response.data.count;
    } catch (error) {
        console.error(`Error fetching ${endpoint} from ${url}:`, error);
        return 'loading..';
    }
}

async function initializeButtons() {
    const buttonConfig = [
        { id: 'toggleButton1', server: DEV_SERVER, clientType: ODSAY },
        { id: 'toggleButton2', server: DEV_SERVER, clientType: GOOGLE },
        { id: 'toggleButton3', server: PROD_SERVER, clientType: ODSAY },
        { id: 'toggleButton4', server: PROD_SERVER, clientType: GOOGLE }
    ];

    buttonConfig.forEach(config => {
        const button = document.getElementById(config.id);
        button.addEventListener('click', (event) => handleButtonClick(event, config.server, config.clientType));
    });
}

async function handleButtonClick(event, server, clientType) {
    const TOGGLE_URL = '/admin/api-call/toggle/';
    const button = event.target;

    try {
        const response = await fetch(server + TOGGLE_URL + clientType, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const data = await response.json();

        updateButtonState(button, data.enabled);
    } catch (error) {
        console.error('Error:', error);
    }
}

function updateButtonState(button, enabled) {
    button.textContent = enabled ? 'enabled' : 'disabled';
    button.className = enabled ? 'blue-button' : 'red-button';
    button.dataset.enabled = enabled;
}

document.addEventListener('DOMContentLoaded', function () {
    fetchApiCallCounts();
    initializeButtons();
});
