// JSON-LD 结构化数据注入 — 将结构化数据脚本注入 <head>，供搜索引擎富文本摘要使用
import { useHead } from '@unhead/vue'

export function useJsonLd(json: Record<string, unknown>) {
  useHead({
    script: [
      {
        type: 'application/ld+json',
        innerHTML: JSON.stringify(json),
      },
    ],
  })
}
