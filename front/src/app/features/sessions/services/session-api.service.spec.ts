import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';

describe('SessionsService', () => {
  let service: SessionApiService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
    });
    service = TestBed.inject(SessionApiService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve the detail of a session', () => {
    const testSession: Session = {
      id: 1,
      name: 'Test Session',
      description: 'This is a test session',
      date: new Date('2023-01-01'),
      teacher_id: 101,
      users: [201, 202],
      createdAt: new Date('2023-01-01T00:00:00'),
      updatedAt: new Date('2023-01-01T00:00:00'),
    };
    const testId = '123';

    service.detail(testId).subscribe((session) => {
      expect(session).toEqual(testSession);
    });

    const req = httpTestingController.expectOne(`api/session/${testId}`);
    expect(req.request.method).toEqual('GET');
    req.flush(testSession);
  });

  it('should delete a session', () => {
    const testId = '123';

    service.delete(testId).subscribe((response) => {
      expect(response).toBeTruthy();
    });

    const req = httpTestingController.expectOne(`api/session/${testId}`);
    expect(req.request.method).toEqual('DELETE');
    req.flush({});
  });

  it('should create a new session', () => {
    const newSession: Session = {
      name: 'New Session',
      description: 'Description of new session',
      date: new Date(),
      teacher_id: 100,
      users: [],
    };

    service.create(newSession).subscribe((session) => {
      expect(session).toEqual(newSession);
    });

    const req = httpTestingController.expectOne('api/session');
    expect(req.request.method).toEqual('POST');
    expect(req.request.body).toEqual(newSession);
    req.flush(newSession);
  });

  it('should update a session', () => {
    const testSession: Session = {
      name: 'Updated Session',
      description: 'Updated Description',
      date: new Date(),
      teacher_id: 101,
      users: [301, 302],
    };
    const testId = '123';

    service.update(testId, testSession).subscribe((session) => {
      expect(session).toEqual(testSession);
    });

    const req = httpTestingController.expectOne(`api/session/${testId}`);
    expect(req.request.method).toEqual('PUT');
    expect(req.request.body).toEqual(testSession);
    req.flush(testSession);
  });

  it('should participate in a session', () => {
    const testSessionId = '123';
    const testUserId = '456';

    service.participate(testSessionId, testUserId).subscribe((response) => {
      expect(response).toBeUndefined();
    });

    const req = httpTestingController.expectOne(
      `api/session/${testSessionId}/participate/${testUserId}`
    );
    expect(req.request.method).toEqual('POST');
    req.flush(null);
  });

  it('should unparticipate from a session', () => {
    const testSessionId = '123';
    const testUserId = '456';

    service.unParticipate(testSessionId, testUserId).subscribe((response) => {
      expect(response).toBeUndefined();
    });

    const req = httpTestingController.expectOne(
      `api/session/${testSessionId}/participate/${testUserId}`
    );
    expect(req.request.method).toEqual('DELETE');
    req.flush(null);
  });
});
