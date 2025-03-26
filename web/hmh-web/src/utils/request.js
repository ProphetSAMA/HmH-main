import { ElMessage } from 'element-plus'
import router from '../router'

// 创建请求工具函数
export const request = async (url, options = {}) => {
  try {
    // 获取 token
    const token = localStorage.getItem('token')
    console.log('Token from localStorage:', token)

    // 设置默认 headers
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers
    }

    // 如果有 token，添加到 headers
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }

    // 构建完整的请求配置
    const config = {
      ...options,
      headers
    }

    console.log('Request URL:', url)
    console.log('Request config:', config)

    const response = await fetch(url, config)
    console.log('Response status:', response.status)
    console.log('Response headers:', Object.fromEntries(response.headers.entries()))

    return await responseInterceptor(response)
  } catch (error) {
    console.error('Request error:', error)
    // 抛出错误，让调用方处理具体的错误信息
    throw error
  }
}

// 响应拦截器
const responseInterceptor = async (response) => {
  const data = await response.json()
  
  // 处理 401 错误
  if (response.status === 401) {
    localStorage.removeItem('token')
    localStorage.removeItem('currentUser')
    router.push('/login')
    throw new Error(data.message)
  }

  // 处理业务错误
  if (!data.success) {
    throw new Error(data.message || '请求失败')
  }

  return data
} 