import React, { useEffect, useState } from 'react';
import { DatePicker, Space, Table, Button } from 'antd';
import dayjs from 'dayjs';
import api from '../services/api.js';

const { RangePicker } = DatePicker;

export default function SalaryPrintPage() {
  const [data, setData] = useState([]);
  const [range, setRange] = useState([dayjs().startOf('month'), dayjs().endOf('month')]);

  const load = async (r) => {
    const params = r
      ? { from: r[0].format('YYYY-MM-DD'), to: r[1].format('YYYY-MM-DD') }
      : {};
    const res = await api.get('/salary/calc', { params });
    setData(res.data);
  };

  useEffect(() => {
    load(range);
  }, []);

  const columns = [
    { title: '姓名', dataIndex: 'workerName' },
    { title: '岗位', dataIndex: 'role' },
    { title: '计薪方式', dataIndex: 'payType', render: (v) => ({ PIECE: '计件', DAILY: '按天', YEARLY: '按年' }[v] || v) },
    { title: '应发工资', dataIndex: 'earnedAmount' },
    { title: '已发工资', dataIndex: 'paidAmount' },
    { title: '欠款', dataIndex: 'owedAmount' }
  ];

  return (
    <div style={{ background: '#fff', padding: 24 }}>
      <div style={{ marginBottom: 12, fontSize: 20, fontWeight: 700 }}>锻造记账系统 - 工资单</div>
      <div style={{ marginBottom: 12, color: '#666' }}>
        统计区间：{range[0].format('YYYY-MM-DD')} 至 {range[1].format('YYYY-MM-DD')}
      </div>
      <Space style={{ marginBottom: 16 }}>
        <RangePicker
          value={range}
          onChange={(v) => {
            setRange(v);
            if (v && v[0] && v[1]) {
              load(v);
            }
          }}
        />
        <Button onClick={() => window.print()}>打印</Button>
      </Space>
      <Table rowKey="workerId" pagination={false} columns={columns} dataSource={data} />
    </div>
  );
}
