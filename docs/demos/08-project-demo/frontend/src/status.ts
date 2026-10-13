export const STATUS_OPTIONS = [
  { value: 'PENDING', label: '待支付' },
  { value: 'PAID', label: '已支付' },
  { value: 'SHIPPED', label: '已发货' },
  { value: 'COMPLETED', label: '已完成' },
  { value: 'CANCELLED', label: '已取消' },
]
export function statusLabel(status: string): string {
  const found = STATUS_OPTIONS.find((item) => item.value === status)
  return found ? found.label : status
}
