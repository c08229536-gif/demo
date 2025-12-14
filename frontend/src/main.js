import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus' // 引入组件库
import 'element-plus/dist/index.css' // 引入样式(关键!)

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus) // 安装组件库

app.mount('#app')
