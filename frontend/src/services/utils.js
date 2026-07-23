export function fmtBRL(n) {
  n = Number(n) || 0
  return n.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

export function fmtDateBR(iso) {
  if (!iso) return '—'
  const [y, m, d] = iso.split('-')
  return `${d}/${m}/${y}`
}

export function todayISO() {
  return new Date().toISOString().slice(0, 10)
}

export function daysUntil(iso) {
  if (!iso) return null
  const target = new Date(iso + 'T00:00:00')
  const now = new Date(todayISO() + 'T00:00:00')
  return Math.round((target - now) / 86400000)
}

export function categoria(pedidoTexto) {
  if (!pedidoTexto) return 'Outros'
  const up = pedidoTexto.toUpperCase()
  if (up.includes('RESSUPRIMENTO')) return 'Ressuprimento'
  if (up.includes('TRANCHE')) return 'Tranche'
  if (up.includes('BRINDE')) return 'Brinde'
  if (up.includes('SEM PEDIDO')) return 'Sem pedido de venda'
  return 'Outros'
}

export const CATEGORIA_CORES = {
  'Ressuprimento': '#D6127D',
  'Tranche': '#2F6FED',
  'Brinde': '#DB8A1F',
  'Sem pedido de venda': '#8B8FA3',
  'Outros': '#2E6B4F'
}

export function statusVencimento(pedido) {
  if (pedido.status === 'pago') return 'pago'
  if (!pedido.vencimento) return 'pendente'
  const dias = daysUntil(pedido.vencimento)
  if (dias < 0) return 'vencido'
  if (dias <= 7) return 'urgente'
  return 'pendente'
}
export function statusLabel(s) {
  return { pago: 'Pago', vencido: 'Vencido', urgente: 'Vencendo', pendente: 'Pendente' }[s]
}

export function statusSelagem(pedido) {
  if (pedido.status !== 'pago') return 'aguardando'
  const esperado = Number(pedido.valorDae) || 0
  const pago = Number(pedido.valorPago) || 0
  return Math.abs(esperado - pago) < 1 ? 'ok' : 'erro'
}
export function selagemLabel(s) {
  return { ok: 'OK', erro: 'Precisa selagem', aguardando: 'Aguardando pgto.' }[s]
}
export function selagemClass(s) {
  return { ok: 'stamp-pago', erro: 'stamp-vencido', aguardando: 'stamp-pendente' }[s]
}
