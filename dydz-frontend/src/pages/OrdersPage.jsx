import React, { useEffect, useState } from 'react';
import { Button, Form, Input, InputNumber, Modal, Select, Space, Table, message, DatePicker, Progress, Upload, Alert } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import api from '../services/api.js';

export default function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [owners, setOwners] = useState([]);
  const [materials, setMaterials] = useState([]);
  const [specs, setSpecs] = useState([]);
  const [statusFilter, setStatusFilter] = useState(undefined);
  const [ownerFilter, setOwnerFilter] = useState(undefined);
  const [orderNoFilter, setOrderNoFilter] = useState('');
  const [dateFilter, setDateFilter] = useState(null);
  const [open, setOpen] = useState(false);
  const [detailOpen, setDetailOpen] = useState(false);
  const [detail, setDetail] = useState(null);
  const [importErrors, setImportErrors] = useState([]);
  const [form] = Form.useForm();

  const load = async (filter) => {
    const params = {};
    if (filter?.productionStatus) params.productionStatus = filter.productionStatus;
    if (filter?.ownerId) params.ownerId = filter.ownerId;
    if (filter?.orderNo) params.orderNo = filter.orderNo;
    if (filter?.from && filter?.to) { params.from = filter.from; params.to = filter.to; }
    const [oRes, ownerRes, materialRes, specRes] = await Promise.all([
      api.get('/orders', { params }),
      api.get('/owners'),
      api.get('/materials'),
      api.get('/specs')
    ]);
    setOrders(oRes.data);
    setOwners(ownerRes.data);
    setMaterials(materialRes.data);
    setSpecs(specRes.data);
  };

  useEffect(() => {
    load();
  }, []);

  const formatNumber = (value, digits = 2) => {
    if (value === null || value === undefined) return '';
    const num = Number(value);
    if (Number.isNaN(num)) return value;
    return num.toFixed(digits);
  };

  const openDetail = async (orderId) => {
    const res = await api.get(`/orders/${orderId}`);
    setDetail(res.data);
    setDetailOpen(true);
  };

  const columns = [
    { title: '订单号', dataIndex: 'orderNo' },
    { title: '货主', dataIndex: ['owner', 'name'] },
    { title: '计划数量', dataIndex: 'planPieceCount', render: (v) => formatNumber(v) },
    { title: '计划毛重', dataIndex: 'planGrossTonnage', render: (v) => formatNumber(v) },
    { title: '总加工费', dataIndex: 'totalFee', render: (v) => formatNumber(v, 2) },
    { title: '日期', dataIndex: 'orderDate' },
    { title: '操作', render: (_, record) => <Button size="small" onClick={() => openDetail(record.id)}>查看详情</Button> }
  ];

  const handleCreate = async () => {
    const values = await form.validateFields();
    const payload = {
      ...values,
      orderDate: values.orderDate.format('YYYY-MM-DD'),
      items: values.items
    };
    await api.post('/orders', payload);
    message.success('订单已创建');
    setOpen(false);
    form.resetFields();
    load();
  };

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" onClick={() => setOpen(true)}>新建订单</Button>
        <Upload
          showUploadList={false}
          accept=".xlsx,.xls"
          customRequest={async ({ file, onSuccess, onError }) => {
            const formData = new FormData();
            formData.append('file', file);
            try {
              const preview = await api.post('/orders/import/preview', formData, { headers: { 'Content-Type': 'multipart/form-data' } });
              if (preview.data && preview.data.length > 0) {
                setImportErrors(preview.data);
                message.error('导入存在错误，请先修正');
                onError && onError(new Error('preview error'));
                return;
              }
              await api.post('/orders/import', formData, { headers: { 'Content-Type': 'multipart/form-data' } });
              setImportErrors([]);
              message.success('导入成功');
              load();
              onSuccess && onSuccess('ok');
            } catch (e) {
              message.error('导入失败');
              onError && onError(e);
            }
          }}
        >
          <Button icon={<UploadOutlined />}>批量导入</Button>
        </Upload>
        <Button onClick={() => window.open('http://localhost:10001/orders/template.xlsx')}>下载模板</Button>
        <Input
          placeholder="订单号搜索"
          style={{ width: 160 }}
          value={orderNoFilter}
          onChange={(e) => setOrderNoFilter(e.target.value)}
          onPressEnter={() => {
            load({
              productionStatus: statusFilter,
              ownerId: ownerFilter,
              orderNo: orderNoFilter,
              from: dateFilter?.[0]?.format('YYYY-MM-DD'),
              to: dateFilter?.[1]?.format('YYYY-MM-DD')
            });
          }}
        />
        <Select
          allowClear
          placeholder="货主筛选"
          style={{ width: 160 }}
          value={ownerFilter}
          onChange={(value) => setOwnerFilter(value)}
          options={owners.map(o => ({ value: o.id, label: o.name }))}
        />
        <DatePicker.RangePicker
          value={dateFilter}
          onChange={(v) => setDateFilter(v)}
        />
        <Select
          allowClear
          placeholder="生产状态筛选"
          style={{ width: 180 }}
          value={statusFilter}
          onChange={(value) => {
            setStatusFilter(value);
            load({ productionStatus: value, ownerId: ownerFilter, orderNo: orderNoFilter, from: dateFilter?.[0]?.format('YYYY-MM-DD'), to: dateFilter?.[1]?.format('YYYY-MM-DD') });
          }}
          options={[
            { value: 'NOT_STARTED', label: '未开始' },
            { value: 'IN_PROGRESS', label: '进行中' },
            { value: 'COMPLETED', label: '已完成' }
          ]}
        />
        <Button onClick={() => {
          load({
            productionStatus: statusFilter,
            ownerId: ownerFilter,
            orderNo: orderNoFilter,
            from: dateFilter?.[0]?.format('YYYY-MM-DD'),
            to: dateFilter?.[1]?.format('YYYY-MM-DD')
          });
        }}>筛选</Button>
        <Button onClick={() => {
          setStatusFilter(undefined);
          setOwnerFilter(undefined);
          setOrderNoFilter('');
          setDateFilter(null);
          load({});
        }}>清空</Button>
      </Space>
      {importErrors.length > 0 && (
        <Alert
          type="error"
          showIcon
          message="导入错误"
          description={
            <div style={{ maxHeight: 120, overflow: 'auto' }}>
              {importErrors.map((e, idx) => (
                <div key={idx}>第 {e.row} 行：{e.message}</div>
              ))}
            </div>
          }
          style={{ marginBottom: 12 }}
        />
      )}
      <Table rowKey="id" columns={columns} dataSource={orders} pagination={{ pageSize: 10 }} />

      <Modal
        title="新建订单"
        open={open}
        onCancel={() => setOpen(false)}
        onOk={handleCreate}
        okText="保存"
        width={920}
      >
        <Form form={form} layout="vertical" initialValues={{ orderDate: dayjs() }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
            <Form.Item name="orderNo" label="订单号" rules={[{ required: true }]}>
              <Input />
            </Form.Item>
            <Form.Item name="ownerId" label="货主" rules={[{ required: true }]}>
              <Select options={owners.map(o => ({ value: o.id, label: o.name }))} />
            </Form.Item>
            <Form.Item name="orderDate" label="订单日期" rules={[{ required: true }]}>
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>
          </div>

          <Form.List name="items" initialValue={[{}]}>
            {(fields, { add, remove }) => (
              <div style={{ display: 'grid', gap: 12 }}>
                {fields.map((field) => (
                  <div key={field.key} style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1fr 1fr 1fr auto', gap: 12, padding: 12, border: '1px solid #eee', borderRadius: 8 }}>
                    <Form.Item {...field} name={[field.name, 'productName']} label="产品名称" rules={[{ required: true }]}>
                      <Input placeholder="例如：法兰、轴类、齿轮坯" />
                    </Form.Item>
                    <Form.Item {...field} name={[field.name, 'materialId']} label="材质">
                      <Select allowClear options={materials.map(m => ({ value: m.id, label: m.name }))} />
                    </Form.Item>
                    <Form.Item {...field} name={[field.name, 'specId']} label="规格">
                      <Select allowClear options={specs.map(s => ({ value: s.id, label: s.name }))} />
                    </Form.Item>
                    <Form.Item {...field} name={[field.name, 'pieceCount']} label="件数" rules={[{ required: true }]}>
                      <InputNumber style={{ width: '100%' }} placeholder="数量" />
                    </Form.Item>
                    <Form.Item {...field} name={[field.name, 'pieceWeight']} label="单件下料重量" rules={[{ required: true }]}>
                      <InputNumber style={{ width: '100%' }} placeholder="吨/件" />
                    </Form.Item>
                    {fields.length > 1 && (
                      <div style={{ display: 'flex', alignItems: 'flex-end' }}>
                        <Button danger onClick={() => remove(field.name)}>删除</Button>
                      </div>
                    )}
                  </div>
                ))}
                <Button onClick={() => add()} type="dashed">新增明细</Button>
              </div>
            )}
          </Form.List>
        </Form>
      </Modal>

      <Modal
        title="订单详情"
        open={detailOpen}
        onCancel={() => setDetailOpen(false)}
        footer={null}
        width={860}
      >
        {detail && (
          <div style={{ display: 'grid', gap: 16 }}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
              <div style={{ padding: 12, border: '1px solid #f0f0f0', borderRadius: 8 }}>
                <div style={{ fontSize: 12, color: '#888' }}>订单号</div>
                <div style={{ fontSize: 16, fontWeight: 600 }}>{detail.order.orderNo}</div>
              </div>
              <div style={{ padding: 12, border: '1px solid #f0f0f0', borderRadius: 8 }}>
                <div style={{ fontSize: 12, color: '#888' }}>货主</div>
                <div style={{ fontSize: 16, fontWeight: 600 }}>{detail.order.owner?.name}</div>
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 12 }}>
              <div style={{ padding: 12, border: '1px solid #f0f0f0', borderRadius: 8 }}>
                <div style={{ fontSize: 12, color: '#888' }}>计划数量</div>
                <div style={{ fontSize: 16, fontWeight: 600 }}>{formatNumber(detail.order.planPieceCount)}</div>
              </div>
              <div style={{ padding: 12, border: '1px solid #f0f0f0', borderRadius: 8 }}>
                <div style={{ fontSize: 12, color: '#888' }}>已完成数量</div>
                <div style={{ fontSize: 16, fontWeight: 600 }}>{formatNumber(detail.order.donePieceCount)}</div>
              </div>
              <div style={{ padding: 12, border: '1px solid #f0f0f0', borderRadius: 8 }}>
                <div style={{ fontSize: 12, color: '#888' }}>完成进度</div>
                <Progress
                  percent={(() => {
                    const plan = Number(detail.order.planPieceCount || 0);
                    const done = Number(detail.order.donePieceCount || 0);
                    return plan > 0 ? Math.min(100, (done / plan) * 100) : 0;
                  })()}
                  size="small"
                />
              </div>
              <div style={{ padding: 12, border: '1px solid #f0f0f0', borderRadius: 8 }}>
                <div style={{ fontSize: 12, color: '#888' }}>计划毛重(吨)</div>
                <div style={{ fontSize: 16, fontWeight: 600 }}>{formatNumber(detail.order.planGrossTonnage)}</div>
              </div>
              <div style={{ padding: 12, border: '1px solid #f0f0f0', borderRadius: 8 }}>
                <div style={{ fontSize: 12, color: '#888' }}>已完成毛重(吨)</div>
                <div style={{ fontSize: 16, fontWeight: 600 }}>{formatNumber(detail.order.doneGrossTonnage)}</div>
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 12 }}>
              <div style={{ padding: 12, border: '1px solid #f0f0f0', borderRadius: 8 }}>
                <div style={{ fontSize: 12, color: '#888' }}>单价(元/吨)</div>
                <div style={{ fontSize: 16, fontWeight: 600 }}>{formatNumber(detail.order.owner?.unitPrice, 2)}</div>
              </div>
              <div style={{ padding: 12, border: '1px solid #f0f0f0', borderRadius: 8 }}>
                <div style={{ fontSize: 12, color: '#888' }}>总加工费(元)</div>
                <div style={{ fontSize: 16, fontWeight: 600 }}>{formatNumber(detail.order.totalFee, 2)}</div>
              </div>
              <div style={{ padding: 12, border: '1px solid #f0f0f0', borderRadius: 8 }}>
                <div style={{ fontSize: 12, color: '#888' }}>生产状态</div>
                <div style={{ fontSize: 16, fontWeight: 600 }}>
                  {{ NOT_STARTED: '未开始', IN_PROGRESS: '进行中', COMPLETED: '已完成' }[detail.order.productionStatus] || detail.order.productionStatus}
                </div>
              </div>
            </div>

            <div style={{ fontWeight: 600 }}>订单明细</div>
            <Table
              rowKey="id"
              pagination={false}
              dataSource={detail.items.map((item) => ({
                ...item,
                materialName: item.material?.name || '-',
                specName: item.spec?.name || '-',
                unitPrice: detail.order.owner?.unitPrice,
                amount: item.amount ?? null
              }))}
              columns={[
                { title: '材质', dataIndex: 'materialName' },
                { title: '规格', dataIndex: 'specName' },
                { title: '下料重量', dataIndex: 'pieceWeight', render: (v) => formatNumber(v) },
                { title: '数量', dataIndex: 'pieceCount', render: (v) => formatNumber(v) },
                { title: '总重', dataIndex: 'grossTonnage', render: (v) => formatNumber(v) },
                { title: '单价', dataIndex: 'unitPrice', render: (v) => formatNumber(v, 2) },
                { title: '金额', dataIndex: 'amount', render: (v) => formatNumber(v, 2) }
              ]}
            />
          </div>
        )}
      </Modal>
    </div>
  );
}
