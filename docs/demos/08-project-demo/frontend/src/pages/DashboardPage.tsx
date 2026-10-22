import { useEffect, useRef, useState } from 'react'
import { Card, Col, Row, Statistic } from 'antd'
import * as echarts from 'echarts'
import { fetchStats, type OrderStats } from '../api/order'
import { statusLabel } from '../status'
export default function DashboardPage() {
  const [stats, setStats] = useState<OrderStats | null>(null)
  const pieRef = useRef<HTMLDivElement>(null)
  const barRef = useRef<HTMLDivElement>(null)
  const aovRef = useRef<HTMLDivElement>(null)
  useEffect(() => { void fetchStats().then(setStats) }, [])
  useEffect(() => {
    if (!stats || !pieRef.current || !barRef.current || !aovRef.current) return
    const pie = echarts.init(pieRef.current)
    pie.setOption({ tooltip: { trigger: 'item' }, series: [{ type: 'pie', radius: '70%', data: stats.statusCounts.map((item) => ({ name: statusLabel(item.status), value: item.count })) }] })
    const bar = echarts.init(barRef.current)
    bar.setOption({
      tooltip: { trigger: 'axis' }, legend: { data: ['订单数', '金额'] },
      xAxis: { type: 'category', data: stats.trend.map((item) => item.date) },
      yAxis: [{ type: 'value' }, { type: 'value' }],
      series: [
        { name: '订单数', type: 'bar', data: stats.trend.map((item) => item.count) },
        { name: '金额', type: 'bar', yAxisIndex: 1, data: stats.trend.map((item) => Number(item.amount)) },
      ],
    })
    const aov = echarts.init(aovRef.current)
    const aovPerDay = stats.trend.map((item) => (item.count > 0 ? Number((Number(item.amount) / item.count).toFixed(2)) : null))
    aov.setOption({
      tooltip: { trigger: 'axis', valueFormatter: (value: unknown) => (value == null ? '-' : `${Number(value).toFixed(2)} 元`) },
      xAxis: { type: 'category', data: stats.trend.map((item) => item.date) },
      yAxis: { type: 'value', axisLabel: { formatter: '{value} 元' } },
      series: [{ name: '客单价', type: 'line', data: aovPerDay }],
    })
    const resize = () => { pie.resize(); bar.resize(); aov.resize() }
    window.addEventListener('resize', resize)
    return () => { window.removeEventListener('resize', resize); pie.dispose(); bar.dispose(); aov.dispose() }
  }, [stats])
  const kpi = stats?.kpi
  return (
    <div>
      <Row gutter={16}>
        <Col span={6}><Card><Statistic title="总订单数" value={kpi ? kpi.totalCount : '-'} /></Card></Col>
        <Col span={6}><Card><Statistic title="总金额" value={kpi ? kpi.totalAmount : '-'} /></Card></Col>
        <Col span={6}><Card><Statistic title="待支付" value={kpi ? kpi.pendingCount : '-'} /></Card></Col>
        <Col span={6}><Card><Statistic title="已完成" value={kpi ? kpi.completedCount : '-'} /></Card></Col>
      </Row>
      <Row gutter={16} style={{ marginTop: 16 }}>
        <Col span={10}><Card title="状态分布"><div ref={pieRef} style={{ height: 320 }} /></Card></Col>
        <Col span={14}><Card title="近 7 日"><div ref={barRef} style={{ height: 320 }} /></Card></Col>
      </Row>
      <Row gutter={16} style={{ marginTop: 16 }}>
        <Col span={24}><Card title="近 7 日客单价"><div ref={aovRef} style={{ height: 320 }} /></Card></Col>
      </Row>
    </div>
  )
}
