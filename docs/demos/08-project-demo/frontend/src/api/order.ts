import axios from 'axios'

export interface CreatedOrder {
  orderNo: string
  totalAmount: string
}

export interface CreateOrderPayload {
  customerName: string
  productName: string
  quantity: number
  unitPrice: string
  status: string
  remark?: string
}

interface ResultBody {
  code: number
  message: string
  data: CreatedOrder
}

export async function createOrder(payload: CreateOrderPayload): Promise<CreatedOrder> {
  const response = await axios.post<ResultBody>('/api/orders', payload)
  if (response.data.code !== 0) {
    throw new Error(response.data.message)
  }
  return response.data.data
}
