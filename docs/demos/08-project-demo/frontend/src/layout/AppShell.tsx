import { Button, Layout, Menu, Modal, message } from 'antd'
import { Link, useLocation } from 'react-router-dom'
import { resetOrders } from '../api/order'
const { Header, Content } = Layout
export default function AppShell({ children }: { children: React.ReactNode }) {
  const location = useLocation()
  const selected = location.pathname.startsWith('/dashboard') ? 'dashboard' : 'orders'
  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
        <div style={{ color: '#fff', fontWeight: 600 }}>订单演示</div>
        <Menu theme="dark" mode="horizontal" selectedKeys={[selected]} style={{ flex: 1, minWidth: 0 }}
          items={[
            { key: 'orders', label: <Link to="/orders">订单管理</Link> },
            { key: 'dashboard', label: <Link to="/dashboard">汇总看板</Link> },
          ]} />
        <Button onClick={() => Modal.confirm({
          title: '重置数据',
          content: '将清除当前订单并恢复 8 条示例数据。',
          okText: '重置',
          cancelText: '取消',
          onOk: async () => { await resetOrders(); message.success('已恢复 8 条示例订单'); window.location.reload() },
        })}>重置数据</Button>
      </Header>
      <Content style={{ padding: 24 }}>{children}</Content>
    </Layout>
  )
}
