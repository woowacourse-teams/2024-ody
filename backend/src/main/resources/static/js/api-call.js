const DEV_SERVER = 'https://dev.oody.site';
const PROD_SERVER = 'https://prod.oody.site';
const ODSAY = '/odsay';
const GOOGLE = '/google';

const COUNT_URL = '/admin/api-call/count';
const TOGGLE_URL = '/admin/api-call/toggle';
const ENABLED_URL = '/admin/api-call/enabled';

async function initializeApiCallCounts() {
    try {
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
        console.error('Error fetching api call count:', error);
    }
}

async function fetchApiCallCount(url, endpoint) {
    try {
        const response = await fetch(url + endpoint);
        return response.data.count;
    } catch (error) {
        console.warn(`Error fetching ${endpoint} from ${url}:`, error);
        return 'loading..';
    }
}

async function initializeApiCallEnabledButtons() {
    const configs = [
        {id: 'odsayDevToggleButton', server: DEV_SERVER, clientType: ODSAY},
        {id: 'googleDevToggleButton', server: DEV_SERVER, clientType: GOOGLE},
        {id: 'odsayProdToggleButton', server: PROD_SERVER, clientType: ODSAY},
        {id: 'googleProdToggleButton', server: PROD_SERVER, clientType: GOOGLE}
    ];

    for (const config of configs) {
        const button = document.getElementById(config.id);
        await fetchApiCallEnabled(button, config.server, config.clientType);
        button.addEventListener('click', (event) => handleButtonClick(event.target, config.server, config.clientType));
    }
}

async function handleButtonClick(button, server, clientType) {
    try {
        const toggleResponse = await fetch(server + TOGGLE_URL + clientType, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        });
        if (!toggleResponse.ok) {
            throw new Error('Network response was not ok');
        }

        await fetchApiCallEnabled(button, server, clientType);
    } catch (error) {
        console.error('Error toggling enabled button:', error);
    }
}

async function fetchApiCallEnabled(button, server, clientType) {
    try {
        const response = await fetch(server + ENABLED_URL + clientType);
        const data = await response.json();
        updateButton(button, data.enabled);
    } catch (error) {
        console.warn('Error fetching api call enabled:', error);
        button.textContent = 'unknown';
        button.className = 'unknown-button';
    }
}

function updateButton(button, enabled) {
    button.textContent = enabled ? 'enabled' : 'disabled';
    button.className = enabled ? 'blue-button' : 'red-button';
}

document.addEventListener('DOMContentLoaded', function () {
    initializeApiCallCounts();
    initializeApiCallEnabledButtons();
});
