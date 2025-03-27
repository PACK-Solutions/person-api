import http from 'k6/http';
import {check, sleep} from 'k6';
import {Counter, Rate, Trend} from 'k6/metrics';
import {randomIntBetween, randomString} from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

// Custom metrics
const createPersonErrors = new Counter('create_person_errors');
const getPersonErrors = new Counter('get_person_errors');
const updatePersonErrors = new Counter('update_person_errors');
const deletePersonErrors = new Counter('delete_person_errors');
const successRate = new Rate('success_rate');
const requestDuration = new Trend('request_duration');

// Configuration for moderate load testing
export const options = {
    stages: [
        {duration: '5s', target: 5},   // Ramp-up to 5 users
        {duration: '5s', target: 15},
        {duration: '5s', target: 30},
        {duration: '10s', target: 30},
        {duration: '5s', target: 15},
        {duration: '5s', target: 0},    // Ramp-down to 0 users
    ],
    thresholds: {
        http_req_duration: ['p(95)<200'], // 95% of requests should be below 200ms
        success_rate: ['rate>0.95'],      // 95% of requests should be successful
    },
};

// Generate a random date of birth (between 18 and 80 years ago)
function randomDateOfBirth() {
    const now = new Date();
    const year = now.getFullYear() - randomIntBetween(18, 80);
    const month = randomIntBetween(1, 12);
    const day = randomIntBetween(1, 28); // Simplified to avoid month-specific day limits
    return `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
}

// Generate random person data
function generatePersonData() {
    return {
        firstName: `John${randomString(5)}`,
        lastName: `Doe${randomString(5)}`,
        dateOfBirth: randomDateOfBirth(),
        cityOfBirth: `City${randomString(3)}`,
        countryOfBirth: `Country${randomString(3)}`,
        nationality: `Nationality${randomString(3)}`
    };
}

// Main test function
export default function () {
    const baseUrl = 'http://localhost:8080/api/persons';
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // Create a new person
    const personData = generatePersonData();
    const createResponse = http.post(baseUrl, JSON.stringify(personData), params);

    // Record metrics
    requestDuration.add(createResponse.timings.duration);

    // Check if creation was successful
    const createSuccess = check(createResponse, {
        'create person status is 201': (r) => r.status === 201,
        'create person response has ID': (r) => JSON.parse(r.body).id !== undefined,
    });

    successRate.add(createSuccess);

    if (!createSuccess) {
        createPersonErrors.add(1);
        console.log(`Create person failed: ${createResponse.status} - ${createResponse.body}`);
        return; // Skip the rest of the test if creation fails
    }

    // Get the created person's ID
    const personId = JSON.parse(createResponse.body).id;

    // Small delay between requests
    sleep(1);

    // Get all persons
    const getAllResponse = http.get(baseUrl, params);
    requestDuration.add(getAllResponse.timings.duration);

    const getAllSuccess = check(getAllResponse, {
        'get all persons status is 200': (r) => r.status === 200,
        'get all persons response is an array': (r) => Array.isArray(JSON.parse(r.body)),
    });

    successRate.add(getAllSuccess);

    if (!getAllSuccess) {
        getPersonErrors.add(1);
        console.log(`Get all persons failed: ${getAllResponse.status} - ${getAllResponse.body}`);
    }

    sleep(1);

    // Get person by ID
    const getByIdResponse = http.get(`${baseUrl}/${personId}`, params);
    requestDuration.add(getByIdResponse.timings.duration);

    const getByIdSuccess = check(getByIdResponse, {
        'get person by ID status is 200': (r) => r.status === 200,
        'get person by ID returns correct person': (r) => JSON.parse(r.body).id === personId,
    });

    successRate.add(getByIdSuccess);

    if (!getByIdSuccess) {
        getPersonErrors.add(1);
        console.log(`Get person by ID failed: ${getByIdResponse.status} - ${getByIdResponse.body}`);
    }

    sleep(1);

    // Update person
    const updatedPersonData = {
        ...personData,
        firstName: `Updated${randomString(5)}`,
        lastName: `Updated${randomString(5)}`,
    };

    const updateResponse = http.put(`${baseUrl}/${personId}`, JSON.stringify(updatedPersonData), params);
    requestDuration.add(updateResponse.timings.duration);

    const updateSuccess = check(updateResponse, {
        'update person status is 200': (r) => r.status === 200,
        'update person returns updated data': (r) => {
            const body = JSON.parse(r.body);
            return body.firstName.startsWith('Updated') && body.lastName.startsWith('Updated');
        },
    });

    successRate.add(updateSuccess);

    if (!updateSuccess) {
        updatePersonErrors.add(1);
        console.log(`Update person failed: ${updateResponse.status} - ${updateResponse.body}`);
    }

    sleep(1);

    // Delete person
    const deleteResponse = http.del(`${baseUrl}/${personId}`, null, params);
    requestDuration.add(deleteResponse.timings.duration);

    const deleteSuccess = check(deleteResponse, {
        'delete person status is 204': (r) => r.status === 204,
    });

    successRate.add(deleteSuccess);

    if (!deleteSuccess) {
        deletePersonErrors.add(1);
        console.log(`Delete person failed: ${deleteResponse.status} - ${deleteResponse.body}`);
    }

    // Add some randomness to the test flow
    sleep(randomIntBetween(1, 3));
}
