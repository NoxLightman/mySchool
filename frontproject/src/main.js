import { createApp } from 'vue'
import App from './App.vue'
import router from "@/router"
import axios from 'axios'
import 'element-plus/theme-chalk/dark/css-vars.css'
import {createPinia} from "pinia";

axios.defaults.baseURL = 'https://www.oilbone.com:8081'
const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
