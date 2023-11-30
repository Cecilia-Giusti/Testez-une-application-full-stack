import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { User } from '../interfaces/user.interface';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
    });
    service = TestBed.inject(UserService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve a user by ID', () => {
    const mockUser: User = {
      id: 1,
      email: 'test@example.com',
      lastName: 'Doe',
      firstName: 'John',
      admin: false,
      password: 'password123',
      createdAt: new Date('2021-01-01T00:00:00'),
      updatedAt: new Date('2021-01-02T00:00:00'),
    };
    const testId = '1';

    service.getById(testId).subscribe((user) => {
      expect(user).toEqual(mockUser);
    });

    const req = httpTestingController.expectOne(`api/user/${testId}`);
    expect(req.request.method).toEqual('GET');
    req.flush(mockUser);
  });

  it('should delete a user', () => {
    const testId = '1';

    service.delete(testId).subscribe((response) => {
      expect(response).toBeTruthy();
    });

    const req = httpTestingController.expectOne(`api/user/${testId}`);
    expect(req.request.method).toEqual('DELETE');
    req.flush({});
  });
});
