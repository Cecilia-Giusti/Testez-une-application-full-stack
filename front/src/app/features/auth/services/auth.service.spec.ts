import { TestBed, inject } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { LoginRequest } from '../interfaces/loginRequest.interface';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';

describe('AuthService', () => {
  let authService: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });

    authService = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(authService).toBeTruthy();
  });

  it('should send a POST request to register', () => {
    const registerRequest: RegisterRequest = {
      email: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'password123',
    };

    authService.register(registerRequest).subscribe(() => {});

    const req = httpMock.expectOne(`${authService['pathService']}/register`);
    expect(req.request.method).toBe('POST');

    req.flush({});
  });

  it('should send a POST request to login', () => {
    const loginRequest: LoginRequest = {
      email: 'test@example.com',
      password: 'password123',
    };

    authService
      .login(loginRequest)
      .subscribe((sessionInfo: SessionInformation) => {
        expect(sessionInfo).toBeDefined();
        expect(sessionInfo.token).toBe('mockToken');
        expect(sessionInfo.type).toBe('mockType');
      });

    const req = httpMock.expectOne(`${authService['pathService']}/login`);
    expect(req.request.method).toBe('POST');

    const mockSessionInfo: SessionInformation = {
      token: 'mockToken',
      type: 'mockType',
      id: 123,
      username: 'mockUsername',
      firstName: 'mockFirstName',
      lastName: 'mockLastName',
      admin: true,
    };
    req.flush(mockSessionInfo);
  });
});
