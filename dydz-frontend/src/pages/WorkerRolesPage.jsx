import React, { useEffect, useState } from 'react';
import { Button, Form, Input, Modal, Space, Table, message } from 'antd';
import api from '../services/api.js';

export default function WorkerRolesPage() {
  const [data, setData] = useState([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();

  const load = async () => {
    const res = await api.get('/worker-roles');
    setData(res.data);
  };

  useEffect(() => {
    load();
  }, []);

  const columns = [
    { title: '编码', dataIndex: 'code' },
    { title: '名称', dataIndex: 'name' },
    {
      title: '操作',
      render: (_, record) => (
        <Space>
          <Button
            size="small"
            onClick={() => {
              setEditing(record);
              form.setFieldsValue(record);
              setOpen(true);
            }}
          >
            编辑
          </Button>
          <Button
            size="small"
            danger
            onClick={() => {
              Modal.confirm({
                title: '确定删除该岗位吗？',
                onOk: async () => {
                  await api.delete(`/worker-roles/${record.id}`);
                  message.success('已删除');
                  load();
                }
              });
            }}
          >
            删除
          </Button>
        </Space>
      )
    }
  ];

  const handleSubmit = async () => {
    const values = await form.validateFields();
    if (editing) {
      await api.put(`/worker-roles/${editing.id}`, values);
      message.success('已更新');
    } else {
      await api.post('/worker-roles', values);
      message.success('已新增');
    }
    setOpen(false);
    setEditing(null);
    form.resetFields();
    load();
  };

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={() => { setEditing(null); setOpen(true); }}>新增岗位</Button>
      </Space>
      <Table rowKey="id" columns={columns} dataSource={data} pagination={{ pageSize: 10 }} />

      <Modal
        title={editing ? '编辑岗位' : '新增岗位'}
        open={open}
        onCancel={() => { setOpen(false); setEditing(null); }}
        onOk={handleSubmit}
        okText="保存"
      >
        <Form form={form} layout="vertical">
          <Form.Item name="code" label="编码" rules={[{ required: true }]}> 
            <Input />
          </Form.Item>
          <Form.Item name="name" label="名称" rules={[{ required: true }]}> 
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
