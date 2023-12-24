import React from 'react';
import type { MenuDataItem } from '@ant-design/pro-layout';
import * as allIcons from '@ant-design/icons';
import {ProfileOutlined, SmileOutlined,TableOutlined,BarsOutlined,FormOutlined,RetweetOutlined} from '@ant-design/icons';

//自定义菜单不显示icon，解决方法一
const IconMap = {
  smile: SmileOutlined,
  profile: ProfileOutlined,
  table:TableOutlined,
  BarsOutlined: BarsOutlined,
  form: FormOutlined,
  RetweetOutlined: RetweetOutlined,
} as const;
const loopMenuItem = (menus: MenuDataItem[]): MenuDataItem[] =>
  menus.map(({ icon, routes, ...item }) => ({
    ...item,
    icon: icon && IconMap[icon as string],
    routes: routes && loopMenuItem(routes),
}));

//自定义菜单不显示icon，解决方法二
const fixMenuItemIcon = (menus: MenuDataItem[], iconType = 'Outlined'): MenuDataItem[] => {
  if(menus == undefined || menus == null) menus= [];
  menus.forEach((item) => {
    const { icon, children } = item;
    if (typeof icon === 'string') {
      const fixIconName = icon.slice(0, 1).toLocaleUpperCase() + icon.slice(1) + iconType;
      // eslint-disable-next-line no-param-reassign
      // @ts-ignore
      item.icon = React.createElement(allIcons[fixIconName] || allIcons[icon]);
    }
    // eslint-disable-next-line no-param-reassign,@typescript-eslint/no-unused-expressions
    children && children.length > 0 ? (item.children = fixMenuItemIcon(children)) : null;
  });
  return menus;
};

export default fixMenuItemIcon;
