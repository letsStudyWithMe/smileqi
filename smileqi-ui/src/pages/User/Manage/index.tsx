import React, {useEffect, useState} from 'react';
import {Button, Input, message, Popconfirm, Table} from "antd";
import {deleteUserUsingPost, listUserByPageUsingPost} from "@/services/qibi/userController";
import {ColumnsType} from "antd/es/table";
import {RedoOutlined} from "@ant-design/icons";
import Search from "antd/es/input/Search";
import {useModel} from "@@/exports";

const Manage: React.FC = () => {
  const initSearchParams = {
    page: 1,
    pageSize: 10,
  };
  // @ts-ignore
  const [searchParams, setSearchParams] = useState<API.UserQueryRequest>({...initSearchParams});
  const [userList, setUserList] = useState<API.User[]>();
  const [total, setTotal] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(false);
  const { Search } = Input;
  const {initialState} = useModel('@@initialState');
  const loadData = async (page: any, pageSize: any) => {
    try {
      searchParams.pageSize = pageSize ?? 10;
      searchParams.current = page ?? 1;
      const res = await listUserByPageUsingPost(searchParams);
      if (res.data) {
        // @ts-ignore
        if (res.data.total == 0) {
          message.success("未查询到相关图表")
        }
        setUserList(res.data.records ?? []);
        // @ts-ignore
        setTotal(res.data.total ?? 0);
      } else {
        message.error("获取用户列表失败");
      }
    } catch (e: any) {
      message.error("获取用户列表失败," + e.message);
    }
  };

  useEffect(() => {
    // @ts-ignore
    loadData();
  }, [searchParams]);

  return (
    <div style={{marginBottom: 30}}>
      <Search placeholder="请输入用户名称" enterButton loading={loading} onSearch={(value) => {
        // 设置搜索条件
        // @ts-ignore
        setSearchParams({
          ...initSearchParams,
          userName: value,
        })
      }}/>
      <Table
        rowKey={"id"}
        pagination={{
          total: total,
          pageSizeOptions: [10, 20, 50, 100],
          onChange: loadData,
          showTotal: total => `共${total}条记录 `,
          defaultPageSize: 10,
          defaultCurrent: 1,
          position: ['bottomRight'],
          size: 'small',
          showSizeChanger: true,
          showQuickJumper: true,
        }}
        style={{marginTop: 20}}
        columns={columns}
        dataSource={userList}/>
    </div>
  );
};

const columns: ColumnsType<API.User> = [
  {
    title: 'Id',
    dataIndex: 'id',
    key: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
    key: 'userAccount',
  },
  {
    title: '昵称',
    dataIndex: 'userName',
    key: 'userName',
  },
  {
    title: '头像',
    key: 'userAvatar',
    dataIndex: 'userAvatar',
  },
  {
    title: '角色',
    key: 'userRole',
    dataIndex: 'userRole',
  }, {
    title: '创建时间',
    key: 'createTime',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
    render: (txt, record, index) =>{
      return (
          <div>
            <Popconfirm title="确定要删除此项？" onCancel={()=>console.log('取消删除')} onConfirm={()=>
            {
              const res =  deleteUserUsingPost(txt).then(data=>data.data);
              if (!res) {
                message.success("删除失败")
              }else {
                message.success("删除成功")
                location.reload();
              }
            }
            }>
              <Button type="primary" size="small">删除</Button>
            </Popconfirm>
          </div>
      )
    }
  },
];
export default Manage;
