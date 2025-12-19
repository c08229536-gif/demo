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
        // 1. 课程中心
        {
          path: 'courses',
          component: CourseList
        },
        // 2. 课程详情 (包含学习进度追踪逻辑)
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
        // 5. 在线考试 (学生 - 新增)
        {
          path: 'exam/:id',
          name: 'exam-view',
          component: () => import('../views/ExamView.vue')
        },
        // 6. 我的作业
        { 
          path: 'my-assignment', 
          component: () => import('../views/MyAssignment.vue') 
        },
        // 7. 作业批改 (教师)
        { 
          path: 'grade-assignment', 
          component: () => import('../views/GradeAssignment.vue')
        },
        // 8. 用户管理 (管理员)
        {
          path: 'user-manage',
          component: () => import('../views/UserManage.vue')
        }, 
        // 9. 课程审核 (管理员)
        {
          path: 'admin-audit',
          component: () => import('../views/AdminCourseAudit.vue')
        },
        // 10. 意见反馈
        {
          path: 'feedback',
          component: UserFeedback
        },
        // 11. 反馈处理 (管理员)
        {
          path: 'admin-feedback',
          component: AdminFeedback
        },
        // 12. 首页运营管理 (管理员 - 新增)
        {
          path: 'banner-manage',
          component: () => import('../views/BannerManage.vue')
        },
        // 13. 系统操作日志 (管理员 - 新增)
        {
          path: 'log-manage',
          component: () => import('../views/LogManage.vue')
        },
        { 
          path: 'exam-manage', 
          component: () => import('../views/ExamManage.vue') 
        }
      ]
    }
  ]
})

// 路由守卫：检查权限
router.beforeEach((to, from, next) => {
  const role = localStorage.getItem('userRole') 

  // 管理员页面拦截 (包含新增的运营和日志)
  const adminRoutes = ['/admin-audit', '/user-manage', '/admin-feedback', '/banner-manage', '/log-manage']
  if (adminRoutes.some(path => to.path.includes(path))) {
    if (role !== 'admin' && role !== 'ADMIN') {
      return next('/home/courses')
    }
  }

  // 教师页面拦截
  if (to.path.includes('/grade-assignment')) { 
    if (role === 'student') {
      return next('/home/courses')
    }
  }

  next()
})

export default router