<template>
    <div class="login-container">
      <el-card class="login-card">
        <h2>系统登录</h2>
        <el-form :model="loginForm" :rules="rules" ref="loginFormRef">
          <el-form-item prop="userName">
            <el-input v-model="loginForm.userName" placeholder="用户名">
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="密码">
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item prop="roleName">
            <el-select v-model="loginForm.roleName" placeholder="请选择角色" style="width: 100%">
              <el-option label="管理员" value="管理员" />
              <el-option label="经理" value="经理" />
              <el-option label="员工" value="员工" />
            </el-select>
          </el-form-item>
          
          <el-button type="primary" @click="handleLogin" style="width: 100%">登录</el-button>
        </el-form>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive } from 'vue'
  import { useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus'
  import { User, Lock } from '@element-plus/icons-vue'
  import { request } from '../utils/request'
  
  const router = useRouter()
  const loginFormRef = ref()
  
  const loginForm = reactive({
    userName: '',
    password: '',
    roleName: ''
  })
  
  const rules = {
    userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
    roleName: [{ required: true, message: '请选择角色', trigger: 'change' }]
  }
  
  const handleLogin = async () => {
    if (!loginFormRef.value) return
    
    await loginFormRef.value.validate(async (valid) => {
      if (valid) {
        try {
          const response = await request('/api/user/login', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginForm)
          })

          // 登录成功
          localStorage.setItem('token', response.token)
          localStorage.setItem('currentUser', JSON.stringify({
            id: response.userId,
            userName: response.userName,
            trueName: response.trueName,
            roleName: response.roleName
          }))
          router.push('/')
          ElMessage.success('登录成功')
          
        } catch (error) {
          // 显示具体的错误信息
          ElMessage.error(error.message)
        }
      }
    })
  }
  </script>
  
  <style scoped>
  .login-container {
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #f5f5f5;
  }
  
  .login-card {
    width: 400px;
  }
  
  h2 {
    text-align: center;
    margin-bottom: 30px;
  }
  </style>