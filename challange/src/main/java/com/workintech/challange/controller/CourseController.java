package com.workintech.challange.controller;

import com.workintech.challange.entity.Course;
import com.workintech.challange.entity.*;
import com.workintech.challange.exceptions.CourseException;
import com.workintech.challange.exceptions.CourseValidation;
import com.workintech.challange.mapping.CourseResponse;
import com.workintech.challange.mapping.CourseResponseFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private List<Course> courses;
    private CourseGpa lowGpa;
    private CourseGpa mediumGpa;
    private CourseGpa highGpa;

    @Autowired
    public CourseController(@Qualifier("lowCourseGpa") CourseGpa lowGpa,
                            @Qualifier("mediumCourseGpa") CourseGpa mediumGpa,
                            @Qualifier("highCourseGpa") CourseGpa highGpa) {
        this.lowGpa = lowGpa;
        this.mediumGpa = mediumGpa;
        this.highGpa = highGpa;
    }

    @PostConstruct
    public void init(){
        courses = new ArrayList<>();
    }

    @GetMapping("/")
    public List<Course> getAllCourses(){
        return courses;
    }

    @GetMapping("/{name}")
    public Course getCourseByName(@PathVariable String name){
        List<Course> foundCourses = courses.stream().filter(course -> course.getName()
                .equals(name)).collect(Collectors.toList());

        if (foundCourses.size() == 0){
            throw new CourseException("Course with given name is not exist:" + name, HttpStatus.BAD_REQUEST);
        }
        return foundCourses.get(0);
    }

    @PostMapping("/")
    public CourseResponse save(@RequestBody Course course){
        CourseValidation.isIdValid(course.getId());
        CourseValidation.checkCourseIsValid(course);
        CourseValidation.isDuplicateNameFound(courses, course.getName());
        courses.add(course);
        return CourseResponseFactory.createCourseResponse(course, lowGpa, mediumGpa, highGpa);
    }

    @PutMapping("/{id}")
    public Course update(@RequestBody Course course, @PathVariable int id){
        CourseValidation.checkCourseIsValid(course);
        Optional<Course> foundCourse = courses.stream().filter(c -> c.getId() == id).findFirst();
        if (foundCourse.isPresent()){
            int index = courses.indexOf(foundCourse.get());
            course.setId(id);
            courses.set(index, course);
            return course;
        } else{
            throw new CourseException("Course with given id is not exist: " + id, HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/")
    public Course save(@PathVariable int id){
        Optional<Course> foundCourse = courses.stream().filter(c -> c.getId() == id).findFirst();
        if (foundCourse.isPresent()){
            int index = courses.indexOf(foundCourse.get());
            courses.remove(index);
            return foundCourse.get();
        } else {
            throw new CourseException("Course with given id is not exist: " + id, HttpStatus.BAD_REQUEST);
        }
    }

}