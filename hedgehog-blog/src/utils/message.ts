// 轻量 Toast 消息提示 — 纯 CSS 实现，无第三方依赖，顶部居中 3 秒自动消失
type MessageType = 'error' | 'success' | 'warning'

// 不同消息类型的 Tailwind 配色
const COLORS: Record<MessageType, string> = {
  error: 'bg-red-50 border-red-200 text-red-700',
  success: 'bg-green-50 border-green-200 text-green-700',
  warning: 'bg-yellow-50 border-yellow-200 text-yellow-700',
}

// 创建 DOM 元素并以淡入-淡出动画展示提示消息
function show(msg: string, type: MessageType) {
  const toast = document.createElement('div')
  toast.className = `fixed top-4 left-1/2 z-[9999] -translate-x-1/2 px-5 py-2.5 rounded-lg border text-sm shadow-lg transition-all duration-300 ${COLORS[type]}`
  toast.style.opacity = '0'
  toast.style.transform = 'translate(-50%, -12px)'
  toast.textContent = msg
  document.body.appendChild(toast)

  // 淡入动画
  requestAnimationFrame(() => {
    toast.style.opacity = '1'
    toast.style.transform = 'translate(-50%, 0)'
  })

  // 3 秒后淡出并移除 DOM
  setTimeout(() => {
    toast.style.opacity = '0'
    toast.style.transform = 'translate(-50%, -12px)'
    toast.addEventListener('transitionend', () => toast.remove())
  }, 3000)
}

// 对外暴露三种预设类型的快捷方法
export const message = {
  error: (msg: string) => show(msg, 'error'),
  success: (msg: string) => show(msg, 'success'),
  warning: (msg: string) => show(msg, 'warning'),
}
