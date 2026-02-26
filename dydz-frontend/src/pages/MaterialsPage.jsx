import React, { useEffect, useState } from 'react';
import { Button, Form, Input, InputNumber, Modal, Space, Table, message } from 'antd';
import api from '../services/api.js';

export default function MaterialsPage() {
  const [data, setData] = useState([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();

  const load = async () => {
    const res = await api.get('/materials');
    setData(res.data);
  };

  useEffect(() => {
    load();
  }, []);

  const columns = [
    { title: '名称', dataIndex: 'name' },
    { title: '编码', dataIndex: 'code' },
    { title: '倍率', dataIndex: 'multiplier' },
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
                title: '确定删除该材质吗？',
                onOk: async () => {
                  await api.delete(`/materials/${record.id}`);
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
      await api.put(`/materials/${editing.id}`, values);
      message.success('已更新');
    } else {
      await api.post('/materials', values);
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
        <Button type="primary" onClick={() => { setEditing(null); setOpen(true); }}>新增材质</Button>
      </Space>
      <Table rowKey="id" columns={columns} dataSource={data} pagination={{ pageSize: 10 }} />

      <Modal
        title={editing ? '编辑材质' : '新增材质'}
        open={open}
        onCancel={() => { setOpen(false); setEditing(null); }}
        onOk={handleSubmit}
        okText="保存"
      >
        <Form form={form} layout="vertical" initialValues={{ multiplier: 1 }}>
          <Form.Item name="name" label="名称" rules={[{ required: true }]}> 
            <Input />
          </Form.Item>
          <Form.Item name="code" label="编码">
            <Input />
          </Form.Item>
          <Form.Item name="multiplier" label="材质倍率" rules={[{ required: true }]}>
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
