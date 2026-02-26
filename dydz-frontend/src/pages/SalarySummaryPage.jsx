import React, { useEffect, useState } from 'react';
import { DatePicker, Space, Table } from 'antd';
import dayjs from 'dayjs';
import api from '../services/api.js';

const { RangePicker } = DatePicker;

export default function SalarySummaryPage() {
  const [data, setData] = useState([]);
  const [range, setRange] = useState([dayjs().startOf('month'), dayjs().endOf('month')]);

  const load = async (r) => {
    const params = r
      ? { from: r[0].format('YYYY-MM-DD'), to: r[1].format('YYYY-MM-DD') }
      : {};
    const res = await api.get('/salary-summary', { params });
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
    <div>
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
      </Space>
      <Table rowKey="workerId" columns={columns} dataSource={data} />
    </div>
  );
}
