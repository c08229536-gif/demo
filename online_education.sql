/*
 Navicat Premium Dump SQL

 Source Server         : crf
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : online_education

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 28/12/2025 17:29:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for assignment
-- ----------------------------
DROP TABLE IF EXISTS `assignment`;
CREATE TABLE `assignment`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作业标题',
  `course_id` int NOT NULL COMMENT '所属课程ID',
  `deadline` datetime NULL DEFAULT NULL COMMENT '截止时间',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of assignment
-- ----------------------------
INSERT INTO `assignment` VALUES (1, '完成 Java 课后习题', 1, '2025-12-31 23:59:59', '待提交', '请完成第一章的所有选择题');
INSERT INTO `assignment` VALUES (2, '撰写 Vue 学习心得', 1, '2025-10-01 12:00:00', '待提交', '不少于500字');
INSERT INTO `assignment` VALUES (3, 'Python 数据清洗实战', 2, '2025-11-11 00:00:00', '待提交', '使用 Pandas 处理 csv 文件');
INSERT INTO `assignment` VALUES (4, '完成第一章作业', 1, '2025-12-16 21:05:04', NULL, '100字');
INSERT INTO `assignment` VALUES (5, '1111', 1, '2025-12-25 00:00:00', NULL, '112222');

-- ----------------------------
-- Table structure for chapter
-- ----------------------------
DROP TABLE IF EXISTS `chapter`;
CREATE TABLE `chapter`  (
  `chapter_id` int NOT NULL AUTO_INCREMENT,
  `course_id` int NOT NULL COMMENT '所属课程ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '章节标题',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `video_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sort_order` int NULL DEFAULT 0 COMMENT '排序(第几章)',
  PRIMARY KEY (`chapter_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chapter
-- ----------------------------
INSERT INTO `chapter` VALUES (1, 1, '第一章：Java历史', 'Java的诞生与发展', 'https://media.w3.org/2010/05/sintel/trailer.mp4', 1);
INSERT INTO `chapter` VALUES (2, 1, '第二章：环境配置', 'JDK安装与IDEA配置', 'https://vjs.zencdn.net/v/oceans.mp4', 2);
INSERT INTO `chapter` VALUES (3, 1, 'Java入门', '', 'https://vjs.zencdn.net/v/oceans.mp4', 1);

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `course_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程标题',
  `teacher` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '讲师',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` int NULL DEFAULT 0 COMMENT '状态：0-待审核，1-已发布，2-已驳回',
  `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `teacher_id` int NULL DEFAULT NULL,
  `price` decimal(38, 2) NOT NULL,
  PRIMARY KEY (`course_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, 'Java 高级编程', '张教授', '/uploads/96b105f2-dad6-4bd3-85fe-04e27f4c7364.jpeg', '深入理解 JVM 原理', 1, '后端', NULL, 0.00);
INSERT INTO `course` VALUES (2, 'Python 数据分析', '李博士', '/uploads/0563b2db-8522-454a-97b3-ac3013e16734.png', 'Pandas 与 NumPy 实战', 1, 'AI', NULL, 0.00);
INSERT INTO `course` VALUES (3, 'Vue3 全栈开发', '王大神', '/uploads/508adb34-27bd-4958-9538-48b27b8806a2.jpg', '从入门到精通', 1, '前端', NULL, 0.00);
INSERT INTO `course` VALUES (4, '测试课程', '我', '/uploads/46eb1054-f7c5-4225-9fa4-806746cce410.jpeg', '你好你好', 1, '其他', NULL, 0.00);
INSERT INTO `course` VALUES (5, 'Python进阶', '陈健宇', '/uploads/f4155775-8b6b-4e95-8723-a73e77a9e956.jpg', 'glagame男主', 1, 'AI', NULL, 0.00);
INSERT INTO `course` VALUES (6, '软件设计', '陈坤', '/uploads/9eef4f13-656a-45ea-951c-3c09d1380d47.jpg', '', 1, '移动端', NULL, 0.00);
INSERT INTO `course` VALUES (7, '测试', '111', '', '', 1, '后端', NULL, 0.00);
INSERT INTO `course` VALUES (8, '22', '111111', '', '', 2, '后端', NULL, 0.00);

-- ----------------------------
-- Table structure for course_progress
-- ----------------------------
DROP TABLE IF EXISTS `course_progress`;
CREATE TABLE `course_progress`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `chapter_id` bigint NOT NULL COMMENT '章节ID',
  `progress_percent` int NULL DEFAULT 0 COMMENT '进度百分比',
  `is_finished` int NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_progress
-- ----------------------------

-- ----------------------------
-- Table structure for course_resource
-- ----------------------------
DROP TABLE IF EXISTS `course_resource`;
CREATE TABLE `course_resource`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `course_id` int NOT NULL COMMENT '所属课程',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `file_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_resource
-- ----------------------------
INSERT INTO `course_resource` VALUES (1, 3, '测试ppt', 'https://pan.baidu.com/s/1LTSzfRpj9FNKb8-frVQK3Q?pwd=w3e6 提取码: w3e6 复制这段内容后打开百度网盘手机App，操作更方便哦', 'pdf', '2025-12-16 16:22:24');
INSERT INTO `course_resource` VALUES (2, 1, '02-第三方库下载', '/uploads/f31c08b7-4a4d-4ed5-8c14-cf90073303fe.pdf', 'pdf', '2025-12-16 16:35:35');

-- ----------------------------
-- Table structure for course_review
-- ----------------------------
DROP TABLE IF EXISTS `course_review`;
CREATE TABLE `course_review`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `course_id` int NOT NULL COMMENT '课程ID',
  `student_id` int NOT NULL COMMENT '学生ID',
  `rating` int NOT NULL COMMENT '评分(1-5)',
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_review
-- ----------------------------
INSERT INTO `course_review` VALUES (1, 1, 5, 4, '还行', '2025-12-17 14:09:23');

-- ----------------------------
-- Table structure for exam
-- ----------------------------
DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '考试标题',
  `course_id` bigint NULL DEFAULT NULL COMMENT '关联课程ID',
  `total_score` int NULL DEFAULT 100 COMMENT '总分',
  `duration` int NULL DEFAULT NULL COMMENT '考试时长(分钟)',
  `create_time` datetime(6) NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `end_time` datetime(6) NULL DEFAULT NULL,
  `start_time` datetime(6) NULL DEFAULT NULL,
  `state` int NULL DEFAULT NULL,
  `teacher_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam
-- ----------------------------

-- ----------------------------
-- Table structure for exam_question
-- ----------------------------
DROP TABLE IF EXISTS `exam_question`;
CREATE TABLE `exam_question`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_id` bigint NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '题目内容',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '选项JSON',
  `answer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `score` int NULL DEFAULT NULL COMMENT '分值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_question
-- ----------------------------

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `course_id` int NOT NULL COMMENT '所属课程',
  `student_id` int NOT NULL COMMENT '提问学生',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `reply` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提问时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feedback
-- ----------------------------
INSERT INTO `feedback` VALUES (1, 1, 5, '救救我', NULL, '2025-12-15 19:51:52');
INSERT INTO `feedback` VALUES (2, 1, 5, '怎么搞啊', '问题发我', '2025-12-15 19:52:00');
INSERT INTO `feedback` VALUES (3, 1, 5, '你好', NULL, '2025-12-18 20:51:32');

-- ----------------------------
-- Table structure for home_banner
-- ----------------------------
DROP TABLE IF EXISTS `home_banner`;
CREATE TABLE `home_banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `link_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sort_order` int NULL DEFAULT 0,
  `is_active` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of home_banner
-- ----------------------------
INSERT INTO `home_banner` VALUES (1, '11', 'https://files.imagetourl.net/uploads/1766133048499-4324f65e-79a2-4eb4-a37d-8ff7ac82db3e.jpeg', '', 0, 1);
INSERT INTO `home_banner` VALUES (2, '22', '/uploads/6947b70f-637f-440c-a338-257a527998aa.jpeg', '', 0, 1);
INSERT INTO `home_banner` VALUES (3, '33', '/uploads/03b1be38-7810-4ac7-8e8c-9359bc433743.jpeg', '', 0, 1);

-- ----------------------------
-- Table structure for homework_submission
-- ----------------------------
DROP TABLE IF EXISTS `homework_submission`;
CREATE TABLE `homework_submission`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `assignment_id` int NOT NULL COMMENT '作业ID',
  `student_id` int NOT NULL COMMENT '学生ID',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `submit_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `score` int NULL DEFAULT NULL COMMENT '分数',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `feedback` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of homework_submission
-- ----------------------------
INSERT INTO `homework_submission` VALUES (1, 1, 5, '你好你好你好', '2025-12-14 12:49:04', 99, NULL, NULL, NULL);
INSERT INTO `homework_submission` VALUES (2, 5, 5, '我来了', '2025-12-14 21:12:16', 80, NULL, NULL, NULL);
INSERT INTO `homework_submission` VALUES (3, 2, 5, '奶粉里都不能发公屏胡闹日读过后偶然很骚返回弄还oihnseroghgohaw】awgh\'rsjg哇好几个、\n啊、\ngrphjgvbapragwrhgparwgawhegjrpaogjaw郭文贵\n见公婆数据、\n、j【sejgvpsrizhgvw、\nwergjaperwghjwjgrpjeawr根据23【竟然3-九1迫击炮i哦好精辟后i好呢日哦和皮偶hi哦和噢ihou、', '2025-12-15 19:25:43', 85, NULL, NULL, NULL);
INSERT INTO `homework_submission` VALUES (4, 3, 5, '啊啊老妇女了你覅偶尔瓦活佛i怀柔i凤凰男破i害怕2312人佛坪1；欧派让他', '2025-12-18 21:00:02', 86, NULL, '继续加油', '已批改');

-- ----------------------------
-- Table structure for learning_record
-- ----------------------------
DROP TABLE IF EXISTS `learning_record`;
CREATE TABLE `learning_record`  (
  `record_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '学生ID',
  `course_id` int NOT NULL COMMENT '课程ID',
  `chapter_id` int NOT NULL COMMENT '章节ID',
  `status` int NULL DEFAULT 0 COMMENT '状态：0-未完成，1-已完成',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  UNIQUE INDEX `idx_user_chapter`(`user_id` ASC, `chapter_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of learning_record
-- ----------------------------
INSERT INTO `learning_record` VALUES (1, 5, 1, 1, 1, '2025-12-14 23:54:00');
INSERT INTO `learning_record` VALUES (2, 5, 1, 3, 1, '2025-12-15 00:01:12');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(38, 2) NULL DEFAULT NULL,
  `course_id` int NULL DEFAULT NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `user_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 0.00, 6, '2025-12-19 00:30:48.425845', '63a4a939-5702-462b-93d8-4a2cb8af8f51', 5);

-- ----------------------------
-- Table structure for student_course
-- ----------------------------
DROP TABLE IF EXISTS `student_course`;
CREATE TABLE `student_course`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '学生ID',
  `course_id` int NOT NULL COMMENT '课程ID',
  `enroll_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_course
-- ----------------------------
INSERT INTO `student_course` VALUES (1, 5, 1, '2025-12-13 23:43:51');
INSERT INTO `student_course` VALUES (2, 5, 2, '2025-12-14 00:03:50');
INSERT INTO `student_course` VALUES (3, 5, 3, '2025-12-14 00:03:53');
INSERT INTO `student_course` VALUES (4, 5, 4, '2025-12-14 18:37:34');
INSERT INTO `student_course` VALUES (5, 7, 4, '2025-12-14 18:38:04');
INSERT INTO `student_course` VALUES (6, 5, 6, '2025-12-19 00:30:48');

-- ----------------------------
-- Table structure for sys_feedback
-- ----------------------------
DROP TABLE IF EXISTS `sys_feedback`;
CREATE TABLE `sys_feedback`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '提交人ID',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `contact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `reply` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_feedback
-- ----------------------------
INSERT INTO `sys_feedback` VALUES (1, 5, '建议', '111', '', '已回复', '何事', '2025-12-19 00:35:24');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `operation` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作描述',
  `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求方法',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求参数',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 292 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_log
-- ----------------------------
INSERT INTO `sys_log` VALUES (1, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:36');
INSERT INTO `sys_log` VALUES (2, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:37');
INSERT INTO `sys_log` VALUES (3, 'Admin', 'getMyMessages', 'com.education.backend.controller.MessageController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:37');
INSERT INTO `sys_log` VALUES (4, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:37');
INSERT INTO `sys_log` VALUES (5, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:37');
INSERT INTO `sys_log` VALUES (6, 'Admin', 'getMyCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:39');
INSERT INTO `sys_log` VALUES (7, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:40');
INSERT INTO `sys_log` VALUES (8, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:40');
INSERT INTO `sys_log` VALUES (9, 'Admin', 'getMyCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:44');
INSERT INTO `sys_log` VALUES (10, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:46');
INSERT INTO `sys_log` VALUES (11, 'Admin', 'getMyCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:46');
INSERT INTO `sys_log` VALUES (12, 'Admin', 'getMyAssignments', 'com.education.backend.controller.AssignmentController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:46');
INSERT INTO `sys_log` VALUES (13, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:46');
INSERT INTO `sys_log` VALUES (14, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:46');
INSERT INTO `sys_log` VALUES (15, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:57');
INSERT INTO `sys_log` VALUES (16, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:57');
INSERT INTO `sys_log` VALUES (17, 'Admin', 'getMyMessages', 'com.education.backend.controller.MessageController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:57');
INSERT INTO `sys_log` VALUES (18, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:57');
INSERT INTO `sys_log` VALUES (19, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:23:57');
INSERT INTO `sys_log` VALUES (20, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:27:18');
INSERT INTO `sys_log` VALUES (21, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:27:18');
INSERT INTO `sys_log` VALUES (22, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:30');
INSERT INTO `sys_log` VALUES (23, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:30');
INSERT INTO `sys_log` VALUES (24, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:31');
INSERT INTO `sys_log` VALUES (25, 'Admin', 'getCourseById', 'com.education.backend.controller.CourseController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:31');
INSERT INTO `sys_log` VALUES (26, 'Admin', 'getResources', 'com.education.backend.controller.CourseResourceController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:31');
INSERT INTO `sys_log` VALUES (27, 'Admin', 'getCourseChapters', 'com.education.backend.controller.CourseController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:31');
INSERT INTO `sys_log` VALUES (28, 'Admin', 'getCourseFeedback', 'com.education.backend.controller.FeedbackController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:31');
INSERT INTO `sys_log` VALUES (29, 'Admin', 'getReviews', 'com.education.backend.controller.ReviewController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:31');
INSERT INTO `sys_log` VALUES (30, 'Admin', 'getCourseStudents', 'com.education.backend.controller.CourseController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:31');
INSERT INTO `sys_log` VALUES (31, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:37');
INSERT INTO `sys_log` VALUES (32, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:37');
INSERT INTO `sys_log` VALUES (33, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:38');
INSERT INTO `sys_log` VALUES (34, 'Admin', 'getCourseById', 'com.education.backend.controller.CourseController', '[3]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:38');
INSERT INTO `sys_log` VALUES (35, 'Admin', 'getReviews', 'com.education.backend.controller.ReviewController', '[3]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:38');
INSERT INTO `sys_log` VALUES (36, 'Admin', 'getResources', 'com.education.backend.controller.CourseResourceController', '[3]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:38');
INSERT INTO `sys_log` VALUES (37, 'Admin', 'getCourseChapters', 'com.education.backend.controller.CourseController', '[3]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:38');
INSERT INTO `sys_log` VALUES (38, 'Admin', 'getCourseFeedback', 'com.education.backend.controller.FeedbackController', '[3]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:39');
INSERT INTO `sys_log` VALUES (39, 'Admin', 'getCourseStudents', 'com.education.backend.controller.CourseController', '[3]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:39');
INSERT INTO `sys_log` VALUES (40, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:40');
INSERT INTO `sys_log` VALUES (41, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:40');
INSERT INTO `sys_log` VALUES (42, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:41');
INSERT INTO `sys_log` VALUES (43, 'Admin', 'getCourseById', 'com.education.backend.controller.CourseController', '[6]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:41');
INSERT INTO `sys_log` VALUES (44, 'Admin', 'getCourseChapters', 'com.education.backend.controller.CourseController', '[6]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:41');
INSERT INTO `sys_log` VALUES (45, 'Admin', 'getCourseFeedback', 'com.education.backend.controller.FeedbackController', '[6]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:41');
INSERT INTO `sys_log` VALUES (46, 'Admin', 'getResources', 'com.education.backend.controller.CourseResourceController', '[6]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:41');
INSERT INTO `sys_log` VALUES (47, 'Admin', 'getReviews', 'com.education.backend.controller.ReviewController', '[6]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:41');
INSERT INTO `sys_log` VALUES (48, 'Admin', 'getCourseStudents', 'com.education.backend.controller.CourseController', '[6]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:41');
INSERT INTO `sys_log` VALUES (49, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:43');
INSERT INTO `sys_log` VALUES (50, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:43');
INSERT INTO `sys_log` VALUES (51, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:44');
INSERT INTO `sys_log` VALUES (52, 'Admin', 'getCourseById', 'com.education.backend.controller.CourseController', '[5]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:44');
INSERT INTO `sys_log` VALUES (53, 'Admin', 'getResources', 'com.education.backend.controller.CourseResourceController', '[5]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:44');
INSERT INTO `sys_log` VALUES (54, 'Admin', 'getCourseChapters', 'com.education.backend.controller.CourseController', '[5]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:44');
INSERT INTO `sys_log` VALUES (55, 'Admin', 'getReviews', 'com.education.backend.controller.ReviewController', '[5]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:44');
INSERT INTO `sys_log` VALUES (56, 'Admin', 'getCourseFeedback', 'com.education.backend.controller.FeedbackController', '[5]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:44');
INSERT INTO `sys_log` VALUES (57, 'Admin', 'getCourseStudents', 'com.education.backend.controller.CourseController', '[5]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:44');
INSERT INTO `sys_log` VALUES (58, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:45');
INSERT INTO `sys_log` VALUES (59, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:45');
INSERT INTO `sys_log` VALUES (60, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:45');
INSERT INTO `sys_log` VALUES (61, 'Admin', 'getCourseById', 'com.education.backend.controller.CourseController', '[4]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:45');
INSERT INTO `sys_log` VALUES (62, 'Admin', 'getCourseChapters', 'com.education.backend.controller.CourseController', '[4]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:46');
INSERT INTO `sys_log` VALUES (63, 'Admin', 'getCourseFeedback', 'com.education.backend.controller.FeedbackController', '[4]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:46');
INSERT INTO `sys_log` VALUES (64, 'Admin', 'getResources', 'com.education.backend.controller.CourseResourceController', '[4]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:46');
INSERT INTO `sys_log` VALUES (65, 'Admin', 'getReviews', 'com.education.backend.controller.ReviewController', '[4]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:46');
INSERT INTO `sys_log` VALUES (66, 'Admin', 'getCourseStudents', 'com.education.backend.controller.CourseController', '[4]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:46');
INSERT INTO `sys_log` VALUES (67, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:48');
INSERT INTO `sys_log` VALUES (68, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:48');
INSERT INTO `sys_log` VALUES (69, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:49');
INSERT INTO `sys_log` VALUES (70, 'Admin', 'getCourseById', 'com.education.backend.controller.CourseController', '[2]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:49');
INSERT INTO `sys_log` VALUES (71, 'Admin', 'getResources', 'com.education.backend.controller.CourseResourceController', '[2]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:49');
INSERT INTO `sys_log` VALUES (72, 'Admin', 'getCourseChapters', 'com.education.backend.controller.CourseController', '[2]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:49');
INSERT INTO `sys_log` VALUES (73, 'Admin', 'getCourseFeedback', 'com.education.backend.controller.FeedbackController', '[2]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:49');
INSERT INTO `sys_log` VALUES (74, 'Admin', 'getReviews', 'com.education.backend.controller.ReviewController', '[2]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:49');
INSERT INTO `sys_log` VALUES (75, 'Admin', 'getCourseStudents', 'com.education.backend.controller.CourseController', '[2]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:49');
INSERT INTO `sys_log` VALUES (76, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:57');
INSERT INTO `sys_log` VALUES (77, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:31:57');
INSERT INTO `sys_log` VALUES (78, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:32:47');
INSERT INTO `sys_log` VALUES (79, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:32:48');
INSERT INTO `sys_log` VALUES (80, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:32:48');
INSERT INTO `sys_log` VALUES (81, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:32:49');
INSERT INTO `sys_log` VALUES (83, 'Admin', 'getPendingCourses', 'com.education.backend.controller.AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:32:53');
INSERT INTO `sys_log` VALUES (84, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:32:54');
INSERT INTO `sys_log` VALUES (86, 'Admin', 'getPendingCourses', 'com.education.backend.controller.AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:32:55');
INSERT INTO `sys_log` VALUES (87, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:32:56');
INSERT INTO `sys_log` VALUES (89, 'Admin', 'getPendingCourses', 'com.education.backend.controller.AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:33:00');
INSERT INTO `sys_log` VALUES (90, 'Admin', 'getAllFeedback', 'com.education.backend.controller.SysFeedbackController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:33:00');
INSERT INTO `sys_log` VALUES (91, 'Admin', 'getAllFeedback', 'com.education.backend.controller.SysFeedbackController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:33:02');
INSERT INTO `sys_log` VALUES (92, 'Admin', 'getAllFeedback', 'com.education.backend.controller.SysFeedbackController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:33:03');
INSERT INTO `sys_log` VALUES (93, 'Admin', 'getPendingCourses', 'com.education.backend.controller.AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:33:04');
INSERT INTO `sys_log` VALUES (94, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:33:04');
INSERT INTO `sys_log` VALUES (96, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:27');
INSERT INTO `sys_log` VALUES (97, 'Admin', 'getMyMessages', 'com.education.backend.controller.MessageController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:27');
INSERT INTO `sys_log` VALUES (98, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:27');
INSERT INTO `sys_log` VALUES (99, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:27');
INSERT INTO `sys_log` VALUES (100, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:27');
INSERT INTO `sys_log` VALUES (101, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:30');
INSERT INTO `sys_log` VALUES (102, 'Admin', 'getCourseById', 'com.education.backend.controller.CourseController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:30');
INSERT INTO `sys_log` VALUES (103, 'Admin', 'getCourseChapters', 'com.education.backend.controller.CourseController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:30');
INSERT INTO `sys_log` VALUES (104, 'Admin', 'getResources', 'com.education.backend.controller.CourseResourceController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:30');
INSERT INTO `sys_log` VALUES (105, 'Admin', 'getReviews', 'com.education.backend.controller.ReviewController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:30');
INSERT INTO `sys_log` VALUES (106, 'Admin', 'getCourseFeedback', 'com.education.backend.controller.FeedbackController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:30');
INSERT INTO `sys_log` VALUES (107, 'Admin', 'getCourseStudents', 'com.education.backend.controller.CourseController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:30');
INSERT INTO `sys_log` VALUES (108, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:40');
INSERT INTO `sys_log` VALUES (109, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:40');
INSERT INTO `sys_log` VALUES (110, 'Admin', 'getAllAssignments', 'com.education.backend.controller.AssignmentController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:43');
INSERT INTO `sys_log` VALUES (111, 'Admin', 'getSubmissions', 'com.education.backend.controller.AssignmentController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:44');
INSERT INTO `sys_log` VALUES (112, 'Admin', 'getSubmissions', 'com.education.backend.controller.AssignmentController', '[2]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:44');
INSERT INTO `sys_log` VALUES (113, 'Admin', 'getSubmissions', 'com.education.backend.controller.AssignmentController', '[3]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:45');
INSERT INTO `sys_log` VALUES (114, 'Admin', 'getMyFeedback', 'com.education.backend.controller.SysFeedbackController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:45');
INSERT INTO `sys_log` VALUES (115, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:46');
INSERT INTO `sys_log` VALUES (116, 'Admin', 'getMyAssignments', 'com.education.backend.controller.AssignmentController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:46');
INSERT INTO `sys_log` VALUES (117, 'Admin', 'getMyCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:46');
INSERT INTO `sys_log` VALUES (118, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:47');
INSERT INTO `sys_log` VALUES (119, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:47');
INSERT INTO `sys_log` VALUES (120, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:51');
INSERT INTO `sys_log` VALUES (121, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:51');
INSERT INTO `sys_log` VALUES (122, 'Admin', 'getMyMessages', 'com.education.backend.controller.MessageController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:51');
INSERT INTO `sys_log` VALUES (123, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:51');
INSERT INTO `sys_log` VALUES (124, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:51');
INSERT INTO `sys_log` VALUES (125, 'Admin', 'getMyCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:34:57');
INSERT INTO `sys_log` VALUES (126, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:05');
INSERT INTO `sys_log` VALUES (127, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:05');
INSERT INTO `sys_log` VALUES (128, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:07');
INSERT INTO `sys_log` VALUES (129, 'Admin', 'getMyCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:07');
INSERT INTO `sys_log` VALUES (130, 'Admin', 'getMyAssignments', 'com.education.backend.controller.AssignmentController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:07');
INSERT INTO `sys_log` VALUES (131, 'Admin', 'getMyCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:09');
INSERT INTO `sys_log` VALUES (132, 'Admin', 'getMyCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:11');
INSERT INTO `sys_log` VALUES (133, 'Admin', 'getMyCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:13');
INSERT INTO `sys_log` VALUES (134, 'Admin', 'getMyAssignments', 'com.education.backend.controller.AssignmentController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:16');
INSERT INTO `sys_log` VALUES (135, 'Admin', 'getMyCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:17');
INSERT INTO `sys_log` VALUES (136, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:18');
INSERT INTO `sys_log` VALUES (137, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:18');
INSERT INTO `sys_log` VALUES (138, 'Admin', 'getMyCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:19');
INSERT INTO `sys_log` VALUES (139, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:20');
INSERT INTO `sys_log` VALUES (140, 'Admin', 'getCourseById', 'com.education.backend.controller.CourseController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:20');
INSERT INTO `sys_log` VALUES (141, 'Admin', 'getResources', 'com.education.backend.controller.CourseResourceController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:20');
INSERT INTO `sys_log` VALUES (142, 'Admin', 'getCourseChapters', 'com.education.backend.controller.CourseController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:20');
INSERT INTO `sys_log` VALUES (143, 'Admin', 'getReviews', 'com.education.backend.controller.ReviewController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:20');
INSERT INTO `sys_log` VALUES (144, 'Admin', 'getCourseProgress', 'com.education.backend.controller.CourseController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:20');
INSERT INTO `sys_log` VALUES (145, 'Admin', 'getCourseFeedback', 'com.education.backend.controller.FeedbackController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:20');
INSERT INTO `sys_log` VALUES (146, 'Admin', 'checkEnrollStatus', 'com.education.backend.controller.CourseController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:20');
INSERT INTO `sys_log` VALUES (147, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:20');
INSERT INTO `sys_log` VALUES (148, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:28');
INSERT INTO `sys_log` VALUES (149, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:28');
INSERT INTO `sys_log` VALUES (150, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:29');
INSERT INTO `sys_log` VALUES (151, 'Admin', 'getCourseById', 'com.education.backend.controller.CourseController', '[2]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:29');
INSERT INTO `sys_log` VALUES (152, 'Admin', 'getCourseChapters', 'com.education.backend.controller.CourseController', '[2]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:29');
INSERT INTO `sys_log` VALUES (153, 'Admin', 'getCourseFeedback', 'com.education.backend.controller.FeedbackController', '[2]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:29');
INSERT INTO `sys_log` VALUES (154, 'Admin', 'getReviews', 'com.education.backend.controller.ReviewController', '[2]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:29');
INSERT INTO `sys_log` VALUES (155, 'Admin', 'getResources', 'com.education.backend.controller.CourseResourceController', '[2]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:29');
INSERT INTO `sys_log` VALUES (156, 'Admin', 'checkEnrollStatus', 'com.education.backend.controller.CourseController', '[2]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:29');
INSERT INTO `sys_log` VALUES (157, 'Admin', 'getCourseProgress', 'com.education.backend.controller.CourseController', '[2]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:29');
INSERT INTO `sys_log` VALUES (158, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:29');
INSERT INTO `sys_log` VALUES (159, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:31');
INSERT INTO `sys_log` VALUES (160, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:31');
INSERT INTO `sys_log` VALUES (161, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:40');
INSERT INTO `sys_log` VALUES (162, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:40');
INSERT INTO `sys_log` VALUES (163, 'Admin', 'getMyMessages', 'com.education.backend.controller.MessageController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:40');
INSERT INTO `sys_log` VALUES (164, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:40');
INSERT INTO `sys_log` VALUES (165, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:35:40');
INSERT INTO `sys_log` VALUES (166, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:38:48');
INSERT INTO `sys_log` VALUES (167, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:38:49');
INSERT INTO `sys_log` VALUES (168, 'Admin', 'getMyMessages', 'com.education.backend.controller.MessageController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:38:49');
INSERT INTO `sys_log` VALUES (169, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:38:49');
INSERT INTO `sys_log` VALUES (170, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:38:49');
INSERT INTO `sys_log` VALUES (171, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:38:51');
INSERT INTO `sys_log` VALUES (173, 'Admin', 'addUser', 'com.education.backend.controller.AdminController', '[{username=t002, realName=叮叮当, role=teacher}]', '0:0:0:0:0:0:0:1', '2025-12-19 16:39:48');
INSERT INTO `sys_log` VALUES (175, 'Admin', 'getAllFeedback', 'com.education.backend.controller.SysFeedbackController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:48:30');
INSERT INTO `sys_log` VALUES (176, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 16:48:32');
INSERT INTO `sys_log` VALUES (178, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:02:55');
INSERT INTO `sys_log` VALUES (179, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:02:55');
INSERT INTO `sys_log` VALUES (180, 'Admin', 'getMyMessages', 'com.education.backend.controller.MessageController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:02:55');
INSERT INTO `sys_log` VALUES (181, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:02:55');
INSERT INTO `sys_log` VALUES (182, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:02:55');
INSERT INTO `sys_log` VALUES (183, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:02:57');
INSERT INTO `sys_log` VALUES (184, 'Admin', 'getAllUsers', 'com.education.backend.controller.AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:02:57');
INSERT INTO `sys_log` VALUES (185, 'Admin', 'getAllFeedback', 'com.education.backend.controller.SysFeedbackController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:03:00');
INSERT INTO `sys_log` VALUES (186, 'Admin', 'getLogs', 'com.education.backend.controller.AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:03:01');
INSERT INTO `sys_log` VALUES (187, 'Admin', 'getLogs', 'com.education.backend.controller.AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:03:01');
INSERT INTO `sys_log` VALUES (188, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:03:02');
INSERT INTO `sys_log` VALUES (189, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:03:03');
INSERT INTO `sys_log` VALUES (190, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:03:03');
INSERT INTO `sys_log` VALUES (191, 'Admin', 'getLogs', 'com.education.backend.controller.AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:03:04');
INSERT INTO `sys_log` VALUES (192, 'Admin', 'getLogs', 'com.education.backend.controller.AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:03:04');
INSERT INTO `sys_log` VALUES (193, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:03:30');
INSERT INTO `sys_log` VALUES (194, 'Admin', 'getAllUsers', 'com.education.backend.controller.AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:03:30');
INSERT INTO `sys_log` VALUES (195, 'Admin', 'getAllFeedback', 'com.education.backend.controller.SysFeedbackController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:03:31');
INSERT INTO `sys_log` VALUES (196, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:03:32');
INSERT INTO `sys_log` VALUES (197, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:03:32');
INSERT INTO `sys_log` VALUES (198, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:39');
INSERT INTO `sys_log` VALUES (199, 'Admin', 'getMyMessages', 'com.education.backend.controller.MessageController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:39');
INSERT INTO `sys_log` VALUES (200, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:39');
INSERT INTO `sys_log` VALUES (201, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:39');
INSERT INTO `sys_log` VALUES (202, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:39');
INSERT INTO `sys_log` VALUES (203, 'Admin', 'getMyFeedback', 'com.education.backend.controller.SysFeedbackController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:42');
INSERT INTO `sys_log` VALUES (204, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:42');
INSERT INTO `sys_log` VALUES (205, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:42');
INSERT INTO `sys_log` VALUES (206, 'Admin', 'getMyFeedback', 'com.education.backend.controller.SysFeedbackController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:43');
INSERT INTO `sys_log` VALUES (207, 'Admin', 'getAllAssignments', 'com.education.backend.controller.AssignmentController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:43');
INSERT INTO `sys_log` VALUES (208, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:44');
INSERT INTO `sys_log` VALUES (209, 'Admin', 'getMyCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:44');
INSERT INTO `sys_log` VALUES (210, 'Admin', 'getMyAssignments', 'com.education.backend.controller.AssignmentController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:44');
INSERT INTO `sys_log` VALUES (211, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:45');
INSERT INTO `sys_log` VALUES (212, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:45');
INSERT INTO `sys_log` VALUES (213, 'Admin', 'getMyFeedback', 'com.education.backend.controller.SysFeedbackController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:46');
INSERT INTO `sys_log` VALUES (214, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:46');
INSERT INTO `sys_log` VALUES (215, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:46');
INSERT INTO `sys_log` VALUES (216, 'Admin', 'getMyFeedback', 'com.education.backend.controller.SysFeedbackController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:46');
INSERT INTO `sys_log` VALUES (217, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:47');
INSERT INTO `sys_log` VALUES (218, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:47');
INSERT INTO `sys_log` VALUES (219, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:48');
INSERT INTO `sys_log` VALUES (220, 'Admin', 'getCourseById', 'com.education.backend.controller.CourseController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:48');
INSERT INTO `sys_log` VALUES (221, 'Admin', 'getResources', 'com.education.backend.controller.CourseResourceController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:48');
INSERT INTO `sys_log` VALUES (222, 'Admin', 'getReviews', 'com.education.backend.controller.ReviewController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:48');
INSERT INTO `sys_log` VALUES (223, 'Admin', 'getCourseChapters', 'com.education.backend.controller.CourseController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:48');
INSERT INTO `sys_log` VALUES (224, 'Admin', 'getCourseStudents', 'com.education.backend.controller.CourseController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:48');
INSERT INTO `sys_log` VALUES (225, 'Admin', 'getCourseFeedback', 'com.education.backend.controller.FeedbackController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 17:04:48');
INSERT INTO `sys_log` VALUES (226, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:05:01');
INSERT INTO `sys_log` VALUES (227, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:05:01');
INSERT INTO `sys_log` VALUES (228, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:05:03');
INSERT INTO `sys_log` VALUES (229, 'Admin', 'getCourseById', 'com.education.backend.controller.CourseController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 17:05:03');
INSERT INTO `sys_log` VALUES (230, 'Admin', 'getCourseChapters', 'com.education.backend.controller.CourseController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 17:05:03');
INSERT INTO `sys_log` VALUES (231, 'Admin', 'getResources', 'com.education.backend.controller.CourseResourceController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 17:05:03');
INSERT INTO `sys_log` VALUES (232, 'Admin', 'getCourseFeedback', 'com.education.backend.controller.FeedbackController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 17:05:03');
INSERT INTO `sys_log` VALUES (233, 'Admin', 'getReviews', 'com.education.backend.controller.ReviewController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 17:05:03');
INSERT INTO `sys_log` VALUES (234, 'Admin', 'getCourseStudents', 'com.education.backend.controller.CourseController', '[1]', '0:0:0:0:0:0:0:1', '2025-12-19 17:05:03');
INSERT INTO `sys_log` VALUES (235, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:05:40');
INSERT INTO `sys_log` VALUES (236, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:05:41');
INSERT INTO `sys_log` VALUES (237, 'Admin', 'getMyMessages', 'com.education.backend.controller.MessageController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:05:41');
INSERT INTO `sys_log` VALUES (238, 'Admin', 'getAllCourses', 'com.education.backend.controller.CourseController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:05:41');
INSERT INTO `sys_log` VALUES (239, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:05:41');
INSERT INTO `sys_log` VALUES (240, 'Admin', 'getCurrentUserInfo', 'com.education.backend.controller.AuthController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:34:41');
INSERT INTO `sys_log` VALUES (241, 'Admin', 'getMyMessages', 'com.education.backend.controller.MessageController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 17:34:41');
INSERT INTO `sys_log` VALUES (242, 'AdminSystem', 'getAllUsers', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 18:21:27');
INSERT INTO `sys_log` VALUES (243, 'AdminSystem', 'getLogs', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 18:21:34');
INSERT INTO `sys_log` VALUES (244, 'AdminSystem', 'getPendingCourses', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 18:30:05');
INSERT INTO `sys_log` VALUES (245, 'AdminSystem', 'auditCourse', 'AdminController', '[{courseId=7, pass=true}]', '0:0:0:0:0:0:0:1', '2025-12-19 18:30:09');
INSERT INTO `sys_log` VALUES (246, 'AdminSystem', 'getPendingCourses', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 18:30:09');
INSERT INTO `sys_log` VALUES (247, 'AdminSystem', 'getLogs', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 18:30:16');
INSERT INTO `sys_log` VALUES (248, 'AdminSystem', 'getAllUsers', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 18:30:17');
INSERT INTO `sys_log` VALUES (249, 'AdminSystem', 'getLogs', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 18:31:42');
INSERT INTO `sys_log` VALUES (250, 'AdminSystem', 'getLogs', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 18:44:48');
INSERT INTO `sys_log` VALUES (251, 'AdminSystem', 'getPendingCourses', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 19:03:27');
INSERT INTO `sys_log` VALUES (252, 'AdminSystem', 'getPendingCourses', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 19:03:37');
INSERT INTO `sys_log` VALUES (253, 'AdminSystem', 'getPendingCourses', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 19:03:39');
INSERT INTO `sys_log` VALUES (254, 'AdminSystem', 'getPendingCourses', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 19:04:06');
INSERT INTO `sys_log` VALUES (255, 'AdminSystem', 'auditCourse', 'AdminController', '[{courseId=8, pass=false}]', '0:0:0:0:0:0:0:1', '2025-12-19 19:04:09');
INSERT INTO `sys_log` VALUES (256, 'AdminSystem', 'getPendingCourses', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-19 19:04:09');
INSERT INTO `sys_log` VALUES (257, 'AdminSystem', 'saveBanner', 'AdminController', '[HomeBanner(id=1, title=11, imageUrl=https://files.imagetourl.net/uploads/1766133048499-4324f65e-79a2-4eb4-a37d-8ff7ac82db3e.jpeg, linkUrl=, sortOrder=0, isActive=1)]', '0:0:0:0:0:0:0:1', '2025-12-19 19:05:55');
INSERT INTO `sys_log` VALUES (258, 'AdminSystem', 'saveBanner', 'AdminController', '[HomeBanner(id=2, title=22, imageUrl=b1555716d8e3297ed177d682b872b599.jpeg, linkUrl=, sortOrder=0, isActive=1)]', '0:0:0:0:0:0:0:1', '2025-12-28 16:31:12');
INSERT INTO `sys_log` VALUES (259, 'AdminSystem', 'getLogs', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:31:28');
INSERT INTO `sys_log` VALUES (260, 'AdminSystem', 'getLogs', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:40:33');
INSERT INTO `sys_log` VALUES (261, 'AdminSystem', 'getBanners', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:40:34');
INSERT INTO `sys_log` VALUES (262, 'AdminSystem', 'saveBanner', 'AdminController', '[HomeBanner(id=2, title=22, imageUrl=/uploads/6947b70f-637f-440c-a338-257a527998aa.jpeg, linkUrl=, sortOrder=0, isActive=1)]', '0:0:0:0:0:0:0:1', '2025-12-28 16:40:48');
INSERT INTO `sys_log` VALUES (263, 'AdminSystem', 'getBanners', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:40:48');
INSERT INTO `sys_log` VALUES (264, 'AdminSystem', 'saveBanner', 'AdminController', '[HomeBanner(id=3, title=33, imageUrl=/uploads/03b1be38-7810-4ac7-8e8c-9359bc433743.jpeg, linkUrl=, sortOrder=0, isActive=1)]', '0:0:0:0:0:0:0:1', '2025-12-28 16:41:10');
INSERT INTO `sys_log` VALUES (265, 'AdminSystem', 'getBanners', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:41:10');
INSERT INTO `sys_log` VALUES (266, 'AdminSystem', 'getLogs', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:41:15');
INSERT INTO `sys_log` VALUES (267, 'AdminSystem', 'getBanners', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:41:15');
INSERT INTO `sys_log` VALUES (268, 'AdminSystem', 'getLogs', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:52:02');
INSERT INTO `sys_log` VALUES (269, 'AdminSystem', 'getBanners', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:52:02');
INSERT INTO `sys_log` VALUES (270, 'AdminSystem', 'getBanners', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:52:03');
INSERT INTO `sys_log` VALUES (271, 'AdminSystem', 'getPendingCourses', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:52:03');
INSERT INTO `sys_log` VALUES (272, 'AdminSystem', 'getLogs', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:52:04');
INSERT INTO `sys_log` VALUES (273, 'AdminSystem', 'getBanners', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:52:04');
INSERT INTO `sys_log` VALUES (274, 'AdminSystem', 'getBanners', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:52:05');
INSERT INTO `sys_log` VALUES (275, 'AdminSystem', 'getAllUsers', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:52:07');
INSERT INTO `sys_log` VALUES (276, 'AdminSystem', 'getAllUsers', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:52:38');
INSERT INTO `sys_log` VALUES (277, 'AdminSystem', 'getAllUsers', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:55:44');
INSERT INTO `sys_log` VALUES (278, 'AdminSystem', 'getAllUsers', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:57:48');
INSERT INTO `sys_log` VALUES (279, 'AdminSystem', 'addUser', 'AdminController', '[User(userId=null, username=202502, password=null, realName=kk, role=student, avatar=null, phone=null, email=null, studentNo=null, balance=1000.00, createTime=null, firstLogin=true, roles=[])]', '0:0:0:0:0:0:0:1', '2025-12-28 16:57:58');
INSERT INTO `sys_log` VALUES (280, 'AdminSystem', 'getAllUsers', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 16:57:58');
INSERT INTO `sys_log` VALUES (281, 'AdminSystem', 'getBanners', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 17:12:12');
INSERT INTO `sys_log` VALUES (282, 'AdminSystem', 'getLogs', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 17:12:13');
INSERT INTO `sys_log` VALUES (283, 'AdminSystem', 'getBanners', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 17:12:13');
INSERT INTO `sys_log` VALUES (284, 'AdminSystem', 'getLogs', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 17:12:14');
INSERT INTO `sys_log` VALUES (285, 'AdminSystem', 'getPendingCourses', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 17:12:15');
INSERT INTO `sys_log` VALUES (286, 'AdminSystem', 'getAllUsers', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 17:12:19');
INSERT INTO `sys_log` VALUES (287, 'AdminSystem', 'getAllUsers', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 17:13:08');
INSERT INTO `sys_log` VALUES (288, 'AdminSystem', 'getLogs', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 17:18:15');
INSERT INTO `sys_log` VALUES (289, 'AdminSystem', 'getBanners', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 17:18:15');
INSERT INTO `sys_log` VALUES (290, 'AdminSystem', 'getAllUsers', 'AdminController', '[]', '0:0:0:0:0:0:0:1', '2025-12-28 17:18:16');
INSERT INTO `sys_log` VALUES (291, 'AdminSystem', 'resetPassword', 'AdminController', '[{userId=5}]', '0:0:0:0:0:0:0:1', '2025-12-28 17:18:18');

-- ----------------------------
-- Table structure for sys_message
-- ----------------------------
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '接收人ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_read` int NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_message
-- ----------------------------
INSERT INTO `sys_message` VALUES (1, 5, '作业已批改', '您的作业《Python 数据清洗实战》已被老师批改，得分：86', 1, '2025-12-18 21:00:22');
INSERT INTO `sys_message` VALUES (2, 5, '作业已批改', '您的作业《Python 数据清洗实战》已被老师批改，得分：86', 1, '2025-12-18 21:00:28');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` int NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `role_name`(`role_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'STUDENT', '学生');
INSERT INTO `sys_role` VALUES (2, 'TEACHER', '教师');
INSERT INTO `sys_role` VALUES (3, 'ADMIN', '管理员');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `real_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `student_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `balance` decimal(38, 2) NOT NULL,
  `first_login` tinyint(1) NULL DEFAULT 1,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (5, 'student1', '$2a$10$JOYRMZNGRix6FS3bXCkHDuDY/edVXRK.rbBy2Piahw.aMpffoGHR.', '???', NULL, '2025-12-12 18:07:25', 'student', NULL, NULL, NULL, NULL, 1111.00, 0);
INSERT INTO `sys_user` VALUES (6, 'student999', '$2a$10$C60fkYPpBiOD.V6VKv5e9uhfYGKPwyps7dUTDFkjGw/1YRiejnfIi', '???', NULL, '2025-12-13 00:23:37', 'teacher', NULL, NULL, NULL, NULL, 0.00, 1);
INSERT INTO `sys_user` VALUES (7, 'test001', '$2a$10$G5SGCzo7vfW6gCNpn9uXy.p9W5mTj00xXC3ZcUePVoDF2Q93B5MeC', '测试老师', NULL, '2025-12-14 16:29:42', 'teacher', NULL, NULL, NULL, NULL, 0.00, 0);
INSERT INTO `sys_user` VALUES (8, 'admin', '$2a$10$qhQdN2iI0o2arCOjWV0U6Oj/FvjfZ08UPARgPw50.LZ3IecG57d2K', '系统管理员', NULL, '2025-12-14 17:42:45', 'admin', NULL, NULL, NULL, NULL, 0.00, 0);
INSERT INTO `sys_user` VALUES (9, 'admin2', '$2a$10$2L.3Y0Z2ZhI7mNOIyEzRF.o1eT/DarEBtaPa/WoJyxnlTgXmGNjlK', '测试管理员', NULL, '2025-12-16 01:01:54', 'admin', NULL, NULL, NULL, NULL, 0.00, 1);
INSERT INTO `sys_user` VALUES (10, '202501', '$2a$10$U5ekxklFMCwj3AOQzsSqDuIQefJ0cx9wErlf5CiSjjZ6jUgJSpofq', '坤坤', NULL, '2025-12-16 16:11:05', 'student', NULL, NULL, NULL, NULL, 0.00, 1);
INSERT INTO `sys_user` VALUES (11, 't001', '$2a$10$fexraLMqNmni5P1TQHuhYuEK95d5j8SdIu0pnp7b9p/4ikEWtZT/.', '坤老师', NULL, '2025-12-16 16:11:21', 'teacher', NULL, NULL, NULL, NULL, 0.00, 1);
INSERT INTO `sys_user` VALUES (12, 't002', '$2a$10$bQ.isU.l2W4FZYjr2rNRxuuLfwJAkRP37hMbBFzmKkdwVprl8QlBq', '叮叮当', NULL, '2025-12-19 16:39:48', 'teacher', NULL, NULL, NULL, NULL, 1000.00, 1);
INSERT INTO `sys_user` VALUES (13, '202502', '$2a$10$oLOZK8vQYvVmqAzRkPOvb.5JRIeiQePnyTUvqgB/eRG52u09o28tq', 'kk', NULL, '2025-12-28 16:57:58', 'student', NULL, NULL, NULL, NULL, 1000.00, 1);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '用户ID',
  `role_id` int NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `FKb40xxfch70f5qnyfw8yme1n1s` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKhh52n8vd4ny9ff4x9fb8v65qx` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (3, 5, 2);
INSERT INTO `sys_user_role` VALUES (7, 7, 2);
INSERT INTO `sys_user_role` VALUES (8, 8, 3);
INSERT INTO `sys_user_role` VALUES (9, 6, 2);
INSERT INTO `sys_user_role` VALUES (10, 10, 1);
INSERT INTO `sys_user_role` VALUES (11, 11, 2);
INSERT INTO `sys_user_role` VALUES (12, 12, 2);

SET FOREIGN_KEY_CHECKS = 1;
