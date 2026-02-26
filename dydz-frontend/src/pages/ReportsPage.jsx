import React, { useEffect, useRef, useState } from 'react';
import { DatePicker, Space, Card, Button, Select } from 'antd';
import dayjs from 'dayjs';
import * as echarts from 'echarts';
import api from '../services/api.js';

const { RangePicker } = DatePicker;

export default function ReportsPage() {
  const [range, setRange] = useState([dayjs().startOf('month'), dayjs().endOf('month')]);
  const productionRef = useRef(null);
  const ordersRef = useRef(null);
  const salaryRef = useRef(null);
  const ownersRef = useRef(null);
  const [prodData, setProdData] = useState([]);
  const [orderData, setOrderData] = useState([]);
  const [salaryData, setSalaryData] = useState([]);
  const [ownerData, setOwnerData] = useState([]);
  const [granularity, setGranularity] = useState('day');

  const load = async (r) => {
    const params = { from: r[0].format('YYYY-MM-DD'), to: r[1].format('YYYY-MM-DD'), granularity };
    const [prodRes, orderRes, salaryRes, ownerRes] = await Promise.all([
      api.get('/reports/production', { params }),
      api.get('/reports/orders', { params }),
      api.get('/reports/salary', { params }),
      api.get('/reports/owners', { params })
    ]);
    setProdData(prodRes.data);
    setOrderData(orderRes.data);
    setSalaryData(salaryRes.data);
    setOwnerData(ownerRes.data);

    const prodChart = echarts.init(productionRef.current);
    prodChart.setOption({
      title: { text: '日产量(净重吨)' },
      xAxis: { type: 'category', data: prodRes.data.map(d => d.date) },
      yAxis: { type: 'value' },
      series: [{ data: prodRes.data.map(d => d.value), type: 'line', smooth: true }]
    });

    const orderChart = echarts.init(ordersRef.current);
    orderChart.setOption({
      title: { text: '订单加工费(元)' },
      xAxis: { type: 'category', data: orderRes.data.map(d => d.date) },
      yAxis: { type: 'value' },
      series: [{ data: orderRes.data.map(d => d.value), type: 'bar' }]
    });

    const salaryChart = echarts.init(salaryRef.current);
    salaryChart.setOption({
      title: { text: '工资应发(元)' },
      xAxis: { type: 'category', data: salaryRes.data.map(d => d.workerName) },
      yAxis: { type: 'value' },
      series: [{ data: salaryRes.data.map(d => d.earnedAmount), type: 'bar' }]
    });

    const ownerChart = echarts.init(ownersRef.current);
    ownerChart.setOption({
      title: { text: '按货主统计(元)' },
      xAxis: { type: 'category', data: ownerRes.data.map(d => d.ownerName) },
      yAxis: { type: 'value' },
      series: [{ data: ownerRes.data.map(d => d.totalFee), type: 'bar' }]
    });
  };

  useEffect(() => {
    load(range);
  }, []);

  const downloadXlsx = (path) => {
    const params = `from=${range[0].format('YYYY-MM-DD')}&to=${range[1].format('YYYY-MM-DD')}&granularity=${granularity}`;
    window.open(`http://localhost:10001${path}?${params}`);
  };

  return (
    <div style={{ display: 'grid', gap: 16 }}>
      <Space>
        <RangePicker
          value={range}
          onChange={(v) => {
            setRange(v);
            if (v && v[0] && v[1]) {
              load(v);
            }
          }}
        />
        <Select
          value={granularity}
          onChange={(v) => {
            setGranularity(v);
            load(range);
          }}
          options={[
            { value: 'day', label: '按日' },
            { value: 'week', label: '按周' },
            { value: 'month', label: '按月' }
          ]}
        />
      </Space>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 12 }}>
        <Card>
          <div style={{ fontSize: 12, color: '#888' }}>总产量(净重吨)</div>
          <div style={{ fontSize: 20, fontWeight: 600 }}>
            {prodData.reduce((s, d) => s + Number(d.value || 0), 0).toFixed(2)}
          </div>
        </Card>
        <Card>
          <div style={{ fontSize: 12, color: '#888' }}>总加工费(元)</div>
          <div style={{ fontSize: 20, fontWeight: 600 }}>
            {orderData.reduce((s, d) => s + Number(d.value || 0), 0).toFixed(2)}
          </div>
        </Card>
        <Card>
          <div style={{ fontSize: 12, color: '#888' }}>应发工资(元)</div>
          <div style={{ fontSize: 20, fontWeight: 600 }}>
            {salaryData.reduce((s, d) => s + Number(d.earnedAmount || 0), 0).toFixed(2)}
          </div>
        </Card>
      </div>
      <Card>
        <Space style={{ marginBottom: 8 }}>
          <Button onClick={() => downloadXlsx('/reports/production.xlsx')}>导出Excel</Button>
        </Space>
        <div ref={productionRef} style={{ height: 320 }} />
      </Card>
      <Card>
        <Space style={{ marginBottom: 8 }}>
          <Button onClick={() => downloadXlsx('/reports/orders.xlsx')}>导出Excel</Button>
        </Space>
        <div ref={ordersRef} style={{ height: 320 }} />
      </Card>
      <Card>
        <Space style={{ marginBottom: 8 }}>
          <Button onClick={() => downloadXlsx('/reports/salary.xlsx')}>导出Excel</Button>
        </Space>
        <div ref={salaryRef} style={{ height: 320 }} />
      </Card>
      <Card>
        <Space style={{ marginBottom: 8 }}>
          <Button onClick={() => downloadXlsx('/reports/owners.xlsx')}>导出Excel</Button>
        </Space>
        <div ref={ownersRef} style={{ height: 320 }} />
      </Card>
    </div>
  );
}
