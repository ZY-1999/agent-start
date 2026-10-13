import { useState } from 'react'
import { Alert, Button, Form, Input, InputNumber, Modal, Select, message } from 'antd'
import { createOrder, type CreatedOrder } from '../api/order'

interface OrderForm {
  customerName: string
  productName: string
  quantity: number
  unitPrice: string
  status: string
  remark?: string
}

const STATUS_OPTIONS = [
  { value: 'PENDING', label: '待支付' },
  { value: 'PAID', label: '已支付' },
  { value: 'SHIPPED', label: '已发货' },
  { value: 'COMPLETED', label: '已完成' },
  { value: 'CANCELLED', label: '已取消' },
]

export default function OrdersPage() {
  const [open, setOpen] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [created, setCreated] = useState<CreatedOrder | null>(null)
  const [form] = Form.useForm<OrderForm>()

  const openModal = () => {
    setCreated(null)
    form.resetFields()
    setOpen(true)
  }

  const submit = async (values: OrderForm) => {
    setSubmitting(true)
    try {
      const order = await createOrder({
        customerName: values.customerName,
        productName: values.productName,
        quantity: values.quantity,
        unitPrice: String(values.unitPrice),
        status: values.status,
        remark: values.remark,
      })
      setCreated(order)
    } catch (error) {
      const text = error instanceof Error ? error.message : '创建失败'
      message.error(text)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main style={{ padding: 24 }}>
      <Button type="primary" onClick={openModal}>
        新建订单
      </Button>
      <Modal
        title="新建订单"
        open={open}
        onCancel={() => setOpen(false)}
        onOk={() => form.submit()}
        confirmLoading={submitting}
        okText="提交"
        cancelText="关闭"
        destroyOnClose
      >
        <Form form={form} layout="vertical" onFinish={submit} initialValues={{ status: 'PENDING' }}>
          <Form.Item name="customerName" label="客户名称" rules={[{ required: true, message: '请填写客户名称' }]}>
            <Input maxLength={64} />
          </Form.Item>
          <Form.Item name="productName" label="商品名称" rules={[{ required: true, message: '请填写商品名称' }]}>
            <Input maxLength={128} />
          </Form.Item>
          <Form.Item name="quantity" label="数量" rules={[{ required: true, message: '请填写数量' }]}>
            <InputNumber min={1} precision={0} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="unitPrice" label="单价" rules={[{ required: true, message: '请填写单价' }]}>
            <InputNumber min={0} precision={2} stringMode style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="status" label="状态" rules={[{ required: true, message: '请选择状态' }]}>
            <Select options={STATUS_OPTIONS} />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea maxLength={255} rows={3} />
          </Form.Item>
        </Form>
        {created && (
          <Alert
            type="success"
            showIcon
            message="订单已创建"
            description={`订单号 ${created.orderNo}，总金额 ${created.totalAmount}`}
          />
        )}
      </Modal>
    </main>
  )
}
