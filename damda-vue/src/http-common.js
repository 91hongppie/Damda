import axios from "axios"

export default axios.create({
  baseURL: 'https://k02b2051.p.ssafy.io',
  headers: {
    "Content-type": "application/json"}
});
