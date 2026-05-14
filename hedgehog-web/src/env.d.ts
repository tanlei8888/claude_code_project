/// <reference types="vite/client" />

declare module '@kangc/v-md-editor/lib/theme/vuepress.js' {
  const theme: any
  export default theme
}
declare module '@kangc/v-md-editor' {
  const VMdEditor: any
  export default VMdEditor
}
declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<object, object, unknown>
  export default component
}
