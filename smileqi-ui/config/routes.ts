export default [
  {
    path: '/user',
    layout: false,
    routes: [
      { path: '/user/login', name: '登录', component: './User/Login' },
      { path: '/user/register', name: '注册', component: './User/Register' },
    ],
  },
/*  {
    name: '欢迎页面',
    path: "/welcome",
    icon: 'smile',
    component: './Welcome',
  },
  {
    path: '/user', name: '用户管理', icon: 'user',
    hideChildrenInMenu: false,
    routes: [
      {path: '/user/showUsers', name: '用户列表', component: './User/Manage',access: 'canAdmin'},
    ]
  },*/
  {
    path: '/admin',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      { path: '/admin', redirect: '/admin/sub-page' },
      { path: '/admin/sub-page', component: './Admin' },
    ],
  },
  { path: '/', redirect: '/welcome' },
  { path: '*', layout: false, component: './404' },
];
