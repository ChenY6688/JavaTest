import axios from 'axios';

export default function () {
    const api = axios.create({
        baseURL: import.meta.env.VITE_API_BASE_URL,
    });

    return {
        api,
    }
}