<script setup>
import {computed,reactive,ref} from "vue";
import {EditPen, Lock, Message, User} from "@element-plus/icons-vue";
import {ElFormItem, ElRow, ElCol, ElMessage} from "element-plus";
import router from "@/router/index.js";
import {get, post} from "@/net";

const form = reactive({
  username: "",
  password: "",
  password_repeat: "",
  email: "",
  code: ""
})

const validateUsername = (rule, value, callback) => {
  if(value === '') {
    callback(new Error('请输入用户名'));
  } else if(!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error('用户名不能包含特殊字符'));
  } else {
    callback();
  }
}

const validatePassword = (rule, value, callback) => {
  if(value === '') {
    callback(new Error("请再次输入密码"));
  } else if(value !== form.password) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
}

const rule = {
  username: [
    {validator: validateUsername, trigger: ['blur', 'change']},
    { min:6, max:10, message: "用户名必须在 6-10 个字符之间", trigger: ['blur', 'change']},
  ],
  password: [
    {required: true, trigger: ['blur', 'change']},
    {min: 6, max:20, message: "密码长度必须在6-20个字符之间", trigger: ['blur', 'change']},
  ],
  password_repeat: [
      {validator: validatePassword, trigger: ['blur', 'change']},
  ],
  email: [
    {required: true, trigger: ['blur', 'change']},
    {type: 'email', message: '请输入邮箱', trigger: ['blur', 'change']},
  ],
  code: [
      {required: true, trigger: ['blur']},
  ]
}

const formRef = ref()
const isEmailValid = ref(false)
const coldTime = ref(0)
const isValidateEmail = computed(() => (/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(form.email)))


function askCode() {
  if(isValidateEmail) {
    get(`/api/auth/ask-code?email=${form.email}&type=register`, ()=>{
      coldTime.value = 60;
      ElMessage.success(`验证码已发送到邮箱: ${form.email}，请注意查收`);
      setInterval(() => {})
      const handle = setInterval(() => {
        coldTime.value--
        if(coldTime.value === 0) {
          clearInterval(handle)
        }
      }, 1000)
    }, (message) => {
      ElMessage.warning(message);
      coldTime.value = 0;
    })
  } else {
    ElMessage.warning("请输入正确的邮箱")
  }
}

function register() {
  formRef.value.validate(async (valid) => {
    if (valid) {
      post("/api/auth/register", {...form}, ()=>{
        ElMessage.success("注册成功，欢迎加入我们");
        router.push("/");
      })
    } else {
      ElMessage.warning("请完整填写表单")
    }
  })
}
</script>

<template>
  <div style="text-align: center; margin-top:100px">
    <div>
      <div style="font-size: 25px">注册新用户</div>
      <div style="font-size: 14px; color:gray" >欢迎来到我们的网站，请在下方填写相关信息</div>
    </div>
    <div style="margin: 50px 20px 0 20px">
        <el-form :model="form" :rules="rule" ref="formRef">
          <el-form-item prop="username">
            <el-input v-model="form.username" maxlength="20" type="text" placeholder="用户名">
              <template #prefix>
                <el-icon><User/></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input v-model="form.password" maxlength="20" type="password" placeholder="密码">
              <template #prefix>
                <el-icon><Lock/></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password_repeat">
            <el-input v-model="form.password_repeat" maxlength="20" type="password" placeholder="重复密码">
              <template #prefix>
                <el-icon><Lock/></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="email">
            <el-input v-model="form.email" maxlength="32" type="text" placeholder="邮箱">
              <template #prefix>
                <el-icon><Message/></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="code">
            <el-row :gutter="10" style="width: 100%">
              <el-col :span="17">
                <el-input v-model="form.code" maxlength="6" type="text" placeholder="验证码">
                  <template #prefix>
                    <el-icon><EditPen/></el-icon>
                  </template>
                </el-input>
              </el-col>
              <el-col :span="5">
                <el-button @click="askCode()" type="success" :disabled="coldTime || !isValidateEmail">
                  {{coldTime > 0 ? '请稍后 ' + coldTime + ' 秒' : '获取验证码'}}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
    </div>
    <div style="margin-top: 80px">
      <el-button @click="register()" style="width: 270px" type="warning" plain>立即注册</el-button>
    </div>
    <div style="margin-top: 20px">
      <span style="margin-top: 20px">已有账号? </span>
      <el-link @click="router.push('/')" type="primary" style="translate: 0 -2px">立即登录</el-link>
    </div>
  </div>
</template>

<style scoped>

</style>