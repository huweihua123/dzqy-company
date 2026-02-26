import React, { useEffect, useState } from 'react';
import { Button, DatePicker, Form, Input, InputNumber, Modal, Select, Space, Table, message } from 'antd';
import dayjs from 'dayjs';
import api from '../services/api.js';

export default function SalaryPaymentsPage() {
  const [payments, setPayments] = useState([]);
  const [workers, setWorkers] = useState([]);
  const [open, setOpen] = useState(false);
  const [form] = Form.useForm();

  const load = async () => {
    const [pRes, wRes] = await Promise.all([
      api.get('/salary-payments'),
      api.get('/workers')
    ]);
    setPayments(pRes.data);
    setWorkers(wRes.data);
  };

  useEffect(() => {
    load();
  }, []);

  const columns = [
    { title: '工人', dataIndex: ['worker', 'name'] },
    { title: '日期', dataIndex: 'payDate' },
    { title: '金额', dataIndex: 'amount' },
    { title: '备注', dataIndex: 'remark' }
  ];

  const handleCreate = async () => {
    const values = await form.validateFields();
    const payload = {
      ...values,
      payDate: values.payDate.format('YYYY-MM-DD')
    };
    await api.post('/salary-payments', payload);
    message.success('工资已发放');
    setOpen(false);
    form.resetFields();
    load();
  };

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={() => setOpen(true)}>发放工资</Button>
      </Space>
      <Table rowKey="id" columns={columns} dataSource={payments} />

      <Modal title="工资发放" open={open} onCancel={() => setOpen(false)} onOk={handleCreate} okText="保存">
        <Form form={form} layout="vertical" initialValues={{ payDate: dayjs() }}>
          <Form.Item name="workerId" label="工人" rules={[{ required: true }]}>
            <Select options={workers.map(w => ({ value: w.id, label: w.name }))} />
          </Form.Item>
          <Form.Item name="payDate" label="发放日期" rules={[{ required: true }]}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="amount" label="金额" rules={[{ required: true }]}>
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
