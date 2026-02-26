import React, { useEffect, useState } from 'react';
import { Button, Form, Input, Modal, Space, Table, message } from 'antd';
import api from '../services/api.js';

export default function SpecsPage() {
  const [data, setData] = useState([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();

  const load = async () => {
    const res = await api.get('/specs');
    setData(res.data);
  };

  useEffect(() => {
    load();
  }, []);

  const columns = [
    { title: '名称', dataIndex: 'name' },
    { title: '描述', dataIndex: 'description' },
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
                title: '确定删除该规格吗？',
                onOk: async () => {
                  await api.delete(`/specs/${record.id}`);
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
      await api.put(`/specs/${editing.id}`, values);
      message.success('已更新');
    } else {
      await api.post('/specs', values);
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
        <Button type="primary" onClick={() => { setEditing(null); setOpen(true); }}>新增规格</Button>
      </Space>
      <Table rowKey="id" columns={columns} dataSource={data} pagination={{ pageSize: 10 }} />

      <Modal
        title={editing ? '编辑规格' : '新增规格'}
        open={open}
        onCancel={() => { setOpen(false); setEditing(null); }}
        onOk={handleSubmit}
        okText="保存"
      >
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="名称" rules={[{ required: true }]}> 
            <Input />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
