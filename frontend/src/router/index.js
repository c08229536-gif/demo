import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'
import CourseList from '../views/CourseList.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login', // 默认首页就是登录页
      component: LoginView
    },
    // 2. 新增这个路由
    {
      path: '/home',
      name: 'home',
      component: HomeView,
      children: [
        {
          path: 'courses', // 注意：这里不要写 /，它是接着父路径的
          component: CourseList
        },
        {
          path: 'course/:id',  // :id 表示这是一个动态参数
          name: 'course-detail',
          component: () => import('../views/CourseDetail.vue')
        },
        { 
          path: 'my-courses', 
          component: () => import('../views/MyCourse.vue') 
        },
        { 
          path: 'my-assignments', 
          component: () => import('../views/MyAssignment.vue') 
        },
        { 
          path: 'profile', 
          component: () => import('../views/ProfileView.vue') 
        },
        { path: 'grade', 
          component: () => import('../views/GradeAssignment.vue')
        },
        {
          path: 'users',
          component: () => import('../views/UserManage.vue')
        }, 
        {
          path: 'audit',
          component: () => import('../views/AdminCourseAudit.vue')
        }
      ]
    }
  ]
})
router.beforeEach((to, from, next) => {
  const role = localStorage.getItem('userRole') // 登录时我们存过这个

  // 1. 如果去管理员页面，但身份不是 admin -> 踢回首页
  if (to.path.includes('/audit') || to.path.includes('/users')) {
    if (role !== 'admin' && role !== 'ADMIN') {
      return next('/home/courses')
    }
  }

  // 2. 如果去教师批改页，但身份是学生 -> 踢回首页
  if (to.path.includes('/grade') || to.path.includes('/add')) { // 假设发布页叫 add
    if (role === 'student') {
      return next('/home/courses')
    }
  }

  next()
})
export default router