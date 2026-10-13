import axios from 'axios'
export interface Order {
  id: number
  orderNo: string
  customerName: string
  productName: string
  quantity: number
  unitPrice: string
  totalAmount: string
  status: string
  remark: string | null
  createTime: string
  updateTime: string
}
export interface OrderPage { total: number; page: number; size: number; records: Order[] }
export interface OrderPayload {
  customerName: string
  productName: string
  quantity: number
  unitPrice: string
  status: string
  remark?: string
}
export interface OrderStats {
  kpi: { totalCount: number; totalAmount: string; pendingCount: number; completedCount: number }
  statusCounts: { status: string; count: number }[]
  trend: { date: string; count: number; amount: string }[]
}
interface ResultBody<T> { code: number; message: string; data: T }
async function unwrap<T>(promise: Promise<{ data: ResultBody<T> }>): Promise<T> {
  const response = await promise
  if (response.data.code !== 0) throw new Error(response.data.message)
  return response.data.data
}
export function createOrder(payload: OrderPayload): Promise<Order> {
  return unwrap(axios.post<ResultBody<Order>>('/api/orders', payload))
}
export function updateOrder(id: number, payload: OrderPayload): Promise<Order> {
  return unwrap(axios.put<ResultBody<Order>>('/api/orders/' + id, payload))
}
export function deleteOrder(id: number): Promise<null> {
  return unwrap(axios.delete<ResultBody<null>>('/api/orders/' + id))
}
export function fetchOrders(params: { kw?: string; status?: string; page: number; size: number }): Promise<OrderPage> {
  return unwrap(axios.get<ResultBody<OrderPage>>('/api/orders', { params }))
}
export function fetchStats(): Promise<OrderStats> {
  return unwrap(axios.get<ResultBody<OrderStats>>('/api/orders/stats'))
}
export function resetOrders(): Promise<{ inserted: number }> {
  return unwrap(axios.post<ResultBody<{ inserted: number }>>('/api/orders/reset'))
}
