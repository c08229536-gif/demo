package com.education.backend.service;

import com.education.backend.entity.Exam;
import com.education.backend.entity.ExamQuestion;

import java.util.List;
import java.util.Map;

public interface ExamService {
    String publishExam(Map<String, Object> payload);

    List<Map<String, Object>> parseWordExam(Map<String, String> payload);

    Exam getExam(Integer id);

    List<ExamQuestion> getQuestions(Integer examId);

    Map<String, Object> startExam(Integer userId, Integer examId);

    void updateSwitchCount(Integer userId, Integer examId);

    Integer submitExam(Integer examId, Map<Integer, String> answers, String username);

    List<Map<String, Object>> getStudentExams(Integer userId);
}
