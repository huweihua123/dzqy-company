import React, { useEffect, useState } from 'react';
import { Button, Form, InputNumber, Modal, Select, Space, Table, message } from 'antd';
import api from '../services/api.js';

const typeOptions = [
  { value: 'PIECE_PER_TON', label: '计件(吨)' },
  { value: 'DAILY', label: '日薪' },
  { value: 'HOURLY', label: '计时' }
];

export default function SalaryRulesPage() {
  const [rules, setRules] = useState([]);
  const [roles, setRoles] = useState([]);
  const [open, setOpen] = useState(false);
  const [form] = Form.useForm();

  const load = async () => {
    const [rRes, roleRes] = await Promise.all([
      api.get('/salary-rules'),
      api.get('/worker-roles')
    ]);
    setRules(rRes.data);
    setRoles(roleRes.data);
  };

  useEffect(() => {
    load();
  }, []);

  const columns = [
    { title: '岗位', dataIndex: ['role', 'name'] },
    { title: '规则', dataIndex: 'ruleType' },
    { title: '吨价', dataIndex: 'pricePerTon' },
    { title: '日薪', dataIndex: 'dailySalary' },
    { title: '时薪', dataIndex: 'hourlySalary' }
  ];

  const handleSave = async () => {
    const values = await form.validateFields();
    await api.post('/salary-rules', { ...values, roleId: values.role });
    message.success('薪资规则已保存');
    setOpen(false);
    form.resetFields();
    load();
  };

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={() => setOpen(true)}>新增/更新规则</Button>
      </Space>
      <Table rowKey="id" columns={columns} dataSource={rules} />

      <Modal title="薪资规则" open={open} onCancel={() => setOpen(false)} onOk={handleSave} okText="保存">
        <Form form={form} layout="vertical">
          <Form.Item name="role" label="岗位" rules={[{ required: true }]}>
            <Select options={roles.map(r => ({ value: r.id, label: r.name }))} />
          </Form.Item>
          <Form.Item name="ruleType" label="规则类型" rules={[{ required: true }]}>
            <Select options={typeOptions} />
          </Form.Item>
          <Form.Item name="pricePerTon" label="吨价">
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="dailySalary" label="日薪">
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="hourlySalary" label="时薪">
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
