import React, { useEffect, useState } from 'react';
import { Button, DatePicker, Space, Table, message } from 'antd';
import dayjs from 'dayjs';
import api from '../services/api.js';

const { RangePicker } = DatePicker;

export default function SalaryCalcPage() {
  const [data, setData] = useState([]);
  const [selectedKeys, setSelectedKeys] = useState([]);
  const [range, setRange] = useState([dayjs().startOf('month'), dayjs().endOf('month')]);
  const [payDate, setPayDate] = useState(dayjs());

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

  const handleBatchPay = async () => {
    const items = data.filter(d => selectedKeys.includes(d.workerId))
      .filter(d => Number(d.owedAmount) > 0)
      .map(d => ({
        workerId: d.workerId,
        payDate: payDate.format('YYYY-MM-DD'),
        amount: d.owedAmount,
        remark: '批量发放'
      }));
    if (items.length === 0) {
      message.warning('请选择有欠款的员工');
      return;
    }
    await api.post('/salary/batch-pay', { items });
    message.success('已批量发放');
    setSelectedKeys([]);
    load(range);
  };

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
        <DatePicker value={payDate} onChange={setPayDate} />
        <Button onClick={() => window.open(`http://localhost:10001/salary/calc.xlsx?from=${range[0].format('YYYY-MM-DD')}&to=${range[1].format('YYYY-MM-DD')}`)}>导出Excel</Button>
        <Button type="primary" onClick={handleBatchPay}>批量发放欠款</Button>
      </Space>
      <Table
        rowKey="workerId"
        columns={columns}
        dataSource={data}
        rowSelection={{ selectedRowKeys: selectedKeys, onChange: setSelectedKeys }}
      />
    </div>
  );
}
