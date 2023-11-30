import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { Teacher } from '../interfaces/teacher.interface';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

import { TeacherService } from './teacher.service';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
    });
    service = TestBed.inject(TeacherService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve all teachers', () => {
    const mockTeachers: Teacher[] = [
      {
        id: 1,
        lastName: 'Doe',
        firstName: 'John',
        createdAt: new Date(),
        updatedAt: new Date(),
      },
      {
        id: 2,
        lastName: 'Smith',
        firstName: 'Jane',
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    ];

    service.all().subscribe((teachers) => {
      expect(teachers).toEqual(mockTeachers);
    });

    const req = httpTestingController.expectOne('api/teacher');
    expect(req.request.method).toEqual('GET');
    req.flush(mockTeachers);
  });

  it('should retrieve the detail of a teacher', () => {
    const mockTeacher: Teacher = {
      id: 1,
      lastName: 'Doe',
      firstName: 'John',
      createdAt: new Date(),
      updatedAt: new Date(),
    };
    const testId = '1';

    service.detail(testId).subscribe((teacher) => {
      expect(teacher).toEqual(mockTeacher);
    });

    const req = httpTestingController.expectOne(`api/teacher/${testId}`);
    expect(req.request.method).toEqual('GET');
    req.flush(mockTeacher);
  });
});
