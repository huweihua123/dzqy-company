import React from 'react';
import { Layout, Menu } from 'antd';
import { Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import OrdersPage from './pages/OrdersPage.jsx';
import WorkersPage from './pages/WorkersPage.jsx';
import AttendancePage from './pages/AttendancePage.jsx';
import SalaryPaymentsPage from './pages/SalaryPaymentsPage.jsx';
import ReportsPage from './pages/ReportsPage.jsx';
import ProductionPage from './pages/ProductionPage.jsx';
import DashboardPage from './pages/DashboardPage.jsx';
import SalarySummaryPage from './pages/SalarySummaryPage.jsx';
import SalaryCalcPage from './pages/SalaryCalcPage.jsx';
import SalaryPrintPage from './pages/SalaryPrintPage.jsx';
import OwnersPage from './pages/OwnersPage.jsx';
import MaterialsPage from './pages/MaterialsPage.jsx';
import SpecsPage from './pages/SpecsPage.jsx';
import WorkerRolesPage from './pages/WorkerRolesPage.jsx';
import WorkerGroupsPage from './pages/WorkerGroupsPage.jsx';
import OwnerReceiptsPage from './pages/OwnerReceiptsPage.jsx';
import OwnerBalancePage from './pages/OwnerBalancePage.jsx';

const { Header, Sider, Content } = Layout;

const menuItems = [
  { key: '/orders', label: '订单管理' },
  {
    key: 'basic',
    label: '基础资料',
    children: [
      { key: '/owners', label: '货主管理' },
      { key: '/owner-balances', label: '货主对账' },
      { key: '/owner-receipts', label: '货主收款' },
      { key: '/materials', label: '材质管理' },
      { key: '/specs', label: '规格管理' },
      { key: '/worker-roles', label: '岗位类型' }
    ]
  },
  {
    key: 'people',
    label: '人员与生产',
    children: [
      { key: '/worker-groups', label: '员工小组' },
      { key: '/workers', label: '员工管理' },
      { key: '/attendance', label: '出勤登记' },
      { key: '/production', label: '产量录入' }
    ]
  },
  {
    key: 'salary',
    label: '薪资',
    children: [
      { key: '/salary-calc', label: '工资核算' },
      { key: '/salary-print', label: '工资打印' },
      { key: '/salary-summary', label: '工资汇总' },
      { key: '/salary-payments', label: '工资发放' }
    ]
  },
  { key: '/dashboard', label: '经营看板' },
  { key: '/reports', label: '统计报表' }
];

export default function App() {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider width={220} theme="light">
        <div style={{ padding: 16, fontWeight: 700 }}>锻造记账系统</div>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={(e) => {
            if (e.key.startsWith('/')) {
              navigate(e.key);
            }
          }}
          defaultOpenKeys={['basic', 'people', 'salary']}
        />
      </Sider>
      <Layout>
        <Header style={{ background: '#fff', padding: '0 20px', fontWeight: 600 }}>
          业务管理
        </Header>
        <Content style={{ padding: 20 }}>
          <Routes>
            <Route path="/" element={<OrdersPage />} />
            <Route path="/orders" element={<OrdersPage />} />
            <Route path="/owners" element={<OwnersPage />} />
            <Route path="/owner-balances" element={<OwnerBalancePage />} />
            <Route path="/owner-receipts" element={<OwnerReceiptsPage />} />
            <Route path="/materials" element={<MaterialsPage />} />
            <Route path="/specs" element={<SpecsPage />} />
            <Route path="/worker-roles" element={<WorkerRolesPage />} />
            <Route path="/worker-groups" element={<WorkerGroupsPage />} />
            <Route path="/workers" element={<WorkersPage />} />
            <Route path="/attendance" element={<AttendancePage />} />
            <Route path="/production" element={<ProductionPage />} />
            <Route path="/salary-calc" element={<SalaryCalcPage />} />
            <Route path="/salary-print" element={<SalaryPrintPage />} />
            <Route path="/salary-summary" element={<SalarySummaryPage />} />
            <Route path="/salary-payments" element={<SalaryPaymentsPage />} />
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/reports" element={<ReportsPage />} />
          </Routes>
        </Content>
      </Layout>
    </Layout>
  );
}
