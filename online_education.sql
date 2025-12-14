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

 Date: 14/12/2025 19:47:20
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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of assignment
-- ----------------------------
INSERT INTO `assignment` VALUES (1, '完成 Java 课后习题', 1, '2025-12-31 23:59:59', '待提交', '请完成第一章的所有选择题');
INSERT INTO `assignment` VALUES (2, '撰写 Vue 学习心得', 1, '2025-10-01 12:00:00', '待提交', '不少于500字');
INSERT INTO `assignment` VALUES (3, 'Python 数据清洗实战', 2, '2025-11-11 00:00:00', '待提交', '使用 Pandas 处理 csv 文件');

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
  PRIMARY KEY (`course_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, 'Java 高级编程', '张教授', 'https://placeholder.co/300x200/409EFF/ffffff?text=Java+Plus', '深入理解 JVM 原理');
INSERT INTO `course` VALUES (2, 'Python 数据分析', '李博士', 'https://placeholder.co/300x200/67C23A/ffffff?text=Python', 'Pandas 与 NumPy 实战');
INSERT INTO `course` VALUES (3, 'Vue3 全栈开发', '王大神', 'https://placeholder.co/300x200/E6A23C/ffffff?text=Vue3', '从入门到精通');
INSERT INTO `course` VALUES (4, '测试课程', '我', 'https://placeholder.co/300x200/808080/ffffff?text=No+Cover', '你好你好');

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
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of homework_submission
-- ----------------------------
INSERT INTO `homework_submission` VALUES (1, 1, 5, '你好你好你好', '2025-12-14 12:49:04', 99);

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
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (5, 'student1', '$2a$10$3knQo8c69MG6zASQTwp7YO6MBpCNZHgdXKH0M1Uc/amMcJ2kvQOQC', '???', NULL, '2025-12-12 18:07:25', 'student', NULL);
INSERT INTO `sys_user` VALUES (6, 'student999', '$2a$10$rlPEwy3eVMxnqZXPsd.aNOrKttcj/4NfG9mqyVyTOJrRpxlxEEDDG', '???', NULL, '2025-12-13 00:23:37', 'student', NULL);
INSERT INTO `sys_user` VALUES (7, 'test001', '$2a$10$n03xzZwjNhpX9w2amvm.g.0XO0wKdO4Ayswjr7qrLMTi9Gak1EgV2', '测试老师', NULL, '2025-12-14 16:29:42', 'teacher', NULL);
INSERT INTO `sys_user` VALUES (8, 'admin', '$2a$10$3knQo8c1/7.1.1.1.1.1.1', '系统管理员', NULL, '2025-12-14 17:42:45', 'admin', NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (2, 6, 1);
INSERT INTO `sys_user_role` VALUES (3, 5, 2);
INSERT INTO `sys_user_role` VALUES (7, 7, 2);

SET FOREIGN_KEY_CHECKS = 1;
