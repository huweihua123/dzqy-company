import React, { useEffect, useState } from 'react';
import { Button, Form, Input, InputNumber, Modal, Space, Table, message } from 'antd';
import api from '../services/api.js';

export default function OwnersPage() {
  const [data, setData] = useState([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();

  const load = async () => {
    const res = await api.get('/owners');
    setData(res.data);
  };

  useEffect(() => {
    load();
  }, []);

  const columns = [
    { title: '名称', dataIndex: 'name' },
    { title: '联系人', dataIndex: 'contact' },
    { title: '电话', dataIndex: 'phone' },
    { title: '地址', dataIndex: 'address' },
    { title: '单价(元/吨)', dataIndex: 'unitPrice' },
    { title: '备注', dataIndex: 'remark' },
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
                title: '确定删除该货主吗？',
                onOk: async () => {
                  await api.delete(`/owners/${record.id}`);
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
      await api.put(`/owners/${editing.id}`, values);
      message.success('已更新');
    } else {
      await api.post('/owners', values);
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
        <Button type="primary" onClick={() => { setEditing(null); setOpen(true); }}>新增货主</Button>
      </Space>
      <Table rowKey="id" columns={columns} dataSource={data} pagination={{ pageSize: 10 }} />

      <Modal
        title={editing ? '编辑货主' : '新增货主'}
        open={open}
        onCancel={() => { setOpen(false); setEditing(null); }}
        onOk={handleSubmit}
        okText="保存"
      >
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="名称" rules={[{ required: true }]}> 
            <Input />
          </Form.Item>
          <Form.Item name="contact" label="联系人">
            <Input />
          </Form.Item>
          <Form.Item name="phone" label="电话">
            <Input />
          </Form.Item>
          <Form.Item name="address" label="地址">
            <Input />
          </Form.Item>
          <Form.Item name="unitPrice" label="单价(元/吨)" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
