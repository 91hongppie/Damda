import axios from "axios"

export default axios.create({
  baseURL: 'https://k02b2051.p.ssafy.io',
  // baseURL: 'http://localhost:8000',
  headers: {
    "Content-type": "application/json"}
});
