// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 POST /sysmenu/add */
export async function addSysMenu(body: API.SysMenuAddRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseLong>('/sysmenu/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /sysmenu/delete */
export async function deleteSysMenu(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/sysmenu/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /sysmenu/list/page */
export async function listSysMenuByPage(
  body: API.SysMenuQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageSysMenu>('/sysmenu/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /sysmenu/showSysMenu */
export async function showSysMenu(options?: { [key: string]: any }) {
  return request<API.BaseResponseListSysMenu>('/sysmenu/showSysMenu', {
    method: 'POST',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /sysmenu/update */
export async function updateSysMenu(
  body: API.SysMenuUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean>('/sysmenu/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
