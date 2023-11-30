import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

import { SessionService } from './session.service';

describe('SessionService', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  const mockSessionInfo: SessionInformation = {
    token: 'mockToken',
    type: 'user',
    id: 1,
    username: 'testUser',
    firstName: 'Test',
    lastName: 'User',
    admin: false,
  };

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should initially not be logged in', (done) => {
    service.$isLogged().subscribe((isLogged) => {
      expect(isLogged).toBeFalsy();
      done();
    });
  });

  it('should reflect logged in status', (done) => {
    service.logIn(mockSessionInfo);

    service.$isLogged().subscribe((isLogged) => {
      expect(isLogged).toBeTruthy();
      done();
    });
  });

  it('should reflect logged out status', (done) => {
    service.logOut();

    service.$isLogged().subscribe((isLogged) => {
      expect(isLogged).toBeFalsy();
      done();
    });
  });
});
