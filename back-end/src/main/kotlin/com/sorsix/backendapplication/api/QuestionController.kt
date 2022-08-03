package com.sorsix.backendapplication.api

import com.sorsix.backendapplication.api.dto.AnswerRequest
import com.sorsix.backendapplication.api.dto.QuestionRequest
import com.sorsix.backendapplication.domain.Question
import com.sorsix.backendapplication.domain.QuestionCreated
import com.sorsix.backendapplication.domain.QuestionFailed
import com.sorsix.backendapplication.service.QuestionService
import com.sorsix.backendapplication.service.TagService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin("http://localhost:4200")
@RequestMapping("/api/questions")
class QuestionController(
    val questionService: QuestionService,
    val tagService: TagService,
) {

    @GetMapping
    fun getAll(): List<Question>? {
        return questionService.findAll();
    }

    @GetMapping("/withoutAnswers")
    fun getAllQuestionsWithoutAnswers(): List<Question>? {
        return questionService.findAllQuestionsWithoutAnswers();
    }

    @PostMapping
    fun createQuestion(@RequestBody request: QuestionRequest): ResponseEntity<String> {
        val result = questionService.createQuestion(request.title,
            request.questionText,
            request.parentQuestionId,
            request.appUserId,
            request.tagsId)

        val resultString = when (result) {
            is QuestionCreated -> {
                result.question.toString();
            }
            is QuestionFailed -> {
                "Failed because " + result.errorMessage;
            }
        }
        return if (result.success()) {
            ResponseEntity.ok(resultString);
        } else {
            ResponseEntity.badRequest().body(resultString);
        }
    }

    @PostMapping("/postAnswer/{id}")
    fun postAnswerToQuestion(@PathVariable id: Long, @RequestBody request: AnswerRequest): ResponseEntity<Any> {
        val result = questionService.postAnswer(request.title,
            request.questionText,
            request.parentQuestionId,
            request.appUserId)
        val resultString = when (result) {
            is QuestionCreated -> {
                result.question;
            }
            is QuestionFailed -> {
                "Failed because " + result.errorMessage;
            }
        }
        return if (result.success()) {
            ResponseEntity.ok(resultString);
        } else {
            ResponseEntity.badRequest().body(resultString);
        }
    }

    @GetMapping("/{id}")
    fun getQuestionById(@PathVariable id: Long): Question? {
        return questionService.findById(id);
    }

    @GetMapping("/answers/{id}")
    fun getAnswersForQuestion(@PathVariable id: Long): List<Question>? {
        return questionService.getAnswersForQuestion(id);
    }

    @GetMapping("/tagged/{word}")
    fun getAllQuestionsWithMentionedWord(@PathVariable("word") word: String): List<Question>? {
        return questionService.findAllQuestionsWithMentionedWord(word);

    }

    @GetMapping("/tags/{id}")
    fun getQuestionTags(@PathVariable id: Long): List<String> {
        return questionService.getQuestionTags(id).map { it.tag.name }
    }

    @GetMapping("/sorted")
    fun getSortedQuestions(): List<Question> {
        return this.questionService.getSortedByTitle()
    }
}