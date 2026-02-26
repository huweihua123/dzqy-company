import React, { useEffect, useState } from 'react';
import { Button, Form, Input, Modal, Select, Space, Table, message, InputNumber } from 'antd';
import api from '../services/api.js';

const payTypeOptions = [
  { value: 'PIECE', label: '计件' },
  { value: 'DAILY', label: '按天' },
  { value: 'YEARLY', label: '按年' }
];

const statusOptions = [
  { value: 'ACTIVE', label: '在岗' },
  { value: 'INACTIVE', label: '离职' }
];

export default function WorkersPage() {
  const [workers, setWorkers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [groups, setGroups] = useState([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [batchGroupId, setBatchGroupId] = useState(null);
  const [form] = Form.useForm();

  const load = async () => {
    const [wRes, rRes, gRes] = await Promise.all([
      api.get('/workers'),
      api.get('/worker-roles'),
      api.get('/worker-groups')
    ]);
    setWorkers(wRes.data);
    setRoles(rRes.data);
    setGroups(gRes.data);
  };

  useEffect(() => {
    load();
  }, []);

  const columns = [
    { title: '姓名', dataIndex: 'name' },
    { title: '岗位', dataIndex: ['role', 'name'] },
    { title: '小组', dataIndex: ['group', 'name'] },
    { title: '电话', dataIndex: 'phone' },
    { title: '状态', dataIndex: 'status' },
    { title: '计薪方式', dataIndex: 'payType', render: (v) => ({ PIECE: '计件', DAILY: '按天', YEARLY: '按年' }[v] || v) },
    { title: '吨价', dataIndex: 'tonPrice' },
    { title: '日薪', dataIndex: 'dailySalary' },
    { title: '年薪', dataIndex: 'yearlySalary' },
    {
      title: '操作',
      render: (_, record) => (
        <Space>
          <Button
            size="small"
            onClick={() => {
              setEditing(record);
              form.setFieldsValue({
                name: record.name,
                role: record.role?.id,
                groupId: record.group?.id,
                payType: record.payType,
                status: record.status,
                phone: record.phone,
                tonPrice: record.tonPrice,
                dailySalary: record.dailySalary,
                yearlySalary: record.yearlySalary,
                remark: record.remark
              });
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
                title: '确定删除该员工吗？',
                onOk: async () => {
                  await api.delete(`/workers/${record.id}`);
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

  const handleCreate = async () => {
    const values = await form.validateFields();
    const payload = { ...values, roleId: values.role, groupId: values.groupId };
    if (editing) {
      await api.put(`/workers/${editing.id}`, payload);
      message.success('员工已更新');
    } else {
      await api.post('/workers', payload);
      message.success('员工已创建');
    }
    setOpen(false);
    setEditing(null);
    form.resetFields();
    load();
  };

  const handleBatchGroup = async () => {
    if (!batchGroupId || selectedRowKeys.length === 0) {
      message.error('请选择小组和员工');
      return;
    }
    await Promise.all(
      selectedRowKeys.map((id) => api.put(`/workers/${id}`, {
        ...workers.find(w => w.id === id),
        roleId: workers.find(w => w.id === id)?.role?.id,
        groupId: batchGroupId,
        payType: workers.find(w => w.id === id)?.payType
      }))
    );
    message.success('批量分组已更新');
    setSelectedRowKeys([]);
    setBatchGroupId(null);
    load();
  };
  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={() => { setEditing(null); setOpen(true); }}>新增员工</Button>
        <Select
          placeholder="批量设置小组"
          style={{ width: 200 }}
          value={batchGroupId}
          onChange={(v) => setBatchGroupId(v)}
          options={groups.map(g => ({ value: g.id, label: g.name }))}
        />
        <Button onClick={handleBatchGroup}>批量分组</Button>
      </Space>
      <Table
        rowKey="id"
        columns={columns}
        dataSource={workers}
        pagination={{ pageSize: 10 }}
        rowSelection={{
          selectedRowKeys,
          onChange: (keys) => setSelectedRowKeys(keys)
        }}
      />

      <Modal
        title={editing ? '编辑员工' : '新增员工'}
        open={open}
        onCancel={() => { setOpen(false); setEditing(null); }}
        onOk={handleCreate}
        okText="保存"
      >
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="姓名" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="role" label="岗位" rules={[{ required: true }]}>
            <Select options={roles.map(r => ({ value: r.id, label: r.name }))} />
          </Form.Item>
          <Form.Item name="groupId" label="小组">
            <Select allowClear options={groups.map(g => ({ value: g.id, label: g.name }))} />
          </Form.Item>
          <Form.Item name="payType" label="计薪方式" rules={[{ required: true }]}>
            <Select options={payTypeOptions} />
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select options={statusOptions} />
          </Form.Item>
          <Form.Item name="phone" label="电话">
            <Input />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input />
          </Form.Item>
          <Form.Item shouldUpdate noStyle>
            {({ getFieldValue }) => {
              const payType = getFieldValue('payType');
              return (
                <>
                  {payType === 'PIECE' && (
                    <Form.Item name="tonPrice" label="吨价" rules={[{ required: true }]}>
                      <InputNumber style={{ width: '100%' }} addonAfter="元/吨" />
                    </Form.Item>
                  )}
                  {payType === 'DAILY' && (
                    <Form.Item name="dailySalary" label="日薪" rules={[{ required: true }]}>
                      <InputNumber style={{ width: '100%' }} addonAfter="元/天" />
                    </Form.Item>
                  )}
                  {payType === 'YEARLY' && (
                    <Form.Item name="yearlySalary" label="年薪" rules={[{ required: true }]}>
                      <InputNumber style={{ width: '100%' }} addonAfter="元/年" />
                    </Form.Item>
                  )}
                </>
              );
            }}
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
