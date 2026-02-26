import React, { useEffect, useMemo, useRef, useState } from 'react';
import { Card, DatePicker, Select, Space, Table, Tag } from 'antd';
import dayjs from 'dayjs';
import * as echarts from 'echarts';
import api from '../services/api.js';

const { RangePicker } = DatePicker;

export default function DashboardPage() {
  const [range, setRange] = useState([dayjs().startOf('month'), dayjs().endOf('month')]);
  const [rangePreset, setRangePreset] = useState('month');
  const [groups, setGroups] = useState([]);
  const [groupId, setGroupId] = useState(null);
  const [daily, setDaily] = useState([]);
  const [selectedDate, setSelectedDate] = useState(dayjs());
  const [dayAttendance, setDayAttendance] = useState([]);
  const [heatMetric, setHeatMetric] = useState('production');
  const heatmapRef = useRef(null);

  const loadGroups = async () => {
    const res = await api.get('/worker-groups');
    setGroups(res.data);
  };

  const loadDaily = async (r, gid) => {
    const params = {
      from: r[0].format('YYYY-MM-DD'),
      to: r[1].format('YYYY-MM-DD')
    };
    if (gid) params.groupId = gid;
    const res = await api.get('/dashboard/daily', { params });
    setDaily(res.data);
  };

  const loadDayDetail = async (date, gid) => {
    const params = { date: date.format('YYYY-MM-DD') };
    if (gid) params.groupId = gid;
    const res = await api.get('/attendance', { params });
    setDayAttendance(res.data);
  };

  useEffect(() => {
    loadGroups();
  }, []);

  useEffect(() => {
    loadDaily(range, groupId);
  }, [range, groupId]);

  useEffect(() => {
    loadDayDetail(selectedDate, groupId);
  }, [selectedDate, groupId]);

  const stats = useMemo(() => {
    return daily.reduce((acc, d) => {
      acc.present += d.presentCount;
      acc.leave += d.leaveCount;
      acc.holiday += d.holidayCount;
      acc.production += Number(d.productionNet || 0);
      return acc;
    }, { present: 0, leave: 0, holiday: 0, production: 0 });
  }, [daily]);

  useEffect(() => {
    if (!heatmapRef.current) return;
    const chart = echarts.init(heatmapRef.current);
    const data = daily.map(d => {
      const val = heatMetric === 'production' ? Number(d.productionNet || 0) : d.presentCount;
      return [d.date, val];
    });
    const maxVal = Math.max(1, ...data.map(d => d[1]));

    chart.setOption({
      tooltip: {
        position: 'top',
        formatter: (p) => heatMetric === 'production'
          ? `${p.data[0]}<br/>产量：${p.data[1].toFixed(2)} 吨`
          : `${p.data[0]}<br/>出勤人数：${p.data[1]}`
      },
      visualMap: {
        min: 0,
        max: maxVal,
        calculable: true,
        orient: 'horizontal',
        left: 'center',
        bottom: 0
      },
      calendar: {
        top: 30,
        left: 20,
        right: 20,
        cellSize: ['auto', 16],
        range: [range[0].format('YYYY-MM-DD'), range[1].format('YYYY-MM-DD')],
        yearLabel: { show: false }
      },
      series: [
        {
          type: 'heatmap',
          coordinateSystem: 'calendar',
          data
        }
      ]
    });

    chart.on('click', (p) => {
      if (p?.data?.[0]) {
        setSelectedDate(dayjs(p.data[0]));
      }
    });

    const onResize = () => chart.resize();
    window.addEventListener('resize', onResize);
    return () => {
      window.removeEventListener('resize', onResize);
      chart.dispose();
    };
  }, [daily, range, heatMetric]);

  const daySummary = useMemo(() => {
    const dateStr = selectedDate.format('YYYY-MM-DD');
    const d = daily.find(x => x.date === dateStr);
    return d || { presentCount: 0, leaveCount: 0, holidayCount: 0, productionNet: 0 };
  }, [daily, selectedDate]);

  const dayColumns = [
    { title: '工人', dataIndex: ['worker', 'name'] },
    { title: '岗位', dataIndex: ['worker', 'role', 'name'] },
    {
      title: '状态',
      dataIndex: 'status',
      render: (v) => {
        if (v === 'LEAVE') return <Tag color="orange">请假</Tag>;
        if (v === 'FACTORY_HOLIDAY') return <Tag color="blue">放假</Tag>;
        return <Tag color="green">出勤</Tag>;
      }
    },
    { title: '备注', dataIndex: 'remark' }
  ];

  return (
    <div style={{ display: 'grid', gap: 16 }}>
      <Card style={{ borderRadius: 12 }}>
        <div style={{ fontSize: 18, fontWeight: 700, marginBottom: 8 }}>经营看板</div>
        <div style={{ color: '#666', marginBottom: 12 }}>一屏看清每日出勤与产量</div>
        <Space wrap>
          <Select
            value={rangePreset}
            onChange={(v) => {
              setRangePreset(v);
              if (v === 'month') {
                setRange([dayjs().startOf('month'), dayjs().endOf('month')]);
              } else if (v === 'year') {
                setRange([dayjs().startOf('year'), dayjs().endOf('year')]);
              }
            }}
            options={[
              { value: 'month', label: '本月' },
              { value: 'year', label: '本年' }
            ]}
            style={{ width: 120 }}
          />
          <RangePicker
            value={range}
            onChange={(v) => {
              if (v) {
                setRangePreset('custom');
                setRange(v);
              }
            }}
          />
          <Select
            allowClear
            placeholder="选择小组"
            value={groupId}
            style={{ width: 200 }}
            onChange={(v) => setGroupId(v)}
            options={groups.map(g => ({ value: g.id, label: g.name }))}
          />
          <Select
            value={heatMetric}
            onChange={(v) => setHeatMetric(v)}
            options={[
              { value: 'production', label: '热力图：产量' },
              { value: 'present', label: '热力图：出勤人数' }
            ]}
            style={{ width: 180 }}
          />
        </Space>
      </Card>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 12 }}>
        <Card style={{ borderRadius: 12 }}>
          <div style={{ fontSize: 12, color: '#888' }}>出勤人次</div>
          <div style={{ fontSize: 20, fontWeight: 600 }}>{stats.present}</div>
        </Card>
        <Card style={{ borderRadius: 12 }}>
          <div style={{ fontSize: 12, color: '#888' }}>请假人次</div>
          <div style={{ fontSize: 20, fontWeight: 600 }}>{stats.leave}</div>
        </Card>
        <Card style={{ borderRadius: 12 }}>
          <div style={{ fontSize: 12, color: '#888' }}>放假人次</div>
          <div style={{ fontSize: 20, fontWeight: 600 }}>{stats.holiday}</div>
        </Card>
        <Card style={{ borderRadius: 12 }}>
          <div style={{ fontSize: 12, color: '#888' }}>产量合计(吨)</div>
          <div style={{ fontSize: 20, fontWeight: 600 }}>{stats.production.toFixed(2)}</div>
        </Card>
      </div>

      <Card style={{ borderRadius: 12 }} title={heatMetric === 'production' ? '每日产量热力图' : '每日出勤人数热力图'}>
        <div ref={heatmapRef} style={{ height: 220 }} />
      </Card>

      <Card style={{ borderRadius: 12 }} title={`当天详情（${selectedDate.format('YYYY-MM-DD')}）`}>
        <div style={{ display: 'flex', gap: 16, marginBottom: 12 }}>
          <div>出勤：{daySummary.presentCount}</div>
          <div>请假：{daySummary.leaveCount}</div>
          <div>放假：{daySummary.holidayCount}</div>
          <div>产量：{Number(daySummary.productionNet || 0).toFixed(2)} 吨</div>
        </div>
        <Table rowKey="id" columns={dayColumns} dataSource={dayAttendance} pagination={{ pageSize: 8 }} />
      </Card>
    </div>
  );
}
