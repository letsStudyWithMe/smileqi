import axios from 'axios';
import type { RouteRecordNormalized } from 'vue-router';
import { UserState } from '@/store/modules/user/types';

export interface LoginData {
  userAccount: string;
  userPassword: string;
}

export interface LoginRes {
  id:string
}
export function login(data: LoginData) {
  console.log(456);
  const  a = axios.post<LoginRes>('/user/login', data);
  a.then((res) =>{
    console.log(789);
    console.log(res);
  })
  return a;
}

export function logout() {
  return axios.post<LoginRes>('/user/logout');
}

export function getUserInfo() {
  return axios.post<UserState>('/user/info');
}

export function getMenuList() {
  return axios.post<RouteRecordNormalized[]>('/user/menu');
}
