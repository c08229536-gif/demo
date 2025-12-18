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

 Date: 18/12/2025 21:11:36
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
  PRIMARY KEY (`course_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, 'Java 高级编程', '张教授', 'https://placeholder.co/300x200/409EFF/ffffff?text=Java+Plus', '深入理解 JVM 原理', 1, '后端', NULL);
INSERT INTO `course` VALUES (2, 'Python 数据分析', '李博士', 'https://placeholder.co/300x200/67C23A/ffffff?text=Python', 'Pandas 与 NumPy 实战', 1, 'AI', NULL);
INSERT INTO `course` VALUES (3, 'Vue3 全栈开发', '王大神', 'https://placeholder.co/300x200/E6A23C/ffffff?text=Vue3', '从入门到精通', 1, '前端', NULL);
INSERT INTO `course` VALUES (4, '测试课程', '我', 'https://placeholder.co/300x200/808080/ffffff?text=No+Cover', '你好你好', 1, '其他', NULL);
INSERT INTO `course` VALUES (5, 'Python进阶', '陈健宇', 'https://placeholder.co/300x200/808080/ffffff?text=No+Cover', 'glagame男主', 1, 'AI', NULL);
INSERT INTO `course` VALUES (6, '软件设计', '陈坤', 'https://placeholder.co/300x200/808080/ffffff?text=No+Cover', '', 1, '移动端', NULL);

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
-- Table structure for student_course
-- ----------------------------
DROP TABLE IF EXISTS `student_course`;
CREATE TABLE `student_course`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '学生ID',
  `course_id` int NOT NULL COMMENT '课程ID',
  `enroll_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_course
-- ----------------------------
INSERT INTO `student_course` VALUES (1, 5, 1, '2025-12-13 23:43:51');
INSERT INTO `student_course` VALUES (2, 5, 2, '2025-12-14 00:03:50');
INSERT INTO `student_course` VALUES (3, 5, 3, '2025-12-14 00:03:53');
INSERT INTO `student_course` VALUES (4, 5, 4, '2025-12-14 18:37:34');
INSERT INTO `student_course` VALUES (5, 7, 4, '2025-12-14 18:38:04');

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_feedback
-- ----------------------------

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
INSERT INTO `sys_message` VALUES (1, 5, '作业已批改', '您的作业《Python 数据清洗实战》已被老师批改，得分：86', 0, '2025-12-18 21:00:22');
INSERT INTO `sys_message` VALUES (2, 5, '作业已批改', '您的作业《Python 数据清洗实战》已被老师批改，得分：86', 0, '2025-12-18 21:00:28');

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
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (5, 'student1', '$2a$10$3knQo8c69MG6zASQTwp7YO6MBpCNZHgdXKH0M1Uc/amMcJ2kvQOQC', '???', NULL, '2025-12-12 18:07:25', 'student', NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (6, 'student999', '$2a$10$C60fkYPpBiOD.V6VKv5e9uhfYGKPwyps7dUTDFkjGw/1YRiejnfIi', '???', NULL, '2025-12-13 00:23:37', 'teacher', NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (7, 'test001', '$2a$10$n03xzZwjNhpX9w2amvm.g.0XO0wKdO4Ayswjr7qrLMTi9Gak1EgV2', '测试老师', NULL, '2025-12-14 16:29:42', 'teacher', NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (8, 'admin', '$2a$10$3knQo8c69MG6zASQTwp7YO6MBpCNZHgdXKH0M1Uc/amMcJ2kvQOQC', '系统管理员', NULL, '2025-12-14 17:42:45', 'admin', NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (9, 'admin2', '$2a$10$2L.3Y0Z2ZhI7mNOIyEzRF.o1eT/DarEBtaPa/WoJyxnlTgXmGNjlK', '测试管理员', NULL, '2025-12-16 01:01:54', 'admin', NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (10, '202501', '$2a$10$U5ekxklFMCwj3AOQzsSqDuIQefJ0cx9wErlf5CiSjjZ6jUgJSpofq', '坤坤', NULL, '2025-12-16 16:11:05', 'student', NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (11, 't001', '$2a$10$fexraLMqNmni5P1TQHuhYuEK95d5j8SdIu0pnp7b9p/4ikEWtZT/.', '坤老师', NULL, '2025-12-16 16:11:21', 'teacher', NULL, NULL, NULL, NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (3, 5, 2);
INSERT INTO `sys_user_role` VALUES (7, 7, 2);
INSERT INTO `sys_user_role` VALUES (8, 8, 3);
INSERT INTO `sys_user_role` VALUES (9, 6, 2);
INSERT INTO `sys_user_role` VALUES (10, 10, 1);
INSERT INTO `sys_user_role` VALUES (11, 11, 2);

SET FOREIGN_KEY_CHECKS = 1;
