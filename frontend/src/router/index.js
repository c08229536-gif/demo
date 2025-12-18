import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'
import CourseList from '../views/CourseList.vue'
import UserFeedback from '../views/UserFeedback.vue'
import AdminFeedback from '../views/AdminFeedback.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: LoginView
    },
    {
      path: '/home',
      name: 'home',
      component: HomeView,
      children: [
        // 1. 课程列表
        {
          path: 'courses',
          component: CourseList
        },
        // 2. 课程详情
        {
          path: 'course/:id',
          name: 'course-detail',
          component: () => import('../views/CourseDetail.vue')
        },
        // 3. 个人中心
        { 
          path: 'profile', 
          component: () => import('../views/ProfileView.vue') 
        },
        // 4. 我的课程 (学生)
        { 
          path: 'my-courses', 
          component: () => import('../views/MyCourse.vue') 
        },
        
        // 👇 5. 我的作业 (修正：改为单数，匹配菜单 /home/my-assignment)
        { 
          path: 'my-assignment', 
          component: () => import('../views/MyAssignment.vue') 
        },
        
        // 👇 6. 作业批改 (修正：改为全称，匹配菜单 /home/grade-assignment)
        { 
          path: 'grade-assignment', 
          component: () => import('../views/GradeAssignment.vue')
        },
        
        // 👇 7. 用户管理 (修正：匹配菜单 /home/user-manage)
        {
          path: 'user-manage',
          component: () => import('../views/UserManage.vue')
        }, 
        
        // 👇 8. 课程审核 (修正：匹配菜单 /home/admin-audit)
        {
          path: 'admin-audit',
          component: () => import('../views/AdminCourseAudit.vue')
        },
        
        // 9. 意见反馈
        {
          path: 'feedback',
          component: UserFeedback
        },
        // 10. 反馈处理 (管理员)
        {
          path: 'admin-feedback',
          component: AdminFeedback
        }
      ]
    }
  ]
})

// 路由守卫：检查权限
router.beforeEach((to, from, next) => {
  const role = localStorage.getItem('userRole') 

  // 1. 管理员页面拦截 (路径关键词更新)
  if (to.path.includes('/admin-audit') || to.path.includes('/user-manage') || to.path.includes('/admin-feedback')) {
    if (role !== 'admin' && role !== 'ADMIN') {
      return next('/home/courses')
    }
  }

  // 2. 教师页面拦截 (路径关键词更新)
  if (to.path.includes('/grade-assignment')) { 
    if (role === 'student') {
      return next('/home/courses')
    }
  }

  next()
})

export default router