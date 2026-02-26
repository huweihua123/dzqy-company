import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { Button, DatePicker, Modal, Space, Table } from 'antd';
import dayjs from 'dayjs';
import api from '../services/api.js';

export default function OwnerBalancePage() {
  const [data, setData] = useState([]);
  const location = useLocation();
  const [receiptOpen, setReceiptOpen] = useState(false);
  const [receiptOwner, setReceiptOwner] = useState(null);
  const [receipts, setReceipts] = useState([]);
  const [receiptRange, setReceiptRange] = useState(null);
  const [ordersOpen, setOrdersOpen] = useState(false);
  const [ordersOwner, setOrdersOwner] = useState(null);
  const [orders, setOrders] = useState([]);
  const [orderRange, setOrderRange] = useState(null);

  const load = async () => {
    const res = await api.get('/owner-balances');
    setData(res.data);
  };

  const loadReceipts = async (owner, range) => {
    const params = { ownerId: owner.ownerId };
    if (range && range[0] && range[1]) {
      params.from = range[0].format('YYYY-MM-DD');
      params.to = range[1].format('YYYY-MM-DD');
    }
    const res = await api.get('/owner-receipts', { params });
    const rows = Array.isArray(res.data) ? res.data : [];
    rows.sort((a, b) => String(b.payDate || '').localeCompare(String(a.payDate || '')));
    setReceipts(rows);
  };

  const loadOrders = async (owner, range) => {
    const params = { ownerId: owner.ownerId };
    if (range && range[0] && range[1]) {
      params.from = range[0].format('YYYY-MM-DD');
      params.to = range[1].format('YYYY-MM-DD');
    }
    const res = await api.get('/orders', { params });
    const rows = Array.isArray(res.data) ? res.data : [];
    rows.sort((a, b) => String(b.orderDate || '').localeCompare(String(a.orderDate || '')));
    setOrders(rows);
  };

  const formatNumber = (value, digits = 2) => {
    if (value === null || value === undefined) return '';
    const num = Number(value);
    if (Number.isNaN(num)) return value;
    return num.toFixed(digits);
  };

  useEffect(() => {
    load();
  }, [location.pathname]);

  const columns = [
    { title: '货主', dataIndex: 'ownerName' },
    { title: '应收总额', dataIndex: 'totalReceivable' },
    { title: '已收总额', dataIndex: 'totalReceived' },
    { title: '欠款', dataIndex: 'totalOutstanding' },
    {
      title: '操作',
      render: (_, record) => (
        <>
          <Button
            size="small"
            onClick={async () => {
              setReceiptOwner(record);
              setReceiptRange(null);
              await loadReceipts(record, null);
              setReceiptOpen(true);
            }}
          >
            收款记录
          </Button>
          <Button
            size="small"
            style={{ marginLeft: 8 }}
            onClick={async () => {
              setOrdersOwner(record);
              setOrderRange(null);
              await loadOrders(record, null);
              setOrdersOpen(true);
            }}
          >
            查看订单
          </Button>
        </>
      )
    }
  ];

  const receiptColumns = [
    { title: '收款日期', dataIndex: 'payDate' },
    { title: '金额', dataIndex: 'amount' },
    { title: '备注', dataIndex: 'remark' }
  ];

  const orderColumns = [
    { title: '订单号', dataIndex: 'orderNo' },
    { title: '材质', dataIndex: ['material', 'name'] },
    { title: '规格', dataIndex: ['spec', 'name'] },
    { title: '数量', dataIndex: 'planPieceCount', render: (v) => formatNumber(v, 0) },
    { title: '重量(吨)', dataIndex: 'planGrossTonnage', render: (v) => formatNumber(v) },
    { title: '加工费(元)', dataIndex: 'totalFee', render: (v) => formatNumber(v, 2) },
    { title: '日期', dataIndex: 'orderDate' }
  ];

  return (
    <div>
      <div style={{ fontSize: 16, fontWeight: 600, marginBottom: 12 }}>货主对账</div>
      <Table rowKey="ownerId" columns={columns} dataSource={data} pagination={{ pageSize: 10 }} />
      <Modal
        title={receiptOwner ? `${receiptOwner.ownerName} - 收款记录` : '收款记录'}
        open={receiptOpen}
        onCancel={() => {
          setReceiptOpen(false);
          setReceiptOwner(null);
          setReceipts([]);
          setReceiptRange(null);
        }}
        footer={null}
        width={720}
      >
        <Space style={{ marginBottom: 12 }}>
          <DatePicker.RangePicker
            value={receiptRange}
            onChange={(v) => {
              setReceiptRange(v);
              if (receiptOwner) {
                loadReceipts(receiptOwner, v);
              }
            }}
          />
          <Button onClick={() => {
            setReceiptRange(null);
            if (receiptOwner) {
              loadReceipts(receiptOwner, null);
            }
          }}>清空</Button>
        </Space>
        <Table
          rowKey="id"
          columns={receiptColumns}
          dataSource={receipts}
          pagination={{ pageSize: 8 }}
        />
      </Modal>
      <Modal
        title={ordersOwner ? `${ordersOwner.ownerName} - 订单记录` : '订单记录'}
        open={ordersOpen}
        onCancel={() => {
          setOrdersOpen(false);
          setOrdersOwner(null);
          setOrders([]);
          setOrderRange(null);
        }}
        footer={null}
        width={900}
      >
        <Space style={{ marginBottom: 12 }}>
          <DatePicker.RangePicker
            value={orderRange}
            onChange={(v) => {
              setOrderRange(v);
              if (ordersOwner) {
                loadOrders(ordersOwner, v);
              }
            }}
          />
          <Button onClick={() => {
            setOrderRange(null);
            if (ordersOwner) {
              loadOrders(ordersOwner, null);
            }
          }}>清空</Button>
        </Space>
        <Table
          rowKey="id"
          columns={orderColumns}
          dataSource={orders}
          pagination={{ pageSize: 8 }}
        />
      </Modal>
    </div>
  );
}
