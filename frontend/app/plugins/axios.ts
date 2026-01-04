import axios from "axios";

export default defineNuxtPlugin((nuxtApp) => {
    const defaultUrl = "http://localhost:8080/academics/api"; // Replace with your API

    let api = axios.create({
        baseURL: defaultUrl,
        headers: {
            common: {},
        },
    });

    return {
        provide: {
            api: api,
        },
    };
});