import '@umijs/max';
import { Button, message, notification } from 'antd';
import defaultSettings from '../config/defaultSettings';
import {request} from "@@/exports";
import {showSysMenu} from "@/services/smileqi/sysMenuController";
const { pwa } = defaultSettings;
const isHttps = document.location.protocol === 'https:';

try {
  //const msg = await showSysMenu();
  const routesData = [
    {
      "id": 1,
      "menuId": "welcome",
      "parentId": "",
      "enable": true,
      "name": "欢迎页面",
      "sort": 1000,
      "path": "/welcome",
      "direct": true,
      "createdAt": "1992-08-17 07:29:03"
    },
    {
      "id": 2,
      "menuId": "user",
      "parentId": "",
      "enable": true,
      "name": "用户管理",
      "sort": 2000,
      "path": "/user",
      "direct": false,
      "createdAt": "2011-01-21 09:25:49"
    },
    {
      "id": 3,
      "menuId": "user_management",
      "parentId": "user",
      "enable": true,
      "name": "用户信息",
      "sort": 2001,
      "path": "/user/showUsers",
      "direct": false,
      "createdAt": "1986-06-03 02:38:12"
    },
    {
      "id": 4,
      "menuId": "role_management",
      "parentId": "user",
      "enable": true,
      "name": "角色管理",
      "sort": 2002,
      "path": "/user/role",
      "direct": false,
      "createdAt": "1986-06-03 02:38:12"
    },
    {
      "id": 5,
      "menuId": "permission_management",
      "parentId": "user",
      "enable": true,
      "name": "权限管理",
      "sort": 2003,
      "path": "/user/permission",
      "direct": false,
      "createdAt": "1986-06-03 02:38:12"
    },
    {
      "id": 6,
      "menuId": "app_management",
      "parentId": "user",
      "enable": true,
      "name": "应用管理",
      "sort": 2004,
      "path": "/user/app",
      "direct": false,
      "createdAt": "1986-06-03 02:38:12"
    }
  ]
  if (routesData) {
    window.dynamicRoutes = routesData;
  }
} catch {
  message.error('路由加载失败');
}

export {};
const clearCache = () => {
  // remove all caches
  if (window.caches) {
    caches
      .keys()
      .then((keys) => {
        keys.forEach((key) => {
          caches.delete(key);
        });
      })
      .catch((e) => console.log(e));
  }
};

// if pwa is true
if (pwa) {
  // Notify user if offline now
  window.addEventListener('sw.offline', () => {
    message.warning('当前处于离线状态');
  });

  // Pop up a prompt on the page asking the user if they want to use the latest version
  window.addEventListener('sw.updated', (event: Event) => {
    const e = event as CustomEvent;
    const reloadSW = async () => {
      // Check if there is sw whose state is waiting in ServiceWorkerRegistration
      // https://developer.mozilla.org/en-US/docs/Web/API/ServiceWorkerRegistration
      const worker = e.detail && e.detail.waiting;
      if (!worker) {
        return true;
      }
      // Send skip-waiting event to waiting SW with MessageChannel
      await new Promise((resolve, reject) => {
        const channel = new MessageChannel();
        channel.port1.onmessage = (msgEvent) => {
          if (msgEvent.data.error) {
            reject(msgEvent.data.error);
          } else {
            resolve(msgEvent.data);
          }
        };
        worker.postMessage(
          {
            type: 'skip-waiting',
          },
          [channel.port2],
        );
      });
      clearCache();
      window.location.reload();
      return true;
    };
    const key = `open${Date.now()}`;
    const btn = (
      <Button
        type="primary"
        onClick={() => {
          notification.destroy(key);
          reloadSW();
        }}
      >
        {'刷新'}
      </Button>
    );
    notification.open({
      message: '有新内容',
      description: '请点击“刷新”按钮或者手动刷新页面',
      btn,
      key,
      onClose: async () => null,
    });
  });
} else if ('serviceWorker' in navigator && isHttps) {
  // unregister service worker
  const { serviceWorker } = navigator;
  if (serviceWorker.getRegistrations) {
    serviceWorker.getRegistrations().then((sws) => {
      sws.forEach((sw) => {
        sw.unregister();
      });
    });
  }
  serviceWorker.getRegistration().then((sw) => {
    if (sw) sw.unregister();
  });
  clearCache();
}
