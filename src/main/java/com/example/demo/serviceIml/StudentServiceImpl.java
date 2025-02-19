package com.example.demo.serviceIml;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.controller.CommonResponses;
import com.example.demo.entity.Course;
import com.example.demo.entity.Student;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.StudentService;
import com.example.demo.utils.Utils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    static Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Autowired
    private final StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public void addNewStudent(final Student student) {
        String studentEmail = student.getEmail();
        if (studentRepository.isExistByEmail(studentEmail)) {
            log.error(studentExist + "E-MAIL: " + studentEmail);
            throw new BadRequestException(studentExist + "E-MAIL: " + studentEmail);
        }

        // check if mail is valid
        if (!Utils.isMailValid(student.getEmail())) {
            log.error(CommonResponses.emailNotValidMsg);
            throw new BadRequestException(CommonResponses.emailNotValidMsg);
        }

        if (student.getName().length() < 2) {
            log.error(CommonResponses.nameNotValidMsg);
            throw new BadRequestException(CommonResponses.nameNotValidMsg);
        }

        studentRepository.save(student);
        log.info("New student saved: {}", student.toString());
    }

    @Transactional
    @Override
    public void updateStudent(String studentEmail, final Student updateStudent) {
        Optional<Student> student = studentRepository.findByEmail(studentEmail);
        // check if the student is exist
        if (!student.isPresent()) {
            log.error(studentNotExistMsg + "E-mail: " + studentEmail);
            throw new NotFoundException(studentNotExistMsg + "E-mail: " + studentEmail);
        }

        Student currentStudent = student.get();

        // Student name processing
        if (updateStudent.getName().length() < 2) {
            log.error(CommonResponses.nameNotValidMsg);
            throw new BadRequestException(CommonResponses.nameNotValidMsg);
        }
        currentStudent.setName(updateStudent.getName());

        // Check student email if valid
        if (!Utils.isMailValid(updateStudent.getEmail())) {
            log.error(CommonResponses.emailNotValidMsg);
            throw new BadRequestException(CommonResponses.emailNotValidMsg);
        }

        // check if e-mail taken
        if (!studentEmail.equals(updateStudent.getEmail())) {
            if (studentRepository.isExistByEmail(updateStudent.getEmail())) {
                log.error(CommonResponses.emailTakenMsg);
                throw new BadRequestException(CommonResponses.emailTakenMsg);
            }
            currentStudent.setEmail(updateStudent.getEmail());
        }

        // no need process for the DOB becase it's @NonNull in the entity class
        currentStudent.setDob(updateStudent.getDob());
        log.info("Student updated: {} ", currentStudent.toString());
    }

    @Override
    public void deleteStudentById(int studentId) {
        if (!studentRepository.existsById(studentId)) {
            log.error(studentNotExistMsg + "ID: " + studentId);
            throw new NotFoundException(studentNotExistMsg + "ID: " + studentId);
        }
        studentRepository.deleteById(studentId);
        log.info(studentSuccessfullyDeleteMsg + "ID: " + studentId);
    }

    @Override
    public void deleteStudentByEmail(String studentEmail) {
        if (!studentRepository.isExistByEmail(studentEmail)) {
            log.error(studentNotExistMsg + "E-MAIL: " + studentEmail);
            throw new NotFoundException(studentNotExistMsg + "E-MAIL: " + studentEmail);
        }
        studentRepository.deleteByEmail(studentEmail);
        log.info(studentSuccessfullyDeleteMsg + "E-MAIL: " + studentEmail);
    }

    @Override
    public Student getStudentById(int studentId) {
        Optional<Student> studentRecord = studentRepository.findById(studentId);
        if (!studentRecord.isPresent()) {
            log.error(studentNotExistMsg + "ID: " + studentId);
            throw new NotFoundException(studentNotExistMsg + "ID: " + studentId);
        }
        Student student = studentRecord.get();
        log.info(studentSuccessfullyFoundMsg + student.toString());
        return student;
    }

    @Override
    public Student getStudentByEmail(String studentEmail) {
        Optional<Student> studentOptional = studentRepository.findByEmail(studentEmail);
        if (!studentOptional.isPresent()) {
            log.error(studentNotExistMsg + "E-MAIL: " + studentEmail);
            throw new NotFoundException(studentNotExistMsg + "E-MAIL: " + studentEmail);
        }
        Student student = studentOptional.get();
        log.info(studentSuccessfullyFoundMsg + student.toString());
        return student;
    }

    @Override
    public List<Student> getStudents() {
        log.info("getStudents: All students are called.");
        return studentRepository.findAll();
    }

    @Override
    @Transactional
    public void enrollToCourse(int studentId, String courseId) {
        Set<Course> enrolledCourses = null;
        Optional<Student> optStudent = studentRepository.findById(studentId);
        if (!optStudent.isPresent()) {
            log.error(studentNotExistMsg + "ID: " + studentId);
            throw new NotFoundException(studentNotExistMsg + "ID: " + studentId);
        }
        Student student = optStudent.get();

        Optional<Course> optCourse = courseRepository.findById(courseId);
        if (!optCourse.isPresent()) {
            log.error("COURSE NOT EXIST ID: " + courseId);
            throw new NotFoundException(" COURSE NOT EXIST ID: " + courseId);
        }
        Course course = optCourse.get();
        enrolledCourses = student.getEnrolledCourses();
        enrolledCourses.add(course);
        student.setEnrolledCourses(enrolledCourses);
        log.info(student.toString() + " enrolled to " + course.toString());
    }
}
