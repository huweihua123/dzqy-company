import React, { useEffect, useRef, useState } from 'react';
import { Button, Calendar, DatePicker, Input, Popover, Select, Space, Table, Tag, message } from 'antd';
import dayjs from 'dayjs';
import api from '../services/api.js';

const statusOptions = [
  { value: 'PRESENT', label: '出勤' },
  { value: 'LEAVE', label: '请假' },
  { value: 'FACTORY_HOLIDAY', label: '工厂放假' }
];

export default function AttendancePage() {
  const [selectedDate, setSelectedDate] = useState(dayjs());
  const [groups, setGroups] = useState([]);
  const [groupId, setGroupId] = useState(null);
  const [rows, setRows] = useState([]);
  const [batchRange, setBatchRange] = useState(null);
  const remarkTimersRef = useRef(new Map());

  const loadGroups = async () => {
    const res = await api.get('/worker-groups');
    setGroups(res.data);
    if (res.data.length > 0 && !groupId) {
      setGroupId(res.data[0].id);
    }
  };

  const loadRows = async (date, gid) => {
    if (!gid) {
      setRows([]);
      return;
    }
    const [wRes, aRes] = await Promise.all([
      api.get('/workers', { params: { groupId: gid } }),
      api.get('/attendance', { params: { date: date.format('YYYY-MM-DD'), groupId: gid } })
    ]);
    const attendanceMap = new Map(aRes.data.map(a => [a.worker.id, a]));
    const newRows = wRes.data.map(w => {
      const a = attendanceMap.get(w.id);
      return {
        worker: w,
        attendanceId: a?.id || null,
        status: a?.status || 'PRESENT',
        remark: a?.remark || ''
      };
    });
    setRows(newRows);
  };

  useEffect(() => {
    loadGroups();
  }, []);

  useEffect(() => {
    if (groupId) {
      loadRows(selectedDate, groupId);
    }
  }, [selectedDate, groupId]);

  const updateRow = (index, patch) => {
    setRows(prev => {
      const next = [...prev];
      next[index] = { ...next[index], ...patch };
      return next;
    });
  };

  const saveRow = async (row) => {
    const payload = {
      workerId: row.worker.id,
      workDate: selectedDate.format('YYYY-MM-DD'),
      status: row.status,
      remark: row.remark
    };
    if (row.attendanceId) {
      await api.put(`/attendance/${row.attendanceId}`, payload);
    } else {
      const res = await api.post('/attendance', payload);
      row.attendanceId = res.data.id;
    }
    message.success('已保存');
  };

  const handleBatch = async (status) => {
    if (!groupId) return;
    await api.post('/attendance/batch-present', {
      workDate: selectedDate.format('YYYY-MM-DD'),
      groupId,
      status
    });
    message.success('批量更新成功');
    loadRows(selectedDate, groupId);
  };

  const handleBatchRange = async (status) => {
    if (!groupId || !batchRange || !batchRange[0] || !batchRange[1]) {
      message.error('请选择小组和日期范围');
      return;
    }
    await api.post('/attendance/batch-range', {
      from: batchRange[0].format('YYYY-MM-DD'),
      to: batchRange[1].format('YYYY-MM-DD'),
      groupId,
      status
    });
    message.success('批量设置已完成');
    loadRows(selectedDate, groupId);
  };

  const columns = [
    { title: '工人', dataIndex: ['worker', 'name'] },
    { title: '岗位', dataIndex: ['worker', 'role', 'name'] },
    {
      title: '状态',
      render: (_, record, index) => (
        <Space>
          <Select
            value={record.status}
            onChange={(value) => {
              const nextRow = { ...record, status: value };
              updateRow(index, { status: value });
              saveRow(nextRow);
            }}
            options={statusOptions}
            style={{ width: 100 }}
          />
          {record.status === 'PRESENT' && <Tag color="green">出勤</Tag>}
          {record.status === 'LEAVE' && <Tag color="orange">请假</Tag>}
          {record.status === 'FACTORY_HOLIDAY' && <Tag color="blue">工厂放假</Tag>}
        </Space>
      )
    },
    {
      title: '备注',
      render: (_, record, index) => (
        <Input
          value={record.remark}
          onChange={(e) => {
            const value = e.target.value;
            updateRow(index, { remark: value });
            const timers = remarkTimersRef.current;
            if (timers.has(record.worker.id)) {
              clearTimeout(timers.get(record.worker.id));
            }
            const timer = setTimeout(() => {
              saveRow({ ...record, remark: value });
              timers.delete(record.worker.id);
            }, 600);
            timers.set(record.worker.id, timer);
          }}
          onBlur={() => saveRow(record)}
        />
      )
    },
  ];

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '320px 1fr', gap: 16 }}>
      <div style={{ border: '1px solid #f0f0f0', borderRadius: 8, padding: 8 }}>
        <Calendar
          fullscreen={false}
          value={selectedDate}
          onSelect={(value) => setSelectedDate(value)}
          headerRender={({ value, onChange }) => (
            <Space style={{ marginBottom: 8 }}>
              <Button size="small" onClick={() => onChange(value.subtract(1, 'month'))}>上月</Button>
              <div style={{ fontWeight: 600 }}>{value.format('YYYY年MM月')}</div>
              <Button size="small" onClick={() => onChange(value.add(1, 'month'))}>下月</Button>
            </Space>
          )}
        />
      </div>
      <div>
        <Space style={{ marginBottom: 12 }}>
          <Select
            placeholder="选择小组"
            value={groupId}
            style={{ width: 200 }}
            onChange={(v) => setGroupId(v)}
            options={groups.map(g => ({ value: g.id, label: g.name }))}
          />
          <Button type="primary" onClick={() => handleBatch('PRESENT')}>全员出勤</Button>
          <Button onClick={() => handleBatch('PRESENT')}>一键设为出勤</Button>
          <Button onClick={() => handleBatch('LEAVE')}>一键设为请假</Button>
          <Button onClick={() => handleBatch('FACTORY_HOLIDAY')}>一键设为工厂放假</Button>
          <Popover
            trigger="click"
            placement="bottom"
            content={(
              <Space direction="vertical">
                <DatePicker.RangePicker
                  value={batchRange}
                  onChange={(v) => setBatchRange(v)}
                />
                <Button onClick={() => handleBatchRange('PRESENT')}>选时间段一键出勤</Button>
              </Space>
            )}
          >
            <Button>时间段一键出勤</Button>
          </Popover>
        </Space>
        <div style={{ marginBottom: 8, color: '#666' }}>
          日期：{selectedDate.format('YYYY-MM-DD')}
        </div>
        <Table
          rowKey={(row) => row.worker.id}
          columns={columns}
          dataSource={rows}
          pagination={{ pageSize: 10 }}
        />
      </div>
    </div>
  );
}
