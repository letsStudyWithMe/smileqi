import Footer from '@/components/Footer';
import {Question} from '@/components/RightContent';
import {LinkOutlined} from '@ant-design/icons';
import type {Settings as LayoutSettings} from '@ant-design/pro-components';
import {SettingDrawer} from '@ant-design/pro-components';
import type {RunTimeLayoutConfig} from '@umijs/max';
import {history, Link} from '@umijs/max';
import defaultSettings from '../config/defaultSettings';
import {AvatarDropdown, AvatarName} from './components/RightContent/AvatarDropdown';
import {errorConfig} from './requestConfig';
import {getLoginUser} from "@/services/smileqi/userController";
import {MenuDataItem} from "@umijs/route-utils";
import loopMenuItem from "@/utils/fixMenuItemIcon";
import {showSysMenu} from "@/services/smileqi/sysMenuController";
import fixMenuItemIcon from "@/utils/fixMenuItemIcon";
import {parseRoutes} from "@/utils/dynamicRoutes";
import axios from "axios";

const isDev = process.env.NODE_ENV === 'development';
const loginPath = '/user/login';

/*export default {
  created() {
    this.fetchMenuData();
  },
  async fetchMenuData() {
    try {
      const response = await axios.get('/sysmenu/showSysMenu'); // 从API获取菜单数据
      window.dynamicRoutes = fixMenuItemIcon(response.data); // 将菜单数据存储到window.dynamicRoutes中
    } catch (error) {
      console.error('获取菜单数据失败：', error);
    }
  }
};*/

// @ts-ignore
export async function patchRoutes({routes, routeComponents}) {
/*  const response = await fetch(`/api/sysmenu/showSysMenu`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  }).then((respone) => {
    return respone.json();
  }).then(data => {
    console.log(data);
    window.dynamicRoutes = data.data;
  });*/

  if (window.dynamicRoutes) {
    console.log(window.dynamicRoutes)
    console.log( Object.keys(routes))
    const currentRouteIndex = Object.keys(routes).length;
    console.log(currentRouteIndex)
    const parsedRoutes = parseRoutes(window.dynamicRoutes, currentRouteIndex);
    Object.assign(routes, parsedRoutes.routes); // 参数传递的为引用类型，直接操作原对象，合并路由数据
    Object.assign(routeComponents, parsedRoutes.routeComponents); // 合并组件
  }
}

/**
 * @see  https://umijs.org/zh-CN/plugins/plugin-initial-state
 * */
export async function getInitialState(): Promise<{
  settings?: Partial<LayoutSettings>;
  currentUser?: API.LoginUserVO;
  loading?: boolean;
  menuData?: MenuDataItem[];
  fetchUserInfo?: () => Promise<API.LoginUserVO | undefined>;
}> {
  const fetchUserInfo = async () => {
    try {
      const msg = await getLoginUser({
        skipErrorHandler: true,
      });
      return msg.data;
    } catch (error) {
      history.push(loginPath);
    }
    return undefined;
  };
  // 如果不是登录页面，执行
  const {location} = history;
  if (location.pathname !== loginPath) {
    const currentUser = await fetchUserInfo();
    return {
      currentUser,
      settings: defaultSettings as Partial<LayoutSettings>,
    };
  }
  return {
    settings: defaultSettings as Partial<LayoutSettings>,
  };
}

// ProLayout 支持的api https://procomponents.ant.design/components/layout
export const layout: RunTimeLayoutConfig = ({initialState, setInitialState}) => {
  return {
    actionsRender: () => [<Question key="doc"/>],
    avatarProps: {
      src: initialState?.currentUser?.userAvatar,
      title: <AvatarName/>,
      render: (_, avatarChildren) => {
        return <AvatarDropdown>{avatarChildren}</AvatarDropdown>;
      },
    },
    waterMarkProps: {
      content: initialState?.currentUser?.userName,
    },
    footerRender: () => <Footer/>,
    onPageChange: () => {
      const {location} = history;
      // 如果没有登录，重定向到 login
      if (!initialState?.currentUser && location.pathname !== loginPath) {
        history.push(loginPath);
      }
    },
    layoutBgImgList: [
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/D2LWSqNny4sAAAAAAAAAAAAAFl94AQBr',
        left: 85,
        bottom: 100,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/C2TWRpJpiC0AAAAAAAAAAAAAFl94AQBr',
        bottom: -68,
        right: -45,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/F6vSTbj8KpYAAAAAAAAAAAAAFl94AQBr',
        bottom: 0,
        left: 0,
        width: '331px',
      },
    ],
    links: isDev
      ? [
        <Link key="openapi" to="/umi/plugin/openapi" target="_blank">
          <LinkOutlined/>
          <span>OpenAPI 文档</span>
        </Link>,
      ]
      : [],
    //menuDataRender:(menuData:MenuDataItem[]) => fixMenuItemIcon(menuData),
    // 自定义 403 页面
    // unAccessible: <div>unAccessible</div>,
    //自定义菜单
    /*postMenuData: () => {
      //return fixMenuItemIcon(initialState?.menuData); //不显示icon 解决方法一
      return loopMenuItem(initialState?.menuData);//不显示icon 解决方法二
    },*/
    // 增加一个 loading 的状态
    childrenRender: (children) => {
      // if (initialState?.loading) return <PageLoading />;
      return (
        <>
          {children}
          <SettingDrawer
            disableUrlParams
            enableDarkTheme
            settings={initialState?.settings}
            onSettingChange={(settings) => {
              // @ts-ignore
              setInitialState((preInitialState) => ({
                ...preInitialState,
                settings,
              }));
            }}
          />
        </>
      );
    },
    ...initialState?.settings,
  };
};

/**
 * @name request 配置，可以配置错误处理
 * 它基于 axios 和 ahooks 的 useRequest 提供了一套统一的网络请求和错误处理方案。
 * @doc https://umijs.org/docs/max/request#配置
 */
export const request = {
  baseURL: "http://localhost:8083/api",
  withCredentials: true,
  ...errorConfig,
};
