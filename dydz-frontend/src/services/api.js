/*
 * @Author: weihua hu
 * @Date: 2026-02-11 01:26:48
 * @LastEditTime: 2026-02-11 01:37:57
 * @LastEditors: weihua hu
 * @Description: 
 */
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:10001',
  timeout: 10000
});

export default api;
