import React, { useEffect, useState } from 'react';
import { Button, DatePicker, Form, InputNumber, Modal, Select, Space, Table, message } from 'antd';
import dayjs from 'dayjs';
import api from '../services/api.js';

export default function ProductionPage() {
  const [records, setRecords] = useState([]);
  const [orders, setOrders] = useState([]);
  const [orderItems, setOrderItems] = useState([]);
  const [workers, setWorkers] = useState([]);
  const [open, setOpen] = useState(false);
  const [form] = Form.useForm();

  const load = async () => {
    const [rRes, oRes, wRes] = await Promise.all([
      api.get('/production-records'),
      api.get('/orders'),
      api.get('/workers')
    ]);
    setRecords(rRes.data);
    setOrders(oRes.data);
    setWorkers(wRes.data);
  };

  useEffect(() => {
    load();
  }, []);

  const columns = [
    { title: '工人', dataIndex: ['worker', 'name'] },
    { title: '订单号', dataIndex: ['order', 'orderNo'] },
    { title: '明细', dataIndex: ['orderItem', 'productName'] },
    { title: '日期', dataIndex: 'workDate' },
    { title: '毛重(吨)', dataIndex: 'grossTonnage' },
    { title: '净重(吨)', dataIndex: 'netTonnage' },
    { title: '件数', dataIndex: 'pieceCount' }
  ];

  const handleCreate = async () => {
    const values = await form.validateFields();
    const payload = {
      ...values,
      workDate: values.workDate.format('YYYY-MM-DD')
    };
    await api.post('/production-records', payload);
    message.success('产量已录入');
    setOpen(false);
    form.resetFields();
    load();
  };

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={() => setOpen(true)}>录入产量</Button>
      </Space>
      <Table rowKey="id" columns={columns} dataSource={records} />

      <Modal title="录入产量" open={open} onCancel={() => setOpen(false)} onOk={handleCreate} okText="保存">
        <Form form={form} layout="vertical" initialValues={{ workDate: dayjs() }}>
          <Form.Item name="orderId" label="订单" rules={[{ required: true }]}>
            <Select
              options={orders.map(o => ({ value: o.id, label: o.orderNo }))}
              onChange={async (orderId) => {
                const res = await api.get(`/orders/${orderId}`);
                setOrderItems(res.data.items || []);
                form.setFieldsValue({ orderItemId: undefined });
              }}
            />
          </Form.Item>
          <Form.Item name="orderItemId" label="订单明细" rules={[{ required: true }]}>
            <Select options={orderItems.map(i => ({ value: i.id, label: i.productName }))} />
          </Form.Item>
          <Form.Item name="workerId" label="工人" rules={[{ required: true }]}>
            <Select options={workers.map(w => ({ value: w.id, label: w.name }))} />
          </Form.Item>
          <Form.Item name="workDate" label="日期" rules={[{ required: true }]}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="pieceCount" label="件数" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
