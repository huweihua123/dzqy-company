import React, { useEffect, useState } from 'react';
import { Button, DatePicker, Form, Input, InputNumber, Modal, Select, Space, Table, message } from 'antd';
import dayjs from 'dayjs';
import api from '../services/api.js';

export default function OwnerReceiptsPage() {
  const [receipts, setReceipts] = useState([]);
  const [owners, setOwners] = useState([]);
  const [ownerFilter, setOwnerFilter] = useState(undefined);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();

  const load = async (filter) => {
    const params = {};
    if (filter?.ownerId) params.ownerId = filter.ownerId;
    const [rRes, oRes] = await Promise.all([
      api.get('/owner-receipts', { params }),
      api.get('/owners')
    ]);
    setReceipts(rRes.data);
    setOwners(oRes.data);
  };

  useEffect(() => {
    load();
  }, []);

  const columns = [
    { title: '货主', dataIndex: ['owner', 'name'] },
    { title: '收款日期', dataIndex: 'payDate' },
    { title: '金额', dataIndex: 'amount' },
    { title: '备注', dataIndex: 'remark' },
    {
      title: '操作',
      render: (_, record) => (
        <Space>
          <Button size="small" onClick={() => {
            setEditing(record);
            form.setFieldsValue({
              ownerId: record.owner?.id,
              payDate: dayjs(record.payDate),
              amount: record.amount,
              remark: record.remark
            });
            setOpen(true);
          }}>编辑</Button>
          <Button size="small" danger onClick={async () => {
            await api.delete(`/owner-receipts/${record.id}`);
            message.success('已作废');
            load({ ownerId: ownerFilter });
          }}>作废</Button>
        </Space>
      )
    }
  ];

  const handleCreate = async () => {
    const values = await form.validateFields();
    const payload = {
      ...values,
      payDate: values.payDate.format('YYYY-MM-DD')
    };
    if (editing) {
      await api.put(`/owner-receipts/${editing.id}`, payload);
      message.success('收款已更新');
    } else {
      await api.post('/owner-receipts', payload);
      message.success('收款已登记');
    }
    setOpen(false);
    setEditing(null);
    form.resetFields();
    load({ ownerId: ownerFilter });
  };

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <div style={{ fontSize: 16, fontWeight: 600, marginRight: 8 }}>货主收款</div>
        <Button type="primary" onClick={() => { setEditing(null); setOpen(true); }}>登记收款</Button>
        <Select
          allowClear
          placeholder="货主筛选"
          style={{ width: 200 }}
          value={ownerFilter}
          onChange={(value) => {
            setOwnerFilter(value);
            load({ ownerId: value });
          }}
          options={owners.map(o => ({ value: o.id, label: o.name }))}
        />
        <Button onClick={() => {
          setOwnerFilter(undefined);
          load({});
        }}>清空</Button>
      </Space>
      <Table rowKey="id" columns={columns} dataSource={receipts} />

      <Modal
        title={editing ? '编辑收款' : '登记收款'}
        open={open}
        onCancel={() => { setOpen(false); setEditing(null); }}
        onOk={handleCreate}
        okText="保存"
      >
        <Form form={form} layout="vertical" initialValues={{ payDate: dayjs() }}>
          <Form.Item name="ownerId" label="货主" rules={[{ required: true }]}> 
            <Select options={owners.map(o => ({ value: o.id, label: o.name }))} />
          </Form.Item>
          <Form.Item name="payDate" label="收款日期" rules={[{ required: true }]}> 
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
