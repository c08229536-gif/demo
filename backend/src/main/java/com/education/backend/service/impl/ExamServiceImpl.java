package com.education.backend.service.impl;

import com.education.backend.entity.*;
import com.education.backend.repository.*;
import com.education.backend.service.ExamService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private ExamQuestionRepository questionRepository;
    @Autowired
    private ExamResultRepository examResultRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentCourseRepository studentCourseRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Override
    @Transactional
    public String publishExam(Map<String, Object> payload) {
        Exam exam = new Exam();
        exam.setTitle((String) payload.get("title"));
        exam.setDuration((Integer) payload.get("duration"));
        exam.setCourseId((Integer) payload.get("courseId"));
        exam.setTotalScore((Integer) payload.get("totalScore"));
        exam.setWordUrl((String) payload.get("wordUrl"));
        exam.setState(1);
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

    @Override
    public List<Map<String, Object>> parseWordExam(Map<String, String> payload) {
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
                if (text == null)
                    continue;
                text = text.trim();
                if (text.isEmpty())
                    continue;

                if (text.contains("单项选择题")) {
                    currentSectionType = "single";
                    Matcher secScoreMatcher = scorePattern.matcher(text);
                    currentSectionScore = null;
                    if (secScoreMatcher.find()) {
                        currentSectionScore = Integer.parseInt(secScoreMatcher.group(1));
                    }
                    continue;
                }
                if (text.contains("判断题")) {
                    currentSectionType = "judge";
                    Matcher secScoreMatcher = scorePattern.matcher(text);
                    currentSectionScore = null;
                    if (secScoreMatcher.find()) {
                        currentSectionScore = Integer.parseInt(secScoreMatcher.group(1));
                    }
                    continue;
                }
                if (text.contains("填空题") || text.contains("简答题") || text.contains("综合题")) {
                    currentSectionType = "text";
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
                } else if (currentSectionType != null
                        && !text.startsWith("答案")
                        && !text.startsWith("【答案】")) {

                    String trimmedStart = text.trim();
                    Matcher looseStart = qStart.matcher(trimmedStart);
                    if (looseStart.find()) {
                        isQuestionLine = true;
                        questionContent = looseStart.group(1).trim();
                    } else {
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

                    Matcher inlineOptMatcher = Pattern.compile("\\s+([A-Da-d])[\\.．、\\)]\\s*").matcher(content);
                    if (inlineOptMatcher.find()) {
                        int splitIndex = inlineOptMatcher.start();
                        String optionsPart = content.substring(splitIndex);
                        content = content.substring(0, splitIndex).trim();

                        String[] optSegments = optionsPart.split("\\s+(?=[A-Da-d][\\.．、\\)])");
                        for (String seg : optSegments) {
                            Matcher om = optPattern.matcher(seg.trim());
                            if (om.find()) {
                                String k = om.group(1).toUpperCase();
                                String v = om.group(2).trim();
                                optionsMap.put(k, v);
                            }
                        }
                        if (!optionsMap.isEmpty() && !current.containsKey("type")) {
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

    @Override
    public Exam getExam(Integer id) {
        return examRepository.findById(id).orElse(null);
    }

    @Override
    public List<ExamQuestion> getQuestions(Integer examId) {
        return questionRepository.findByExamId(examId);
    }

    @Override
    @Transactional
    public Map<String, Object> startExam(Integer userId, Integer examId) {
        ExamResult result = examResultRepository.findByStudentId(userId).stream()
                .filter(r -> r.getExamId().equals(examId))
                .findFirst()
                .orElse(null);

        if (result == null) {
            result = new ExamResult();
            result.setExamId(examId);
            result.setStudentId(userId);
            result.setStartTime(new Date());
            result.setStatus(0);
            result.setSwitchCount(0);
            examResultRepository.save(result);
        }

        Exam exam = examRepository.findById(examId).orElseThrow();
        long now = System.currentTimeMillis();
        long start = result.getStartTime().getTime();
        long elapsedSeconds = (now - start) / 1000;
        long totalSeconds = exam.getDuration() * 60;
        long remaining = totalSeconds - elapsedSeconds;

        if (remaining <= 0 && result.getStatus() == 0) {
            result.setStatus(1);
            result.setSubmitTime(new Date());
            result.setScore(0);
            examResultRepository.save(result);
            remaining = 0;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("startTime", result.getStartTime());
        map.put("remainingSeconds", remaining > 0 ? remaining : 0);
        map.put("switchCount", result.getSwitchCount());
        map.put("status", result.getStatus());
        return map;
    }

    @Override
    @Transactional
    public void updateSwitchCount(Integer userId, Integer examId) {
        ExamResult result = examResultRepository.findByStudentId(userId).stream()
                .filter(r -> r.getExamId().equals(examId))
                .findFirst()
                .orElse(null);
        if (result != null && result.getStatus() == 0) {
            result.setSwitchCount(result.getSwitchCount() + 1);
            examResultRepository.save(result);
        }
    }

    @Override
    @Transactional
    public Integer submitExam(Integer examId, Map<Integer, String> answers, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        List<ExamQuestion> questions = questionRepository.findByExamId(examId);

        int totalScore = 0;
        for (ExamQuestion q : questions) {
            String studentAnswer = answers.get(q.getId());
            if (studentAnswer != null && q.getAnswer() != null) {
                String type = q.getType();
                if ("单选".equals(type) || "判断".equals(type) || "single".equalsIgnoreCase(type)
                        || "judge".equalsIgnoreCase(type)) {
                    if (studentAnswer.trim().equalsIgnoreCase(q.getAnswer().trim())) {
                        totalScore += (q.getScore() != null ? q.getScore() : 0);
                    }
                }
            }
        }

        ExamResult result = examResultRepository.findByStudentId(user.getUserId()).stream()
                .filter(r -> r.getExamId().equals(examId))
                .findFirst()
                .orElse(new ExamResult());

        if (result.getId() == null) {
            result.setExamId(examId);
            result.setStudentId(user.getUserId());
            result.setStartTime(new java.util.Date());
        }

        result.setScore(totalScore);
        result.setSubmitTime(new java.util.Date());
        result.setStatus(1);
        try {
            result.setAnswers(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(answers));
        } catch (Exception e) {
            result.setAnswers("{}");
        }

        examResultRepository.save(result);
        return totalScore;
    }

    @Override
    public List<Map<String, Object>> getStudentExams(Integer userId) {
        List<StudentCourse> courses = studentCourseRepository.findByUserId(userId);
        if (courses == null) {
            courses = new ArrayList<>();
        }

        List<Integer> enrolledCourseIds = courses.stream()
                .map(StudentCourse::getCourseId)
                .collect(Collectors.toList());

        List<Exam> exams = examRepository.findAll();
        List<ExamResult> myResults = examResultRepository.findByStudentId(userId);

        List<Map<String, Object>> list = new ArrayList<>();
        for (Exam e : exams) {
            if (e.getState() != 1) {
                continue;
            }
            if (e.getCourseId() != null && !enrolledCourseIds.contains(e.getCourseId())) {
                continue;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("id", e.getId());
            map.put("title", e.getTitle());
            map.put("duration", e.getDuration());
            map.put("totalScore", e.getTotalScore());
            map.put("courseId", e.getCourseId());

            courseRepository.findById(e.getCourseId()).ifPresent(c -> map.put("courseName", c.getTitle()));

            ExamResult r = myResults.stream().filter(res -> res.getExamId().equals(e.getId())).findFirst().orElse(null);
            if (r != null) {
                map.put("status", r.getStatus() == 1 ? "已提交" : "进行中");
                map.put("score", r.getScore());
            } else {
                map.put("status", "未开始");
            }
            list.add(map);
        }
        return list;
    }
}
