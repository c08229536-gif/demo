package com.education.backend.controller;

import com.education.backend.entity.*;
import com.education.backend.repository.*;
import com.education.backend.service.EduService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional; // 👈 必须导入，修复 image_57f25f 报错

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

@RestController
@RequestMapping("/admin") // 👈 注意：去掉 /api，以适配 Vite 代理重写规则
@CrossOrigin
public class AdminController {

    @Autowired private EduService eduService;
    @Autowired private UserRepository userRepository;
    @Autowired private HomeBannerRepository bannerRepository;
    @Autowired private ExamRepository examRepository;
    @Autowired private ExamQuestionRepository questionRepository;
    @Autowired private SysLogRepository logRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;


    // === 1. 用户权限管理 (修复 404) ===
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return eduService.findAllUsers();
    }

    @PostMapping("/user/add")
    public ResponseEntity<?> addUser(@RequestBody User request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setRealName(request.getRealName());

        // 关键修复：从 RoleRepository 查找 Role 并设置到用户的 Set<Role> 中
        Role role = roleRepository.findByRoleName(request.getRole().toUpperCase())
                .orElseThrow(() -> new RuntimeException("错误：找不到角色: " + request.getRole()));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        newUser.setRoles(roles);
        // 同时，为了兼容旧的前端逻辑或显示，仍然设置一下 string role
        newUser.setRole(request.getRole());

        newUser.setPassword(passwordEncoder.encode("123456"));
        newUser.setFirstLogin(true);

        userRepository.save(newUser);
        return ResponseEntity.ok("用户添加成功");
    }

    @PostMapping("/users/batch-add")
    @Transactional
    public ResponseEntity<?> batchAddUsers(@RequestBody List<User> users) {
        // 1. 预加载所有角色和已存在的用户名，提高效率
        Role studentRole = roleRepository.findByRoleName("STUDENT").orElseThrow(() -> new RuntimeException("角色不存在: STUDENT"));
        Role teacherRole = roleRepository.findByRoleName("TEACHER").orElseThrow(() -> new RuntimeException("角色不存在: TEACHER"));
        List<String> existingUsernames = userRepository.findAll().stream().map(User::getUsername).collect(java.util.stream.Collectors.toList());

        int successCount = 0;
        int skippedCount = 0;
        
        for (User userRequest : users) {
            // 2. 如果用户名已存在，则跳过
            if (existingUsernames.contains(userRequest.getUsername())) {
                skippedCount++;
                continue;
            }

            User newUser = new User();
            newUser.setUsername(userRequest.getUsername());
            newUser.setRealName(userRequest.getRealName());
            newUser.setPassword(passwordEncoder.encode("123456"));
            newUser.setFirstLogin(true);

            // 3. 分配角色
            Set<Role> roles = new HashSet<>();
            if ("student".equalsIgnoreCase(userRequest.getRole())) {
                roles.add(studentRole);
            } else if ("teacher".equalsIgnoreCase(userRequest.getRole())) {
                roles.add(teacherRole);
            } else {
                // 如果角色不是学生或老师，也跳过，或者可以抛出异常
                skippedCount++;
                continue;
            }
            newUser.setRoles(roles);
            newUser.setRole(userRequest.getRole()); // 兼容旧字段

            // 4. 保存新用户
            userRepository.save(newUser);
            successCount++;
        }

        String message = String.format("批量导入完成：成功新增 %d 人，因用户名已存在或角色无效而跳过 %d 人。", successCount, skippedCount);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/user/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, Integer> payload) {
        Integer userId = payload.get("userId");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        user.setPassword(passwordEncoder.encode("123456"));
        user.setFirstLogin(true);
        userRepository.save(user);

        return ResponseEntity.ok("密码重置成功");
    }

    @GetMapping("/user/fix-roles")
    @Transactional
    public ResponseEntity<?> fixRoles() {
        List<User> allUsers = userRepository.findAll();
        Role studentRole = roleRepository.findByRoleName("STUDENT").orElseThrow();
        Role teacherRole = roleRepository.findByRoleName("TEACHER").orElseThrow();
        Role adminRole = roleRepository.findByRoleName("ADMIN").orElseThrow();

        for (User user : allUsers) {
            if (user.getRole() != null) {
                Set<Role> roles = new HashSet<>();
                switch (user.getRole()) {
                    case "student":
                        roles.add(studentRole);
                        break;
                    case "teacher":
                        roles.add(teacherRole);
                        break;
                    case "admin":
                        roles.add(adminRole);
                        break;
                }
                if (!roles.isEmpty()) {
                    user.setRoles(roles);
                    userRepository.save(user);
                }
            }
        }
        return ResponseEntity.ok("所有用户角色已校准。");
    }

    // === 2. 课程审核台 (修复 image_595a49) ===
    @GetMapping("/courses/pending")
    public List<Course> getPendingCourses() {
        return courseRepository.findByStatus(0);
    }

    @PostMapping("/course/audit")
    public String auditCourse(@RequestBody Map<String, Object> params) {
        eduService.auditCourse((Integer)params.get("courseId"), (Boolean)params.get("pass"));
        return "审核处理完毕";
    }

    // === 3. 首页运营管理 (轮播图) ===
    @PostMapping("/banners/save")
    public HomeBanner saveBanner(@RequestBody HomeBanner banner) {
        return bannerRepository.save(banner);
    }

    @DeleteMapping("/banners/{id}")
    public void deleteBanner(@PathVariable Integer id) {
        bannerRepository.deleteById(id);
    }

    @GetMapping("/banners")
    public List<HomeBanner> getBanners() {
        return bannerRepository.findAll();
    }

    // === 4. 发布试卷 (核心补全：解决学生端没内容问题) ===
    @PostMapping("/exam/publish")
    @Transactional
    @SuppressWarnings("unchecked")
    public String publishExam(@RequestBody Map<String, Object> payload) {
        Exam exam = new Exam();
        exam.setTitle((String) payload.get("title"));
        exam.setDuration((Integer) payload.get("duration"));
        exam.setCourseId((Integer) payload.get("courseId"));
        exam.setTotalScore((Integer) payload.get("totalScore"));
        exam.setWordUrl((String) payload.get("wordUrl"));
        exam.setState(1); // 核心修复：发布时自动设置为“已发布”状态
        Exam savedExam = examRepository.save(exam);

        List<Map<String, Object>> questions = (List<Map<String, Object>>) payload.get("questions");
        for (Map<String, Object> qMap : questions) {
            ExamQuestion q = new ExamQuestion();
            q.setExamId(savedExam.getId());
            q.setContent((String) qMap.get("content"));
            String rawType = (String) qMap.get("type");
            String finalType = rawType;
            if ("single".equals(rawType)) {
                finalType = "单选";
            } else if ("judge".equals(rawType)) {
                finalType = "判断";
            } else if ("text".equals(rawType)) {
                finalType = "简答";
            }
            q.setType(finalType);
            q.setOptions((String) qMap.get("options")); 
            q.setAnswer((String) qMap.get("answer"));
            q.setScore((Integer) qMap.get("score"));
            questionRepository.save(q);
        }
        return "试卷发布成功";
    }

    @PostMapping("/exam/parse-word")
    public List<Map<String, Object>> parseWordExam(@RequestBody Map<String, String> payload) {
        String wordUrl = payload.get("wordUrl");
        if (wordUrl == null || wordUrl.isEmpty()) {
            throw new RuntimeException("wordUrl 不能为空");
        }
        String fileName = Paths.get(wordUrl).getFileName().toString();
        Path path = Paths.get(System.getProperty("user.dir"), "uploads", fileName);
        if (!Files.exists(path)) {
            throw new RuntimeException("Word 文件不存在: " + fileName);
        }

        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(path))) {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> current = null;
            Map<String, String> optionsMap = null;

            Pattern qStart = Pattern.compile("^[0-9]+[\\.．、]\\s*(.*)$");
            Pattern optPattern = Pattern.compile("^([A-Da-d])[\\.．、\\)]\\s*(.+)$");
            Pattern scorePattern = Pattern.compile("([0-9]+)分");

            String currentSectionType = null;
            Integer currentSectionScore = null;

            for (XWPFParagraph p : doc.getParagraphs()) {
                String text = p.getText();
                if (text == null) continue;
                text = text.trim();
                if (text.isEmpty()) continue;

                // Debug log
                System.out.println("Processing line: [" + text + "]");

                if (text.contains("单项选择题")) {
                    currentSectionType = "single";
                    System.out.println("Found Section: single");
                    Matcher secScoreMatcher = scorePattern.matcher(text);
                    currentSectionScore = null;
                    if (secScoreMatcher.find()) {
                        currentSectionScore = Integer.parseInt(secScoreMatcher.group(1));
                    }
                    continue;
                }
                if (text.contains("判断题")) {
                    currentSectionType = "judge";
                    System.out.println("Found Section: judge");
                    Matcher secScoreMatcher = scorePattern.matcher(text);
                    currentSectionScore = null;
                    if (secScoreMatcher.find()) {
                        currentSectionScore = Integer.parseInt(secScoreMatcher.group(1));
                    }
                    continue;
                }
                if (text.contains("填空题") || text.contains("简答题") || text.contains("综合题")) {
                    currentSectionType = "text";
                    System.out.println("Found Section: text");
                    Matcher secScoreMatcher = scorePattern.matcher(text);
                    currentSectionScore = null;
                    if (secScoreMatcher.find()) {
                        currentSectionScore = Integer.parseInt(secScoreMatcher.group(1));
                    }
                    continue;
                }

                Matcher qMatcher = qStart.matcher(text);
                boolean isQuestionLine = false;
                String questionContent = null;

                if (qMatcher.find()) {
                    isQuestionLine = true;
                    questionContent = qMatcher.group(1).trim();
                    System.out.println("Matched Question (Standard): " + questionContent);
                } else if (currentSectionType != null
                        && !text.startsWith("答案")
                        && !text.startsWith("【答案】")) {
                    
                    // 特殊处理：尝试识别以空格/制表符开头后跟数字的情况 (e.g. " 1. ", "\t1. ")
                    String trimmedStart = text.trim();
                    Matcher looseStart = qStart.matcher(trimmedStart);
                    if (looseStart.find()) {
                        isQuestionLine = true;
                        questionContent = looseStart.group(1).trim();
                        System.out.println("Matched Question (Loose): " + questionContent);
                    } else {
                        // 之前的兜底逻辑：靠括号或下划线识别
                        boolean looksLikeOptionLine = false;
                        Matcher tmpOpt = optPattern.matcher(text);
                        if (tmpOpt.find()) {
                            looksLikeOptionLine = true;
                        }
                        if (!looksLikeOptionLine) {
                            if ((text.contains("（") && text.contains("）"))
                                    || (text.contains("(") && text.contains(")"))
                                    || text.contains("___")
                                    || text.contains("____")
                                    || text.contains("——")) {
                                isQuestionLine = true;
                                questionContent = text;
                                System.out.println("Matched Question (Heuristic): " + questionContent);
                            }
                        }
                    }
                }

                if (isQuestionLine) {
                    if (current != null) {
                        if (optionsMap != null && !optionsMap.isEmpty()) {
                            current.put("options", buildOptionsJson(optionsMap));
                        }
                        if (!current.containsKey("score")) {
                            if (currentSectionScore != null) {
                                current.put("score", currentSectionScore);
                            } else {
                                current.put("score", 5);
                            }
                        }
                        if (!current.containsKey("type")) {
                            if (currentSectionType != null) {
                                current.put("type", currentSectionType);
                            } else {
                                current.put("type", "single");
                            }
                        }
                        result.add(current);
                    }
                    current = new LinkedHashMap<>();
                    optionsMap = new LinkedHashMap<>();

                    String content = questionContent;
                    Integer score = null;
                    Matcher sMatcher = scorePattern.matcher(content);
                    if (sMatcher.find()) {
                        score = Integer.parseInt(sMatcher.group(1));
                        content = content.replace(sMatcher.group(0), "").trim();
                    }

                    // 核心修复：检查题目内容中是否混杂了选项（Inline Options）
                    // 例如： "下列选项中，不属于...的是（ ） A. 逻辑推理 B. 语言表达..."
                    // 我们需要提取出 A. ... B. ...
                    // 策略：查找第一个 "A." 或 "A、" 的位置，如果存在，截断题目，并解析后续选项
                    Matcher inlineOptMatcher = Pattern.compile("\\s+([A-Da-d])[\\.．、\\)]\\s*").matcher(content);
                    if (inlineOptMatcher.find()) {
                         int splitIndex = inlineOptMatcher.start();
                         String optionsPart = content.substring(splitIndex);
                         content = content.substring(0, splitIndex).trim(); // 截断题目内容
                         
                         // 解析 optionsPart
                         // 使用更严谨的正则来切分选项
                         String[] optSegments = optionsPart.split("\\s+(?=[A-Da-d][\\.．、\\)])");
                         for (String seg : optSegments) {
                             Matcher om = optPattern.matcher(seg.trim());
                             if (om.find()) {
                                 String k = om.group(1).toUpperCase();
                                 String v = om.group(2).trim();
                                 optionsMap.put(k, v);
                             }
                         }
                         // 关键：如果解析出了选项，根据当前题型判断，如果没识别出题型，则默认为单选
                         if (!optionsMap.isEmpty() && !current.containsKey("type")) {
                             // 简单启发式：如果答案长度>1且不是单词，可能是多选，但Word解析很难拿到标准答案
                             // 这里暂时保守一点：只有当它之前没被识别为其他题型时，才设为单选
                             // 如果是“多项选择题”区域，currentSectionType已经是 "multi" 了 (假设有这个逻辑)
                             // 目前代码只处理了 single/judge/text，没处理 multi
                             // 如果未来支持多选，这里需要改。现在为了修复bug，先去掉强制覆盖，仅当没有type时设置
                             current.put("type", "single");
                         }
                    }

                    current.put("content", content);
                    if (score != null) {
                        current.put("score", score);
                    }
                    continue;
                }

                if (current == null) {
                    continue;
                }

                boolean hasOption = false;
                String[] parts = text.split("\\s{2,}");
                for (String part : parts) {
                    String trimmed = part.trim();
                    if (trimmed.isEmpty()) {
                        continue;
                    }
                    Matcher optMatcher = optPattern.matcher(trimmed);
                    if (optMatcher.find()) {
                        String key = optMatcher.group(1).toUpperCase();
                        String val = optMatcher.group(2).trim();
                        if (optionsMap == null) {
                            optionsMap = new LinkedHashMap<>();
                        }
                        optionsMap.put(key, val);
                        hasOption = true;
                    }
                }
                if (hasOption) {
                    continue;
                }

                if (text.startsWith("答案") || text.startsWith("【答案】")) {
                    String answer = text.replace("【答案】", "")
                            .replace("答案", "")
                            .replace("：", "")
                            .replace(":", "")
                            .trim();
                    current.put("answer", answer);
                    if ("正确".equals(answer) || "错误".equals(answer) || "对".equals(answer) || "错".equals(answer)) {
                        current.put("type", "judge");
                    } else if (answer.matches("^[A-D]$")) {
                        current.put("type", "single");
                    } else {
                        current.put("type", "text");
                    }
                    continue;
                }
            }

            if (current != null) {
                if (optionsMap != null && !optionsMap.isEmpty()) {
                    current.put("options", buildOptionsJson(optionsMap));
                }
                if (!current.containsKey("score")) {
                    if (currentSectionScore != null) {
                        current.put("score", currentSectionScore);
                    } else {
                        current.put("score", 5);
                    }
                }
                if (!current.containsKey("type")) {
                    if (currentSectionType != null) {
                        current.put("type", currentSectionType);
                    } else {
                        current.put("type", "single");
                    }
                }
                result.add(current);
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("解析 Word 试卷失败: " + e.getMessage(), e);
        }
    }

    private String buildOptionsJson(Map<String, String> options) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int i = 0;
        for (Map.Entry<String, String> e : options.entrySet()) {
            if (i++ > 0) {
                sb.append(",");
            }
            sb.append("\"").append(e.getKey()).append("\":\"")
                    .append(e.getValue().replace("\"", "\\\""))
                    .append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    @GetMapping("/logs")
    public List<SysLog> getLogs() {
        return logRepository.findAll();
    }
}
