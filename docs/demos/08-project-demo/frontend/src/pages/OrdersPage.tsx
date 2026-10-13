import { useEffect, useState } from 'react'
import { Button, Form, Input, InputNumber, Modal, Select, Space, Table, message } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { createOrder, deleteOrder, fetchOrders, updateOrder, type Order, type OrderPayload } from '../api/order'
import { STATUS_OPTIONS, statusLabel } from '../status'
interface OrderForm { customerName: string; productName: string; quantity: number; unitPrice: string; status: string; remark?: string }
export default function OrdersPage() {
  const [loading, setLoading] = useState(false)
  const [records, setRecords] = useState<Order[]>([])
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(1)
  const [size, setSize] = useState(10)
  const [kw, setKw] = useState('')
  const [status, setStatus] = useState<string | undefined>()
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<Order | null>(null)
  const [submitting, setSubmitting] = useState(false)
  const [form] = Form.useForm<OrderForm>()
  const load = async (nextPage = page, nextSize = size, nextKw = kw, nextStatus = status) => {
    setLoading(true)
    try {
      const data = await fetchOrders({ kw: nextKw.trim() || undefined, status: nextStatus, page: nextPage, size: nextSize })
      setRecords(data.records); setTotal(data.total); setPage(data.page); setSize(data.size)
    } catch (error) { message.error(error instanceof Error ? error.message : '加载失败') }
    finally { setLoading(false) }
  }
  useEffect(() => { void load(1, 10, '', undefined) }, [])
  const submit = async (values: OrderForm) => {
    const payload: OrderPayload = { customerName: values.customerName, productName: values.productName, quantity: values.quantity, unitPrice: String(values.unitPrice), status: values.status, remark: values.remark }
    setSubmitting(true)
    try {
      const saved = editing ? await updateOrder(editing.id, payload) : await createOrder(payload)
      message.success('订单 ' + saved.orderNo + '，金额 ' + saved.totalAmount)
      setOpen(false); await load()
    } catch (error) { message.error(error instanceof Error ? error.message : '保存失败') }
    finally { setSubmitting(false) }
  }
  const columns: ColumnsType<Order> = [
    { title: '订单号', dataIndex: 'orderNo' }, { title: '客户', dataIndex: 'customerName' }, { title: '商品', dataIndex: 'productName' },
    { title: '数量', dataIndex: 'quantity' }, { title: '单价', dataIndex: 'unitPrice' }, { title: '总金额', dataIndex: 'totalAmount' },
    { title: '状态', dataIndex: 'status', render: (value: string) => statusLabel(value) }, { title: '创建时间', dataIndex: 'createTime' },
    { title: '操作', render: (_, order) => <Space>
      <Button size="small" onClick={() => { setEditing(order); form.setFieldsValue({ customerName: order.customerName, productName: order.productName, quantity: order.quantity, unitPrice: order.unitPrice, status: order.status, remark: order.remark ?? '' }); setOpen(true) }}>编辑</Button>
      <Button size="small" danger onClick={() => Modal.confirm({ title: '删除订单', content: '确认删除 ' + order.orderNo + '？', okText: '删除', cancelText: '取消', onOk: async () => { await deleteOrder(order.id); message.success('已删除'); await load() } })}>删除</Button>
    </Space> },
  ]
  return <div>
    <Space style={{ marginBottom: 16 }} wrap>
      <Input.Search allowClear placeholder="订单号或客户名称前缀" style={{ width: 280 }} onSearch={(value) => { setKw(value); void load(1, size, value, status) }} />
      <Select allowClear placeholder="状态" style={{ width: 140 }} options={STATUS_OPTIONS} value={status} onChange={(value) => { setStatus(value); void load(1, size, kw, value) }} />
      <Button type="primary" onClick={() => { setEditing(null); form.resetFields(); form.setFieldsValue({ quantity: 1, status: 'PENDING' }); setOpen(true) }}>新建订单</Button>
    </Space>
    <Table rowKey="id" loading={loading} columns={columns} dataSource={records} pagination={{ current: page, pageSize: size, total, showSizeChanger: true, pageSizeOptions: [10, 20, 50], onChange: (nextPage, nextSize) => { void load(nextPage, nextSize) } }} />
    <Modal title={editing ? '编辑订单' : '新建订单'} open={open} confirmLoading={submitting} onCancel={() => setOpen(false)} onOk={() => form.submit()} destroyOnClose>
      <Form form={form} layout="vertical" onFinish={submit}>
        <Form.Item name="customerName" label="客户名称" rules={[{ required: true, message: '请填写客户名称' }]}><Input /></Form.Item>
        <Form.Item name="productName" label="商品名称" rules={[{ required: true, message: '请填写商品名称' }]}><Input /></Form.Item>
        <Form.Item name="quantity" label="数量" rules={[{ required: true, message: '请填写数量' }]}><InputNumber min={1} style={{ width: '100%' }} /></Form.Item>
        <Form.Item name="unitPrice" label="单价" rules={[{ required: true, message: '请填写单价' }]}><Input /></Form.Item>
        <Form.Item name="status" label="状态" rules={[{ required: true, message: '请选择状态' }]}><Select options={STATUS_OPTIONS} /></Form.Item>
        <Form.Item name="remark" label="备注"><Input.TextArea rows={2} /></Form.Item>
      </Form>
    </Modal>
  </div>
}
