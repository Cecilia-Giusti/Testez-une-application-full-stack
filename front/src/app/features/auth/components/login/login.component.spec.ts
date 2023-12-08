import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { Component, NgZone } from '@angular/core';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  let mockAuthService: { login: any };
  let mockSessionService: { logIn: any };

  beforeEach(async () => {
    mockAuthService = { login: jest.fn() };
    mockSessionService = { logIn: jest.fn() };

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: SessionService, useValue: mockSessionService },
        FormBuilder,
      ],
      imports: [
        RouterTestingModule.withRoutes([{ path: 'sessions', redirectTo: '' }]),
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call login and handle success', () => {
    const response: SessionInformation = {
      token: 'fake-jwt-token',
      type: 'Bearer',
      id: 1,
      username: 'testuser',
      firstName: 'Test',
      lastName: 'User',
      admin: false,
    };

    const spyRouter = jest.spyOn(component['router'], 'navigate');

    mockAuthService.login.mockReturnValue(of(response));

    component.form.setValue({ email: 'test@example.com', password: '123456' });
    const ngZone = TestBed.inject(NgZone);
    ngZone.run(() => {
      component.submit();
    });

    expect(mockAuthService.login).toHaveBeenCalled();
    expect(mockSessionService.logIn).toHaveBeenCalledWith(response);
    expect(spyRouter).toHaveBeenCalledWith(['/sessions']);
  });

  it('should handle login error', () => {
    mockAuthService.login.mockReturnValue(
      throwError(() => new Error('Login failed'))
    );

    component.form.setValue({ email: 'test@example.com', password: '123456' });
    component.submit();

    expect(mockAuthService.login).toHaveBeenCalled();
    expect(component.onError).toBe(true);
  });
});
