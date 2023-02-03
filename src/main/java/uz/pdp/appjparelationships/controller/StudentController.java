package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    SubjectRepository subjectRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/FacultyDekan/{id}")
    public Page<Student> getFaculty(@PathVariable Integer id,@RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_FacultyId(id, pageable);
        return studentPage;
    }

    //4. GROUP OWNER
    @GetMapping("/group/{id}")
    public Page<Student> getGrpup(@PathVariable Integer id,@RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroupId(id, pageable);
        return studentPage;
    }

    @PostMapping("/addStudent")
    public String addStudent(@RequestBody StudentDto studentDto) {
        Student student = new Student();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        //address
        Address address = new Address();
        address.setStreet(studentDto.getStreet());
        address.setCity(studentDto.getCity());
        address.setDistrict(studentDto.getDistrict());

        student.setAddress(address);
        Optional<Group> byId = groupRepository.findById(studentDto.getGroupId());
        if (!byId.isPresent()) {
            return "Groupa yoq";
        }
        Group group = byId.get();
        student.setGroup(group);
        List<Subject> subjectList = new ArrayList<>();
        List<Integer> subjectListDto = studentDto.getSubjectList();

        for (Integer i : subjectListDto) {
            Optional<Subject> optionalSubject = subjectRepository.findById(i);
            if (optionalSubject.isPresent()) {
                Subject subject = optionalSubject.get();
                subjectList.add(subject);
            } else {
                return "subject topilmadi";
            }
        }
        student.setSubjects(subjectList);

        return null;
    }


}
