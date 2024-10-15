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

async function handleApiCallToggle(env, client) {
    const TOGGLE_URL = '/admin/api-call/toggle';
    const server = env === 'dev' ? DEV_SERVER : PROD_SERVER;

    try {
        const response = await axios.post(server + TOGGLE_URL + client);

        if (response.data && response.data.enabled !== undefined) {
            const buttonId = `${env}${api.charAt(0).toUpperCase() + api.slice(1)}Toggle`;
            const button = document.getElementById(buttonId);
            const statusId = `${env}${api.charAt(0).toUpperCase() + api.slice(1)}Status`;

            if (response.data.enabled) {
                button.innerText = 'Disable'; // Change the text to Disable if enabled
                document.getElementById(statusId).innerText = '1'; // or the actual count based on your logic
            } else {
                button.innerText = 'Enable'; // Change the text to Enable if disabled
                document.getElementById(statusId).innerText = '0'; // or the actual count based on your logic
            }
        }
    } catch (error) {
        console.error(`Error toggling ${api} for ${env}:`, error);
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
